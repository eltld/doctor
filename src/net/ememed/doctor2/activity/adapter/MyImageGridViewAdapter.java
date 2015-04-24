package net.ememed.doctor2.activity.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.ememed.doctor2.R;
import net.ememed.doctor2.util.BitmapPutUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MyImageGridViewAdapter extends BaseAdapter implements ImageLoadingListener{

	private List<String> data;
	private Context context;
	private ImageLoader imageLoader;
	public DisplayImageOptions options = new DisplayImageOptions.Builder()
	.showImageOnLoading(R.drawable.avatar_large)
	.showImageForEmptyUri(R.drawable.avatar_large)
	.showImageOnFail(R.drawable.avatar_large)
	.cacheInMemory(true)
	.cacheOnDisk(true)
	.considerExifParams(true)
	.displayer(new RoundedBitmapDisplayer(20))
	.build();
	
	
	public MyImageGridViewAdapter(List<String> data, Context context,ImageLoader imageLoader) {

		this.data = data;
		this.context = context;
		this.imageLoader = imageLoader;
	}

	public int getCount() {
		if (data == null)
			return 0;
		if(data.size()>5){
			return 5;
		}
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		ImageView view = new ImageView(context);
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(BitmapPutUtil.convertdipTopx(context, 50),BitmapPutUtil.convertdipTopx(context, 50));
		view.setLayoutParams(params);
		imageLoader.displayImage(data.get(arg0),view ,options);
		arg1 = view;
		return arg1;
	}
	
	public List<String> getData(){
		return this.data;
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
