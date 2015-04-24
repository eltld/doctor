package net.ememed.doctor2.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.GroupDetailActivity;
import net.ememed.doctor2.entity.GroupMemberEntity;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GroupPutImageAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mType;
	private List<GroupMemberEntity> mDatas;

	private GroupMemberEntity mAddGroupMember;
	private GroupMemberEntity mSubGroupMember;

	private OnMemberDeteleListener mMemberDeteleListener;
	private boolean mIsDeleteStatus;

	public GroupPutImageAdapter(Context context, List<GroupMemberEntity> list, int type) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mType = type;
		mDatas = list == null ? new ArrayList<GroupMemberEntity>() : list;
		mAddGroupMember = new GroupMemberEntity("drawable://" + R.drawable.ic_group_add);
		mSubGroupMember = new GroupMemberEntity("drawable://" + R.drawable.ic_group_sub);
		switch (type) {
		case GroupDetailActivity.TYPE_APPLY:
			break;
		case GroupDetailActivity.TYPE_OWNER:
			mDatas.add(mSubGroupMember);
			mDatas.add(mAddGroupMember);
			break;
		case GroupDetailActivity.TYPE_JOINER:
			mDatas.add(mAddGroupMember);
			break;
		default:
			break;
		}
		checkHideDelete();
	}

	public int getCount() {
		return mDatas.size();
	}

	public GroupMemberEntity getItem(int position) {
		return mDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_group_member_img, null);
			holder.iv_portrait = (CircleImageView) convertView.findViewById(R.id.iv_portrait);
			holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mMemberDeteleListener != null) {
						mMemberDeteleListener.onDelete(position);
					}
				}
			});
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ImageLoader.getInstance().displayImage(
				mDatas.get(position).getMEMBERINFO().getExistAvater(), holder.iv_portrait,
				Util.getOptions_pic());
		holder.tv_name.setText(mDatas.get(position).getMEMBERINFO().getREALNAME());

		int isShowDelete = mIsDeleteStatus ? View.VISIBLE : View.GONE;
		if (position != 0 && !mDatas.get(position).isButton) {
			holder.iv_delete.setVisibility(isShowDelete);
		}else {
			holder.iv_delete.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ViewHolder {
		CircleImageView iv_portrait;
		ImageView iv_delete;
		TextView tv_name;
	}

	public void remove(int position) {
		mDatas.remove(position);
		checkHideDelete();
		notifyDataSetChanged();
	}
	
	/**
	 * 检查是否需要隐藏删除按钮
	 */
	private void checkHideDelete() {
		if (mType == GroupDetailActivity.TYPE_OWNER && getCount() == 3) {
			mDatas.remove(1);
		}
	}

	public void delete() {
		mIsDeleteStatus = !mIsDeleteStatus;
		notifyDataSetChanged();
	}

	public OnMemberDeteleListener getMemberDeteleListener() {
		return mMemberDeteleListener;
	}

	public void setMemberDeteleListener(OnMemberDeteleListener mMemberDeteleListener) {
		this.mMemberDeteleListener = mMemberDeteleListener;
	}

	public interface OnMemberDeteleListener {
		public void onDelete(int position);
	}
}
