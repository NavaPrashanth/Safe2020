package com.example.notify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import android.Manifest;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.UserInfo.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public GPSTracker gps;
    Button submit;
    Button report;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    public double latitude,longitude;
    private static final int REQUEST_CODE_PERMISSION = 2;
    DatabaseReference mDatabase;
    Button volunteerSubmit;
    FirebaseAuth auth;
    String phoneNo,message;
    int counter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
// See the UserRecord reference doc for the contents of userRecord.
        counter=0;
        volunteerSubmit=(Button)findViewById(R.id.volunteer);
        //startService(new Intent(this,backgroundNotifier.class));
        report=(Button)findViewById(R.id.report);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        submit = (Button) findViewById(R.id.submit);
        mDatabase = FirebaseDatabase.getInstance().getReference();
       // mDatabase.child("number").setValue(0);
        //for(int i=1;i<=520;i++)
        //  mDatabase.child("UnsafeAreas").child("table"+i).setValue(user);

        //startActivity(new Intent(MainActivity.this,MainPage.class));
        gps = new GPSTracker(MainActivity.this);
        /*report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ReportUnsafeArea.class));
            }
        });*/
    }
    public void onClick4(View view){
        startActivity(new Intent(MainActivity.this,MapsActivity.class));

           }
    public boolean dispatchKeyEvent(KeyEvent key){
        int action,keycode;
        action =key.getAction();
        keycode=key.getKeyCode();
        switch (keycode){
            case KeyEvent.KEYCODE_VOLUME_DOWN :{
                Toast.makeText(this, counter+" ", Toast.LENGTH_LONG).show();

                if(counter==5){

                    //Toast.makeText(this, counter+" ", Toast.LENGTH_LONG).show();
                    //takeVedio();
                    //+"&query_place_id=ChIJKxjxuaNqkFQR3CK6O1HNNqY";
                    counter=6;
                    sendAlerts(1);

                }else counter++;
            }
        }
        return super.dispatchKeyEvent(key);
    }
    public void sendAlerts(int type){
        if(type==1) {
            gps = new GPSTracker(MainActivity.this);

            String address = "";
            String city = "";
            String state = "";
            String country = "";
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                Log.i("My Checks", Double.toString(latitude));
                Log.i("My Checks", Double.toString(longitude));
                Geocoder geocoder;
                List<Address> addresses = new ArrayList<Address>();
                geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();


                // \n is for new line
                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                        + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
            // phoneNo = "7075134557";
            message = "Iam feeling unsafe" + " " + " \n My address is " + "\n City" + city + "\n state" + state + "\nLink to Location: https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        /*String message2 = "Iam feeling unsafe"+"my Location is - \nLat: "
                + latitude + "\nLong: " + longitude+"\nLink to Location: https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude +"\nVideo Link";//+imageUrl);
*/
        }else message="Thank you.\n Now I am feeling safe.";
        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                auth=FirebaseAuth.getInstance();
                String uid = FirebaseAuth.getInstance().getUid();
                phoneNo=mDatabase.child("Users").child(uid).child("ph1").getKey();
                //Toast.makeText(MarkerActivity.this,phoneNo,)
                //Log.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }
    public void onClick3(View view){
        startActivity(new Intent(MainActivity.this,CovidMap.class));
    }
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
    public void onClick1(View view) {
        startActivity(new Intent(MainActivity.this,ReportUnsafeArea.class));
    }
    public void onClick2(View view) {
        startActivity(new Intent(MainActivity.this,showUnsafeMap.class));
    }
    public void onClick(View view) {
        ;
        //mDatabase.child("users").child("n").setValue(user);
    }



    /*public  void notifyUser(){
        gps = new GPSTracker(MainActivity.this);
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
        mDatabase.addValueEventListener( new ValueEventListener() {
            //public int att;
            Location  l1=gps.getLocationExe();
            Location  l2=gps.getLocation();
            //l1.

            double x=(latitude-17)*1000000-210000;
            double y=(longitude-78)*1000000-150000;
                //System.out.println(x+" "+y);
            int a=(int)x/200000;
            int b=(int)y/200000;
                //System.out.println(a+" "+b);
            final String table="table"+((a)*26+(b)+1)+"";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int len;
                len=Integer.parseInt(dataSnapshot.child("UnsafeAreas").child(table).child("id").getValue().toString());
                System.out.println(len+" cvb123456789");
                for(int i=0;i<len;i++){
                    try {
                        l2.setLongitude(Double.parseDouble(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("lon").getValue().toString()));
                        l2.setLatitude(Double.parseDouble(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("lat").getValue().toString()));
                        System.out.println(i + " " + table + " " + l1.getLatitude() + " " + l1.getLongitude() + " " + l2.getLatitude() + " " + l2.getLongitude() + " " + l2.distanceTo(l1));
                    if(Integer.parseInt(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("radius").getValue().toString())>=(int)l1.distanceTo(l2)){
                        addNotification();
                        System.out.println("added");
                        }else ;System.out.println(" hjhkk "+Double.parseDouble(dataSnapshot.child("UnsafeAreas").child(table).child("un" + i).child("radius").getValue().toString())+" "+(int)l1.distanceTo(l2));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                l2.setLongitude(78.447008);
                l2.setLatitude(17.467058);
                System.out.println(  " " + table + " " + l1.getLatitude() + " " + l1.getLongitude() + " " + l2.getLatitude() + " " + l2.getLongitude() + " " + l2.distanceTo(l1));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        });
    }*/

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
