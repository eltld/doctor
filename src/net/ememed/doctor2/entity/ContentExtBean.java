package net.ememed.doctor2.entity;

import java.io.Serializable;

public class ContentExtBean implements Serializable{
	private String secret;
	private ContentExtSizeBean size;
    private String thumb;
    private String filename;
    private String type;
    private String file_length;
    private String url;
	public String getSecret() {
		return secret;
	}
	public ContentExtSizeBean getSize() {
		return size;
	}
	public String getThumb() {
		return thumb;
	}
	public String getFilename() {
		return filename;
	}
	public String getType() {
		return type;
	}
	public String getFile_length() {
		return file_length;
	}
	public String getUrl() {
		return url;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public void setSize(ContentExtSizeBean size) {
		this.size = size;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setFile_length(String file_length) {
		this.file_length = file_length;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
