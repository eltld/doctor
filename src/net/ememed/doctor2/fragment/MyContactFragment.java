package net.ememed.doctor2.fragment;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.InviteActivity;
import net.ememed.doctor2.activity.MyContactActivity;
import net.ememed.doctor2.widget.RefreshListView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
/**
 * 医生联系人单聊的分页
 * @author huangjk
 *
 */
public class MyContactFragment extends Fragment implements OnClickListener {

	private FrameLayout mContentView;
	private LinearLayout ll_please_reg;
	private LinearLayout ll_find_chat;
	private RefreshListView rlv_my_contact;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.root_layout, null);
		mContentView = (FrameLayout) view.findViewById(R.id.mainView);
		View v = View.inflate(getActivity(), R.layout.fragment_my_contact, null);
		ll_please_reg = (LinearLayout) v.findViewById(R.id.ll_please_reg);
		ll_find_chat = (LinearLayout) v.findViewById(R.id.ll_find_chat);
		rlv_my_contact = (RefreshListView) v.findViewById(R.id.rlv_my_contact);
		ll_please_reg.setOnClickListener(this);
		ll_find_chat.setOnClickListener(this);
		mContentView.addView(v);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_please_reg://跳邀请注册
			Intent i  = new Intent(getActivity(),InviteActivity.class);
			startActivity(i);
			break;
		case R.id.ll_find_chat://跳发起对话
			Intent intent = new Intent(getActivity(), MyContactActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	

}
