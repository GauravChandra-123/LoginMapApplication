package com.example.loginmapapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MapEditor extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {
    private MapView mapView;
    private ArcGISMap arcGISMap;
    ImageButton btn1, zoomin, zoomout, home;
    ConstraintLayout constraintLayout;
    private SimpleLineSymbol mLineSymbol;
    private SimpleMarkerSymbol mPointSymbol;
    private SimpleFillSymbol mFillSymbol;
    private SketchEditor mSketchEditor;
    private GraphicsOverlay mGraphicsOverlay;
    private LocationDisplay mLocationDisplay;
    private final String[] reqPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};
    private final int requestCode = 2;
    private Spinner mSpinner;
    String geomType, geomType1, geomType2;
    private EditText txt;
    private final String TAG = MainActivity.class.getSimpleName();
    private ServiceFeatureTable mServiceFeatureTable;

    private ImageButton mSave, sketch, delete, mRedo, mUndo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_editor);
        ArcGISRuntimeEnvironment.setApiKey("AAPKa0888be4f47a4dc1b33af7f76df19d8aGD_78sZInxD2O7w8-a0ZbrCafT6tceH91Oe7oPDJKz9IiEMoFniVv833w847kBp5");
        mPointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFFFF0000, 20);
        mLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF8800, 4);
        mFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0x40FFA9A9, mLineSymbol);

        mapView = findViewById(R.id.mapView);
        arcGISMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 28.608428727225885, 77.4460168147695, 9);
        mapView.setMap(arcGISMap);
        mLocationDisplay = mapView.getLocationDisplay();

        zoomin = findViewById(R.id.zoomin);
        zoomout = findViewById(R.id.zoomout);
        home = findViewById(R.id.homie);
        btn1 = findViewById(R.id.basemapgallery);
        constraintLayout = findViewById(R.id.cons);
        delete = findViewById(R.id.delete);

        mGraphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(mGraphicsOverlay);
        mServiceFeatureTable = new ServiceFeatureTable(getString(R.string.service_layer_url));
        FeatureLayer featureLayer = new FeatureLayer(mServiceFeatureTable);

        mSketchEditor = new SketchEditor();
        mapView.setSketchEditor(mSketchEditor);
        mRedo = findViewById(R.id.redo);
        mUndo = findViewById(R.id.undo);
        mSave = findViewById(R.id.save);
        sketch = findViewById(R.id.sketcheditor);
        mSpinner = findViewById(R.id.spinner);
        mLocationDisplay = mapView.getLocationDisplay();
        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {
            if (dataSourceStatusChangedEvent.isStarted())
                return;
            if (dataSourceStatusChangedEvent.getError() == null)
                return;
            boolean permissionCheck1 = ContextCompat.checkSelfPermission(this, reqPermissions[0]) ==
                    PackageManager.PERMISSION_GRANTED;
            boolean permissionCheck2 = ContextCompat.checkSelfPermission(this, reqPermissions[1]) ==
                    PackageManager.PERMISSION_GRANTED;

            if (!(permissionCheck1 && permissionCheck2)) {
                ActivityCompat.requestPermissions(this, reqPermissions, requestCode);
            } else {
                String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                        .getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                mSpinner.setSelection(0, true);
            }
        });

        ArrayList<ItemData> list = new ArrayList<>();
        list.add(new ItemData("Stop", R.drawable.markeroff24));
        list.add(new ItemData("On", R.drawable.mylocation24));
        list.add(new ItemData("Re-Center", R.drawable.on24));
        list.add(new ItemData("Navigation", R.drawable.nearme24));
        list.add(new ItemData("Compass", R.drawable.compass24));

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, list);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        if (mLocationDisplay.isStarted())
                            mLocationDisplay.stop();
                        break;
                    case 1:
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                    case 2:
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                    case 3:
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                    case 4:
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSketchEditor.clearGeometry();
            }
        });


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSketchEditor.isSketchValid()) {
                    Geometry geometry = GeometryEngine.project(mSketchEditor.getGeometry(), SpatialReferences.getWebMercator());
                    Intent intent = new Intent(MapEditor.this, FormActivity.class);
                    intent.putExtra("geom",geometry.toJson());
                    intent.putExtra("geomType",geomType);


                    startActivity(intent);
                }

                else {
                    reportNotValid();
                }
            }
        });

        sketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plp(view);
            }
        });

        mRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSketchEditor.canRedo()) {
                    mSketchEditor.redo();
                }
            }
        });


        mUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSketchEditor.canUndo()) {
                    mSketchEditor.undo();
                }
            }
        });

        zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.setViewpointScaleAsync(mapView.getMapScale() / 2);
            }
        });

        zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.setViewpointScaleAsync(mapView.getMapScale() * 2);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.setViewpoint(new Viewpoint(28.608428727225885, 77.4460168147695, 70000));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();
        } else {
            Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
            mSpinner.setSelection(0, true);
        }
    }

    public void showbasemap(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.base_map);
        popupMenu.show();
    }

    public void plp(View view) {
        PopupMenu popmenu = new PopupMenu(this, view);
        popmenu.setOnMenuItemClickListener(this);
        popmenu.inflate(R.menu.point_line_polygon);
        popmenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.usr_setting, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login12:
                Intent intent = new Intent(MapEditor.this, MainActivity.class);
                startActivity(intent);

            case R.id.logout12:
                Intent intnt = new Intent(MapEditor.this, MainActivity.class);
                startActivity(intnt);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch ((menuItem.getItemId())) {
            case R.id.basemap1:
                arcGISMap.setBasemap(new Basemap(BasemapStyle.ARCGIS_STREETS));
                return true;

            case R.id.basemap2:
                arcGISMap.setBasemap(new Basemap(BasemapStyle.ARCGIS_NAVIGATION));
                return true;

            case R.id.basemap3:
                arcGISMap.setBasemap(new Basemap(BasemapStyle.ARCGIS_TOPOGRAPHIC));
                return true;

            case R.id.basemap4:
                arcGISMap.setBasemap(new Basemap(BasemapStyle.ARCGIS_TERRAIN));
                return true;

            case R.id.basemap5:
                arcGISMap.setBasemap(new Basemap(BasemapStyle.ARCGIS_LIGHT_GRAY));
                return true;

            case R.id.basemap6:
                arcGISMap.setBasemap(new Basemap(BasemapStyle.OSM_LIGHT_GRAY));
                return true;

            case R.id.point:
                geomType = "POINT";
                mSketchEditor.start(SketchCreationMode.POINT);
                mSave.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);
                mRedo.setVisibility(View.VISIBLE);
                mUndo.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                return true;

            case R.id.line:
                geomType1 = "LINE";
                mSketchEditor.start(SketchCreationMode.POLYLINE);
                mSave.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);
                mRedo.setVisibility(View.VISIBLE);
                mUndo.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                return true;

            case R.id.polygon:
                geomType2 = "POLYGON";
                mSketchEditor.start(SketchCreationMode.POLYGON);
                mSave.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);
                mRedo.setVisibility(View.VISIBLE);
                mUndo.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                return true;

            default:
                return false;
        }
    }

    private void stop() {
        if (!mSketchEditor.isSketchValid()) {
            mSketchEditor.stop();
        }

        Geometry sketchGeometry = mSketchEditor.getGeometry();
        mSketchEditor.stop();

        if (sketchGeometry != null) {
            Graphic graphic = new Graphic(sketchGeometry);
            if (graphic.getGeometry().getGeometryType() == GeometryType.POLYGON) {
                graphic.setSymbol(mFillSymbol);
            } else if (graphic.getGeometry().getGeometryType() == GeometryType.POLYLINE) {
                graphic.setSymbol(mLineSymbol);
            } else if (graphic.getGeometry().getGeometryType() == GeometryType.POINT) {
                graphic.setSymbol(mPointSymbol);
            }
            mGraphicsOverlay.getGraphics().add(graphic);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String geometry = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(getApplicationContext(), geometry, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void reportNotValid() {
        String validIf;
        if (mSketchEditor.getSketchCreationMode() == SketchCreationMode.POINT) {
            validIf = "Point only valid if it contains an x & y coordinate.";
        } else if (mSketchEditor.getSketchCreationMode() == SketchCreationMode.POLYLINE) {
            validIf = "Polyline only valid if it contains at least one part of 2 or more vertices.";
        } else if (mSketchEditor.getSketchCreationMode() == SketchCreationMode.POLYGON) {
            validIf = "Polygon only valid if it contains at least one part of 3 or more vertices which form a closed ring.";
        } else {
            validIf = "No sketch creation mode selected.";
        }
        String report = "Sketch geometry invalid:\n" + validIf;
        Snackbar reportSnackbar = Snackbar.make(findViewById(R.id.toolbarInclude), report, Snackbar.LENGTH_INDEFINITE);
        reportSnackbar.setAction("Dismiss", view -> reportSnackbar.dismiss());
        TextView snackbarTextView = reportSnackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarTextView.setSingleLine(false);
        reportSnackbar.show();
        Log.e(TAG, report);
    }

}