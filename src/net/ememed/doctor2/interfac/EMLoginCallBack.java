package net.ememed.doctor2.interfac;

public interface EMLoginCallBack {
	
	public void onSuccess();
	public void onProgress(int progress,String str);
	public void onError(int age1,String age2);
}
