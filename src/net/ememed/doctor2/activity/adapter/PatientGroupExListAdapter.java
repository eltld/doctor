package net.ememed.doctor2.activity.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.PatientGroupDataBean;

import android.content.Context;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PatientGroupExListAdapter extends BaseExpandableListAdapter{
//	private List<String> group;
//	private List<List<String[]>> child;
	private Context context;
	private LinearLayout ll_group;
//	private LinearLayout ll_child;
	private TextView grouptxt;
//	private ImageView avatar;
//	private TextView name;
//	private TextView content;
	private List<PatientGroupDataBean> list;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

//	public PatientGroupExListAdapter(Context context,List<String> group,List<List<String[]>> child) {
//		super();
//		// TODO Auto-generated constructor stub
////		this.group=group;
////		this.child=child;
//		this.context=context;
//	}
	public PatientGroupExListAdapter(Context context,List<PatientGroupDataBean> list,ImageLoader imageLoader,DisplayImageOptions options) {
		super();
		// TODO Auto-generated constructor stub
		this.list=list;
		this.context=context;
		this.imageLoader=imageLoader;
		this.options=options;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
//		return child.get(arg0).get(arg1);
		return list.get(arg0).getPATIENTS().get(arg1).getREALNAME();
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return arg1;
	}


	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
//		if(child.get(arg0)==null){
//			return 0;
//		}
//		return child.get(arg0).size();
		if(list.get(arg0).getPATIENTS()==null){
			return 0;
		}
		return list.get(arg0).getPATIENTS().size();
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
//		return group.get(arg0);
		return list.get(arg0).getGROUPNAME();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		if(list==null){
			return 0;
		}
		return list.size();
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
		// TODO Auto-generated method stub
//		ViewHolder holder=null;
//		if(arg2==null){
//			holder=new ViewHolder();
//			arg2=LayoutInflater.from(context).inflate(R.layout.patient_group_item_group, null);
//			holder.patient_group_txt=(TextView) arg2.findViewById(R.id.patient_group_txt);
//			holder.patient_group_txt.setText(list.get(arg0).getGROUPNAME());
//			arg2.setTag(holder);
//		}
//		holder=(ViewHolder) arg2.getTag();
		ll_group=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.patient_group_item_group, null);
		grouptxt=(TextView) ll_group.findViewById(R.id.patient_group_txt);
		grouptxt.setText(list.get(arg0).getGROUPNAME());
		return ll_group;
	}
	
	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
			ViewGroup arg4) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(arg3==null){
			holder=new ViewHolder();
			arg3=LayoutInflater.from(context).inflate(R.layout.patient_group_item, null);
			holder.patient_group_avatar=(ImageView) arg3.findViewById(R.id.patient_group_avatar);
			this.imageLoader.displayImage(list.get(arg0).getPATIENTS().get(arg1).getAVATAR(), holder.patient_group_avatar,options);
//			holder.avatar_roddot=(ImageView) arg3.findViewById(R.id.patient_group_avatar_roddot);
			holder.patient_group_name=(TextView) arg3.findViewById(R.id.patient_group_name);
			holder.patient_group_name.setText(list.get(arg0).getPATIENTS().get(arg1).getREALNAME());
			holder.patient_group_content=(TextView) arg3.findViewById(R.id.patient_group_content);
			holder.patient_group_content.setText(list.get(arg0).getPATIENTS().get(arg1).getCONTENT());
			arg3.setTag(holder);
		}
		holder=(ViewHolder) arg3.getTag();
//		ll_child=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.patient_group_item, null);
//		avatar=(ImageView) ll_child.findViewById(R.id.patient_group_avatar);
//		imageLoader.displayImage(child.get(arg0).get(arg1)[0], avatar);
//		name=(TextView) ll_child.findViewById(R.id.patient_group_name);
//		name.setText(child.get(arg0).get(arg1)[1]);
//		content=(TextView) ll_child.findViewById(R.id.patient_group_content);
//		content.setText(child.get(arg0).get(arg1)[2]);
		
		return arg3;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}
	
	static class ViewHolder{
		public TextView patient_group_txt; //组名
		public ImageView patient_group_avatar;//头像
		public ImageView avatar_roddot;//红点
		public TextView patient_group_name;//名字
		public TextView patient_group_content;//信息
	}
}
