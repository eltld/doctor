package net.ememed.doctor2.baike;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.entity.BaikeShuoshuoInfo;
import net.ememed.doctor2.baike.entity.MyBaikeEntry;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.util.ShareSdkUtil;
import net.ememed.doctor2.util.Util;

public class PicViewPagerActivity extends BasicActivity{
	private final int MSG_UPDATE_PROGRESS = 2015;
	private final int MSG_DOWNLOAD_START = 2016;
	private final int MSG_DOWNLOAD_FAILED = 2017;
	private final int MSG_DOWNLOAD_COMPLETED = 2018;
	private ViewPager viewpager;
	private View[] childViews;
	private List<String> pics = null;
	private int currentItem = 0;
	private PagerAdapter adapter;
 	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_pic_viewpager);
		pics = (List<String>) getIntent().getSerializableExtra("pics");
		currentItem = Integer.parseInt(getIntent().getStringExtra("current_item"));
		if(null == pics){
			pics = new ArrayList<String>();
		}
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		if(pics != null && pics.size() > 0){
			final int count = pics.size();
			childViews = new View[count];
			for(int i= 0; i < count; i++){
				childViews[i] = View.inflate(this, R.layout.pic_pager_item, null);
			}
		}
		
		adapter = new InnerPagerAdapter();
		// 为ViewPager绑定adapter
		viewpager.setAdapter(adapter);
		viewpager.setCurrentItem(currentItem);
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				currentItem = arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}
	
	@Override
	protected void onResult(Message msg) {
		
		try {
			TextView tv_progress = (TextView) childViews[currentItem].findViewById(R.id.tv_progress);
			
			switch (msg.what) {
			case MSG_UPDATE_PROGRESS:
				if(View.GONE == tv_progress.getVisibility() && msg.arg2 < msg.arg1){
					tv_progress.setVisibility(View.VISIBLE);
				}
				String percentage = (msg.arg2 * 100 / msg.arg1)+"%";
				tv_progress.setText("已加载："+percentage);
				break;
			case MSG_DOWNLOAD_START:
				tv_progress.setText("已加载：0%");
				break;
			case MSG_DOWNLOAD_FAILED:
				tv_progress.setText("加载失败");
				break;
			case MSG_DOWNLOAD_COMPLETED:
				tv_progress.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onResult(msg);
	}
	
	
	private class InnerPagerAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View root = childViews[position].findViewById(R.id.layout_root);
			root.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			// 添加子级页面
			ImageView imageView1 = (ImageView) childViews[position].findViewById(R.id.imageView1);
			TextView tv_pic_num = (TextView) childViews[position].findViewById(R.id.tv_pic_num);
			TextView tv_progress = (TextView) childViews[position].findViewById(R.id.tv_progress);
			tv_pic_num.setText(""+(position+1)+"/"+pics.size());
//			imageLoader.displayImage(pics.get(position), imageView1, Util.getOptions_big_pic());

		    imageLoader.displayImage(pics.get(position), imageView1, Util.getOptions_for_viewPager(), 
		    		new ImageLoadingListener() {  
						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub
							
						}
						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							Message message = new Message();
							message.what = MSG_DOWNLOAD_COMPLETED;
							handler.sendMessage(message);
							
						}
						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							Message message = new Message();
							message.what = MSG_DOWNLOAD_FAILED;
							handler.sendMessage(message);
						}
						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							Message message = new Message();
							message.what = MSG_DOWNLOAD_START;
							handler.sendMessage(message);
						}
					},
				new ImageLoadingProgressListener() {        
		          @Override  
		          public void onProgressUpdate(String imageUri, View view, int current,int total) {     
		        	  //在这里更新 ProgressBar的进度信息  
		        	  Message message = new Message();
					  message.what = MSG_UPDATE_PROGRESS;
					  message.arg1 = total;
					  message.arg2 = current;
					  handler.sendMessage(message);
		          }  
		        });  
			
			container.addView(childViews[position]);
			// 返回当前页面
			return childViews[position];
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// 移除
			container.removeView(childViews[position]);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}

		@Override
		public int getCount() {
			// 获取数量
			return childViews.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// 判断是否为同一个对象
			return arg0 == arg1;
		}
	}
}
