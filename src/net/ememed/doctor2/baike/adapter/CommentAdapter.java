package net.ememed.doctor2.baike.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.entity.SaysCommentInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.MenuDialog;
import android.content.DialogInterface;
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

public class CommentAdapter extends BaseAdapter {

	private BasicActivity activity;
	private List<SaysCommentInfo> listItems;
	private Handler handler;
	private Boolean isMyBaike; // 是否是自己发布的说说

	public CommentAdapter(List<SaysCommentInfo> listItems, BasicActivity activity, Handler handler, Boolean isMyBaike) {
		if (null == listItems) {
			listItems = new ArrayList<SaysCommentInfo>();
		}

		this.listItems = listItems;
		this.activity = activity;
		this.handler = handler;
		this.isMyBaike = isMyBaike;
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.comment_item, null);
			holder.civ_photo = (CircleImageView) convertView.findViewById(R.id.civ_photo);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_quote_name = (TextView) convertView.findViewById(R.id.tv_quote_name);
			holder.tv_quoto_content = (TextView) convertView.findViewById(R.id.tv_quoto_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.ll_quote_area = (LinearLayout) convertView.findViewById(R.id.ll_quote_area);
			holder.iv_comment = (ImageView) convertView.findViewById(R.id.iv_comment);
			holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SaysCommentInfo info = listItems.get(position);

		if (isMyBaike) {
			holder.iv_delete.setVisibility(View.VISIBLE);
		} else {
			if (SharePrefUtil.getString(Conast.Doctor_ID).equals(info.getMEMBERID())) {
				holder.iv_delete.setVisibility(View.VISIBLE);
			} else {
				holder.iv_delete.setVisibility(View.GONE);
			}
		}

		// 头像和名字（匿名用户头像显示默认，姓名显示匿名用户）
		if ("1".equals(info.getANONYMOUS())) {
			activity.imageLoader.displayImage(null, holder.civ_photo, Util.getOptions_avatar());
			holder.tv_name.setText("匿名用户");
		} else {
			activity.imageLoader.displayImage(info.getAVATAR(), holder.civ_photo, Util.getOptions_avatar());
			holder.tv_name.setText(info.getREALNAME());
		}

		// 引用的内容
		if (!TextUtils.isEmpty(info.getREFER_COMMENTID())) {
			holder.ll_quote_area.setVisibility(View.VISIBLE);
			if ("1".equals(info.getREFER_COMMENT().getANONYMOUS())) {
				holder.tv_quote_name.setText("匿名用户");
			} else {
				holder.tv_quote_name.setText(info.getREFER_COMMENT().getREALNAME());
			}
			holder.tv_quoto_content.setText(info.getREFER_COMMENT().getCONTENT());
		} else {
			holder.ll_quote_area.setVisibility(View.GONE);
		}

		holder.tv_content.setText(info.getCONTENT());
		holder.tv_time.setText(info.getCREATE_TIME());

		final int pos = position;
		holder.iv_comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message message = new Message();
				message.what = IResult.COMMENT_OTHER;
				message.arg1 = Integer.parseInt(listItems.get(pos).getCOMMENTID());
				message.obj = listItems.get(pos).getREALNAME();
				handler.sendMessage(message);
			}
		});

		holder.iv_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MenuDialog.Builder alert = new MenuDialog.Builder(activity);
				MenuDialog dialog = alert.setTitle("确定删除此条评论吗？")
						.setPositiveButton(activity.getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								Message message = new Message();
								message.what = IResult.DELETE_COMMENT_INNER;
								message.arg1 = Integer.parseInt(listItems.get(pos).getCOMMENTID());
								handler.sendMessage(message);
							}
						}).setNegativeButton(activity.getString(R.string.confirm_no), new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).create();
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		});

		return convertView;
	}

	public void change(List<SaysCommentInfo> lists) {
		if (lists == null) {
			lists = new ArrayList<SaysCommentInfo>();
		}
		this.listItems = lists;
		notifyDataSetChanged();
	}

	public void clear() {
		this.listItems.clear();
	}

	class ViewHolder {
		CircleImageView civ_photo;
		ImageView iv_comment;
		ImageView iv_delete;
		TextView tv_name;
		TextView tv_quote_name;
		TextView tv_quoto_content;
		TextView tv_content;
		TextView tv_time;
		LinearLayout ll_quote_area;
	}

}
