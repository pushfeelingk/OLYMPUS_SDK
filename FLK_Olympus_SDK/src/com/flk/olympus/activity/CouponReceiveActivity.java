package com.flk.olympus.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.feelingk.pushagent.dto.MsgItem;
import com.feelingk.pushagent.dto.MsgListData;
import com.feelingk.pushagent.network.constant.NetworkConstant;
import com.feelingk.pushagent.network.http.OnResultListener;
import com.feelingk.pushagent.network.http.task.MsgDeleteRequestTask;
import com.feelingk.pushagent.network.http.task.MsgListRequestTask;
import com.feelingk.pushagent.util.DebugLog;
import com.feelingk.pushagent.util.UIUtil;
import com.flk.olympus.R;
import com.flk.olympus.adapter.AdapterCoupon;
import com.flk.olympus.view.PullToRefreshView;
import com.flk.olympus.view.PullToRefreshView.OnFooterRefreshListener;
import com.flk.olympus.view.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 쿠폰 수신함
 */
public class CouponReceiveActivity extends BaseActivity implements OnItemClickListener, OnScrollListener, OnFooterRefreshListener, OnHeaderRefreshListener {
	private TextView mHidden;
	
	private PullToRefreshView mPullToRefreshView;
	private ListView mListView;
	private AdapterCoupon mAdpter;
	private TextView mEmptyMsgTv;
	private ImageButton mSettingBtn;
	private ImageButton mDeleteBtn;
	private ImageButton mTopBtn;
	
	private String appId = "";
	private String appRegId = "";
	
	private AlertDialog mDeleteConfirmDialog;
	private AlertDialog mDeleteAllConfirmDialog;

	private int mCurrentPage = 1;
	private int mMsgTotalCnt = 0;
	
	private int mShowHiddenPopup = 0;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0 : // 히든 메뉴 진입 카운트 리셋
				mShowHiddenPopup = 0;
				break;
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(mDeleteConfirmDialog != null){
			mDeleteConfirmDialog.dismiss();
		}
		
		if(mDeleteAllConfirmDialog != null){
			mDeleteAllConfirmDialog.dismiss();
		}
		
		dismissFullProgressDialog();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flk_olympus_activity_coupon_receive);

		if (getIntent().hasExtra("appRegId")) {
			appRegId = getIntent().getStringExtra("appRegId");
		}
		
		if (getIntent().hasExtra("appId")) {
			appId = getIntent().getStringExtra("appId");
		}

		DebugLog.d("@@@ CouponReceiveActivity onCreate : " + appRegId);
		
		mHidden = (TextView)findViewById(R.id.tv_hidden);
		mHidden.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mShowHiddenPopup == 0){
					mHandler.sendEmptyMessageDelayed(0, 1000*3);
				}
				mShowHiddenPopup++;

				if(mShowHiddenPopup == 10){
					DebugLog.DEBUG_LOG = !DebugLog.DEBUG_LOG;
					showToast("Log 설정값 변경 : " + DebugLog.DEBUG_LOG);
				}				
			}
		});
		
		mSettingBtn = (ImageButton) findViewById(R.id.btn_setting);
		mSettingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent settingIntent = new Intent(CouponReceiveActivity.this, CouponConfigActivity.class);
				settingIntent.putExtra("appRegId", appRegId);
				startActivity(settingIntent);				
			}
		});
		mDeleteBtn = (ImageButton)findViewById(R.id.btn_delete);
		mDeleteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mAdpter.getCount() > 0){
					if (mDeleteAllConfirmDialog == null || !mDeleteAllConfirmDialog.isShowing()){
						mDeleteAllConfirmDialog = UIUtil.createDialog(CouponReceiveActivity.this, getString(R.string.flk_olympus_noti_msg_coupon_all_delete), 
								getString(R.string.flk_olympus_confirm), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								requestAllMsgDelete();
								
							}
						},
						getString(R.string.flk_olympus_cancel), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						});
						mDeleteAllConfirmDialog.show();
					}
				} else {
					showToast(getString(R.string.flk_olympus_toast_coupon_delete_empty));
				}
								
			}
		});
		mTopBtn = (ImageButton)findViewById(R.id.btn_top);
		mTopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListView.setSelectionFromTop(0, 0);				
			}
		});
		
		mPullToRefreshView = (PullToRefreshView)findViewById(R.id.pullToRefresh);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mListView = (ListView) findViewById(R.id.listView);
		
		mEmptyMsgTv = (TextView) findViewById(R.id.tv_empty);

		mAdpter = new AdapterCoupon(CouponReceiveActivity.this);
		mListView.setAdapter(mAdpter);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		
		requestMsgList();
	}

	private void setCouponListData(){
		if (mAdpter.getCount() > 0) {
			mPullToRefreshView.setVisibility(View.VISIBLE);
			mEmptyMsgTv.setVisibility(View.GONE);
			mDeleteBtn.setVisibility(View.VISIBLE);
		} else {
			mPullToRefreshView.setVisibility(View.GONE);
			mEmptyMsgTv.setVisibility(View.VISIBLE);
			mDeleteBtn.setVisibility(View.GONE);
		}
		
		
		// 당겨서 새로고침 해제
		if(mPullToRefreshView != null){
			mPullToRefreshView.onHeaderRefreshComplete();
			mPullToRefreshView.onFooterRefreshComplete();
		}
		
		dismissFullProgressDialog();
	}
	
	
	@Override
	public void onFooterRefresh(PullToRefreshView view) {

		// 더보기 요청 체크 
		if(mMsgTotalCnt > mAdpter.getCount()){
			mCurrentPage++;
			requestMsgList();
	
		}else{
			//더보기 완료 처리
			mPullToRefreshView.onFooterRefreshComplete();
			if(mPullToRefreshView.getMoreUse()){
				mPullToRefreshView.setMoreUse(false);
				showToast(getString(R.string.flk_olympus_toast_last_msg));
			}
		}		
		
	}	
	
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// 새로고침 
		mCurrentPage = 1;
		mPullToRefreshView.setMoreUse(true);
		requestMsgList();
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MsgItem selectItem = mAdpter.getItem(position);
		
		Intent detailIntent = new Intent(CouponReceiveActivity.this, CouponDetailActivity.class);
		detailIntent.putExtra("couponItem", selectItem);
		detailIntent.putExtra("appId", appId);
		detailIntent.putExtra("appRegId", appRegId);
		startActivityForResult(detailIntent, 0000);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
				case 0000:
					mCurrentPage = 1;
					requestMsgList();
					break;
			}
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(firstVisibleItem == 0 && mTopBtn.getVisibility() == View.VISIBLE){
			mTopBtn.setVisibility(View.GONE);
		}
		
		if(firstVisibleItem > 0){
			if(mTopBtn.getVisibility() == View.GONE){
				mTopBtn.setVisibility(View.VISIBLE);
			}
		}
	}
	
	
	/**
	 * 부가정보 조회 
	 * @param tryCount
	 */
	private void requestMsgList(){
		showFullProgressDialog();
		try {
			MsgListRequestTask task = new MsgListRequestTask(getApplicationContext(), new OnResultListener() {

				@Override
				public void onResult(Object resultObject, int resultCode, String resultMsg, int tryCount) {
					if((NetworkConstant.CODE_SUCCESS == resultCode)){
						
						
						MsgListData listData = new MsgListData();
						listData.parseMsgData(resultMsg);
						
						mMsgTotalCnt = listData.totalCount;
						
						if(listData.startIdx == 1){
							mAdpter.setCustomListItems(listData.msgList);	
						} else {
							mAdpter.addCustomListItem(listData.msgList);
						}
						
						setCouponListData();
					} else {
						if(mCurrentPage > 1){
							mCurrentPage--; // 실패시 페이지 값 리턴
						}
						dismissFullProgressDialog();
					}
				}
			});
			task.execute(1, appId, appRegId, mCurrentPage);

		} catch (Exception e) {
			DebugLog.e("[MsgListRequestTask] Error : " + e);
		} 
	}
	
	
	private void requestAllMsgDelete(){
		showFullProgressDialog();
		try {
			MsgDeleteRequestTask task = new MsgDeleteRequestTask(getApplicationContext(), new OnResultListener() {

				@Override
				public void onResult(Object resultObject, int resultCode, String resultMsg, int tryCount) {
					if((NetworkConstant.CODE_SUCCESS == resultCode)){
						
						mAdpter.listClear();
						setCouponListData();
					} else {
						dismissFullProgressDialog();
					}
				}
			});
			task.execute(1, appId, appRegId, "-1", "-1");

		} catch (Exception e) {
			DebugLog.e("[MsgDeleteRequestTask] Error : " + e);
		} 
	}


}
