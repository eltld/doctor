package net.ememed.doctor2.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.EditMassMessageActivity;
import net.ememed.doctor2.activity.MassMessageActivity;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.ContactInfo;
import net.ememed.doctor2.fragment.adapter.ContactAdapter;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * 
 * @author fxx 所有联系人
 * 
 */

public class AllContactFragment extends Fragment implements OnClickListener{

	private View rootView;
	private RefreshListView lv_contact_all;
	private ContactAdapter contactAdapter;
	private MassMessageActivity activity;
	private LinearLayout ll_net_unavailable;
	private LinearLayout ll_empty;
	private int page;
	private boolean isloading = true;
	public CheckBox cb_choice_all;
	public Button btn_next;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.layout_mass_all, null);
		initView();
		return rootView;
	}

	public void initView() {
		activity = (MassMessageActivity) getActivity();
		lv_contact_all = (RefreshListView) rootView.findViewById(R.id.lv_contact);
		ll_net_unavailable = (LinearLayout) rootView.findViewById(R.id.ll_net_unavailable);
		ll_net_unavailable.setOnClickListener(this);
		ll_empty = (LinearLayout) rootView.findViewById(R.id.ll_empty);
		cb_choice_all = (CheckBox) rootView.findViewById(R.id.cb_choice_all);
		btn_next = (Button) rootView.findViewById(R.id.btn_next);
		cb_choice_all.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		contactAdapter = new ContactAdapter(null,getActivity(),activity.imageLoader,btn_next);
		lv_contact_all.setAdapter(contactAdapter);
		
	}

	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			if(isloading){
				page = 1;
				getContactList(page);
				isloading = false;
			}
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	public void setCheckList(){
		
		System.out.println("adapter = "+contactAdapter);
		
		if(contactAdapter!=null){
			contactAdapter.setCheckList();
		}
	}
	
	public void resetCheckList(){
		if(contactAdapter!=null){
			contactAdapter.resetCheckList();
		}
	}
	
	private void getContactList(int page) {

		if (NetWorkUtils.detect(activity)) {
			activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", page + "");
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_contact_list, ContactInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							
							ContactInfo info = (ContactInfo) response;
							activity.destroyDialog();
							if (null != info && info.getSuccess() == 1) {
								contactAdapter.addData(info.getData());
							}else{
								ll_empty.setVisibility(View.VISIBLE);
								lv_contact_all.setVisibility(View.GONE);
							}
							
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							
							lv_contact_all.setVisibility(View.GONE);
							ll_empty.setVisibility(View.VISIBLE);
							ll_net_unavailable.setVisibility(View.GONE);
							
						}
					});
		} else {
			lv_contact_all.setVisibility(View.GONE);
			ll_net_unavailable.setVisibility(View.VISIBLE);
			ll_empty.setVisibility(View.GONE);
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_net_unavailable:
			getContactList(1);
			break;
		case R.id.cb_choice_all:
			if(cb_choice_all.isChecked()){
				setCheckList();
				btn_next.setText("下一步("+contactAdapter.getCheckNumber()+")");
			}else{
				resetCheckList();
				btn_next.setText("下一步(0)");
			}
			break;
		case R.id.btn_next:
			if(contactAdapter.getCheckNumber() <= 0){
				activity.showToast("请先选择联系人");
				return;
			}
			Intent intent = new Intent(getActivity(), EditMassMessageActivity.class);
			intent.putExtra("contact_mass_message", (Serializable)getContact());
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	public List<ContactEntry> getContact(){
		List<Boolean> isChecks = contactAdapter.getCheckList();
		List<ContactEntry> data = contactAdapter.getListItems();
		
		List<ContactEntry> msgData = new ArrayList<ContactEntry>();
		for (int i = 0; i < data.size(); i++) {
			if(isChecks.get(i)){
				msgData.add(data.get(i));
			}
		}
		return msgData;
	}

}
