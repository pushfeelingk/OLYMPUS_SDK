package com.flk.olympussample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.flk.olympus.FLKOlympusInterface;

public class MainActivity extends AppCompatActivity implements FLKOlympusInterface.OnPushLibResultListener {
    FLKOlympusInterface flkInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flkInterface = new FLKOlympusInterface(getApplicationContext(), "", MainActivity.this);
        flkInterface.interfaceInit();

        Button testBtn = (Button)findViewById(R.id.btn_test);
        testBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                flkInterface.sendToReceiveActivity();
            }
        });
    }

    @Override
    public void onResult(int resultCode) {
        String stateMsg = "";
        if(resultCode == 1000){
            stateMsg = "정상적으로 PushAgent와 연동되었습니다.";
        }

        if(resultCode == 1001){
            stateMsg = "사용자가 PushAgent 설치를 거부하였습니다.";
        }

        if(resultCode == 700){
            stateMsg = "사용자가 위치 정보 수집 및 마케팅 메시지 수신을 거부하였습니다.";
        }

        Toast.makeText(MainActivity.this, stateMsg, Toast.LENGTH_SHORT).show();
    }
}
