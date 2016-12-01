package com.flk.olympus.util;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.flk.olympus.R;

public class NotificationUtil {
	

    /**
     * 기본 타입 Noti
     * @param context
     * @param title
     * @param message
     * @param notiId
     * @param notificationIntent
     */
    public static void generateNotification(Context context, String notiId, String message, Intent notiIntent) {
        
    	try {
	    	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    	PendingIntent intent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notiIntent, 0);
    	
	    	PackageManager pm = context.getPackageManager();
	        ApplicationInfo app = pm.getApplicationInfo(context.getPackageName(), 0);
	        Resources res = pm.getResourcesForApplication(app);
	        
	    	Notification.Builder mBuilder = new Notification.Builder(context);
	    	mBuilder.setSmallIcon(R.drawable.flk_olympus_small_icon);
	    	mBuilder.setLargeIcon(BitmapFactory.decodeResource(res, app.icon));
	    	mBuilder.setWhen(System.currentTimeMillis());
	    	mBuilder.setContentTitle(app.loadLabel(pm).toString());
			mBuilder.setContentText(message);
	    	mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
	    	mBuilder.setAutoCancel(true);
	    	mBuilder.setContentIntent(intent);
	    	
	    	
	        notificationManager.notify(notiId.hashCode(), mBuilder.build());
        
    	} catch (NameNotFoundException e) {
		} catch (Exception e){
		}
    }
    
    /**
     * 이미지 확장형 Noti
     * @param context
     * @param title
     * @param message
     * @param notiId
     * @param notificationIntent
     */
    public static void generateImageNotification(Context context, String notiId, String message, String imagePath, Intent notiIntent) {
    	try {
    		
    		NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
    		PendingIntent intent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    		PackageManager pm = context.getPackageManager();
	        ApplicationInfo app = pm.getApplicationInfo(context.getPackageName(), 0);
	        Resources res = pm.getResourcesForApplication(app);
    		
			URL url = new URL(imagePath);
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
			Bitmap bm = BitmapFactory.decodeStream(bis);
			bis.close();
			notiStyle.bigPicture(bm);
			
			
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
			
	    	mBuilder.setSmallIcon(R.drawable.flk_olympus_small_icon);//required
	    	mBuilder.setLargeIcon(BitmapFactory.decodeResource(res, app.icon));
	    	mBuilder.setContentTitle(app.loadLabel(pm).toString());//required 
	    	mBuilder.setContentText(message);//required 
	    	mBuilder.setAutoCancel(true);
	    	mBuilder.setStyle(notiStyle);
	    	mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
	    	mBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
	    	mBuilder.setContentIntent(intent); 
	    	
	    	
	    	NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
	    	mNotificationManager.notify(notiId.hashCode(), mBuilder.build());
			
		} catch (Exception e) {
			// 이미지 로드 실패시 일반 푸시로 띄움 
			generateNotification(context, notiId, message, notiIntent);
		}
    	
    }
    
}
