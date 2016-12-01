package com.flk.olympus.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.flk.olympus.FLKOlympusInterface;
import com.flk.olympus.FLKOlympusInterface.OnPushLibResultListener;


public class MainActivity extends Activity implements OnPushLibResultListener{
	FLKOlympusInterface flkInterface;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /** 
         * FLKPushAgentLIB 인터페이스 생성 
         * this : Context
         * this : FLKPushInterface.OnPushLibResultListener OLYMPUSH 앱 연동 결과를 리턴해준다.
         */
        flkInterface = new FLKOlympusInterface(this, this);
        // service에 사용되는 사용자 고유 키 값이 있다면 아래의 userKey에 세팅하여 사용 가능.
        //flkInterface = new FLKPushInterface(this, "userKey", this);
        flkInterface.interfaceInit();
        
        Button testBtn = (Button)findViewById(R.id.btn_test);
        testBtn.setOnClickListener(new OnClickListener() {
			
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
