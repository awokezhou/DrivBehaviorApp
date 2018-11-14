package com.example.awoke.drivbehaviorapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import service.SensorPollService;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_UPDATEUI = "action.updateUI";
    private BroadcastReceiver receiver;
    private TextView mDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSubActivity();
        initService();
        mDebug = findViewById(R.id.debugs);
    }

    private void initSubActivity() {
        Button base = findViewById(R.id.button_activity_base);
        Button view3d = findViewById(R.id.button_activity_3d);
        Button map = findViewById(R.id.button_activity_map);

        base.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BasePattern.class);
                MainActivity.this.startActivity(intent);
            }
        });

        view3d.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, View3dPattern.class);
                MainActivity.this.startActivity(intent);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapPattern.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void initService() {
        //启动服务
        Intent i = new Intent(this,  SensorPollService.class);
        //下面写自己的路径
        i.setAction("service.SensorPollService");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startService(i);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATEUI);
        receiver = new mBroadcastReceiver();
        registerReceiver(receiver, filter);
    }

    private class mBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mDebug.setText("get data");
        }
    }

    protected void onResume() {
        super.onResume();
        System.out.println("[SensorListener] registerListener");
    }

    protected void onPause() {
        super.onPause();
        System.out.println("[SensorListener] unregisterListener");
    }
}
