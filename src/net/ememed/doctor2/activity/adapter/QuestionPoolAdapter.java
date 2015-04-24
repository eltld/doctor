package net.ememed.doctor2.activity.adapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.PreeChatActivity;
import net.ememed.doctor2.activity.QuestionPoolActivity;
import net.ememed.doctor2.activity.ShowImageActivity;
import net.ememed.doctor2.entity.QuestionPoolInfo;
import net.ememed.doctor2.entity.ReceiveEntr;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BitmapPutUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.GridViewForScrollview;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import de.greenrobot.event.util.TimeTool;

public class QuestionPoolAdapter extends BaseAdapter implements ImageLoadingListener{
	
	private Context context;
	private List<QuestionPoolInfo> data;
	
	private long times;
	
	private ImageLoader imageLoader;
	QuestionPoolActivity activity;
	public QuestionPoolAdapter(Context context,ImageLoader imageLoader) {
		this.context = context;
		this.activity = (QuestionPoolActivity) context;
		this.imageLoader = imageLoader;
	}

	public int getCount() {
		if (data==null) {
			return 0;
		}
		return data.size();
	}

	public Object getItem(int arg0) {
		return arg0;
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		ViewHolder holder;
		if(arg1 == null){
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.question_pool_claim, null);
			holder.question_pool_info_img_head = (CircleImageView) arg1.findViewById(R.id.question_pool_info_img_head);
			holder.question_pool_info_txt_name = (TextView) arg1.findViewById(R.id.question_pool_info_txt_name);
			holder.question_pool_info_txt_sex = (TextView) arg1.findViewById(R.id.question_pool_info_txt_sex);
			holder.question_pool_info_txt_age = (TextView) arg1.findViewById(R.id.question_pool_info_txt_age);
			holder.question_pool_info_txt_date = (TextView) arg1.findViewById(R.id.question_pool_info_txt_date);
			holder.receive_bnt = (Button) arg1.findViewById(R.id.receive_bnt);
			holder.question_pool_txt_question = (TextView) arg1.findViewById(R.id.question_pool_txt_question);
			holder.question_pool_grid_question = (LinearLayout) arg1.findViewById(R.id.question_pool_grid_question);
			holder.gridView = (GridViewForScrollview) arg1.findViewById(R.id.gridView);
			arg1.setTag(holder);
		}else{
			holder = (ViewHolder) arg1.getTag();
		}
		final QuestionPoolInfo info = data.get(arg0);
		imageLoader.displayImage(info.getAVATAR(), holder.question_pool_info_img_head,Util.getOptions_pic());
		holder.question_pool_info_txt_name.setText(info.getREALNAME());
		String sex = info.getSEX();
		if(sex.equals("0")){
			holder.question_pool_info_txt_sex.setText("");
		}else if(sex.equals("1")){
			holder.question_pool_info_txt_sex.setText("男");
		}else{
			holder.question_pool_info_txt_sex.setText("女");
		}
		String AGE_UNIT = info.getAGE_UNIT();
		if(info.getAGE().trim().equals("0")){
			holder.question_pool_info_txt_age.setVisibility(View.GONE);
		}else{
			if(AGE_UNIT.equals("1")){
				holder.question_pool_info_txt_age.setText(info.getAGE()+"岁");
			}else if(AGE_UNIT.equals("2")){
				holder.question_pool_info_txt_age.setText(info.getAGE()+"个月");
			}
		}
		
		
		Long time = TimeTool.getTime(info.getCREATE_TIME().trim());
		long newitem= System.currentTimeMillis();
		System.out.println("time = "+time+"  newitem = "+newitem);
		times=(newitem-time)/60000;
		if(times<60){
			holder.question_pool_info_txt_date.setText(times+"分前");
		}else if(times>=60){
			if((times/60)<24){
				holder.question_pool_info_txt_date.setText(times/60+"小时前");
			}else if((times/60)>=24){
				holder.question_pool_info_txt_date.setText(times/60/24+"天前");
			}
		}
//		holder.question_pool_info_txt_date.setText(((newitem-time)/60000)+"分前");
		holder.question_pool_txt_question.setText(info.getSYMPTOMS());
		holder.receive_bnt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setClaim(info);
			}
		});
		
		List<String> priData = info.getPICS();
		final MyImageGridViewAdapter adapter = new MyImageGridViewAdapter(priData,context,imageLoader);
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
	
	public void setData(List<QuestionPoolInfo> data){
		this.data = data;
		notifyDataSetChanged();
	}
	
	public void addData(List<QuestionPoolInfo> data){
		this.data.addAll(data);
		notifyDataSetChanged();
	}
	
	public void setClaim(final QuestionPoolInfo info){
		activity.loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("questionid", info.getQUESTIONID());
		params.put("channel", "android");
		params.put("app_version", PublicUtil.getVersionName(activity));
		System.out.println("params = "+params);
		MyApplication.volleyHttpClient.postOAuthWithParams(HttpUtil.set_claim, ReceiveEntr.class, params, new Listener(){

			public void onResponse(Object arg0) {
				activity.destroyDialog();
				ReceiveEntr receiveEntr = (ReceiveEntr) arg0;
				if(receiveEntr.getSuccess().trim().equals("1")){
					activity.showToast(receiveEntr.getErrormsg());
					
					Intent intent = new Intent(activity,PreeChatActivity.class);
					intent.putExtra("orderid", receiveEntr.getData().trim());
					intent.putExtra("title", info.getREALNAME());
					intent.putExtra("tochat_userId", info.getUSERID());
					if(TextUtils.isEmpty(info.getAVATAR())){
						intent.putExtra("user_avatar", "");
					} else {
						intent.putExtra("user_avatar", info.getAVATAR().trim());
					}
					/*if(TextUtils.isEmpty(info.getAVATAR().trim())){
						intent.putExtra("user_avatar", info.getAVATAR().trim());
					}else{
						intent.putExtra("user_avatar", "");
					}*/
					intent.putExtra("questionid", info.getQUESTIONID());
					intent.putExtra("status", "1");
					activity.startActivity(intent);
				}else{
					activity.showToast(receiveEntr.getErrormsg());
				}
				
			}
			
		}, new Response.ErrorListener() {

			public void onErrorResponse(VolleyError arg0) {
				activity.destroyDialog();
				activity.showToast("网络异常,请排查网络原因");
			} 
		});
	}
	
	
	public static class ViewHolder{
		private CircleImageView question_pool_info_img_head;
		private TextView question_pool_info_txt_name;
		private TextView question_pool_info_txt_sex;
		private TextView question_pool_info_txt_age;
		private TextView question_pool_info_txt_date;
		private Button receive_bnt;
		private TextView question_pool_txt_question;
		private LinearLayout question_pool_grid_question;
		private GridViewForScrollview gridView;
	}


	@Override
	public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		// TODO Auto-generated method stub
		((ImageView)arg1).setImageBitmap(BitmapPutUtil.getSquareBitmap(arg2));
	}

	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadingStarted(String arg0, View arg1) {
		// TODO Auto-generated method stub
		
	}

}
