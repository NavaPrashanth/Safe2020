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


public class MarkerActivity extends AppCompatActivity {
    public GPSTracker gps;
    public double latitude,longitude;
    DatabaseReference mDatabase;
    public TextView name,age,gender,time,reason,radius;
    String table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        gps = new GPSTracker(MarkerActivity.this);
        name=(TextView)findViewById(R.id.name);
        age=(TextView)findViewById(R.id.age);
        gender=(TextView)findViewById(R.id.gender);
        time=(TextView)findViewById(R.id.timming);
        reason=(TextView)findViewById(R.id.reason);
        radius=(TextView)findViewById(R.id.radius);
        // check if GPS enabled
        Intent intent=getIntent();
        table=intent.getStringExtra("title");
        showData();
    }
    public void showData(){
        //gps = new GPSTracker(MarkerActivity.this);
        // check if GPS enabled
        //if(gps.canGetLocation()){

        //name = getIntent().getStringExtra("name").toString();
            longitude = gps.getLongitude();
            // \n is for new line
            Toast.makeText(getApplicationContext(), "\nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        //}else{
        //    gps.showSettingsAlert();
        //}
        //mDatabase.child("alert").setValue("alerted");
        mDatabase.addValueEventListener( new ValueEventListener() {
            //public int att;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


            age.setText(dataSnapshot.child("UnsafeAreas").child(table).child("age").getValue().toString());
                            time.setText(dataSnapshot.child("UnsafeAreas").child(table).child("time").getValue().toString());
                            gender.setText(dataSnapshot.child("UnsafeAreas").child(table).child("gender").getValue().toString());
                            radius.setText(dataSnapshot.child("UnsafeAreas").child(table).child("radius").getValue().toString());
                            name.setText(dataSnapshot.child("UnsafeAreas").child(table).child("name").getValue().toString());
                            reason.setText(dataSnapshot.child("UnsafeAreas").child(table).child("reason").getValue().toString());
                            System.out.println("added");




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

}

