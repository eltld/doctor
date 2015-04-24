package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PatientEditGroupBean;
import net.ememed.doctor2.entity.PatientGroupBean;
import net.ememed.doctor2.entity.PatientGroupDataBean;
import net.ememed.doctor2.entity.PatientSetUserGroupBean;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PatientEditGroupActivity extends BasicActivity implements Callback {

	public static final int REQUEST_ADD_GROUP = 100;
	public static final int REQUEST_EDIT_GROUP = 101;

	public static int EDIT_USER_GROUP = 1; // 设置患者分组
	public static int EDIT_PATIENT_GROUP = 2; // 添加修改分组

	private boolean mIsChange = false;
	public int editType;

	private List<PatientGroupDataBean> list_group = null;
	private String userid;
	private List<String> listGroupName = null; // 保存组名
	private List<String> listGroupId = null;
	private String user_group_name;
	public int list_index;
	public int delete_index;

	private int selectItem;

	private LinearLayout ll_patient;
	private LinearLayout ll_empty;
	private LinearLayout ll_net_unavailable;
	private ListView listView;
	private TextView group_txt;
	private LinearLayout ll_edit_group;
	ImageView patient_edit_chooes_img;
	View ll_edit_list;
	View list_item;

	PatientEditGroupAdapter adapter;
	Handler handler = null;

	public boolean isedit = true; // true编辑，false完成
	public boolean isdelete = false; // true删除 ， false隐藏按钮

	private TextView top_title;
	private Button btn_edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_edit_group);
		// list_group=(List<PatientGroupDataBean>) getIntent().getSerializableExtra("group_info");
		editType = getIntent().getIntExtra("edit_type", EDIT_USER_GROUP);
		userid = getIntent().getStringExtra("userid");
		user_group_name = getIntent().getStringExtra("user_group_name");
		handler = new Handler(this);
		patientGroup();
		initView();
	}

	private void initView() {
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("分组");
		btn_edit = (Button) findViewById(R.id.btn_addhealth);
		btn_edit.setVisibility(View.VISIBLE);
		btn_edit.setBackgroundResource(android.R.color.transparent);
		btn_edit.setText("编辑");
		ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
		ll_net_unavailable = (LinearLayout) findViewById(R.id.ll_net_unavailable);
		ll_patient = (LinearLayout) findViewById(R.id.ll_patient);
		ll_edit_group = (LinearLayout) findViewById(R.id.ll_patient_edit_group);
		if (editType == EDIT_USER_GROUP) {
			btn_edit.setText("完成");
			ll_edit_group.setVisibility(View.GONE);
		}
		// setData();
		listView = (ListView) findViewById(R.id.listView);
		adapter = new PatientEditGroupAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (editType == EDIT_PATIENT_GROUP) {
					list_index = arg2;
					Intent intent = new Intent(PatientEditGroupActivity.this,
							PatientGroupAddActivity.class);
					intent.putExtra("title", "修改分组");
					intent.putExtra("groupid", list_group.get(arg2).getGROUPID());
					startActivityForResult(intent, REQUEST_EDIT_GROUP);
				} else if (editType == EDIT_USER_GROUP) {

					selectItem = arg2;
					patient_edit_chooes_img = (ImageView) arg1
							.findViewById(R.id.patient_edit_chooes_img);
					patient_edit_chooes_img.setBackgroundResource(R.drawable.ygx_btn);
					if (list_item != null) {
						patient_edit_chooes_img = (ImageView) list_item
								.findViewById(R.id.patient_edit_chooes_img);
						patient_edit_chooes_img.setBackgroundResource(R.drawable.wgx_icon);
					}
					list_item = arg1;
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	public int getGroupIndex() {
		int index = 0;
		for (int i = 0; i < list_group.size(); i++) {
			if (user_group_name != null && user_group_name.equals(list_group.get(i).getGROUPNAME())) {

				index = i;
			}
		}
		selectItem = index;
		return index;
	}

	private void refresh() {
		delete_index = -1;
		patientGroup();
	}

	private void setGroupName() {
		if (list_group != null) {
			listGroupName = new ArrayList<String>();
			listGroupId = new ArrayList<String>();
			for (int i = 0; i < list_group.size(); i++) {
				listGroupName.add(list_group.get(i).getGROUPNAME());
				listGroupId.add(list_group.get(i).getGROUPID());
			}
		}
	}

	// 获取患者分组
	public void patientGroup() {
		loading(null);
		if (NetWorkUtils.detect(this)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", "1");
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_patients_group_list,
					PatientGroupBean.class, params, new Listener() {

						@Override
						public void onResponse(Object arg0) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.obj = arg0;
							msg.what = IResult.PATIENT_GROUP;
							handler.sendMessage(msg);
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.obj = arg0;
							msg.what = IResult.DATA_ERROR;
							handler.sendMessage(msg);
						}

					});
		} else {
			destroyDialog();
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	// 设置患者分组
	public void getUserGroup(String userid, String groupid) {
		this.loading(null);
		if (NetWorkUtils.detect(this)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("userid", userid);
			params.put("groupid", groupid);
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_patient_group,
					PatientSetUserGroupBean.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							PatientEditGroupActivity.this.destroyDialog();
							PatientSetUserGroupBean psygb = (PatientSetUserGroupBean) response;
							if (psygb.getSuccess().equals("1")) {
								destroyDialog();
							}
							showToast(psygb.getErrormsg());
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							PatientEditGroupActivity.this.destroyDialog();
							handler.sendEmptyMessage(IResult.DATA_ERROR);
						}
					});
		} else {
			destroyDialog();
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	// 删除患者分组
	public void getEditGroup(String groupid) {
		this.loading(null);
		if (NetWorkUtils.detect(this)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("groupid", groupid);
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.delete_patients_group,
					PatientEditGroupBean.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							PatientEditGroupActivity.this.destroyDialog();
							PatientEditGroupBean pegb = (PatientEditGroupBean) response;
							if (pegb.getSuccess().equals("1")) {
								destroyDialog();
							}
							showToast(pegb.getErrormsg());
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							PatientEditGroupActivity.this.destroyDialog();
							handler.sendEmptyMessage(IResult.DATA_ERROR);
						}
					});
		} else {
			destroyDialog();
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	public void doClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.ll_patient_edit_group) {
			Intent intent = new Intent(this, PatientGroupAddActivity.class);
			intent.putExtra("title", "添加分组");
			intent.putExtra("groupid", "");
			startActivityForResult(intent, REQUEST_ADD_GROUP);
		} else if (v.getId() == R.id.btn_addhealth) {
			if (editType == EDIT_USER_GROUP) {
				getUserGroup(userid, list_group.get(selectItem).getGROUPID());
				finish();
			} else if (editType == EDIT_PATIENT_GROUP) {
				if (isedit) {
					btn_edit.setText("完成");
					isedit = false;
					adapter.notifyDataSetChanged();
				} else {
					btn_edit.setText("编辑");
					delete_index = -1;
					isedit = true;
					adapter.notifyDataSetChanged();
				}
			}
		} else if (v.getId() == R.id.btn_back) {
			setResult(mIsChange == true ? RESULT_OK : RESULT_CANCELED);
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_ADD_GROUP || requestCode == REQUEST_EDIT_GROUP) {
			mIsChange = true;
			refresh();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case IResult.PATIENT_GROUP:
			destroyDialog();
			list_group = ((PatientGroupBean) msg.obj).getData();
			// adapter.setData(list_group);
			if (list_group != null) {
				ll_patient.setVisibility(View.VISIBLE);
				ll_empty.setVisibility(View.GONE);
				ll_net_unavailable.setVisibility(View.GONE);
				setGroupName();
				adapter.setData(listGroupName, listGroupId);
				listView.setSelection(getGroupIndex());
			}
			break;

		case IResult.DATA_ERROR:
			ll_patient.setVisibility(View.GONE);
			ll_empty.setVisibility(View.VISIBLE);
			ll_net_unavailable.setVisibility(View.GONE);
			break;

		case IResult.NET_ERROR:
			ll_patient.setVisibility(View.GONE);
			ll_empty.setVisibility(View.GONE);
			ll_net_unavailable.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}

		return false;
	}

	class PatientEditGroupAdapter extends BaseAdapter {
		View oldView;
		List<PatientGroupDataBean> list_group = null;
		List<String> listGroupName = null; // 保存组名
		List<String> listGroupId = null;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// if(list_group==null){
			// return 0;
			// }
			// return list_group.size();
			if (listGroupName == null) {
				return 0;
			}
			return listGroupName.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listGroupName.get(arg0);
			// return list_group.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub

			final int listIndex = arg0;
			ll_edit_list = View.inflate(PatientEditGroupActivity.this,
					R.layout.patient_edit_group_item, null);
			group_txt = (TextView) ll_edit_list.findViewById(R.id.patient_edit_txt);
			group_txt.setText(listGroupName.get(arg0));
			// group_txt.setText(list_group.get(arg0).getGROUPNAME());
			Button delete_image = (Button) ll_edit_list.findViewById(R.id.patient_edit_chooes);
			ImageView patient_img = (ImageView) ll_edit_list
					.findViewById(R.id.patient_edit_chooes_img);
			delete_image.setBackgroundResource(R.drawable.del_btn);
			if (editType == EDIT_PATIENT_GROUP) {
				if (!isedit) {
					delete_image.setVisibility(View.VISIBLE);
					patient_img.setVisibility(View.GONE);
				} else {
					delete_image.setVisibility(View.GONE);
					patient_img.setVisibility(View.GONE);
				}
			} else {
				patient_edit_chooes_img = (ImageView) ll_edit_list
						.findViewById(R.id.patient_edit_chooes_img);
				if (arg0 == selectItem) {
					patient_edit_chooes_img.setBackgroundResource(R.drawable.ygx_btn);
				}
			}

			final Button delete_btn = (Button) ll_edit_list.findViewById(R.id.patient_edit_delete);
			delete_image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					notifyDataSetChanged();
					delete_index = listIndex;
					if (isdelete) {
						isdelete = false;
					} else {
						isdelete = true;
					}
				}
			});
			if (isdelete) {
				if (delete_index == arg0) {
					delete_btn.setVisibility(8 - delete_btn.getVisibility());
				}
			}
			delete_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// getEditGroup(list_group.get(listIndex).getGROUPID());
					mIsChange = true;
					getEditGroup(listGroupId.get(listIndex));
					delete_index = -1;
					listGroupName.remove(listIndex);
					listGroupId.remove(listIndex);
					notifyDataSetChanged();
				}
			});
			return ll_edit_list;
		}

		public void setData(List<String> listGroupName, List<String> listGroupId) {
			this.listGroupName = listGroupName;
			this.listGroupId = listGroupId;
			notifyDataSetChanged();
		}

	}

}
