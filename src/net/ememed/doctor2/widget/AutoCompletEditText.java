package net.ememed.doctor2.widget;

import net.ememed.doctor2.R;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 
 * @author FXX 输入弹出提示
 * 
 */
public class AutoCompletEditText extends LinearLayout implements
		OnClickListener {

	private Context context;
	private View mainView;
	private EditText autoEdit;
	private PopupWindow p_window;

	private ImageView delete;
	private ImageView search;

	private int[] location = new int[2];
	String[] hisArrays;

	private ListView auto_list;
	private MyAdapter adapter;

	private AutoCompletListener completListener;

	public AutoCompletEditText(Context context) {
		super(context);
		this.context = context;
		mainView = LayoutInflater.from(context).inflate(
				R.layout.patient_grouping_search, this);
		initView();
	}

	public AutoCompletEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		mainView = LayoutInflater.from(context).inflate(
				R.layout.patient_grouping_search, this);
		initView();
	}

	public void initView() {
		autoEdit = (EditText) mainView.findViewById(R.id.et_disease_keyword);
		delete = (ImageView) mainView.findViewById(R.id.delete);
		search = (ImageView) mainView.findViewById(R.id.bt_keyword_search);
		autoEdit.setOnClickListener(this);
		delete.setOnClickListener(this);
		search.setOnClickListener(this);
		setListener();
		initPopupWindow();
	}

	private void setListener() {
		autoEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				delete.setVisibility(arg0.length() > 0 ? View.VISIBLE
						: View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.et_disease_keyword:
			this.getLocationInWindow(location);
			adapter.setData(getHisArrays());
			p_window.showAtLocation(this, Gravity.NO_GRAVITY, location[0],
					location[1] + this.getHeight());
			break;
		case R.id.delete:
			autoEdit.setText("");
			break;
		case R.id.bt_keyword_search:// 保存 搜索记录 并且搜索
			saveHistory();
			if (completListener != null) {
				completListener.onSearch();
			}
			break;
		case R.id.last_hint:// 清除历史记录
			cleanHistory();
			p_window.dismiss();
			break;
		default:
			break;
		}
	}

	TextView first_hint;

	public void initPopupWindow() {
		View autocomplete_item = LayoutInflater.from(context).inflate(
				R.layout.patient_grouping_search_item, null);
		p_window = new PopupWindow(autocomplete_item,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		auto_list = (ListView) autocomplete_item.findViewById(R.id.auto_list);
		first_hint = (TextView) autocomplete_item.findViewById(R.id.first_hint);
		LinearLayout last_hint = (LinearLayout) autocomplete_item
				.findViewById(R.id.last_hint);
		last_hint.setOnClickListener(this);
		hisArrays = getHisArrays();
		adapter = new MyAdapter(hisArrays);
		auto_list.setAdapter(adapter);
		p_window.setContentView(autocomplete_item);

		p_window.setFocusable(false);
		p_window.setOutsideTouchable(true);
		p_window.setBackgroundDrawable(new BitmapDrawable());
	}

	public String[] getHisArrays() {
		String longhistory = SharePrefUtil.getString("search_key");
		if (longhistory.equals("")) {
			return null;
		}
		String[] hisArrays = longhistory.split(",");
		return hisArrays;
	}

	class MyAdapter extends BaseAdapter {
		String[] hisArrays;

		public MyAdapter(String[] hisArrays) {
			this.hisArrays = hisArrays;
		}

		public int getCount() {
			if (hisArrays == null)
				return 0;
			return hisArrays.length;
		}

		public String getItem(int arg0) {
			return hisArrays[arg0];
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			View view = LayoutInflater.from(context).inflate(R.layout.hint,
					null);
			TextView textView = (TextView) view.findViewById(R.id.item_tx);
			textView.setText(hisArrays[arg0]);
			if (getCount() == 0) {
				first_hint.setVisibility(View.GONE);
			}
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					p_window.dismiss();
					autoEdit.setText(hisArrays[arg0]);
					autoEdit.setSelection(hisArrays[arg0].length());
				}
			});
			return view;
		}

		public void setData(String[] ars) {
			this.hisArrays = ars;
			notifyDataSetChanged();
		}

	}

	private void saveHistory() {
		String text = autoEdit.getText().toString();

		String cacheText = SharePrefUtil.getString("search_key");
		
		if(!text.equals("")){
			String str=text;
			if(cacheText.equals("")){
				SharePrefUtil.putString("search_key", text);
			}else{
				String[] cacheArray=cacheText.split(",");
				for (int i = 0; i < cacheArray.length; i++) {
					if(i<=5 && !cacheArray[i].equals(text) && !cacheArray[i].equals("")){
						str+=","+cacheArray[i];
					}
				}
				SharePrefUtil.putString("search_key",str);
			}
			SharePrefUtil.commit();
		}

	}

	public void setOnAutoCompletListener(AutoCompletListener completListener) {
		this.completListener = completListener;
	}

	public void cleanHistory() {
		SharePrefUtil.putString("search_key", "");
		SharePrefUtil.commit();
	}

	public void disAutoComplet() {
		p_window.dismiss();
	}

	public String getText() {

		String text = autoEdit.getText().toString().trim();
		return text;
	}

	public void setText(String text) {
		autoEdit.setText(text);
	}

	public EditText getEditText() {
		return autoEdit;
	}

}
