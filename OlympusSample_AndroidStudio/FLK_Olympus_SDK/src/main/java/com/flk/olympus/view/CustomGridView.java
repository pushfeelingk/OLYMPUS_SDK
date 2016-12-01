package com.flk.olympus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * 스크롤뷰 안에서 그리드뷰 사용시 스크롤이 안되거나, 사이즈가 변경되는 현상으로 인한 커스텀 그리드뷰 
 */
public class CustomGridView extends GridView{
	
	public CustomGridView(Context context) {
		super(context);
	}
	
	public CustomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = getMeasuredHeight();
	}
	

	
}
