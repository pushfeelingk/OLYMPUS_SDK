package com.flk.olympus.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.feelingk.pushagent.db.LocalDBManager;
import com.feelingk.pushagent.dto.MsgItem;
import com.feelingk.pushagent.network.constant.NetworkConstant;
import com.feelingk.pushagent.network.http.OnResultListener;
import com.feelingk.pushagent.network.http.task.MsgDeleteRequestTask;
import com.feelingk.pushagent.util.DebugLog;
import com.feelingk.pushagent.util.IntentUtil;
import com.feelingk.pushagent.util.StringUtil;
import com.feelingk.pushagent.util.UIUtil;
import com.flk.olympus.R;
import com.flk.olympus.util.ImageUtil;

/**
 * 쿠폰 상세 화면   
 */
public class CouponDetailActivity extends BaseActivity{
	private MsgItem mCouponItem;
//	private PushMsgData mMsgData;
	
	private TextView mReceiveDate;
	private TextView mPushTitle;
	private ImageView mCouponContent;
	private TextView mPushContent;
	private Button mPushDeleteBtn;
	private ImageButton mTitleBackBtn;
	
	private AlertDialog mDeleteConfirmDialog;
	
	private String appId = "";
	private String appRegId = "";
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mDeleteConfirmDialog != null){
			mDeleteConfirmDialog.dismiss();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flk_olympus_activity_coupon_detail);
		
		if (getIntent().hasExtra("couponItem")){
			mCouponItem = getIntent().getParcelableExtra("couponItem");
		}
		
		if (getIntent().hasExtra("appRegId")) {
			appRegId = getIntent().getStringExtra("appRegId");
		}
		
		if (getIntent().hasExtra("appId")) {
			appId = getIntent().getStringExtra("appId");
		}
		
		mReceiveDate = (TextView)findViewById(R.id.tv_recive_date);
		mPushTitle = (TextView)findViewById(R.id.tv_title);
		mCouponContent = (ImageView)findViewById(R.id.iv_coupon);
		mCouponContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// http:// 또는 https:// 만 들어오는 경우를 위한 필터링
				if (mCouponItem.couponActLink.length() > 8){
					Intent intent = IntentUtil.actionWebBrowserIntent(mCouponItem.couponActLink);
					if(intent != null){
						startActivity(intent);
					}
				}				
			}
		});
		mPushContent = (TextView)findViewById(R.id.tv_coupon);
		mPushDeleteBtn = (Button)findViewById(R.id.bt_msg_delete);
		mPushDeleteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mDeleteConfirmDialog == null || !mDeleteConfirmDialog.isShowing()){
					mDeleteConfirmDialog = UIUtil.createDialog(CouponDetailActivity.this, getString(R.string.flk_olympus_noti_msg_coupon_delete), 
							getString(R.string.flk_olympus_confirm), new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									requestMsgDelete();
								}
							},
							getString(R.string.flk_olympus_cancel), new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							});
					
					mDeleteConfirmDialog.show();
				}				
			}
		});
		mTitleBackBtn = (ImageButton)findViewById(R.id.btn_back);
		mTitleBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		if (mCouponItem != null){
			if(StringUtil.isEmpty(mCouponItem.readTime)){
				// 읽음 확인 이벤트 
				IntentUtil.actionPushMsgRead(CouponDetailActivity.this, mCouponItem.pushId, mCouponItem.regId, mCouponItem.legacyIp, mCouponItem.legacyPort, mCouponItem.couponId);
			}
			
//			mReceiveDate.setText(StringUtil.convertDateFormat(mCouponItem.time, "yyyyMMddHHmmss", "yyyy년 MM월 dd일 E요일"));
			
			switch (mCouponItem.pushType) {
			case 1: // 일반 푸시 
				mCouponContent.setVisibility(View.GONE);
				mPushContent.setText(mCouponItem.msgContent);
//				mPushTitle.setText(mCouponItem.message);
				break;

			case 2: // 광고 푸시
				
				mPushTitle.setText(String.format(getString(R.string.flk_olympus_format_coupon_expire), StringUtil.convertDateFormat(mCouponItem.couponExpireDate, "yyyyMMdd", "yyyy.MM.dd")));
				
				if (!StringUtil.isEmpty(mCouponItem.couponImg)){
					ImageUtil.initNoCacheRoundImageLoader(CouponDetailActivity.this, 0, R.drawable.flk_olympus_img_error_page);
					ImageUtil.imageLoader.displayImage(mCouponItem.couponImg, mCouponContent, ImageUtil.options);
				} else {
					mCouponContent.setVisibility(View.GONE);
				}
				
				if (!StringUtil.isEmpty(mCouponItem.couponDesc)){
					mPushContent.setText(mCouponItem.couponDesc);
				} else {
					mPushContent.setVisibility(View.GONE);
				}
				
				break;
			}
			
			
		} else {
			
			Toast.makeText(this, "메시지 로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
	}

	private void requestMsgDelete(){
		showFullProgressDialog();
		try {
			MsgDeleteRequestTask task = new MsgDeleteRequestTask(getApplicationContext(), new OnResultListener() {

				@Override
				public void onResult(Object resultObject, int resultCode, String resultMsg, int tryCount) {
					if((NetworkConstant.CODE_SUCCESS == resultCode)){
						showToast(getString(R.string.flk_olympus_toast_delete_msg));
						setResult(RESULT_OK);
						finish();
					} else {
						dismissFullProgressDialog();
					}
				}
			});
			task.execute(1, appId, appRegId, mCouponItem.pushId, mCouponItem.couponId);

		} catch (Exception e) {
			DebugLog.e("[MsgDeleteRequestTask] Error : " + e);
		} 
	}
	
}
