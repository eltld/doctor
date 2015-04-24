package net.ememed.doctor2.baike.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.ContactInfoActivity;
import net.ememed.doctor2.activity.MainActivity;
import net.ememed.doctor2.baike.BaikeHomeActivity;
import net.ememed.doctor2.baike.OtherBaikeActivity;
import net.ememed.doctor2.baike.SayDetailsActivity;
import net.ememed.doctor2.baike.adapter.RelateMeAdapter;
import net.ememed.doctor2.baike.entity.BaikeShuoshuoInfo;
import net.ememed.doctor2.baike.entity.MyBaikeEntry;
import net.ememed.doctor2.baike.entity.RelateMeEntry;
import net.ememed.doctor2.baike.entity.RelateMeInfo;
import net.ememed.doctor2.baike.entity.SaysListEntry;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.ShareSdkUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

public class BaikeRelateMeFragment extends Fragment  implements Callback, OnClickListener{
	private final int BAIKE_SELECTOR_ALL = 0; 
	private final int BAIKE_SELECTOR_PRAISE = 1; 
	private final int BAIKE_SELECTOR_COMMENT = 2; 
	private final int BAIKE_SELECTOR_SHARE = 3; 
	private static final String FROMAT_ALL = "总共%S条消息";
	private static final String FROMAT_PRAISE = "赞我的总共%S条";
	private static final String FROMAT_COMMENT = "评论我的总共%S条";
	private static final String FROMAT_SHARE = "分享我的总共%S条";
	
	private BaikeHomeActivity activity = null;
	private Handler mHandler = null;
	
	private ImageView btn_back;
	private LinearLayout ll_choice;
	private TextView top_title;
	private ImageView iv_down_arrow;
	private FrameLayout fl_top_title;
	private TextView tv_total_num;
	
	private String delete_comment_id = null;
	private AlertDialog myDialog;
	
	private PopupWindow popup;
	private SelectorAdapter mAdapter;
	private ArrayList<String> selectors;
	private int currentSelector = 0;
	private boolean isRefresh = true;
	private RefreshListView lv_all, lv_praise, lv_comment, lv_share;
	private RelateMeAdapter adapter_all, adapter_praise, adapter_comment, adapter_share;
	private List<RelateMeInfo> list_all = null, list_praise = null, list_comment = null, list_share = null;
	private int page_all = 1, page_praise = 1, page_comment = 1, page_share = 1;
	private int count_all = -1, count_praise = -1, count_comment = -1, count_share = -1;
	
	public BaikeRelateMeFragment(){
		this.activity = (BaikeHomeActivity) getActivity();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (BaikeHomeActivity) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler(this);
//		list_all = new ArrayList<RelateMeInfo>();
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_baike_relate_me, null);
		
		setupView(view);
		addListener();
		return view;
	}
	
	private void setupView(View view){
		btn_back = (ImageView) view.findViewById(R.id.btn_back);
		fl_top_title = (FrameLayout) view.findViewById(R.id.fl_top_title);
		ll_choice = (LinearLayout) view.findViewById(R.id.ll_choice);
		top_title = (TextView) view.findViewById(R.id.top_title);
		top_title.setText(getString(R.string.baike_selector_all));
		iv_down_arrow = (ImageView) view.findViewById(R.id.iv_down_arrow);
		TextView tv_menu = (TextView) view.findViewById(R.id.tv_menu);
		tv_menu.setVisibility(View.GONE);
		
		tv_total_num = (TextView) view.findViewById(R.id.tv_total_num);
		tv_total_num.setVisibility(View.GONE);
		
		lv_all = (RefreshListView) view.findViewById(R.id.lv_all);
		adapter_all = new RelateMeAdapter(activity, mHandler, null);
		lv_all.setAdapter(adapter_all);
		
		lv_praise = (RefreshListView) view.findViewById(R.id.lv_praise);
		adapter_praise = new RelateMeAdapter(activity, mHandler, null);
		lv_praise.setAdapter(adapter_praise);
		
		lv_comment = (RefreshListView) view.findViewById(R.id.lv_comment);
		adapter_comment = new RelateMeAdapter(activity, mHandler, null);
		lv_comment.setAdapter(adapter_comment);

		lv_share = (RefreshListView) view.findViewById(R.id.lv_share);
		adapter_share = new RelateMeAdapter(activity, mHandler, null);
		lv_share.setAdapter(adapter_share);
		
		initPopUp();
	}
	
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			getRelateMeList(currentSelector);
		} else {
			MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_relateme_list);
			MyApplication.volleyHttpClient.cancelRequest(HttpUtil.delete_comment);
		}
	}
	
	private boolean needLoadingCircle(){
		if(BAIKE_SELECTOR_ALL == currentSelector && null == list_all){
			return true;
		} else if(BAIKE_SELECTOR_PRAISE == currentSelector && null == list_praise){
			return true;
		} else if(BAIKE_SELECTOR_COMMENT == currentSelector && null == list_comment){
			return true;
		} else if(BAIKE_SELECTOR_SHARE == currentSelector && null == list_share){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @param type （0所有消息，1赞我的，2评论我的，3分享我的）
	 */
	private void getRelateMeList(int type) {
		if (NetWorkUtils.detect(activity)) {
			if(needLoadingCircle()){
				activity.loading(null);
			}
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("type", ""+type);
			if(0 == type){
				params.put("page", ""+ page_all);
			} else if(1 == type){
				params.put("page", ""+ page_praise);
			} else if(2 == type){
				params.put("page", ""+ page_comment);
			} else if(3 == type){
				params.put("page", ""+ page_share);
			}

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_relateme_list, RelateMeEntry.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_RELATEME_LIST;
							mHandler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							mHandler.sendMessage(message);
						}
					});
		} else {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
	
	private void delelteComment(String commentid){
		if (NetWorkUtils.detect(activity)) {
			activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("commentid", commentid);
		
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.delete_comment, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.DELETE_COMMENT;
							mHandler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							mHandler.sendMessage(message);
						}
					});
		} else {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
	
	
	@Override
	public boolean handleMessage(Message msg) {
		try {
			activity.destroyDialog();
			lv_all.onRefreshComplete();
			lv_praise.onRefreshComplete();
			lv_comment.onRefreshComplete();
			lv_share.onRefreshComplete();
			
			switch (msg.what) {
			case IResult.GET_RELATEME_LIST:
				RelateMeEntry entry = (RelateMeEntry) msg.obj;
				if (null != entry && entry.getSuccess() == 1) {
					if(BAIKE_SELECTOR_ALL == currentSelector){
						count_all = entry.getCount();
						tv_total_num.setVisibility(View.VISIBLE);
						setTotalNum(BAIKE_SELECTOR_ALL, count_all);
						if(isRefresh){
							list_all = entry.getData();
						} else {
							list_all.addAll(entry.getData());
						}
						
						adapter_all.change(list_all);
						
						if(entry.getPages() <= page_all){
							lv_all.onLoadMoreComplete(true);
						} else {
							lv_all.onLoadMoreComplete(false);
						}
					} else if(BAIKE_SELECTOR_PRAISE == currentSelector){
						count_praise = entry.getCount();
						tv_total_num.setVisibility(View.VISIBLE);
						setTotalNum(BAIKE_SELECTOR_PRAISE, count_praise);
						if(isRefresh){
							list_praise = entry.getData();
						} else {
							list_praise.addAll(entry.getData());
						}
						
						adapter_praise.change(list_praise);
						
						if(entry.getPages() <= page_praise){
							lv_praise.onLoadMoreComplete(true);
						} else {
							lv_praise.onLoadMoreComplete(false);
						}
					} else if(BAIKE_SELECTOR_COMMENT == currentSelector){
						count_comment = entry.getCount();
						tv_total_num.setVisibility(View.VISIBLE);
						setTotalNum(BAIKE_SELECTOR_COMMENT, count_comment);
						if(isRefresh){
							list_comment = entry.getData();
						} else {
							list_comment.addAll(entry.getData());
						}
						
						adapter_comment.change(list_comment);
						
						if(entry.getPages() <= page_comment){
							lv_comment.onLoadMoreComplete(true);
						} else {
							lv_comment.onLoadMoreComplete(false);
						}
					} else if(BAIKE_SELECTOR_SHARE == currentSelector){
						count_share = entry.getCount();
						tv_total_num.setVisibility(View.VISIBLE);
						setTotalNum(BAIKE_SELECTOR_SHARE, count_share);
						if(isRefresh){
							list_share = entry.getData();
						} else {
							list_share.addAll(entry.getData());
						}
						
						adapter_share.change(list_share);
						
						if(entry.getPages() <= page_share){
							lv_share.onLoadMoreComplete(true);
						} else {
							lv_share.onLoadMoreComplete(false);
						}
					}
				}
				break;
			case IResult.DELETE_COMMENT_INNER:
				delete_comment_id = ""+msg.arg1;
				showAlertDialogue();
				break;
			case IResult.DELETE_COMMENT:
				activity.destroyDialog();
				CommonResponseEntity response = (CommonResponseEntity) msg.obj;
				if (null != response) {
					if (1 == response.getSuccess()) {
						activity.showToast("删除成功");
						// 更新界面
						if(BAIKE_SELECTOR_ALL == currentSelector){
							for (int i = 0; i < list_all.size(); i++) {
								if (list_all.get(i).getTBTYPE().equals("2") && list_all.get(i).getCONTENTID().equals(delete_comment_id)) {
									list_all.remove(i);
									break;
								}
							}
							
							adapter_all.change(list_all);
						} else if(BAIKE_SELECTOR_COMMENT == currentSelector){
							for (int i = 0; i < list_comment.size(); i++) {
								if (list_comment.get(i).getCONTENTID().equals(delete_comment_id)) {
									list_comment.remove(i);
									break;
								}
							}
							
							adapter_comment.change(list_comment);
						}
						
						delete_comment_id = null;
					} else {
						activity.showToast(response.getErrormsg());
					}
				}
				break;
			case IResult.DATA_ERROR:
				activity.showToast("数据异常");
				Log.i("测试", msg.obj.toString());
				break;
			case IResult.NET_ERROR:
				activity.showToast("网络异常");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	private void setTotalNum(int type, int num){
		if(BAIKE_SELECTOR_ALL == type){
			tv_total_num.setText(String.format(FROMAT_ALL, ""+num));
		} else if(BAIKE_SELECTOR_PRAISE == type){
			tv_total_num.setText(String.format(FROMAT_PRAISE, ""+num));
		}else if(BAIKE_SELECTOR_COMMENT == type){
			tv_total_num.setText(String.format(FROMAT_COMMENT, ""+num));
		}else if(BAIKE_SELECTOR_SHARE == type){
			tv_total_num.setText(String.format(FROMAT_SHARE, ""+num));
		}
	}
	
	/**
	 * 初始化删除信息的提示框
	 */
	private void showAlertDialogue() {
		if (null == myDialog) {
			myDialog = new AlertDialog.Builder(activity).create();
			View view = LayoutInflater.from(activity).inflate(R.layout.message_list_alert_dialog, null);

			LayoutParams lps = view.getLayoutParams();

			if (lps == null) {
				lps = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
			lps.width = MyApplication.getInstance().canvasWidth * 3 / 4;
			lps.height = LayoutParams.WRAP_CONTENT;
			view.setLayoutParams(lps);

			myDialog.setCanceledOnTouchOutside(true);
			Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
			Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
			btn_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					myDialog.dismiss();
					delete_comment_id = null;
				}
			});

			btn_confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					delelteComment(delete_comment_id);
					myDialog.dismiss();
				}
			});
			myDialog.setView(view);
		}

		myDialog.show();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ll_choice:
			if(popup!=null){
				popup.showAsDropDown(fl_top_title, 0, 0);
				iv_down_arrow.setSelected(true);
			}
			break;
		case R.id.btn_back:
			activity.finish();
			break;
		}
		
	}
	
	private void initPopUp(){
		ListView lv = new ListView(activity);
		
		lv.setBackgroundColor(getResources().getColor(R.color.reseda));
		mAdapter = new SelectorAdapter();
		
		lv.setVerticalScrollBarEnabled(true);
		lv.setDivider(new ColorDrawable(Color.GRAY));
		lv.setDividerHeight(1);
		lv.setAdapter(mAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String choice = (String) arg0.getAdapter().getItem(arg2);
				popup.dismiss();
				top_title.setText(choice);
				if(choice.equals(getString(R.string.baike_selector_all))){
					if(BAIKE_SELECTOR_ALL != currentSelector){
						currentSelector = BAIKE_SELECTOR_ALL;
						lv_all.setVisibility(View.VISIBLE);
						lv_praise.setVisibility(View.GONE);
						lv_comment.setVisibility(View.GONE);
						lv_share.setVisibility(View.GONE);
						if(null == list_all){
							list_all = new ArrayList<RelateMeInfo>();
							tv_total_num.setVisibility(View.GONE);
							getRelateMeList(0);
						} else {
							tv_total_num.setVisibility(View.VISIBLE);
							setTotalNum(BAIKE_SELECTOR_ALL, count_all);
						}
					} 
				} else if(choice.equals(getString(R.string.baike_selector_praise))){
					if(BAIKE_SELECTOR_PRAISE != currentSelector){
						currentSelector = BAIKE_SELECTOR_PRAISE;
						lv_all.setVisibility(View.GONE);
						lv_praise.setVisibility(View.VISIBLE);
						lv_comment.setVisibility(View.GONE);
						lv_share.setVisibility(View.GONE);
						if(null == list_praise){
//							list_praise = new ArrayList<RelateMeInfo>();
							tv_total_num.setVisibility(View.GONE);
							getRelateMeList(1);
						} else {
							tv_total_num.setVisibility(View.VISIBLE);
							setTotalNum(BAIKE_SELECTOR_PRAISE, count_praise);
						}
					
					} 
				} else if(choice.equals(getString(R.string.baike_selector_comment))){
					if(BAIKE_SELECTOR_COMMENT != currentSelector){
						currentSelector = BAIKE_SELECTOR_COMMENT;
						lv_all.setVisibility(View.GONE);
						lv_praise.setVisibility(View.GONE);
						lv_comment.setVisibility(View.VISIBLE);
						lv_share.setVisibility(View.GONE);
						if(null == list_comment){
//							list_comment = new ArrayList<RelateMeInfo>();
							tv_total_num.setVisibility(View.GONE);
							getRelateMeList(2);
						} else {
							tv_total_num.setVisibility(View.VISIBLE);
							setTotalNum(BAIKE_SELECTOR_COMMENT, count_comment);
						}
					}		
				} else if(choice.equals(getString(R.string.baike_selector_share))){
					if(BAIKE_SELECTOR_SHARE != currentSelector){
						currentSelector = BAIKE_SELECTOR_SHARE;
						lv_all.setVisibility(View.GONE);
						lv_praise.setVisibility(View.GONE);
						lv_comment.setVisibility(View.GONE);
						lv_share.setVisibility(View.VISIBLE);
						if(null == list_share){
//							list_share = new ArrayList<RelateMeInfo>();
							tv_total_num.setVisibility(View.GONE);
							getRelateMeList(3);
						} else {
							tv_total_num.setVisibility(View.VISIBLE);
							setTotalNum(BAIKE_SELECTOR_SHARE, count_share);
						}
					}
				}
			}
		});
		
		popup = new PopupWindow(lv, MyApplication.getInstance().canvasWidth, Util.dip2px(activity, 250));
		popup.setOutsideTouchable(true);
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popup.setFocusable(true);
		popup.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				iv_down_arrow.setSelected(false);
			}
		});
	}
	
	class SelectorAdapter extends BaseAdapter {

		public SelectorAdapter(){
			selectors = new ArrayList<String>();
			selectors.add(getString(R.string.baike_selector_all));
			selectors.add(getString(R.string.baike_selector_praise));
			selectors.add(getString(R.string.baike_selector_comment));
			selectors.add(getString(R.string.baike_selector_share));
		}
		
		@Override
		public int getCount() {
			return selectors.size();
		}

		@Override
		public Object getItem(int position) {
			return selectors.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				convertView = View.inflate(activity, R.layout.item_baike_selector, null);
				tv = (TextView) convertView.findViewById(R.id.tv_selector);
				convertView.setTag(tv);
			} else {
				tv = (TextView) convertView.getTag();
			}
			tv.setText(selectors.get(position));
			return convertView;
		}
	}
		
	
	
	private void addListener(){
		ll_choice.setOnClickListener(this);
		btn_back.setOnClickListener(this);

		lv_all.setOnRefreshListener(new IOnRefreshListener() {
			@Override
			public void OnRefresh() {
				isRefresh = true;
				page_all = 1;
				getRelateMeList(0);
			}
		});
		lv_all.setOnLoadMoreListener(new IOnLoadMoreListener() {
			@Override
			public void OnLoadMore() {
				isRefresh = false;
				page_all += 1;
				getRelateMeList(0);
			}
		});
		lv_all.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = null;
				RelateMeInfo info = (RelateMeInfo) arg0.getAdapter().getItem(arg2);
				if(info.getTBTYPE().equals("3")){
					if(info.getCONTENT_COMMENT().trim().startsWith("分享百科首页至")){
						if(info.getUTYPE().equals("doctor")){
							intent = new Intent(activity, OtherBaikeActivity.class);
							intent.putExtra("other_doctor_id", info.getMEMBERID());
						}else if(info.getUTYPE().equals("user")){
							intent = new Intent(getActivity(), ContactInfoActivity.class);
							intent.putExtra("title", info.getREALNAME());
							intent.putExtra("tochat_userId", info.getMEMBERID());
							intent.putExtra("user_avatar", info.getAVATAR());
							intent.putExtra("is_star", info.getIS_STAR());
							intent.putExtra("note_name", info.getNOTE_NAME());
							intent.putExtra("description", info.getDESCRIPTION());
							intent.putExtra("index", 1);
						}
						startActivity(intent);
					} else if(info.getCONTENT_COMMENT().trim().startsWith("分享至")){
						intent = new Intent(activity, SayDetailsActivity.class);
						intent.putExtra("says_id", info.getSAYSID());
						intent.putExtra("is_my_baike", true);
						startActivity(intent);
					}
				} else {
					intent = new Intent(activity, SayDetailsActivity.class);
					intent.putExtra("says_id", info.getSAYSID());
					intent.putExtra("is_my_baike", true);
					startActivity(intent);
				}
			} 
		});
		
		lv_praise.setOnRefreshListener(new IOnRefreshListener() {
			@Override
			public void OnRefresh() {
				isRefresh = true;
				page_praise = 1;
				getRelateMeList(1);
			}
		});
		lv_praise.setOnLoadMoreListener(new IOnLoadMoreListener() {
			@Override
			public void OnLoadMore() {
				isRefresh = false;
				page_praise += 1;
				getRelateMeList(1);
			}
		});
		lv_praise.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				RelateMeInfo info = (RelateMeInfo) arg0.getAdapter().getItem(arg2);
				Intent intent = new Intent(activity, SayDetailsActivity.class);
				intent.putExtra("says_id", info.getSAYSID());
				intent.putExtra("is_my_baike", true);
				startActivity(intent);
			}
		});
		
		lv_comment.setOnRefreshListener(new IOnRefreshListener() {
			@Override
			public void OnRefresh() {
				isRefresh = true;
				page_comment = 1;
				getRelateMeList(2);
			}
		});
		lv_comment.setOnLoadMoreListener(new IOnLoadMoreListener() {
			@Override
			public void OnLoadMore() {
				isRefresh = false;
				page_comment += 1;
				getRelateMeList(2);
			}
		});
		lv_comment.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				RelateMeInfo info = (RelateMeInfo) arg0.getAdapter().getItem(arg2);
				Intent intent = new Intent(activity, SayDetailsActivity.class);
				intent.putExtra("says_id", info.getSAYSID());
				intent.putExtra("is_my_baike", true);
				startActivity(intent);
			}
		});
		
		lv_share.setOnRefreshListener(new IOnRefreshListener() {
			@Override
			public void OnRefresh() {
				isRefresh = true;
				page_share = 1;
				getRelateMeList(3);
			}
		});
		lv_share.setOnLoadMoreListener(new IOnLoadMoreListener() {
			@Override
			public void OnLoadMore() {
				isRefresh = false;
				page_share += 1;
				getRelateMeList(3);
			}
		});
		lv_share.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = null;
				RelateMeInfo info = (RelateMeInfo) arg0.getAdapter().getItem(arg2);
				if(info.getCONTENT_COMMENT().trim().startsWith("分享百科首页至")){
					if(info.getUTYPE().equals("doctor")){
						intent = new Intent(activity, OtherBaikeActivity.class);
						intent.putExtra("other_doctor_id", info.getMEMBERID());
					}else if(info.getUTYPE().equals("user")){
						intent = new Intent(getActivity(), ContactInfoActivity.class);
						intent.putExtra("title", info.getREALNAME());
						intent.putExtra("tochat_userId", info.getMEMBERID());
						intent.putExtra("user_avatar", info.getAVATAR());
						intent.putExtra("is_star", info.getIS_STAR());
						intent.putExtra("note_name", info.getNOTE_NAME());
						intent.putExtra("description", info.getDESCRIPTION());
						intent.putExtra("index", 1);
					}
					startActivity(intent);
				} else if(info.getCONTENT_COMMENT().trim().startsWith("分享至")){
					intent = new Intent(activity, SayDetailsActivity.class);
					intent.putExtra("says_id", info.getSAYSID());
					intent.putExtra("is_my_baike", true);
					startActivity(intent);
				}
			}
		});
	}
	
}
