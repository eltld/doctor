package net.ememed.doctor2.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Pattern;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.db.BankConfigTable;
import net.ememed.doctor2.db.BanksTable;
import net.ememed.doctor2.entity.BankCardInfo;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.IncomeExpend;
import net.ememed.doctor2.entity.SetBankCard;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
/**设置银行卡*/
public class BankCardActivity extends BasicActivity implements
		OnRefreshListener {
	private PullToRefreshLayout mPullToRefreshLayout;
	private TextView top_title;
	private Button btnSave;
	private EditText et_bank_number;
	private TextView et_bank_name;
	private EditText et_bank_branch;
	private EditText et_hodler;
//	private EditText et_card_number;

	private String bankNumber;
	private String bankName;
	private String bankBranch;
	private String Hodler;

	private LinearLayout ll_bank_name;
	private BankCardInfo data;
//	private NiftyDialogBuilder dialogBuilder;
//	private Effectstype effect;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.finance_details);
		data = (BankCardInfo) getIntent().getSerializableExtra("data");
//		dialogBuilder = new NiftyDialogBuilder(this,
//				R.style.dialog_untran);
	}

	@Override
	protected void setupView() {
		super.setupView();
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.add_bank_card_title));
		btnSave = (Button) findViewById(R.id.btn_addhealth);
		btnSave.setVisibility(View.VISIBLE);
		btnSave.setBackgroundDrawable(null);
		btnSave.setText(getString(R.string.bt_save));

		et_bank_number = (EditText) findViewById(R.id.et_bank_number);
		et_bank_name = (TextView) findViewById(R.id.et_bank_name);
		et_bank_branch = (EditText) findViewById(R.id.et_bank_branch);
		et_hodler = (EditText) findViewById(R.id.et_hodler);
//		et_card_number = (EditText) findViewById(R.id.et_card_number);

		ll_bank_name = (LinearLayout) findViewById(R.id.ll_bank_name);

		if (SharePrefUtil.getBoolean(Conast.FLAG)) {
			et_bank_name.setText(SharePrefUtil.getString(Conast.BANK_NAME));
			et_bank_number.setText(SharePrefUtil.getString(Conast.BANK_NUMBER));
			et_hodler.setText(SharePrefUtil.getString(Conast.BANK_HOLDER));
			et_bank_branch.setText(SharePrefUtil.getString(Conast.BANK_BRANCH));
//			et_card_number.setText(SharePrefUtil.getString(Conast.CARDNUMBER));
		} else {
			if (data != null) {
				et_bank_name.setText(data.getBANKNAME());

				et_bank_number.setText(data.getBANKCARDNUM());
				et_hodler.setText(data.getHOLDER());
				et_bank_branch.setText(data.getBANKBRANCH());
//				et_card_number.setText(data.getCARDNUMBER());
			}
		}
		//自动弹出软键盘
		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager inputManager = (InputMethodManager) BankCardActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.toggleSoftInput(0,
							InputMethodManager.HIDE_NOT_ALWAYS);
					et_hodler.setFocusable(true);
					et_hodler.setFocusableInTouchMode(true);
					et_hodler.requestFocus();
				}
			}, 200L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		} else if (view.getId() == R.id.btn_addhealth) {
			setBankcard();
		} else if (view.getId() == R.id.ll_bank_name) {
			BankConfigTable banks = new BankConfigTable();
			final String[] banksNames = { "中国银行", "中国建设银行", "中国农业银行", "中国工商银行"};
//			View view2 = LayoutInflater.from(this)
//					.inflate(R.layout.custom_view, null);
//			effect = Effectstype.Slideleft;
////			String defaultStr = pay_txt.getText().toString().trim();
//			dialogBuilder
//					.withTitle("选择银行")
//					// .withTitle(null) no title
//					.withTitleColor("#000000")
//					// def
//					// .withDividerColor("#11000000")
//					// def
//					.withMessage(null)
//					// .withMessage(null) no Msg
//					.withMessageColor("#000000")
//					// def
//					.withIcon(
//							getResources().getDrawable(
//									R.drawable.medical_history_ememed2))
//					.isCancelableOnTouchOutside(true) // def |
//														// isCancelable(true)
//					.withDuration(700) // def
//					.withEffect(effect) // def Effectstype.Slidetop
//					.setCustomView(view2, this).show();
//
//			ListView listView = (ListView) view2.findViewById(R.id.list);
//			final SingleDmAdapter singleBaseAdapter = new SingleDmAdapter(this,
//					banksNames, "中国银行");
//			listView.setAdapter(singleBaseAdapter);
//			listView.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//					singleBaseAdapter.setName(banksNames[arg2]);
//					singleBaseAdapter.notifyDataSetChanged();
//					dialogBuilder.dismiss();
//					if (arg2 == 4) {
//						final NiftyDialogBuilder dialogBuilder2 = new NiftyDialogBuilder(
//								BankCardActivity.this,
//								R.style.dialog_untran);
//						effect = Effectstype.Flipv;
//						View view2 = LayoutInflater.from(
//								BankCardActivity.this).inflate(
//								R.layout.erh_person_ids, null);
//						final EditText idsExit = (EditText) view2
//								.findViewById(R.id.name);
//						dialogBuilder2
//								.withTitle("选择银行")
//								// .withTitle(null) no title
//								.withTitleColor("#000000")
//								// def
//								// .withDividerColor("#11000000")
//								// def
//								.withMessage(null)
//								// .withMessage(null) no Msg
//								.withMessageColor("#000000")
//								// def
//								.withIcon(
//										getResources()
//												.getDrawable(
//														R.drawable.medical_history_ememed2))
//								.isCancelableOnTouchOutside(true)
//								// def |
//								// isCancelable(true)
//								.withDuration(700)
//								// def
//								.withEffect(effect)
//								// def Effectstype.Slidetop
//								.setCustomView(view2,
//										BankCardActivity.this)
//								.withButton1Text("确定")
//								// def
//								// gone
//								.withButton2Text("取消")
//								// def gone
//								// .setCustomView(R.layout.custom_view,
//								// MainMeActivity.this)
//								// // .setCustomView(View
//								// or
//								// ResId,context)
//								.setButton1Click(
//										new View.OnClickListener() {
//											@Override
//											public void onClick(View v) {
//												String idStr = idsExit
//														.getText()
//														.toString().trim();
//												if (idStr == null
//														|| idStr.equals("")) {
//													showToast("请填写内容");
//													return;
//												}
//												dialogBuilder2.dismiss();
//												et_bank_name.setText(idStr);
//											}
//										})
//								.setButton2Click(
//										new View.OnClickListener() {
//											@Override
//											public void onClick(View v) {
//												dialogBuilder2.dismiss();
//											}
//										}).show();
//					} else {
//						et_bank_name.setText(banksNames[arg2]);
//					}
//					
//				}
//			});
			android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
					this);
			builder.setTitle("开户银行列表");
			builder.setItems(banksNames, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					et_bank_name.setText(banksNames[which]);
				}
			});
			builder.create().show();
		}
	}

	private void setBankcard() {
		bankNumber = et_bank_number.getText().toString().trim();
		bankName = et_bank_name.getText().toString().trim();
		bankBranch = et_bank_branch.getText().toString().trim();
		Hodler = et_hodler.getText().toString().trim();
		if (TextUtils.isEmpty(Hodler) && Hodler.length() == 0) {
			showToast("开户姓名必填哦");
			return;
		}
		if (TextUtil.isNotName(Hodler)) {
			showToast("请输入正确的姓名");
			return;
		}
		if (TextUtils.isEmpty(bankName) && bankName.length() == 0) {
			showToast("银行名称必填哦");
			return;
		}
		if (TextUtils.isEmpty(bankNumber) && bankNumber.length() == 0) {
			showToast("银行卡号必填哦");
			return;
		}
		if (TextUtils.isEmpty(bankBranch) && bankBranch.length() == 0) {
			showToast("开户行必填哦");
			return;
		}
//		if (TextUtils.isEmpty(et_card_number.getText().toString().trim())
//				&& et_card_number.getText().toString().trim().length() == 0) {
//			showToast("身份证必填哦");
//			return;
//		}
		// 处理是身份证号的情况
//		String hint = IDCard.IDCardValidate(et_card_number.getText().toString()
//				.trim());
//		if (!hint.equals("YES")) {
//			showToast(hint);
//			return;
//		}

		if (bankNumber.length() >= 16 && bankNumber.length() <= 22) {
			if (NetWorkUtils.detect(BankCardActivity.this)) {
				loading(null);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("doctorid",
						SharePrefUtil.getString(Conast.Doctor_ID));
				params.put("token",
						SharePrefUtil.getString(Conast.ACCESS_TOKEN));
				params.put("bankid", SharePrefUtil.getString(Conast.BANK_ID));
				params.put("bankcardnum", bankNumber);
				params.put("bankname", bankName);
				params.put("bankbranch", bankBranch);
				params.put("cardnumber",
						SharePrefUtil.getString(Conast.CARDNUMBER));
				params.put("holder", Hodler);
				params.put("cardnumber", "");
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
		} else {
			showToast("卡号填写有误哦");
		}

	}

	@Override
	protected void onResult(Message msg) {
		try {
			destroyDialog();
			SetBankCard bankCard = (SetBankCard) msg.obj;
			int success_2 = bankCard.getSuccess();
			switch (msg.what) {
			case IResult.BANK_CARD:
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
			case IResult.NET_ERROR:
				destroyDialog();
				showToast(IMessage.NET_ERROR);
				break;
			default:
				break;
			}
		} catch (Exception e) {

		}
		super.onResult(msg);
	}

	/** 保存银行卡信息 */
	private void SaveCardInfo(SetBankCard bankCard) {
		SharePrefUtil.putString(Conast.MBCID, bankCard.getMBCID());
		SharePrefUtil.putString(Conast.BANK_HOLDER, et_hodler.getText()
				.toString().trim());
		SharePrefUtil.putString(Conast.BANK_NAME, et_bank_name.getText()
				.toString().trim());
		SharePrefUtil.putString(Conast.BANK_NUMBER, et_bank_number.getText()
				.toString());
		SharePrefUtil.putString(Conast.BANK_BRANCH, et_bank_branch.getText()
				.toString());
//		SharePrefUtil.putString(Conast.CARDNUMBER, et_card_number.getText()
//				.toString());
		SharePrefUtil.putBoolean(Conast.FLAG, true);
		SharePrefUtil.commit();
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
	public void onRefreshStarted(View view) {
	}
}
