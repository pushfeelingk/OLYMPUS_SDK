package com.flk.olympus.activity;

import com.flk.olympus.view.FullProgressDialog;

import android.app.Activity;
import android.widget.Toast;

public class BaseActivity extends Activity{
	private Toast mToast = null;
	private FullProgressDialog mFullProgressDialog = null; // 전체 프로그래스 로딩
	
	/**
	 *	Toast 
	 * @param toastText
	 */
	public void showToast(String toastText){
		if (mToast == null){
			mToast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(toastText);
		}
		mToast.show();
	}
	
	/**
	 * Full Progress Dialog Show
	 * */
	public void showFullProgressDialog(){

		if(mFullProgressDialog == null || !mFullProgressDialog.isShowing()){
			mFullProgressDialog = FullProgressDialog.show(this);
		}
	}

	/**
	 * Full Progress Dialog Dismiss
	 * */
	public void dismissFullProgressDialog(){

		if(mFullProgressDialog != null && mFullProgressDialog.isShowing()){
			mFullProgressDialog.dismiss();
		}
	}
}
