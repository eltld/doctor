package net.ememed.doctor2.fragment.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import net.ememed.doctor2.R;
import net.ememed.doctor2.fragment.MineFragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MineAdapter extends BaseAdapter {
	
	public MineAdapter(MineFragment mineFragment,
			ArrayList<HashMap<String, Object>> data) {
		if(data != null){
			data = new ArrayList<HashMap<String, Object>>();
		}
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
