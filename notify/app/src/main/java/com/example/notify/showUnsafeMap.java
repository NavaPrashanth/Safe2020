package com.example.notify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static android.graphics.BlendMode.HUE;
public class showUnsafeMap extends FragmentActivity implements OnMapReadyCallback{
    DatabaseReference mDatabase;
    GoogleMap map ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_unsafe_map);
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        GPSTracker gpsTracker =new GPSTracker(showUnsafeMap.this);
        Location mylatlon;
        mylatlon=gpsTracker.getLocation();
        map.addMarker(new MarkerOptions().position( new LatLng(mylatlon.getLatitude(), mylatlon.getLongitude())).title("myLocation")
        .icon(BitmapDescriptorFactory.defaultMarker(270)));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(showUnsafeMap.this, ""+ marker.getTitle(), Toast.LENGTH_LONG).show();
                Intent intent=new Intent(showUnsafeMap.this,MarkerActivity.class);
                intent.putExtra("title",marker.getTitle());
                    startActivity(intent);
                return true;
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference("UnsafeAreas");
        mDatabase.addValueEventListener(new ValueEventListener() {
            final String table = "table";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int len;
                double lat,lon,radii;
                LatLng loc;
                String title;
                lat=lon=radii=0;
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    //lat=Double.parseDouble(dataSnapshot.child("UnsafeAreas").child(dataSnapshot1.getKey()).child("lat").getValue().toString());
                    Toast.makeText(showUnsafeMap.this, "" + dataSnapshot1.getKey(), Toast.LENGTH_LONG).show();
                    String uid = dataSnapshot1.getKey();
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        if (dataSnapshot2.getKey().equals("lat"))
                            lat = Double.parseDouble(dataSnapshot2.getValue().toString());
                        else if (dataSnapshot2.getKey().equals("lon"))
                            lon = Double.parseDouble(dataSnapshot2.getValue().toString());
                        else if (dataSnapshot2.getKey().equals("radius"))
                            radii = Double.parseDouble(dataSnapshot2.getValue().toString());
                    }
                      title=dataSnapshot1.getKey();
                        loc = new LatLng(lat, lon);
                        map.addMarker(new MarkerOptions().position(loc).title(title));
                        map.addCircle(new CircleOptions().center(loc).radius(radii));
                         map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,12));
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }
}