package com.example.awoke.drivbehaviorapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.TextView;

import com.example.awoke.drivbehaviorapp.CoordinateAxis.CoordinateAxisDraw;
import com.example.awoke.drivbehaviorapp.CoordinateAxis.CoordinateAxisRenderer;

import java.util.Vector;

import service.SensorPollService;

public class View3dPattern extends Activity {

    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView5;

    private GLSurfaceView mView;
    private CoordinateAxisRenderer mRenderer;
    public View3dPattern.SensorPollBroadcastReceiver mBroadcastReceiver;

    private Vector vecVehicle, vecDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view3dpattern);

        initView();

        initReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView() {
        mRenderer = new CoordinateAxisRenderer();
        mView = findViewById(R.id.view_opengl);
        mView.setRenderer(mRenderer);

        mTextView1 = findViewById(R.id.view3d_text_1);
        mTextView2 = findViewById(R.id.view3d_text_2);
        mTextView3 = findViewById(R.id.view3d_text_3);
        mTextView4 = findViewById(R.id.view3d_text_4);
        mTextView5 = findViewById(R.id.view3d_text_5);
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_UPDATEUI);
        mBroadcastReceiver = new View3dPattern.SensorPollBroadcastReceiver();
        registerReceiver(mBroadcastReceiver, filter);

        vecVehicle = new Vector();
        vecDirection = new Vector();

        service.SensorPollService.vectorInit(vecVehicle, 3);
        service.SensorPollService.vectorInit(vecDirection, 3);
    }

    private class SensorPollBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTextView1.setText(intent.getExtras().getDouble("data.Mod")+"");

            String statCrash = intent.getExtras().getString("stat.crash");
            String statRapidAcc = intent.getExtras().getString("stat.rapidAcc");
            String statRapidDec = intent.getExtras().getString("stat.rapidDec");
            String statBrake = intent.getExtras().getString("stat.brake");

            mTextView2.setText(statCrash);
            mTextView3.setText(statRapidAcc);
            mTextView4.setText(statRapidDec);
            mTextView5.setText(statBrake);
            DrawSensorData(intent);
        }
    }

    private void DrawSensorData(Intent intent) {
        intentToVector(intent, "data.vec.DelGrav", vecVehicle);
        intentToVector(intent, "data.vec.Direction", vecDirection);
        CoordinateAxisDraw.setData(vecVehicle, vecDirection);
    }

    private void intentToVector(Intent intent, String key, Vector vec) {
        double x, y, z;
        x = intent.getExtras().getDouble(key + ".X");
        y = intent.getExtras().getDouble(key + ".Y");
        z = intent.getExtras().getDouble(key + ".Z");
        vec.setElementAt(x, 0);
        vec.setElementAt(y, 1);
        vec.setElementAt(z, 2);
    }
}
