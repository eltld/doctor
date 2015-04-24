package net.ememed.doctor2.finace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.dialog.Effectstype;
import net.ememed.doctor2.dialog.NiftyDialogBuilder;
import net.ememed.doctor2.entity.BankCard;
import net.ememed.doctor2.entity.BankCardInfo;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.finace.adapter.BankcardAdapter2;
import net.ememed.doctor2.finace.bean.FinaceApplyBean;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.ListViewForScrollView;

public class FinaceNowCashActivity extends BasicActivity {
	private TextView top_title;
	private TextView txt_balance;
	private TextView txt_bank;
	private TextView txt_address;
	private EditText cashEdit;
	private Button btn;
	private NiftyDialogBuilder dialogBuilder;
	private Effectstype effect;
	private String balance;
	private boolean isBank = false; //是否选择银行卡
	private boolean isZhi = false; //是否选择支付宝
	private List<BankCardInfo> bankcardData = null;
	private BankCardInfo choiceBank = null;
	private ListViewForScrollView lv_bankcard; 
	private BankcardAdapter2 adapter;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_finace_cash);
		dialogBuilder = new NiftyDialogBuilder(this,
				R.style.dialog_untran);
		balance = getIntent().getExtras().getString("balance");
		bankcardData = new ArrayList<BankCardInfo>();
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("提现");
		txt_balance = (TextView) findViewById(R.id.txt_cash);
		txt_balance.setText("可提取金额为" + balance + "元");
		cashEdit = (EditText) findViewById(R.id.edit_cash);
		btn = (Button) findViewById(R.id.btn_money);
		
		lv_bankcard = (ListViewForScrollView) findViewById(R.id.lv_bankcard);
		adapter = new BankcardAdapter2(null, this);
		lv_bankcard.setAdapter(adapter);
		lv_bankcard.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.setCurrentChoice(position);
				
				for(int i = 0; i < lv_bankcard.getCount();i++){
					if(null != lv_bankcard.getChildAt(i)){
						lv_bankcard.getChildAt(i).findViewById(R.id.btn_bank).setBackgroundResource(R.drawable.finace_circle);
					}
				}
				view.findViewById(R.id.btn_bank).setBackgroundResource(R.drawable.finace_gou);
				
				choiceBank = bankcardData.get(position);
				
				if(!TextUtils.isEmpty(bankcardData.get(position).getALIPAY_ACCOUNT())){
					isZhi = true;
					isBank = false;
				}else{
					isZhi = false;
					isBank = true;
				}
			}
		});
		
		/*LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.finace_cash_next_button, null);
		Button btn_next = (Button) layout.findViewById(R.id.btn_next);
		btn_next.setOnClickListener(new View.OnClickListener() {  
              
            @Override  
            public void onClick(View v) {  
            	
            }  
        });  
        lv_bankcard.addFooterView(layout);  */
		
		/*imageButton1 = (ImageButton) findViewById(R.id.btn_bank);
		imageButton2 = (ImageButton) findViewById(R.id.btn_zhifubao);
		imageButton1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (isBank) {
					isBank = false;
					imageButton1.setBackgroundResource(R.drawable.finace_circle);
				} else {
					isBank = true;
					isZhi = false;
					imageButton1.setBackgroundResource(R.drawable.finace_gou);
					imageButton2.setBackgroundResource(R.drawable.finace_circle);
				}
				
			}
		});*/
		
		if(null != BankCardInfoCommon.getInstance().getBankcardList() || null != BankCardInfoCommon.getInstance().getZhifubaoList()){
			makeAdapterData();
		}else{
			getInitData();	//获取银行卡信息列表
		}
	}
	
	private void postApplyData(String money, String bankName, String bankCard, String zhifubaoMoney) {
		if (NetWorkUtils.detect(FinaceNowCashActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("amount", money);
			if (isBank) {
				params.put("account_type", 1 + "");
				params.put("bankname", bankName);
				params.put("bankcardnum", bankCard);
				params.put("alipay_account", "");
			} else {
				params.put("account_type", 2 + "");
				params.put("bankname", "");
				params.put("bankcardnum", "");
				params.put("alipay_account", zhifubaoMoney);
			}
			Log.e("XX", params.toString());
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.post_doctor_apply, FinaceApplyBean.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.APPLY_CASH;
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
	
	private void getInitData() {
		if (NetWorkUtils.detect(FinaceNowCashActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_bankcard, BankCard.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_BANKCARD;
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
			if (msg.what == IResult.GET_BANKCARD) {
				BankCard bankCard2 = (BankCard) msg.obj;
				if (null != bankCard2) {
					if (bankCard2.getSuccess() == 1) {
						// if(refresh){
						List<BankCardInfo> info = bankCard2.getData();
						if (null != info && info.size() > 0) {
							BankCardInfoCommon.getInstance().clearbankCardInfo();
							BankCardInfoCommon.getInstance().saveBankCardInfo(info);
							makeAdapterData();	//生成List列表，放入adapter
						}
					} else {
						showToast(bankCard2.getErrormsg());
					}
				} else {
					showToast(IMessage.DATA_ERROR);
				}
			} else if (msg.what == IResult.APPLY_CASH){
				FinaceApplyBean applyBean = (FinaceApplyBean) msg.obj;
				int success_2 = applyBean.getSuccess();
				if (success_2 == 0) {
					showToast((String) applyBean.getErrormsg());
					return;
				} else {
					//showToast("提交成功");
					BankCardInfoCommon.getInstance().setTotalMoney(Double.parseDouble(applyBean.getData().getAvailable()));
					Utils.startActivity(FinaceNowCashActivity.this, FinaceFinallyActivity.class);
					finish();
				}
			}
		}
		
	};
	
	private void makeAdapterData() {
		bankcardData.clear();
	
		List<BankCardInfo> bankcardList = BankCardInfoCommon.getInstance().getBankcardList();
		List<BankCardInfo> zhifubaoList = BankCardInfoCommon.getInstance().getZhifubaoList();
		
		if(null != zhifubaoList && zhifubaoList.size() > 0){
			for(BankCardInfo cardInfo : zhifubaoList){
				bankcardData.add(cardInfo);
			}
		}
		
		if(null != bankcardList && bankcardList.size() > 0){
			for(BankCardInfo cardInfo : bankcardList){
				bankcardData.add(cardInfo);
			}
		}
		
		adapter.change(bankcardData);
	}
	
	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		} else if(view.getId() == R.id.btn_money){
			String cashNum = cashEdit.getText().toString().trim();
			
			if (TextUtils.isEmpty(cashNum)) {
				showToast("请填写取现金额！");
				return;
			}
			if(Float.parseFloat(cashNum)<100.0){
				showToast("提现金额不应低于100元！");
				return;
			}
			if(cashNum.contains(".")){
				if(cashNum.indexOf(".") + 2 < cashNum.length()-1){
					showToast("对不起，您输入的金额不正确，最多可输入小数点后2位（例如：100.05），请重新输入，谢谢！");
					return;
				}
			}
			
			if (!isBank && !isZhi) {
				showToast("请选择一个提现账户！");
				return;
			}
			String money, bankName, bankCard, zhifubaoMoney;
			View view2 = LayoutInflater.from(FinaceNowCashActivity.this)
					.inflate(R.layout.activity_finace_gou, null);
			
			effect = Effectstype.Slideleft;
			dialogBuilder
					.withTitle("确认提现")
					.withTitleColor("#00A600")
					.withMessage(null)
					.withMessageColor("#000000")
					.withIcon(getResources().getDrawable(R.drawable.dialog_ememed))
					.isCancelableOnTouchOutside(true) 
					.withDuration(700) // def
					.withEffect(effect) // def Effectstype.Slidetop
					.setCustomView(view2, FinaceNowCashActivity.this)
					.withButton1Text("取消")
					.withButton2Text("确定")
					.setButton2Click(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							dialogBuilder.dismiss();
							postApplyData(cashEdit.getText().toString().trim(), choiceBank.getBANKNAME(),
									choiceBank.getBANKCARDNUM(), choiceBank.getALIPAY_ACCOUNT());
						}
					}).setButton1Click(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							dialogBuilder.dismiss();
						}
					}).show();
			
			TextView cash = (TextView) view2.findViewById(R.id.cash);
			TextView bank_name = (TextView) view2.findViewById(R.id.bank_name);
			TextView cash_card = (TextView) view2.findViewById(R.id.cash_card);
			TextView user_name2 = (TextView) view2.findViewById(R.id.user_name2);
			TextView txt_bank_name = (TextView) view2.findViewById(R.id.txt_bank_name);
			TextView txt_bank_card = (TextView) view2.findViewById(R.id.txt_bank_card);
			ImageView bank_icon = (ImageView) view2.findViewById(R.id.bank_icon);
			cash.setText(cashEdit.getText().toString().trim() + "元");
			if (isBank) {
				cash_card.setText(choiceBank.getBANKCARDNUM());
				bank_icon.setImageResource(R.drawable.finace_yinlian);
				txt_bank_name.setText("银行名称");
				bank_name.setText(choiceBank.getBANKNAME());
				txt_bank_card.setText("银行卡号");
			} else {
				bank_name.setText("支付宝");
				cash_card.setText(choiceBank.getALIPAY_ACCOUNT());
				bank_icon.setImageResource(R.drawable.finace_zhifubao);
				txt_bank_name.setText("支付名称");
				txt_bank_card.setText("支付宝账号");
			}
			user_name2.setText(choiceBank.getHOLDER());
		}
	}
}
