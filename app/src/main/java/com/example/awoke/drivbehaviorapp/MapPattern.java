package com.example.awoke.drivbehaviorapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.awoke.drivbehaviorapp.DataSave.DataSave;
import com.example.awoke.drivbehaviorapp.DataSave.DataSaveFormat;
import com.example.awoke.drivbehaviorapp.DataSave.NowTime;

import java.util.ArrayList;
import java.util.Vector;

public class MapPattern extends Activity implements SensorEventListener {
    private TextView mTextView;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button mButtonMark;
    private Button mButtonSave;
    private DataSave mDataSave;
    private SensorManager mSensorManager;
    private LocationClient mLocationClient;
    private MyLocationData mLocationDate;

    private OverlayOptions mMarkerTest;
    private OverlayOptions mMarkerCrash;
    private OverlayOptions mMarkerRapidAcc;
    private OverlayOptions mMarkerRapidDec;
    private OverlayOptions mMarkerBrakes;
    private OverlayOptions mMarkerRollOver;
    private OverlayOptions mMarkerSuddTurn;

    private int mCurrentDirection = 0;
    private boolean fFirstLocation = true;
    private Double lastX = 0.0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    public MapPattern.SensorPollBroadcastReceiver mBroadcastReceiver;
    ArrayList<DataSaveFormat> mDataSaveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappattern);

        mDataSaveList = new ArrayList<DataSaveFormat>();
        mDataSave = new DataSave();

        initView();
        initReceiver();
        initLocation();
        initSensor();
        initMarker();
        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    private void initView() {
        mTextView = findViewById(R.id.map_text_debug);
        mMapView = findViewById(R.id.view_map);
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(mBDLocationListener);

        mButtonMark = findViewById(R.id.button_mark);
        mButtonMark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setMarkTest(mMarkerTest);
            }
        });

        mButtonSave = findViewById(R.id.button_save);
        mButtonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    mDataSave.vectorSave(mDataSaveList);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void setMarkTest(OverlayOptions marker) {
        LatLng ll = new LatLng(mCurrentLat, mCurrentLon);
        /*
        OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
                .fontSize(30).fontColor(0xFFFF00FF).text("mark").rotate(-30)
                .position(ll);
                */
        //int zoom = (int)mBaiduMap.getMapStatus().zoom;
        //int size =  ((TextOptions) marker).getFontSize();
        ((TextOptions) marker).position(ll);
        mBaiduMap.addOverlay(marker);
    }

    private void initLocation() {
        mBaiduMap.setMyLocationEnabled(true);

        /* set accuracy, default is high accuracy */
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        /*
        * coordinate selection, default gcj02
        * gcj02:  coordinates of national survey bureau
        * bd09:   baidu mercator coordinates
        * bd09ll: baidu latitude and longitude coordinates
        */
        option.setCoorType("bd09ll");

        /* Set location request interval, 0 means request once */
        int span = 5000;
        option.setScanSpan(span);

        /* set if need address info */
        option.setIsNeedAddress(true);

        /* set if use GPS */
        option.setOpenGps(true);

        /* set whether to output GPS results at 1S/1 times when GPS is valid. */
        option.setLocationNotify(true);

        /* set if need location require semantic location results */
        option.setIsNeedLocationDescribe(true);

        /* set if need set POI results */
        option.setIsNeedLocationPoiList(true);

        /*
         * sdk is a service in a independent process,
         * set if kill this process when stop
         */
        option.setIgnoreKillProcess(false);

        /* set if collect crash information */
        option.SetIgnoreCacheException(false);

        /* set if need to filter GPS simulation results */
        option.setEnableSimulateGps(false);

        /* load options */
        mLocationClient.setLocOption(option);
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_UPDATEUI);
        mBroadcastReceiver = new MapPattern.SensorPollBroadcastReceiver();
        registerReceiver(mBroadcastReceiver, filter);
    }

    private class SensorPollBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String statCrash = intent.getExtras().getString("stat.crash");
            String statRapidAcc = intent.getExtras().getString("stat.rapidAcc");
            String statRapidDec = intent.getExtras().getString("stat.rapidDec");
            String statBrake = intent.getExtras().getString("stat.brake");
            String statSuddTurn = intent.getExtras().getString("stat.suddenTurn");
            String statRollOver = intent.getExtras().getString("stat.rollOver");

            //System.out.println(statCrash);
            //System.out.println(statRapidAcc);
            //System.out.println(statRapidDec);
            //System.out.println(statBrake);
            //System.out.println(statSuddTurn);
            //System.out.println(statRollOver);

            DataSaveFormat saveData = new DataSaveFormat();
            saveData.now = new NowTime();
            NowTime.getTime(saveData.now);
            saveData.mCurrentLat = mCurrentLat;
            saveData.mCurrentLon = mCurrentLon;

            if (!statCrash.equals("null")) {
                setMarkTest(mMarkerCrash);
                saveData.drivBehavior = statCrash;
                mDataSaveList.add(saveData);
            }
            if (!statRapidAcc.equals("null")) {
                setMarkTest(mMarkerRapidAcc);
                saveData.drivBehavior = statRapidAcc;
                mDataSaveList.add(saveData);
            }
            if (!statRapidDec.equals("null")) {
                setMarkTest(mMarkerRapidDec);
                saveData.drivBehavior = statRapidDec;
                mDataSaveList.add(saveData);
            }
            if (!statBrake.equals("null")) {
                setMarkTest(mMarkerBrakes);
                saveData.drivBehavior = statBrake;
                mDataSaveList.add(saveData);
            }
            if (!statSuddTurn.equals("null")) {
                setMarkTest(mMarkerSuddTurn);
                saveData.drivBehavior = statSuddTurn;
                mDataSaveList.add(saveData);
            }
            if (!statRollOver.equals("null")) {
                setMarkTest(mMarkerRollOver);
                saveData.drivBehavior = statRollOver;
                mDataSaveList.add(saveData);
            }
        }
    }

    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    private void initMarker() {
        mMarkerTest = new TextOptions().bgColor(0xFFFFFFFF)
                .fontSize(30)
                .fontColor(0xFFF0F0F0)
                .text("[mark---test]")
                .rotate(0);
        mMarkerCrash = new TextOptions().bgColor(0xDCF398)
                .fontSize(50)
                .fontColor(0xFFFF0000)
                .text("[mark---crash]")
                .rotate(15);
        mMarkerBrakes = new TextOptions().bgColor(0xDCF398)
                .fontSize(50)
                .fontColor(0xFFFF0000)
                .text("[mark---brakes]")
                .rotate(-15);
        mMarkerRapidAcc = new TextOptions().bgColor(0xDCF398)
                .fontSize(40)
                .fontColor(0xFF00FF00)
                .text("[mark---rapidacc]")
                .rotate(45);
        mMarkerRapidDec = new TextOptions().bgColor(0xDCF398)
                .fontSize(40)
                .fontColor(0xFF00FF00)
                .text("[mark---rapiddec]")
                .rotate(-45);
        mMarkerRollOver = new TextOptions().bgColor(0xDCF398)
                .fontSize(50)
                .fontColor(0xFFFF00FF)
                .text("[mark---rollover]")
                .rotate(75);
        mMarkerSuddTurn = new TextOptions().bgColor(0xDCF398)
                .fontSize(40)
                .fontColor(0xFF0000FF)
                .text("[mark---suddturn]")
                .rotate(-75);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        /*
         * every time direction changes, relocat on the map,
         * use last time's location data
         */
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            mLocationDate = new MyLocationData.Builder().accuracy(mCurrentAccracy)
                    .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(mLocationDate);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private BDLocationListener mBDLocationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            /* structural location data */
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .direction(mCurrentDirection)
                    .build();

            /* set location data to map */
            mBaiduMap.setMyLocationData(data);
            mCurrentLat = bdLocation.getLatitude();
            mCurrentLon = bdLocation.getLongitude();
            mCurrentAccracy = bdLocation.getRadius();
            String location = "[Location] lat " + mCurrentLat + ", lon " + mCurrentLon;
            System.out.println(location);
            /*
             * configure the location layer display mode
             * @var1: location mode, NORMAL/FOLLOWING/COMPASS
             * @var2: boolean, if show direction
             * @var3: location marker, if null, use default
             */
            MyLocationConfiguration locationCfg = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.FOLLOWING, true, null);
            mBaiduMap.setMyLocationConfiguration(locationCfg);

            /* first time need to fresh map status */

            if (fFirstLocation) {
                LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(ll)// map zoom Center
                        .zoom(18f)
                        .build();// zoom multiples Baidu map supports scaling 21 level special layers to 20 level

                //changing map status
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                mBaiduMap.setMapStatus(mMapStatusUpdate);
                fFirstLocation = false;
            }
        }
    };

    @Override
    protected void onResume() {
        System.out.printf("onResume\n");
        mMapView.onResume();
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        System.out.printf("onPause\n");
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        System.out.printf("onStop\n");
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.getMap().clear();
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
