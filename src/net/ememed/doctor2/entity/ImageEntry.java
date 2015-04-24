package net.ememed.doctor2.entity;

import com.nostra13.universalimageloader.core.assist.ImageSize;


public class ImageEntry {

	
	private String type;
	private String url;
	private String filename;
	private String thumb;
	private String secret;
	private String thumb_secret;
	private IamgeSize size;
	private int length;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getThumb_secret() {
		return thumb_secret;
	}
	public void setThumb_secret(String thumb_secret) {
		this.thumb_secret = thumb_secret;
	}
	public IamgeSize getSize() {
		return size;
	}
	public void setSize(IamgeSize size) {
		this.size = size;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	
}
