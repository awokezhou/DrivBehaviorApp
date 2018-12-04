package com.example.awoke.drivbehaviorapp.DataSave;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;

public class DataSave {
    private File mDataFile;
    private XmlSerializer mXmlSerializer;
    private FileOutputStream mFileOutputStream = null;

    private final String FILE_VECTOR = "vector";
    private final String DIR_DATA_SAVE = "/DrivingBehaviorDetection/VectorSum/Data";
    private final String DIR_ROOT = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private final String CODE_ENTRY = System.getProperty("line.separator");

    public void vectorSave(ArrayList<DataSaveFormat> dataList) {
        NowTime mSaveTime = new NowTime();
        NowTime.getTime(mSaveTime);
        dirCheck(DIR_ROOT+DIR_DATA_SAVE+File.separator);
        mDataFile = new File(DIR_ROOT+DIR_DATA_SAVE,
                FILE_VECTOR+mSaveTime.Date+"-"+mSaveTime.Time+".xml");
        try {
            mFileOutputStream = new FileOutputStream(mDataFile);
            mXmlSerializer = Xml.newSerializer();
            mXmlSerializer.setOutput(mFileOutputStream, "utf-8");
            mXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            mXmlSerializer.startDocument("utf-8", true);
            mXmlSerializer.setPrefix("Vector", "http://com.DrivingBehaviorDetection.VectorSum.Data.Vector");
            mXmlSerializer.startTag(null, "vectors");
            mXmlSerializer.attribute(null, "Date", mSaveTime.DateTime);
            mXmlSerializer.attribute(null, "EntryNumber", dataList.size()+"");

            for (DataSaveFormat data:dataList) {
                mXmlSerializer.startTag(null, "vector");
                mXmlSerializer.attribute(null, "Date", data.now.Time);
                /*
                mXmlSerializer.startTag(null, "Sensor");
                mXmlSerializer.text(vectorToString(data.vecSensor));
                mXmlSerializer.endTag(null, "Sensor");
                mXmlSerializer.startTag(null, "Gravity");
                mXmlSerializer.text(vectorToString(data.vecGravity));
                mXmlSerializer.endTag(null, "Gravity");
                mXmlSerializer.startTag(null, "Vehicle");
                mXmlSerializer.text(vectorToString(data.vecVehicle));
                mXmlSerializer.endTag(null, "Vehicle");
                mXmlSerializer.startTag(null, "Vehicle Sum");
                mXmlSerializer.text(vectorToString(data.vecVehisum));
                mXmlSerializer.endTag(null, "Vehicle Sum");
                mXmlSerializer.startTag(null, "Direction");
                mXmlSerializer.text(vectorToString(data.vecDirection));
                mXmlSerializer.endTag(null, "Direction");
                mXmlSerializer.startTag(null, "Acceleration");
                mXmlSerializer.text(vectorToString(data.vecAcceleration));
                mXmlSerializer.endTag(null, "Acceleration");
                mXmlSerializer.startTag(null, "AccelerationMod");
                mXmlSerializer.text(data.AccelerationMod+"");
                mXmlSerializer.endTag(null, "AccelerationMod");*/
                mXmlSerializer.startTag(null, "Behavior");
                mXmlSerializer.text(data.drivBehavior+"");
                mXmlSerializer.endTag(null, "Behavior");
                mXmlSerializer.startTag(null, "mCurrentLat");
                mXmlSerializer.text(data.mCurrentLat+"");
                mXmlSerializer.endTag(null, "mCurrentLat");
                mXmlSerializer.startTag(null, "mCurrentLon");
                mXmlSerializer.text(data.mCurrentLon+"");
                mXmlSerializer.endTag(null, "mCurrentLon");

                mXmlSerializer.endTag(null, "vector");
            }

            mXmlSerializer.endTag(null, "vectors");
            mXmlSerializer.endDocument();
            mFileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean dirCheck(String dir) {
        File file = new File(dir);
        if(!file.exists()) {
            file.mkdirs();
            return false;
        }
        return true;
    }

    private String vectorToString(Vector vec) {
        return "\t(" + vec.get(0).toString() + ",\t" +
                vec.get(1).toString() + ",\t" +
                vec.get(2).toString() + ")";
    }
}
