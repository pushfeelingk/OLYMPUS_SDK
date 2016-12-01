package com.flk.olympus.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feelingk.pushagent.dto.UserOptionalInfoData;
import com.feelingk.pushagent.util.StringUtil;
import com.flk.olympus.R;
import com.flk.olympus.adapter.AdapterOptionalItemGridView;

/** 부가정보 표기 View */
public class UserOptionalInfoView extends LinearLayout implements OnItemClickListener{

	
	private Context mContext;
	private EventListener mEventListener;
	private UserOptionalInfoData mData;
	
	private TextView mOptionTitle;
	private CustomGridView mOptionSelectItemView;
	private AdapterOptionalItemGridView mOptionSelectAdapter;
	
	
	public UserOptionalInfoView(Context context) {
		super(context);
	}
	
	public UserOptionalInfoView(Context context, UserOptionalInfoData data) {
		super(context);
		
		mContext = context;
		mData = data;
		init();
	}
	

	/**
	 * 화면 초기화 
	 */
	private void init() {
		String service = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)mContext.getSystemService (service);
        View v = li.inflate (R.layout.flk_olympus_view_user_optional_info, this, true);
        
        mOptionTitle = (TextView)v.findViewById(R.id.tv_optional_title);
        mOptionSelectItemView = (CustomGridView)v.findViewById(R.id.grid_item);
        mOptionSelectItemView.setOnItemClickListener(this);
        
        mOptionSelectAdapter = new AdapterOptionalItemGridView(mContext, !StringUtil.intToBoolean(mData.type));
        mOptionSelectItemView.setAdapter(mOptionSelectAdapter);
        
        mOptionTitle.setText(mData.title);
        mOptionSelectAdapter.setItemList(mData.items);
        mOptionSelectAdapter.setCheckValue(mData.selectCode);
        
	}
	
	public String getOptionalTitle(){
		return mData.title;
	}
	
	public int getOptionalType(){
		return mData.type;
	}
	
	public String getOptionalCode(){
		return mData.optCode;
	}
	
	public String getSelectedItemData(){
		return mOptionSelectAdapter.getSelectedItem();
	}
	
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(mEventListener != null){
			mEventListener.selectItem();
		}
		
		if(mOptionSelectAdapter.getSelectType()){
			mOptionSelectAdapter.setCheck(position);
		} else {
			mOptionSelectAdapter.setRadioCheck(position);	
		}
		
	}

	
	public void setEventListener(EventListener l){
		mEventListener = l;
	}
	
	public interface EventListener{
		void selectItem();
			
	}
	
}
