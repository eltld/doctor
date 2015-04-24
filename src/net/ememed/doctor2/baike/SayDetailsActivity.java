package net.ememed.doctor2.baike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import de.greenrobot.event.ShareEnd;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.adapter.CommentAdapter;
import net.ememed.doctor2.baike.adapter.PutImageAdapter;
import net.ememed.doctor2.baike.adapter.ShuoshuoAdapter;
import net.ememed.doctor2.baike.entity.BaikeShuoshuoInfo;
import net.ememed.doctor2.baike.entity.SaysCommentInfo;
import net.ememed.doctor2.baike.entity.SaysDetailEntry;
import net.ememed.doctor2.baike.fragment.BaikeHomeFragment;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.ShareSdkUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.MyGridView;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class SayDetailsActivity extends BasicActivity{

	private ListView lv_comment;
	private WebSettings webSettings;
//	private WebView wb;
	private ProgressBar webview_progress;
	private String says_id;
	private Boolean need_adjust = false;
	private TextView tv_title;
	private TextView tv_time;
	private TextView tv_author;
	private TextView tv_read_num;
	private TextView tv_praise_num;
	private TextView tv_share_num;
	private EditText et_comment;
	private LinearLayout ll_pic_area;
	private MyGridView gridview;
	private Button btn_send;
//	private ImageView iv_praise;
	private Button iv_praise;
	private String refer_commentid = null;
	private String refer_name = null;
	private CommentAdapter adapter;
	private PutImageAdapter adapter2;
	private List<SaysCommentInfo> commentList;
	private String commentid = null;
	private TextView tv_comment_num;
	private RelativeLayout rl_content_area;
	private BaikeShuoshuoInfo detailInfo = null;
	private List<String> pics = null;
	private List<String> pics_thumb = null;
	private ScrollView scrollView1;
	private LinearLayout ll_all_area;
	//private boolean isFirstEntry = true;
	private FrameLayout fl_topLayout;
	private ShareSdkUtil share = null;
	private boolean isMyBaike;	//是不是自己发的说说
	private InnerReceiver receiver;
	private IntentFilter filter;
	private TextView tv_shuoshuo;
	private WebView wv_oauth;
	private String author;
	private PopupWindow popup;
	private AlertDialog myDialog;
//	private BaikeShuoshuoInfo currentShuoshuo = null;
	private boolean isDeleteShuoshuo = false;
	
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		
		setContentView(R.layout.activity_say_details);
		says_id = getIntent().getStringExtra("says_id");
		isMyBaike = getIntent().getBooleanExtra("is_my_baike", false);
		need_adjust = getIntent().getBooleanExtra("need_adjust", false);
		author = getIntent().getStringExtra("name");
		
		refer_commentid = getIntent().getStringExtra("refer_comment_id");
		refer_name = getIntent().getStringExtra("refer_name");
		
		if((!TextUtils.isEmpty(refer_commentid)) && (!TextUtils.isEmpty("refer_name"))){
			Message message = new Message();
			message.obj = refer_name;
			message.arg1 = Integer.parseInt(refer_commentid);
			message.what = IResult.COMMENT_OTHER;
			handler.sendMessage(message);
		}
		
		receiver = new InnerReceiver();
		filter = new IntentFilter();
		filter.addAction(MyApplication.WECHAT_SHARE_SUCCESS);
		registerReceiver(receiver, filter);
		
		initView();
	}
	
	public void onEvent(ShareEnd event) {
		destroyDialog();
	}
	
	public void initView(){
		
		fl_topLayout = (FrameLayout) findViewById(R.id.fl_top_title);
		((TextView) findViewById(R.id.top_title)).setText("说说详情");
		((ImageView) findViewById(R.id.iv_right_fun_2)).setVisibility(View.VISIBLE);
		
		rl_content_area = (RelativeLayout) findViewById(R.id.rl_content_area);
		rl_content_area.setVisibility(View.GONE);
		
//		iv_praise = (ImageView) findViewById(R.id.iv_praise);
		iv_praise = (Button) findViewById(R.id.iv_praise);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_author = (TextView) findViewById(R.id.tv_author);
		tv_read_num = (TextView) findViewById(R.id.tv_read_num);
		tv_praise_num = (TextView) findViewById(R.id.tv_praise_num);
		tv_share_num = (TextView) findViewById(R.id.tv_share_num);
		tv_comment_num = (TextView) findViewById(R.id.tv_comment_num);
		
		lv_comment = (ListView) findViewById(R.id.lv_comment);
		adapter = new CommentAdapter(null,this,handler, isMyBaike);
		lv_comment.setAdapter(adapter);
		
		ll_pic_area = (LinearLayout) findViewById(R.id.ll_pic_area);
		gridview = (MyGridView) findViewById(R.id.gridview);
		gridview.setColumnWidth(Util.dip2px(this, 70));
		
		tv_shuoshuo = (TextView) findViewById(R.id.tv_shuoshuo);
		wv_oauth = (WebView) findViewById(R.id.wv_oauth);
		
		btn_send = (Button) findViewById(R.id.btn_send);
		et_comment = (EditText) findViewById(R.id.et_comment);
		et_comment.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					btn_send.setVisibility(View.VISIBLE);
				} else {
					btn_send.setVisibility(View.GONE);
				}
			}
		});
		
		scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
		ll_all_area = (LinearLayout) findViewById(R.id.ll_all_area);
		
		initMenuPopupWindow();
		getSayDetail();
	}
	
	private void getSayDetail(){
		if (NetWorkUtils.detect(SayDetailsActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("saysid", says_id);
			params.put("app_version", PublicUtil.getVersionName(this));
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_says_detail, SaysDetailEntry.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_SAYS_DETAIL;
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
	
	private void setShare(int channel_type){
		if (NetWorkUtils.detect(this)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("other_doctorid",detailInfo.getDOCTORID());
			params.put("saysid", detailInfo.getSAYSID());
			params.put("channel_type", ""+channel_type);
			
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_share, CommonResponseEntity.class, params,
				new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.SET_SHARE;
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
	
	private void givePraise(){
		if (NetWorkUtils.detect(SayDetailsActivity.this)) {
			loading(null);
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
	
	private void commentShuoshuo(String refer_commentid){
		if (NetWorkUtils.detect(SayDetailsActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("saysid", says_id);
			params.put("content", et_comment.getText().toString().trim());
			params.put("anonymous", "0");
			if(null != refer_commentid){
				params.put("refer_commentid", refer_commentid);
			}
		
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_comment, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.COMMENT_SHUOSHUO;
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
	
	private void delelteComment(String commentid){
		if (NetWorkUtils.detect(SayDetailsActivity.this)) {
			loading(null);
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
	
	private void deleteShuoshuo() {
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("saysid", says_id);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.delete_says, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.DELETE_SAYS;
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
	
	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);
		try {
			switch (msg.what) {
			case IResult.GET_SAYS_DETAIL:
				destroyDialog();
				SaysDetailEntry response = (SaysDetailEntry) msg.obj;
				if (null != response) {
					if (1 == response.getSuccess()) {
						
						rl_content_area.setVisibility(View.VISIBLE);
//						tv_shuoshuo.setText(Html.fromHtml(response.getData().getCONTENT()));
						wv_oauth.loadData(response.getData().getCONTENT(),
								"text/html; charset=UTF-8", null);
						
					/*	if(true == need_adjust){
							wb.addOnLayoutChangeListener(new OnLayoutChangeListener() {
								@Override
								public void onLayoutChange(View v, int left, int top, int right,
										int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
									scrollToBottom(scrollView1, ll_all_area);
								}
							});
						}*/
						detailInfo = response.getData();
						commentList = detailInfo.getCOMMENT_LIST().getList();
						if(commentList != null && commentList.size() > 0){
							adapter.change(commentList);
						}
						
						pics = detailInfo.getPICS();
						pics_thumb = detailInfo.getPICS_THUMB();
						if(pics != null && pics.size() > 0){
							ll_pic_area.setVisibility(View.VISIBLE);
							List<Map<String, String>> picMapList = new ArrayList<Map<String,String>>();
							for(int i = 0; i < pics.size(); i++){
								HashMap<String, String> tempMap = new HashMap<String, String>();
								//tempMap.put("path", curPicPath);
								tempMap.put("url", pics_thumb.get(i));
								tempMap.put("url_big", pics.get(i));
								picMapList.add(tempMap);
							}
							
							adapter2 = new PutImageAdapter(picMapList, this, handler, false);
							gridview.setAdapter(adapter2);
						} else {
							ll_pic_area.setVisibility(View.GONE);
						}
						
						author = detailInfo.getREALNAME();
						updateView(response.getData());
					} else {
						showToast(response.getErrormsg());
					}
				} else {
					showToast("获取说说详情失败");
				}
				
				break;
			case IResult.GIVE_PRAISE:
				destroyDialog();
				CommonResponseEntity response2 = (CommonResponseEntity)msg.obj;
				if (null != response2) {
					if (1 == response2.getSuccess()) {
						int praise_num = Integer.parseInt(detailInfo.getPRAISE_NUM())+1;
						detailInfo.setPRAISE_NUM(String.valueOf(praise_num));
						tv_praise_num.setText(detailInfo.getPRAISE_NUM());
						if(!detailInfo.getIS_PRAISED()){
							detailInfo.setIS_PRAISED(true);
//							iv_praise.setImageDrawable(getResources().getDrawable(R.drawable.btn_already_praise));
							setButtonText(true);
						}
						setResult(RESULT_OK);
					} else {
						showToast(response2.getErrormsg());
					}
				}
				break;
			case IResult.COMMENT_SHUOSHUO:
				CommonResponseEntity response3 = (CommonResponseEntity)msg.obj;
				if (null != response3) {
					if (1 == response3.getSuccess()) {
						int comment_num = Integer.parseInt(detailInfo.getCOMMENT_NUM())+1;
						detailInfo.setCOMMENT_NUM(String.valueOf(comment_num));
						showToast("发表评论成功");
						setResult(RESULT_OK);
						//XXX 需更新评论列表
						getSayDetail();
					} else {
						showToast(response3.getErrormsg());
					}
				}
				break;
			case IResult.COMMENT_OTHER:
				//弹出软键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);     
				imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);  
				String name = (String)msg.obj;
				et_comment.requestFocus();
				et_comment.setHint("@"+name);
				refer_commentid = msg.arg1 + "";
				//commentShuoshuo(msg.arg1+"");
				break;
			case IResult.DELETE_COMMENT_INNER:
				commentid = msg.arg1 + "";
				delelteComment(commentid);
				break;
			case IResult.DELETE_COMMENT:
				destroyDialog();
				CommonResponseEntity response4 = (CommonResponseEntity)msg.obj;
				if (null != response4) {
					if (1 == response4.getSuccess()) {
						int comment_num = Integer.parseInt(detailInfo.getCOMMENT_NUM())-1;
						detailInfo.setCOMMENT_NUM(String.valueOf(comment_num));
						showToast("删除评论成功");
						setResult(RESULT_OK);
						//需更新评论列表
						updateCommnetList();
					} else {
						showToast(response4.getErrormsg());
					}
				}
				break;
			case IResult.DELETE_SAYS:
				destroyDialog();
				CommonResponseEntity response5 = (CommonResponseEntity) msg.obj;
				if (null != response5) {
					if (1 == response5.getSuccess()) {
						isDeleteShuoshuo  = true;
						setResult(RESULT_OK);
						finish();
					} else {
						showToast(response5.getErrormsg());
					}
				}
				break;
			case IResult.NET_ERROR:
				destroyDialog();
				showToast(getString(R.string.net_error));
				break;
			case IResult.DATA_ERROR:
				destroyDialog();
				showToast("数据出错！");
				break;
			case ShareSdkUtil.onError:
				destroyDialog();
				showToast("分享失败");
				break;
			case ShareSdkUtil.onComplete:
				destroyDialog();
				showToast("分享成功");
				setShare(msg.arg1);
				break;
			case ShareSdkUtil.onCancel:
				destroyDialog();
				showToast("取消分享");
				break;
			case IResult.SET_SHARE:
				destroyDialog();
				CommonResponseEntity response6 = (CommonResponseEntity)msg.obj;
				if(1 == response6.getSuccess()){
					int share_count = Integer.parseInt(detailInfo.getSHARE_COUNT()) + 1;
					detailInfo.setSHARE_COUNT(String.valueOf(share_count));
					tv_share_num.setText(String.valueOf(share_count));
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void scrollToBottom(final View scroll, final View inner) {

		handler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}

				int offset = inner.getMeasuredHeight() - scroll.getHeight();
				if (offset < 0) {
					offset = 0;
				}

				scroll.scrollTo(0, offset);
			}
		});
	}
	
	/**
	 * 更新评论列表（删除评论后，本地更新列表，不用重新请求服务器）
	 */
	private void updateCommnetList() {
		
		for(int i= 0 ; i < commentList.size(); i++){
			if(commentList.get(i).getCOMMENTID().equals(commentid)){
				commentList.remove(i);
				break;
			}
		}
		adapter.change(commentList);
		
		if(commentid.equals(refer_commentid)){
			refer_commentid = null;
			
			if(TextUtils.isEmpty(et_comment.getText().toString().trim())){
				et_comment.setHint("请输入要发表评论的内容");
			}
		}
		commentid = null;
	}

	private void updateView(BaikeShuoshuoInfo info){
		
		if(!TextUtils.isEmpty(info.getTITLE())){
			tv_title.setVisibility(View.VISIBLE);
			tv_title.setText(info.getTITLE());
		} else {
			tv_title.setVisibility(View.GONE);
		}
		
		tv_time.setText(info.getCREATE_TIME());
		tv_author.setText(info.getREALNAME());
		tv_read_num.setText(info.getHITS());
		tv_praise_num.setText(info.getPRAISE_NUM());
		tv_comment_num.setText("共"+info.getCOMMENT_NUM()+"条评论");
		tv_share_num.setText(info.getSHARE_COUNT());
		
//		if(info.getIS_PRAISED()){
//			iv_praise.setImageDrawable(getResources().getDrawable(R.drawable.btn_already_praise));
//		} else {
//			iv_praise.setImageDrawable(getResources().getDrawable(R.drawable.btn_praise));
//		}
		
		if(info.getIS_PRAISED()){
			setButtonText(true);
		} else {
			setButtonText(false);
		}
	}
	
	public void doClick(View view){
		Intent intent = null;
		switch(view.getId()){
		case R.id.btn_back:
			finish();
			break;
		case R.id.iv_right_fun_2:
			if(null != popup){
				popup.showAsDropDown(view, 0, 40);
			}
			break;
		case R.id.iv_praise:
			givePraise();
			break;
		case R.id.btn_send:
			if(TextUtils.isEmpty(et_comment.getText().toString().trim())){
				showToast("请输入要发表评论的内容");
			} else {
				commentShuoshuo(refer_commentid);
				refer_commentid = null;
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);     
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				et_comment.setText("");
				et_comment.setHint("请输入要发表评论的内容");
				tv_time.requestFocus();
			}
			break;
		default:
			break;
		}
	}

	private void shareShuoshuo() {
		if(detailInfo != null){
			String name = null;
			String text = null;
			String pic = null;
			
			if(isMyBaike){
				name = SharePrefUtil.getString(Conast.Doctor_Name);
			} else {
				name = author.trim();
			}
			
			String title = "薏米网-"+name+"的说说";
			
			
			if(!TextUtils.isEmpty(detailInfo.getTITLE())){
				text = "来自薏米网"+name+"医生的原创--"+detailInfo.getTITLE();
			} else {
				if(detailInfo.getCONTENT().length() > 10){
					text = "来自薏米网"+name+"医生的原创--"+detailInfo.getCONTENT().substring(0,10);
				} else {
					text = "来自薏米网"+name+"医生的原创--"+detailInfo.getCONTENT();
				}
			}
			
			if(pics_thumb != null && pics_thumb.size()>0){
				pic = pics_thumb.get(0); 
			}
			
			if(null == share){
				share = new ShareSdkUtil(this, handler, ShareSdkUtil.SHARE_BAIKE_SAYS);
			}
			
			share.initModePopupWindow( fl_topLayout, title, text, detailInfo.getSHARE_URL(), pic);
		}
	}
	
	private void initMenuPopupWindow() {
		LinearLayout ll_menu = (LinearLayout) getLayoutInflater().inflate(R.layout.say_detail_menu, null);
		LinearLayout ll_delete = (LinearLayout) ll_menu.findViewById(R.id.ll_delete);
		LinearLayout ll_share = (LinearLayout) ll_menu.findViewById(R.id.ll_share);
		LinearLayout ll_home = (LinearLayout) ll_menu.findViewById(R.id.ll_home);
		
		if(isMyBaike){	//自己的说说才有删除选项
			ll_delete.setVisibility(View.VISIBLE);
		} else {
			ll_delete.setVisibility(View.GONE);
		}
		
		ll_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != popup){
					popup.dismiss();
//					deleteShuoshuo();
					showAlertDialogue();
				}
			}
		});
		
		ll_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != popup){
					popup.dismiss();
					shareShuoshuo();
				}
			}
		});
		
		ll_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent(SayDetailsActivity.this, BaikeHomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		popup = new PopupWindow(ll_menu,MyApplication.getInstance().canvasWidth *2/5, LayoutParams.WRAP_CONTENT);
		popup.setOutsideTouchable(true);
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popup.setFocusable(true);
	}

	private class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(MyApplication.WECHAT_SHARE_SUCCESS)){
				Logger.iout("测试", "收到微信分享说说成功的广播");
				int type = intent.getIntExtra("share_type", ShareSdkUtil.SHARE_TO_WECHAT);
				int source = intent.getIntExtra("share_source", ShareSdkUtil.SHARE_BAIKE_HOME);
				if(source == ShareSdkUtil.SHARE_BAIKE_SAYS){
					setShare(type);
					Logger.iout("测试", "说说分享统计");
				}
			}
		}
	}
	
	private void setButtonText(boolean isPressed){
		if(isPressed){
//			iv_praise.setPressed(true);
			iv_praise.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_praise_pressed));
			iv_praise.setText("已赞");
		} else {
//			iv_praise.setPressed(false);
			iv_praise.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_praise_normal));
			iv_praise.setText("点赞");
		}
	}
	
	/**
	 * 初始化删除信息的提示框
	 */
	private void showAlertDialogue() {
		if (null == myDialog) {
			myDialog = new AlertDialog.Builder(this).create();
			View view = LayoutInflater.from(this).inflate(R.layout.message_list_alert_dialog, null);

			 android.view.ViewGroup.LayoutParams lps = view.getLayoutParams();

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
	protected void onStop() {
		super.onStop();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_comment);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_praise);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_says_detail);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.delete_comment);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_share);
		
		Intent intent = new Intent();
		intent.putExtra("says_detail", detailInfo);
		intent.putExtra("is_delete_says", isDeleteShuoshuo);
		intent.putExtra("is_my_baike", isMyBaike);
		intent.setAction(BaikeHomeFragment.UPDATE_MY_SAYS_LIST);
		sendBroadcast(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
