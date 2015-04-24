package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.List;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.AutoCompletEditText;
import net.ememed.doctor2.widget.AutoCompletListener;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.RefreshListView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchPatientActivity extends BasicActivity implements Callback,AutoCompletListener{
	
	private TextView top_title;
	private LinearLayout ll_empty;
	private LinearLayout ll_net_unavailable;
	private RefreshListView listView;
	private AutoCompletEditText editText;
	private ContactAdapter adapter;
	private Context context=SearchPatientActivity.this;
	
	private List<ContactEntry> contacts = new ArrayList<ContactEntry>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_grouping_search);
		contacts=(List<ContactEntry>) getIntent().getSerializableExtra("patientgroup");
		initView();
	}
	
	private void initView(){
		top_title=(TextView) findViewById(R.id.top_title);
		top_title.setText("搜索患者");
		ll_empty=(LinearLayout) findViewById(R.id.ll_empty);
		ll_net_unavailable=(LinearLayout) findViewById(R.id.ll_net_unavailable);
		editText=(AutoCompletEditText) findViewById(R.id.editText);
		editText.setOnAutoCompletListener(this);
		listView=(RefreshListView) findViewById(R.id.listView);
		adapter=new ContactAdapter();
		listView.setAdapter(adapter);
		setListener();
	}
	
	private void setListener() {
		// TODO Auto-generated method stub
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (!NetWorkUtils.detect(context)) {
					((BasicActivity) context).showToast(getString(R.string.network_unavailable));
				}
				ContactEntry item = (ContactEntry) parent.getAdapter().getItem(
						arg2);
				Intent intent = new Intent(context, ContactInfoActivity.class);
				intent.putExtra("title", item.getREALNAME());
				intent.putExtra("tochat_userId", item.getMEMBERID());
				intent.putExtra("user_avatar", item.getAVATAR());
				intent.putExtra("is_star", item.getIS_STAR() == null ? "0"
						: item.getIS_STAR());
				intent.putExtra("note_name", item.getNOTE_NAME());
				intent.putExtra("description", item.getDESCRIPTION());
				startActivity(intent);
			}
		});
	}
	
	
	public List<ContactEntry> getListData(List<ContactEntry> oidlist,
			String search) {
		List<ContactEntry> list = new ArrayList<ContactEntry>();
		for (int i = 0; i < oidlist.size(); i++) {
			if(oidlist.get(i).getNOTE_NAME()!=null && oidlist.get(i).getNOTE_NAME().contains(search)){
				list.add(oidlist.get(i));
			}
			else if (oidlist.get(i).getNOTE_NAME()==null && oidlist.get(i).getREALNAME().contains(search)) {
				list.add(oidlist.get(i));
			}
		}
		return list;
	}
	
	
	

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case IResult.DATA_ERROR:
			listView.setVisibility(View.GONE);
			ll_empty.setVisibility(View.VISIBLE);
			ll_net_unavailable.setVisibility(View.GONE);
			break;
		case IResult.NET_ERROR:
			listView.setVisibility(View.GONE);
			ll_empty.setVisibility(View.GONE);
			ll_net_unavailable.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		
		return false;
	}
	public void doClick(View v){
		if(v.getId()==R.id.btn_back){
			finish();
		}
	}
	
	private class ContactAdapter extends BaseAdapter {
		List<ContactEntry> listItems;

//		public ContactAdapter(List<ContactEntry> listItems) {
//			this.listItems = listItems;
//		}

		@Override
		public int getCount() {
			return (listItems != null && listItems.size() > 0) ? listItems
					.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return (listItems != null && listItems.size() > 0) ? listItems
					.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(SearchPatientActivity.this).inflate(
						R.layout.contact_item, null);
				holder.image_person = (CircleImageView) convertView
						.findViewById(R.id.image_person);
				holder.image_person.setImageResource(R.drawable.avatar_medium);
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.tv_consult_info = (TextView) convertView
						.findViewById(R.id.tv_consult_info);
				holder.unreadLabel = (TextView) convertView
						.findViewById(R.id.unreadLabel);
				holder.iv_fans = (ImageView) convertView
						.findViewById(R.id.iv_fans);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ContactEntry entry = (ContactEntry) getItem(position);
			if (null != entry) {
				holder.tv_user_name
						.setText(entry.getNOTE_NAME() == null ? entry
								.getREALNAME() : entry.getNOTE_NAME());
				if (!TextUtils.isEmpty(entry.getISSYSTEMMSG())
						&& "1".equals(entry.getISSYSTEMMSG())) {
					holder.tv_consult_info.setText(SearchPatientActivity.this
							.getString(R.string.system_msg));
				} else {
					holder.tv_consult_info.setText(entry.getCONTENT());
				}

				SearchPatientActivity.this.imageLoader.displayImage(entry.getAVATAR(),
						holder.image_person, Util.getOptions_pic());
				// 获取与此用户/群组的会话
				EMConversation conversation = EMChatManager.getInstance()
						.getConversation(entry.getMEMBERID());
				if (conversation.getUnreadMsgCount() > 0) {
					// 显示与此用户的消息未读数
					holder.unreadLabel.setText(String.valueOf(conversation
							.getUnreadMsgCount()));
					holder.unreadLabel.setVisibility(View.VISIBLE);
				} else {
					holder.unreadLabel.setVisibility(View.INVISIBLE);
				}

				if ("1".equals(entry.getIS_MY_FANS())) {
					holder.iv_fans.setVisibility(View.VISIBLE);
				} else {
					holder.iv_fans.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		public void change(List<ContactEntry> lists) {
			if (lists == null) {
				lists = new ArrayList<ContactEntry>();
			}
			this.listItems = lists;
			notifyDataSetChanged();
		}

	}

	class ViewHolder {
		public TextView unreadLabel;
		CircleImageView image_person;
		TextView tv_user_name;
		TextView tv_consult_info;
		ImageView iv_fans;
	}

	@Override
	public void onSearch() {
		// TODO Auto-generated method stub
		String search_tx = editText.getText().toString().trim();
		if (search_tx == null || search_tx.equals("")) {
			this.showToast("搜索关键字为空");
		} else {
//			closeKeyboard(this);
			List<ContactEntry> entries = getListData(
					contacts, search_tx);
			if (entries.size() == 0) {
				this.showToast("没有数据");
				adapter.change(getListData(entries,
						search_tx));
			} else {
				adapter.change(getListData(entries,
						search_tx));

			}
		}
	}
}
