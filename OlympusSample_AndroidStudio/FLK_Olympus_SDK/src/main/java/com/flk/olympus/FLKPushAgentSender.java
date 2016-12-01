package com.flk.olympus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.feelingk.pushagent.util.SharedPreference;
import com.feelingk.pushagent.util.StringUtil;
import com.flk.olympus.defines.FLKPushInterfaceDefines;

public class FLKPushAgentSender {
	public static final String mHostName = "olympus";
	
	private static final String encoding = "UTF-8";
	public static final String OlympusConfig = "com.feelingk.olympus.pushagent.lib";
	private static final String PushAgentHName = "flk_push://olympus";

	public static void sendToStart(Context context) {
		Intent startIntent = new Intent(OlympusConfig + ".SERVICE_START");
		startIntent.setPackage(context.getPackageName());
		startIntent.setFlags(32);
		context.sendBroadcast(startIntent);
	}
	
	public static void sendToStartWakeup(Context context, boolean isWakeup) {
		Intent startIntent = new Intent(OlympusConfig + ".SERVICE_START");
		startIntent.setPackage(context.getPackageName());
		startIntent.setFlags(32);
		
		startIntent.putExtra("wakeUp", isWakeup);
		
		context.sendBroadcast(startIntent);
	}
	
	
	public static void sendToRegistration(Context context, String pName, String appName) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder(PushAgentHName);
		sb.append("?pname=").append(URLEncoder.encode(pName, encoding));
		sb.append("&hname=").append(URLEncoder.encode(mHostName, encoding));
		sb.append("&appname=").append(URLEncoder.encode(appName, encoding));
		
		String userKey = SharedPreference.getSharedPreference(context, FLKPushInterfaceDefines.USER_KEY);
		if(!StringUtil.isEmpty(userKey)){
			sb.append("&userkey=").append(URLEncoder.encode(userKey, encoding));
		}
		
		Intent intent = new Intent(OlympusConfig + ".APP_REGISTRATION", Uri.parse(sb.toString()));
		intent.setPackage(context.getPackageName());
		intent.setFlags(32);
		context.sendBroadcast(intent);
	}
	

	public static void sendToRegistration(Context context) {
		String regID = SharedPreference.getSharedPreference(context, FLKPushInterfaceDefines.REGID);

		if(StringUtil.isEmpty(regID)){
			try {
				FLKPushAgentSender.sendToRegistration(context, context.getPackageName(), SharedPreference.getSharedPreference(context, FLKPushInterfaceDefines.APPID));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
}
