package com.example.luces;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class monitorizcion extends AppCompatActivity {
    private static  final long START_TIME_IN_MILLIS= 60000;
    private TextView vista;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimerLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitorizcion);
        vista = (TextView) findViewById(R.id.vista);
        starttimer();
    }
    private  void  starttimer(){
        mCountDownTimer = new CountDownTimer(mTimerLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerLeftInMillis = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {
                mTimerRunning=false;
                finish();

            }
        }.start();
        mTimerRunning =true;

    }
    private void updateCountDownText(){
        int minutes =(int)(mTimerLeftInMillis /1000)/60;
        int seconds =(int)(mTimerLeftInMillis /1000)%60;
        String timeLeftFormatted= String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        vista.setText(timeLeftFormatted);
    }
}
