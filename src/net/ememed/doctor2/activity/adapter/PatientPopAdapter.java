package net.ememed.doctor2.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.PatientGroupDataBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PatientPopAdapter extends BaseAdapter{
	private List<PatientGroupDataBean> list;
	private LayoutInflater view;
	public PatientPopAdapter(Context context) {
		super();
		// TODO Auto-generated constructor stub
		view=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(list==null){
			return 0;
		}
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(arg1==null){
			holder=new ViewHolder();
			arg1=view.inflate(R.layout.popupwindow_item, null);
			holder.popitem=(TextView) arg1.findViewById(R.id.popup_patient_group_list);
			holder.popitem.setText(list.get(arg0).getGROUPNAME());
			holder.popcount=(TextView) arg1.findViewById(R.id.popup_patient_group_count);
			if(list.get(arg0).getPATIENTS()!=null){
				holder.popcount.setVisibility(View.VISIBLE);
				holder.popcount.setText("["+list.get(arg0).getPATIENTS().size()+"]");
			}
			arg1.setTag(holder);
		}
		holder=(ViewHolder) arg1.getTag();
		
		return arg1;
	}
	
	public void setData(List<PatientGroupDataBean> list){
		if(list==null){
			list=new ArrayList<PatientGroupDataBean>();
		}
		this.list=list;
	}
	
	static class ViewHolder{
		private TextView popitem;
		private TextView popcount;
	}
}
