package net.ememed.doctor2.baike.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.DoctorInfoActivity;
import net.ememed.doctor2.activity.ShowBigImage;
import net.ememed.doctor2.baike.PicViewPagerActivity;
import net.ememed.doctor2.baike.adapter.CommentAdapter.ViewHolder;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.util.BitmapPutUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PutImageAdapter extends BaseAdapter{
	private final int DELETE_PIC = 888; 
	private List<Map<String, String>> list;
	private BasicActivity activity;
	private Handler handler;
	private boolean isAddPic = true; 
	public int CanvasHeight;
	public int CanvasWidth;
	private int maxPic = -1;

	public PutImageAdapter(List<Map<String, String>> list, BasicActivity activity, Handler handler) {
		if(null == list){
			list = new ArrayList<Map<String, String>>();
			Map map = new HashMap<String, String>();
			list.add(map);
		}
		
		this.list = list;
		this.activity = activity;
		this.handler = handler;

		CanvasWidth = MyApplication.getInstance().canvasWidth;
		CanvasHeight = MyApplication.getInstance().canvasHeight;
	}
	
	public PutImageAdapter(List<Map<String, String>> list, BasicActivity activity, Handler handler, boolean isAddPic) {
		if(null == list){
			list = new ArrayList<Map<String, String>>();
			Map map = new HashMap<String, String>();
			list.add(map);
		}
		
		this.list = list;
		this.activity = activity;
		this.handler = handler;
		this.isAddPic = isAddPic;

		CanvasWidth = MyApplication.getInstance().canvasWidth;
		CanvasHeight = MyApplication.getInstance().canvasHeight;
	}
	
	public PutImageAdapter(List<Map<String, String>> list, BasicActivity activity, Handler handler, boolean isAddPic, int maxPic) {
		if(null == list){
			list = new ArrayList<Map<String, String>>();
			Map map = new HashMap<String, String>();
			list.add(map);
		}
		
		this.list = list;
		this.activity = activity;
		this.handler = handler;
		this.isAddPic = isAddPic;
		this.maxPic = maxPic;
		
		CanvasWidth = MyApplication.getInstance().canvasWidth;
		CanvasHeight = MyApplication.getInstance().canvasHeight;
	}

	public List<Map<String, String>> getList(){
		if(null == list){
			list = new ArrayList<Map<String, String>>();
		}
		return list;
	}
	
	public int getCount() {
		if (list == null) {
			list = new ArrayList<Map<String, String>>();
		}
		
		if(maxPic > 0){
			if(list.size() <= maxPic)
				return list.size();
			else
				return maxPic;
		} else {
			if(list.size() <= 5)
				return list.size();
			else
				return 5;
		}
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.item_add_img, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
			holder.ll_delete_pic = (LinearLayout) convertView.findViewById(R.id.ll_delete_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int number = activity.dip2px(activity, 50);
		int number1 = (CanvasWidth - activity.dip2px(activity, 60)) / 4;
		if (number > number1) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					(CanvasWidth - activity.dip2px(activity, 60)) / 4,
					(CanvasWidth - activity.dip2px(activity, 60)) / 4);
			holder.imageView.setLayoutParams(params);
		}
		
		if(!isAddPic){
			holder.ll_delete_pic.setVisibility(View.GONE);
			
			if(!TextUtils.isEmpty(list.get(position).get("path"))){
				holder.imageView.setImageBitmap(BitmapPutUtil.getPathBitmap(list.get(position).get("path"), activity));
			} else if(!TextUtils.isEmpty(list.get(position).get("url"))){
				activity.imageLoader.displayImage(list.get(position).get("url"), holder.imageView, Util.getOptions_pic());
			} else if(!TextUtils.isEmpty(list.get(position).get("url_big"))){
				activity.imageLoader.displayImage(list.get(position).get("url_big"), holder.imageView, Util.getOptions_pic());
			}
			
			final int pos = position;
			holder.imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ArrayList<String> pics = new ArrayList<String>();
					for(int i = 0; i < list.size(); i++){
						pics.add(list.get(i).get("url_big"));
					}
					
					Intent intent = new Intent(activity, PicViewPagerActivity.class);
					intent.putStringArrayListExtra("pics", pics);	//原图
					//intent.putStringArrayListExtra("pics_thumb", pics); //缩略图
					intent.putExtra("current_item", pos+"");
					activity.startActivity(intent);
				}
			});
		}else{
			if (position == (list.size() - 1)) {
				holder.imageView.setImageResource(R.drawable.add_img);
				holder.ll_delete_pic.setVisibility(View.GONE);
			} else {
				holder.ll_delete_pic.setVisibility(View.VISIBLE);
				if(!TextUtils.isEmpty(list.get(position).get("path"))){
					holder.imageView.setImageBitmap(BitmapPutUtil.getPathBitmap(list.get(position).get("path"), activity));
				} else if(!TextUtils.isEmpty(list.get(position).get("url"))){
					activity.imageLoader.displayImage(list.get(position).get("url"), holder.imageView, Util.getOptions_pic());
				}
				
				final int pos = position;
				holder.ll_delete_pic.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						list.remove(pos);
						notifyDataSetChanged();
						
						Message message = new Message();
						message.what = DELETE_PIC;
						message.arg1 = pos;
						handler.sendMessage(message);
					}
				});
				
				/*holder.imageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
					}
				});*/
			}
		}
		
		return convertView;
	}
	
	/*private Uri getPicUri(String picPath){
		Uri mUri = Uri.parse("content://media/external/images/media");
		Uri mImageUri = null; 
		Cursor cursor = activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, 
				MediaStore.Images.Media.DEFAULT_SORT_ORDER);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)); 
			if (picPath.equals(data)) {
				int ringtoneID = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
				mImageUri = Uri.withAppendedPath(mUri, "" + ringtoneID);
				return mImageUri;
				} 
			cursor.moveToNext();
		}
		return null;
	}*/

	public void addImgPath(HashMap map) {
		list.add(list.size() - 1, map);
		notifyDataSetChanged();
	}
	
	class ViewHolder{
		ImageView imageView;
		LinearLayout ll_delete_pic;
	}
}
