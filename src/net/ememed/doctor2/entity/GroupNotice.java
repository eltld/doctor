package net.ememed.doctor2.entity;

import com.google.gson.annotations.SerializedName;

public class GroupNotice extends BaseEntity {

	@SerializedName("data")
	private Notice notice;

	public Notice getNotice() {
		return notice;
	}

	public static class Notice {

		@SerializedName("createtime")
		private String time;
		@SerializedName("msg_content")
		private String message;
		@SerializedName("no_read_count")
		private String count;

		public String getTime() {
			return time;
		}

		public String getMessage() {
			return message;
		}

		public String getCount() {
			return count;
		}

	}

}
