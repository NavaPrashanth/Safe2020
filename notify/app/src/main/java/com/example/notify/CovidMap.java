package com.example.notify;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CovidMap extends AppCompatActivity implements OnMapReadyCallback {
    DatabaseReference mDatabase;
    GoogleMap map ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_map);
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(CovidMap.this, ""+ marker.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference("CovidZones");
        mDatabase.addValueEventListener(new ValueEventListener() {
            final String table = "table";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int len;
                double lat,lon,radii;


                String title;
                lat=lon=radii=0;
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    //lat=Double.parseDouble(dataSnapshot.child("UnsafeAreas").child(dataSnapshot1.getKey()).child("lat").getValue().toString());
                    Toast.makeText(CovidMap.this, "" + dataSnapshot1.getKey(), Toast.LENGTH_LONG).show();
                    String uid = dataSnapshot1.getKey();
                    LatLng loc = new LatLng(0, 0);
                    int col = 0;
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        try {
                            if (dataSnapshot2.getKey().equals("name")) {
                                List<LatLng> latLngList = new ArrayList<LatLng>(1);

                                if (Geocoder.isPresent()) {
                                    try {

                                        Geocoder gc = new Geocoder(CovidMap.this);
                                        List<Address> addresses = gc.getFromLocationName(dataSnapshot2.getValue().toString(), 5); // get the found Address Object
                                        latLngList = new ArrayList<LatLng>(addresses.size());
                                        for (Address a : addresses) {

                                            latLngList.add(new LatLng(a.getLatitude(), a.getLongitude()));


                                        }
                                        if (latLngList.isEmpty()) {
                                            Log.i("My Checks 3", "No lat lng "+dataSnapshot2.getValue().toString());
                                        } else {
                                            Log.i("My Checks 2", Double.toString(latLngList.get(0).latitude));
                                            Log.i("My Checks 2", Double.toString(latLngList.get(0).longitude));

                                        }
                                    } catch (IOException e) {

                                    }
                                }

                                loc = latLngList.get(0);
                                title = dataSnapshot2.getValue().toString();
                            } else if (dataSnapshot2.getKey().equals("zone")) {
                                String s = dataSnapshot2.getValue().toString();
                                if (s.equals("red")) {
                                    col = 0;
                                } else if (s.equals("orange")) {
                                    col = 35;
                                } else {
                                    col = 85;
                                }

                            }
                            title = dataSnapshot1.getKey();

                            ;   map.addMarker(new MarkerOptions().position(loc).title(title).icon(BitmapDescriptorFactory.defaultMarker(col)));
                        } catch (Exception e1) {
                            ;
                        }
                    }
                }
                GPSTracker gpsTracker =new GPSTracker(CovidMap.this);
                Location mylatlon;
                mylatlon=gpsTracker.getLocation();
                map.addMarker(new MarkerOptions().position( new LatLng(mylatlon.getLatitude(), mylatlon.getLongitude())).title("myLocation")
                        .icon(BitmapDescriptorFactory.defaultMarker(270)));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mylatlon.getLatitude(), mylatlon.getLongitude()),12));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

}
