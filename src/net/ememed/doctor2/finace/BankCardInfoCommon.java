package net.ememed.doctor2.finace;

import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;
import net.ememed.doctor2.entity.BankCardInfo;

public class BankCardInfoCommon {

	private List<BankCardInfo> bankcardList = null;
	private List<BankCardInfo> zhifubaoList = null;
	private double total_money;

	/**
	 * 单例对象实例
	 */
	private static BankCardInfoCommon instance = null;

	public synchronized static BankCardInfoCommon getInstance() {
		if (instance == null) {
			instance = new BankCardInfoCommon();
		}
		return instance;
	}

	synchronized void addBankcard(BankCardInfo cardInfo) {
		if (null == bankcardList)
			bankcardList = new ArrayList<BankCardInfo>();
		bankcardList.add(cardInfo);
	}

	synchronized void deleteBankcard(String mbcid) {
		for (int i = 0; i < bankcardList.size(); i++) {
			if (bankcardList.get(i).getMBCID().equals(mbcid)) {
				bankcardList.remove(i);
				break;
			}
		}
	}

	synchronized void deleteZhifubao(String mbcid) {
		for (int i = 0; i < zhifubaoList.size(); i++) {
			if (zhifubaoList.get(i).getMBCID().equals(mbcid)) {
				zhifubaoList.remove(i);
				break;
			}
		}
	}

	synchronized void addZhifubao(BankCardInfo cardInfo) {
		if (null == zhifubaoList)
			zhifubaoList = new ArrayList<BankCardInfo>();
		zhifubaoList.add(cardInfo);
	}

	public List<BankCardInfo> getBankcardList() {
		return bankcardList;
	}

	public List<BankCardInfo> getZhifubaoList() {
		return zhifubaoList;
	}

	synchronized void setTotalMoney(double money) {
		total_money = money;
	}

	public double getTotal_money() {
		return total_money;
	}

	synchronized void saveBankCardInfo(List<BankCardInfo> list) {
		clearbankCardInfo();
		for (BankCardInfo info : list) {
			if (TextUtils.isEmpty(info.getALIPAY_ACCOUNT())) {
				addBankcard(info);
			} else {
				addZhifubao(info);
			}
		}
	}

	public synchronized void clearbankCardInfo() {
		if (null != bankcardList) {
			while (bankcardList.size() > 0) {
				bankcardList.remove(0);
			}
		}

		if (null != zhifubaoList) {
			while (zhifubaoList.size() > 0) {
				zhifubaoList.remove(0);
			}
		}
	}
	
	public synchronized void clearTotalMoney(){
		setTotalMoney(0);
	}
}
