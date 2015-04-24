package net.ememed.doctor2.baike;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.entity.RewardListInfo;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;

public class RewardDetailActivity extends BasicActivity{

	RewardListInfo info = null;
	CircleImageView civ_photo;
	TextView tv_name;
	TextView tv_money;
	TextView tv_time;
	TextView tv_content;
	ImageView iv_fans;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_reward_detail);
		info = (RewardListInfo) getIntent().getSerializableExtra("reward_info");
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		((TextView)findViewById(R.id.top_title)).setText("赏金详情");
		civ_photo = (CircleImageView) findViewById(R.id.civ_photo);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_content = (TextView) findViewById(R.id.tv_content);
		iv_fans = (ImageView) findViewById(R.id.iv_fans);
		
		if(!TextUtils.isEmpty(info.getREALNAME())){
			imageLoader.displayImage(info.getAVATAR(), civ_photo, Util.getOptions_avatar());
		}
		
		if(!TextUtils.isEmpty(info.getREALNAME())){
			tv_name.setText(info.getREALNAME());
		}
		
		if(!TextUtils.isEmpty(info.getMONEY())){
			tv_money.setText(info.getMONEY());
		}
		
		if(!TextUtils.isEmpty(info.getCREATE_TIME())){
			tv_time.setText(info.getCREATE_TIME());
		}
		
		if(!TextUtils.isEmpty(info.getISMYFANS()) && "1".equals(info.getISMYFANS())){
			iv_fans.setVisibility(View.VISIBLE);
		} else {
			iv_fans.setVisibility(View.GONE);
		}
		
		if(!TextUtils.isEmpty(info.getCONTENT())){
			setContentColor(info.getCONTENT());
		}
	}
	
	
	private void setContentColor(String str){
		String[] text = new String[]{"患者留言：", "    "+str};
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < text.length; i++){
			sb.append(text[i]);
		}
		
		int color1 = getResources().getColor(R.color.black);
		int color2 = getResources().getColor(R.color.text_color_gray);
		
		SpannableStringBuilder spannable = new SpannableStringBuilder(sb.toString());
		int begin = 0;
		for(int i = 0; i < text.length; i++){
			int textLength = text[i].length();
			if(1 == i){
				spannable.setSpan(new ForegroundColorSpan(color2), begin, begin+textLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			} else {
				spannable.setSpan(new ForegroundColorSpan(color1), begin, begin+textLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}
			begin += textLength;
		}
		
		tv_content.setText(spannable);
	}
	
	public void doClick(View view){
		if(R.id.btn_back == view.getId()){
			finish();
		}
	}
}
