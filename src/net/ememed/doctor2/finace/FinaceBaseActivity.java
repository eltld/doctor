package net.ememed.doctor2.finace;

import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Text;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonParseException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.OrderClassifyActivity;
import net.ememed.doctor2.dialog.Effectstype;
import net.ememed.doctor2.dialog.NiftyDialogBuilder;
import net.ememed.doctor2.entity.BankCard;
import net.ememed.doctor2.entity.BankCardInfo;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.MemberInfo;
import net.ememed.doctor2.entity.SetBankCard;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.IDCardUtil;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;

public class FinaceBaseActivity extends BasicActivity {
	
	private final int RESULT_GET_BANKCARD = 0x12;
	
	private TextView top_title;
	private EditText finace_name, finace_idcard, finace_zhifubao_pay, finace_bank_address, finace_bank_idcard;
	private NiftyDialogBuilder dialogBuilder;
	private Effectstype effect;
	private String bankCardService;
	private String MBCID = "";
//	private List<BankCardInfo> data;
		
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_finace_base);
		dialogBuilder = new NiftyDialogBuilder(this,
				R.style.dialog_untran);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.finace_id));
		
		finace_name = (EditText) findViewById(R.id.finace_name); 
		finace_idcard = (EditText) findViewById(R.id.finace_idcard); 
		finace_zhifubao_pay = (EditText) findViewById(R.id.finace_zhifubao_pay); 
		finace_bank_address = (EditText) findViewById(R.id.finace_bank_address); 
		finace_bank_idcard = (EditText) findViewById(R.id.finace_bank_idcard);
		
		getInitData();
	}
	
	/** 保存银行卡信息 */
	private void SaveCardInfo(SetBankCard bankCard) {
		SharePrefUtil.putString(Conast.MBCID, bankCard.getMBCID());
		SharePrefUtil.putString(Conast.BANK_HOLDER, finace_name.getText()
				.toString().trim());
//		SharePrefUtil.putString(Conast.BANK_NAME, et_bank_name.getText()
//				.toString().trim());
		SharePrefUtil.putString(Conast.BANK_NUMBER, finace_bank_idcard.getText()
				.toString());
		SharePrefUtil.putString(Conast.BANK_BRANCH, finace_bank_address.getText()
				.toString());
//		SharePrefUtil.putString(Conast.CARDNUMBER, et_card_number.getText()
//				.toString());
		SharePrefUtil.putBoolean(Conast.FLAG, true);
		SharePrefUtil.commit();
	}
	
	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		} else if (view.getId() == R.id.finace_name){
			getEditDialog(finace_name, "姓名");
		} else if (view.getId() == R.id.finace_idcard){
			getEditDialog(finace_idcard, "身份证");
		} else if (view.getId() == R.id.finace_zhifubao_pay){
			getEditDialog(finace_zhifubao_pay, "支付宝");
		} else if (view.getId() == R.id.finace_bank_address){
			getEditDialog(finace_bank_address, "开户行信息");
		} else if (view.getId() == R.id.finace_bank_idcard){
			getEditDialog(finace_bank_idcard, "银行卡号");
		} else if (view.getId() == R.id.btn_money){
			setBankcard();
		}
	}

	private void setBankcard() {
		StringBuffer buf = new StringBuffer();
		String temp = finace_bank_idcard.getText().toString().replace(" ", "");
		buf.append(temp);
		String bankNumber = buf.toString();		
		String bankBranch = finace_bank_address.getText().toString().trim();
		String Hodler = finace_name.getText().toString().trim();
		String zhifubaoNum = finace_zhifubao_pay.getText().toString().trim();
		String CARDNUMBER = finace_idcard.getText().toString().trim();
		if (TextUtils.isEmpty(Hodler) && Hodler.length() == 0) {
			showToast("姓名必填哦");
			return;
		}
		if (TextUtil.isNotName(Hodler)) {
			showToast("请输入正确的姓名");
			return;
		}

		if (TextUtils.isEmpty(zhifubaoNum) && TextUtils.isEmpty(bankNumber)) {
			showToast("支付宝、银行卡必填一项");
			return;
		}
		if (!TextUtils.isEmpty(bankNumber)) {
			if (TextUtils.isEmpty(bankBranch) && bankBranch.length() == 0) {
				showToast("开户行信息需要填写");
				return;
			}
		}

		if (NetWorkUtils.detect(FinaceBaseActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid",
					SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token",
					SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("bankid", MBCID);
			if (TextUtils.isEmpty(bankCardService)) {
				if (TextUtils.isEmpty(bankNumber)) {
					params.put("bankcardnum", "");
				} else {
					params.put("bankcardnum", bankNumber);
				}		
			} else {
				params.put("bankcardnum", bankNumber);
			}
			
			if (TextUtils.isEmpty(bankBranch)) {
				params.put("bankname", "");
			} else {
				params.put("bankname", bankBranch);
			}
			
			if (TextUtils.isEmpty(bankBranch)) {
				params.put("bankbranch", "");
			} else {
				params.put("bankbranch", bankBranch);
			}
			
			if (TextUtils.isEmpty(CARDNUMBER)) {
				params.put("cardnumber", "");
			} else {
				params.put("cardnumber", CARDNUMBER);
			}
			
			params.put("alipay_account",
					finace_zhifubao_pay.getText().toString().trim());
			params.put("holder", Hodler);
			params.put("channel", "android");
			Log.e("XX", params.toString() + "");
			
			
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.set_bankcard, SetBankCard.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.BANK_CARD;
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

	private void getEditDialog(final TextView finace_name2, final String title){
		effect = Effectstype.Flipv;
		View view2 = LayoutInflater.from(this).inflate(
				R.layout.erh_person_ids, null);
		final EditText idsExit = (EditText) view2.findViewById(R.id.name);
		idsExit.setText(finace_name2.getText().toString().trim());
		idsExit.setSelection(finace_name2.getText().length());
		
		if(title.equals("银行卡号")){
			idsExit.setInputType(InputType.TYPE_CLASS_DATETIME);
			//设置银行卡输入时四个数字加一个空格
			Utils.setEditTextListenerForInputBankCard(idsExit);
		}
		
		dialogBuilder
				.withTitle(title)
				// .withTitle(null) no title
				.withTitleColor("#000000")
				// def
				// .withDividerColor("#11000000")
				// def
				.withMessage(null)
				// .withMessage(null) no Msg
				.withMessageColor("#000000")
				// def
				.withIcon(
						getResources().getDrawable(
								R.drawable.medical_history_ememed2))
				.isCancelableOnTouchOutside(true) // def |
													// isCancelable(true)
				.withDuration(700) // def
				.withEffect(effect) // def Effectstype.Slidetop
				.setCustomView(view2, this).withButton1Text("确定") // def
																	// gone
				.withButton2Text("取消") // def gone
				// .setCustomView(R.layout.custom_view, MainMeActivity.this)
				// // .setCustomView(View
				// or
				// ResId,context)
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						String idStr = idsExit.getText().toString().trim();
						if (idStr == null || idStr.equals("")) {
							showToast("请填写" + title);
							return;
						}	
						if (title.equals("身份证")) {
							String result = IDCardUtil.IDCardValidate(idStr);
							if(!result.equals("YES")) {
								showToast(result);
								return;
							}
						}	
						
						finace_name2.setText(idStr);
						dialogBuilder.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.dismiss();
					}
				}).show();
	}
	
	@Override
	protected void onResult(Message msg) {
		try {
			destroyDialog();
			switch (msg.what) {
			case IResult.BANK_CARD:
				SetBankCard bankCard = (SetBankCard) msg.obj;
				int success_2 = bankCard.getSuccess();
				if (success_2 == 0) {
					showToast((String) bankCard.getErrormsg());
					return;
				} else {
					showToast("保存成功");
					SaveCardInfo(bankCard);
					setResult(RESULT_OK);
					finish();
				}
				break;
			case IResult.DATA_ERROR:
				showToast("保持失败");
				break;
			case IResult.NET_ERROR:
				showToast(IMessage.NET_ERROR);
				break;
			default:
				break;
			}
		} catch (Exception e) {

		}
		super.onResult(msg);
	}
	
	private void getInitData() {
		if (NetWorkUtils.detect(FinaceBaseActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			Log.e("XX", params.toString());
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_bankcard, BankCard.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = RESULT_GET_BANKCARD;
							mHandler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							mHandler.sendMessage(message);
						}
					});
		} else {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
		}
		
	}
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			destroyDialog();
			if (msg.what == RESULT_GET_BANKCARD) {
				BankCard bankCard2 = (BankCard) msg.obj;
				if (null != bankCard2) {
					if (bankCard2.getSuccess() == 1) {
						// if(refresh){
						List<BankCardInfo> data = bankCard2.getData();
						if (null != data && data.size() > 0) {
							if (!TextUtils.isEmpty(data.get(0).getBANKCARDNUM())) {
								bankCardService = data.get(0).getBANKCARDNUM();
								
								String cardNum = data.get(0).getBANKCARDNUM();
								StringBuffer buf = new StringBuffer();
								for(int i = 0; i < cardNum.length(); i++){
									if(i > 0 && i < cardNum.length() - 1 && i % 4 == 0){
										buf.append(" ");
									}
									buf.append(cardNum.substring(i,i+1));
								}
								finace_bank_idcard.setText(buf);
								
								finace_bank_address.setText(data.get(0).getBANKNAME());
							}
							MBCID = data.get(0).getMBCID();
							finace_name.setText(data.get(0).getHOLDER());
							if (!TextUtils.isEmpty(data.get(0).getALIPAY_ACCOUNT())) {
								finace_zhifubao_pay.setText(data.get(0).getALIPAY_ACCOUNT());
							}
							
							if (!TextUtils.isEmpty(data.get(0).getCARDNUMBER())) {
								finace_idcard.setText(data.get(0).getCARDNUMBER());
							} 
						}

					} else {
						showToast(bankCard2.getErrormsg());
					}
				} else {
					showToast(IMessage.DATA_ERROR);
				}
			}
		}
		
	};
}
