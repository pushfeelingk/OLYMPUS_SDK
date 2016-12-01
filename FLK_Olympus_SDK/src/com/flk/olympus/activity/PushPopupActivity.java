package com.flk.olympus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.feelingk.pushagent.util.DebugLog;
import com.feelingk.pushagent.util.IntentUtil;
import com.flk.olympus.R;
import com.flk.olympus.util.ImageUtil;

public class PushPopupActivity extends BaseActivity {
	private ImageView mImageView;
	private TextView mPopupContentTv;
	private Button mCloseBtn;
	
	private String mPopupImg;
	private String mPopupLink;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.flk_olympus_popup_layout);
		
		DebugLog.e("*************** PushPopup onCreate ************");
		
		mImageView = (ImageView)findViewById(R.id.iv_notiImg);
		mImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// http:// 또는 https:// 만 들어오는 경우를 위한 필터링 
				if(mPopupLink.length() > 8){
					Intent actionIntent = IntentUtil.actionWebBrowserIntent(mPopupLink);
					startActivity(actionIntent);
				}				
			}
		});
		mPopupContentTv = (TextView)findViewById(R.id.tv_content);
		mCloseBtn = (Button)findViewById(R.id.btn_close);
		mCloseBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
        initIntentData(getIntent());
        setPopupData();
	}

	
	private void initIntentData(Intent intent){
		
		mPopupImg = "";
		mPopupLink = "";
		
		if (intent.hasExtra("popupImg")) {
			mPopupImg = intent.getStringExtra("popupImg");
		}

		if (intent.hasExtra("popupLink")) {
			mPopupLink = intent.getStringExtra("popupLink");
		}

	}
	
	private void setPopupData(){
		mImageView.setVisibility(View.VISIBLE);
		mPopupContentTv.setVisibility(View.GONE);
		
		ImageUtil.initNoCacheRoundImageLoader(PushPopupActivity.this, 0, R.drawable.flk_olympus_img_error_page);
		ImageUtil.imageLoader.displayImage(mPopupImg, mImageView, ImageUtil.options);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		initIntentData(intent);
		setPopupData();
		
	}
	
}
