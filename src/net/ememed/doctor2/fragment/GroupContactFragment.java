package net.ememed.doctor2.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.EditMassMessageActivity;
import net.ememed.doctor2.activity.MassMessageActivity;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.GroupContactEntity;
import net.ememed.doctor2.entity.GroupContactEntityItem;
import net.ememed.doctor2.entity.PatientsEntity;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class GroupContactFragment extends Fragment implements OnClickListener {

	private View rootView;
	private ExpandableListView exlv_contact_group;
	private MyExpandableAdapter adapter;
	public MassMessageActivity activity;
	private LinearLayout ll_net_unavailable;
	private LinearLayout ll_empty;
	private CheckBox cb_choice_all;
	private Button btn_next;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.layout_mass_group, null);
		activity = (MassMessageActivity) getActivity();
		initView();
		return rootView;
	}

	public void initView() {
		exlv_contact_group = (ExpandableListView) rootView
				.findViewById(R.id.exlv_contact_group);

		ll_net_unavailable = (LinearLayout) rootView
				.findViewById(R.id.ll_net_unavailable);
		cb_choice_all = (CheckBox) rootView.findViewById(R.id.cb_choice_all);
		btn_next = (Button) rootView.findViewById(R.id.btn_next);
		ll_net_unavailable.setOnClickListener(this);
		cb_choice_all.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		ll_empty = (LinearLayout) rootView.findViewById(R.id.ll_empty);
		getGroupContactList();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_net_unavailable:
			getGroupContactList();
			break;
		case R.id.btn_next:
			if(adapter.getUserCount() <= 0){
				activity.showToast("请先选择联系人");
				return;
			}
			Intent intent = new Intent(getActivity(), EditMassMessageActivity.class);
			intent.putExtra("contact_mass_message", (Serializable)getContact());
			startActivity(intent);
			break;
		case R.id.cb_choice_all:
			if(cb_choice_all.isChecked()){
				adapter.setAllChecked(true);
			}else{
				adapter.setAllChecked(false);
			}
			int count = adapter.getUserCount();
			btn_next.setText("下一步("+count+")");
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * @return
	 */
	public List<ContactEntry> getContact(){
		List<GroupFlag> isChecks = adapter.getCheckList();
		List<GroupContactEntityItem> data = adapter.getListItems();
		
		List<ContactEntry> msgData = new ArrayList<ContactEntry>();
		for (int i = 0; i < data.size(); i++) {
			List<PatientsEntity> entities = data.get(i).getPATIENTS();
			for (int j = 0; j < entities.size(); j++) {
				if(isChecks.get(i).getList().get(j)){
					ContactEntry contactEntry = new ContactEntry();
					contactEntry.setAVATAR(entities.get(j).getAVATAR());
					contactEntry.setREALNAME(entities.get(j).getREALNAME());
					contactEntry.setMEMBERID(entities.get(j).getMEMBERID());
					msgData.add(contactEntry);
				}
			}
			
		}
		return msgData;
	}

	public void getGroupContactList() {
		if (NetWorkUtils.detect(activity)) {
			activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", 1 + "");

			System.out.println("分组 params = " + params);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_patients_group_list, GroupContactEntity.class,
					params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							activity.destroyDialog();
							GroupContactEntity info = (GroupContactEntity) response;
							if (null != info && info.getSuccess() == 1) {

								adapter = new MyExpandableAdapter(info
										.getData());
								exlv_contact_group.setAdapter(adapter);

							} else {
								ll_empty.setVisibility(View.VISIBLE);
								exlv_contact_group.setVisibility(View.GONE);
							}

						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							activity.destroyDialog();
							exlv_contact_group.setVisibility(View.GONE);
							ll_empty.setVisibility(View.VISIBLE);
							ll_net_unavailable.setVisibility(View.GONE);

						}
					});
		} else {
			exlv_contact_group.setVisibility(View.GONE);
			ll_net_unavailable.setVisibility(View.VISIBLE);
			ll_empty.setVisibility(View.GONE);
		}
	}

	class MyExpandableAdapter extends BaseExpandableListAdapter {

		private List<GroupContactEntityItem> data;
		private List<GroupFlag> isSelectData;

		public MyExpandableAdapter(List<GroupContactEntityItem> data) {
			isSelectData = new ArrayList<GroupFlag>();
			if (data != null) {
				for (int i = 0; i < data.size(); i++) {

					GroupFlag groupFlag = new GroupFlag();
					groupFlag.setCheck(false);
					List<Boolean> isChild = new ArrayList<Boolean>();
					for (int j = 0; j < data.get(i).getPATIENTS().size(); j++) {
						isChild.add(j, false);
					}
					groupFlag.setList(isChild);
					isSelectData.add(groupFlag);
				}
			}
			this.data = data;
		}

		@Override
		public Object getChild(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return data.get(arg0).getPATIENTS().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return arg1;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(activity).inflate(
						R.layout.item_mass_message_contact, null);
				holder.iv_photo = (CircleImageView) convertView
						.findViewById(R.id.iv_photo);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.cb_choice);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			PatientsEntity entity = data.get(groupPosition).getPATIENTS()
					.get(childPosition);
			activity.imageLoader.displayImage(entity.getAVATAR(),
					holder.iv_photo, Util.getOptions_avatar());
			holder.tv_name.setText(entity.getREALNAME());
			holder.checkBox.setChecked(isSelectData.get(groupPosition)
					.getList().get(childPosition));
			holder.checkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CheckBox checkBox = (CheckBox) v;
					isSelectData.get(groupPosition).getList()
					.set(childPosition, checkBox.isChecked());
			btn_next.setText("下一步("+getUserCount()+")");
				}
			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0).getPATIENTS().size();
		}

		@Override
		public Object getGroup(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0);
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public long getGroupId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			ViewHolder2 holder2 = null;
			if (null == convertView) {
				holder2 = new ViewHolder2();
				convertView = LayoutInflater.from(activity).inflate(
						R.layout.item_mass_message_group, null);
				holder2.tv_group_name = (TextView) convertView
						.findViewById(R.id.tv_group_name);
				holder2.checkBox = (CheckBox) convertView
						.findViewById(R.id.cb_choice);
				convertView.setTag(holder2);
			} else {
				holder2 = (ViewHolder2) convertView.getTag();
			}
			holder2.tv_group_name.setText(data.get(groupPosition)
					.getGROUPNAME());
			holder2.checkBox.setChecked(isSelectData.get(groupPosition)
					.isCheck());
			holder2.checkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CheckBox checkBox = (CheckBox) v;
					List<Boolean> child = isSelectData.get(
							groupPosition).getList();
					GroupFlag groupFlag = isSelectData.get(groupPosition);
					groupFlag.setCheck(checkBox.isChecked());
					for (int i = 0; i < child.size(); i++) {
						child.set(i, checkBox.isChecked());
					}
					btn_next.setText("下一步("+getUserCount()+")");
					notifyDataSetChanged();
				}
			});

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}

		public void setAllChecked(boolean isflag) {
			for (int i = 0; i < isSelectData.size(); i++) {
				GroupFlag groupFlag = isSelectData.get(i);
				groupFlag.setCheck(isflag);
				List<Boolean> list = groupFlag.getList();
				for (int j = 0; j < list.size(); j++) {
					list.set(j, isflag);
				}
			}
			notifyDataSetChanged();
		}
		
		public int getUserCount(){
			int count = 0;
			
			for (int i = 0; i < isSelectData.size(); i++) {
				GroupFlag groupFlag = isSelectData.get(i);
				for (int j = 0; j < groupFlag.getList().size(); j++) {
					if(groupFlag.getList().get(j)){
						count++;
					}
				}
			}
			return count;
			
		}
		
		public List<GroupContactEntityItem> getListItems(){
			
			return this.data;
		}
		
		public List<GroupFlag> getCheckList(){
			
			return this.isSelectData;
		}

	}

	class ViewHolder {
		CircleImageView iv_photo;
		TextView tv_name;
		CheckBox checkBox;
	}

	class ViewHolder2 {
		TextView tv_group_name;
		CheckBox checkBox;
	}

	class GroupFlag {

		public List<Boolean> list;
		public boolean check;

		public List<Boolean> getList() {
			return list;
		}

		public void setList(List<Boolean> list) {
			this.list = list;
		}

		public boolean isCheck() {
			return check;
		}

		public void setCheck(boolean check) {
			this.check = check;
		}

	}

}
