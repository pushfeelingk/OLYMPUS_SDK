package com.flk.olympus.util;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageUtil {

	public static ImageLoader imageLoader = ImageLoader.getInstance();
	public static DisplayImageOptions options;

	public static void initFileImageLoader(Context context, int radius) {

		 imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		 options = new DisplayImageOptions.Builder()
		 .cacheInMemory(true)
		 .cacheOnDisc(true)
		 .displayer(new RoundedBitmapDisplayer(radius))
		 .build();
	 }


	 /**
	  * UIL Library DisplayImageOptions 초기화
	  * 
	  * @param radius (RoundSize) 
	  * @param context
	  */
	 public static void initNoCacheRoundImageLoader(Context context, int radius, int failRes) {

		 imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		 options = new DisplayImageOptions.Builder()
		 .showImageOnFail(failRes)
		 .showImageForEmptyUri(failRes)
		 .cacheInMemory(false)
		 .cacheOnDisc(false)
		 .displayer(new RoundedBitmapDisplayer(radius))
		 .build();
		 
	 }


	 
	/**
	 * ImageLoader Cache Delete (캐시 삭제) 
	 * 
	 * @param context
	 */
	public static void clearChache(Context context) {
		
    	try {
    		ImageLoader imageLoader = ImageLoader.getInstance();
    		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    		imageLoader.clearDiscCache();
    		imageLoader.clearMemoryCache();
    	} catch (Exception e) {
    	}
	}
	
}
