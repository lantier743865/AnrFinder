package com.lantier.kingsoft.anrfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button tvTest;
    private Button tvIo;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tvTest = (Button) findViewById(R.id.tv_test);
        tvTest.setOnClickListener(this);
        tvIo = (Button) findViewById(R.id.tv_io);
        tvIo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_test){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "you hava sleep 2000ms", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.tv_io){
            double compute = compute();
            Log.e(TAG, "---->>compute: "+compute );

        }
    }

    private double compute() {
        double result = 0;
        for (int i = 0; i < 1000000; ++i) {
            result += Math.acos(Math.cos(i));
            result -= Math.sin(Math.sin(i));
        }
        return result;
    }
}
