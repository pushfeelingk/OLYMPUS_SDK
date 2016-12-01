package com.flk.olympus.activity;

import java.io.UnsupportedEncodingException;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.feelingk.pushagent.network.constant.NetworkConstant;
import com.feelingk.pushagent.network.http.OnResultListener;
import com.feelingk.pushagent.network.http.task.AppRegIdSelectRequestTask;
import com.feelingk.pushagent.network.json.JsonManager;
import com.feelingk.pushagent.service.PushAgentSeviceSender;
import com.feelingk.pushagent.util.DebugLog;
import com.feelingk.pushagent.util.SharedPreference;
import com.feelingk.pushagent.util.StringUtil;
import com.feelingk.pushagent.util.UIUtil;
import com.flk.olympus.FLKPushAgentSender;
import com.flk.olympus.R;
import com.flk.olympus.defines.FLKPushInterfaceDefines;
import com.flk.olympus.view.popup.LocationAgreePopup;

/**
 * 위치정보 수집 동의  
 */
public class LocationAgreeActivity extends BaseActivity {//implements RIDResutlListener {
	
	private LocationAgreePopup agreeDialog; // 위치 수집 동의 
	private AlertDialog mLocationPermissionGuide; // 위치 서비스 On
	
	/** 위치 퍼미션 */
	private String[] PermissionArr = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(agreeDialog != null){
			agreeDialog.dismiss();
		}
		
		if(mLocationPermissionGuide != null){
			mLocationPermissionGuide.dismiss();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		DebugLog.d("@@@ PushAgent LocationAgreeActivity onCreate");
		String userKey = SharedPreference.getSharedPreference(LocationAgreeActivity.this, FLKPushInterfaceDefines.USER_KEY);
		if(StringUtil.isEmpty(userKey)){
			showAgreePopup();
		} else {
			requestAppRegSelect(1, userKey);
		}
		
	}
	
	private void showAgreePopup(){
		if(!this.isFinishing()){
			if (agreeDialog == null || !agreeDialog.isShowing()){
				agreeDialog = new LocationAgreePopup(LocationAgreeActivity.this
						, new OnClickListener() {
							@Override
							public void onClick(View v) {
								try {
									agreeDialog.dismiss();
									PushAgentSeviceSender.SendRegResultErrorTo3rdPartyApp(LocationAgreeActivity.this, getPackageName(), JsonManager.RESULT_CODE_700);
									finish();
								} catch (UnsupportedEncodingException e) {
								}	
							}
						}, new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if(agreeDialog.isAgreeCheck()){
									agreeDialog.dismiss();
									FLKPushAgentSender.sendToRegistration(getApplicationContext());
									requestRuntimePermission();
									
								} else { 
									showToast(getString(R.string.flk_olympus_toast_location_agree_check));
								}
							}
						});
				// 팝업 떠있을때 백키 이벤트 취소버튼 이벤트와 동일하게 처리 
				agreeDialog.setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
				        	try {
								agreeDialog.dismiss();
								PushAgentSeviceSender.SendRegResultErrorTo3rdPartyApp(LocationAgreeActivity.this, getPackageName(), JsonManager.RESULT_CODE_700);
								finish();
							} catch (UnsupportedEncodingException e) {
							}	
							return true;
						}							
						
						return false;
					}
				});
				agreeDialog.show();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		switch (requestCode) {
		case 0000:
				onActivityResult();
			break;

		default:
			break;
		}
		
	}
	
	
	private void requestRuntimePermission(){
		if (Build.VERSION.SDK_INT >= 23){
			if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
				requestPermissions(PermissionArr, 0000);
			} else{
				onActivityResult();	
			}
		} else {
			onActivityResult();
		}
		
	}
	
	/**
	 * RID 등록 및 퍼미션 처리 완료 되면 3rd App에 RID전달 
	 */
	private void onActivityResult(){
		// 위치 서비스 꺼져있으면 켜도록 유도하는 팝업 -> 확인시 설정 > 위치정보로 이동 
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if(!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
		
			if(mLocationPermissionGuide == null || !mLocationPermissionGuide.isShowing()){
				mLocationPermissionGuide = UIUtil.createDialog(LocationAgreeActivity.this, getString(R.string.flk_olympus_noti_msg_location_permission), 
						getString(R.string.flk_olympus_confirm), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
								mLocationPermissionGuide.dismiss();
								finish();
							}
						}, 
						getString(R.string.flk_olympus_cancel), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mLocationPermissionGuide.dismiss();
								finish();
							}
						});
				mLocationPermissionGuide.show();
			}
		}else{
			finish();
		}
			
	}
	
	private void requestAppRegSelect(int tryCount, String userKey){
		
		try {
			AppRegIdSelectRequestTask task = new AppRegIdSelectRequestTask(getApplicationContext(), new OnResultListener() {
				
				@Override
				public void onResult(Object resultObject, int resultCode, String resultMsg, int tryCount) {
					if((NetworkConstant.CODE_SUCCESS == resultCode)){
						// 기존 발급 받은 이력이 있다면, 팝업 띄우지 않고 RID 발급요청만 진행.
						if(StringUtil.isEmpty(resultMsg)){
							showAgreePopup();
						} else {
							DebugLog.d("기존 RID 발급 이력이 있어, 약관 동의 팝업 노출 없이 RID 발급 요청 !!!");
							FLKPushAgentSender.sendToRegistration(getApplicationContext());
							requestRuntimePermission();
						}
					} else { 
						showAgreePopup();
					}
				}
			});
			task.execute(tryCount, userKey);
		} catch (Exception e) {
			DebugLog.e("[AppRegIdSelectRequestTask] Error : " + e);
		} 
		
	}
}
