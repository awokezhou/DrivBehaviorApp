package com.example.awoke.drivbehaviorapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import service.SensorPollService;

public class BasePattern extends Activity {

    TextView mTextView1;
    TextView mTextView2;
    TextView mTextView3;
    TextView mTextView4;
    TextView mTextView5;
    TextView mTextView6;
    TextView mTextView7;
    TextView mTextView8;
    TextView mTextView9;
    TextView mTextView10;
    TextView mTextView11;
    TextView mTextView12;

    public SensorPollBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basepattern);

        initView();

        initReceiver();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView() {
        mTextView1 = findViewById(R.id.text_1);
        mTextView2 = findViewById(R.id.text_2);
        mTextView3 = findViewById(R.id.text_3);
        mTextView4 = findViewById(R.id.text_4);
        mTextView5 = findViewById(R.id.text_5);
        mTextView6 = findViewById(R.id.text_6);
        mTextView7 = findViewById(R.id.text_7);
        mTextView8 = findViewById(R.id.text_8);
        mTextView9 = findViewById(R.id.text_9);
        mTextView10 = findViewById(R.id.text_10);
        mTextView11 = findViewById(R.id.text_11);
        mTextView12 = findViewById(R.id.text_12);
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_UPDATEUI);
        mBroadcastReceiver = new SensorPollBroadcastReceiver();
        registerReceiver(mBroadcastReceiver, filter);
    }

    private class SensorPollBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTextView1.setText(String.valueOf(intent.getExtras().getString("data.vecAcc")));
            mTextView2.setText(String.valueOf(intent.getExtras().getString("data.vecGravity")));
            mTextView3.setText(String.valueOf(intent.getExtras().getString("data.vecDelGrav")));
            mTextView4.setText(String.valueOf(intent.getExtras().getString("data.vecAccAngle")));
            mTextView5.setText(String.valueOf(intent.getExtras().getString("data.vecgyrAngle")));
            mTextView6.setText(String.valueOf(intent.getExtras().getString("data.vecCmpAngle")));
            mTextView7.setText(String.valueOf(intent.getExtras().getDouble("data.Mod")));
            mTextView8.setText(String.valueOf(intent.getExtras().getString("stat.crash")));
            mTextView9.setText(String.valueOf(intent.getExtras().getString("stat.rapidAcc")));
            mTextView10.setText(String.valueOf(intent.getExtras().getString("stat.rapidDec")));
            mTextView11.setText(String.valueOf(intent.getExtras().getString("stat.brake")));
            mTextView12.setText(String.valueOf(intent.getExtras().getString("stat.suddenTurn")));
        }
    }
}
