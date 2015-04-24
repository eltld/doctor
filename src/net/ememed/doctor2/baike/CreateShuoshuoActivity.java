package net.ememed.doctor2.baike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import de.greenrobot.event.util.ImageUploadTool;
import de.greenrobot.event.util.ImageUploadTool.DownloadStateListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.ShowBigImage;
import net.ememed.doctor2.baike.adapter.PutImageAdapter;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BitmapUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.MenuDialog;
import net.ememed.doctor2.widget.MyGridView;

public class CreateShuoshuoActivity extends BasicActivity implements DownloadStateListener{
	
	//private final int REQUEST_PUBLISH_SAYS = 1;
	private final int DELETE_PIC = 888; 
	private TextView tv_tip;
	private PutImageAdapter adapter;
	private MyGridView gridView;
	private List<String> picList;
	private TextView tv_menu;
	//private String curPicPath;	//当前上传的照片的path
	private EditText et_content;
	private ImageUploadTool imageUploadTool;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_create_shuo);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		((TextView)findViewById(R.id.top_title)).setText("创建说说");
		tv_menu = (TextView)findViewById(R.id.tv_menu);
		tv_menu.setVisibility(View.VISIBLE);
		tv_menu.setText("发表");
		
		et_content = (EditText) findViewById(R.id.et_content);
		
		setTextColor();
		adapter = new PutImageAdapter(null, this, handler);
		gridView = (MyGridView) findViewById(R.id.gridview);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == (adapter.getCount() - 1)) {
					setDoctorImage();
				} else {
					Intent bigPicIntent = new Intent(CreateShuoshuoActivity.this, ShowBigImage.class);
					HashMap<String, String> map = (HashMap<String, String>) adapter.getItem(position);
					if(!TextUtils.isEmpty(map.get("path"))){
						bigPicIntent.putExtra("path", map.get("path"));	//携带本地路径
					}/* else {
						if(!TextUtils.isEmpty(map.get("url"))){			//携带服务器路径
							bigPicIntent.putExtra("remotepath", map.get("url"));
						}
					}*/
					CreateShuoshuoActivity.this.startActivity(bigPicIntent);	
				}
			}
		});
	}
	
	private void publishShuoshuo(String content,String pics){
		if (NetWorkUtils.detect(CreateShuoshuoActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("content", content);
			if(null != pics){
				params.put("pics", pics);
			}
		
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_says, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.SET_SAY;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
	
	
	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);
		try {
			destroyDialog();
			switch (msg.what) {
			case IResult.SET_SAY:
				CommonResponseEntity response = (CommonResponseEntity) msg.obj;
				if (null != response) {
					if (1 == response.getSuccess()) {
						showToast("发表成功");
						setResult(RESULT_OK);
						finish();
					} else {
						showToast(response.getErrormsg());
					}
				} else {
					showToast("发表失败");
				}
				
				break;
			case DELETE_PIC:
				picList.remove(msg.arg1);
				break;
			case IResult.NET_ERROR:
				showToast(getString(R.string.net_error));
				break;
			case IResult.DATA_ERROR:
				showToast("数据出错！");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
/*	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_PUBLISH_SAYS:
			if (RESULT_OK == resultCode && null != data) {
				String account = data.getStringExtra("account");
				String pwd = data.getStringExtra("pwd");
			}
			break;
		default:
			break;
		}
	}*/
	
	private void setDoctorImage() {
		final Context dialogContext = new ContextThemeWrapper(this,
				android.R.style.Theme_Light);
		String[] choices;
		choices = new String[2];
		choices[0] = getString(R.string.change_avatar_take_photo); // 拍照
		choices[1] = getString(R.string.change_avatar_album); // 从相册中选择

		MenuDialog.Builder builder = new MenuDialog.Builder(dialogContext);
		MenuDialog dialog = builder.setTitle("选择要上传的照片")
				.setItems(choices, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							String status = Environment
									.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								basictakePicture(false);
							}
							break;
						case 1:
							basicopenGallery(false);
							break;
						}
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	private void setTextColor(){
		/*String[] text = getResources().getStringArray(R.array.baike_tip);
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < text.length; i++){
			sb.append(text[i]);
		}
		
		int color1 = getResources().getColor(R.color.black);
		int color2 = getResources().getColor(R.color.blue);
		
		SpannableStringBuilder spannable = new SpannableStringBuilder(sb.toString());
		int begin = 0;
		for(int i = 0; i < text.length; i++){
			int textLength = text[i].length();
			if(1 == i || 3 == i){
				spannable.setSpan(new ForegroundColorSpan(color2), begin, begin+textLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			} else {
				spannable.setSpan(new ForegroundColorSpan(color1), begin, begin+textLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}
			begin += textLength;
		}*/
		
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		String exchange = getResources().getString(R.string.create_say_tip);  
		tv_tip.setText(Html.fromHtml(exchange)); 
//		tv_tip.setText(spannable);
	}
	
	@Override
	public void onCameraPath(String path) {
		super.onCameraPath(path);
		//curPicPath = path;
		
		/*final Bitmap bmp = getBitmapByPath(path);
		uploadImage(bmp);
		loading("正在上传照片");*/
		
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("path", path);
		adapter.addImgPath(tempMap);
		
		if(null == picList){
			picList = new ArrayList<String>();
		}
		picList.add(path);
	}
	
	/**
	  * 生成与原图同样大小的Bitmap，不作压缩
	  * 
	  * @param path
	  * @return
	  */
	 public static Bitmap getBitmapByPath(String path) {
		 if (path == null)
			 return null;
		 
		 Bitmap temp = null;
		
		 try {
			 temp = BitmapFactory.decodeFile(path);
		 } catch (Exception e) {
			 e.printStackTrace();
		 } catch (Error e) {
			 e.printStackTrace();
		 }
		
		 return temp;
	 }
	
	// 上传图像
	/*private void uploadImage(final Bitmap bmp) {
		new Thread() {
			public void run() {
				try {
					byte[] bytes = BitmapUtil.Bitmap2Bytes(bmp);
					ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("doctorid", SharePrefUtil.getString(Conast.Doctor_ID)));
					params.add(new BasicNameValuePair("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN)));
					params.add(new BasicNameValuePair("ext", "jpg"));
					params.add(new BasicNameValuePair("dir", "15"));//上传目录，此处指定为15
							
					String json = HttpUtil.uploadFile(HttpUtil.URI + HttpUtil.set_says_pic, params, bytes);
					json = net.ememed.doctor2.util.TextUtil.substring(json, "{");
					final Gson gson = new Gson();
		//			SetbSayPicEntry entry = gson.fromJson(json, SetbSayPicEntry.class);
					HashMap<String, Object> map = new HashMap<String, Object>();
					JSONObject obj = new JSONObject(json);
					map.put("success", obj.getInt("success"));
					map.put("errormsg", obj.getString("errormsg"));
					JSONObject obj_data = (JSONObject) obj.get("data");
					String url = obj_data.getString("URL");
					map.put("url", url);
					sendMessage(IResult.SUCCESS, map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}*/
	
	public void doClick(View view){
		switch(view.getId()){
		case R.id.btn_back:
			finish();
			break;
		case R.id.tv_menu:
			String text = et_content.getText().toString().trim();
			if(TextUtils.isEmpty(text)){
				showToast("请输入您要发表的内容");
				return;
			}
			
			/*List<Map<String, String>> list = adapter.getList();
			ArrayList<String> tempList = new ArrayList<String>();
			if(list.size() > 0){
				for(int i = 0; i < list.size(); i++){
					if(i < 4 && list.get(i).get("url") != null){
						tempList.add(list.get(i).get("url"));
					}
				}
			}
			StringBuilder json = null;
			json = new StringBuilder();
			json.append("[");
			for(int i = 0; i < tempList.size(); i++){
				json.append("\"");
				json.append(list.get(i).get("url"));
				json.append("\"");
				if(i < tempList.size() - 1){
					json.append(",");
				}
			}
			json.append("]");
			
			String pics = json.toString();
			pics = pics.replace("/", "\\/");
			Log.i("测试", pics);*/
			loading(null);
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("doctorid", SharePrefUtil.getString(Conast.Doctor_ID)));
			params.add(new BasicNameValuePair("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN)));
			params.add(new BasicNameValuePair("dir", "15"));//上传目录，此处指定为15
			
			imageUploadTool = new ImageUploadTool(this, picList, params, HttpUtil.set_says_pic, "ext", this);
			
			break;
		}
	}
	
	
	
	@Override
	protected void onStop() {
		super.onStop();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_says_pic);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_says);
	}

	@Override
	public void onFinish(Map<String, String> successURL) {
		String text = et_content.getText().toString().trim();
		String pics = ListTOJson(successURL);
		publishShuoshuo(text, pics);
	}

	@Override
	public void onFailed() {
		// TODO Auto-generated method stub
		
	}
	
	public String ListTOJson(Map<String, String> successURL) {
		StringBuffer sb = new StringBuffer();
		
		JSONArray array = new JSONArray();
		sb.append("[");
		for (int i = 0; i < successURL.size(); i++) {
			array.put(successURL.get(picList.get(i)));
		}
		sb.append("]");
		return array.toString();
	}
}	
