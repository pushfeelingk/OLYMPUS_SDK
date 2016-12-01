package com.flk.olympus.view.popup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.flk.olympus.R;

/**
 * 공통 Dialog
 * */
public class LocationAgreePopup extends Dialog {

	private Context mContext;

	private LinearLayout mAgreeButton;
	private CheckBox mAgreeCheckBox;
	private Button mLeftButton;
	private Button mRightButton;
	private WebView mTermsContentWebView;
	private View.OnClickListener mLeftClickListener;
	private View.OnClickListener mRightClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.5f;
		getWindow().setAttributes(lpWindow);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.flk_olympus_popup_location_agree);

		initView();

		if(mLeftClickListener != null){
			mLeftButton.setOnClickListener(mLeftClickListener);
		}
		
		if(mRightClickListener != null){
			mRightButton.setOnClickListener(mRightClickListener);
		}

		mTermsContentWebView.loadUrl("file:///android_asset/flk_olympus_push_terms.html");
		
		mAgreeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAgreeCheckBox.setChecked(!mAgreeCheckBox.isChecked());
			}
		});
		
	}

	private void initView() {
		mAgreeButton = (LinearLayout)findViewById(R.id.btn_agree_check);
		mAgreeCheckBox = (CheckBox)findViewById(R.id.check_agree);
		mTermsContentWebView = (WebView)findViewById(R.id.terms_view);
		mLeftButton = (Button) findViewById(R.id.btn_left);
		mRightButton = (Button) findViewById(R.id.btn_right);
	}

	public LocationAgreePopup(Context context) {
		super(context, android.R.style.Theme_Translucent);
	}

	/**
	 * Title, Contents, Left/Right Button 으로 이루어진 CommonPopup 생성자
	 * 
	 * @param context
	 * @param title
	 * @param content
	 * @param leftListener
	 * @param rightListener
	 * @param type
	 */
	public LocationAgreePopup(Context context, View.OnClickListener leftListener, View.OnClickListener rightListener) {

		super(context, android.R.style.Theme_Translucent);
		this.mContext = context;
		this.mLeftClickListener = leftListener;
		this.mRightClickListener = rightListener;
	}

	
	public boolean isAgreeCheck(){
		return mAgreeCheckBox.isChecked();
	}
	
	public void setCancel() {
		setCancelable(false);
	}
}
