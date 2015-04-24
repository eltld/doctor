package net.ememed.doctor2.baike.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.LogonResultActivity;
import net.ememed.doctor2.activity.PersonInfoActivity;
import net.ememed.doctor2.baike.DoctorSimpleInfoActivity;
import net.ememed.doctor2.baike.MyFansActivity;
import net.ememed.doctor2.baike.RewardListActivity;
import net.ememed.doctor2.baike.SayDetailsActivity;
import net.ememed.doctor2.baike.entity.BaikeMemberInfo;
import net.ememed.doctor2.baike.entity.BaikeShuoshuoInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.MemberInfo;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShuoshuoAdapter extends BaseAdapter {

	private final int REQUEST_SAYS_DETAIL = 2;
	private List<BaikeShuoshuoInfo> listItems;
	private BaikeMemberInfo memberInfo = null;
	private BasicActivity activity;
	private Handler handler;
	private Boolean isMyBaike = false;
	private Boolean isShowName = false;

	public ShuoshuoAdapter(List<BaikeShuoshuoInfo> listItems, BasicActivity activity, Handler handler, Boolean isMyBaike) {
		if (null == listItems) {
			listItems = new ArrayList<BaikeShuoshuoInfo>();
		}

		this.listItems = listItems;
		this.activity = activity;
		this.handler = handler;
		this.isMyBaike = isMyBaike;
	}
	
	public ShuoshuoAdapter(List<BaikeShuoshuoInfo> listItems, BasicActivity activity, Handler handler, Boolean isMyBaike, Boolean isShowName) {
		if (null == listItems) {
			listItems = new ArrayList<BaikeShuoshuoInfo>();
		}

		this.listItems = listItems;
		this.activity = activity;
		this.handler = handler;
		this.isMyBaike = isMyBaike;
		this.isShowName = isShowName;
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.say_item, null);
			holder.rl_shuoshuo = (RelativeLayout) convertView.findViewById(R.id.rl_shuoshuo);
			holder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
			holder.tv_read_num = (TextView) convertView.findViewById(R.id.tv_read_num);
			holder.tv_praise_num = (TextView) convertView.findViewById(R.id.tv_praise_num);
			holder.tv_comment_num = (TextView) convertView.findViewById(R.id.tv_comment_num);
			holder.tv_share_num = (TextView) convertView.findViewById(R.id.tv_share_num);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.ll_pic_area = (LinearLayout) convertView.findViewById(R.id.ll_pic_area);
			holder.gridview = (GridView) convertView.findViewById(R.id.gridview);
			holder.iv_new = (ImageView) convertView.findViewById(R.id.iv_new);
			holder.ll_praise_layout = (LinearLayout) convertView.findViewById(R.id.ll_praise_layout);
			holder.ll_comment_layout = (LinearLayout) convertView.findViewById(R.id.ll_comment_layout);
			holder.iv_praise = (ImageView) convertView.findViewById(R.id.iv_praise);
			holder.tv_pic_count = (TextView) convertView.findViewById(R.id.tv_pic_count);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		BaikeShuoshuoInfo info = listItems.get(position);
		
		if(isShowName){
			holder.tv_name.setVisibility(View.VISIBLE);
			holder.tv_name.setText(info.getREALNAME());
		} else {
			holder.tv_name.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(info.getIS_NEW()) && "1".equals(info.getIS_NEW())) {
			holder.iv_new.setVisibility(View.VISIBLE);
		} else {
			holder.iv_new.setVisibility(View.GONE);
		}

		holder.tv_context.setText(info.getCONTENT_SHOW());

		// 阅读数
		if (!TextUtils.isEmpty(info.getHITS())) {
			holder.tv_read_num.setText(convertNum(info.getHITS()));
		} else {
			holder.tv_read_num.setText("0");
		}
		// 点赞图标
		if (true == info.getIS_PRAISED()) {
			holder.iv_praise.setBackgroundResource(R.drawable.praise_ioc_p);
		} else {
			holder.iv_praise.setBackgroundResource(R.drawable.praise_ioc_n);
		}

		// 点赞数
		if (!TextUtils.isEmpty(info.getPRAISE_NUM())) {
			holder.tv_praise_num.setText(convertNum(info.getPRAISE_NUM()));
		} else {
			holder.tv_praise_num.setText("0");
		}
		// 评论数
		if (!TextUtils.isEmpty(info.getCOMMENT_NUM())) {
			holder.tv_comment_num.setText(convertNum(info.getCOMMENT_NUM()));
		} else {
			holder.tv_comment_num.setText("0");
		}
		// 分享数
		if (!TextUtils.isEmpty(info.getSHARE_COUNT())) {
			holder.tv_share_num.setText(convertNum(info.getSHARE_COUNT()));
		} else {
			holder.tv_share_num.setText("0");
		}

		String time = info.getCREATE_TIME();	//2014-12-12 12:12:12
		time = time.substring(2, time.length()-4);	//14-12-12 12:12
		holder.tv_time.setText(info.getCREATE_TIME());

		final BaikeShuoshuoInfo info2 = info;
		// 点击评论图标，直接跳到详情页评论的地方
		holder.ll_comment_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, SayDetailsActivity.class);
				intent.putExtra("says_id", info2.getSAYSID());
				intent.putExtra("need_adjust", true);
				intent.putExtra("name", memberInfo.getREALNAME());
				if (isMyBaike || info2.getDOCTORID().equals(SharePrefUtil.getString(Conast.Doctor_ID))) {
					intent.putExtra("is_my_baike", true);
				}
				activity.startActivityForResult(intent, REQUEST_SAYS_DETAIL);
			}
		});

		final int says_id = Integer.parseInt(info.getSAYSID());
		final int pos = position;
		final boolean isPraised = info.getIS_PRAISED();
		holder.ll_praise_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isPraised) {
					Message message = new Message();
					message.what = IResult.GIVE_PRAISE_INNER;
					message.arg1 = says_id;
					message.arg2 = pos;
					handler.sendMessage(message);
				}
			}
		});

		if (null != info.getPICS_THUMB() && info.getPICS_THUMB().size() > 0) {
			holder.ll_pic_area.setVisibility(View.VISIBLE);
			List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
			for (int i = 0; i < info.getPICS_THUMB().size(); i++) {
				if (/* i < 4 && */!TextUtils.isEmpty(info.getPICS_THUMB().get(i))) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("path", "");
					map.put("url", info.getPICS_THUMB().get(i));
					map.put("url_big", info.getPICS().get(i));
					list2.add(map);
				}
			}
			PutImageAdapter adapter = new PutImageAdapter(list2, activity, handler, false, 4);
			int wid;

			if (list2.size() > 4) {
				wid = 4 * 50 + 3 * 10;
				holder.gridview.setNumColumns(4);
			} else {
				wid = list2.size() * 50 + (list2.size() - 1) * 10;
				holder.gridview.setNumColumns(list2.size());
			}
			holder.gridview.setLayoutParams(new LayoutParams(Util.dip2px(activity, wid), LayoutParams.WRAP_CONTENT));
			holder.gridview.setColumnWidth(50);
			holder.gridview.setAdapter(adapter);
			holder.tv_pic_count.setText("共" + list2.size() + "张");
		} else {
			holder.ll_pic_area.setVisibility(View.GONE);
		}

		return convertView;
	}

	public void change(List<BaikeShuoshuoInfo> lists, BaikeMemberInfo memberInfo) {
		if (lists == null) {
			lists = new ArrayList<BaikeShuoshuoInfo>();
		}
		clear();
		this.listItems.addAll(lists);
		this.memberInfo = memberInfo;
		notifyDataSetChanged();
	}
	
	public void change(List<BaikeShuoshuoInfo> lists) {
		if (lists == null) {
			lists = new ArrayList<BaikeShuoshuoInfo>();
		}
		clear();
		this.listItems.addAll(lists);
		notifyDataSetChanged();
	}

	public void clear() {
		this.listItems.clear();
	}

	private String convertNum(String num) {

		try {
			int n = Integer.parseInt(num);
			if (n > 10000) {
				return (n / 10000) + "万";
			} else {
				return num;
			}
		} catch (NumberFormatException e) {
			return num;
		}
	}

	class ViewHolder {
		RelativeLayout rl_shuoshuo;
		TextView tv_context;
		TextView tv_read_num;
		TextView tv_praise_num;
		TextView tv_comment_num;
		TextView tv_share_num;
		TextView tv_time;
		LinearLayout ll_pic_area;
		GridView gridview;
		TextView tv_pic_count;
		ImageView iv_new;
		LinearLayout ll_comment_layout;
		LinearLayout ll_praise_layout;
		ImageView iv_praise;
		TextView tv_name;
	}

}
