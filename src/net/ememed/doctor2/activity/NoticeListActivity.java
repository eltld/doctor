package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.NoticeListBean;
import net.ememed.doctor2.entity.NoticeListDataBean;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NoticeListActivity extends BasicActivity {

	private ListView listView;
	private NoticeListAdapter adapter;
	private NoticeListBean nb = null;
	private List<NoticeListDataBean> list = null;
	private TextView top_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_list);
		initView();
	}

	private void initView() {
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("消息中心");
		listView = (ListView) findViewById(R.id.listview);
		adapter = new NoticeListAdapter(this);
		listView.setAdapter(adapter);
		getNoticeInfo();
	}

	//获取消息通知列表 
	public void getNoticeInfo() {
		if (!NetWorkUtils.detect(this)) {
			showToast("未连接到网络");
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
		 System.out.println("token---"+SharePrefUtil.getString(Conast.ACCESS_TOKEN)+"---memberid-----"+SharePrefUtil.getString(Conast.Doctor_ID));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_notice_list,
				NoticeListBean.class, params, new Listener() {

					@Override
					public void onResponse(Object arg0) {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.obj = arg0;
						msg.what = IResult.NOTICE_INFO;
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
	
	//拒绝入群申请
		public void setDecline(String noticeid,String memberid) {
			if (!NetWorkUtils.detect(this)) {
				showToast("未连接到网络");
				return;
			}
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("noticeid", noticeid);
			params.put("memberid", memberid);
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.refused_add_group,
					null, params, new Listener() {

						@Override
						public void onResponse(Object arg0) {
							// TODO Auto-generated method stub
//							showToast(msg)
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
		
		//群组加人
		public void setConsent(String groupnum,String apply_memberid) {
			if (!NetWorkUtils.detect(this)) {
				showToast("未连接到网络");
				return;
			}
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("groupnum", groupnum);
			params.put("apply_memberid", apply_memberid);
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("is_invite", "0");
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.add_group_user,
					null, params, new Listener() {

						@Override
						public void onResponse(Object arg0) {
							// TODO Auto-generated method stub
							
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

	public void getData(List<NoticeListDataBean> list) {
		if (list == null) {
			list = new ArrayList<NoticeListDataBean>();
		}
		adapter.getData(list);
	}
	
	private void refresh(){
		getNoticeInfo();
	}

	@Override
	protected void onResult(Message msg) {
		// TODO Auto-generated method stub
		super.onResult(msg);
		switch (msg.what) {
		case IResult.NOTICE_INFO:
			destroyDialog();
			nb = (NoticeListBean) msg.obj;
			if (nb != null && nb.getSuccess().equals("1")) {
				list = (List<NoticeListDataBean>) nb.getData();
				getData(list);
			}
			break;
		case IResult.DATA_ERROR:
			destroyDialog();
			showToast("没有数据。。。");
			break;

		default:
			break;
		}
	}

	class NoticeListAdapter extends BaseAdapter implements OnClickListener{
		private List<NoticeListDataBean> list;
		private Context context;
		private ViewHolder holder = null;
		Button btn_decline;  //拒绝
		Button btn_consent;  //同意
		private String groupnum;
		private String apply_memberid;
		private String noticeid;

		public NoticeListAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (list == null) {
				return 0;
			}
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			groupnum=list.get(arg0).getGROUPNUM();
			apply_memberid=list.get(arg0).getDOCTOR_INFO().getMEMBERID();
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.notice_list_item, null);
				holder.img_avatar = (ImageView) view
						.findViewById(R.id.img_avatar);
				imageLoader.displayImage(list.get(arg0).getDOCTOR_INFO()
						.getAVATAR(), holder.img_avatar);
				holder.txt_name = (TextView) view.findViewById(R.id.txt_name);
				holder.txt_name.setText(list.get(arg0).getDOCTOR_INFO()
						.getREALNAME());
				holder.txt_info = (TextView) view.findViewById(R.id.txt_info);
				holder.txt_info.setText(list.get(arg0).getMSGDESC());
				
				view.setTag(holder);
			}
			
			Button btn_decline=(Button) view.findViewById(R.id.btn_decline);
			Button btn_consent=(Button) view.findViewById(R.id.btn_consent);
			if(list.get(arg0).getTYPE().equals("1")&& list.get(arg0).getRESULT().equals("0")){
				
					btn_decline.setVisibility(view.VISIBLE);
					btn_decline.setOnClickListener(this);
					btn_consent.setVisibility(view.VISIBLE);
					btn_consent.setOnClickListener(this);
			}
			
			holder = (ViewHolder) view.getTag();
			return view;
		}

		public void getData(List<NoticeListDataBean> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		class ViewHolder {
			private ImageView img_avatar;
			private TextView txt_name;
			private TextView txt_info;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId()==R.id.btn_decline){
				setDecline(noticeid,apply_memberid);
			}else if(v.getId()==R.id.btn_consent){
				setConsent(groupnum,apply_memberid);
			}
			refresh();
		}

	}

}
