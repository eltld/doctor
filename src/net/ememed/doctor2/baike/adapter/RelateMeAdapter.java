package net.ememed.doctor2.baike.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.SayDetailsActivity;
import net.ememed.doctor2.baike.entity.RelateMeInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RelateMeAdapter extends BaseAdapter{
	private String TYPE_PRAISE = "1";
	private String TYPE_COMMENT = "2";
	private String TYPE_SHARE = "3";
	
	private Handler handler;
	private Context context;
	private List<RelateMeInfo> list;
	
	public RelateMeAdapter(Context context, Handler handler, List<RelateMeInfo> list){
		this.context = context;
		this.handler = handler;
		if(null == list){
			this.list = new ArrayList<RelateMeInfo>();
		} else {
			this.list = list;
		}
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(null == convertView){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_relate_me, null);
			holder.civ_photo = (CircleImageView) convertView.findViewById(R.id.civ_photo);
			holder.iv_comment = (ImageView) convertView.findViewById(R.id.iv_comment);
			holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
			holder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_refer_author = (TextView) convertView.findViewById(R.id.tv_refer_author);
			holder.tv_refer_content = (TextView) convertView.findViewById(R.id.tv_refer_content);
			holder.tv_share_to = (TextView) convertView.findViewById(R.id.tv_share_to);
			holder.tv_shuoshuo = (TextView) convertView.findViewById(R.id.tv_shuoshuo);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.ll_content = (LinearLayout) convertView.findViewById(R.id.ll_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		RelateMeInfo info = list.get(position);
		
		holder.tv_name.setText(info.getREALNAME());
		if(TextUtils.isEmpty(info.getTITLE_SHOW())){
			holder.tv_title.setVisibility(View.GONE);
		} else {
			holder.tv_title.setVisibility(View.VISIBLE);
			holder.tv_title.setText(info.getTITLE_SHOW());
		}
		
		//TBTYPE 类别（1赞我的，2评论我的，3分享我的）
		if(info.getTBTYPE().equals(TYPE_PRAISE)){
			((BasicActivity)context).imageLoader.displayImage(info.getAVATAR(), holder.civ_photo, Util.getOptions_avatar());
			holder.tv_time.setText(info.getPRAISE_TIME());
			holder.iv_comment.setVisibility(View.GONE);
			holder.iv_delete.setVisibility(View.GONE);
			holder.tv_comment.setText(info.getCONTENT_COMMENT());
			holder.tv_share_to.setVisibility(View.GONE);
			holder.tv_refer_author.setVisibility(View.GONE);
			holder.tv_refer_content.setVisibility(View.GONE);
			holder.ll_content.setVisibility(View.VISIBLE);
			holder.tv_shuoshuo.setText(info.getCONTENT_SHOW());
			
		} else if(info.getTBTYPE().equals(TYPE_COMMENT)){
			if("1".equals(info.getANONYMOUS())){	//匿名用户不显示头像
				((BasicActivity)context).imageLoader.displayImage(null, holder.civ_photo, Util.getOptions_avatar());
				holder.tv_name.setText("匿名用户");
			} else {
				((BasicActivity)context).imageLoader.displayImage(info.getAVATAR(), holder.civ_photo, Util.getOptions_avatar());
			}
			
			holder.tv_time.setText(info.getCOMMENT_TIME());
			holder.iv_comment.setVisibility(View.VISIBLE);
			holder.iv_delete.setVisibility(View.VISIBLE);
			holder.tv_comment.setText(info.getCONTENT_COMMENT());
			if(info.getREFER_COMMENTID() != null){
				holder.tv_share_to.setVisibility(View.VISIBLE);
				holder.tv_refer_author.setVisibility(View.VISIBLE);
				holder.tv_refer_content.setVisibility(View.VISIBLE);
				holder.tv_share_to.setText("评论@"+info.getREFER_REALNAME()+":");
				holder.tv_refer_author.setText(info.getREFER_REALNAME());
				holder.tv_refer_content.setText(info.getREFER_COMMENT());
			} else {
				holder.tv_share_to.setVisibility(View.GONE);
				holder.tv_refer_author.setVisibility(View.GONE);
				holder.tv_refer_content.setVisibility(View.GONE);
			}
			holder.ll_content.setVisibility(View.VISIBLE);
			holder.tv_shuoshuo.setText(info.getCONTENT_SHOW());
			
		} else if(info.getTBTYPE().equals(TYPE_SHARE)){
			((BasicActivity)context).imageLoader.displayImage(info.getAVATAR(), holder.civ_photo, Util.getOptions_avatar());
			holder.tv_time.setText(info.getSHARE_TIME());
			holder.iv_comment.setVisibility(View.GONE);
			holder.iv_delete.setVisibility(View.GONE);
			holder.tv_share_to.setVisibility(View.VISIBLE);
			if(info.getCONTENT_COMMENT().trim().startsWith("分享百科首页至")){
				holder.tv_share_to.setVisibility(View.VISIBLE);
				holder.ll_content.setVisibility(View.GONE);
				holder.tv_share_to.setText("分享百科首页至");
				String str = info.getCONTENT_COMMENT().trim();
				holder.tv_comment.setText(str.substring(7));
			} else if(info.getCONTENT_COMMENT().trim().startsWith("分享至")){
				holder.tv_share_to.setVisibility(View.VISIBLE);
				holder.ll_content.setVisibility(View.VISIBLE);
				holder.tv_share_to.setText("分享至");
				holder.tv_comment.setText(info.getCONTENT_COMMENT().trim().substring(3));
				holder.tv_shuoshuo.setText(info.getCONTENT_SHOW());
			}
			holder.tv_refer_author.setVisibility(View.GONE);
			holder.tv_refer_content.setVisibility(View.GONE);
		}
		
		final RelateMeInfo info2 = info;
		holder.iv_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message message = new Message();
				message.what = IResult.DELETE_COMMENT_INNER;
				message.arg1 = Integer.parseInt(info2.getCONTENTID());
				handler.sendMessage(message);
			}
		});
		
		holder.iv_comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, SayDetailsActivity.class);
				intent.putExtra("says_id", info2.getSAYSID());
				intent.putExtra("is_my_baike", true);
				intent.putExtra("name", SharePrefUtil.getString(Conast.Doctor_Name));
				intent.putExtra("refer_comment_id", info2.getCONTENTID());
				intent.putExtra("refer_name", info2.getREALNAME());
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public void change(List<RelateMeInfo> list){
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	
	class ViewHolder{
		CircleImageView civ_photo;
		TextView tv_name;
		TextView tv_time;
		TextView tv_share_to;
		TextView tv_comment;
		ImageView iv_delete;
		ImageView iv_comment;
		TextView tv_refer_author;
		TextView tv_refer_content;
		TextView tv_title;
		TextView tv_shuoshuo;
		LinearLayout ll_content;
	}
}
