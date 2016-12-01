//package com.flk.olympus.adapter;
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.feelingk.pushagent.db.data.PushMessageData;
//import com.feelingk.pushagent.dto.PushMsgData;
//import com.feelingk.pushagent.util.StringUtil;
//import com.flk.olympus.R;
//import com.lguplus.uworks.data.GroupItem;
//
//public class AdapterCouponNew extends BaseAdapter {
//	private Context mContext;
//
//	private ArrayList<PushMessageData> Items = new ArrayList<PushMessageData>();
//	private LayoutInflater inflater;
//	private ViewHolder viewHolder = null;
//
//	private OnDeleteEventListener mListener;
//
//	public interface OnDeleteEventListener {
//		public void onCouponDelete(String msgId);
//	}
//
//	public AdapterCouponNew(Context context) {
//		super();
//		this.mContext = context;
//		this.inflater = LayoutInflater.from(context);
//
//	}
//
//	public void setItemDeleteListener(OnDeleteEventListener listener){
//		mListener = listener;
//	}
//	
//	public void setCustomListItems(ArrayList<PushMessageData> items) {
//		Items = items;
//		this.notifyDataSetChanged();
//	}
//	
//	public void addCustomListItem(ArrayList<PushMessageData> item){
//		Items.addAll(Items.size(), item);
//		this.notifyDataSetChanged();
//	}
//
//	public void listClear() {
//		Items.clear();
//		notifyDataSetChanged();
//	}
//
//	@Override
//	public int getCount() {
//		return Items.size();
//	}
//
//	@Override
//	public PushMessageData getItem(int position) {
//		return Items.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//	
//	@Override
//	public View getView(final int position, View convertView, ViewGroup parent) {
//		View v = convertView;
//		if (v == null) {
//			viewHolder = new ViewHolder();
//			v = inflater.inflate(R.layout.flk_olympus_item_coupon, null);
//
//			viewHolder.tvDate = (TextView) v.findViewById(R.id.tv_date);
//			viewHolder.imgNotiType = (ImageView)v.findViewById(R.id.img_type);
//			viewHolder.tvExpireDate = (TextView) v.findViewById(R.id.tv_expire_date);
//			viewHolder.tvContent = (TextView) v.findViewById(R.id.tv_content);
//			viewHolder.btnDelete = (ImageButton) v.findViewById(R.id.btn_msg_delete);
//			viewHolder.viewBottomMargin = (View) v.findViewById(R.id.view_bottom_margin);
//			v.setTag(viewHolder);
//
//		} else {
//			viewHolder = (ViewHolder) v.getTag();
//		}
//
//		final PushMessageData item = Items.get(position);
//		PushMsgData couponItem = item.getMsgData();
//		
//		// 리스트 하단 마진 처리 
//		if(position == getCount()-1){
//			viewHolder.viewBottomMargin.setVisibility(View.VISIBLE);
//		}else{
//			viewHolder.viewBottomMargin.setVisibility(View.GONE);
//		}
//		
//		
//		switch (couponItem.pushType) {
//		case 1: // 일반 푸시
//			viewHolder.imgNotiType.setImageResource(R.drawable.flk_olympus_icon_notice);
//
//			viewHolder.tvExpireDate.setVisibility(View.GONE);
//			viewHolder.tvContent.setText(couponItem.msgContent);
//			break;
//
//		case 2: // 광고형 쿠폰 푸시 
//			viewHolder.imgNotiType.setImageResource(R.drawable.flk_olympus_icon_coupon);
//			
//			viewHolder.tvExpireDate.setVisibility(View.VISIBLE);
//			viewHolder.tvExpireDate.setText(couponItem.couponExpireDate);
//			viewHolder.tvContent.setText(couponItem.couponDesc);
//			
//			break;
//		}
//		
//		viewHolder.tvDate.setText(StringUtil.convertDateFormat(item.time, "yyyyMMddHHmmss", "yyyy년 MM월 dd일 E요일"));
//		
//		viewHolder.btnDelete.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(mListener != null){
//					mListener.onCouponDelete(item.pushId + "&&" + item.couponId);
//				}
//			}
//		});
//
//		return v;
//	}
//
//	class ViewHolder {
//
//		public TextView tvDate = null;
//		public ImageView imgNotiType = null;
//		public TextView tvExpireDate = null;
//		public TextView tvContent = null;
//		public ImageButton btnDelete = null;
//		
//		public View viewBottomMargin = null;
//
//	}
//
//}
