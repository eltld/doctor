package net.ememed.doctor2.fragment;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.CommunicationGroupCreateActivity.OnInputCallBack;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class CommunicationGroupCreateNameFragment extends Fragment implements OnInputCallBack {

	private static final String TIPS = "为您的群起一个<font color='#ff5c03'>心仪的名称</font>吧！";

	private TextView tv_name;
	private EditText et_name;
	private TextView tv_count;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_communication_group_create_name, null);
		tv_name = (TextView) rootView.findViewById(R.id.tv_name);
		et_name = (EditText) rootView.findViewById(R.id.et_name);
		tv_count = (TextView) rootView.findViewById(R.id.tv_count);
		tv_name.setText(Html.fromHtml(TIPS));
		et_name.addTextChangedListener(mTextWatcher);
		return rootView;
	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			tv_count.setText(String.valueOf(15 - s.length()));
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	@Override
	public String getInput() {
		return et_name.getText().toString().trim();
	}

	@Override
	public boolean hasInput() {
		String name = et_name.getText().toString().trim();
		return name.length() < 16 && name.length() > 0 ? true : false;
	}

}
