package com.example.loginmapapplication;

import  androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText edtxt, edtxt1, edtxt2, edtxt3, edtxt4,edtxt5;
    private ServiceFeatureTable mServiceFeatureTable;
    private Button btn;
    ImageButton img;
    int SELECT_PICTURE = 200;
    String geom,geom1, geom2,geomType,geomType1, geomType2;
    ImageView IVPreviewImage;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_form);
        edtxt = findViewById(R.id.spinner_state);
        edtxt1 = findViewById(R.id.ed_category);
        edtxt2 = findViewById(R.id.ed_description);
        edtxt3 = findViewById(R.id.ed_city);
        edtxt4 = findViewById(R.id.ed_address);
        edtxt5 = findViewById(R.id.ed_pincode);
        btn = findViewById(R.id.point_button);

        mServiceFeatureTable = new ServiceFeatureTable(getString(R.string.service_layer_url).concat(getString(R.string.pointLayer_endpoint)));
        geom =getIntent().getStringExtra("geom");
        geomType =getIntent().getStringExtra("geomType");
        Geometry geometry = Geometry.fromJson(geom);

       /* geom1 = getIntent().getStringExtra("geom1");
        geomType1 =getIntent().getStringExtra("geomType1");
        Geometry geometry1 = Geometry.fromJson(geom1);*/


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFeature(geometry,mServiceFeatureTable);
                }
        });

       /* img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });*/

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

   /* void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }*/
    private void addFeature(Geometry geometry, final ServiceFeatureTable featureTable) {
        Map<String,Object> attributes = new HashMap<>();
        String categoryName = edtxt1.getText().toString();
        String Description = edtxt2.getText().toString();
        String District = edtxt3.getText().toString();
        String Address = edtxt4.getText().toString();
        String State = edtxt.getText().toString();
        String PinCode = edtxt5.getText().toString();

        attributes.put("CategoryName",categoryName);
        attributes.put("Description", Description);
        attributes.put("State", State);
        attributes.put("District", District);
        attributes.put("Address", Address);
        attributes.put("PIN", PinCode);

        featureTable.loadAsync();
        featureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                Feature feature = featureTable.createFeature(attributes, geometry);
                if (featureTable.canAdd()) {

                    featureTable.addFeatureAsync(feature).addDoneListener(() -> applyEdits(featureTable));
                } else {
                    runOnUiThread(() -> logToUser(true, getString(R.string.error_cannot_add_to_feature_table)));
                }
            }
        });
    }

    private void applyEdits(ServiceFeatureTable featureTable) {
        final ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
        editResult.addDoneListener(() -> {
            try {
                List<FeatureEditResult> editResults = editResult.get();
                if (editResults != null && !editResults.isEmpty()) {
                    if (!editResults.get(0).hasCompletedWithErrors()) {
                        runOnUiThread(() -> logToUser(false, getString(R.string.feature_added)));
                    } else {
                        throw editResults.get(0).getError();
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                runOnUiThread(() -> logToUser(true, getString(R.string.error_applying_edits, e.getCause().getMessage())));
            }
        });
    }

    private void logToUser(boolean isError, String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (isError) {
            Log.e(TAG, message);
        } else {
            Log.d(TAG, message);
        }
    }

   /* private void addFeature1(Geometry geometry1, final ServiceFeatureTable featureTable1) {
        Map<String,Object> attributes = new HashMap<>();
        String categoryName = edtxt1.getText().toString();
        String Description = edtxt2.getText().toString();
        String District = edtxt3.getText().toString();
        String Address = edtxt4.getText().toString();
        String State = edtxt.getText().toString();
        String PinCode = edtxt5.getText().toString();

        attributes.put("CategoryName",categoryName);
        attributes.put("Description", Description);
        attributes.put("State", State);
        attributes.put("District", District);
        attributes.put("Address", Address);
        attributes.put("PIN", PinCode);

        featureTable1.loadAsync();
        featureTable1.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                Feature feature = featureTable1.createFeature(attributes, geometry1);
                if (featureTable1.canAdd()) {

                    featureTable1.addFeatureAsync(feature).addDoneListener(() -> applyEdits(featureTable1));
                } else {
                    runOnUiThread(() -> logToUser(true, getString(R.string.error_cannot_add_to_feature_table)));
                }
            }
        });
    }*/
}

