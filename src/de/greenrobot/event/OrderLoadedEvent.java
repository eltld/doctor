package de.greenrobot.event;

import java.util.List;

import net.ememed.doctor2.entity.MemberOrderEntry;
import net.ememed.doctor2.entity.OrderListEntry;


/**
 * Created by chen
 */
public class OrderLoadedEvent {
	/**是否有正在服务中的订单*/
	private boolean hasOpenOrder;
	private  List<MemberOrderEntry> orderLists;
	
	public OrderLoadedEvent(boolean hasOpenOrder,List<MemberOrderEntry> orderLists) {
		this.setOrderLists(orderLists);
		this.setHasOpenOrder(hasOpenOrder);
    }
	public boolean hasOpenOrder() {
		return hasOpenOrder;
	}
	public void setHasOpenOrder(boolean hasOpenOrder) {
		this.hasOpenOrder = hasOpenOrder;
	}
	public List<MemberOrderEntry> getOrderLists() {
		return orderLists;
	}
	public void setOrderLists(List<MemberOrderEntry> orderLists) {
		this.orderLists = orderLists;
	}
}
