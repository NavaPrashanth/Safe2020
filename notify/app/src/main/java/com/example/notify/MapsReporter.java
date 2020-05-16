package com.example.notify;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.random;

public class MapsReporter extends FragmentActivity implements OnMapReadyCallback {

    UnsafeArea ua;
    GoogleMap map ;
    GoogleMap mMap;
    String[] location = new String[6];
    Button submit;
    DatabaseReference mDatabase;
    public int att;
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        submit=(Button)findViewById(R.id.submit);
        setContentView(R.layout.activity_maps_reporter);
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent in = getIntent();
        location = in.getStringArrayExtra("communicator");
    }
    public void submitArea(View view){
        gps=new GPSTracker(MapsReporter.this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener( new ValueEventListener() {
            //public int att;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                att=Integer.parseInt(dataSnapshot.child("number").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        });
        mDatabase.child("UnsafeAreas").child(ua.name).setValue(ua);
    }
    public void change(){
        mDatabase.child("UnsafeArea").child("table520").child("number").setValue(random());
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        List<LatLng> latLngList = new ArrayList<LatLng>(1);

        if(Geocoder.isPresent()){
            try {

                Geocoder gc = new Geocoder(this);
                List<Address> addresses= gc.getFromLocationName(location[0], 5); // get the found Address Objects


                latLngList = new ArrayList<LatLng>(addresses.size());
                for(Address a : addresses){

                    latLngList.add(new LatLng(a.getLatitude(), a.getLongitude()));


                }
                if(latLngList.isEmpty()){
                    Log.i("My Checks 3", "No lat lng");
                }
                else{
                    Log.i("My Checks 2", Double.toString(latLngList.get(0).latitude));
                    Log.i("My Checks 2", Double.toString(latLngList.get(0).longitude));

                }
            } catch (IOException e) {

            }
        }

        LatLng loc = latLngList.get(0);
        map.addMarker(new MarkerOptions().position(loc).title("India").draggable(true));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,12));
        Circle circle = map.addCircle(new CircleOptions()
                .center(latLngList.get(0))
                .radius(10000)
                .strokeColor(Color.CYAN)
                .fillColor(Color.TRANSPARENT));

        ua = new UnsafeArea(latLngList.get(0).latitude,latLngList.get(0).longitude,location[0],location[1],location[5],Double.parseDouble(location[3]),location[2],location[4]);

    }
    public class UnsafeArea {

        public String name;
        public String reason;
        public double lat;
        public double lon;
        public String time;
        public String age;
        public String gender;
        public double radius;
        public UnsafeArea() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public  UnsafeArea(double lat,double lon,String name,String reason,String time,double radius,String age,String gender) {
            this.name=name;
            this.reason=reason;
            this.lat=lat;
            this.lon=lon;
            this.time=time;
            this.age=age;
            this.gender=gender;
            this.radius=radius;
        }

    }
}
