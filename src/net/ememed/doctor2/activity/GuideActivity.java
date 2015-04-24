package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.ViewPagerAdapter;

public class GuideActivity extends FragmentActivity implements OnPageChangeListener, OnClickListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;
    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    private ImageView[] dots ;  
//    private Button guide_login;
//    private Button guide_zhuche;
    private ImageView guide_login;
    private ImageView guide_zhuche;
    // 记录当前选中位置
    private int currentIndex;
    Button guide_tixian;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_guide_first_install);
        guide_login = (ImageView) findViewById(R.id.guide_login);
        guide_zhuche = (ImageView) findViewById(R.id.guide_zhuche);
        initViews();    
   	 
	     guide_login.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	             // 设置已经引导
	             setGuided();
	             gologin();
	    }});
	     
	     guide_zhuche.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
	             setGuided();
	             gozhuche();
					
				}
	     });
     }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        // 初始化引导图片列表
        views.add(inflater.inflate(R.layout.guider_1, null));
        views.add(inflater.inflate(R.layout.guider_2, null));
        views.add(inflater.inflate(R.layout.guider_3, null));
        views.add(inflater.inflate(R.layout.guider_4, null));

        // 初始化Adapter
        vpAdapter = new ViewPagerAdapter(views);

        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        // 绑定回调
        vp.setOnPageChangeListener(this);
        initDots();
    }
    
	private void initDots() {
		ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[views.size()];

		// 循环取得小点图片
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// 都设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	/**
	 * *设置当前的引导页 .
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= views.size()) {
			return;
		}

		vp.setCurrentItem(position);
	}

	/**
	 * 这只当前引导小点的选中
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > views.size() - 1
				|| currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = positon;
	}


    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		setCurDot(arg0);
		if(arg0==views.size()-1){
			guide_login.setVisibility(View.VISIBLE);
			guide_zhuche.setVisibility(View.VISIBLE);
			ll.setVisibility(View.GONE);
		}else{
			guide_login.setVisibility(View.GONE);
			guide_zhuche.setVisibility(View.GONE);
			ll.setVisibility(View.VISIBLE);
		}
	}
    private void gozhuche() {
        // 跳转
        Intent intent = new Intent(this, LogonsActivity.class);
        this.startActivity(intent);
        this.finish();
    }
    private void gologin() {
        // 跳转
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
        this.finish();
    }
    private void setGuided() {
        SharedPreferences preferences = this.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        // 存入数据
        editor.putBoolean("isFirstIn", false);
        // 提交修改
        editor.commit();
    }
    
    @Override  
    public void onClick(View v) {  
        int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
    }  
}