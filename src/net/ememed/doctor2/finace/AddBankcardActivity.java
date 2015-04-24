package net.ememed.doctor2.finace;

import java.util.HashMap;

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
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.IDCardUtil;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;

public class AddBankcardActivity extends BasicActivity{

	private EditText et_name;
	private EditText et_card_num;
	private EditText et_bank_name;
	private EditText et_bankcard_num;
	
	private TextView btn_save;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.add_bankcard);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		et_name = (EditText) findViewById(R.id.et_name);
		et_card_num = (EditText) findViewById(R.id.et_card_num);
		et_bank_name = (EditText) findViewById(R.id.et_bank_name);
		et_bankcard_num = (EditText) findViewById(R.id.et_bankcard_num);
		//设置银行卡输入时四个数字加一个空格
		Utils.setEditTextListenerForInputBankCard(et_bankcard_num);
		
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
	 * 保存银行卡
	 */
	private void saveBankcard(){
		String name = et_name.getText().toString().trim();
		String cardId = et_card_num.getText().toString().trim();
		String bankName = et_bank_name.getText().toString().trim();
		String bankcardNum = et_bankcard_num.getText().toString().replace(" ", "");
		
		if(TextUtils.isEmpty(name)){
			showToast("请输入用户姓名");
			return;
		}
		
		if (TextUtil.isNotName(name)) {
			showToast("请输入正确的姓名");
			return;
		}
		
		if(TextUtils.isEmpty(bankName)){
			showToast("请输入开户银行");
			return;
		}
		if(TextUtils.isEmpty(bankcardNum)){
			showToast("请输入银行卡号");
			return;
		}
		if(bankcardNum.length() < 16 || bankcardNum.length() > 19){
			showToast("银行卡号不正确");
			return;
		}
		
		if(!TextUtils.isEmpty(cardId)){
			String result = IDCardUtil.IDCardValidate(cardId);
			if(!result.equals("YES")) {
				showToast(result);
				return;
			}
		}
		
		setbankcard(name, cardId, bankName, bankcardNum);
	}
	
	private void setbankcard(String name, String cardId, String bankName, String bankcardNum) {
		
		if (NetWorkUtils.detect(AddBankcardActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("bankid", "");
			params.put("bankcardnum", bankcardNum);
			params.put("bankname", bankName);
			params.put("holder", name);
			params.put("cardnumber", cardId);
			
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
						info.setBANKNAME(et_bank_name.getText().toString().trim());
						info.setBANKCARDNUM(et_bankcard_num.getText().toString().replace(" ", ""));
						info.setCARDNUMBER(et_card_num.getText().toString().trim());
						info.setMBCID(response.getMBCID());
						BankCardInfoCommon.getInstance().addBankcard(info);
						showToast("绑定银行卡成功！");
						sendBroadcast(new Intent(ActionFilter.REQUEST_FINACE_DETAIL));
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
		super.onStop();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_bankcard);
	}
}
