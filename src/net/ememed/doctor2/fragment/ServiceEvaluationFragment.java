package net.ememed.doctor2.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.SaveEmailActivity;
import net.ememed.doctor2.activity.ServiceEvaluationActivity;
import net.ememed.doctor2.entity.DoctorInfo;
import net.ememed.doctor2.entity.EvaluationEntry;
import net.ememed.doctor2.entity.EvaluationInfo;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.NewsItem;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.ColoredRatingBar;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class ServiceEvaluationFragment extends Fragment implements Handler.Callback{
	private String fragmentKey = "";
    private BasicActivity activity = null;
    private Handler mHandler;
    
	private LinearLayout ll_view_content;
	private FrameLayout mContentView = null;
	private RefreshListView lvCustomEvas;
	private CustomEvaluateAdapter adapter;
	
	private int page = 1;
	private LinearLayout ll_empty;
	private boolean refresh = true;
	private String request_type;
	private boolean init;
	private TextView tv_notice;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        mHandler = new Handler(this);
	        activity = (BasicActivity) getActivity();
	        
	        request_type = getArguments().getString("request_type");
	        fragmentKey = getArguments().getString("fragmentKey");
//	        System.out.println("TestFragment onCreate fragmentKey : "+fragmentKey);
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	    	View view = inflater.inflate(R.layout.root_layout,null);
	    	mContentView = (FrameLayout) view.findViewById(R.id.mainView);

	    	ll_view_content = (LinearLayout)LayoutInflater.from(activity).inflate(R.layout.fragment_service_eval, null);
	    	tv_notice = (TextView) ll_view_content.findViewById(R.id.tv_notice);
	    	tv_notice.setText(getString(R.string.no_evaluation));
	    	ll_empty = (LinearLayout)ll_view_content.findViewById(R.id.ll_empty);
	    	lvCustomEvas = (RefreshListView)ll_view_content.findViewById(R.id.lv_custom_evas);
	    	adapter = new CustomEvaluateAdapter(null);
			lvCustomEvas.setAdapter(adapter);
	    	mContentView.addView(ll_view_content);
	    	lvCustomEvas.setOnRefreshListener(new IOnRefreshListener() {
				
				@Override
				public void OnRefresh() {
					refresh();
				}
			});
			lvCustomEvas.setOnLoadMoreListener(new IOnLoadMoreListener() {
				
				@Override
				public void OnLoadMore() {
					loadMore();
				}
			});
			return view;
	    }
	    
	    @Override
	    public void setUserVisibleHint(boolean isVisibleToUser) {
	          super.setUserVisibleHint(isVisibleToUser);
	          if (isVisibleToUser) {
	              //fragment可见时执行加载数据或者进度条等
	        	  if (!init) {
	        		  getCommentList(request_type,page );
	        	  }
	          } else {
	              //不可见时不执行操作
//	        	  getCommentList(request_type,page );
	          }
	    }
	    
	    protected void refresh() {
	    	refresh  = true;
			page = 1;
			getCommentList(request_type, page);
		}

		protected void loadMore() {
			refresh = false;
			page++;
			getCommentList(request_type, page);
		}

	    @Override
	    public void onResume() {
	    	super.onResume();
	    	
	    }
		
		protected void sendMessage(int what, Object obj) {
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = obj;
			mHandler.sendMessage(msg);
		}
		
	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
//	        System.out.println("TestFragment onSaveInstanceState fragmentKey : "+fragmentKey);
	    }
	    
		@Override
		public boolean handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case IResult.RESULT:
					lvCustomEvas.onRefreshComplete();
					activity.destroyDialog();
					EvaluationInfo info = (EvaluationInfo)msg.obj;
					if (null != info) {
						if (info.getSuccess() == 1) {
							init = true;
							ll_empty.setVisibility(View.GONE);
							lvCustomEvas.setVisibility(View.VISIBLE);
							if(refresh){
								if (info.getData()==null ||info.getData().size()==0) {
									ll_empty.setVisibility(View.VISIBLE);
									lvCustomEvas.setVisibility(View.GONE);
								} else {
									ll_empty.setVisibility(View.GONE);
									lvCustomEvas.setVisibility(View.VISIBLE);
									adapter.change(info.getData());		
								}
							} else {
								adapter.add(info.getData());
							}
							if (page < info.getPages()) {
								lvCustomEvas.onLoadMoreComplete(false);
							} else {
								lvCustomEvas.onLoadMoreComplete(true);
							}
							
						} else {
							ll_empty.setVisibility(View.VISIBLE);
							lvCustomEvas.setVisibility(View.GONE);
						}
					} else {
						ll_empty.setVisibility(View.VISIBLE);
						lvCustomEvas.setVisibility(View.GONE);
					}
					
					break;
				case IResult.END:
					
					break;
				case IResult.NET_ERROR:
					activity.destroyDialog();
//					lvCustomEvas.onRefreshComplete();
					ll_empty.setVisibility(View.VISIBLE);
					lvCustomEvas.setVisibility(View.GONE);
					break;

				case IResult.DATA_ERROR:
					activity.destroyDialog();
//					lvCustomEvas.onRefreshComplete();
					ll_empty.setVisibility(View.VISIBLE);
					lvCustomEvas.setVisibility(View.GONE);
					break;
				default:
					break;
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
		
		private void getCommentList(String type,int page){
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("type", type);
			params.put("page", page+"");
			params.put("stype", "");
			if(NetWorkUtils.detect(activity)){
				activity.loading(getString(R.string.progressdialog_loading));
				MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_comment_List,
						EvaluationInfo.class, params, new Response.Listener() {
							@Override
							public void onResponse(Object response) {

								Message message = new Message();
								message.obj = response;
								message.what = IResult.RESULT;
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
		
		private class CustomEvaluateAdapter  extends BaseAdapter {
			List<EvaluationEntry> listItems;
			public CustomEvaluateAdapter(List<EvaluationEntry> listItems) {
				if(listItems==null){
					listItems = new ArrayList<EvaluationEntry>();
				}
				this.listItems = listItems;
			}

			@Override
			public int getCount() {
				return listItems.size();
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder = null;
				if(convertView==null){
					holder = new ViewHolder();
					convertView = LayoutInflater.from(activity).inflate(R.layout.custom_evaluate_item, null);
					holder.image_person =  (CircleImageView) convertView.findViewById(R.id.image_person);
					holder.iv_evaluat_type =  (ImageView) convertView.findViewById(R.id.iv_evaluat_type);
					holder.tv_content_detail = (TextView) convertView.findViewById(R.id.tv_content_detail);
					holder.customName = (TextView) convertView.findViewById(R.id.tv_custom_name);
					holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
					holder.date = (TextView) convertView.findViewById(R.id.tv_custom_date);
					holder.goods = (ColoredRatingBar) convertView.findViewById(R.id.rtb_service_goods);
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				EvaluationEntry entry = listItems.get(position);
				if (null != entry) {
					holder.customName.setText(entry.getUSERNAME());
					holder.tv_content.setText(entry.getRATECONTENT());
					holder.date.setText(entry.getDSRTIME());
				
					activity.imageLoader.displayImage(entry.getUSERAVATAR(), holder.image_person,Util.getOptions_big_avatar());
					if (!TextUtils.isEmpty(entry.getDSRATTITUDE())) {
						holder.goods.setRating(Integer.valueOf(entry.getDSRATTITUDE())/2);	
					}
					if (!TextUtils.isEmpty(entry.getSTYPE())) {
						holder.tv_content_detail.setText(PublicUtil.getServiceNameByid(entry.getSTYPE()));
						holder.iv_evaluat_type.setImageResource(PublicUtil.getServiceDrawableByServiceid(entry.getSTYPE()));
					}
					
				}
				return convertView;
			}
			public void change(List<EvaluationEntry> lists){
				if(lists == null){
					lists = new ArrayList<EvaluationEntry>();
				}
				this.listItems = lists;
				notifyDataSetChanged();
			}
			public void add(List<EvaluationEntry> list) {
				this.listItems.addAll(list);
				notifyDataSetChanged();
			}
		}
		class ViewHolder{
			public TextView tv_content_detail;
			public ImageView iv_evaluat_type;
			public TextView tv_content;
			public CircleImageView image_person;
			ImageView userPhoto;
			TextView customName;
			TextView date;
			ColoredRatingBar goods;
			EditText content;
		}
}
