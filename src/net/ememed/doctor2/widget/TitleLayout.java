package net.ememed.doctor2.widget;

import net.ememed.doctor2.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 
 * 自定义此布局的目的是避免每个界面都需要编写标题栏的返回按钮事件
 * 
 * @author Administrator
 *
 */
public class TitleLayout extends LinearLayout{
	
	public TitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.top_bar_doctor_green, this);	//此处第二个参数为this,表示把这个布局加到TitleLayout这个布局
		ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((Activity)getContext()).finish();
			}
		});
	}
}
