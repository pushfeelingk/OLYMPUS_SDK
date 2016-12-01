package com.flk.olympus;


import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.feelingk.pushagent.util.DebugLog;
import com.feelingk.pushagent.util.IntentUtil;
import com.feelingk.pushagent.util.SharedPreference;
import com.feelingk.pushagent.util.StringUtil;
import com.feelingk.pushagent.util.WakeLockUtil;
import com.flk.olympus.activity.LocationAgreeActivity;
import com.flk.olympus.activity.PushPopupActivity;
import com.flk.olympus.defines.FLKPushInterfaceDefines;
import com.flk.olympus.util.NotificationUtil;

public class FLKPushAgentReceiver extends BroadcastReceiver {	
	private static Handler mHandler;

	public static void setEventHandler(Handler handler){
		mHandler = handler;
	}
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		
		DebugLog.d("$$$ LIB === FLKPushAgentReceiver action : " + intent.getAction());
		
		if(intent.getAction().equals(FLKPushAgentSender.OlympusConfig + ".RECEIVED_APP_REG_ID")){
			
			Uri revUri = Uri.parse(intent.getDataString());
			final String regID = revUri.getQueryParameter("regid");
			
			SharedPreference.putSharedPreference(context, FLKPushInterfaceDefines.REGID, regID);
			SharedPreference.putSharedPreference(context, FLKPushInterfaceDefines.INIT_CONFIG, 2);
			SharedPreference.putSharedPreference(context, FLKPushInterfaceDefines.LOCATION_AGREE, 1); // RID를 발급 받았다는 것은 위치정보 수집동의를 받은 것임.
			if(mHandler != null){
				mHandler.sendEmptyMessage(1); // 3rdApp에 결과 전달 
			}
			
		}
		
		
		if(intent.getAction().equals(FLKPushAgentSender.OlympusConfig + ".REQUEST_READY_FOR_AGENT")){
			// 약관 팝업 노출 
			Intent agreePopup = new Intent(context, LocationAgreeActivity.class);
			agreePopup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(agreePopup);
		}
		
		// 에러 
		if(intent.getAction().equals(FLKPushAgentSender.OlympusConfig  + ".RECEIVED_REG_PARAM_ERROR") || intent.getAction().equals(FLKPushAgentSender.OlympusConfig  + ".RECEIVED_REG_RESULT_ERROR")){
			Uri revUri = Uri.parse(intent.getDataString());
			String errorCode = revUri.getQueryParameter("errorcode");
			
			int intValue = 0;
			try {
				intValue = Integer.parseInt(errorCode);
			} catch (Exception e) {
			}
			
			
			// 위치정보 수집 동의를 거부한 경우 다지 RID 요청하지 않도록 플래그 변경 
			if(intValue == 700){
				SharedPreference.putSharedPreference(context, FLKPushInterfaceDefines.INIT_CONFIG, 1);
			}
			
			// Push 수신 동의 여부 변경 
			if(intValue == 900){
				SharedPreference.putSharedPreference(context, FLKPushInterfaceDefines.LOCATION_AGREE, 1);
			}else if(intValue == 901){
				SharedPreference.putSharedPreference(context, FLKPushInterfaceDefines.LOCATION_AGREE, 0);
			}
			
			if(mHandler != null){
				Message msg = mHandler.obtainMessage(2);
				msg.arg1 = intValue;
				mHandler.sendMessage(msg); // 3rdApp에 결과 전달 
			}
		}
		
		
		if(intent.getAction().equals(FLKPushAgentSender.OlympusConfig + ".RECEIVED_APP_MSG_INFO")) {
			
			boolean isMsgReceivedAgree = StringUtil.intToBoolean(SharedPreference.getIntSharedPreference(context, FLKPushInterfaceDefines.LOCATION_AGREE));
			if(isMsgReceivedAgree){
				String notiId = intent.getStringExtra("notiId");
				int notiType = intent.getIntExtra("notiType", 0);
				String notiMsg = intent.getStringExtra("notiMsg");
				String notiActLink = intent.getStringExtra("notiActLink");
				String notiImg = intent.getStringExtra("notiImg");
				
				if(notiType != 0 && notiType <= 3){
					
					if(notiType != 3){
						NotificationUtil.generateNotification(context, notiId, notiMsg, IntentUtil.actionNotiIntent(context, notiActLink));
					}
					
					
					if (notiType != 1){
						/**
						 * 화면이 켜져 있는 상태에서는 다른 앱을 하고 있을 가능성이 있기 때문에
						 * 화면이 꺼져있을 경우에만 팝업타입의 메시지를 보여준다.
						 */
						boolean isScreenOn = WakeLockUtil.isScreenOn(context);
						DebugLog.d("Msg Popup isScreenOn : " + isScreenOn);
						if(!isScreenOn){
							Intent popupIntent = new Intent(context, PushPopupActivity.class);
							popupIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
							popupIntent.putExtra("popupImg", notiImg);
							popupIntent.putExtra("popupLink", notiActLink);
			
							PendingIntent pi = PendingIntent.getActivity(context, StringUtil.stringToInt(notiId), popupIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
							try {
								pi.send();
							} catch (CanceledException e) {
								DebugLog.d("[PushAgentSeviceSender] MsgPopup Exception : " + Log.getStackTraceString(e));
								// notiType 2일 경우 팝업+노티 타입인데 팝업이 노티로 뜨게되면 중복 노티 처리되어 제외 
								if (notiType != 2){
									NotificationUtil.generateNotification(context, notiId, notiMsg, IntentUtil.actionNotiIntent(context, notiActLink));
								}
							}
						} else {
							// notiType 2일 경우 팝업+노티 타입인데 팝업이 노티로 뜨게되면 중복 노티 처리되어 제외 
							if (notiType != 2){
								NotificationUtil.generateNotification(context, notiId, notiMsg, IntentUtil.actionNotiIntent(context, notiActLink));
							}
						}
					}
					
				}
			}
		}

		
		
	}
}

