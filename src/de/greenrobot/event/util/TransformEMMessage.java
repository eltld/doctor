package de.greenrobot.event.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import net.ememed.doctor2.entity.ChatHistoryEntry;
import net.ememed.doctor2.entity.IMEntry;
import net.ememed.doctor2.entity.ImInfo;
import net.ememed.doctor2.interfac.LoadInterface;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import android.os.Environment;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;

import de.greenrobot.event.util.Download.OnDownloadListener;


public class TransformEMMessage {

	
	public static TransformEMMessage emMessage;
	
	public static TransformEMMessage getInstance(){

		if(emMessage==null){
			emMessage = new TransformEMMessage();
		}
		return emMessage;
	}
	/**
	 * 
	 * @param list 将从后台获取的数据直接 转换成 环信 EMMessage 对象
	 * @return
	 */
	
	Download download = null;
	AudioDownload a_download = null;
	
	
	LoadInterface loadInterface;
	
	public void setLoadInterface(LoadInterface loadInterface){
		this.loadInterface = loadInterface;
	}
	
	public List<EMMessage> transformEM(
			ImInfo imInfo,String id,String name,String orderid){
		
		EMConversation conversation
		= EMChatManager.getInstance().getConversation(
				name);
		//去重 方法
		List<EMMessage> databaseIMdata = getDisplayData(conversation, orderid);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < databaseIMdata.size(); i++) {
			if(i==(databaseIMdata.size()-1)){
				sb.append(databaseIMdata.get(i).getMsgId());
			}else{
				sb.append(databaseIMdata.get(i).getMsgId()).append(",");
			}
		}
		IMEntry iMEntry = imInfo.getData();
		if(iMEntry != null){
			List<ChatHistoryEntry> list = iMEntry.getList();
			for (int i = 0; i < list.size(); i++) {
				if(!sb.toString().contains(list.get(i).getMSG_ID())){
					ChatHistoryEntry entry = list.get(i);
					EMMessage message = null;
					if(entry.getTYPE().equals("txt")){
						message = EMMessage.createSendMessage(EMMessage.Type.TXT);
						message.setReceipt(entry.getRECEIVER());
						message.setMsgTime(Long.parseLong(entry.getSENDTIME().trim()));
						
						if(entry.getEXT()==null){
							continue;
						}else{
							message.setAttribute("ext", entry.getEXT().get(0).getExtToString());
						}
						message.setMsgId(entry.getMSG_ID());
						if(id.equals(entry.getRECEIVER().trim())){
							message.direct = message.direct.RECEIVE;
						}else{
							message.direct  = message.direct.SEND;
						}
						TextMessageBody body = new TextMessageBody(entry.getCONTENT());
						message.status = EMMessage.Status.SUCCESS;
						message.addBody(body);
						databaseIMdata.add(message);
					}if(entry.getTYPE().equals("img")){
						
						message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
						message.setMsgTime(Long.parseLong(entry.getSENDTIME()));
						final ImageMessageBody imageBody = new ImageMessageBody();
						message.setFrom(SharePrefUtil.getString(Conast.Doctor_ID));
						message.setAttribute("ext", entry.getEXT().get(0).getExtToString());
						message.setMsgId(entry.getMSG_ID());
						imageBody.setRemoteUrl(entry.getCONTENT_EXT().get(0).getUrl());
						imageBody.setSecret(entry.getCONTENT_EXT().get(0).getSecret());
						imageBody.setThumbnailUrl(entry.getCONTENT_EXT().get(0).getUrl());
						
						message.setReceipt(entry.getRECEIVER());
						message.status = EMMessage.Status.SUCCESS;
						download = new Download(entry.getDOCTORID());
						download.setParameters( new OnDownloadListener() {
							@Override
							public void onSuccess(int downloadId, String fileName) {
								imageBody.setLocalUrl(fileName);
								if(loadInterface!=null){
									loadInterface.loadSuccess();
								}
							}
							@Override
							public void onStart(int downloadId, long fileSize) {
							}
							@Override
							public void onPublish(int downloadId, long size) {
							}
							@Override
							public void onPause(int downloadId) {
							}
							@Override
							public void onGoon(int downloadId, long localSize) {
							}
							@Override
							public void onError(int downloadId) {
							}
							@Override
							public void onCancel(int downloadId) {
							}
						});
						download.start(false,entry, id);
						
						
						if(id.equals(entry.getRECEIVER().trim())){
							message.direct = message.direct.RECEIVE;
						}else{
							message.direct  = message.direct.SEND;
						}
						message.addBody(imageBody);
						databaseIMdata.add(message);
					} if (entry.getTYPE().equals("audio")){
						String paht = Environment.getExternalStorageDirectory().getAbsolutePath()+
								"/Android/data/net.ememed.doctor2/ememed#ememeduserofficial/97908/voice";
						message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
						message.setMsgTime(Long.parseLong(entry.getSENDTIME()));
						message.setAttribute("ext", entry.getEXT().get(0).getExtToString());
						message.setMsgId(entry.getMSG_ID());
						int Length = entry.getCONTENT_EXT().get(0).getLength();
						final VoiceMessageBody voiceMessageBody ;
						message.status = EMMessage.Status.SUCCESS;
						
						if(id.equals(entry.getRECEIVER().trim())){
							message.direct = message.direct.RECEIVE;
							String pathName = entry.getCONTENT_EXT().get(0).getUrl();
							pathName = pathName.substring(pathName.lastIndexOf("/"), pathName.length());
							voiceMessageBody = new VoiceMessageBody(new File(paht+pathName),Length);
						}else{
							message.direct  = message.direct.SEND;
							voiceMessageBody = new VoiceMessageBody(new File(paht+File.separator+entry.getCONTENT_EXT().get(0).getFilename()),Length);
						}
						a_download=  new AudioDownload();
						a_download.setOnDownloadListener(new de.greenrobot.event.util.AudioDownload.OnDownloadListener() {
							@Override
							public void onSuccess(int downloadId, String fileName) {
								voiceMessageBody.setLocalUrl(fileName);
							}
							
							@Override
							public void onStart(int downloadId, long fileSize) {
								
							}
							
							@Override
							public void onPublish(int downloadId, long size) {
								
							}
							
							@Override
							public void onPause(int downloadId) {
								
							}
							
							@Override
							public void onGoon(int downloadId, long localSize) {
								
							}
							
							@Override
							public void onError(int downloadId) {
							}
							
							@Override
							public void onCancel(int downloadId) {
							}
						});
						a_download.start(false,entry, id);
						voiceMessageBody.setSecret(entry.getCONTENT_EXT().get(0).getSecret());
						voiceMessageBody.setRemoteUrl(entry.getCONTENT_EXT().get(0).getUrl());
						voiceMessageBody.setFileName(entry.getCONTENT_EXT().get(0).getFilename());
						message.addBody(voiceMessageBody);
						databaseIMdata.add(message);
					}
				}
			}
		}
		
		
		Collections.sort(databaseIMdata,new ComparatorEntry());
		
		return databaseIMdata;
	}
	
	
	
	public List<EMMessage> addEMMessageList(
			ImInfo imInfo,String id){
		
		
		List<EMMessage> databaseIMdata = new ArrayList<EMMessage>();
		
		IMEntry iMEntry = imInfo.getData();
		if(iMEntry != null){
			List<ChatHistoryEntry> list = iMEntry.getList();
			for (int i = 0; i < list.size(); i++) {
					ChatHistoryEntry entry = list.get(i);
					EMMessage message = null;
					if(entry.getTYPE().equals("txt")){
						message = EMMessage.createSendMessage(EMMessage.Type.TXT);
						message.setReceipt(entry.getRECEIVER());
						message.setMsgTime(Long.parseLong(entry.getSENDTIME()));
						message.setAttribute("ext", entry.getEXT().get(0).getExtToString());
						message.setMsgId(entry.getMSG_ID());
						if(id.equals(entry.getRECEIVER().trim())){
							message.direct = message.direct.RECEIVE;
						}else{
							message.direct  = message.direct.SEND;
						}
						TextMessageBody body = new TextMessageBody(entry.getCONTENT());
						message.status = EMMessage.Status.SUCCESS;
						message.addBody(body);
						databaseIMdata.add(message);
					}if(entry.getTYPE().equals("img")){
						
						message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
						message.setMsgTime(Long.parseLong(entry.getSENDTIME()));
						final ImageMessageBody imageBody = new ImageMessageBody();
						message.setFrom(SharePrefUtil.getString(Conast.Doctor_ID));
						message.setAttribute("ext", entry.getEXT().get(0).getExtToString());
						message.setMsgId(entry.getMSG_ID());
						imageBody.setRemoteUrl(entry.getCONTENT_EXT().get(0).getUrl());
						imageBody.setSecret(entry.getCONTENT_EXT().get(0).getSecret());
						imageBody.setThumbnailUrl(entry.getCONTENT_EXT().get(0).getThumb());
						
						message.setReceipt(entry.getRECEIVER());
						message.status = EMMessage.Status.SUCCESS;
						download = new Download(entry.getDOCTORID());
						download.setParameters( new OnDownloadListener() {
							@Override
							public void onSuccess(int downloadId, String fileName) {
								imageBody.setLocalUrl(fileName);
								if(loadInterface!=null){
									loadInterface.loadSuccess();
								}
							}
							@Override
							public void onStart(int downloadId, long fileSize) {
							}
							@Override
							public void onPublish(int downloadId, long size) {
							}
							@Override
							public void onPause(int downloadId) {
							}
							@Override
							public void onGoon(int downloadId, long localSize) {
							}
							@Override
							public void onError(int downloadId) {
							}
							@Override
							public void onCancel(int downloadId) {
							}
						});
						download.start(false,entry, id);
						if(id.equals(entry.getRECEIVER().trim())){
							message.direct = message.direct.RECEIVE;
						}else{
							message.direct  = message.direct.SEND;
						}
						message.addBody(imageBody);
						databaseIMdata.add(message);
					} if (entry.getTYPE().equals("audio")){
						String paht = Environment.getExternalStorageDirectory().getAbsolutePath()+
								"/Android/data/net.ememed.doctor2/ememed#ememeduserofficial/97908/voice";
						message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
						message.setMsgTime(Long.parseLong(entry.getSENDTIME()));
						message.setAttribute("ext", entry.getEXT().get(0).getExtToString());
						message.setMsgId(entry.getMSG_ID());
						int Length = entry.getCONTENT_EXT().get(0).getLength();
						final VoiceMessageBody voiceMessageBody ;
						message.status = EMMessage.Status.SUCCESS;
						
						if(id.equals(entry.getRECEIVER().trim())){
							message.direct = message.direct.RECEIVE;
							String pathName = entry.getCONTENT_EXT().get(0).getUrl();
							pathName = pathName.substring(pathName.lastIndexOf("/"), pathName.length());
							voiceMessageBody = new VoiceMessageBody(new File(paht+pathName),Length);
						}else{
							message.direct  = message.direct.SEND;
							voiceMessageBody = new VoiceMessageBody(new File(paht+File.separator+entry.getCONTENT_EXT().get(0).getFilename()),Length);
						}
						a_download=  new AudioDownload();
						a_download.setOnDownloadListener(new de.greenrobot.event.util.AudioDownload.OnDownloadListener() {
							@Override
							public void onSuccess(int downloadId, String fileName) {
								voiceMessageBody.setLocalUrl(fileName);
							}
							
							@Override
							public void onStart(int downloadId, long fileSize) {
								
							}
							
							@Override
							public void onPublish(int downloadId, long size) {
								
							}
							
							@Override
							public void onPause(int downloadId) {
								
							}
							
							@Override
							public void onGoon(int downloadId, long localSize) {
								
							}
							
							@Override
							public void onError(int downloadId) {
							}
							
							@Override
							public void onCancel(int downloadId) {
							}
						});
						a_download.start(false,entry, id);
						
						voiceMessageBody.setSecret(entry.getCONTENT_EXT().get(0).getSecret());
						voiceMessageBody.setRemoteUrl(entry.getCONTENT_EXT().get(0).getUrl());
						voiceMessageBody.setFileName(entry.getCONTENT_EXT().get(0).getFilename());
						message.addBody(voiceMessageBody);
						databaseIMdata.add(message);
					}
				}
		}
		
		return databaseIMdata;
	}
	
	
	
	public List<EMMessage> getDisplayData(EMConversation conversation,String orderid) {

		List<EMMessage> data = conversation.getAllMessages();
		
		System.out.println("环信内存消息数量："+data.size());
		
		List<EMMessage> newData = new ArrayList<EMMessage>();

		for (int i = 0; i < data.size(); i++) {
			String ext;
			try {
				ext = data.get(i).getStringAttribute("ext");
				JSONObject jo = new JSONObject(ext);
				String oidorderod = jo.getString("ORDERID");
				if (oidorderod.equals(orderid)) {
					newData.add(data.get(i));
				}
			} catch (EaseMobException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return newData;
	}
	
}
