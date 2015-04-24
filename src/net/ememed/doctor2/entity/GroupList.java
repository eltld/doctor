package net.ememed.doctor2.entity;

import java.util.List;

import net.ememed.doctor2.util.UserPreferenceWrapper;

import android.text.TextUtils;

import com.easemob.chat.EMConversation;
import com.google.gson.annotations.SerializedName;

public class GroupList extends BaseEntity {

	@SerializedName("data")
	private List<Group> groups;

	public List<Group> getGroups() {
		return groups;
	}

	public boolean isEmpty() {
		return groups == null || groups.size() == 0;
	}

	public static class Group {

		@SerializedName("LOGO")
		private String portrait; // 头像
		@SerializedName("GROUPID")
		private String groupId; // 群id
		@SerializedName("GROUPNAME")
		private String groupName; // 群名称
		@SerializedName("GROUPNUM")
		private String groupNum; // 群号
		@SerializedName("GROUPDESC")
		private String groupDesc; // 群描述
		@SerializedName("OWNERID")
		private String ownerId; // 创建者id
		@SerializedName("OWNERNAME")
		private String ownerName; // 创建者名称
		@SerializedName("TAGS")
		private String tags; // 标签
		@SerializedName("MEMBER_LIMIT")
		private Integer memberLimit; // 人数限制
		@SerializedName("ISPUBLIC")
		private String isPublic; // 是否公开群
		@SerializedName("APPROVAL")
		private String approval; // 加入公开群是否需要批准

		@SerializedName("IM_GROUPID")
		private String easemobGroupId; // 环信群id
		
		private EMConversation emConversation;//环信聊天消息,本地业务

		public String getPortrait() {
			return portrait;
		}

		public String getGroupId() {
			return groupId;
		}

		public String getGroupName() {
			return groupName;
		}

		public String getGroupNum() {
			return groupNum;
		}

		public String getGroupDesc() {
			return groupDesc;
		}

		public String getOwnerId() {
			return ownerId;
		}

		public String getOwnerName() {
			return ownerName;
		}

		public String getTags() {
			return tags;
		}

		public Integer getMemberLimit() {
			return memberLimit;
		}

		public String getIsPublic() {
			return isPublic;
		}

		public String getApproval() {
			return approval;
		}

		public String getEasemobGroupId() {
			return easemobGroupId;
		}

		public boolean isOwner() {
			return !TextUtils.isEmpty(ownerId)
					&& ownerId.equals(UserPreferenceWrapper.getMemberId());
		}

		public EMConversation getEmConversation() {
			return emConversation;
		}

		public void setEmConversation(EMConversation conversation) {
			this.emConversation = conversation;
		}
		
	}

}
