package com.flk.olympus.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

import com.flk.olympus.R;

/**
 * custom loading progress
 */
public class FullProgressDialog extends Dialog {
	/** 프로그래스 사용여부 (default true) */
	private static boolean isUseProgress = true;
    
    private static FullProgressDialog mFullProgressDialog = null;

	public static FullProgressDialog show(Context context) {
		isUseProgress = true;
		return show(context, "", "", false, false, null); 
	}
	
	public static FullProgressDialog show(Context context, boolean useProgress) {
		isUseProgress = useProgress;
		return show(context, "", "", false, false, null); 
	}
	
	public static FullProgressDialog show(Context context, boolean canceled, OnCancelListener cancelListener) { 
		return show(context, "", "", false, canceled, cancelListener); 
	} 

	public static FullProgressDialog show(Context context, CharSequence title, 
			CharSequence message, boolean indeterminate, boolean cancelable) { 
	   
		return show(context, title, message, indeterminate, cancelable, null); 
	} 

	/**
	 * loading progress show
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param indeterminate
	 * @param cancelable
	 * @param cancelListener
	 * @return
	 */
	public static FullProgressDialog show(Context context, CharSequence title, 
			CharSequence message, boolean indeterminate, 
			boolean cancelable, OnCancelListener cancelListener) { 
	    
		FullProgressDialog dialog = new FullProgressDialog(context);
		
		if(isUseProgress){
			dialog.setTitle(title); 
			dialog.setCancelable(cancelable); 
			dialog.setContentView(R.layout.flk_olympus_view_full_progress);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog.setOnCancelListener(cancelListener);
		}else{
			dialog.setTitle(title); 
			dialog.setCancelable(cancelable); 
			dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog.setOnCancelListener(cancelListener);
		}
		
		dialog.show(); 

		return dialog; 
	} 

	public FullProgressDialog(Context context) { 
		super(context, R.style.FLKOlympusTransparentDialog); 
	} 
	
	public void setUseProgress(boolean isProgress){
		isUseProgress = isProgress;
	}
	
	
   /**
     * Close progress dialog
     */
    public void dismissProgressDialog() {

        if (mFullProgressDialog != null) {
            mFullProgressDialog.dismiss();
        }
        mFullProgressDialog = null;
    }
}