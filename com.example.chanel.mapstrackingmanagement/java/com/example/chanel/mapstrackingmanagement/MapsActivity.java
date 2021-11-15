package com.example.chanel.mapstrackingmanagement;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sqlitelib.DataBaseHelper;
import com.sqlitelib.SQLite;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    ArrayAdapter AdapterCoordinates;
    public int cptr;
    public DataBaseHelper dbhelper = new DataBaseHelper(this, "MapCoordinates", 2);
    public Double lat;
    List<String> listCoordinates;
    public Double lng;
    public String location;
    ListView lstviewCoordinateList;
    private GoogleMap mMap;
    public Integer[] valueId;
    public Double[] valueLatitude;
    public String[] valueLocation;
    public Double[] valueLongitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.lstviewCoordinateList = (ListView) findViewById(R.id.ListViewPoint);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        reloadPointList();
        lstviewCoordinateListItemSelectedChangedListener();
        mapFragment.getMapAsync(this);
    }

    private void lstviewCoordinateListItemSelectedChangedListener() {
        this.lstviewCoordinateList.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                MapsActivity.this.cptr = position;
                MapsActivity.this.msrkNewPoints();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void msrkNewPoints() {
        this.lat = this.valueLatitude[this.cptr];
        this.lng = this.valueLongitude[this.cptr];
        this.location = this.valueLocation[this.cptr];
        this.mMap.clear();
        LatLng sydney = new LatLng(this.lat.doubleValue(), this.lng.doubleValue());
        this.mMap.addMarker(new MarkerOptions().position(sydney).title(this.location));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void reloadPointList() {
        SQLiteDatabase dbCoordinates = this.dbhelper.getWritableDatabase();
        Cursor cCordinates = dbCoordinates.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='tblcoordinates'", null);
        cCordinates.moveToNext();
        if (cCordinates.getCount() == 0) {
            SQLite.FITCreateTable("MapCoordinates", this, "tblcoordinates", "id INTEGER PRIMARY KEY AUTOINCREMENT, latitude DOUBLE,longitude DOUBLE,location VARCHAR(100)");
            dbCoordinates.execSQL("INSERT INTO tblcoordinates (latitude,longitude, location) VALUES (-31.950527,115.860457,'Perth, Australia')");
            dbCoordinates.execSQL("INSERT INTO tblcoordinates (latitude,longitude, location) VALUES (-26.204103,28.047305,'Johannesburg, South Africa')");
            dbCoordinates.execSQL("INSERT INTO tblcoordinates (latitude,longitude, location) VALUES (27.717245,85.323960,'Katmandu, Nepal')");
            return;
        }
        cCordinates = dbCoordinates.rawQuery("SELECT id, latitude, longitude,location FROM tblcoordinates order by id desc", null);
        String[] valueCoordinates = new String[cCordinates.getCount()];
        Integer[] ValueCurrentID = new Integer[cCordinates.getCount()];
        Double[] valueCurrentLatitude = new Double[cCordinates.getCount()];
        Double[] valueCurrentLongitude = new Double[cCordinates.getCount()];
        String[] valueCurrentLocation = new String[cCordinates.getCount()];
        int ctrl = 0;
        while (cCordinates.moveToNext()) {
            valueCoordinates[ctrl] = ((BuildConfig.FLAVOR + "Latitude : " + cCordinates.getDouble(cCordinates.getColumnIndex("latitude"))) + System.lineSeparator() + "Longitude : " + cCordinates.getDouble(cCordinates.getColumnIndex("longitude"))) + System.lineSeparator() + "Address : " + cCordinates.getString(cCordinates.getColumnIndex("location"));
            ValueCurrentID[ctrl] = Integer.valueOf(cCordinates.getInt(cCordinates.getColumnIndex("id")));
            ctrl++;
        }
        this.valueId = (Integer[]) Arrays.copyOf(ValueCurrentID, cCordinates.getCount());
        this.valueLatitude = (Double[]) Arrays.copyOf(valueCurrentLatitude, cCordinates.getCount());
        this.valueLongitude = (Double[]) Arrays.copyOf(valueCurrentLongitude, cCordinates.getCount());
        this.valueLocation = (String[]) Arrays.copyOf(valueCurrentLocation, cCordinates.getCount());
        this.listCoordinates = new ArrayList();
        for (Object add : valueCoordinates) {
            this.listCoordinates.add(add);
        }
        this.AdapterCoordinates = new ArrayAdapter(this, 17367043, valueCoordinates);
        try {
            this.lstviewCoordinateList.setAdapter(this.AdapterCoordinates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        LatLng sydney = new LatLng(-34.0d, 151.0d);
        this.mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        this.mMap.setOnMapClickListener(new OnMapClickListener() {
            public void onMapClick(LatLng point) {
                MapsActivity.this.mMap.clear();
                LatLng newmarker = new LatLng(point.latitude, point.longitude);
                MapsActivity.this.mMap.addMarker(new MarkerOptions().position(newmarker).title("New Point").snippet("4 E. 28TH Street From $15 /per night").rotation(-15.0f).icon(BitmapDescriptorFactory.defaultMarker(30.0f)));
                MapsActivity.this.lat = Double.valueOf(point.latitude);
                MapsActivity.this.lng = Double.valueOf(point.longitude);
                MapsActivity.this.addCoordinates();
                MapsActivity.this.mMap.moveCamera(CameraUpdateFactory.newLatLng(newmarker));
                MapsActivity.this.mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f), 1000, null);
            }
        });
    }

    private void addCoordinates() {
        try {
            this.dbhelper.getWritableDatabase().execSQL("INSERT INTO tblcoordinates (latitude,longitude, location) VALUES (" + this.lat + "," + this.lng + ",'')");
        } catch (SQLException e) {
            Toast.makeText(this, e.getMessage().toString(), 1).show();
        }
        reloadPointList();
    }
}
