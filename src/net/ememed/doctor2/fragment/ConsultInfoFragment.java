package net.ememed.doctor2.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.MainActivity;
import net.ememed.doctor2.activity.ShowBigImage;
import net.ememed.doctor2.activity.WebViewActivity;
import net.ememed.doctor2.db.ConsultInfoTable;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.NewsEntity;
import net.ememed.doctor2.entity.NewsItem;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

/** 咨询详情列表 */
public class ConsultInfoFragment extends Fragment implements Handler.Callback, OnClickListener {
	private String news_title = "";
	private BasicActivity activity = null;
	private Handler mHandler;

	private TextView tv_none = null;
	private LinearLayout ll_view_content;
	private FrameLayout mContentView = null;
	private RefreshListView lvCustomEvas;

	private static final String TAG = ConsultInfoFragment.class.getSimpleName();

	private ViewPager viewPager;
	private List<ImageView> imageViews; // 滑动的图片
	private List<View> dots; // 图片标题正文的那些点
	private int currentItem = 0;// 当前图片的索引号
	private TextView tv_title;

	private View view_header;
	private String type_id = "0";
	private int page = 1;
	private int totalpages = 1;
	private boolean refresh = true;
	private CustomEvaluateAdapter new_adapter = new CustomEvaluateAdapter(null);
	private boolean init = false;
	private LinearLayout ll_dot_content;
	private LinearLayout ll_net_unavailable;
	private LinearLayout ll_empty;

	private ConsultInfoTable table = new ConsultInfoTable();
	private List<NewsItem> focusList;

	public ConsultInfoFragment() {
		this.activity = (BasicActivity) getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler(this);
		activity = (BasicActivity) getActivity();
		// setRetainInstance(true);
		news_title = getArguments().getString("news_title");
		type_id = getArguments().getString("type_id");
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// fragment可见时执行加载数据或者进度条等
			Logger.dout(TAG + "isVisibleToUser");
			
			if (!init) {// init 首次进入加载数据
				getNewsListFromNet(page, type_id);
			} else {
				
			}
		} else {
			// 不可见时不执行操作
			Logger.dout(TAG + "unVisibleToUser");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.root_layout, null);
		mContentView = (FrameLayout) view.findViewById(R.id.mainView);

		ll_view_content = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.fragment_news_info, null);
		lvCustomEvas = (RefreshListView) ll_view_content.findViewById(R.id.lv_contact_class);
		ll_net_unavailable = (LinearLayout) ll_view_content.findViewById(R.id.ll_net_unavailable);
		ll_empty = (LinearLayout) ll_view_content.findViewById(R.id.ll_empty);
		ll_net_unavailable.setOnClickListener(this);
		ll_empty.setOnClickListener(this);

		view_header = (View) inflater.inflate(R.layout.news_info_title_pic, null);
		ll_dot_content = (LinearLayout) view_header.findViewById(R.id.ll_dot_content);
		
		lvCustomEvas.setAdapter(new_adapter);
		mContentView.addView(ll_view_content);
		addListener();
		getNewsListFromDB(page, type_id);
		return view;
	}

	private void addListener() {
		lvCustomEvas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				NewsItem entry = (NewsItem) new_adapter.getItem(position - lvCustomEvas.getHeaderViewsCount());
				Intent intent = new Intent(activity, WebViewActivity.class);
				intent.putExtra("type", "InfoFragment");
				intent.putExtra("pic", entry.getPIC());
				intent.putExtra("title", entry.getTITLE());
				intent.putExtra("tabid", entry.getID());
				intent.putExtra("url", entry.getFURL());
				intent.putExtra("subtitle", entry.getSUBTITLE());
				intent.putExtra("allowcomment", entry.getALLOWCOMMENT());
				startActivity(intent);
			}
		});
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
	}

	protected void refresh() {
		refresh = true;
		page = 1;
		getNewsListFromNet(page, type_id);
	}

	protected void loadMore() {
		refresh = false;
		page++;
		getNewsListFromNet(page, type_id);
	}

	private void getNewsListFromNet(int page, String phonetypeid) {

		if (NetWorkUtils.detect(activity)) {
			if(new_adapter.getCount()==0)
				activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("phonetypeid", phonetypeid);
			params.put("page", page + "");
			params.put("type", "doctor");
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_news_list, NewsEntity.class, params, new Response.Listener() {
				@Override
				public void onResponse(Object response) {
					Message message = mHandler.obtainMessage();
					message.obj = response;
					message.what = IResult.NEWS_LIST;
					mHandler.sendMessage(message);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Message message = mHandler.obtainMessage();
					message.obj = error.getMessage();
					message.what = IResult.DATA_ERROR;
					mHandler.sendMessage(message);
				}
			});
		} else {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
		}

	}

	private void getNewsListFromDB(int page, String phonetypeid) {

		Message message = Message.obtain();
		message.what = IResult.LOCAL_CONSULT_INFO;
		mHandler.sendMessage(message);
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
		// System.out.println("TestFragment onSaveInstanceState fragmentKey : "+news_title);
	}

	@Override
	public boolean handleMessage(Message msg) {
		lvCustomEvas.onRefreshComplete();
		activity.destroyDialog();
		try {
			switch (msg.what) {
			case 999:
				viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
				break;
			
			case IResult.LOCAL_CONSULT_INFO:
				totalpages = table.getTotalPage(type_id);
				if (totalpages == -1) {
					// Log.d("chenhj", "pages=-1");
				} else {
					// Log.d("chenhj", "pages = " + totalpages);
					init = true;
					if (refresh) {
						// if (table.getFocusList(type_id, page+"")==null ||
						// table.getFocusList(type_id, page+"").size()==0) {
						// ll_empty.setVisibility(View.VISIBLE);
						// lvCustomEvas.setVisibility(View.GONE);
						// } else {

						ll_empty.setVisibility(View.GONE);
						lvCustomEvas.setVisibility(View.VISIBLE);
						focusList = table.getFocusList(type_id, page + "");
						List<NewsItem> newsList = table.getNewsList(type_id, page + "");
						if((focusList!=null && focusList.size()>0) || (newsList!=null && newsList.size()>0) ){
							setupFocusViews(focusList);
							new_adapter.change(newsList);
							init = true;
						}
						
						
						
						// }
					} else {
						new_adapter.add(table.getNewsList(type_id, page + ""));
					}
					if (page < totalpages) {
						lvCustomEvas.onLoadMoreComplete(false);
					} else {
						lvCustomEvas.onLoadMoreComplete(true);
					}
				}
				getNewsListFromNet(page, type_id);
				break;

			case IResult.NEWS_LIST:
				ll_net_unavailable.setVisibility(View.GONE);
				NewsEntity response = (NewsEntity) msg.obj;
				if (null != response) {
					if (response.getSuccess() == 1) {
						init = true;
						totalpages = response.getPages();
						if (refresh) {
							// if (response.getData().getFocuslist()==null ||
							// response.getData().getFocuslist().size()==0) {
							// ll_empty.setVisibility(View.VISIBLE);
							// lvCustomEvas.setVisibility(View.GONE);
							// } else {
							ll_empty.setVisibility(View.GONE);
							lvCustomEvas.setVisibility(View.VISIBLE);
							focusList = response.getData().getFocuslist();
							setupFocusViews(focusList);
							final List<NewsItem> newsList = response.getData().getNewslist();
							new_adapter.change(newsList);
							// table.clearTable();

							new Thread() {
								public void run() {
									// 清楚原来的数据
									table.clearAtTypePage(type_id, page + "");
									// 保存新数据到数据库中
									for (int i = 0; i < focusList.size(); i++) {
										table.saveConsultInfo(focusList.get(i), type_id, page + "", false);
									}
									for (int i = 0; i < newsList.size(); i++) {
										table.saveConsultInfo(newsList.get(i), type_id, page + "", true);
									}
								};

							}.start();

							// }
						} else {
							new_adapter.add(response.getData().getNewslist());
						}
						if (page < totalpages) {
							lvCustomEvas.onLoadMoreComplete(false);
						} else {
							lvCustomEvas.onLoadMoreComplete(true);
						}
					} else {
						ll_empty.setVisibility(View.VISIBLE);
						lvCustomEvas.setVisibility(View.GONE);
						ll_net_unavailable.setVisibility(View.VISIBLE);
					}
				}
				break;
			case IResult.RESULT:
				break;
			case IResult.END:
				break;
			case IResult.NET_ERROR:
				activity.showToast(IMessage.NET_ERROR);

				ll_net_unavailable.setVisibility(View.VISIBLE);
				// ll_empty.setVisibility(View.GONE);
				// lvCustomEvas.setVisibility(View.GONE);

				break;
			case IResult.DATA_ERROR:
				if (new_adapter.getCount() == 0) {
					ll_net_unavailable.setVisibility(View.GONE);
					ll_empty.setVisibility(View.VISIBLE);
					lvCustomEvas.setVisibility(View.GONE);
				}
				table.clearAtTypePage(type_id, page + "");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void setupFocusViews(List<NewsItem> focusList) {
		try {
			if (null != focusList && focusList.size() > 0) {
				ll_dot_content.removeAllViews();
				imageViews = new ArrayList<ImageView>();
				LayoutParams fill_params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				LayoutParams wrap_params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				dots = new ArrayList<View>();
				for (int i = 0; i < focusList.size(); i++) {
					View dot_item = LayoutInflater.from(activity).inflate(R.layout.v_dot_news_pic, null);
					LinearLayout.LayoutParams dot_params = new LinearLayout.LayoutParams(40, 6);
					dot_params.setMargins(2, 0, 2, 0);
					dot_item.setLayoutParams(dot_params);
					dots.add(dot_item);
					ll_dot_content.addView(dot_item);
					
					ImageView imageView = initItem(focusList.get(i), wrap_params);
					imageViews.add(imageView);
				}
				dots.get(0).setBackgroundResource(R.drawable.dot_focused);
				
				
				if(focusList.size() == 2 || focusList.size()==3){
					for (int i = 0; i < focusList.size(); i++) {
						ImageView imageView = initItem(focusList.get(i), wrap_params);
						imageViews.add(imageView);
					}
				}
				viewPager = (ViewPager) view_header.findViewById(R.id.vp_news);
				viewPager.setAdapter(new PageAdapter(imageViews));// 设置填充Viewpager
																	// 页面的适配器
				// 设置一个监听器,当ViewPager 中的页面改变时调用
				viewPager.setOnPageChangeListener(new MyPageChangeListener());
				lvCustomEvas.removeHeaderView(view_header);
				lvCustomEvas.addHeaderView(view_header);
				if(focusList.size()>1){
					TimeSlide();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 循环线程
	Timer t;
	private void TimeSlide() {
		if (t != null) {
			t.cancel();
		}
		t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				sendMessage(999, null);
			}
		}, 3000, 3000);
	}

	private ImageView initItem(NewsItem Item, LayoutParams wrap_params) {
		NewsItem newsItem = Item;
		ImageView imageView = new ImageView(activity);
		imageView.setTag(newsItem);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setLayoutParams(wrap_params);
		activity.imageLoader.displayImage(newsItem.getPICEXT1(), imageView,Util.getOptions_pic());
		imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NewsItem item = (NewsItem)v.getTag();
				Intent intent = new Intent(activity,WebViewActivity.class);
				intent.putExtra("type", "InfoFragment");
				intent.putExtra("pic", item.getPIC());
				intent.putExtra("title", item.getTITLE());
				intent.putExtra("tabid", item.getID());
				intent.putExtra("url", item.getFURL());
				intent.putExtra("subtitle", item.getSUBTITLE());
				intent.putExtra("allowcomment", item.getALLOWCOMMENT());
				startActivity(intent);
			}
		});
		return imageView;
	}

	private class PageAdapter extends PagerAdapter {

		private List<ImageView> data = null;

		public PageAdapter(List<ImageView> data) {
			super();
			this.data = data;
		}

		public int getCount() {
			if (data.size() == 1) {
				return data.size();
			} else {
				return Integer.MAX_VALUE;
			}
		}

		public Object instantiateItem(ViewGroup container, int position) {

			int posi = 0;
			if (data.size() == 1) {
				posi = position;
			} else {
				posi = position % data.size();
			}
			container.addView(data.get(posi));
			return data.get(posi);
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			int posi = 0;
			if (data.size() == 1) {
				posi = position;
			} else {
				posi = position % data.size();
			}

			View view = data.get(posi);
			container.removeView(view);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {

		}

		public void finishUpdate(View arg0) {

		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private int oldPosition = 0;

	private class MyPageChangeListener implements OnPageChangeListener {

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			if (focusList == null || focusList.size() == 0) {
				return;
			}
			int posi = position;
			if (focusList.size() == 1) {
				posi = position;
			} else {
				posi = position % focusList.size();
			}
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(posi).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = posi;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	private class CustomEvaluateAdapter extends BaseAdapter {
		private List<NewsItem> listItems;

		public CustomEvaluateAdapter(ArrayList<NewsItem> listItems) {
			if (listItems == null) {
				listItems = new ArrayList<NewsItem>();
			}
			this.listItems = listItems;
		}

		@Override
		public int getCount() {
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			return (listItems == null || listItems.size() == 0) ? null : listItems.get(position);
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
				convertView = LayoutInflater.from(activity).inflate(R.layout.contact_info_item, null);

				holder.iv_contact = (ImageView) convertView.findViewById(R.id.iv_contact);
				holder.tv_contact_title = (TextView) convertView.findViewById(R.id.tv_contact_title);
				holder.tv_contact_info = (TextView) convertView.findViewById(R.id.tv_contact_info);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			NewsItem entry = listItems.get(position);
			if (null != entry) {

				activity.imageLoader.displayImage(entry.getPIC(), holder.iv_contact, Util.getOptions_little_pic());

				String title = entry.getTITLE();
				if (!TextUtils.isEmpty(title)) {
					holder.tv_contact_title.setText(title);
					holder.tv_contact_title.setVisibility(View.VISIBLE);
				} else {
					holder.tv_contact_title.setVisibility(View.GONE);
				}
				String subtitle = entry.getSUBTITLE();
				if (!TextUtils.isEmpty(subtitle)) {
					holder.tv_contact_info.setText(subtitle);
					holder.tv_contact_info.setVisibility(View.VISIBLE);
				} else {
					holder.tv_contact_info.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		public void change(List<NewsItem> lists) {
			if (lists == null) {
				lists = new ArrayList<NewsItem>();
			}
			this.listItems = lists;
			notifyDataSetChanged();
		}

		public void add(List<NewsItem> list) {
			this.listItems.addAll(list);
			notifyDataSetChanged();
		}
	}

	class ViewHolder {
		ImageView iv_contact;
		TextView tv_contact_title;
		TextView tv_contact_info;

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_net_unavailable) {
			refresh();
		} else if (id == R.id.ll_empty) {
			refresh();
		}
	}
}
