package net.ememed.doctor2.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.CircleImageView;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class PatientTongxunluAdapter extends BaseAdapter implements SectionIndexer{

	private Context mContext;
	private List<ContactEntry> list;
	static int i;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	public String nickName;

	public PatientTongxunluAdapter(Context mContext,ImageLoader imageLoader,DisplayImageOptions options) {
		this.mContext = mContext;
		this.imageLoader=imageLoader;
		this.options=options;
		
	}

	@Override
	public int getCount() {
		if(list==null){
			return 0;
		}
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("11111111111111111" + list);
		nickName = list.get(position).getNOTE_NAME()!=null?list.get(position).getNOTE_NAME():list.get(position).getREALNAME();
		if(list.get(position).getIS_STAR()!=null && list.get(position).getIS_STAR().equals("1")){
			nickName="★";
		}
//		System.out.println("nickName----"+nickName);
		i = position;

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.patient_tongxunlu_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvCatalog = (TextView) convertView
					.findViewById(R.id.patient_tongxunlu_catalog);
			viewHolder.tvNick = (TextView) convertView
					.findViewById(R.id.patient_tongxunlu_nick);
			viewHolder.ivAvatar=(CircleImageView) convertView.findViewById(R.id.patient_tongxunlu_head_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String catalog = converterToFirstSpell(nickName).substring(0, 1);
		viewHolder.tvCatalog.setText(catalog);
		if (position == 0) {
			viewHolder.tvCatalog.setVisibility(View.VISIBLE);
			viewHolder.tvCatalog.setText(catalog);
		} else {
			String lastCatalog = converterToFirstSpell(list.get(position-1).getREALNAME())
					.substring(0, 1);
			if(list.get(position-1).getIS_STAR()!=null && list.get(position-1).getIS_STAR().equals("1")){
				lastCatalog="★";
			}
			if (catalog.equals(lastCatalog)) {
				viewHolder.tvCatalog.setVisibility(View.GONE);
			} else {
				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
				viewHolder.tvCatalog.setText(catalog);
			}
		}
		
		imageLoader.displayImage(list.get(position).getAVATAR(), viewHolder.ivAvatar, options);
		
		viewHolder.tvNick.setText(list.get(position).getNOTE_NAME()!=null?list.get(position).getNOTE_NAME():list.get(position).getREALNAME());
//		System.out.println("nnn"+viewHolder.tvCatalog.getText());
		
		return convertView;
	}

	static class ViewHolder {
		TextView tvCatalog;// 字母头
		CircleImageView ivAvatar;//头像
		TextView tvNick;// 名字
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < list.size(); i++) {
			String l = converterToFirstSpell(list.get(i).getREALNAME()).substring(0, 1);
			if(list.get(i).getIS_STAR()!=null && list.get(i).getIS_STAR().equals("1")){
				l="★";
			}
			char firstChar = l.toUpperCase().charAt(0);
			
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		if(chines==null || chines.substring(0, 1).equals(" ")){
			chines="#";
		}if(chines.equals("★")){
			
		}
		
		
		
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] >= 19968 && nameChar[i] <= 40869 ) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				if(nameChar[i]>='a' && nameChar[i]<='z'){
					nameChar[i]=(char) (nameChar[i]-32);
				}
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}
	
	public void setData(List<ContactEntry> list){
		if(null==list){
			list=new ArrayList<ContactEntry>();
		}
		this.list=list;
		notifyDataSetChanged();
	}
}
