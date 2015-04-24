package net.ememed.doctor2.activity.adapter;

import java.io.Serializable;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.ShowImageActivity;
import net.ememed.doctor2.entity.PeerInfo;
import net.ememed.doctor2.entity.QuestionPoolInfo;
import net.ememed.doctor2.widget.GridViewForScrollview;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.util.TimeTool;

public class PeerAnswerAdapter extends BaseAdapter {

	private Context context;
	private List<PeerInfo> data;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public PeerAnswerAdapter(Context context, ImageLoader imageLoader,DisplayImageOptions options) {
		this.context = context;
		this.imageLoader = imageLoader;
		this.options=options;
	}

	@Override
	public int getCount() {
		if (data == null)
			return 0;
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		ViewHolder holder;
		if(arg1==null){
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.question_pool_answer_other, null);
			holder.question_pool_other_img_head = (ImageView) arg1.findViewById(R.id.question_pool_other_img_head);
			holder.question_pool_other_txt_name = (TextView) arg1.findViewById(R.id.question_pool_other_txt_name);
			holder.question_pool_other_txt_sex = (TextView) arg1.findViewById(R.id.question_pool_other_txt_sex);
			holder.question_pool_other_txt_age = (TextView) arg1.findViewById(R.id.question_pool_other_txt_age);
			holder.question_pool_other_txt_date = (TextView) arg1.findViewById(R.id.question_pool_other_txt_date);
			holder.question_pool_other_txt_number_zan = (TextView) arg1.findViewById(R.id.question_pool_other_txt_number_zan);
			holder.question_pool_other_txt_number_punlun = (TextView) arg1.findViewById(R.id.question_pool_other_txt_number_punlun);
			holder.question_pool_txt_question = (TextView) arg1.findViewById(R.id.question_pool_txt_question);
			holder.gridView=(GridViewForScrollview) arg1.findViewById(R.id.gridView);
			arg1.setTag(holder);
		}else{
			holder = (ViewHolder) arg1.getTag();
		}
		
		final PeerInfo info = data.get(arg0);
		imageLoader.displayImage(info.getAVATAR(), holder.question_pool_other_img_head,options);
		holder.question_pool_other_txt_name.setText(info.getREALNAME());
		String sex = info.getSEX();
		if(sex.equals("0")){
			holder.question_pool_other_txt_sex.setText("");
		}else if(sex.equals("1")){
			holder.question_pool_other_txt_sex.setText("男");
		}else{
			holder.question_pool_other_txt_sex.setText("女");
		}
		String AGE_UNIT = info.getAGE_UNIT();
		if(AGE_UNIT.equals("1")){
			holder.question_pool_other_txt_age.setText(info.getAGE()+"岁");
		}else if(AGE_UNIT.equals("2")){
			holder.question_pool_other_txt_age.setText(info.getAGE()+"个月");
		}
		
		Long time = TimeTool.getTime(info.getCREATE_TIME().trim());
		long newitem= System.currentTimeMillis();
		long times=(newitem-time)/60000;
		if(times<60){
			holder.question_pool_other_txt_date.setText(times+"分前");
		}else{
			if(times/60<24){
				holder.question_pool_other_txt_date.setText(times/60+"小时前");
			}else{
				holder.question_pool_other_txt_date.setText(times/60/24+"天前");
			}
		}
		
		holder.question_pool_txt_question.setText(info.getSYMPTOMS());
		holder.question_pool_other_txt_number_zan.setText(info.getPRAISE_NUM());
		holder.question_pool_other_txt_number_punlun.setText(info.getCOMMENT_NUM());
		
		//显示图片
		List<String> priData=info.getPICS();
		final MyImageGridViewAdapter adapter=new MyImageGridViewAdapter(priData, context, imageLoader);
		
		holder.gridView.setAdapter(adapter);
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

	public void setData(List<PeerInfo> data) {
		if (data != null) {
			this.data = data;
		}
		notifyDataSetChanged();
	}
	public void addData(List<PeerInfo> data){
		this.data.addAll(data);
		notifyDataSetChanged();
	}
	
	class ViewHolder{
		
		ImageView question_pool_other_img_head;
		TextView question_pool_other_txt_name;
		TextView question_pool_other_txt_sex;
		TextView question_pool_other_txt_age;
		TextView question_pool_other_txt_date;
		TextView question_pool_other_txt_number_zan;
		TextView question_pool_other_txt_number_punlun;
		TextView question_pool_txt_question;
		GridViewForScrollview gridView;
	}

}
