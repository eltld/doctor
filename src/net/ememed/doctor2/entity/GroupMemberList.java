package net.ememed.doctor2.entity;

import java.util.List;

public class GroupMemberList {
	private List<GroupMemberEntity> owner;
	private List<GroupMemberEntity> members;
	
	public List<GroupMemberEntity> getOwner() {
		return owner;
	}
	public void setOwner(List<GroupMemberEntity> owner) {
		this.owner = owner;
	}
	public List<GroupMemberEntity> getMembers() {
		return members;
	}
	public void setMembers(List<GroupMemberEntity> members) {
		this.members = members;
	}
	
	
}
