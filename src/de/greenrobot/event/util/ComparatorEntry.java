package de.greenrobot.event.util;

import java.util.Comparator;

import net.ememed.doctor2.entity.ChatHistoryEntry;

import com.easemob.chat.EMMessage;

public class ComparatorEntry implements Comparator<EMMessage> {
	@Override
	public int compare(EMMessage lhs, EMMessage rhs) {
		long lhsTime = lhs.getMsgTime();
		long rhsTime = rhs.getMsgTime();
		if((lhsTime - rhsTime)>=1){
			return  1;
		}else{
			return  -1;
		}
	}

}
