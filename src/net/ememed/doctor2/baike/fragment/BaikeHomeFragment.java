package net.ememed.doctor2.baike.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.PersonInfoActivity;
import net.ememed.doctor2.baike.BaikeHomeActivity;
import net.ememed.doctor2.baike.CreateShuoshuoActivity;
import net.ememed.doctor2.baike.MyAttentionActivity;
import net.ememed.doctor2.baike.MyFansActivity;
import net.ememed.doctor2.baike.SayDetailsActivity;
import net.ememed.doctor2.baike.adapter.ShuoshuoAdapter;
import net.ememed.doctor2.baike.entity.BaikeMemberInfo;
import net.ememed.doctor2.baike.entity.BaikeShuoshuoInfo;
import net.ememed.doctor2.baike.entity.MyBaikeEntry;
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
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

public class BaikeHomeFragment extends Fragment implements Callback, OnClickListener{
	private final int BAIKE_SELECTOR_HOME = 0; 
	private final int BAIKE_SELECTOR_RANDOM = 1; 
	private final int BAIKE_SELECTOR_ATTENTION = 2; 
	
	public static String UPDATE_MY_SAYS_LIST = "update_my_says_list";
	
	private BaikeHomeActivity activity = null;
	private Handler mHandler = null;
	private View baikeView;
	
	private ImageView btn_back;
	private TextView top_title;
	private FrameLayout fl_baike_home;
	private LinearLayout ll_baike_random;
	private LinearLayout ll_baike_attention;
	private FrameLayout fl_top_title;
	private TextView btn_create_shushuo;
	private LinearLayout ll_go_home;
	private LinearLayout ll_choice;
	private TextView tv_menu;
	private PopupWindow popup;
	private SelectorAdapter mAdapter;
	private ImageView iv_down_arrow;
	private ArrayList<String> selectors;
	private int currentSelector = BAIKE_SELECTOR_HOME;
	
	///////////////////////////////////////////////////////////////
	private final int REQUEST_PUBLISH_SAYS = 1;
	private final int REQUEST_SAYS_DETAIL = 2;
	private final int REQUEST_REWARD_LIST = 3;
	private final int REQUEST_DOCTOR_FANS_LIST = 4;
	private final int REQUEST_ATTENTION_LIST = 5;
	private final int REQUEST_USER_FANS_LIST = 6;
	
	private LinearLayout ll_doctor_fans_area;
	private LinearLayout ll_user_fans_area;
	private LinearLayout ll_attention_area;
	
	private ShareSdkUtil share;
	private AlertDialog myDialog;
	private String delete_says_id = null;
	private InnerReceiver receiver;
	private IntentFilter filter;

	private View headView;
	private CircleImageView iv_photo;
	private TextView tv_name;
	private TextView tv_professional;
	private TextView tv_zhuanke;
	private TextView tv_hospital;
	private TextView tv_shanchang;
	private TextView tv_shuoshuo_total;
//	private TextView tv_money_num;
	private TextView tv_attention_num;
	private TextView tv_doctor_fans_num;
	private TextView tv_user_fans_num;
//	private TextView tv_redpoint_money;
	private TextView tv_redpoint_attention;
	private TextView tv_redpoint_doctor_fans;
	private TextView tv_redpoint_user_fans;
	private CheckBox follow;
	private TextView tv_today_visit_num;
	private TextView tv_total_visit_num;
	
	private RefreshListView listView, listView_random, listView_attention;
	private ShuoshuoAdapter adapter, adapter_random, adapter_attention;
	private List<BaikeShuoshuoInfo> shuoshuoList = null;
	private List<BaikeShuoshuoInfo> randomList = null;
	private List<BaikeShuoshuoInfo> attentionList = null;

	private int praise_position = -1;
	private int page = 1;
	private int page_random = 1;
	private int page_attention = 1;
	private BaikeMemberInfo memberInfo = null;
	private boolean isRefresh = true;
	private LinearLayout ll_content_area;
	private LinearLayout ll_background;
	private ImageView iv_guider1;
	private ImageView iv_guider2;
	private ImageView iv_share;
	private int total_shuoshuo = -1;
	///////////////////////////////////////////////////////////////
	
	public BaikeHomeFragment(){
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
		setRetainInstance(true);
		mHandler = new Handler(this);
		receiver = new InnerReceiver();
		filter = new IntentFilter();
		filter.addAction(MyApplication.WECHAT_SHARE_SUCCESS);
		filter.addAction(UPDATE_MY_SAYS_LIST);
		activity.registerReceiver(receiver, filter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		baikeView = inflater.inflate(R.layout.fragment_baike_home, null);
		
		setupView(baikeView);
		
		addListener();
		return baikeView;
	}
	
	private void setupView(View view){

		top_title = (TextView) view.findViewById(R.id.top_title);
		btn_back = (ImageView) view.findViewById(R.id.btn_back);
		fl_baike_home = (FrameLayout) view.findViewById(R.id.fl_baike_home);
		ll_baike_random = (LinearLayout) view.findViewById(R.id.ll_baike_random);
		ll_baike_attention = (LinearLayout) view.findViewById(R.id.ll_baike_attention);
		
		ll_choice = (LinearLayout) view.findViewById(R.id.ll_choice);
		btn_create_shushuo = (TextView) view.findViewById(R.id.tv_menu);
		ll_go_home = (LinearLayout) view.findViewById(R.id.ll_go_home);
		
		fl_top_title = (FrameLayout) view.findViewById(R.id.fl_top_title);
		tv_menu = (TextView) view.findViewById(R.id.tv_menu);
		iv_down_arrow = (ImageView) view.findViewById(R.id.iv_down_arrow);
		
		iv_share = (ImageView) view.findViewById(R.id.iv_share);
		iv_share.setVisibility(View.GONE);
		
		//随便看看
		listView_random = (RefreshListView) view.findViewById(R.id.listView_random);
		adapter_random = new ShuoshuoAdapter(null, activity, mHandler, false, true);
		listView_random.setAdapter(adapter_random);
		
		//我关注的
		listView_attention = (RefreshListView) view.findViewById(R.id.listView_attention);
		adapter_attention = new ShuoshuoAdapter(null, activity, mHandler, false, true);
		listView_attention.setAdapter(adapter_attention);
		
		listView = (RefreshListView) view.findViewById(R.id.listView);
		adapter = new ShuoshuoAdapter(null, activity, mHandler, true);
		listView.setAdapter(adapter);
		headView = LayoutInflater.from(activity).inflate(R.layout.activity_my_baike, null);
		// ///////////////////////////////////////////////////
//		tv_money_num = (TextView) headView.findViewById(R.id.tv_money_num);
//		tv_redpoint_money = (TextView) headView.findViewById(R.id.tv_redpoint_money);
		
		ll_doctor_fans_area = (LinearLayout) headView.findViewById(R.id.ll_doctor_fans_area);
		ll_user_fans_area = (LinearLayout) headView.findViewById(R.id.ll_user_fans_area);
		ll_attention_area = (LinearLayout) headView.findViewById(R.id.ll_attention_area);
		
		tv_redpoint_attention = (TextView) headView.findViewById(R.id.tv_redpoint_attention);
		tv_redpoint_doctor_fans = (TextView) headView.findViewById(R.id.tv_redpoint_doctor_fans);
		tv_redpoint_user_fans = (TextView) headView.findViewById(R.id.tv_redpoint_user_fans);
		iv_photo = (CircleImageView) headView.findViewById(R.id.iv_photo);
		tv_name = (TextView) headView.findViewById(R.id.tv_name);
		tv_professional = (TextView) headView.findViewById(R.id.tv_professional);
		tv_zhuanke = (TextView) headView.findViewById(R.id.tv_zhuanke);
		tv_hospital = (TextView) headView.findViewById(R.id.tv_hospital);
		tv_shanchang = (TextView) headView.findViewById(R.id.tv_shanchang);
		tv_shuoshuo_total = (TextView) headView.findViewById(R.id.tv_shuoshuo_total);
		tv_attention_num = (TextView) headView.findViewById(R.id.tv_attention_num);
		tv_doctor_fans_num = (TextView) headView.findViewById(R.id.tv_doctor_fans_num);
		tv_user_fans_num = (TextView) headView.findViewById(R.id.tv_user_fans_num);
		follow = (CheckBox) headView.findViewById(R.id.follow);
		tv_today_visit_num = (TextView) headView.findViewById(R.id.tv_today_visit_num);
		tv_total_visit_num = (TextView) headView.findViewById(R.id.tv_total_visit_num);
		
		headView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != memberInfo) {
					Intent intent = new Intent(activity, PersonInfoActivity.class);
					startActivity(intent);
				}
			}
		});
		// ////////////////////////////////////////////////////
		listView.addHeaderView(headView);
		listView.setVisibility(View.GONE);

		initPopUp();
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			if(null == memberInfo)
				getPersonBaikeInfo();
		} else {
			MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_my_baike);
			MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_says_list);
			MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_share);
			MyApplication.volleyHttpClient.cancelRequest(HttpUtil.delete_says);
			MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_praise);
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
				MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_my_baike);
				MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_says_list);
				if(choice.equals(getString(R.string.baike_selector_home))){
					if(BAIKE_SELECTOR_HOME != currentSelector){
						currentSelector = BAIKE_SELECTOR_HOME;
						fl_baike_home.setVisibility(View.VISIBLE);
						ll_baike_random.setVisibility(View.GONE);
						ll_baike_attention.setVisibility(View.GONE);
						if(null == randomList){
							randomList = new ArrayList<BaikeShuoshuoInfo>();
							getSaysList(1);
						}
						//TODO
					} 
				} else if(choice.equals(getString(R.string.baike_selector_random))){
					if(BAIKE_SELECTOR_RANDOM != currentSelector){
						currentSelector = BAIKE_SELECTOR_RANDOM;
						fl_baike_home.setVisibility(View.GONE);
						ll_baike_random.setVisibility(View.VISIBLE);
						ll_baike_attention.setVisibility(View.GONE);

						if(null == randomList){
							randomList = new ArrayList<BaikeShuoshuoInfo>();
							getSaysList(0);
						}
					} 
				} else if(choice.equals(getString(R.string.baike_selector_attention))){
					if(BAIKE_SELECTOR_ATTENTION != currentSelector){
						currentSelector = BAIKE_SELECTOR_ATTENTION;
						fl_baike_home.setVisibility(View.GONE);
						ll_baike_random.setVisibility(View.GONE);
						ll_baike_attention.setVisibility(View.VISIBLE);
						
						if(null == attentionList){
							attentionList = new ArrayList<BaikeShuoshuoInfo>();
							getSaysList(2);
						}
					}		
				} 
			}
		});
		
		popup = new PopupWindow(lv, MyApplication.getInstance().canvasWidth, Util.dip2px(activity, 200));
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
	
	private void getPersonBaikeInfo() {
		if (NetWorkUtils.detect(activity)) {
			if(null == memberInfo){
				activity.loading(null);
			} else {
				Logger.iout("测试", "不转圈");
			}

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", page + "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_my_baike, MyBaikeEntry.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_BAIKE_HOME;
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
	
	/**
	 * 获取说说列表 0所有说说（随便看看），1我的说说，2我关注的医生的说说
	 */
	private void getSaysList(int type) {
		int temp_page = 0;
		if (NetWorkUtils.detect(activity)) {
			activity.loading(null);
			if(0 == type){
				temp_page = page_random; 
			} else if(2 == type){
				temp_page = page_attention;
			}
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("type", ""+type);
			params.put("page", ""+ temp_page);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_says_list, SaysListEntry.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_SAYS_LIST;
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

	private void givePraise(String says_id) {
		if (NetWorkUtils.detect(activity)) {
			activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("saysid", says_id);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_praise, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GIVE_PRAISE;
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

	private void deleteShuoshuo() {
		if (NetWorkUtils.detect(activity)) {
			activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("saysid", delete_says_id);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.delete_says, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.DELETE_SAYS;
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
	
	private void setShare(int channel_type) {
		if (NetWorkUtils.detect(activity)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("other_doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("channel_type", "" + channel_type);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_share, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.SET_SHARE;
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
			switch (msg.what) {
			case IResult.GET_BAIKE_HOME:
				listView.onRefreshComplete();
				MyBaikeEntry entry = (MyBaikeEntry) msg.obj;
				if (null != entry) {
					if (entry.getSuccess() == 1) {
						listView.setVisibility(View.VISIBLE);
						iv_share.setVisibility(View.VISIBLE);
						memberInfo = entry.getMemberinfo();
						total_shuoshuo = entry.getCount();
						showPersonInfo();

						if (null != memberInfo) {
							// updateView(entry);

							if ("2.2".equals(MyApplication.getAppVersion())
									&& (!SharePrefUtil.getBoolean(Conast.IS_ALREADY_ACCESS_BAIKE
											+ SharePrefUtil.getString(Conast.Doctor_ID)))) {
								guider(baikeView);
							}
						}

						List<BaikeShuoshuoInfo> temp = entry.getData();
						if (null != temp && temp.size() > 0) {
							if (isRefresh) {
								shuoshuoList = temp;
							} else {
								shuoshuoList.addAll(temp);
							}
						}

						adapter.change(shuoshuoList, memberInfo);

						if (page < entry.getPages()) {
							listView.onLoadMoreComplete(false);
						} else {
							listView.onLoadMoreComplete(true);
						}


					} else {
						activity.showToast(entry.getErrormsg());
					}
					if(BAIKE_SELECTOR_HOME == currentSelector){
						activity.destroyDialog();
					}
				}
				break;
			case IResult.GIVE_PRAISE_INNER:
				int id = msg.arg1;
				praise_position = msg.arg2;
				givePraise("" + id);
				break;
			case IResult.GIVE_PRAISE:
				activity.destroyDialog();
				CommonResponseEntity response2 = (CommonResponseEntity) msg.obj;
				if (null != response2) {
					if (1 == response2.getSuccess()) {
						if (praise_position >= 0) {
							int num = Integer.parseInt(shuoshuoList.get(praise_position).getPRAISE_NUM());
							shuoshuoList.get(praise_position).setPRAISE_NUM((num + 1) + "");
							shuoshuoList.get(praise_position).setIS_PRAISED(true);
							adapter.change(shuoshuoList);
						}
					} else {
						activity.showToast(response2.getErrormsg());
					}
				}
				praise_position = -1;
				break;
			case IResult.DELETE_SAYS:
				activity.destroyDialog();
				CommonResponseEntity response3 = (CommonResponseEntity) msg.obj;
				if (null != response3) {
					if (1 == response3.getSuccess()) {
						activity.showToast("删除成功");
						// 更新界面
						for (int i = 0; i < shuoshuoList.size(); i++) {
							if (shuoshuoList.get(i).getSAYSID().equals(response3.getData())) {
								shuoshuoList.remove(i);
								break;
							}
						}

						delete_says_id = null;
						adapter.change(shuoshuoList);
					} else {
						activity.showToast(response3.getErrormsg());
					}
				}
				break;
			case IResult.GET_SAYS_LIST:
				activity.destroyDialog();
				listView_random.onRefreshComplete();
				listView_attention.onRefreshComplete();
				SaysListEntry entry2 = (SaysListEntry) msg.obj;
				if (null != entry2 && entry2.getSuccess() == 1) {
					if(BAIKE_SELECTOR_RANDOM == currentSelector){
						
						if(isRefresh){
							randomList = entry2.getData();
						} else {
							randomList.addAll(entry2.getData());
						}
						
						adapter_random.change(randomList, memberInfo);
						
						if(entry2.getPages() <= page_random){
							listView_random.onLoadMoreComplete(true);
						} else {
							listView_random.onLoadMoreComplete(false);
						}
								
					} else if(BAIKE_SELECTOR_ATTENTION == currentSelector){
						if(isRefresh){
							attentionList = entry2.getData();
						} else {
							attentionList.addAll(entry2.getData());
						}
						
						adapter_attention.change(attentionList, memberInfo);
						
						if(entry2.getPages() <= page_attention){
							listView_attention.onLoadMoreComplete(true);
						} else {
							listView_attention.onLoadMoreComplete(false);
						}
					}
				}
				break;
			case IResult.DATA_ERROR:
				listView.onRefreshComplete();
				activity.destroyDialog();
				activity.showToast("数据异常");
				Logger.iout("测试", "abc+<<<<"+msg.obj.toString());
				break;
			case IResult.NET_ERROR:
				activity.showToast("网络异常");
				listView.onRefreshComplete();
				activity.destroyDialog();
				break;
			case ShareSdkUtil.onError:
				activity.showToast("分享失败");
				break;
			case ShareSdkUtil.onComplete:
				activity.showToast("分享成功");
				setShare(msg.arg1);
				break;
			case ShareSdkUtil.onCancel:
				activity.showToast("取消分享");
				break;
			case IResult.SET_SHARE:
				CommonResponseEntity result = (CommonResponseEntity) msg.obj;
				if (1 == result.getSuccess()) {
					Logger.iout("测试", "分享回调统计成功");
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void showPersonInfo() {
		headView.setVisibility(View.VISIBLE);
		tv_name.setText(memberInfo.getREALNAME());
		tv_professional.setText(memberInfo.getPROFESSIONAL());
		tv_hospital.setText(memberInfo.getHOSPITALNAME());
		tv_shanchang.setText(memberInfo.getSPECIALITY());
		tv_attention_num.setText(memberInfo.getAttention_total_num());
		tv_doctor_fans_num.setText(memberInfo.getFans_doctor_total_num()+"");
		tv_user_fans_num.setText(memberInfo.getFans_user_total_num()+"");
		tv_shuoshuo_total.setText("全部说说 （" + total_shuoshuo + "）");
		tv_today_visit_num.setText(memberInfo.getBaike_visitors_today());
		tv_total_visit_num.setText(memberInfo.getBAIKE_VISITORS());

		
		if ((!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AUDIT_STATUS)))
				&& "1".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))
				&& (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR)))) {
			activity.imageLoader.displayImage(SharePrefUtil.getString(Conast.AVATAR), iv_photo, Util.getOptions_avatar());
		} else {
			iv_photo.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
		}

		if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AUDIT_STATUS))
				&& "1".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))) {
			if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.Doctor_Type))) {
				if ("1".equals(SharePrefUtil.getString(Conast.Doctor_Type))) {
					tv_zhuanke.setText(getString(R.string.doctor_type_quanke));
				} else {
					tv_zhuanke.setText(getString(R.string.doctor_type_zhuanke));
				}
			}
		} else {
			tv_zhuanke.setText("未认证");
			tv_zhuanke.setBackgroundResource(R.drawable.shape_circle_rectangle_gray);
		}

		// 新增关注的红点
		if (memberInfo.getAttention_unread_num() > 0) {
			tv_redpoint_attention.setVisibility(View.VISIBLE);
		} else {
			tv_redpoint_attention.setVisibility(View.GONE);
		}

		// 新增医生粉丝的红点
		if (memberInfo.getFans_doctor_unread_num()> 0) {
			tv_redpoint_doctor_fans.setVisibility(View.VISIBLE);
			tv_redpoint_doctor_fans.setText("+" + memberInfo.getFans_doctor_unread_num());
		} else {
			tv_redpoint_doctor_fans.setVisibility(View.GONE);
		}
		
		// 新增患者粉丝的红点
		if (memberInfo.getFans_doctor_unread_num()> 0) {
			tv_redpoint_user_fans.setVisibility(View.VISIBLE);
			tv_redpoint_user_fans.setText("+" + memberInfo.getFans_user_unread_num());
		} else {
			tv_redpoint_doctor_fans.setVisibility(View.GONE);
		}
		
		//已认证图标
		if(!TextUtils.isEmpty(memberInfo.getAUDITSTATUS()) && "1".equals(memberInfo.getAUDITSTATUS())){
			follow.setChecked(true);
		} else {
			follow.setChecked(false);
		}
	}

	private void guider(View view) {

		SharePrefUtil.putBoolean(Conast.IS_ALREADY_ACCESS_BAIKE + SharePrefUtil.getString(Conast.Doctor_ID), true);
		SharePrefUtil.commit();
		ll_content_area = (LinearLayout) view.findViewById(R.id.ll_content_area);
		ll_background = (LinearLayout) view.findViewById(R.id.ll_background);
		iv_guider1 = (ImageView) view.findViewById(R.id.iv_guider1);
		iv_guider2 = (ImageView) view.findViewById(R.id.iv_guider2);

		ll_content_area.setEnabled(false);
		ll_background.setVisibility(View.VISIBLE);
		iv_guider1.setVisibility(View.VISIBLE);

		iv_guider1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_guider1.setVisibility(View.GONE);
				iv_guider2.setVisibility(View.VISIBLE);
			}
		});
		iv_guider2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_guider2.setVisibility(View.GONE);
				ll_background.setVisibility(View.GONE);
				ll_content_area.setEnabled(true);
			}
		});
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
					delete_says_id = null;
				}
			});

			btn_confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteShuoshuo();
					myDialog.dismiss();
				}
			});
			myDialog.setView(view);
		}

		myDialog.show();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*
		 * case R.id.btn_back: finish(); break;
		 */
		case R.id.tv_menu:
			Intent intent = new Intent(activity, CreateShuoshuoActivity.class);
			startActivityForResult(intent, REQUEST_PUBLISH_SAYS);
			break;
		/*case R.id.ll_reward_area:
			Intent intent2 = new Intent(this, RewardListActivity.class);
			intent2.putExtra("total_money", memberInfo.getBounty_total_money());
			startActivityForResult(intent2, REQUEST_REWARD_LIST);
			break;*/
		case R.id.ll_doctor_fans_area:
			Intent intent3 = new Intent(activity, MyFansActivity.class);
			intent3.putExtra("fans_type", 1);
			startActivityForResult(intent3, REQUEST_DOCTOR_FANS_LIST);
			break;
		case R.id.ll_user_fans_area:
			Intent intent4 = new Intent(activity, MyFansActivity.class);
			intent4.putExtra("fans_type", 2);
			startActivityForResult(intent4, REQUEST_USER_FANS_LIST);
			break;
		case R.id.ll_attention_area:
			startActivityForResult(new Intent(activity, MyAttentionActivity.class), REQUEST_ATTENTION_LIST);
			break;
		case R.id.iv_share:

			String name = SharePrefUtil.getString(Conast.Doctor_Name);
			String text = null;
			String pic = null;
			String title = "薏米网-" + name;
			String url = null;

			String hospital = SharePrefUtil.getString(Conast.HOSPITAL_NAME);
			String professional = SharePrefUtil.getString(Conast.Doctor_Professional);
			String speciality = SharePrefUtil.getString(Conast.SPECIALITY);
			text = hospital + "," + professional + "," + speciality;

			if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR))) {
				pic = SharePrefUtil.getString(Conast.AVATAR);
			}

			if (!TextUtils.isEmpty(memberInfo.getShare_url())) {
				url = memberInfo.getShare_url();
			}

			if (null == share) {
				share = new ShareSdkUtil(activity, mHandler, ShareSdkUtil.SHARE_BAIKE_HOME, SharePrefUtil.getString(Conast.Doctor_ID));
			}
			share.initModePopupWindow(fl_top_title, title, text, url, pic);
			break;
		case R.id.ll_choice:
			if(popup!=null){
				popup.showAsDropDown(fl_top_title, 0, 0);
				iv_down_arrow.setSelected(true);
			}
			break;
		case R.id.btn_back:
			activity.finish();
			break;
		default:
			break;
		}
	}
	
	private class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.iout("测试", "收到微信分享成功的广播");
			if (intent.getAction().equals(MyApplication.WECHAT_SHARE_SUCCESS)) {
				int type = intent.getIntExtra("share_type", ShareSdkUtil.SHARE_TO_WECHAT);
				int source = intent.getIntExtra("share_source", ShareSdkUtil.SHARE_BAIKE_SAYS);
				String doctor_id = intent.getStringExtra("share_doctor_id");
				Logger.iout("测试", "type="+type+">>>source="+source);
				if(source == ShareSdkUtil.SHARE_BAIKE_HOME && doctor_id.equals(SharePrefUtil.getString(Conast.Doctor_ID))){
					setShare(type);
					ShareSdkUtil.setShare_doctor_id(null);
					Logger.iout("测试", "分享统计");
				}
				
			} else if(intent.getAction().equals(UPDATE_MY_SAYS_LIST)){
				BaikeShuoshuoInfo detail = (BaikeShuoshuoInfo) intent.getSerializableExtra("says_detail");
				boolean isDeleteSays = intent.getBooleanExtra("is_delete_says", false);
				boolean isMyBaike = intent.getBooleanExtra("is_my_baike", false);
				
				if(shuoshuoList != null && shuoshuoList.size() > 0 && isMyBaike){
					for(int i = 0; i < shuoshuoList.size(); i++){
						BaikeShuoshuoInfo temp = shuoshuoList.get(i);
						if(temp.getSAYSID().equals(detail.getSAYSID())){
							if(isDeleteSays){
								shuoshuoList.remove(i);
							} else {
								detail.setCONTENT_SHOW(temp.getCONTENT_SHOW());
								detail.setIS_NEW(temp.getIS_NEW());
								detail.setRNUM(temp.getRNUM());
								shuoshuoList.set(i, detail);
								adapter.change(shuoshuoList, memberInfo);
							}
							break;
						}
					}
				} 
				
				if(randomList != null && randomList.size() > 0){
					for(int i = 0; i < randomList.size(); i++){
						BaikeShuoshuoInfo temp = randomList.get(i);
						if(temp.getSAYSID().equals(detail.getSAYSID())){
							if(isDeleteSays){
								randomList.remove(i);
							} else {
								detail.setCONTENT_SHOW(temp.getCONTENT_SHOW());
								detail.setIS_NEW(temp.getIS_NEW());
								detail.setRNUM(temp.getRNUM());
								randomList.set(i, detail);
								adapter_random.change(randomList, memberInfo);
							}
							break;
						}
					}
				}
				
				if(attentionList != null && attentionList.size() > 0 && (!isMyBaike)){
					for(int i = 0; i < attentionList.size(); i++){
						BaikeShuoshuoInfo temp = attentionList.get(i);
						if(temp.getSAYSID().equals(detail.getSAYSID())){
							detail.setCONTENT_SHOW(temp.getCONTENT_SHOW());
							detail.setIS_NEW(temp.getIS_NEW());
							detail.setRNUM(temp.getRNUM());
							attentionList.set(i, detail);
							adapter_attention.change(attentionList, memberInfo);
							break;
						}
					}
				}
			}
		}
	}
	
	class SelectorAdapter extends BaseAdapter {

		public SelectorAdapter(){
			selectors = new ArrayList<String>();
			selectors.add(getString(R.string.baike_selector_home));
			selectors.add(getString(R.string.baike_selector_random));
			selectors.add(getString(R.string.baike_selector_attention));
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(receiver!=null){
			activity.unregisterReceiver(receiver);
		}
	}
	
	private void addListener(){
		
		ll_doctor_fans_area.setOnClickListener(this);
		ll_user_fans_area.setOnClickListener(this);
		ll_attention_area.setOnClickListener(this);
		iv_share.setOnClickListener(this);
		tv_menu.setOnClickListener(this);
		fl_top_title.setOnClickListener(this);
		ll_choice.setOnClickListener(this);
		btn_create_shushuo.setOnClickListener(this);
		ll_go_home.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		
		listView.setOnRefreshListener(new IOnRefreshListener() {
			@Override
			public void OnRefresh() {
				page = 1;
				isRefresh = true;
				getPersonBaikeInfo();
			}
		});
		listView.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				page += 1;
				isRefresh = false;
				getPersonBaikeInfo();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position > 1) {
					// BaikeShuoshuoInfo info = (BaikeShuoshuoInfo)
					// adapter.getItem(position - 1);
					BaikeShuoshuoInfo info = (BaikeShuoshuoInfo) parent.getAdapter().getItem(position);
					Intent intent = new Intent(activity, SayDetailsActivity.class);
					intent.putExtra("says_id", info.getSAYSID());
					intent.putExtra("is_my_baike", true);
					intent.putExtra("name", info.getREALNAME());
					
					startActivityForResult(intent, REQUEST_SAYS_DETAIL);
				}
			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				BaikeShuoshuoInfo info = (BaikeShuoshuoInfo) parent.getAdapter().getItem(position);
				delete_says_id = info.getSAYSID();
				showAlertDialogue();
				return true;
			}
		});
		
		listView_random.setOnRefreshListener(new IOnRefreshListener() {
			@Override
			public void OnRefresh() {
				page_random = 1;
				isRefresh = true;
				getSaysList(0);
			}
		});
		listView_random.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				page_random += 1;
				isRefresh = false;
				getSaysList(0);
			}
		});
		listView_random.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					BaikeShuoshuoInfo info = (BaikeShuoshuoInfo) parent.getAdapter().getItem(position);
					Intent intent = new Intent(activity, SayDetailsActivity.class);
					intent.putExtra("says_id", info.getSAYSID());
					if(info.getDOCTORID().equals(SharePrefUtil.getString(Conast.Doctor_ID))){
						intent.putExtra("is_my_baike", true);
					}
					intent.putExtra("name", info.getREALNAME());
					startActivity(intent);
			}
		});
		
		listView_attention.setOnRefreshListener(new IOnRefreshListener() {
			@Override
			public void OnRefresh() {
				page_attention = 1;
				isRefresh = true;
				getSaysList(2);
			}
		});
		listView_attention.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				page_attention += 1;
				isRefresh = false;
				getSaysList(2);
			}
		});
		listView_attention.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					BaikeShuoshuoInfo info = (BaikeShuoshuoInfo) parent.getAdapter().getItem(position);
					Intent intent = new Intent(activity, SayDetailsActivity.class);
					intent.putExtra("says_id", info.getSAYSID());
					intent.putExtra("name", info.getREALNAME());
					startActivity(intent);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (REQUEST_PUBLISH_SAYS == requestCode) {
			if (Activity.RESULT_OK == resultCode) {
				page = 1;
				getPersonBaikeInfo();
			}
		} /*else if (REQUEST_SAYS_DETAIL == requestCode) {
			page = 1;
			isRefresh = true;
			getPersonBaikeInfo();
		}*/ /*else if (REQUEST_REWARD_LIST == requestCode) {
			if (RESULT_OK == resultCode) {
				tv_redpoint_money = (TextView) findViewById(R.id.tv_redpoint_money);
				tv_redpoint_money.setVisibility(View.GONE);
			}
		} */else if (REQUEST_DOCTOR_FANS_LIST == requestCode) {
			if (Activity.RESULT_OK == resultCode) {
				memberInfo.setFans_doctor_unread_num(0);
				tv_redpoint_doctor_fans.setVisibility(View.GONE);
			}
		} else if (REQUEST_USER_FANS_LIST == requestCode) {
			if (Activity.RESULT_OK == resultCode) {
				memberInfo.setFans_user_unread_num(0);
				tv_redpoint_user_fans.setVisibility(View.GONE);
			}
		} else if (REQUEST_ATTENTION_LIST == requestCode) {
			page = 1;
			isRefresh = true;
			getPersonBaikeInfo();	
		}
	}
}
