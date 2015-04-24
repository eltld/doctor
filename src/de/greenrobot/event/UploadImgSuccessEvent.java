package de.greenrobot.event;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.UploadPaperActivity.PhotoType;

/**
 * Created by kevintanhongann on 11/27/13.
 */
public class UploadImgSuccessEvent {

	private byte[]  bytes;
	private PhotoType photoType;
    public UploadImgSuccessEvent(byte[]  bytes,PhotoType photoType) {
    	this.setBytes(bytes);
    	this.setPhotoType(photoType);
    }

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public PhotoType getPhotoType() {
		return photoType;
	}

	public void setPhotoType(PhotoType photoType) {
		this.photoType = photoType;
	}
    
}
