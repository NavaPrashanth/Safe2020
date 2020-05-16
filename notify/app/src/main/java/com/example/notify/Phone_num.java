package com.example.notify;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Phone_num extends AppCompatActivity {
    DatabaseReference mDatabase;
    Button btnFinish;
    EditText etPh1,etPh2,etPh3;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_num);
        btnFinish = findViewById(R.id.finish);
        etPh1=findViewById(R.id.phn1);
        etPh2=findViewById(R.id.phn2);
        etPh3=findViewById(R.id.phn3);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ph1 = etPh1.getText().toString();
                String ph2 = etPh2.getText().toString();
                String ph3 = etPh3.getText().toString();
                if(TextUtils.isEmpty(ph1)){
                    etPh1.setError("Phone number is Required.");
                    return;
                }
                if(TextUtils.isEmpty(ph2)){
                    etPh2.setError("Phone number is Required.");
                    return;
                }
                if(TextUtils.isEmpty(ph3)){
                    etPh3.setError("Phone number is Required.");
                    return;
                }
                phones pho =new phones(ph1,ph2,ph3);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                auth=FirebaseAuth.getInstance();
                String uid = FirebaseAuth.getInstance().getUid();
                mDatabase.child("Users").child(uid).setValue(pho);
                Intent intent=getIntent();
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("phoneNo",intent.getStringExtra("phoneNo")));
            }
        });
    }
    public class phones{
        public String ph1;
        public String ph2;
        public String ph3;
        phones(String ph1,String ph2,String ph3){
            this.ph1=ph1;
            this.ph2=ph2;
            this.ph3=ph3;
        }
    }
}