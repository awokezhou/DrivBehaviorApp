package com.example.awoke.drivbehaviorapp.CoordinateAxis;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

public class CoordinateAxisDraw {

    private FloatBuffer axisVectorBuffer;
    private FloatBuffer axisColorBuffer;
    private FloatBuffer vehicleVectorBuffer;
    private FloatBuffer vehicleColorBuffer;
    private FloatBuffer AccelerationVectorBuffer;
    private FloatBuffer AccelerationColorBuffer;

    private final float AXIS_MAX_LEN = 3.0f;

    private float axisVectorArray[] = {
            -AXIS_MAX_LEN,0.0f,0.0f,
            AXIS_MAX_LEN,0.0f,0.0f,
            0.0f,-AXIS_MAX_LEN,0.0f,
            0.0f,AXIS_MAX_LEN,0.0f,
            0.0f,0.0f,-AXIS_MAX_LEN,
            0.0f,0.0f,AXIS_MAX_LEN
    };

    private float axisColorArray[] = {
            0.0f,0.0f,0.0f,1.0f,
            0.0f,0.0f,0.0f,1.0f,
            0.0f,0.0f,0.0f,1.0f,
            0.0f,0.0f,0.0f,1.0f,
            0.0f,0.0f,0.0f,1.0f,
            0.0f,0.0f,0.0f,1.0f,
    };

    static private float vehicleVectorArray[] = {
            0.0f, 0.0f, 0.0f,
            0.3f, 0.5f, 0.7f,
    };

    static private float AccelerationVectorArray[] = {
            0.0f, 0.0f, 0.0f,
            0.7f, 0.5f, 0.3f,
    };

    private float vehicleColorArray[] = {
            0.0f,1.0f,0.0f,0.0f,
            0.0f,1.0f,0.0f,0.0f
    };

    private float AccelerationColorArray[] = {
            1.0f,0.0f,0.0f,0.0f,
            1.0f,0.0f,0.0f,0.0f
    };

    public CoordinateAxisDraw() {
        //arrayToBuffer(axisVectorArray, axisVectorBuffer);
        //arrayToBuffer(axisColorArray, axisColorBuffer);
        //arrayToBuffer(dataVectorArray, dataVectorBuffer);
        //arrayToBuffer(dataColorArray, dataColorBuffer);

        ByteBuffer vbb=ByteBuffer.allocateDirect(axisVectorArray.length*4);
        vbb.order(ByteOrder.nativeOrder());
        axisVectorBuffer=vbb.asFloatBuffer();
        axisVectorBuffer.put(axisVectorArray);
        axisVectorBuffer.position(0);

        ByteBuffer cbb=ByteBuffer.allocateDirect(axisColorArray.length*4);
        cbb.order(ByteOrder.nativeOrder());
        axisColorBuffer=cbb.asFloatBuffer();
        axisColorBuffer.put(axisColorArray);
        axisColorBuffer.position(0);

        /*
        Random random = new Random();
        float ran = random.nextInt(5)%(5-1+1) + 1;
        dataVectorArray[3] = dataVectorArray[3] - ran/10;
        dataVectorArray[4] = dataVectorArray[4] - ran/10;
        dataVectorArray[5] = dataVectorArray[5] - ran/10;
        */
        ByteBuffer vva = ByteBuffer.allocateDirect(vehicleVectorArray.length*4);
        vva.order(ByteOrder.nativeOrder());
        vehicleVectorBuffer=vva.asFloatBuffer();
        vehicleVectorBuffer.put(vehicleVectorArray);
        vehicleVectorBuffer.position(0);

        ByteBuffer vca = ByteBuffer.allocateDirect(vehicleColorArray.length*4);
        vca.order(ByteOrder.nativeOrder());
        vehicleColorBuffer=vca.asFloatBuffer();
        vehicleColorBuffer.put(vehicleColorArray);
        vehicleColorBuffer.position(0);

        ByteBuffer ava = ByteBuffer.allocateDirect(AccelerationVectorArray.length*4);
        ava.order(ByteOrder.nativeOrder());
        AccelerationVectorBuffer=ava.asFloatBuffer();
        AccelerationVectorBuffer.put(AccelerationVectorArray);
        AccelerationVectorBuffer.position(0);

        ByteBuffer aca = ByteBuffer.allocateDirect(AccelerationColorArray.length*4);
        aca.order(ByteOrder.nativeOrder());
        AccelerationColorBuffer=aca.asFloatBuffer();
        AccelerationColorBuffer.put(AccelerationColorArray);
        AccelerationColorBuffer.position(0);
    }

    public static void setData(Vector vehicle, Vector Acceleration) {
        vehicleVectorArray[3] = -Float.valueOf(vehicle.get(0).toString());
        vehicleVectorArray[4] = -Float.valueOf(vehicle.get(1).toString());
        vehicleVectorArray[5] = -Float.valueOf(vehicle.get(2).toString());
        AccelerationVectorArray[3] = -Float.valueOf(Acceleration.get(0).toString());
        AccelerationVectorArray[4] = -Float.valueOf(Acceleration.get(1).toString());
        AccelerationVectorArray[5] = -Float.valueOf(Acceleration.get(2).toString());
    }

    private void arrayToBuffer(float src[], FloatBuffer dst) {
        ByteBuffer bf = ByteBuffer.allocateDirect(src.length*4);
        bf.order(ByteOrder.nativeOrder());
        dst = bf.asFloatBuffer();
        dst.put(src);
        dst.position(0);
    }

    public void draw(GL10 gl) {

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        // Axis Draw
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,axisVectorBuffer);
        gl.glColorPointer(4,GL10.GL_FLOAT,0,axisColorBuffer);
        gl.glDrawArrays(GL10.GL_LINES,0,axisVectorArray.length/3);

        // Data Draw
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,vehicleVectorBuffer);
        gl.glColorPointer(4,GL10.GL_FLOAT,0,vehicleColorBuffer);
        gl.glDrawArrays(GL10.GL_LINES,0,vehicleVectorArray.length/3);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,AccelerationVectorBuffer);
        gl.glColorPointer(4,GL10.GL_FLOAT,0,AccelerationColorBuffer);
        gl.glDrawArrays(GL10.GL_LINES,0,AccelerationVectorArray.length/3);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
