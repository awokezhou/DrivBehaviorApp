package com.example.awoke.drivbehaviorapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

public class MapPattern extends Activity implements SensorEventListener {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button mButtonMark;
    private SensorManager mSensorManager;
    private LocationClient mLocationClient;
    private MyLocationData mLocationDate;

    private int mCurrentDirection = 0;
    private boolean fFirstLocation = true;
    private Double lastX = 0.0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappattern);

        initView();
        initLocation();
        initSensor();
        mLocationClient.start();
    }

    private void initView() {
        mMapView = findViewById(R.id.view_map);
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(mBDLocationListener);

        mButtonMark = findViewById(R.id.button_mark);
        mButtonMark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setMarkTest();
            }
        });
    }

    private void setMarkTest() {
        LatLng ll = new LatLng(mCurrentLat, mCurrentLon);
        OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
                .fontSize(30).fontColor(0xFFFF00FF).text("mark").rotate(-30)
                .position(ll);
        mBaiduMap.addOverlay(ooText);
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
        int span = 1000;
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

    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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
                MapStatus.Builder builder = new MapStatus.Builder()
                        .target(ll)// map zoom Center
                        .zoom(18f);// zoom multiples Baidu map supports scaling 21 level special layers to 20 level

                /* changing map status */
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                fFirstLocation = false;
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

    }
}
