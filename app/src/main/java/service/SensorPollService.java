package service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.awoke.drivbehaviorapp.MainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class SensorPollService extends Service implements SensorEventListener {

    private Sensor mGyroscope;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;

    private ArrayList<Vector> gyroList;
    private ArrayList<Vector> gravityList;
    private ArrayList<Vector> vecVehicleList;
    private Vector gravityVec, accVec, accDelGravityVec, gyroVec;
    private Vector accAngleVec, gyroAngleVec, angleVec;
    private Vector vecSensor, vecVehicle, vecVehisum, vecAcceleration, vecDirection;
    double mod;

    private boolean ifGravityCalculated = false;
    private final int gravityCalculateCount = 50;
    private final int GYRO_LIST_RECORD_TIME = 4000;
    private final double RO_ANGLE = 46.0;
    private final double CRASH_MOD = 14;
    private boolean fRollOver = false;
    private boolean fCrash = false;
    private boolean fRapidAcc = false;
    private boolean fRapidDec = false;
    private boolean fBrake = false;
    private boolean fSuddenTurn = false;

    private Timer timer;
    private TimerTask task;
    private int count;
    Intent intent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        initCalculateData();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        final Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_UPDATEUI);
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                sensorDataStruct(intent);
                sendBroadcast(intent);
            }
        };
        timer.schedule(task, 100, 1000);
    }

    private void sensorDataStruct(Intent intent) {

        if (ifGravityCalculated == false) {
            String dataGravity = "gravity calculating... " + gravityList.size();
            intent.putExtra("data.vecGravity", dataGravity);
        } else {
            String dataGravity = "vec gravity (" + gravityVec.toString() + ")";
            intent.putExtra("data.vecGravity", dataGravity);
            String dataDelGrav = "vec gravity remove (" + accDelGravityVec.toString() + ")";
            intent.putExtra("data.vecDelGrav", dataDelGrav);
            intent.putExtra("data.vec.DelGrav.X", vectorX(accDelGravityVec));
            intent.putExtra("data.vec.DelGrav.Y", vectorY(accDelGravityVec));
            intent.putExtra("data.vec.DelGrav.Z", vectorZ(accDelGravityVec));
        }

        String dataAccVec = "vec acc (" + accVec.toString() + ")";
        intent.putExtra("data.vecAcc", dataAccVec);

        String dataAccAngle = "vec acc angle (" + accAngleVec.toString() + ")";
        intent.putExtra("data.vecAccAngle", dataAccAngle);

        String dataGyrAngle = "vec gyr angle (" + gyroAngleVec.toString() + ")";
        intent.putExtra("data.vecgyrAngle", dataGyrAngle);

        String dataCmpAngle = "vec cmp angle (" + angleVec.toString() + ")";
        intent.putExtra("data.vecCmpAngle", dataCmpAngle);

        String dataDirection = "vec direction (" + vecDirection.toString() + ")";
        intent.putExtra("data.vecDirection", dataDirection);
        intent.putExtra("data.vec.Direction.X", vectorX(vecDirection));
        intent.putExtra("data.vec.Direction.Y", vectorY(vecDirection));
        intent.putExtra("data.vec.Direction.Z", vectorZ(vecDirection));

        String dataAcceleration = "vec acceleration (" + vecAcceleration.toString() + ")";
        intent.putExtra("data.vecAcceleration", dataAcceleration);

        intent.putExtra("data.Mod", mod);

        if (fRollOver == true) {
            String statRollOver = "Roll Over";
            intent.putExtra("stat.rollOver", statRollOver);
        } else {
            String statRollOver = "null";
            intent.putExtra("stat.rollOver", statRollOver);
        }

        if (fCrash == true) {
            String statCrash = "Crash";
            intent.putExtra("stat.crash", statCrash);
        } else {
            String statCrash = "null";
            intent.putExtra("stat.crash", statCrash);
        }

        if (fRapidAcc == true) {
            String statRapidAcc = "Rapid Acceleration";
            intent.putExtra("stat.rapidAcc", statRapidAcc);
        } else {
            String statRapidAcc = "null";
            intent.putExtra("stat.rapidAcc", statRapidAcc);
        }

        if (fRapidDec == true) {
            String statRapidDec = "Rapid Deceleration";
            intent.putExtra("stat.rapidDec", statRapidDec);
        } else {
            String statRapidDec = "null";
            intent.putExtra("stat.rapidDec", statRapidDec);
        }

        if (fBrake == true) {
            String statBrake = "Brakes";
            intent.putExtra("stat.brake", statBrake);
        } else {
            String statBrake = "null";
            intent.putExtra("stat.brake", statBrake);
        }

        if (fSuddenTurn == true) {
            String statSuddenTurn = "Sudden Turn";
            intent.putExtra("stat.suddenTurn", statSuddenTurn);
        } else {
            String statSuddenTurn = "null";
            intent.putExtra("stat.suddenTurn", statSuddenTurn);
        }
    }

    /*
    @Override
    public void onCreate() {
        super.onCreate();
        final Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_UPDATEUI);
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                intent.putExtra("count", ++count);
                sendBroadcast(intent);
            }
        };
        timer.schedule(task, 1000, 1000);
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /* Accuracy Changed */
        System.out.println("[SensorListener] onAccuracyChanged");
    }

    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            procAccEvent(event);
        } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            procGyroEvent(event);
        }
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
    }

    private void initCalculateData() {
        gyroList = new ArrayList<Vector>();
        gravityList = new ArrayList<Vector>();

        accVec = new Vector();
        gyroVec = new Vector();
        angleVec = new Vector();
        gravityVec = new Vector();
        accAngleVec = new Vector();
        gyroAngleVec = new Vector();
        accDelGravityVec = new Vector();

        vecSensor = new Vector();
        vecVehicle = new Vector();
        vecVehisum = new Vector();
        vecAcceleration = new Vector();
        vecDirection = new Vector();

        vectorInit(accVec, 3);
        vectorInit(gyroVec, 4);
        vectorInit(angleVec, 3);
        vectorInit(gravityVec, 3);
        vectorInit(accAngleVec, 3);
        vectorInit(gyroAngleVec, 3);
        vectorInit(accDelGravityVec, 3);

        vectorInit(vecSensor, 3);
        vectorInit(vecVehicle, 3);
        vectorInit(vecVehisum, 3);
        vectorInit(vecAcceleration, 3);
        vectorInit(vecDirection, 3);
    }

    private void procAccEvent(SensorEvent event) {
        eventToVector(event, accVec);
        vectorAccuracy(accVec, 2);
        gravityCalculate(accVec, gravityVec, gravityList);
        if (ifGravityCalculated == true) {
            accDelGravityCalculate(accVec, gravityVec, accDelGravityVec);
            DirectionCalculate(accDelGravityVec, vecDirection, vecVehicleList);
            mod = AccelerationCalculate(accDelGravityVec, vecDirection, vecAcceleration);
        }
        accAngleCalculate(accVec, accAngleVec);
    }

    private void gravityCalculate(Vector vs, Vector vg, ArrayList<Vector> list) {
        if (ifGravityCalculated == false) {
            list.add(vs);
            if (list.size() >= gravityCalculateCount) {
                double x, y, z;
                Vector vecSum = new Vector();
                vectorInit(vecSum, 3);
                vectorSum(list, vecSum);
                x = Double.valueOf(vecSum.get(0).toString())/list.size();
                y = Double.valueOf(vecSum.get(1).toString())/list.size();
                z = Double.valueOf(vecSum.get(2).toString())/list.size();
                vg.setElementAt(x, 0);
                vg.setElementAt(y, 1);
                vg.setElementAt(z, 2);
                vectorAccuracy(vg, 2);
                ifGravityCalculated = true;
                System.out.println("[Activity] gravity calculated ok");
            }
        }
    }

    private void DirectionCalculate(Vector vv, Vector vd, ArrayList<Vector> list) {
        //list.add(vv);
        double xv, yv, zv;
        double xsum, ysum, zsum;
        xv = vectorX(vv);
        yv = vectorY(vv);
        zv = vectorZ(vv);
        xsum = vectorX(vecVehisum)*0.97 + xv;
        ysum = vectorY(vecVehisum)*0.97 + yv;
        zsum = vectorZ(vecVehisum)*0.97 + zv;
        vecVehisum.setElementAt(xsum, 0);
        vecVehisum.setElementAt(ysum, 1);
        vecVehisum.setElementAt(zsum, 2);
        vectorAccuracy(vecVehisum, 2);
        vectorToUnit(vecVehisum, vd);
        vectorAccuracy(vd, 2);
    }

    private double AccelerationCalculate(Vector vv, Vector vd, Vector va) {
        double mod;
        double xv, yv, zv;
        double xd, yd, zd;
        double xa, ya, za;
        xv = Double.valueOf(vv.get(0).toString());
        yv = Double.valueOf(vv.get(1).toString());
        zv = Double.valueOf(vv.get(2).toString());
        xd = Double.valueOf(vd.get(0).toString());
        yd = Double.valueOf(vd.get(1).toString());
        zd = Double.valueOf(vd.get(2).toString());
        xa = xv*xd;
        ya = yv*yd;
        za = zv*zd;
        mod = xa + ya + za;
        mod = doubleAccuracy(mod, 2);
        va.setElementAt(xa, 0);
        va.setElementAt(ya, 1);
        va.setElementAt(za, 2);
        vectorAccuracy(va, 2);
        return mod;
    }

    private void accDelGravityCalculate(Vector vs, Vector vg, Vector va) {
        double xs, ys, zs;
        double xg, yg, zg;
        double xa, ya, za;
        xs = vectorX(vs);
        ys = vectorY(vs);
        zs = vectorZ(vs);
        xg = vectorX(vg);
        yg = vectorY(vg);
        zg = vectorZ(vg);
        xa = xs - xg;
        ya = ys - yg;
        za = zs - zg;
        va.setElementAt(xa, 0);
        va.setElementAt(ya, 1);
        va.setElementAt(za, 2);
        vectorAccuracy(va, 2);
    }

    private void accAngleCalculate(Vector va, Vector vangle) {
        double pai = 3.14;
        double xa, ya, za;
        double roll = 0;
        double pitch = 0;
        double yaw = 0;
        xa = vectorX(va);
        ya = vectorY(va);
        za = vectorZ(va);
        roll = Math.atan2(xa, Math.sqrt(Math.pow(ya, 2)+Math.pow(za, 2)))*180/pai;
        pitch = Math.atan2(ya, Math.sqrt(Math.pow(xa, 2)+Math.pow(za, 2)))*180/pai;
        yaw = Math.atan2(za, Math.sqrt(Math.pow(xa, 2)+Math.pow(ya, 2)))*180/pai;
        vangle.setElementAt(roll, 0);
        vangle.setElementAt(pitch, 1);
        vangle.setElementAt(yaw, 2);
        vectorAccuracy(vangle, 2);
    }

    private void procGyroEvent(SensorEvent event) {
        double intv, start, end;
        long times = System.currentTimeMillis();
        Vector sensorVec = new Vector();
        vectorInit(sensorVec, 4);
        eventToVector(event, sensorVec);
        sensorVec.setElementAt(times, 3);
        vectorAccuracy(sensorVec, 2);

        gyroList.add(sensorVec);

        end = Double.valueOf(gyroList.get(gyroList.size()-1).get(3).toString());
        start = Double.valueOf(gyroList.get(0).get(3).toString());

        if ( (end - start) > GYRO_LIST_RECORD_TIME){
            gyroList.remove(0);
        }

        gyroAngleCalculate(gyroList, gyroAngleVec);
        compAngleCalculate(accAngleVec, gyroAngleVec, angleVec, 0.1);
        behaviorDetection();
    }

    private void gyroAngleCalculate(ArrayList<Vector> list, Vector vangle) {
        double roll = 0;
        double pitch = 0;
        double yaw = 0;

        double intv = GYRO_LIST_RECORD_TIME/list.size();
        /*
        String debug = "gyro list size " + list.size();
        mDebug.setDebug(mTextView2, mDebug.DI_VEC_GRAVITY, debug);
        */
        for(Vector vec:list) {
            roll += vectorX(vec)*intv/1000;
            pitch += vectorY(vec)*intv/1000;
            yaw += vectorZ(vec)*intv/1000;
        }

        vangle.setElementAt(Math.toDegrees(roll), 0);
        vangle.setElementAt(Math.toDegrees(pitch), 1);
        vangle.setElementAt(Math.toDegrees(yaw), 2);
        vectorAccuracy(vangle, 2);
    }

    private void compAngleCalculate(Vector va_angle, Vector vg_angle, Vector vangle, double weight) {
        double r_va, p_va, y_va;
        double r_vg, p_vg, y_vg;
        double r_angle, p_angle, y_angle;
        r_va = vectorX(va_angle);
        p_va = vectorY(va_angle);
        y_va = vectorZ(va_angle);
        r_vg = vectorX(vg_angle);
        p_vg = vectorY(vg_angle);
        y_vg = vectorZ(vg_angle);
        r_angle = weight*r_vg + (1-weight)*r_va;
        p_angle = weight*p_va + (1-weight)*p_vg;
        y_angle = weight*y_va + (1-weight)*y_vg;
        vangle.setElementAt(r_angle, 0);
        vangle.setElementAt(p_angle, 1);
        vangle.setElementAt(y_angle, 2);
        vectorAccuracy(vangle, 2);
    }

    private void behaviorDetection() {
        double corner = vectorZ(gyroAngleVec);
        double angle_z = Math.abs(vectorZ(angleVec));
        double angle_xy = Math.abs(vectorX(angleVec)) + Math.abs(vectorY(angleVec));
        if ((angle_xy > RO_ANGLE || angle_z > (RO_ANGLE+90)) && (fRollOver == false)) {
            fRollOver = true;
        } else if ((angle_xy <= RO_ANGLE) && (angle_z <= (RO_ANGLE+90)) && (fRollOver == true)) {
            fRollOver = false;
        }

        if (corner <= -180 || corner >= 180) {
            corner = 360 - Math.abs(corner);
        }
        if ((corner > 60) && (fSuddenTurn == false)) {
            fSuddenTurn = true;
        } else if ((corner < 60) && (fSuddenTurn == true)) {
            fSuddenTurn = false;
        }

        if ((mod >= CRASH_MOD) && (fCrash == false)) {
            fCrash = true;
        } else if ((mod < CRASH_MOD) && (fCrash == true)) {
            fCrash = false;
        }

        if ((mod > 2 && mod < CRASH_MOD) && (fRapidAcc == false)) {
            fRapidAcc = true;
        } else if ((mod < 2) && (fRapidAcc == true)) {
            fRapidAcc = false;
        }

        if ((mod < -3.5 && mod > -5) && (fRapidDec == false)) {
            fRapidDec = true;
        } else if ((mod > -3.5) && (fRapidDec == true)) {
            fRapidDec = false;
        }

        if ((mod < -5) && (fBrake == false)) {
            fBrake = true;
        } else if ((mod > -5) && (fBrake == true)) {
            fBrake = false;
        }
    }

    private void eventToVector(SensorEvent event, Vector vec) {
        double x, y, z;
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        vec.setElementAt(x, 0);
        vec.setElementAt(y, 1);
        vec.setElementAt(z, 2);
    }

    private void vectorAccuracy(Vector vec, int accuracy) {
        double data;
        String fmtString = "0.";
        for (int i=0; i<accuracy; i++) {
            fmtString += "0";
        }
        DecimalFormat fmt = new DecimalFormat(fmtString);

        for (int i=0; i<vec.size(); i++) {
            data = Double.valueOf(vec.get(i).toString());
            vec.setElementAt(Double.valueOf(fmt.format(data)), i);
        }
    }

    public static void vectorInit(Vector vec, int len) {
        for (int i=0; i<len; i++) {
            vec.addElement(0);
        }
    }

    private void vectorSum(ArrayList<Vector> list, Vector vsum) {
        double xsum=0, ysum=0, zsum=0;
        for (Vector vec:list) {
            xsum += Double.valueOf(vec.get(0).toString());
            ysum += Double.valueOf(vec.get(1).toString());
            zsum += Double.valueOf(vec.get(2).toString());
        }
        vsum.setElementAt(xsum, 0);
        vsum.setElementAt(ysum, 1);
        vsum.setElementAt(zsum, 2);
    }

    private double vectorX(Vector vec) { return Double.valueOf(vec.get(0).toString()); }

    private double vectorY(Vector vec) {
        return Double.valueOf(vec.get(1).toString());
    }

    private double vectorZ(Vector vec) {
        return Double.valueOf(vec.get(2).toString());
    }

    private void vectorToUnit(Vector vsum, Vector vuit) {
        double mod;
        double xsum, ysum, zsum;
        double xuit, yuit, zuit;
        xsum = Double.valueOf(vsum.get(0).toString());
        ysum = Double.valueOf(vsum.get(1).toString());
        zsum = Double.valueOf(vsum.get(2).toString());
        mod = Math.sqrt(Math.pow(xsum, 2) + Math.pow(ysum, 2) + Math.pow(zsum, 2));
        xuit = xsum/mod;
        yuit = ysum/mod;
        zuit = zsum/mod;
        vuit.setElementAt(xuit, 0);
        vuit.setElementAt(yuit, 1);
        vuit.setElementAt(zuit, 2);
    }

    private double doubleAccuracy(double data, int accuracy) {
        double tmp;
        String fmtString = "0.";
        for (int i=0; i<accuracy; i++) {
            fmtString += "0";
        }
        DecimalFormat fmt = new DecimalFormat(fmtString);
        tmp = data;
        data = Double.valueOf(fmt.format(tmp));
        return data;
    }
}
