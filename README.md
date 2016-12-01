## OLYMPUS Android SDK 연동가이드

※ 본 가이드는 OLYMPUS 서비스를 제공하는데 필요한 Agent와의 연동에 목적이 있다. 
 
### 목차
1. OLYMPUS Service 용어 설명 
2. OLYMPUS Service 주의사항 
3. LIB 적용 및 Sample Code

<br>
#### OLYMPUS Service 용어 설명 

| 용어 	       |  의미                                	 					|
| ------------- | ---------------------------------------------------------- |
| 3rd App      	| OLYMPUS Agent와 연동하는 Application						   |
| AgentID(AID)  | OLYMPUS Server로부터 발급받은 Agent의 고유 Key<br> * Agent가 실행되면 내부적으로 발급되는 값 					 |
| AppID 		| 3rd App을 식별할 수 있는 고유 Key<br>* 10자리 Hex 코드 값으로 Feelingk로부터 발급받은 고유값을 사용한다.<br> * 해당 값은 3rd App의 package 이름과 매핑되어 관리 되기 때문에 임의의 값을 사용시 Service 사용 불가하다.|
    
 <br>   
    
#### OLYMPUS Service 주의사항 
* OLYMPUSH Agent의 SDK Min Version은 14(Android 4.0)으로, 이하 버전 지원 프로젝트는 연동이 불가합니다.
* OLYMPUSH Agent의 SDK Target Version은 23(Android 6.0)으로 설정해야 합니다.
* 해당 SDK는 Test용 샘플으로, 실제 OLYMPUS Service 사용을 원할시 하단의 `License` 항목을 참고 하세요.


<br>
    
#### 개발환경 구성
1. AndroidStudio에서 개발하기

	* Olympus SDK Project를 다운받는다. Project Structure를 열고 Module 추가를 선택한다. 
	* Import Eclipse ADT Project를 선택하고 next 버튼을 누른다.

        <img src="https://cloud.githubusercontent.com/assets/22470636/20710222/aef11a02-b67c-11e6-859f-9d0180c5cce9.png"/>
        
	* Olympus SDK Projcet를 다운 받은 경로를 선택하고, Module name를 확인한다.

	<img src="https://cloud.githubusercontent.com/assets/22470636/20710221/aef0feaa-b67c-11e6-92e5-9f0243f6f2e3.png"/>
                
	* app > Dependencies에서 위에서 추가한 module Dependency를 등록한다.
	<img src="https://cloud.githubusercontent.com/assets/22470636/20710223/aef187bc-b67c-11e6-84dd-9b45aa9ee2a9.png"/>


	
2. Eclipse에서 개발하기 
	
 	* Olympus SDK Project를 다운로드 받아 Eclipse에 import한다. SDK를 적용할 프로젝트의 Properties를 선택한다. Library영역에 FLK_Olympus_SDK를 추가한다.
  
		<img src="https://cloud.githubusercontent.com/assets/22470636/20701794/2b9f07dc-b658-11e6-927e-2be3a24515c4.png" />

3. 프로가드 적용시 아래 설정을 추가한다.
	``` txt
	-dontwarn org.apache.**
	-keep class com.feelingk.pushagent.** { *; }
	-keep class com.flk.olympus.** { *; }
	```

<br>
    
#### SDK 적용 및 Sample Code
1. Sample Project에 assets폴더에 있는 'flk_olympus_push_terms.html' 파일을 SDK를 적용할 Project의 assets폴더에 복사한다.
2. res > values > strings.xml에 AppID값을 등록한다. 
	```xml
    <resources>
    	<string name="flk_olympus_appid">[Fleeingk로부터 발급 받은 10자리의 AppID]</string>
	</resources>
    ```

3. AndroidManifest.xml을 설정한다. 
	* user-permission 리스트를 적용한다.
	```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    ```
	* 아래의 규격은 FLK_OLYMPUS_SDK 내에서 사용하는 연동 규격들로, 'application' 태그 내에 적용한다.
	```xml
    <meta-data android:name="flk.olympus.AppID" android:value="@string/flk_olympus_appid" />
    
    	<receiver android:name="com.flk.olympus.FLKPushAgentReceiver" >
            <intent-filter>
                <action android:name="com.feelingk.olympus.pushagent.lib.RECEIVED_APP_REG_ID" />
                <action android:name="com.feelingk.olympus.pushagent.lib.RECEIVED_REG_PARAM_ERROR" />
                <action android:name="com.feelingk.olympus.pushagent.lib.RECEIVED_REG_RESULT_ERROR" />
                <action android:name="com.feelingk.olympus.pushagent.lib.RECEIVED_APP_MSG_INFO"/>

                <data android:host="olympus" android:scheme="flk_push" />

                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.feelingk.olympus.pushagent.lib.REQUEST_APP_STATE" />
                <action android:name="com.feelingk.olympus.pushagent.lib.REQUEST_READY_FOR_AGENT" />
            </intent-filter>
        </receiver>
        
        <receiver
            android:name="com.feelingk.pushagent.service.PushAgentServiceManager"
            android:enabled="true" >
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.PACKAGE_REMOVED" />
                <action android:name="android.intent.action.PACKAGE_ADDED" />
                <data android:scheme="package" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.feelingk.olympus.pushagent.lib.APP_REGISTRATION" />
                <action android:name="com.feelingk.olympus.pushagent.lib.SERVICE_START" />
				<action android:name="com.feelingk.olympus.pushagent.lib.SERVICE_SHARED_INFO"/>
                <data android:host="olympus" android:scheme="flk_push" />

                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.feelingk.olympus.pushagent.lib.RESPONSE_READ_MSG" />
                <action android:name="com.feelingk.olympus.pushagent.lib.AGENT_PAUSE" />
                <action android:name="com.feelingk.olympus.pushagent.lib.SERVICE_START" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </receiver>

        <receiver android:name="com.feelingk.pushagent.service.NetworkStateMonitor" >
            <intent-filter>
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
            </intent-filter>
        </receiver>

         <receiver
            android:name="com.feelingk.pushagent.service.PushAgentAlarmManager"
            android:enabled="true" >
            <intent-filter>
                <action android:name="com.feelingk.olympus.pushagent.lib.action.checkprocess" />
                <action android:name="com.feelingk.olympus.pushagent.lib.action.delayprocess" />
                <action android:name="com.feelingk.olympus.pushagent.lib.action.force" />
                <action android:name="com.feelingk.olympus.pushagent.lib.action.socket" />
            </intent-filter>
        </receiver>
        <receiver
            android:name="com.feelingk.pushagent.service.PushAgentServiceAlarmManager"
            android:enabled="true" >
            <intent-filter>
                <action android:name="com.feelingk.olympus.pushagent.lib.service.postdelayed" />
                <action android:name="com.feelingk.olympus.pushagent.lib.service.sleepdelayed"/>
            </intent-filter>
        </receiver>

        <service
            android:name="com.feelingk.pushagent.service.PushAgentService"
            android:enabled="true" >
        </service>

        <service android:name="com.feelingk.pushagent.service.RIDInfoValidCheckService"/>
        
        <receiver android:name="com.feelingk.pushagent.service.LBSServiceAlarmManager" >
            <intent-filter>
                <action android:name="com.feelingk.olympus.pushagent.lib.action.lbsinfo.lm" />
                <action android:name="com.feelingk.olympus.pushagent.lib.action.lbsinfo.lmInit" />
                <action android:name="com.feelingk.olympus.pushagent.lib.action.lbsinfo.wf" />
                <action android:name="com.feelingk.olympus.pushagent.lib.action.lbsinfo.bl" />
                <action android:name="com.feelingk.olympus.pushagent.lib.action.lbsinfo.cl" />
                <action android:name="com.feelingk.olympus.pushagent.lib.action.lbsinfo.al" />
            </intent-filter>
        </receiver>

        <service android:name="com.feelingk.pushagent.service.RIDInfoValidCheckService" />

        <activity
            android:name="com.feelingk.pushagent.popup.WakeUpActivity"
            android:clearTaskOnLaunch="true"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:excludeFromRecents="true"
            android:launchMode="singleTask"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:windowSoftInputMode="stateAlwaysHidden" >
        </activity>
        <activity
            android:name="com.flk.olympus.activity.PushPopupActivity"
            android:clearTaskOnLaunch="true"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:excludeFromRecents="true"
            android:launchMode="singleTop"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:windowSoftInputMode="stateAlwaysHidden" >
        </activity>
        <activity
            android:name="com.flk.olympus.activity.LocationAgreeActivity"
            android:clearTaskOnLaunch="true"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:excludeFromRecents="true"
            android:launchMode="singleTop"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:windowSoftInputMode="stateAlwaysHidden" >
        </activity>
        <activity
            android:name="com.flk.olympus.activity.CouponReceiveActivity"
            android:clearTaskOnLaunch="true"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:excludeFromRecents="true"
            android:launchMode="singleTop"
            android:theme="@android:style/Theme.Holo.NoActionBar"
            android:windowSoftInputMode="stateAlwaysHidden" >
        </activity>
        <activity
            android:name="com.flk.olympus.activity.CouponConfigActivity"
            android:clearTaskOnLaunch="true"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:excludeFromRecents="true"
            android:launchMode="singleTop"
            android:theme="@android:style/Theme.Holo.NoActionBar"
            android:windowSoftInputMode="stateAlwaysHidden" >
        </activity>
        <activity
            android:name="com.flk.olympus.activity.CouponDetailActivity"
            android:clearTaskOnLaunch="true"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:excludeFromRecents="true"
            android:launchMode="singleTop"
            android:theme="@android:style/Theme.Holo.NoActionBar"
            android:windowSoftInputMode="stateAlwaysHidden" >
        </activity>
        
        <provider android:name="com.feelingk.pushagent.db.DataContentProvider" android:authorities="[packageName]" android:exported="true"/>
	```
    

4. 'FLKPushAgentLIB'를 사용할 Activity에 아래와 같이 구현한다.
	```java
	public class ExampleActivity extends AppCompatActivity implements FLKPushInterface.OnPushLibResultListener{

	    private FLKPushInterface flkInterface;

    	@Override
	    protected void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_example);

			/** 
	         * FLKPushAgentLIB 인터페이스 생성 
        	 * this : Context
    	     * this : FLKPushInterface.OnPushLibResultListener OLYMPUSH 앱 연동 결과를 리턴해준다.
	         */
        	flkInterface = new FLKPushInterface(this, this);
            // service에 사용되는 사용자 고유 키 값이 있다면 아래의 userKey에 세팅하여 사용 가능.
			//flkInterface = new FLKPushInterface(this, "userKey", this);
            flkInterface.interfaceInit();

    	    Button receiveMessagebox = (Button)findViewById(R.id.btn_received_messagebox);
	        receiveMessagebox.setOnClickListener(new View.OnClickListener() {
        	    @Override
    	        public void onClick(View view) {
                	// Agent 내의 수신함 연동
	                if (flkInterface != null) {
                	    flkInterface.sendToReceiveActivity();
            	    }
        	    }
    	    });

	    }

    	@Override
	    public void onResult(int resultCode) {
    	  // 응답코드에 따른 결과 처리 
	    }
	}
	```

5. 응답 코드

	| Code  |  Message                       | 설명                                                 |
	|:-----:|--------------------------------|-----------------------------------------------------|
	| 700   | User Terms Agree Fail          | 사용자가 OLYMPUS Agent 사용 약관 미동의로 인해 서비스 이용불가  |
	| 993   | Not Ready State                | Agent 서비스 동작 준비중 상태                             |
	| 995   | Parameter Error                | RID Parameter 값 오류           					  |
    | 996   | Fail Connection                | RID 발급 요청에 대한 서버응답 Null로 인한 RID 발급 실패 오류	  |
	| 998   | Push Service License Fail      | Agent Service License Fail  						   |
	| 999   | Service License Fail           | 인증되지 않은 AppID나 Package 사용 오류  				  |
	| 1000  | Linked Success                 | Agent 연동 성공   							 		 |
	| 1001  | User App Install Fail          | 사용자가 OLYMPUS Agent 설치를 거부하여 서비스 이용 불가 	   |



<br>
### Class Reference
<hr/>

|FLKPushInterface |설명  |
|-----------------|------|
| FLKPushInterface|FLKPushAgentLIB 사용을 위한 Interface 생성자<br>Parameters<br> * Context : Activity or Application Context <br> * UserKey (Option): App Service에 사용되는 사용자 고유 키 값<br>* OnPushLibResultListener : FLKPushAgentLIB와의 연동 결과를 전달 받기 위한 리스너 |
|interfaceInit() | Agent와 연동. Agent 연동 결과는 OnPushLibResultListener onResult로 전달 받는다. (`Screenshot Flow` 항목 참고) |
|sendToReceiveActivity()|Agent 내 수신함 연동. <br>interfaceInit() 함수를 통해 onResult에 '1000' 코드 받았을 시에만 정상적으로 화면 연동 가능하다.<br> '1000' 외의 코드 전달 받았을 시 interfaceInit()과 동작 Flow는 동일하다.|


<br>
### Screenshot Flow
<hr/>

* FLKPushAgentLIB 동작 순서

|Sreenshot |Step  |
|-----------------|------|
|<img src="https://cloud.githubusercontent.com/assets/22470636/18987235/b97fe072-873b-11e6-946b-e1fbd37deb49.png" width="320" height="400" alt="step3" /> | * Step1. 최초 1회 interfaceInit() 함수 호출 시  '위치 및 마케팅 이용동의' 팝업 노출<br> - '아니오' 버튼 선택 시, 최초 1회는 '700' 코드 전달 되며, 이후 interfaceInit() 함수 호출 시 '1001' 코드로 전달<br>- '예' 버튼 선택 시, Agent 와 연동 결과 코드 전달. 정상 연동시 '1000' 코드 전달|
|<img src="https://cloud.githubusercontent.com/assets/22470636/18988068/e756a086-873f-11e6-95ac-b841f07f6781.jpg" width="320" height="400" alt="step3" />|* Step2. sendToReceiveActivity() 함수 호출 시 Agent 내의 Message Box 연동<br> - Agent와 정상적으로 연동되어 '1000'코드를 받은 후 Step4 진행 시 Message Box 화면으로 연동 된다.<br> - '1000' 외의 코드를 전달 받은 후 Step4 진행 시 Step1 부터 진행된다.|


<br>
### Release Note
<hr/>

- [v1.0](https://github.com/pushfeelingk/OLYMPUS_SDK/files/624451/FLK_Olympus_SDK_v1.0.zip)
	



<br>
### License 
<hr/>
OLYMPUS Service에 관련하여 문의사항은 아래로 문의 주시면 빠르게 응대해드리도록 하겠습니다.

* <B> Call </B> : 02 - 2102 - 7300
* <B> Email </B> : olympus-push@feelingk.com

<br><br>
Copyright 2016. Feelingk All Right Reserved.

