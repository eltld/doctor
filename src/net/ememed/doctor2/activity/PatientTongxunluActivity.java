package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.PatientTongxunluAdapter;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.ContactInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PinyinComparator;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import net.ememed.doctor2.widget.SideBar;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PatientTongxunluActivity extends BasicActivity implements Callback {

	private WindowManager mWindowManager;
	private SideBar indexBar;

	private LinearLayout ll_empty;
	private LinearLayout ll_net_unavailable;
	private TextView mDialogLayout;
	private RefreshListView patient_tongxunlu_listview;
	private TextView top_title;

	public PatientTongxunluAdapter adapter;
	public Handler handler = null;
	final Context context = PatientTongxunluActivity.this;
	List<ContactEntry> list = null;

	private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			getContactList();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_tongxunlu_list);
		initView();
		// list=(List<ContactEntry>) getIntent().getSerializableExtra("tongxunlu");
		findView();
		// setData(list);
		getContactList();
		
		IntentFilter filter = new IntentFilter(ActionFilter.REQUEST_PATIENT_TONGXUNLU);
		registerReceiver(mRefreshReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mRefreshReceiver);
		super.onDestroy();
	}

	private void initView() {
		handler = new Handler(this);
		ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
		ll_net_unavailable = (LinearLayout) findViewById(R.id.ll_net_unavailable);
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setVisibility(View.VISIBLE);
		top_title.setText("患者通讯录");
		patient_tongxunlu_listview = (RefreshListView) findViewById(R.id.patient_tongxunlu_listview);
		adapter = new PatientTongxunluAdapter(this, this.imageLoader, this.options);
		patient_tongxunlu_listview.setAdapter(adapter);
		indexBar = (SideBar) findViewById(R.id.sideBar);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

	}

	private void findView() {

		// indexBar=new SideBar(this, lAdp.first);
		indexBar = (SideBar) findViewById(R.id.sideBar);
		// indexBar.setListView(lvContact);
		mDialogLayout = (TextView) LayoutInflater.from(this).inflate(
				R.layout.patient_tongxunlu_position, null);
		mDialogLayout.setVisibility(View.INVISIBLE);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogLayout, lp);

		indexBar.setTextView(mDialogLayout);
		listener();
	}

	public void getContactList() {

		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", "1");
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_contact_list,
					ContactInfo.class, params, new Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_CONTACT_LIST;
							handler.sendMessage(message);
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	public void listener() {
		patient_tongxunlu_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (!NetWorkUtils.detect(PatientTongxunluActivity.this)) {
					PatientTongxunluActivity.this
							.showToast(getString(R.string.network_unavailable));
				}
				ContactEntry item = (ContactEntry) adapter.getItem(arg2 - 1);
				Intent intent = new Intent(context, ContactInfoActivity.class);
				intent.putExtra("title", item.getREALNAME());
				intent.putExtra("tochat_userId", item.getMEMBERID());
				intent.putExtra("user_avatar", item.getAVATAR());
				intent.putExtra("is_star", item.getIS_STAR() == null ? "0" : item.getIS_STAR());
				intent.putExtra("note_name", item.getNOTE_NAME());
				intent.putExtra("description", item.getDESCRIPTION());
				startActivity(intent);
			}
		});

		// patient_tongxunlu_listview.setOnRefreshListener(new IOnRefreshListener() {
		//
		// @Override
		// public void OnRefresh() {
		// // TODO Auto-generated method stub
		// refresh();
		// }
		// });

		// 设置右侧触摸监听
		indexBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// TODO Auto-generated method stub
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					patient_tongxunlu_listview.setSelection(position);
				}
			}
		});
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub

		switch (msg.what) {
		case IResult.GET_CONTACT_LIST:
			destroyDialog();
			list = ((ContactInfo) msg.obj).getData();
			// System.out.println("---------------->   "+list);
			setData(list);
			listener();
			break;

		case IResult.DATA_ERROR:
			destroyDialog();
			patient_tongxunlu_listview.setVisibility(View.GONE);
			ll_empty.setVisibility(View.VISIBLE);
			ll_net_unavailable.setVisibility(View.GONE);
			break;

		case IResult.NET_ERROR:
			destroyDialog();
			patient_tongxunlu_listview.setVisibility(View.GONE);
			ll_empty.setVisibility(View.GONE);
			ll_net_unavailable.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
		return false;
	}

	public void setData(List<ContactEntry> list) {
		if (list == null) {
			list = new ArrayList<ContactEntry>();
		}
		Collections.sort(list, new PinyinComparator());
		adapter.setData(list);
	}

	public void doClick(View v) {
		if (v.getId() == R.id.btn_back) {
			mWindowManager.removeView(mDialogLayout);
			finish();
		}
	}
}
