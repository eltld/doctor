package net.ememed.doctor2.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.PatientEditGroupActivity;
import net.ememed.doctor2.entity.PatientGroupDataBean;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class PatientEditGroupAdapter extends BaseAdapter{
	private Context context;
	List<PatientGroupDataBean> list_group;
	boolean isedit;
	
	public PatientEditGroupAdapter(Context context) {
		super();
		// TODO Auto-generated constructor stub
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(list_group==null){
			return 0;
		}
		return list_group.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list_group.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final int listIndex=arg0;
		View ll_edit_list=View.inflate(context,R.layout.patient_edit_group_item, null);
		TextView group_txt=(TextView) ll_edit_list.findViewById(R.id.patient_edit_txt);
		group_txt.setText(list_group.get(arg0).getGROUPNAME());
		Button delete_image=(Button) ll_edit_list.findViewById(R.id.patient_edit_chooes);
		if(!isedit){
			delete_image.setVisibility(View.VISIBLE);
		}else{
			delete_image.setVisibility(View.GONE);
		}
		final Button delete_btn=(Button) ll_edit_list.findViewById(R.id.patient_edit_delete);
		delete_image.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				delete_btn.setVisibility(8-delete_btn.getVisibility());
			}
		});
		delete_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				getEditGroup(list_group.get(listIndex).getGROUPID());
			}
		});
		return ll_edit_list;
	}
	
	public void setData(List<PatientGroupDataBean> list_group,boolean isedit){
		if(list_group==null){
			list_group=new ArrayList<PatientGroupDataBean>();
		}
		this.list_group=list_group;
		this.isedit=isedit;
	}

}
