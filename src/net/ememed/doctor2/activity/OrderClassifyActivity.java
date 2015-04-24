package net.ememed.doctor2.activity;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.OrderInformation;
import net.ememed.doctor2.entity.OrderListEntity;
import net.ememed.doctor2.entity.OrderListEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

/**
 * 订单分类，从主页点击不同类型的订单，筛选不同类型的订单显示在界面上
 * @author chen
 *
 */
public class OrderClassifyActivity extends BasicActivity implements Callback, OnClickListener {
	private String TAG = "llj-orderClassifyActivity";
	private List<OrderListEntry> allOrderList = new ArrayList<OrderListEntry>();
	private List<OrderListEntry> filterOrderList = new ArrayList<OrderListEntry>();
	
	private OrderInformation orderInfo;
	private boolean refresh = true;
	private int page = 1;		//用于直接搜索
	private int page2 = 1;		//用于根据姓名过滤搜索
	private Handler mHandler = null;
	private RefreshListView list_view;
	private OrderAdapter adapter;
	private LinearLayout ll_empty;
	private LinearLayout ll_net_unavailable;
	private Button btn_invite_patient;
	private LinearLayout ll_open_service; 
	private ImageView btn_back;
	private LinearLayout ll_select_order_type;
	private PopupWindow popup;
	private AlertDialog myDialog;
	private TextView tv_order_name;
	private EditText et_search_box;
	private Button btn_open_service;
	private FrameLayout fl_top_title;
	private ImageView iv_search_pic;
	private Button btn_cancel_search;
	private ImageView order_list_img;
	private LinearLayout ll_invite_patient;
	
	private Boolean hasMeasured = false;
	
	private int topBarHeight = 0;
	
	private boolean isfilterByName = false;	//是否是根据名字来查找订单
	
	private AlertDialog myDialog2;		//如果医生资料未审核通过，点击邀请患者时弹出的对话框
	
	//两个数组，服务名称，服务类型，一一对应，不可调整顺序
	//'1'=>'图文咨询','2'=>'预约通话','3'=>'预约加号','4'=>'上门会诊',
	//'5'=>'院内陪诊','7'=>'免费挂号','11'=>'体检报告解读','12'=>'个人健康季评',
	//'14'=>'预约住院','15'=>'私人医生服务','16'=>'其他服务','30'=>'科研调查奖励'
	private String orderNames[] = {"图文咨询订单","预约通话订单","私人医生服务订单",
			  "预约加号订单","预约住院订单"};
	private String orderTypes[] = {"1","2","15","3","14"};
	private int orderIcons[] = {R.drawable.icon_consult_picture, R.drawable.icon_consult_phone, R.drawable.icon_private_doctor_2,
	 R.drawable.icon_jinji_jiahao, R.drawable.icon_jinji_zhuyuan};
//	private String orderNames[] = {"图文咨询订单","预约通话订单","私人医生服务订单","上门会诊订单",
//							  "预约加号订单","预约住院订单","其他服务订单"};
//	private String orderTypes[] = {"1","2","15","4","3","14","16"};
//	private int orderIcons[] = {R.drawable.icon_consult_picture, R.drawable.icon_consult_phone, R.drawable.icon_private_doctor_2,
//			R.drawable.icon_shangmen_2, R.drawable.icon_jinji_jiahao, R.drawable.icon_jinji_zhuyuan, R.drawable.icon_other_service};
	private ArrayList<OrderInformation> orders = null;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.order_list);
		mHandler = new Handler(this);
		orderInfo = (OrderInformation) getIntent().getSerializableExtra("order_info");
	}
	
	@Override
	protected void setupView() {
		super.setupView();

		//初始化OrderInformation清单
		initOrderInformation();
		
		iv_search_pic = (ImageView) findViewById(R.id.iv_search_pic);
		btn_cancel_search = (Button) findViewById(R.id.btn_cancel_search);
		btn_cancel_search.setOnClickListener(this);
		
		tv_order_name = (TextView) findViewById(R.id.tv_order_type);
		tv_order_name.setText(orderInfo.getName());
		tv_order_name.requestFocus();
		list_view = (RefreshListView) findViewById(R.id.order_listview);
		ll_empty = (LinearLayout) findViewById(R.id.ll_no_order);
		ll_net_unavailable = (LinearLayout) findViewById(R.id.ll_net_unavailable);
		btn_invite_patient = (Button) findViewById(R.id.btn_invite_patient);
		btn_invite_patient.setOnClickListener(this);
		ll_open_service = (LinearLayout) findViewById(R.id.ll_open_service);
		btn_open_service = (Button) findViewById(R.id.btn_open_service);
		btn_open_service.setOnClickListener(this);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		order_list_img=(ImageView) findViewById(R.id.order_list_img);
		ll_invite_patient=(LinearLayout) findViewById(R.id.ll_invite_patient);
		/*spinner = (Spinner)findViewById(R.id.spinner1);
		// 建立订单类型下拉框Adapter并且绑定数据源
		ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<String>(this, R.layout.simple_spinner_item, orderNameList);
		//spinner绑定adapter
		spinner.setAdapter(spinnerAdapter);*/
		ll_select_order_type = (LinearLayout) findViewById(R.id.ll_selcet_order_type);	//切换订单类型
		ll_select_order_type.setOnClickListener(this);
		
		adapter = new OrderAdapter(null);
		list_view.setAdapter(adapter);
		
		et_search_box = (EditText) findViewById(R.id.et_search_box);
		
        setSearchBoxhint();
		
		
		if (!NetWorkUtils.detect(this)) {
			showToast(getString(R.string.network_unavailable));
		}
		
		getOrderListByOrderType(orderInfo.getType(), page, true);
		
		
		fl_top_title = (FrameLayout) findViewById(R.id.fl_top_title);
		ViewTreeObserver vto = fl_top_title.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            public boolean onPreDraw()
            {
                if (hasMeasured == false)
                {
                    topBarHeight = fl_top_title.getMeasuredHeight();//获取到宽度和高度后，可用于initAlertDialogue计算 alertDialog的位置
                    hasMeasured = true;
                    initAlertDialogue();
                }
                return true;
            }
        });
		
		//initAlertDialogue();
	}


	private void initOrderInformation() {
		orders = new ArrayList<OrderInformation>();
		for(int i = 0; i < orderNames.length; i++)
		{
			boolean isOpen = judgeServiceIsOpen(orderTypes[i]);
			OrderInformation info = new OrderInformation(orderNames[i], orderTypes[i], orderIcons[i], isOpen);
			orders.add(info);
		}
	}


	/**
	 * 设置搜索框的默认提示，涉及到图文混排,前面放大镜为图片
	 */
	private void setSearchBoxhint() {
		//  根据资源ID获得资源图像的Bitmap对象
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.order_search);
        //  根据Bitmap对象创建ImageSpan对象
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        //  创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
        SpannableString spannableString = new SpannableString(getString(R.string.search_order));
        //  用ImageSpan对象替换face
        spannableString.setSpan(imageSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //  将随机获得的图像追加到EditText控件的最后
        //et_search_box.setText(spannableString);
        et_search_box.setHint(spannableString);
        et_search_box.setGravity(Gravity.CENTER);
        et_search_box.setHintTextColor(Color.parseColor("#a4a4a4"));
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btn_open_service:
			startActivity(new Intent(this, ServiceAllSettingActivity.class));
			break;
		case R.id.btn_invite_patient:
			if(false == SharePrefUtil.getBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+SharePrefUtil.getString(Conast.Doctor_ID))){
				if(true == SharePrefUtil.getBoolean(Conast.VALIDATED)){
					//startActivity(new Intent(activity, RegisterSuccessActivity.class));
					startActivity(new Intent(OrderClassifyActivity.this, PersonInfoActivity.class));
				} else {
					if(!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
						if("0".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
							showAlertDialogue();
						} else {
							startActivity(new Intent(OrderClassifyActivity.this, RegisterSuccessActivity.class));
						}
					}
				}
			} else {
				startActivity(new Intent(OrderClassifyActivity.this, BusinessCardActivity.class));
			}
			
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.ll_selcet_order_type:
			myDialog.show();
			break;
		case R.id.btn_cancel_search:
			et_search_box.setText("");
			et_search_box.clearFocus();
			isfilterByName = false;
			MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_order_list);
			adapter.change(allOrderList);
			break;
		}
	}
	int before1 ;
	int after1 ;
	/**
	 * 初始化下拉框
	 */
	private void showAlertDialogue(){
		if(null == myDialog2){
			myDialog2 = new AlertDialog.Builder(OrderClassifyActivity.this).create();
			View view = LayoutInflater.from(OrderClassifyActivity.this).inflate(R.layout.dialog_cannot_private_patient, null);
			view.setMinimumHeight(MyApplication.getInstance().canvasHeight*3/5);
			view.setMinimumWidth(MyApplication.getInstance().canvasWidth*3/4);
			myDialog2.setCanceledOnTouchOutside(true);
			Button btn = (Button) view.findViewById(R.id.button1);
			btn.setOnClickListener(new OnClickListener() {
		
				@Override
				public void onClick(View v) {
//					if("2".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
//						startActivity(new Intent(activity, VerifingActivity.class));
//					} else if("3".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
//						startActivity(new Intent(activity, VerifyFailedActivity.class));
//					} else if("0".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
						//直接跳到完善资料的部分
						Intent intent = new Intent(OrderClassifyActivity.this, RegisterSuccessActivity.class);
						startActivity(intent);
//					}
					myDialog2.dismiss();
				}
			});
			myDialog2.setView(view);
		}
		
		myDialog2.show();
	}
	
	@Override
	protected void addListener() {
		super.addListener();
		
		/*spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				 //String str=parent.getItemAtPosition(position).toString();
				if(!orderTypeList[position].equals(orderType)){
					orderType = orderTypeList[position];
					page = 1;
					getOrderListByOrderType(orderType, page, true);
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});*/
		
		/*ll_search_box_hint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ll_search_box_hint.setVisibility(View.GONE);
				et_search_box.requestFocus();
			}
		});*/
		
		et_search_box.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				et_search_box.setGravity(Gravity.CENTER_VERTICAL);
				et_search_box.setHint("");
				iv_search_pic.setVisibility(View.VISIBLE);
				btn_cancel_search.setVisibility(View.VISIBLE);
			}
		});
		
		et_search_box.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					et_search_box.setGravity(Gravity.CENTER_VERTICAL);
					et_search_box.setHint("");
					iv_search_pic.setVisibility(View.VISIBLE);
					btn_cancel_search.setVisibility(View.VISIBLE);
				} else{
					iv_search_pic.setVisibility(View.GONE);
					btn_cancel_search.setVisibility(View.GONE);
					setSearchBoxhint();
				}
			}
		});
		
		
		
		et_search_box.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				System.out.println("before = " +before);
				before1 = before;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				//btn_search.setPressed(true);
				System.out.println("after = " +after);
				after1 = after;
			}

			@Override
			public void afterTextChanged(Editable s) {
				
				System.out.println("s.length() = "+s.length());
				
				if ( before1 == 0 && s.length() == 0  ) {
					
					if(et_search_box.isFocusable()){
						
						et_search_box.clearFocus();
						closeKeyboard(OrderClassifyActivity.this);
						adapter.change(allOrderList);
					}
					
				}
				
			}
		});
		
		//输入法回车键变搜索
		et_search_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
						
				if (actionId == EditorInfo.IME_ACTION_SEND || (event!=null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {      
					String tempKeyword = et_search_box.getText().toString().trim();
					if(TextUtils.isEmpty(tempKeyword)) {
						showToast("搜索关键字不能为空");
					}
					else {
						page2 = 1;
						getOrderListByName(orderInfo.getType(), et_search_box.getText().toString().trim(), page2, true);
					}
					return true;             
				}
				
				return false;
			} 
		});
		
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
				if (!NetWorkUtils.detect(OrderClassifyActivity.this)) {
					showToast(getString(R.string.network_unavailable));
				}
				
				//ContactEntry item = (ContactEntry) adapter.getItem(position - 1);
				OrderListEntry item = (OrderListEntry) adapter.getItem(position - 1);
				
				if(!TextUtils.isEmpty(item.getORDERTYPE())){
					if(item.getORDERTYPE().equals("15")){
						Intent intent1 = new Intent(OrderClassifyActivity.this, OrderDetailActivity.class);
						intent1.putExtra("user_name", item.getCONTRACTOR());
						intent1.putExtra("order", item);
						startActivity(intent1);
					} else {
						Intent intent = new Intent(OrderClassifyActivity.this, DoctorChatActivity.class);
						intent.putExtra("title", item.getCONTRACTOR());
						intent.putExtra("tochat_userId", item.getUSERID());
						intent.putExtra("user_avatar", item.getAVATAR());
						intent.putExtra("orderid", item.getORDERID());
						intent.putExtra("DESC_TEXT", item.getDESC_TEXT());
						intent.putExtra("ORDERTYPE", item.getORDERTYPE());
						intent.putExtra("order", item);
						startActivity(intent);
					}
				}
			}
		});

		list_view.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				refresh();
			}
		});

		list_view.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				loadMore();
			}
		});
	}
	

	
	/**
	 * 获取指定类型的订单
	 * @param page		分页
	 * @param isLoading 是否需要显示正在加载的动画
	 * @param orderType	订单类型
	 */
	//'1'=>'图文咨询','2'=>'预约通话','3'=>'预约加号','4'=>'上门会诊',
	//'5'=>'院内陪诊','7'=>'免费挂号','11'=>'体检报告解读','12'=>'个人健康季评',
	//'14'=>'预约住院','15'=>'私人医生服务','16'=>'其他服务','30'=>'科研调查奖励'
	private void getOrderListByOrderType(String orderType, int page, boolean isLoading){
		if(false == judgeServiceIsOpen(orderType)){
			ll_open_service.setVisibility(View.VISIBLE);
		}
		else{
			ll_open_service.setVisibility(View.GONE);
		}
		
		if (NetWorkUtils.detect(this)) {
			if(isLoading) loading(null);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("ordertype", orderType);
			params.put("type", "0");//获取类型 0 所有订单 1 已结束订单 2 还在进行中的订单
			params.put("page", page + "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_order_list, OrderListEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.ORDER_LIST;
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
	protected void onStop() {
		super.onStop();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_order_list);
	}


	/**
	 * 获取指定类型的订单（不显示正在加载动画）
	 * @param page		分页
	 * @param orderType	订单类型
	 */
	private void getOrderListByOrderType(String orderType, int page){
		getOrderListByOrderType(orderType, page, false);
	}
	
	/**
	 * 根据患者名称获取订单
	 * @param orderType
	 * @param page
	 * @param isLoading
	 */
	private void getOrderListByName(String orderType, String keyword, int page, boolean isLoading){
		if(false == judgeServiceIsOpen(orderType)){
			ll_open_service.setVisibility(View.VISIBLE);
		}
		else{
			ll_open_service.setVisibility(View.GONE);
		}
		
		if (NetWorkUtils.detect(this)) {
			if(isLoading) loading(null);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("ordertype", orderType);
			params.put("type", "0");//获取类型 0 所有订单 1 已结束订单 2 还在进行中的订单
			params.put("page", page + "");
			params.put("keyword", keyword);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_order_list, OrderListEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							isfilterByName = true;
							Message message = new Message();
							message.obj = response;
							message.what = IResult.ORDER_LIST;
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
	

	private void AutoRefresh() {
		refresh = true;
		page = 1;
		page2 = 1;
		
		if(!et_search_box.isFocused() || et_search_box.getText().equals(""))
		{
			getOrderListByOrderType(orderInfo.getType(), page, false);
		} else {
			getOrderListByName(orderInfo.getType(), et_search_box.getText().toString().trim(), page2, false);
		}
		
	}
	
	public void refresh() {
		refresh = true;
		if(!et_search_box.isFocused() || et_search_box.getText().equals("")){
			page = 1;
			getOrderListByOrderType(orderInfo.getType(), page);
		} else {
			page2 = 1;
			getOrderListByName(orderInfo.getType(), et_search_box.getText().toString().trim(), page2, false);
		}
		
		
	}
	
	protected void loadMore() {
		refresh  = false;
		if(!et_search_box.isFocused() || et_search_box.getText().equals("")){
			page++;
			getOrderListByOrderType(orderInfo.getType(), page);
		} else {
			page2++;
			getOrderListByName(orderInfo.getType(), et_search_box.getText().toString().trim(), page2, false);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		try {
			switch (msg.what) {
			case IResult.ORDER_LIST:
				list_view.onRefreshComplete();
				destroyDialog();
				OrderListEntity olEntity = (OrderListEntity) msg.obj;
				if (null != olEntity) {
					if (olEntity.getSuccess() == 1) {
						//根据关键字搜索的订单
						if(true == isfilterByName){	
							isfilterByName = false;
							if(refresh){
								filterOrderList = olEntity.getData();
								adapter.change(filterOrderList);
							} else {
								filterOrderList.addAll(olEntity.getData());
								adapter.change(filterOrderList);
							}
						} else {
							if(refresh){
								allOrderList = olEntity.getData();
								adapter.change(allOrderList);
							} else {
								allOrderList.addAll(olEntity.getData());
								adapter.change(allOrderList);
							}
						}
						
						if (page < olEntity.getPages()) {
							list_view.onLoadMoreComplete(false);
						} else {
							list_view.onLoadMoreComplete(true);
						}
						
						if(olEntity.getData().size() <= 0){
							ll_empty.setVisibility(View.VISIBLE);
							list_view.setVisibility(View.GONE);
						} else {
							ll_empty.setVisibility(View.GONE);
							list_view.setVisibility(View.VISIBLE);
						}
						
					} else {
						showToast(olEntity.getErrormsg());
						ll_empty.setVisibility(View.VISIBLE);
						list_view.setVisibility(View.GONE);
					}
				} else {
					showToast(IMessage.DATA_ERROR);
					ll_empty.setVisibility(View.VISIBLE);
					list_view.setVisibility(View.GONE);
				}
				break;
			case IResult.NET_ERROR:
				showToast(IMessage.NET_ERROR);
				ll_net_unavailable.setVisibility(View.VISIBLE);
				list_view.setVisibility(View.GONE);
				break;
			case IResult.DATA_ERROR:
				destroyDialog();
				showToast("获取数据错误，请稍后再试");
				Logger.dout(msg.getData().toString());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private class OrderAdapter extends BaseAdapter {
		List<OrderListEntry> listItems;

		public OrderAdapter(List<OrderListEntry> listItems) {
			if (listItems == null) {
				listItems = new ArrayList<OrderListEntry>();
			}
			this.listItems = listItems;
		}

		@Override
		public int getCount() {
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			return (listItems == null || listItems.size() == 0) ? null
					: listItems.get(position);
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
				convertView = LayoutInflater.from(OrderClassifyActivity.this).inflate(R.layout.order_item, null);
				holder.civ_patient_photo = (CircleImageView) convertView.findViewById(R.id.image_person);
				holder.civ_patient_photo.setImageResource(R.drawable.avatar_medium);
				holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
				holder.tv_chat_msg = (TextView) convertView.findViewById(R.id.textView_1);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				holder.iv_red_icon = (ImageView) convertView.findViewById(R.id.unreadLabel);
				holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
				holder.tv_service_state = (TextView) convertView.findViewById(R.id.tv_service_state);
				holder.ll_cut_line = (View) convertView.findViewById(R.id.ll_cut_line);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			OrderListEntry orderListEntry = listItems.get(position);
			if(orderListEntry != null) {
				imageLoader.displayImage(orderListEntry.getAVATAR(), holder.civ_patient_photo, Util.getOptions_pic());
				holder.tv_user_name.setText(orderListEntry.getCONTRACTOR());
				
				if(orderInfo.getType().equals("15")){
					holder.ll_cut_line.setVisibility(View.GONE);
					holder.tv_chat_msg.setVisibility(View.GONE);
				}else{
					holder.ll_cut_line.setVisibility(View.VISIBLE);
					holder.tv_chat_msg.setVisibility(View.VISIBLE);
					
					//XXX消息为文字时，getContent才能获取，待改善
					if(orderListEntry.getIM_TALK() != null && orderListEntry.getIM_TALK().getCONTENT() != null){
						holder.tv_chat_msg.setText(orderListEntry.getIM_TALK().getCONTENT());
					} else {
						holder.tv_chat_msg.setText("");
					}
				}
				
				//2014-12-02 09:46:15
				holder.tv_time.setText(orderListEntry.getADDTIME().substring(0,10));
				//XXX红点判断，暂时不加红点
				holder.iv_red_icon.setVisibility(View.GONE);
				holder.tv_price.setText("￥"+ orderListEntry.getORDERAMOUNT());
				holder.tv_service_state.setText(getOrderState(orderListEntry));
			}
			
			return convertView;
		}
		
		public void change(List<OrderListEntry> lists){
			if(lists == null){
				lists = new ArrayList<OrderListEntry>();
			}
			this.listItems = lists;
			notifyDataSetChanged();
		}
		
		public void add(List<OrderListEntry> list) {
			this.listItems.addAll(list);
			notifyDataSetChanged();
		}
	}

	class ViewHolder {
		private CircleImageView civ_patient_photo;
		private ImageView iv_red_icon;
		private TextView tv_user_name;
		private TextView tv_chat_msg;
		private TextView tv_time;
		private TextView tv_price;
		private TextView tv_service_state;
		private View ll_cut_line;
	}
	
	//'1'=>'图文咨询','2'=>'预约通话','3'=>'预约加号','4'=>'上门会诊',
	//'5'=>'院内陪诊','7'=>'免费挂号','11'=>'体检报告解读','12'=>'个人健康季评',
	//'14'=>'预约住院','15'=>'私人医生服务','16'=>'其他服务','30'=>'科研调查奖励'
	private boolean judgeServiceIsOpen(String serviceType){
		if("1".equals(serviceType)){
			if(1 == SharePrefUtil.getInt(Conast.FLAG_CONSULT_PICTURE)) {
				return true;
			}else{
				return false;
			}
		}else if("2".equals(serviceType)){
			if(1 == SharePrefUtil.getInt(Conast.FLAG_CONSULT_PHONE)) {
				return true;
			}else{
				return false;
			}
		}else if("3".equals(serviceType)){
			if(1 == SharePrefUtil.getInt(Conast.FLAG_JINJI_JIAHAO)) {
				return true;
			}else{
				return false;
			}
		}
//		else if("4".equals(serviceType)){
//			if(1 == SharePrefUtil.getInt(Conast.FLAG_SHANGMEN)) {
//				return true;
//			}else{
//				return false;
//			}
//		}
		else if("14".equals(serviceType)){
			if(1 == SharePrefUtil.getInt(Conast.FLAG_JINJI_ZHUYUAN)) {
				return true;
			}else{
				return false;
			}
		}else if("15".equals(serviceType)){
			if(1 == SharePrefUtil.getInt(Conast.FLAG_PRIVATE_DOCTOR)) {
				return true;
			}else{
				return false;
			}
		}
//		else if("16".equals(serviceType)){
//			return true;
//		}
		else if("".equals(serviceType)){
			order_list_img.setVisibility(View.GONE);
			ll_invite_patient.setVisibility(View.GONE);
			return true;
		}

		return false;
	}
	
	/**
	 * 初始化下拉框
	 */
	private void initAlertDialogue(){
		
	  myDialog = new AlertDialog.Builder(OrderClassifyActivity.this).create();
	  View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_order_type, null);
	  view.setMinimumHeight(MyApplication.getInstance().canvasHeight/2);
	  view.setMinimumWidth(MyApplication.getInstance().canvasWidth*3/4);
	  ListView listView = (ListView) view.findViewById(R.id.lv_selector);
	  SelectAdapter selectAdapter = new SelectAdapter(orders);
	  listView.setAdapter(selectAdapter);
	  
	  listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(!orders.get(position).getType().equals(orderInfo)){
					orderInfo = orders.get(position);
					page = 1;
					tv_order_name.setText(orderInfo.getName());
					getOrderListByOrderType(orderInfo.getType(), page, true);
				}
				myDialog.dismiss();
			}
	  });
	  
      myDialog.setView(view);
     
      //获取通知栏的高度
      int statusBarHeight = getStatusBarHeight(this);
      
      Window win = myDialog.getWindow();  
      LayoutParams params = new LayoutParams();
      params.x = 0;//设置x坐标  
      params.y = 0/*-(MyApplication.getInstance().canvasHeight/4 - topBarHeight - statusBarHeight)*/;//设置y坐标  
      win.setAttributes(params);  
      myDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog  
	}
	
	private class SelectAdapter extends BaseAdapter{

		ArrayList<OrderInformation> list;
		
		public SelectAdapter(ArrayList<OrderInformation> list){
			if(null != list){
				this.list = list;
			}else{
				list = new ArrayList<OrderInformation>();
			}
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder2 holder= null;
			if (convertView == null) {
				holder = new ViewHolder2();
				convertView = View.inflate(getApplicationContext(), R.layout.dialog_select_order_type_item, null);
				holder.orderTypeName = (TextView) convertView.findViewById(R.id.textView1);
				holder.orderTypeIcon = (ImageView) convertView.findViewById(R.id.iv_pic1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder2) convertView.getTag();
			}
			
			holder.orderTypeName.setText(list.get(position).getName());
			holder.orderTypeIcon.setImageDrawable(getResources().getDrawable(list.get(position).getIconId()));
			return convertView;
		}
		
	}
	
	private class ViewHolder2{
		ImageView orderTypeIcon;
		TextView orderTypeName;
	}

	
	private String getOrderState(OrderListEntry orderListEntry){
		int orderType = Integer.parseInt(orderListEntry.getORDERTYPE());
		int state = Integer.parseInt(orderListEntry.getSTATE());
		
		if(1 == orderType){	//图文咨询
			if(1 == state){
				return "待处理";
			}else{
				return "服务结束";
			}
		} else if(2 == orderType){
			if(1 == state){
				return "待处理";
			} else if(2 == state){
				return "等待通话";
			}else{
				return "服务结束";
			}
		} else if(15 == orderType){
			if(1 == state){
				return "服务期内";
			}else{
				return "服务结束";
			}
		} else {
			if(1 == state){
				return "待处理";
			}else if(2 == state){
				return "等待用户支付";
			}else if(3 == state){
				return "用户支付成功";
			}else{
				return "服务结束";
			}
		}
	}
	
	private int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
	
	/**
	* 关闭软键盘
	*/
	public static void closeKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
	
