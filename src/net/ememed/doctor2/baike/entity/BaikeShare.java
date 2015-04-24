package net.ememed.doctor2.baike.entity;

import java.util.List;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class BaikeShare {

	@SerializedName("SAYSID")
	private String sayId;
	@SerializedName("TITLE_SHOW")
	private String sayTitle;
	@SerializedName("CONTENT_SHOW")
	private String sayContent;
	@SerializedName("CREATE_TIME_SAYS")
	private String sayTime;
	@SerializedName("REALNAME")
	private String sayName;
	@SerializedName("AVATAR")
	private String sayPortrait;
	@SerializedName("PICS")
	private List<String> sayImageUrls;
	@SerializedName("PICS_THUMB")
	private List<String> sayThumbImageUrls;
	@SerializedName("DOCTORID")
	private String authorId;

	@SerializedName("HITS")
	private String hits;// 浏览量
	@SerializedName("PRAISE_NUM")
	private String praiseCount;// 点赞数
	@SerializedName("COMMENT_NUM")
	private String commentCount;// 评论数
	@SerializedName("SHARE_COUNT")
	private String shareCount;// 分享数

	@SerializedName("COMMENTID")
	private String commentId;
	@SerializedName("CONTENT_COMMENT")
	private String commentContent;// 评论内容
	@SerializedName("CREATE_TIME")
	private String commentTime;// 评论时间

	// 仅回复时存在
	@SerializedName("REFER_REALNAME")
	private String referName;
	@SerializedName("REFER_MEMBERID")
	private String referMemberId;
	@SerializedName("REFER_COMMENT")
	private String referComment;
	@SerializedName("REFER_COMMENTID")
	private String referCommentId;

	public String getAuthorId() {
		return authorId;
	}

	public String getSayId() {
		return sayId;
	}

	public String getSayTitle() {
		return sayTitle;
	}

	public String getSayContent() {
		return sayContent;
	}

	public String getSayTime() {
		return sayTime;
	}

	public String getSayName() {
		return sayName;
	}

	public String getSayPortrait() {
		return sayPortrait;
	}

	public List<String> getSayImageUrls() {
		return sayImageUrls;
	}

	public List<String> getSayThumbImageUrls() {
		return sayThumbImageUrls;
	}

	public String getHits() {
		return hits;
	}

	public String getPraiseCount() {
		return praiseCount;
	}

	public String getCommentCount() {
		return commentCount;
	}

	public String getShareCount() {
		return shareCount;
	}

	public String getCommentId() {
		return commentId;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public String getCommentTime() {
		return commentTime;
	}

	public String getReferName() {
		return referName == null ? "" : String.format("@%s：", referName);
	}

	public String getReferName2() {
		return referName == null ? "" : String.format("回复@%s：", referName);
	}

	public boolean existReferName() {
		return TextUtils.isEmpty(referName) ? false : true;
	}

	public boolean existReferComment() {
		return TextUtils.isEmpty(referComment) ? false : true;
	}

	public String getReferMemberId() {
		return referMemberId;
	}

	public String getReferComment() {
		return referComment;
	}

	public String getReferCommentId() {
		return referCommentId;
	}

}
