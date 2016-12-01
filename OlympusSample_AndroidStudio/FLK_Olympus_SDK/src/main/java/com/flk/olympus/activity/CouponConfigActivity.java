package com.flk.olympus.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.feelingk.pushagent.db.LocalDBManager;
import com.feelingk.pushagent.db.data.PushAgentAppData;
import com.feelingk.pushagent.dto.UserOptionalData;
import com.feelingk.pushagent.dto.UserOptionalInfoData;
import com.feelingk.pushagent.network.constant.NetworkConstant;
import com.feelingk.pushagent.network.http.OnResultListener;
import com.feelingk.pushagent.network.http.task.AppConfigUpdateRequestTask;
import com.feelingk.pushagent.network.http.task.UserOptionalInfoListRequestTask;
import com.feelingk.pushagent.network.http.task.UserOptionalInfoUpdateRequestTask;
import com.feelingk.pushagent.network.json.JsonManager;
import com.feelingk.pushagent.service.PushAgentSeviceSender;
import com.feelingk.pushagent.util.DebugLog;
import com.feelingk.pushagent.util.StringUtil;
import com.feelingk.pushagent.util.UIUtil;
import com.flk.olympus.R;
import com.flk.olympus.view.UserOptionalInfoView;
import com.flk.olympus.view.UserOptionalInfoView.EventListener;

/**
 * 쿠폰 수신 설정 화면  
 */
public class CouponConfigActivity extends BaseActivity implements EventListener{
	private ImageButton mBackBtn;
	private ImageButton mSaveBtn;
	
	private LinearLayout mReceiveAgree;
	private CheckBox mReceiveAgreeCheck;
	
	private LinearLayout mUserInfoConfig;
	private CheckBox mUserInfoConfigCheck;
	
	private LinearLayout mUserInfoConfigItemView;
	private View mUserInfoDimView;
	
	private String appRegId = "";
	
	private int orgAgreeDate;
	private PushAgentAppData mAppData;
	private UserOptionalData mUserOptionalInfoData;
	
	private LocalDBManager mDbManager;
	
	private AlertDialog mLocationPermissionGuide;
	private AlertDialog mCommDialog;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(mLocationPermissionGuide != null){
			mLocationPermissionGuide.dismiss();
		}
		dismissFullProgressDialog();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flk_olympus_activity_coupon_config);
		
		if(getIntent().hasExtra("appRegId")){
			appRegId = getIntent().getStringExtra("appRegId");
		}
		
		mBackBtn = (ImageButton)findViewById(R.id.btn_back);
		mBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();				
			}
		});
		
		mSaveBtn = (ImageButton)findViewById(R.id.btn_setting_upload);
		mSaveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				appConfigChange();				
			}
		});
		
		mReceiveAgree = (LinearLayout)findViewById(R.id.btn_push_receive_agree);
		mReceiveAgree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveChangedData();
				if(!mReceiveAgreeCheck.isChecked()){
					
					LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
					if(!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
					
						if(mLocationPermissionGuide == null || !mLocationPermissionGuide.isShowing()){
							mLocationPermissionGuide = UIUtil.createDialog(CouponConfigActivity.this, getString(R.string.flk_olympus_noti_msg_location_permission), 
									getString(R.string.flk_olympus_confirm), new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
										}
									}, 
									getString(R.string.flk_olympus_cancel), new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											
										}
									});
							mLocationPermissionGuide.show();
						}
					}
				}
				
				mReceiveAgreeCheck.setChecked(!mReceiveAgreeCheck.isChecked());				
			}
		});
		mReceiveAgreeCheck = (CheckBox)findViewById(R.id.check_receive);
		
		mUserInfoConfig = (LinearLayout)findViewById(R.id.btn_user_info_setting);
		mUserInfoConfig.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveChangedData();
				mUserInfoConfigCheck.setChecked(!mUserInfoConfigCheck.isChecked());				
			}
		});
		mUserInfoConfigCheck = (CheckBox)findViewById(R.id.check_user_info);
		mUserInfoConfigCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mUserInfoDimView.setVisibility(View.GONE);
				}else{
					mUserInfoDimView.setVisibility(View.VISIBLE);
				}
			}
		});
		
		mUserInfoConfigItemView = (LinearLayout)findViewById(R.id.ly_user_info_config);
		mUserInfoDimView = (View)findViewById(R.id.view_dim);
		
		mDbManager = new LocalDBManager(CouponConfigActivity.this);
		ArrayList<PushAgentAppData> appDatas = mDbManager.selectDataList(appRegId, "regid");
		
		if (appDatas.size() == 1){
			mAppData = appDatas.get(0);
			orgAgreeDate = mAppData.getReceiveAgree();
			mReceiveAgreeCheck.setChecked(StringUtil.intToBoolean(orgAgreeDate));
			requestUserOpitionalInfoList(0);
		} else { 
			// TODO?
		}
		
	}

	private void saveChangedData(){
		if(mSaveBtn.getVisibility() == View.GONE){
			mSaveBtn.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onBackPressed() {
		if(mSaveBtn.getVisibility() == View.VISIBLE){
			if(mCommDialog == null || !mCommDialog.isShowing()){
				mCommDialog = UIUtil.createDialog(CouponConfigActivity.this, getString(R.string.flk_olympus_noti_msg_confi_save_confirm), 
						getString(R.string.flk_olympus_confirm), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								appConfigChange();
							}
						}, 
						getString(R.string.flk_olympus_cancel), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});
				mCommDialog.show();
			}
		} else {
			finish();
		}
	}
	
	
	private void appConfigChange() {
		
		boolean isUserInfoCheck = mUserInfoConfigCheck.isChecked();
		
		if (mUserOptionalInfoData != null){
			// 부가정보 설정 Flag가 기존과 다르면 데이터 업로드 
			if (isUserInfoCheck || mUserOptionalInfoData.optionInfoFlag != StringUtil.booleanToInt(isUserInfoCheck)){
			
				UserOptionalData updateData = new UserOptionalData();
				updateData.optionInfoFlag = StringUtil.booleanToInt(isUserInfoCheck);
				
				boolean isCheck = true;
				if (isUserInfoCheck){
					for (int i = 0 ; i < mUserInfoConfigItemView.getChildCount() ; i++){
						
						UserOptionalInfoView itemView = (UserOptionalInfoView)mUserInfoConfigItemView.getChildAt(i);
						String selectedData = itemView.getSelectedItemData();
						if (StringUtil.isEmpty(selectedData)){
							isCheck = false;
							dismissFullProgressDialog();
							showToast(String.format(getString(R.string.flk_olympus_toast_optional_info_select_empty), itemView.getOptionalTitle()));
							break;
						} else {
							UserOptionalInfoData itemData = new UserOptionalInfoData();
							itemData.optCode = itemView.getOptionalCode();
							itemData.type = itemView.getOptionalType();
							itemData.selectCode = selectedData;
							updateData.addItem(itemData);
						}
					}
				}
				
				if(isCheck){
					requestUserOpitionalInfoUpdate(0, updateData.getOptionalSelectedDate());
				}
			
			} else {
				onActivityFinish();
			}
		} else {
			onActivityFinish();
		}
	}

	
	private void onActivityFinish(){
		// 수신 설정이 다르면 서버에 등록 후 화면 종료 
		if(orgAgreeDate != StringUtil.booleanToInt(mReceiveAgreeCheck.isChecked()) ){
			requestAppRegInsert(0, StringUtil.booleanToInt(mReceiveAgreeCheck.isChecked()));	
		} else {
			dismissFullProgressDialog();
			finish();
		}
	}
	
	
	
	/**
	 * 옵션 설정정보 세팅 
	 */
	private void setUserOptionalData(){
		dismissFullProgressDialog();
		if(mUserOptionalInfoData != null){
			mUserInfoConfigCheck.setChecked(StringUtil.intToBoolean(mUserOptionalInfoData.optionInfoFlag));
			
			if(mUserOptionalInfoData.optionInfo.size() > 0){
				for(UserOptionalInfoData viewItem : mUserOptionalInfoData.optionInfo){
					UserOptionalInfoView itemView = new UserOptionalInfoView(CouponConfigActivity.this, viewItem);
					itemView.setEventListener(this);
					mUserInfoConfigItemView.addView(itemView);
				}
			}
		}
	}
	
	/**
	 * App Config Update
	 * @param userId
	 * @param tryCount
	 * @param packageNm
	 */
	private void requestAppRegInsert(int tryCount, final int recvYN){
		showFullProgressDialog();
		try {
			AppConfigUpdateRequestTask task = new AppConfigUpdateRequestTask(getApplicationContext(), new OnResultListener() {

				@Override
				public void onResult(Object resultObject, int resultCode, String resultMsg, int tryCount) {
					if((NetworkConstant.CODE_SUCCESS == resultCode)){
						mDbManager.updateReceiveAgree(mAppData.getPackageName(), recvYN);
						try {
							if(StringUtil.intToBoolean(recvYN)){
								PushAgentSeviceSender.SendRegResultErrorTo3rdPartyApp(CouponConfigActivity.this, mAppData.getPackageName(), JsonManager.RESULT_CODE_900);
							}else{
								PushAgentSeviceSender.SendRegResultErrorTo3rdPartyApp(CouponConfigActivity.this, mAppData.getPackageName(), JsonManager.RESULT_CODE_901);
							}
							
						} catch (UnsupportedEncodingException e) {
						}
					} 
					dismissFullProgressDialog();
					finish();
				}
			});
			task.execute(tryCount, mAppData.getAppName(), mAppData.getRegId(), StringUtil.intToString(recvYN));

		} catch (Exception e) {
			DebugLog.e("[AppConfigUpdateRequestTask] Error : " + e);
		} 
	}
	
	/**
	 * 부가정보 조회 
	 * @param tryCount
	 */
	private void requestUserOpitionalInfoList(int tryCount){
		showFullProgressDialog();
		try {
			UserOptionalInfoListRequestTask task = new UserOptionalInfoListRequestTask(getApplicationContext(), new OnResultListener() {

				@Override
				public void onResult(Object resultObject, int resultCode, String resultMsg, int tryCount) {
					if((NetworkConstant.CODE_SUCCESS == resultCode)){
						
						mUserOptionalInfoData = new UserOptionalData(resultMsg);
						setUserOptionalData();
					
					} 
				}
			});
			task.execute(tryCount, mAppData.getAppName(), mAppData.getRegId());

		} catch (Exception e) {
			DebugLog.e("[UserOptionalInfoListRequestTask] Error : " + e);
		} 
	}
	
	/**
	 * 부가정보 설정 업데이트 
	 * @param tryCount
	 * @param optionalInfoStr
	 */
	private void requestUserOpitionalInfoUpdate(int tryCount, String optionalInfoStr){
		showFullProgressDialog();
		try {
			UserOptionalInfoUpdateRequestTask task = new UserOptionalInfoUpdateRequestTask(getApplicationContext(), new OnResultListener() {

				@Override
				public void onResult(Object resultObject, int resultCode, String resultMsg, int tryCount) {
					if((NetworkConstant.CODE_SUCCESS == resultCode)){
						onActivityFinish();
					} 
				}
			});
			task.execute(tryCount, mAppData.getAppName(), mAppData.getRegId(), optionalInfoStr);

		} catch (Exception e) {
			DebugLog.e("[UserOptionalInfoListRequestTask] Error : " + e);
		} 
	}
	

	@Override
	public void selectItem() {
		saveChangedData();		
	}
}
