package com.example.awoke.drivbehaviorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSubActivity();
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
}
