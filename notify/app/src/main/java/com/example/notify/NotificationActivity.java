package com.example.notify;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class NotificationActivity extends AppCompatActivity {
    public GPSTracker gps;
    public double latitude,longitude;
    DatabaseReference mDatabase;
    public TextView name,age,gender,time,reason,radius;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        gps = new GPSTracker(NotificationActivity.this);
        name=(TextView)findViewById(R.id.name);
        age=(TextView)findViewById(R.id.age);
        gender=(TextView)findViewById(R.id.gender);
        time=(TextView)findViewById(R.id.timming);
        reason=(TextView)findViewById(R.id.reason);
        radius=(TextView)findViewById(R.id.radius);
        // check if GPS enabled

        showData();
    }
    public void showData(){
        gps = new GPSTracker(NotificationActivity.this);
        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        }else{
            gps.showSettingsAlert();
        }
        //mDatabase.child("alert").setValue("alerted");
       mDatabase.addValueEventListener( new ValueEventListener() {
            //public int att;
            Location l1 = gps.getLocationExe();
            Location l2 = gps.getLocation();
            //l1.

            double x = (latitude - 17) * 1000000 - 210000;
            double y = (longitude - 78) * 1000000 - 150000;
            //System.out.println(x+" "+y);
            int a = (int) x / 200000;
            int b = (int) y / 200000;
            //System.out.println(a+" "+b);
            final String table = "table" + ((a) * 26 + (b) + 1) + "";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ;
                int len;
                len = Integer.parseInt(dataSnapshot.child("UnsafeAreas").child(table).child("id").getValue().toString());
                System.out.println(len + " cvb123456789");
                for (int i = 0; i < len; i++) {
                    try {
                        l2.setLongitude(Double.parseDouble(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("lon").getValue().toString()));
                        l2.setLatitude(Double.parseDouble(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("lat").getValue().toString()));
                        if (Integer.parseInt(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("radius").getValue().toString()) >= (int) l1.distanceTo(l2)) {
                            age.setText(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("age").getValue().toString());
                            time.setText(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("time").getValue().toString());
                            gender.setText(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("gender").getValue().toString());
                            radius.setText(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("radius").getValue().toString());
                            name.setText(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("name").getValue().toString());
                            reason.setText(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("reason").getValue().toString());
                            System.out.println("added");
                        } else ;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }


}

