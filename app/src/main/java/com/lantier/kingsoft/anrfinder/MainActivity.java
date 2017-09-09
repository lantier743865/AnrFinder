package com.lantier.kingsoft.anrfinder;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lantier.kingsoft.develop.crashintercepter.CrashIntercepter;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button tvTest;
    private Button tvIo;
    private Button tv_install;
    private Button tv_exception;
    private Button tv_unInstall;
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
        tv_install = (Button) findViewById(R.id.tv_install);
        tv_install.setOnClickListener(this);
        tv_exception = (Button) findViewById(R.id.tv_exception);
        tv_exception.setOnClickListener(this);
        tv_unInstall = (Button) findViewById(R.id.tv_unInstall);
        tv_unInstall.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_test:
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "you hava sleep 2000ms", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_io:
                double compute = compute();
                Log.e(TAG, "---->>compute: "+compute );
                break;
            case R.id.tv_install:
                Log.e(TAG, "---->>tv_install: " );
                install();
                break;
            case R.id.tv_exception:
                Log.e(TAG, "---->>tv_exception: " );
                makeException();
                break;
            case R.id.tv_unInstall:
                Log.e(TAG, "---->>tv_unInstall: " );
                uninsatll();
                break;

        }
    }

    private void uninsatll() {
        CrashIntercepter.unInstall();
    }

    private void makeException() {
        int i = 0;
        int j = 1 / 0;
    }

    private void install() {
        //handleException内部建议手动try{自己的异常逻辑}catch (Throwable e){},以防
        //handleException内部再次抛出异常，导致循环调用handleException
        CrashIntercepter.install(new CrashIntercepter.ExceptionHandler() {
            @Override
            public void handleException(final Thread thread, final Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e(TAG, "---->>thread: "+thread+",throwable:"+throwable );
                            throwable.printStackTrace();
//                            throwable.fillInStackTrace();
                            Toast.makeText(MainActivity.this, "Exception Happend \n "+thread +"\n" +throwable.toString(), Toast.LENGTH_SHORT).show();
                        } catch (Throwable e){
                        }
                    }
                });
            }
        });
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
