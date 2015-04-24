package net.ememed.doctor2.util;

import java.util.Comparator;

import net.ememed.doctor2.entity.ContactEntry;

public class PinyinComparator implements Comparator{
	
	@Override
	public int compare(Object o1, Object o2) {
		ContactEntry ce1=(ContactEntry) o1;
		ContactEntry ce2=(ContactEntry) o2;
		String star1=ce1.getIS_STAR();
		String star2=ce2.getIS_STAR();
		String name1=ce1.getNOTE_NAME()!=null ?ce1.getNOTE_NAME():ce1.getREALNAME();
		String name2=ce2.getNOTE_NAME()!=null ?ce2.getNOTE_NAME():ce2.getREALNAME();
		if(ce1.getIS_STAR()==null){
			star1="0";
		}
		if(ce2.getIS_STAR()==null){
			star2="0";
		}
		if(Integer.parseInt(star1)<Integer.parseInt(star2)){
			return 1;
		}else if(Integer.parseInt(star1)>Integer.parseInt(star2)){
			return -1;
		}else {
			String str1 = PingYinUtil.getPingYin(name1);
			// System.out.println("+++++++"+str1);
			String str2 = PingYinUtil.getPingYin(name2);
			return str1.compareTo(str2);
		}
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return super.equals(o);
	}

}
