package net.ememed.doctor2.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.ememed.doctor2.R;
import net.ememed.doctor2.util.BitmapUtil;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.ImageZoomView;
import net.ememed.doctor2.widget.SimpleZoomListener;
import net.ememed.doctor2.widget.ZoomState;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ZoomControls;

/**
 * 查看图片 双手触摸放大缩小，单手拖放
 * @author chen
 *
 */
public class PhotoDetailActivity extends BasicActivity implements OnClickListener{

	private Bitmap bmp;  
	private Bitmap tempBmp;
	
	private ImageZoomView mZoomView;
	private ZoomState mZoomState;
	private SimpleZoomListener mZoomListener;
	
	private static final int EXEU_GET_DATA_FAILED = 5;
	private Handler handler;
	private byte[] tempBytes;
	private String imgPathUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showimageview);
		
		Intent intent = getIntent();
		tempBytes = intent.getByteArrayExtra("thumbnail");
		imgPathUrl = intent.getStringExtra("imgPathUrl");
		mZoomView = (ImageZoomView) findViewById(R.id.zoomView);
		
		if (tempBytes != null) {
			tempBmp = BitmapUtil.bytes2Bimap(tempBytes);
			mZoomView.setImageBitmap(tempBmp);
			mZoomView.invalidateImage();
		}
		if (!TextUtils.isEmpty(imgPathUrl)) {
			mZoomView.setPorterDuffMode(false);
			mZoomView.setLoading(false);
			imageLoader.displayImage(imgPathUrl,mZoomView, Util.getOptions_pic());
			mZoomView.invalidateImage();
			mZoomView.setLoading(false);
			mZoomState = new ZoomState();
			mZoomView.setZoomState(mZoomState);
			mZoomListener = new SimpleZoomListener();
			mZoomListener.setZoomState(mZoomState);
			mZoomView.setOnTouchListener(mZoomListener);
			ZoomControls zoomCtrl = (ZoomControls) findViewById(R.id.zoomCtrl);
			zoomCtrl.setOnZoomInClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					float z = mZoomState.getZoom() + 0.25f;
					mZoomState.setZoom(z);
					mZoomState.notifyObservers();
				}
			});
			zoomCtrl.setOnZoomOutClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					float z = mZoomState.getZoom() - 0.25f;
					mZoomState.setZoom(z);
					mZoomState.notifyObservers();
				}
			});
			resetZoomState();
			
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		 MobclickAgent.onPause(this);
	}
	@Override
	public void onClick(View v) {
		 if (v.getId() == R.id.btn_back) {
				finish();
		} 
//		 else if (v == bt_save) {
//			UICore.eventTask(this, this, download_pic, "", null);
//		} 
	}
	
	@Override
	public void execute(int mes, Object obj) {
		switch (mes) {
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bmp != null)
			bmp.recycle();
		 mZoomView.setOnTouchListener(null);
		 if (mZoomState != null) {
			 mZoomState.deleteObservers();
		 }
	}
	
	private void resetZoomState() {
		mZoomState.setPanX(0.5f);
		mZoomState.setPanY(0.5f);
		mZoomState.setZoom(1f);
		mZoomState.notifyObservers();
	}


	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
		}
		finish();
	}
	
}
