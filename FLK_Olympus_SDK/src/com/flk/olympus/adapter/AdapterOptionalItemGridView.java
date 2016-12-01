package com.flk.olympus.adapter;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.feelingk.pushagent.dto.UserOptionalInfoSelectData;
import com.feelingk.pushagent.util.StringUtil;
import com.flk.olympus.R;

/**
 * 그룹 리스트 GridView Adapter Class
 * */
public class AdapterOptionalItemGridView extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLiInflater;
	private boolean mIsMultSelect = false;
	
	private ArrayList<UserOptionalInfoSelectData> mData = new ArrayList<UserOptionalInfoSelectData>();


	public AdapterOptionalItemGridView(Context context, boolean isMult) {
		mContext = context;
		mLiInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mIsMultSelect = isMult;
	}

	public void setItemList(ArrayList<UserOptionalInfoSelectData> list) {
		mData = list;
		notifyDataSetChanged();
	}

	/**
	 * 체크상태 변경 (현재 값 토글 처리)
	 */
	public void setCheck(int pos) {
		mData.get(pos).isCheck = !mData.get(pos).isCheck;
		notifyDataSetChanged();
	}

	/** 
	 * 단일 선택 체크 상태 적용 (라디오 버튼 동작방식)
	 * @param chk
	 */
	public void setRadioCheck(int pos){
		for(UserOptionalInfoSelectData item : mData){
			item.isCheck = false;
		}
		
		mData.get(pos).isCheck = true;
		notifyDataSetChanged();
	}
	
	/**
	 * 선택 값 세팅
	 * @param selectCode
	 */
	public void setCheckValue(String selectCode){
		
		String[] checkArr = selectCode.split("|");
		ArrayList<String> valueList = new ArrayList<String>(Arrays.asList(checkArr));
		
		for(UserOptionalInfoSelectData item : mData){
			if(valueList.contains(item.code)){
				item.isCheck = true;
			}
		}
		
		notifyDataSetChanged();
	}
	
	
	/**
	 * 전체선택/해제
	 * @param chk
	 */
	public void checkAll(boolean chk) {
		for(UserOptionalInfoSelectData item : mData){
			item.isCheck = chk;
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public UserOptionalInfoSelectData getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public boolean getSelectType(){
		return mIsMultSelect;
	}

	/**
	 * 선택 아이템 정보
	 * @return
	 */
	public String getSelectedItem(){
		String result = "";
		for(UserOptionalInfoSelectData item : mData){
			if(item.isCheck){
				if(!StringUtil.isEmpty(result)){
					result += "|";
				}
				
				result += item.code;
			}
		}
		
		return result;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;

		if (convertView == null) {
			convertView = mLiInflater.inflate(R.layout.flk_olympus_item_user_optional_select, null);
			viewHolder = new ViewHolder();
			viewHolder.checkbox = (CheckBox)convertView.findViewById(R.id.checkbox);
			if(mIsMultSelect){
				viewHolder.checkbox.setBackgroundResource(R.drawable.flk_olympus_btn_square);	
			} else {
				viewHolder.checkbox.setBackgroundResource(R.drawable.flk_olympus_btn_radio);
			}
			
			viewHolder.itemName = (TextView)convertView.findViewById(R.id.tv_optional_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}

		UserOptionalInfoSelectData data = mData.get(position);

		viewHolder.itemName.setText(data.name);
		viewHolder.checkbox.setChecked(data.isCheck);
		
		return convertView;
	}

	public static class ViewHolder {
		public CheckBox checkbox;
		public TextView itemName;
	}

}
