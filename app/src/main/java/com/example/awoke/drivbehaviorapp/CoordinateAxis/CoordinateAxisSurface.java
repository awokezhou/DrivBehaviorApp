package com.example.awoke.drivbehaviorapp.CoordinateAxis;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;

public class CoordinateAxisSurface extends AppCompatActivity {

    private GLSurfaceView mView;
    private CoordinateAxisRenderer mRenderer;

    public CoordinateAxisSurface(@android.support.annotation.IdRes int id) {
        mRenderer = new CoordinateAxisRenderer();
        mView = findViewById(id);
        mView.setRenderer(mRenderer);
    }
}
