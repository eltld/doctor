package net.ememed.doctor2.finace;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.entity.BankCard;
import net.ememed.doctor2.entity.BankCardInfo;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.SetBankCard;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.IDCardUtil;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;

public class AddZhifubaoActivity extends BasicActivity{
	
	private EditText et_name;
	private EditText et_zhifubao_id;
	
	private TextView btn_save;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.add_zhifubao);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		et_name = (EditText) findViewById(R.id.et_name);
		et_zhifubao_id = (EditText) findViewById(R.id.et_zhifubao_id);
		
		btn_save = (TextView) findViewById(R.id.tv_menu);
		btn_save.setVisibility(View.VISIBLE);
		btn_save.setText("保存");
		btn_save.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				 switch (event.getAction()) { 	  
	    	        case MotionEvent.ACTION_DOWN:	//按下
	    	        	btn_save.setTextColor(getResources().getColor(R.color.tab_light_gray));
	    	            break;
	    	        case MotionEvent.ACTION_UP://抬起
	    	        	btn_save.setTextColor(getResources().getColor(R.color.white));
	    	        	saveBankcard();
	    	            break;
	    	        default:
	    	            break;
				 }
				return true;
			}
		});
	}
	
	/**
	 * 判断邮箱号
	 * @param str
	 * @return
	 */
	private boolean isEmail(String str){
		String reg = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        if(str.matches(reg)){
           return true;
        }
        return false;
    }

	/**
	 * 判断手机号
	 * @param str
	 * @return
	 */
	private boolean isMobilePhone(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() || !str.substring(0,1).equals("1"))
		{
			return false;
		} 
		
		if(str.length() != 11){
			return false;
		}
		
		return true;
			
	}
	
	
	
	/**
	 * 保存银行卡
	 */
	private void saveBankcard(){
		String name = et_name.getText().toString().trim();
		String zhifubaoId = et_zhifubao_id.getText().toString().trim();

		if (!TextUtils.isEmpty(name) && TextUtil.isNotName(name)) {
			showToast("请输入正确的姓名");
			return;
		}
		
		if(TextUtils.isEmpty(zhifubaoId)){
			showToast("请输入支付宝账号");
			return;
		}
		
		if((!isEmail(zhifubaoId)) && (!isMobilePhone(zhifubaoId))){
			showToast("支付宝账号不正确");
			return;
		}
		
		setZhifubao(name, zhifubaoId);
	}
	
	private void setZhifubao(String name, String zhifubaoId) {
		
		if (NetWorkUtils.detect(AddZhifubaoActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("bankid", "");
			params.put("holder", name);
			params.put("alipay_account", zhifubaoId);
			
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_bankcard, SetBankCard.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.SET_BANKCARD;
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
	
	public void doClick(View view) {
		
		if(R.id.btn_back == view.getId()){
			finish();
		} 
	}
	
	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);
		try {
			destroyDialog();
			switch (msg.what) {
			case IResult.SET_BANKCARD:
				SetBankCard response = (SetBankCard) msg.obj;
				if (null != response) {
					if (response.getSuccess() == 1) {
						BankCardInfo info = new BankCardInfo();
						info.setHOLDER(et_name.getText().toString().trim());
						info.setALIPAY_ACCOUNT(et_zhifubao_id.getText().toString().trim());
						info.setMBCID(response.getMBCID());
						BankCardInfoCommon.getInstance().addZhifubao(info);
						showToast("绑定支付宝成功！");
						finish();
					} else {
						showToast(response.getErrormsg());
					}
				} else {
					showToast(IMessage.DATA_ERROR);
				}
				break;
			case IResult.NET_ERROR:
				showToast(getString(R.string.net_error));
				break;
			case IResult.DATA_ERROR:
				showToast("获取数据出错！");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_bankcard);
	}
}
