package net.ememed.doctor2.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.PatientGroupAllExListAdapter;
import net.ememed.doctor2.activity.adapter.PatientGroupExListAdapter;
import net.ememed.doctor2.activity.adapter.PatientPopAdapter;
import net.ememed.doctor2.activity.adapter.PatientTongxunluAdapter;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.ContactInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PatientGroupBean;
import net.ememed.doctor2.entity.PatientGroupDataBean;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshExpandableListView;
import net.ememed.doctor2.widget.RefreshExpandableListView.IOnRefreshListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PatientGroupActivity extends BasicActivity implements Callback,
		OnChildClickListener{
	
	private static final int REQUEST_EDIT_GROUP = 100;
	private TextView top_title;
	private ImageView iv_right_fun;
	private PopupWindow popup;
	// public ExpandableListView listView;
	public RefreshExpandableListView listViewAll;
	public PatientGroupAllExListAdapter allAdapter;
	private PullToRefreshLayout mPullToRefreshLayout;
	
	private LinearLayout ll_empty;
	private LinearLayout ll_net_unavailable;
	// public PatientGroupExListAdapter adapter;

	public List<String> group;
	public String[] child;
	public List<String[]> childList = null;
	public List<List<String[]>> childAllList = null;
	public String childAva;
	public String childName;
	public String childCon;
	public Handler handler = null;
	PatientGroupBean pgb;
	List<ContactEntry> ce=null;
	List<PatientGroupDataBean> list_group=null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_grouping);
		handler = new Handler(this);
//		ce = (List<ContactEntry>) getIntent().getSerializableExtra(
//				"patientgroup");
		initView();
	}

	private void initView() {
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("患者组");
		iv_right_fun = (ImageView) findViewById(R.id.iv_right_fun);
		iv_right_fun.setImageResource(R.drawable.menu_icon);
		iv_right_fun.setVisibility(View.VISIBLE);
		ll_empty=(LinearLayout) findViewById(R.id.ll_empty);
		ll_net_unavailable=(LinearLayout) findViewById(R.id.ll_net_unavailable);
		// listView = (ExpandableListView)
		// findViewById(R.id.patient_grouping_list);
		// listView.setOnChildClickListener(this);
		mPullToRefreshLayout = (PullToRefreshLayout) 
				findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
		.setup(mPullToRefreshLayout);
		if (null != mPullToRefreshLayout && mPullToRefreshLayout.isRefreshing()) {
			mPullToRefreshLayout.setRefreshComplete();
		}
		allAdapter = new PatientGroupAllExListAdapter(this, imageLoader,
				options);
		listViewAll = (RefreshExpandableListView) findViewById(R.id.patient_grouping_all_list);
		listViewAll.setOnChildClickListener(this);
		listener();
		patientGroup();
		getContactList();
		
	}
	
	private void listener(){
		listViewAll.setOnRefreshListener(new IOnRefreshListener() {
			
			@Override
			public void OnRefresh() {
				// TODO Auto-generated method stub
				refresh();
			}
		});
		ll_net_unavailable.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				refresh();
			}
		});
	}

	public void doClick(View v) {
		if (v.getId() == R.id.btn_back) {
			finish();
		} else if (v.getId() == R.id.iv_right_fun) {
			if (popup != null) {
				popup.showAsDropDown(iv_right_fun, -155, 13);
			}
		}
	}

	//获取所有联系人
	private void getContactList() {

		if (NetWorkUtils.detect(this)) {
			this.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", "1");
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_contact_list, ContactInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_CONTACT_LIST;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
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
	
	//获取所有分组
	public void patientGroup() {
		loading(null);
		if (NetWorkUtils.detect(this)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", "1");
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_patients_group_list, PatientGroupBean.class,
					params, new Listener() {

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
		}
	}

	private void initPupupWindow() {
		LinearLayout ll_menu = (LinearLayout) getLayoutInflater().inflate(
				R.layout.popupwindow_patient_grouping, null);
		// 所有
		LinearLayout ll_patient_group_all = (LinearLayout) ll_menu
				.findViewById(R.id.ll_patient_group_all);
		TextView patient_all_count = (TextView) ll_menu
				.findViewById(R.id.patient_all_count);
		if (ce != null) {
			patient_all_count.setVisibility(View.VISIBLE);
			patient_all_count.setText("[" + ce.size() + "]");
		}
		// 收藏
		LinearLayout ll_patient_group_collect = (LinearLayout) ll_menu
				.findViewById(R.id.ll_patient_group_collect);
		TextView patient_collect_count = (TextView) ll_menu
				.findViewById(R.id.patient_collect_count);
		if (childAllList!= null && childAllList.toString()!="" && !childAllList.equals(null) && childAllList.size()!=0) {
			patient_collect_count.setVisibility(View.VISIBLE);
			patient_collect_count.setText("[" + childAllList.get(1).size() + "]");
		}
		// 我的分组
		ListView listView = (ListView) ll_menu.findViewById(R.id.listView);
		PatientPopAdapter adapter = new PatientPopAdapter(this);
		adapter.setData(list_group);
		listView.setAdapter(adapter);
		// 历史患者
//		LinearLayout ll_patient_group_history = (LinearLayout) ll_menu
//				.findViewById(R.id.ll_patient_group_history);
//		TextView patient_hostory_count = (TextView) ll_menu
//				.findViewById(R.id.patient_hostory_count);
		// 编辑分组
		LinearLayout ll_patient_group_redact = (LinearLayout) ll_menu
				.findViewById(R.id.ll_patient_group_redact);

		// 所有
		ll_patient_group_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != popup) {
					popup.dismiss();
					childAllList.clear();
					childAllList = new ArrayList<List<String[]>>();
					group.clear();
					group = new ArrayList<String>();

					if (!list_group.equals("")) {
						group.add("所有");
						childList.clear();
						childList = new ArrayList<String[]>();
						for (int j = 0; j < ce.size(); j++) {

							child = new String[] { ce.get(j).getAVATAR(),
									ce.get(j).getREALNAME(),
									ce.get(j).getCONTENT(),
									ce.get(j).getMEMBERID(),
									ce.get(j).getIS_STAR(),
									ce.get(j).getNOTE_NAME(),
									ce.get(j).getDESCRIPTION() };
							childList.add(child);
						}

						childAllList.add(childList);
					}
					allAdapter = new PatientGroupAllExListAdapter(
							PatientGroupActivity.this, imageLoader, options);
					allAdapter.setData(group, childAllList);
					listViewAll.setAdapter(allAdapter);
					listViewAll.expandGroup(0);
				}
			}
		});
		// 收藏
		ll_patient_group_collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != popup) {
					popup.dismiss();
					childAllList.clear();
					childAllList = new ArrayList<List<String[]>>();
					group.clear();
					group = new ArrayList<String>();

					if (!list_group.equals("")) {
						group.add("收藏");
						childList.clear();
						childList = new ArrayList<String[]>();
						for (int j = 0; j < ce.size(); j++) {
							if (ce.get(j).getIS_STAR() != null
									&& ce.get(j).getIS_STAR().equals("1")) {
								child = new String[] { ce.get(j).getAVATAR(),
										ce.get(j).getREALNAME(),
										ce.get(j).getCONTENT(),
										ce.get(j).getMEMBERID(),
										ce.get(j).getIS_STAR(),
										ce.get(j).getNOTE_NAME(),
										ce.get(j).getDESCRIPTION() };
								childList.add(child);
							}

						}

						childAllList.add(childList);
					}
					allAdapter = new PatientGroupAllExListAdapter(
							PatientGroupActivity.this, imageLoader, options);
					allAdapter.setData(group, childAllList);
					listViewAll.setAdapter(allAdapter);
					listViewAll.expandGroup(0);
				}
			}
		});
		// 我的分组
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int i,
					long arg3) {
				// TODO Auto-generated method stub
				if (null != popup) {
					popup.dismiss();
					childAllList = new ArrayList<List<String[]>>();
					group = new ArrayList<String>();

					if (!list_group.equals("")) {

						group.add(list_group.get(i).getGROUPNAME());

						childList = new ArrayList<String[]>();

						for (int j = 0; j < list_group.get(i).getPATIENTS()
								.size(); j++) {
							child = new String[] {
									list_group.get(i).getPATIENTS().get(j)
											.getAVATAR(),
									list_group.get(i).getPATIENTS().get(j)
											.getREALNAME(),
									list_group.get(i).getPATIENTS().get(j)
											.getCONTENT(),
									list_group.get(i).getPATIENTS().get(j)
											.getMEMBERID(),
									list_group.get(i).getPATIENTS().get(j) // 4
											.getIS_STAR(),
									list_group.get(i).getPATIENTS().get(j) // 5
											.getNOTE_NAME(),
									list_group.get(i).getPATIENTS().get(j) // 6
											.getDESCRIPTION() };
							childList.add(child);
						}
						childAllList.add(childList);

					}
					allAdapter = new PatientGroupAllExListAdapter(
							PatientGroupActivity.this, imageLoader, options);
					allAdapter.setData(group, childAllList);
					listViewAll.setAdapter(allAdapter);
					listViewAll.expandGroup(0);
				}
			}
		});
		// 历史患者
//		ll_patient_group_history.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (null != popup) {
//					popup.dismiss();
//					//
//				}
//			}
//		});
		// 编辑分组
		ll_patient_group_redact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != popup) {
					popup.dismiss();
					Intent intent = new Intent(PatientGroupActivity.this,
							PatientEditGroupActivity.class);
					intent.putExtra("group_info", (Serializable) list_group);
					intent.putExtra("edit_type",
							PatientEditGroupActivity.EDIT_PATIENT_GROUP);
					startActivityForResult(intent, REQUEST_EDIT_GROUP);
				}
			}
		});

		
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) listView.getLayoutParams();
		if(list_group.size()>=6){
			linearParams.height=MyApplication.getInstance().canvasHeight *3/10;
		}
		
		popup = new PopupWindow(ll_menu,
				MyApplication.getInstance().canvasWidth * 7 / 20,
				LayoutParams.WRAP_CONTENT);
		popup.setOutsideTouchable(true);
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popup.setFocusable(true);
	}

	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case IResult.PATIENT_GROUP:
			listViewAll.onRefreshComplete();
			mPullToRefreshLayout.setRefreshComplete();
			pgb = (PatientGroupBean) msg.obj;
			if(pgb!=null ){
				listViewAll.setVisibility(View.VISIBLE);
				ll_empty.setVisibility(View.GONE);
				ll_net_unavailable.setVisibility(View.GONE);
				list_group = pgb.getData();
				if(ce!=null){
					destroyDialog();
					setGroupData(list_group,ce);
					initPupupWindow();
				}
			}
			break;
		case IResult.GET_CONTACT_LIST:
				ce=((ContactInfo) msg.obj).getData();
				if(ce!=null){
					if(list_group!=null){
						destroyDialog();
						setGroupData(list_group,ce);
						initPupupWindow();
					}
				}
				
			break;
		case IResult.DATA_ERROR:
			destroyDialog();
			listViewAll.setVisibility(View.GONE);
			ll_empty.setVisibility(View.VISIBLE);
			ll_net_unavailable.setVisibility(View.GONE);
			break;
			
		case IResult.NET_ERROR:
			destroyDialog();
			listViewAll.setVisibility(View.GONE);
			ll_empty.setVisibility(View.GONE);
			ll_net_unavailable.setVisibility(View.VISIBLE);
			break;
		default:

			break;
		}

		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_EDIT_GROUP) {
			refresh();
		}
	}
	
	
	public void setGroupData(List<PatientGroupDataBean> list_group,List<ContactEntry> ce) {
		childAllList = new ArrayList<List<String[]>>();
		group = new ArrayList<String>();

		if (list_group!=null) {
			group.add("所有");
			group.add("收藏");
			for (int i = 0; i < list_group.size() + 2; i++) {
				if (i != list_group.size() && i!=list_group.size()+1) {
					group.add(list_group.get(i).getGROUPNAME());
				}
				childList = new ArrayList<String[]>();
				if (i == 0) {
					for (int j = 0; j < ce.size(); j++) {

						child = new String[] { ce.get(j).getAVATAR(),
								ce.get(j).getREALNAME(),
								ce.get(j).getCONTENT(),
								ce.get(j).getMEMBERID(),
								ce.get(j).getIS_STAR(),
								ce.get(j).getNOTE_NAME(),
								ce.get(j).getDESCRIPTION() };
						childList.add(child);
					}
				} else if(i==1){
					for (int j = 0; j < ce.size(); j++) {
						if (ce.get(j).getIS_STAR() != null
								&& ce.get(j).getIS_STAR().equals("1")) {
							child = new String[] { ce.get(j).getAVATAR(),
									ce.get(j).getREALNAME(),
									ce.get(j).getCONTENT(),
									ce.get(j).getMEMBERID(),
									ce.get(j).getIS_STAR(),
									ce.get(j).getNOTE_NAME(),
									ce.get(j).getDESCRIPTION() };
							childList.add(child);
						}

					}
				}
				else {
					if(list_group.get(i - 2).getPATIENTS()!=null){
						for (int j = 0; j < list_group.get(i - 2).getPATIENTS()
								.size(); j++) {
							child = new String[] {
									list_group.get(i - 2).getPATIENTS().get(j) // 0
									.getAVATAR(),
									list_group.get(i - 2).getPATIENTS().get(j) // 1
									.getREALNAME(),
									list_group.get(i - 2).getPATIENTS().get(j) // 2
									.getCONTENT(),
									list_group.get(i - 2).getPATIENTS().get(j) // 3
									.getMEMBERID(),
									list_group.get(i - 2).getPATIENTS().get(j) // 4
									.getIS_STAR(),
									list_group.get(i - 2).getPATIENTS().get(j) // 5
									.getNOTE_NAME(),
									list_group.get(i - 2).getPATIENTS().get(j) // 6
									.getDESCRIPTION() };
							childList.add(child);
						}
					}
				}
				childAllList.add(childList);
			}
		}
		allAdapter.setData(group, childAllList);
		listViewAll.setAdapter(allAdapter);
	}

	private void refresh() {
		getContactList();
		patientGroup();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		// Intent intent = new Intent(PatientGroupActivity.this,
		// PatientEditGroupActivity.class);
		// intent.putExtra("group_info", (Serializable) list_group);
		// // intent.putExtra("userid",
		// //
		// pgb.getData().get(groupPosition).getPATIENTS().get(childPosition).getMEMBERID());
		// intent.putExtra("userid",
		// childAllList.get(groupPosition).get(childPosition)[3]);
		// intent.putExtra("edit_type",
		// PatientEditGroupActivity.EDIT_USER_GROUP);
		// startActivity(intent);
		Intent intent = new Intent(this, ContactInfoActivity.class);
		intent.putExtra("title",
				childAllList.get(groupPosition).get(childPosition)[1]);
		intent.putExtra("tochat_userId",
				childAllList.get(groupPosition).get(childPosition)[3]);
		intent.putExtra("user_avatar",
				childAllList.get(groupPosition).get(childPosition)[0]);
		intent.putExtra(
				"is_star",
				childAllList.get(groupPosition).get(childPosition)[4] == null ? "0"
						: childAllList.get(groupPosition).get(childPosition)[4]);
		intent.putExtra("note_name",
				childAllList.get(groupPosition).get(childPosition)[5]);
		intent.putExtra("description",
				childAllList.get(groupPosition).get(childPosition)[6]);
		startActivity(intent);
		return false;
	}
}
