package com.flk.olympus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.flk.olympus.R;

public class PullToRefreshView extends LinearLayout {

	// refresh states
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	// pull state
	private static final int PULL_UP_STATE = 0;
	private static final int PULL_DOWN_STATE = 1;
	/** last y */
	private int mLastMotionY;
	private View mHeaderView;
	private View mFooterView;
	private AdapterView<?> mAdapterView;
	private ScrollView mScrollView;
	private int mHeaderViewHeight;
	private int mFooterViewHeight;
	private ProgressBar mHeaderProgress;
	private ProgressBar mFooterProgress;
	private LayoutInflater mInflater;
	/** header view current state */
	private int mHeaderState;
	/** footer view current state */
	private int mFooterState;
	/** pull state,pull up or pull down;PULL_UP_STATE or PULL_DOWN_STATE */
	private int mPullState;
	private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;
	private OnFooterRefreshListener mOnFooterRefreshListener;
	private OnHeaderRefreshListener mOnHeaderRefreshListener;

	// 더보기 사용 여부
	private boolean mUseMore = true;
	private boolean mUseRefresh = true;
	private boolean isMoreContent = true;

	public PullToRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullToRefreshView(Context context) {
		super(context);
		init();
	}

	/**
	 * 더보기 사용여부 세팅
	 * @param useMore
	 */
	public void setMoreUse(boolean useMore){
		this.mUseMore = useMore;
	}
	
	public boolean getMoreUse(){
		return mUseMore;
	}
	
	/**
	 * 새로고침 사용여부 세팅
	 * @param useRefresh
	 */
	public void setRefreshUse(boolean useRefresh){
		this.mUseRefresh = useRefresh;
	}
	
	/**
	 * 더보기 컨텐츠 여부 세팅
	 * @param moreContentYN
	 */
	public void setMoreContentYN(boolean moreContentYN){
		this.isMoreContent = moreContentYN;
	}
	
	
	/**
	 * Init 
	 */
	 private void init() { 
		// Load all of the animations we need in code rather than through XML
		 mFlipAnimation = new RotateAnimation(0, -180,
				 RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				 RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		 mFlipAnimation.setInterpolator(new LinearInterpolator());
		 mFlipAnimation.setDuration(250);
		 mFlipAnimation.setFillAfter(true);
		 mReverseFlipAnimation = new RotateAnimation(-180, 0,
				 RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				 RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		 mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		 mReverseFlipAnimation.setDuration(250);
		 mReverseFlipAnimation.setFillAfter(true);

		 mInflater = LayoutInflater.from(getContext());
		 // header view add
		 addHeaderView();
	 }

	 /**
	  * Header View Setting
	  * */
	 private void addHeaderView() {

		 int res = R.layout.view_refresh_header;

		 // header view
		 mHeaderView = mInflater.inflate(res, this, false);

		 mHeaderProgress = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);
		 mHeaderProgress.animate().cancel();

		 measureView(mHeaderView);
		 mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		 LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderViewHeight);

		 params.topMargin = -(mHeaderViewHeight);
		 addView(mHeaderView, params);

	 }

	 /**
	  * FooterView Setting  (xml에 gone 처리) 
	  * */
	 private void addFooterView() {
		 // footer view
		 mFooterView = mInflater.inflate(R.layout.view_refresh_footer, this, false);
		 
		 mFooterProgress = (ProgressBar) mFooterView.findViewById(R.id.pull_to_refresh_progress);
		 
		 measureView(mFooterView);
		 mFooterViewHeight = mFooterView.getMeasuredHeight();
		 
		 LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mFooterViewHeight);
		 
		 addView(mFooterView, params);
	 }

	 @Override
	 protected void onFinishInflate() {
		 super.onFinishInflate();
		 // footer view add
		 addFooterView();
		 initContentAdapterView();
	 }

	 /**
	  * init AdapterView like ListView,GridView and so on;or init ScrollView
	  */
	 private void initContentAdapterView() {

		 int count = getChildCount();
		 if (count < 3) {
			 throw new IllegalArgumentException("this layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
		 }
		 
		 View view = null;
		 for (int i = 0; i < count - 1; ++i) {
			 view = getChildAt(i);
			 if (view instanceof AdapterView<?>) {
				 mAdapterView = (AdapterView<?>) view;
			 }
			 if (view instanceof ScrollView) {
				 // finish later
				 mScrollView = (ScrollView) view;
			 }
		 }
		 
		 if (mAdapterView == null && mScrollView == null) {
			 throw new IllegalArgumentException("must contain a AdapterView or ScrollView in this layout!");
		 }
	 }

	 private void measureView(View child) {
		 ViewGroup.LayoutParams p = child.getLayoutParams();
		 if (p == null) {
			 p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		 }

		 int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		 int lpHeight = p.height;
		 int childHeightSpec;

		 if (lpHeight > 0) {
			 childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		 } else {
			 childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		 }
		 child.measure(childWidthSpec, childHeightSpec);
	 }

	 @Override
	 public boolean onInterceptTouchEvent(MotionEvent e) {
		 int y = (int) e.getRawY();
		 switch (e.getAction()) {
		 case MotionEvent.ACTION_DOWN:
			 mLastMotionY = y;
			 break;
		 case MotionEvent.ACTION_MOVE:

			 int deltaY = y - mLastMotionY; 
			 /** onTouchEvent 가로채는 현상으로 인해 민감도 낮춤 */
			 if (Math.abs(deltaY) > 5 && isRefreshViewScroll(deltaY)) {
				 return true;
			 }
			 break;
		 case MotionEvent.ACTION_UP:
		 case MotionEvent.ACTION_CANCEL:
			 break;
		 }
		 return false;
	 }


	 @Override
	 protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		 super.onScrollChanged(l, t, oldl, oldt);
	 }

	 @Override
	 public boolean onTouchEvent(MotionEvent event) {
//		 if (mLock) {
//			 return true;
//		 }
		 int y = (int) event.getRawY();
		 switch (event.getAction()) {
		 case MotionEvent.ACTION_DOWN:
			 // mLastMotionY = y;
			 break;
		 case MotionEvent.ACTION_MOVE:
			 int deltaY = y - mLastMotionY;

			 /** onTouchEvent 가로채는 현상으로 인해 민감도 낮춤 */
			 if(Math.abs(deltaY) > 5) {

				 if (mPullState == PULL_DOWN_STATE) {
					 headerPrepareToRefresh(deltaY);
				 } else if (mPullState == PULL_UP_STATE) {
					 // 더보기 컨텐츠가 있을 경우만 더보기 프로그래스 보임
					 if(isMoreContent){
						 footerPrepareToRefresh(deltaY);
					 }else{ // 더보기가 없을 경우 리스너만 던져주어 후 처리 가능
						 if (mOnFooterRefreshListener != null) {
							 mOnFooterRefreshListener.onFooterRefresh(this);
						 }
					 }
				 }
				 mLastMotionY = y;
			 }else{

			 }


			 break;
		 case MotionEvent.ACTION_UP:
		 case MotionEvent.ACTION_CANCEL:
			 int topMargin = getHeaderTopMargin();
			 if (mPullState == PULL_DOWN_STATE) {
				 if (topMargin >= -2) { /** 헤더 엘리먼트 고정 이슈로 인해 0에서 -2로 변경  */ 
					 headerRefreshing();
				 } else {
					 setHeaderTopMargin(-mHeaderViewHeight);
				 }
			 } else if (mPullState == PULL_UP_STATE) {
				 if (Math.abs(topMargin) >= mHeaderViewHeight + mFooterViewHeight) {
					 footerRefreshing();
				 } else {
					 setHeaderTopMargin(-mHeaderViewHeight);
				 }
			 }
			 break;
		 }
		 return true; // Touch 자식뷰로 이벤트 받게 리턴 
	 }

	 private boolean isRefreshViewScroll(int deltaY) {
		 if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
			 return false;
		 }
		 if (mAdapterView != null) {
			 if (deltaY > 0) {

				 View child = mAdapterView.getChildAt(0);
				 if (child == null) {
					 return false;
				 }
				 if (mAdapterView.getFirstVisiblePosition() == 0
						 && child.getTop() == 0) {
					 mPullState = PULL_DOWN_STATE;
					 return true;
				 }
				 int top = child.getTop();
				 int padding = mAdapterView.getPaddingTop();
				 if (mAdapterView.getFirstVisiblePosition() == 0
						 && Math.abs(top - padding) <= 8) {
					 mPullState = PULL_DOWN_STATE;
					 return true;
				 }

			 } else if (deltaY < 0) {
				 View lastChild = mAdapterView.getChildAt(mAdapterView.getChildCount() - 1);
				 if (lastChild == null) {
					 return false;
				 }
				 if (lastChild.getBottom() <= getHeight() && mAdapterView.getLastVisiblePosition() == mAdapterView.getCount() - 1) {
					 mPullState = PULL_UP_STATE;
					 return true;
				 }
			 }
		 }
		 if (mScrollView != null) {
			 View child = mScrollView.getChildAt(0);
			 if (deltaY > 0 && mScrollView.getScrollY() == 0) {
				 mPullState = PULL_DOWN_STATE;
				 return true;
			 } else if (deltaY < 0 && child.getMeasuredHeight() <= getHeight() + mScrollView.getScrollY()) {
				 mPullState = PULL_UP_STATE;
				 return true;
			 }
		 }
		 return false;
	 }

	 private void headerPrepareToRefresh(int deltaY) {
		 if(mUseRefresh){
			 int newTopMargin = changingHeaderViewTopMargin(deltaY);
	
			 if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
				 mHeaderState = RELEASE_TO_REFRESH;
			 } else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {
				 mHeaderState = PULL_TO_REFRESH;
			 }
		 }
	 }

	 private void footerPrepareToRefresh(int deltaY) {
		 if(mUseMore){
			 int newTopMargin = changingHeaderViewTopMargin(deltaY);
			 mFooterView.setVisibility(View.VISIBLE);
			 if(mUseRefresh){
				 if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight) && mFooterState != RELEASE_TO_REFRESH) {
					 mFooterState = RELEASE_TO_REFRESH;
				 } else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight)) {
					 mFooterState = PULL_TO_REFRESH;
				 }
			 }else{
				 if (Math.abs(newTopMargin) >= (mFooterViewHeight) && mFooterState != RELEASE_TO_REFRESH) {
					 mFooterState = RELEASE_TO_REFRESH;
				 } else if (Math.abs(newTopMargin) < (mFooterViewHeight)) {
					 mFooterState = PULL_TO_REFRESH;
				 }
			 }
		 }else{
			 mFooterView.setVisibility(View.GONE); 
		 }
	 }

	 private int changingHeaderViewTopMargin(int deltaY) {
		 LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		 float newTopMargin = params.topMargin + deltaY * 0.3f;
		 //params.topMargin = (int) newTopMargin;

		 /*** Header엘리먼트 고정 이슈로 인해 변경 */
		 if((int) newTopMargin > LayoutParams.WRAP_CONTENT){
			 params.topMargin = LayoutParams.WRAP_CONTENT;
		 }else{
			 params.topMargin = (int) newTopMargin;
		 }

		 mHeaderView.setLayoutParams(params);
		 invalidate();
		 return params.topMargin;
	 }

	 private void headerRefreshing() {
		 mHeaderState = REFRESHING;
		 setHeaderTopMargin(0);
		 if (mOnHeaderRefreshListener != null) {
			 mOnHeaderRefreshListener.onHeaderRefresh(this);
		 }
	 }

	 private void footerRefreshing() {
		 mFooterState = REFRESHING;
		 int top = mHeaderViewHeight + mFooterViewHeight;
		 setHeaderTopMargin(-top);

		 if (mOnFooterRefreshListener != null) {
			 mOnFooterRefreshListener.onFooterRefresh(this);
		 }
	 }

	 private void setHeaderTopMargin(int topMargin) {
		 LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		 params.topMargin = topMargin;
		 mHeaderView.setLayoutParams(params);
		 invalidate();
	 }

	 
	 public void onHeaderRefreshComplete() {
		 setHeaderTopMargin(-mHeaderViewHeight);
		 mHeaderState = PULL_TO_REFRESH;
	 }

	 public void onHeaderRefreshComplete(CharSequence lastUpdated) {
		 onHeaderRefreshComplete();
	 }

	 public void onFooterRefreshComplete() {
		 setHeaderTopMargin(-mHeaderViewHeight);
		 mFooterView.setVisibility(View.GONE);
		 mFooterState = PULL_TO_REFRESH;
	 }


	 private int getHeaderTopMargin() {
		 LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
		 return params.topMargin;
	 }

	 public void setOnHeaderRefreshListener(OnHeaderRefreshListener headerRefreshListener) {
		 mOnHeaderRefreshListener = headerRefreshListener;
	 }

	 public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener) {
		 mOnFooterRefreshListener = footerRefreshListener;
	 }

	 /**
	  * Interface definition for a callback to be invoked when list/grid footer
	  * view should be refreshed.
	  */
	  public interface OnFooterRefreshListener {
		  public void onFooterRefresh(PullToRefreshView view);
	  }

	 /**
	  * Interface definition for a callback to be invoked when list/grid header
	  * view should be refreshed.
	  */
	  public interface OnHeaderRefreshListener {
		  public void onHeaderRefresh(PullToRefreshView view);
	  }
}
