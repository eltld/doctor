package net.ememed.doctor2.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.MainActivity;
import net.ememed.doctor2.activity.WebViewActivity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.NewsEntity;
import net.ememed.doctor2.entity.NewsItem;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.util.Util;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * @author taro chyaohui@gmail.com 
 */
public class HomeFragment extends Fragment implements Callback , OnRefreshListener {
	private static final String TAG = HomeFragment.class
			.getSimpleName();
	public static final int EXEU_GET_DATA = 0;
	public static final int EXEU_GET_DATA_ERROR = 1;
	
	private FrameLayout mContentView = null;
	private Handler handler = null;
	private MainActivity activity = null;
	
	private ViewPager viewPager;
	private List<ImageView> imageViews; // 滑动的图片
	private int[] imageResId;// 图片ID
	private List<View> dots; // 图片标题正文的那些点
	private int currentItem = 0;// 当前图片的索引号
	private LinearLayout ll_view_content;
	private LinearLayout ll_dot_content;
	private PullToRefreshLayout mPullToRefreshLayout;
	
	public HomeFragment(){
		this.activity = (MainActivity) getActivity();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		imageResId = new int[] { R.drawable.test_home_pic_1, R.drawable.test_home_pic_1, R.drawable.test_home_pic_1,
				R.drawable.test_home_pic_1, R.drawable.test_home_pic_1 };
		
		handler = new Handler(this);
		Logger.dout(TAG + "onCreate");
	}

	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
          super.setUserVisibleHint(isVisibleToUser);
          if (isVisibleToUser) {
              //fragment可见时执行加载数据或者进度条等
        	  Logger.dout(TAG + "isVisibleToUser");
        	  activity = (MainActivity) getActivity();
          } else {
              //不可见时不执行操作
        	  Logger.dout(TAG + "unVisibleToUser");
          }
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Logger.dout(TAG + "onCreateView");
		View view = inflater.inflate(R.layout.root_layout, null);
		mContentView = (FrameLayout) view.findViewById(R.id.mainView);
		
		ll_view_content = (LinearLayout)inflater.inflate(R.layout.fragment_home, null);
		TextView top_title = (TextView) ll_view_content.findViewById(R.id.top_title);
		top_title.setText(getString(R.string.act_title_home));
		ImageView btn_back = (ImageView) ll_view_content.findViewById(R.id.btn_back);
		btn_back.setVisibility(View.INVISIBLE);
		
		ll_dot_content = (LinearLayout)ll_view_content.findViewById(R.id.ll_dot_content);
		
		imageViews = new ArrayList<ImageView>();
		dots = new ArrayList<View>();
		
//		for (int i = 0; i < imageResId.length; i++) {
//			ImageView imageView = new ImageView(activity);
//			imageView.setImageResource(imageResId[i]);
//			imageView.setScaleType(ScaleType.CENTER_CROP);
//			imageViews.add(imageView);
//			View dot_item = LayoutInflater.from(activity).inflate(R.layout.v_dot_news_pic, null);
//			LinearLayout.LayoutParams dot_params = new LinearLayout.LayoutParams(40, 6);
//			dot_params.setMargins(2, 0, 2, 0);
//			dot_item.setLayoutParams(dot_params);
//			dots.add(dot_item);
//			ll_dot_content.addView(dot_item);
//		}
//		
//		viewPager = (ViewPager) ll_view_content.findViewById(R.id.vp_news);
//		viewPager.setAdapter(new PageAdapter());// 设置填充Viewpager 页面的适配器
//		// 设置一个监听器,当ViewPager 中的页面改变时调用
//		viewPager.setOnPageChangeListener(new MyPageChangeListener());

		mContentView.addView(ll_view_content);
		
		mPullToRefreshLayout = (PullToRefreshLayout) ll_view_content.findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(activity).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);
		
		getHomeNews();
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.dout(TAG + "onResume");
	}

	@Override
	public void onPause() {
		super.onStop();
	}
	  @Override
	  public void onRefreshStarted(View view) {
		  getHomeNews();
	}
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case EXEU_GET_DATA:
			mPullToRefreshLayout.setRefreshComplete();
			NewsEntity entity = (NewsEntity) msg.obj;
			if (null != entity) {
				if (entity.getSuccess() == 1) {
					setupFocusViews(entity.getData().getFocuslist());
				} else {
				}
			} else {
				
			}
			break;
		case EXEU_GET_DATA_ERROR:
			mPullToRefreshLayout.setRefreshComplete();
			break;
		case IResult.EXEU_CHANGE_PIC:
			 viewPager.setCurrentItem(currentItem);// 切换当前显示的图片  
			break;
		default:
			break;
		}
		return false;
	}

	private void getHomeNews(){
//		mPullToRefreshLayout.setRefreshing(true);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type", "doctor");
        MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_news_home_focus, NewsEntity.class,params, new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {

                        Message message = new Message();
                        message.obj = response;
                        message.what = EXEU_GET_DATA;
                        handler.sendMessage(message);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    	Message message = new Message();
                        message.obj = error.getMessage();
                        message.what = EXEU_GET_DATA_ERROR;
                        handler.sendMessage(message);
                    }
                }
        );
	}
	
	public void doClick(View view) {
		try {
			int id = view.getId();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void setupFocusViews(List<NewsItem> focusList) {
		try {
			if (null!= focusList && focusList.size() > 0) {
				ll_dot_content.removeAllViews();
				imageViews = new ArrayList<ImageView>();
				LayoutParams fill_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				LayoutParams wrap_params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				dots = new ArrayList<View>();
				for (int i = 0; i < focusList.size(); i++) {
					View dot_item = LayoutInflater.from(activity).inflate(R.layout.v_dot_news_pic, null);
					ImageView imageView = new ImageView(activity);
					imageView.setTag(focusList.get(i));
					imageView.setLayoutParams(fill_params);
					activity.imageLoader.displayImage(focusList.get(i).getPIC(), imageView,Util.getOptions_pic());
					imageView.setScaleType(ScaleType.FIT_XY);
					imageView.invalidate();
					imageViews.add(imageView);
					dots.add(dot_item);
					LinearLayout.LayoutParams dot_params = new LinearLayout.LayoutParams(40, 6);
					dot_params.setMargins(2, 0, 2, 0);
					dot_item.setLayoutParams(dot_params);
					ll_dot_content.addView(dot_item);
					imageView.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							NewsItem item = (NewsItem)v.getTag();
							Intent intent = new Intent(activity,WebViewActivity.class);
							intent.putExtra("title", item.getTITLE());
							intent.putExtra("url",item.getFURL());
							startActivity(intent);
						}
					});
				}
				dots.get(0).setBackgroundResource(R.drawable.dot_focused);
				viewPager = (ViewPager) ll_view_content.findViewById(R.id.vp_news);
				viewPager.setAdapter(new PageAdapter(focusList));// 设置填充Viewpager 页面的适配器
				// 设置一个监听器,当ViewPager 中的页面改变时调用
				viewPager.setOnPageChangeListener(new MyPageChangeListener());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ScheduledExecutorService scheduledExecutorService;  
	  
	 @Override  
	 public void onStart() {  
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();  
        // 当Activity显示出来后，每两秒钟切换一次图片显示  
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 2, 3, TimeUnit.SECONDS);  
        super.onStart();  
	}  
	 
	@Override
	public void onStop() {
		super.onStop();
		 // 当Activity不可见的时候停止切换  
        scheduledExecutorService.shutdown(); 
	}
    /** 
     * 换行切换任务 
     *  
     * @author Administrator 
     *  
     */  
    private class ScrollTask implements Runnable {  
  
        public void run() {  
            synchronized (viewPager) {  
                currentItem = (currentItem + 1) % imageViews.size();  
            	Message message = new Message();
                message.what = IResult.EXEU_CHANGE_PIC;
                handler.sendMessage(message);
            }  
        }  
    }  
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	private class PageAdapter extends PagerAdapter {

		private List<NewsItem> data = null;
		
		public PageAdapter(List<NewsItem> data) {
			super();
			this.data = data;
		}


		public int getCount() {
			return data.size();
		}

		
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
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
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}
}
