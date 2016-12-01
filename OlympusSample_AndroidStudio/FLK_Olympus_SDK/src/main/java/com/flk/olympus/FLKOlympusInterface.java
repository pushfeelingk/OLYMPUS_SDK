package com.flk.olympus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.feelingk.pushagent.network.json.JsonManager;
import com.feelingk.pushagent.service.constant.ServiceConstant;
import com.feelingk.pushagent.util.SharedPreference;
import com.feelingk.pushagent.util.StringUtil;
import com.flk.olympus.activity.CouponReceiveActivity;
import com.flk.olympus.activity.LocationAgreeActivity;
import com.flk.olympus.defines.FLKPushInterfaceDefines;

public class FLKOlympusInterface {
	private Context mContext;
	private OnPushLibResultListener mOnPushLibResultListener;
	
	public interface OnPushLibResultListener{
		void onResult(int resultCode);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: // RID 발급 완료
				sendResultCode(1000);
				break;
				
			case 2: // RID 발급 중 에러 
				sendResultCode(msg.arg1);
				break;
			}
		}
	};
	
	public FLKOlympusInterface(Context context, OnPushLibResultListener listener){
		mContext = context;
		mOnPushLibResultListener = listener;
		configInit();
	}
	
	
	public FLKOlympusInterface(Context context, String userKey, OnPushLibResultListener listener){
		mContext = context;
		mOnPushLibResultListener = listener;
		
		SharedPreference.putSharedPreference(mContext, FLKPushInterfaceDefines.USER_KEY, userKey);
		
		configInit();
	}
	
	private String getAppId(){
		
		String appId = SharedPreference.getSharedPreference(mContext, FLKPushInterfaceDefines.APPID);
		if (StringUtil.isEmpty(appId)){
			try {
				ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
				Bundle bundle = appInfo.metaData;
				
				if(bundle != null){
					String saveAppID = bundle.getString("flk.olympus.AppID"); 
					SharedPreference.putSharedPreference(mContext, FLKPushInterfaceDefines.APPID, saveAppID);
					return saveAppID;
				}
			} catch (NameNotFoundException e) {
			}
			return "";
		} else {
			return appId;	
		}
	}
	
	private void configInit(){
		
		if(StringUtil.isEmpty(getAppId())){
			sendResultCode(JsonManager.RESULT_ERROR_CODE_999);
			return;
		}
		
		SharedPreference.putSharedPreference(mContext, ServiceConstant.LAST_VIEWING_TIME, StringUtil.currentDateFormat("yyyyMMdd"));
		// 리시버 핸들러 등록 
		FLKPushAgentReceiver.setEventHandler(mHandler);
	}
	
	
	public void interfaceInit() {
		FLKPushAgentSender.sendToStart(mContext);
		
		int mInitFlag = SharedPreference.getIntSharedPreference(mContext, FLKPushInterfaceDefines.INIT_CONFIG);
		if(mInitFlag == 1) {
			// 1은 설치 유도 팝업에서 사용자가 거부 한 경우
			sendResultCode(1001);
		} else { 
			if(!StringUtil.isEmpty(SharedPreference.getSharedPreference(mContext, FLKPushInterfaceDefines.REGID))){
				sendResultCode(1000);
			}
		}
		
		
	}
	
	public void sendToReceiveActivity(){
		
		// TODO RID 체크 Null 시 에러코드 리턴 
		String regId = SharedPreference.getSharedPreference(mContext, FLKPushInterfaceDefines.REGID);
		String appId = SharedPreference.getSharedPreference(mContext, FLKPushInterfaceDefines.APPID);
		
		if(StringUtil.isEmpty(regId)){
			// 약관 팝업 노출 
			Intent agreePopup = new Intent(mContext, LocationAgreeActivity.class);
			agreePopup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(agreePopup);
			return;
		}
		
		Intent couponActivity = new Intent(mContext, CouponReceiveActivity.class);
		couponActivity.putExtra("appRegId", regId);
		couponActivity.putExtra("appId", appId);
		
		couponActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(couponActivity);
	}
	
	/**
	 * 3rd App에 결과 코드 리턴 
	 * @param resultCode
	 */
	private void sendResultCode(int resultCode){
		// 핸들러 초기화
		FLKPushAgentReceiver.setEventHandler(null);
		
		if(mOnPushLibResultListener != null){
    		mOnPushLibResultListener.onResult(resultCode);
    	}
	}
	
}
