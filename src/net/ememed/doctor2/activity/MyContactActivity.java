package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.MyContactEntry;
import net.ememed.doctor2.util.TextUtil;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.RefreshListView;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyContactActivity extends BasicActivity {

	private List<MyContactEntry> contacts = new ArrayList<MyContactEntry>();
	private RefreshListView rlv_my_contact;
	private EditText et_search_key;
	private Button btn_search;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_my_contact);
	}

	
	@Override
	protected void setupView() {
		TextView title = (TextView) findViewById(R.id.top_title);
		title.setText("发起对话");
		et_search_key = (EditText) findViewById(R.id.et_search_key);
		btn_search = (Button) findViewById(R.id.btn_search);
		getContact();

		rlv_my_contact = (RefreshListView) findViewById(R.id.rlv_my_contact);
		rlv_my_contact.setAdapter(new ContactAdapter(contacts, this));
	}
	@Override
	protected void addListener() {
		/**
		 * 搜索输入框的监听
		 */
		et_search_key.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		/**
		 * 搜索按钮的监听
		 */
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	
	/**
	 * 读取手机联系人
	 */
	public void getContact() {
		MyContactEntry entry = null;
		Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		// 循环遍历
		if (cur.moveToFirst()) {
			int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);

			int displayNameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

			do {
				// 获得联系人的ID号
				String contactId = cur.getString(idColumn);
				// 获得联系人姓名
				String disPlayName = cur.getString(displayNameColumn);

				// 查看该联系人有多少个电话号码。如果没有这返回值为0
				int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (phoneCount > 0) {
					// 获得联系人的电话号码
					Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
					if (phones.moveToFirst()) {
						do {
							// 遍历所有的电话号码
							String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String phoneType = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
							if (TextUtil.isMobileNumber(phoneNumber)) {
								entry = new MyContactEntry();
								entry.setId(contactId);
								entry.setName(disPlayName);
								entry.setPhone(phoneNumber);
								entry.setType(phoneType);
								contacts.add(entry);
							}
						} while (phones.moveToNext());
					}
				}
			} while (cur.moveToNext());
		}
	}

	public void doClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			onBackClick();
			break;

		default:
			break;
		}
	}

	class ContactAdapter extends BaseAdapter {
		List<MyContactEntry> contacts;
		BasicActivity activity;

		public ContactAdapter(List<MyContactEntry> contacts, BasicActivity activity) {
			if (contacts == null)
				contacts = new ArrayList<MyContactEntry>();
			this.contacts = contacts;
			this.activity = activity;

		}

		@Override
		public int getCount() {
			return contacts.size();
		}

		@Override
		public Object getItem(int position) {
			return contacts.get(position);
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
				convertView = View.inflate(activity, R.layout.item_my_contact, null);
				holder.civ_contact_log = (CircleImageView) convertView.findViewById(R.id.civ_contact_log);
				holder.tv_contact_name = (TextView) convertView.findViewById(R.id.tv_contact_name);
				holder.tv_contact_src = (TextView) convertView.findViewById(R.id.tv_contact_src);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MyContactEntry myContactEntry = contacts.get(position);
			holder.tv_contact_name.setText(myContactEntry.getName());
			holder.tv_contact_src.setText("通讯录：" + myContactEntry.getType() + " : " + myContactEntry.getPhone());
			return convertView;
		}

	}

	class ViewHolder {
		public TextView tv_contact_src;
		public TextView tv_contact_name;
		CircleImageView civ_contact_log;
	}
}
