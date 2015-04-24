package net.ememed.doctor2.activity.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.ShowImageActivity;
import net.ememed.doctor2.entity.QuestionPoolInfo;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.GridViewForScrollview;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.util.TimeTool;

public class ClaimListAdapter extends BaseAdapter{
	private List<QuestionPoolInfo> list;
	private Context context;
	private ImageLoader imageloader;
	private DisplayImageOptions options;
	private long times;
	
	public ClaimListAdapter(Context context,ImageLoader imageloader,DisplayImageOptions options){
		this.context=context;
		this.imageloader=imageloader;
		this.options=options;
	}
	@Override
	public int getCount() {
		if(list==null){
			return 0;
		}
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder;
		if(arg1 == null){
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.question_pool_condition, null);
			holder.question_pool_condition_img_head = (CircleImageView) arg1.findViewById(R.id.question_pool_condition_img_head);
			holder.question_pool_condition_txt_name = (TextView) arg1.findViewById(R.id.question_pool_condition_txt_name);
			holder.question_pool_condition_txt_sex = (TextView) arg1.findViewById(R.id.question_pool_condition_txt_sex);
			holder.question_pool_condition_txt_age = (TextView) arg1.findViewById(R.id.question_pool_condition_txt_age);
			holder.question_pool_condition_txt_date = (TextView) arg1.findViewById(R.id.question_pool_condition_txt_date);
			holder.question_pool_appraise_remark = (TextView) arg1.findViewById(R.id.question_pool_appraise_remark);
			holder.question_pool_ishuifu=(TextView) arg1.findViewById(R.id.question_pool_condition_ishuifu);
			holder.question_pool_appraise_money=(TextView) arg1.findViewById(R.id.question_pool_appraise_money);
			holder.question_pool_txt_question = (TextView) arg1.findViewById(R.id.question_pool_txt_question);
			holder.rating_layout=(LinearLayout) arg1.findViewById(R.id.rating_layout);
			holder.question_pool_appraise_rl_money=(RelativeLayout) arg1.findViewById(R.id.question_pool_appraise_rl_money);
			holder.ratingBar=(RatingBar) arg1.findViewById(R.id.ratingBar);
			holder.gridView = (GridViewForScrollview) arg1.findViewById(R.id.gridView);
			arg1.setTag(holder);
		}else{
			holder = (ViewHolder) arg1.getTag();
		}
		final QuestionPoolInfo info = list.get(arg0);
		imageloader.displayImage(info.getAVATAR(), holder.question_pool_condition_img_head,options);
		holder.question_pool_condition_txt_name.setText(info.getREALNAME());
		String sex = info.getSEX();
		if(sex.equals("0")){
			holder.question_pool_condition_txt_sex.setText("");
		}else if(sex.equals("1")){
			holder.question_pool_condition_txt_sex.setText("男");
		}else{
			holder.question_pool_condition_txt_sex.setText("女");
		}
		String AGE_UNIT = info.getAGE_UNIT();
		if(TextUtils.isEmpty(info.getAGE())||info.getAGE().equals("0")){
			holder.question_pool_condition_txt_age.setVisibility(View.GONE);
		}else{
			holder.question_pool_condition_txt_age.setVisibility(View.VISIBLE);
			if(AGE_UNIT.equals("1")){
				holder.question_pool_condition_txt_age.setText(info.getAGE()+"岁");
			}else if(AGE_UNIT.equals("2")){
				holder.question_pool_condition_txt_age.setText(info.getAGE()+"个月");
			}
		}
		Long time = TimeTool.getTime(info.getCREATE_TIME().trim());
		long newitem= System.currentTimeMillis();
		times=(newitem-time)/60000;
		if(times<60){
			holder.question_pool_condition_txt_date.setText(times+"分前");
		}else{
			if((times/60)<24){
				holder.question_pool_condition_txt_date.setText(times/60+"小时前");
			}else{
				holder.question_pool_condition_txt_date.setText(times/60/24+"天前");
			}
		}

		String status =  info.getSTATUS();
//		if(status.equals("1")){
//			//是否回复     0未回复，1已回复
//			if(info.getREPLY_STATUS().equals("0")){
//				holder.question_pool_ishuifu.setText("进行中");
//				holder.question_pool_ishuifu.setTextColor(context.getResources().getColor(R.color.question_pool_btn_down));
//			}else if(info.getREPLY_STATUS().equals("1")){
//				holder.question_pool_ishuifu.setText("已回复");
//				holder.question_pool_ishuifu.setTextColor(0x70A03A);
//			}
//		}else{
//			holder.question_pool_ishuifu.setText("已关闭");
//		}
		holder.question_pool_ishuifu.setText(info.getSTATUS_DESC());
//		EVALUATION 评价星级（分数值）
//		EVALUATION_CONTENT 评价内容
		
		if(!TextUtils.isEmpty(info.getEVALUATION())){
			holder.ratingBar.setProgress((int) Float.parseFloat(info.getEVALUATION()));
			holder.rating_layout.setVisibility(View.VISIBLE);
		}else{
			holder.rating_layout.setVisibility(View.GONE);
		}
		if(!TextUtils.isEmpty(info.getEVALUATION_CONTENT())){
			holder.question_pool_appraise_remark.setText(info.getEVALUATION_CONTENT());
			holder.question_pool_appraise_remark.setVisibility(View.VISIBLE);
		}else{
			holder.question_pool_appraise_remark.setVisibility(View.GONE);
		}
		
		if(TextUtils.isEmpty(info.getMONEY())||info.getMONEY().equals("0")){
			holder.question_pool_appraise_rl_money.setVisibility(View.GONE);
		}else{
			holder.question_pool_appraise_rl_money.setVisibility(View.VISIBLE);
		}
		
		//收到的心意
		holder.question_pool_appraise_money.setText(info.getMONEY());
		holder.question_pool_txt_question.setText(info.getSYMPTOMS());
		//问题图片
		List<String> priData = info.getPICS();
		
		final MyImageGridViewAdapter adapter =new MyImageGridViewAdapter(priData,context,imageloader);
		holder.gridView.setAdapter( adapter);
		holder.gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent intent = new Intent(context, ShowImageActivity.class);
				intent.putExtra("position", arg2);
				List<String> pris = adapter.getData();
				intent.putExtra("imageUrls", (Serializable)pris);
				context.startActivity(intent);
			}
		});
		
		return arg1;
	}
	
	public void setData(List<QuestionPoolInfo> list){
		this.list = list;
		notifyDataSetChanged();
	}
	
	
	public class ViewHolder{
		
		RatingBar ratingBar;
		private CircleImageView question_pool_condition_img_head;
		private TextView question_pool_condition_txt_name;
		private TextView question_pool_condition_txt_sex;
		private TextView question_pool_condition_txt_age;
		private TextView question_pool_condition_txt_date;
		private TextView question_pool_ishuifu;
		private TextView question_pool_txt_question;
		private TextView question_pool_appraise_money;
		private TextView question_pool_appraise_remark;
		private LinearLayout rating_layout;
		private RelativeLayout question_pool_appraise_rl_money;	//心意layout
		private GridViewForScrollview gridView;
		
	}

}
