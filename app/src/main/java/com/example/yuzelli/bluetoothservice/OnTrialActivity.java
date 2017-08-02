package com.example.yuzelli.bluetoothservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class OnTrialActivity extends AppCompatActivity {
    TextView textView3;
    TextView textView4;
    String time = "2017-08-05 12:00:00";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_trial);
        textView3.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(OtherUtils.date2TimeStamp(time)> System.currentTimeMillis()/1000){
                    startActivity(new Intent(OnTrialActivity.this,MainActivity.class));
                    finish();
                }else {
                    textView4.setText("已过期");
                    Toast.makeText(OnTrialActivity.this,"已过期",Toast.LENGTH_SHORT).show();

                }
            }
        },3*1000);
    }
}
