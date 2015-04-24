package net.ememed.doctor2.fragment;

import java.util.HashMap;
import java.util.List;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.AboutUsActivity;
import net.ememed.doctor2.activity.FinaceDetailsActivity;
import net.ememed.doctor2.activity.GroupSearchActivity;
import net.ememed.doctor2.activity.InviteActivity;
import net.ememed.doctor2.activity.LogonResultActivity;
import net.ememed.doctor2.activity.MainActivity;
import net.ememed.doctor2.activity.PersonInfoActivity;
import net.ememed.doctor2.activity.ServiceEvaluationActivity;
import net.ememed.doctor2.activity.ServiceAllSettingActivity;
import net.ememed.doctor2.activity.SettingAppActivity;
import net.ememed.doctor2.activity.SettingPwdActivity;
import net.ememed.doctor2.baike.BaikeVisitorActivity;
import net.ememed.doctor2.db.ConfigTable;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.finace.FinaceDetails2Activity;
import net.ememed.doctor2.fragment.adapter.MineAdapter;
import net.ememed.doctor2.message_center.MessageCenterActivity;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;

import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 我的空间tab
 * 
 * @author taro chyaohui@gmail.com
 */
public class MineFragment extends Fragment implements Callback, OnClickListener {
	private static final String TAG = MineFragment.class.getSimpleName();
	public static final int EXEU_GET_DATA = 0;

	private FrameLayout mContentView = null;
	private Handler handler = null;
	private MainActivity activity = null;
	private TextView doctor_type;
	private TextView tv_money;
	private String auditStatus = null;		//资料审核状态
	
	public MineFragment() {
		this.activity = (MainActivity) getActivity();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		handler = new Handler(this);
		Logger.dout(TAG + "onCreate");
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// fragment可见时执行加载数据或者进度条等
			Logger.dout(TAG + "isVisibleToUser");
		} else {
			// 不可见时不执行操作
			Logger.dout(TAG + "unVisibleToUser");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.dout(TAG + "onCreateView");
		View view = inflater.inflate(R.layout.mine_layout, null);
		mContentView = (FrameLayout) view.findViewById(R.id.mine_mainView);
		setupView(view);
		return view;
	}
	

	private void setupView(View view) {
		CircleImageView image_person = (CircleImageView) view.findViewById(R.id.image_person);
		
		/*if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR))) {
			activity.imageLoader.displayImage(SharePrefUtil.getString(Conast.AVATAR), image_person, Util.getOptions_big_avatar());
		}else if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR_UNAUDIT))){
			activity.imageLoader.displayImage(SharePrefUtil.getString(Conast.AVATAR_UNAUDIT), image_person, Util.getOptions_big_avatar());
		}*/
		
		/*auditStatus = SharePrefUtil.getString(Conast.AUDIT_STATUS);
		if(auditStatus.equals("1")){
			if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR))) {
				activity.imageLoader.displayImage(SharePrefUtil.getString(Conast.AVATAR), image_person, Util.getOptions_avatar());
			} else {
				image_person.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
			}
		} else {
			image_person.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
		}*/
		
		String portrait = SharePrefUtil.getString(Conast.AVATAR);
		if (TextUtils.isEmpty(portrait)) 
			portrait = SharePrefUtil.getString(Conast.AVATAR_UNAUDIT);
		activity.imageLoader.displayImage(portrait, image_person, Util.getOptions_avatar());
		
		TextView doctor_name = (TextView) view.findViewById(R.id.doctor_name);
		if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.Doctor_Name))) {
			doctor_name.setText(SharePrefUtil.getString(Conast.Doctor_Name));
		}
		doctor_type = (TextView) view.findViewById(R.id.doctor_type);
		if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.Doctor_Type)) && "1".equals(SharePrefUtil.getString(Conast.Doctor_Type))) {
			doctor_type.setText(getResources().getString(R.string.doctor_type_quanke));
		} else {
			doctor_type.setText(getResources().getString(R.string.doctor_type_zhuanke));
		}
		LinearLayout person_info = (LinearLayout) view.findViewById(R.id.person_info);
		LinearLayout about_us = (LinearLayout) view.findViewById(R.id.about_us);
		LinearLayout finance_details = (LinearLayout) view.findViewById(R.id.finance_details);
		LinearLayout user_service_set = (LinearLayout) view.findViewById(R.id.user_service_set);
		LinearLayout service_evaluate = (LinearLayout) view.findViewById(R.id.service_evaluate);
		LinearLayout invate_hint = (LinearLayout) view.findViewById(R.id.invate_hint);
		View invate_hint_line = (View) view.findViewById(R.id.invate_hint_line);
		if(SharePrefUtil.getInt(Conast.YOUJIANGYAOQING)==1){
			invate_hint.setVisibility(View.VISIBLE);
			invate_hint_line.setVisibility(View.VISIBLE);
		}else{
			invate_hint.setVisibility(View.GONE);
			invate_hint_line.setVisibility(View.GONE);
		}
		
		tv_money = (TextView) view.findViewById(R.id.tv_money);
		if(!TextUtils.isEmpty(SharePrefUtil.getString(Conast.USER_MONEY))){
			tv_money.setText("余额￥" + SharePrefUtil.getString(Conast.USER_MONEY) + "元");
		} else {
			tv_money.setText("余额￥0.00元");
		}
		
		person_info.setOnClickListener(this);
		about_us.setOnClickListener(this);
		finance_details.setOnClickListener(this);
		service_evaluate.setOnClickListener(this);
		LinearLayout apply_set = (LinearLayout) view.findViewById(R.id.apply_set);
		user_service_set.setOnClickListener(this);
		apply_set.setOnClickListener(this);
		invate_hint.setOnClickListener(this);
		
		LinearLayout ll_message_center = (LinearLayout) view.findViewById(R.id.ll_message_center);
		ll_message_center.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.Doctor_Type)) && "1".equals(SharePrefUtil.getString(Conast.Doctor_Type))) {
			doctor_type.setText(getResources().getString(R.string.doctor_type_quanke));
		} else {
			doctor_type.setText(getResources().getString(R.string.doctor_type_zhuanke));
		}
		
		if(!TextUtils.isEmpty(SharePrefUtil.getString(Conast.USER_MONEY))){
			tv_money.setText("余额￥" + SharePrefUtil.getString(Conast.USER_MONEY) + "元");
		} else {
			tv_money.setText("余额￥0.00元");
		}
	}

	@Override
	public void onPause() {
		super.onStop();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {

		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onClick(View v) {
		try {
			int id = v.getId();

			if (id == R.id.person_info) {//第一次点“个人资料”，都应该是看到审核结果界面；除了资料审核成功，先看到审核成功界面，第二次再点“个人资料”后，就展现个人资料的展现页
				if("".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS)) || "0".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
					startActivity(new Intent(activity, LogonResultActivity.class));
				}else{
					startActivity(new Intent(activity, PersonInfoActivity.class));
				}
			} else if (id == R.id.about_us) {
				startActivity(new Intent(activity, AboutUsActivity.class));
			} else if (id == R.id.finance_details) {
				Intent intent = new Intent(activity, FinaceDetails2Activity.class);
				startActivity(intent);
			} else if (id == R.id.apply_set) {
				startActivity(new Intent(activity, SettingAppActivity.class));
			} else if (id == R.id.service_evaluate) {
				startActivity(new Intent(activity, ServiceEvaluationActivity.class));
			} else if (id == R.id.user_service_set) {
				startActivity(new Intent(activity, ServiceAllSettingActivity.class));
			} else if (id == R.id.invate_hint) {
				startActivity(new Intent(activity, InviteActivity.class));
			} else if (id == R.id.ll_message_center){
				startActivity(new Intent(activity, MessageCenterActivity.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
