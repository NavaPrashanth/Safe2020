package com.example.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class backgroundNotifier extends Service {
    public int x=0;
    GPSTracker gps;
    public double latitude,longitude;

     DatabaseReference mDatabase;
    public backgroundNotifier() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //for(int i=0;i<10000;i++)
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            //if(x%10000==0)addNotify();

            gps = new GPSTracker(backgroundNotifier.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                // \n is for new line

            } else {
                // gps.showSettingsAlert();
            }
            mDatabase = FirebaseDatabase.getInstance().getReference("UnsafeAreas");
            mDatabase.addValueEventListener(new ValueEventListener() {
                //public int att;
                Location l1 = gps.getLocationExe();
                Location l2 = gps.getLocation();

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    double lat, lon, radii;
                    LatLng loc;
                    String title;
                    lat = lon = radii = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        //lat=Double.parseDouble(dataSnapshot.child("UnsafeAreas").child(dataSnapshot1.getKey()).child("lat").getValue().toString());
                       // Toast.makeText(backgroundNotifier.this, "" + dataSnapshot1.getKey(), Toast.LENGTH_LONG).show();
                        try {
                            ;//Thread.sleep(1000);
                        } catch (Exception e) {

                        }
                        String uid = dataSnapshot1.getKey();
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            if (dataSnapshot2.getKey().equals("lat"))
                                lat = Double.parseDouble(dataSnapshot2.getValue().toString());
                            else if (dataSnapshot2.getKey().equals("lon"))
                                lon = Double.parseDouble(dataSnapshot2.getValue().toString());
                            else if (dataSnapshot2.getKey().equals("radius"))
                                radii = Double.parseDouble(dataSnapshot2.getValue().toString());
                        }
                        //lat=Double.parseDouble(dataSnapshot1.child(uid).child("lat").getValue().toString());
                        //Toast.makeText(showUnsafeMap.this,dataSnapshot1.child("Golconda").child("lat").getValue().toString()+" "+dataSnapshot.getKey(), Toast.LENGTH_LONG).show();
                        //lon=Double.parseDouble(dataSnapshot1.child(dataSnapshot1.getKey()).child("lon").getValue().toString());
                        l1.setLongitude(lon);
                        l1.setLatitude(lat);
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {

                        }
                        //Toast.makeText(backgroundNotifier.this, dataSnapshot1.getKey() + "" + lat + " " + lon + " ", Toast.LENGTH_LONG).show();
                        if (l1.distanceTo(l2) <= radii) {
                            addNotify();
                            break;
                        }
                        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,12));

                    }
                    //System.out.println(" " + table + " " + l1.getLatitude() + " " + l1.getLongitude() + " " + l2.getLatitude() + " " + l2.getLongitude() + " " + l2.distanceTo(l1));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    // ...
                }
            });
        }
        catch(Exception e){

        }
            //onStartCommand(intent,flags,startId);
            startService(new Intent(this, backgroundNotifier.class));
            onDestroy();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void addNotify(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel=
                    new NotificationChannel("my","my", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this,"my")
                        .setSmallIcon(R.drawable.abc)
                        .setContentTitle("back Alert")
                        .setContentText("You are entering into an unsafe zone. Click to see data");

        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
