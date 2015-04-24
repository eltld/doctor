package net.ememed.doctor2.network;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import net.ememed.doctor2.R;
import net.ememed.doctor2.util.ByteUtil;
import net.ememed.doctor2.util.StreamUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import com.google.gson.stream.JsonWriter;

import android.text.TextUtils;

public class HttpUtil {
	public static final int GET = 1;
	public static final int POST = 2;
	// public static final String URI = "http://demo.uimed.net/user/";
//	public static final String URI = "http://183.60.177.178:8002";//正式环境
//	public static final String URI =  "http://183.60.177.178:9004";//测试环境
//	public static final String URI =  "http://www.ememed.net:9004";//测试环境
	public static final String URI =  "http://www.ememed.net:8004";//正式环境
	
	public static final String URI_DOCTOR = URI+"/doctor/";
	public static final String URI_USER = URI+"/user/";
	public static final String URI_COMMON = URI+"/common/";
	public static final String URI_ACCOUNT_DOCTOR = URI+"/account_doctor/";
	public static final String URI_DOCTOR_SETTING = URI+"/setting_doctor/";
	
	public static final String URI_NEWS = "http://www.ememed.net/news/newsDetail/";
	public static final String URI_HTML_SUFFIX = ".html";
	public static final String ADD_NEWS_COMMENT = "/right/common/add_news_comment";
	
	public static final String login = "/normal/doctor/login";
	public static final String logout = "/normal/doctor/logout";
	public static final String new_phone_verify_code = "/normal/common/new_phone_verify_code";
	public static final String register = "/normal/doctor/register";
	public static final String register_detail = "/normal/doctor/register_detail";
	public static final String set_doctor_detail_info = "/right/doctor/set_doctor_detail_info";
	public static final String sync_config = "/normal/doctor/sync_config";
	public static final String get_introduce_list = "/normal/common/get_introduce_list";
	public static final String get_faq_list = "/normal/common/get_faq_list";
	/*个人资料*/
	public static final String get_doctor_info = "/right/doctor/get_doctor_info"; 
	public static final String set_doctor_email = "/right/doctor/set_doctor_email"; 
	public static final String set_doctor_certificatenum = "/right/doctor/set_doctor_certificatenum";
	public static final String set_doctor_hospitalname = "/right/doctor/set_doctor_hospitalname";
	public static final String set_doctor_speciality = "/right/doctor/set_doctor_speciality";
	public static final String set_doctor_resume = "/right/doctor/set_doctor_resume";
	public static final String set_doctor_roomname = "/right/doctor/set_doctor_roomname";
	public static final String set_doctor_avatar = "/normal/doctor/set_doctor_avatar";
	public static final String set_doctor_sex = "/right/doctor/set_doctor_sex";
	public static final String ALERT_MOBILE = "/right/doctor/set_mobile";
	
	public static final String set_doctor_certificate = "/normal/doctor/set_doctor_certificate";
	public static final String set_doctor_allowfreeconsult = "/right/doctor/set_doctor_allowfreeconsult";
	public static final String set_doctor_professional = "/right/doctor/set_doctor_professional";
	
	/**html返回*/
//	public static final String NO_PRESS = "http://183.60.177.178:9004/html/nopress.html";
	public static final String NO_PRESS = URI+"/html/nopress.html";
//	public static final String regClause = "http://www.ememed.net:9004/html/regClause.html";
	public static final String regClause = URI+"/html/regClause.html";
	
	//找回密码
	public static final String get_tel_and_email = "/normal/common/get_tel_and_email";
	public static final String get_validate_code = "/normal/common/get_validate_code";
	public static final String reset_password = "/normal/common/reset_password";
	public static final String check_mobile_code = "/normal/common/check_mobile_code";
	
	/**密码修改*/
	public static final String set_doctor_password = "/right/doctor/set_doctor_password";
	
	public static final String get_version_info = "/normal/common/get_version_info";
	public static final String download_apk_url ="http://www.ememed.net/downloads/android/Ememed.apk";
	
	public static final String get_news_type = "/normal/common/get_news_type";
	public static final String get_news_list = "/normal/common/get_news_list";
	public static final String get_news_home_focus = "/normal/common/get_news_home_focus";
	
	/* 获取其他服务 */
	public static final String get_other_service_list = "/right/doctor/get_third_service_list";
	
	/**财务详情*/
	public static final String get_doctor_account = "/right/doctor/get_doctor_account";
	public static final String get_income_expend_list = "/right/doctor/get_income_expend_list";
	public static final String get_doctor_income_list = "/right/doctor/get_doctor_income_list";
	public static final String get_doctor_expend_list = "/right/doctor/get_doctor_expend_list";
	public static final String get_doctor_freeze_list = "/right/doctor/get_doctor_freeze_list";
	public static final String get_bankcard = "/right/doctor/get_bankcard";
	public static final String get_doctor_blocking = "/right/doctor/get_apply_cash_list";
	public static final String get_doctor_web = "/normal/html5/apply_cash_message";
	public static final String post_doctor_apply = "/right/doctor/apply_cash";
	public static final String post_doctor_income = "/right/doctor/get_doctor_income_list";
	
	public static final String set_bankcard = "/right/doctor/set_bankcard";
	
	public static final String getServiceSet = "/right/service_setting/get_service_setting"; 
	public static final String set_text_consult = "/right/service_setting/set_text_consult";
	public static final String set_phone_consult = "/right/service_setting/set_phone_consult";
	public static final String set_shangmen = "/right/service_setting/set_shangmen";
	public static final String set_jiahao = "/right/service_setting/set_jiahao";
	public static final String set_zhuyuan = "/right/service_setting/set_zhuyuan";
	public static final String set_packet = "/right/service_setting/set_packet";
	public static final String enable_text_consult = "/right/service_setting/enable_text_consult";
	public static final String enable_phone_consult = "/right/service_setting/enable_phone_consult";
	
	public static final String enable_shangmen = "/right/service_setting/enable_shangmen";
	public static final String enable_jiahao = "/right/service_setting/enable_jiahao";
	public static final String enable_zhuyuan = "/right/service_setting/enable_zhuyuan";
	public static final String enable_packet = "/right/service_setting/enable_packet";
	public static final String get_comment_List = "/right/doctor/get_comment_List";
	
	//医生端-聊天订单相关
	public static final String get_contact_list = "/right/doctor/get_contact_list";
	public static final String get_order_list = "/right/doctor/get_order_list";
	public static final String get_order_info = "/right/doctor/get_order_info";
	public static final String get_member_info = "/right/doctor/get_member_info";
	public static final String set_oto_service = "/right/doctor/set_oto_service";
	public static final String set_call_time = "/right/doctor/set_call_time";
	public static final String get_call_time_list = "/right/doctor/get_call_time_list";
	public static final String get_im_list = "/right/doctor/get_im_list";
	public static final String reg_to_im ="/right/common/reg_to_im";
	public static final String get_patients_group_list="/right/doctor/get_patients_group_list";   //获取患者分组列表
	public static final String set_patient_note="/right/doctor/set_patient_note";  //设置患者备注信息
	public static final String set_patient_star="/right/doctor/set_patient_star";  //设置星标
	public static final String set_patients_group_info="/right/doctor/set_patients_group_info"; //编辑分组
	public static final String delete_patients_group="/right/doctor/delete_patients_group";//删除分组
	public static final String set_patient_group="/right/doctor/set_patient_group"; //设置患者分组
	
	public static final String set_feedback = "/right/common/set_feedback"; 
	
	public static final String get_invite_info = "/normal/common/get_invite_info";
	public static final String invite_user = "/right/doctor/invite_user";
	public static final String get_my_friend_list = "/right/im/get_my_friend_list";
	public static final String search_wait_contact = "/right/im/search_wait_contact";
	public static final String get_wait_contact_member_list = "/right/im/get_wait_contact_member_list";
	
	//银行卡（支付宝）解绑
	public static final String unbind_bankcard = "/right/doctor/unblock_bankcard";
	
	//消息中心
	public static final String get_msg_pool_class = "/right/common/get_msg_pool_class";
	public static final String get_msg_pool_list = "/right/common/get_msg_pool_list";
	public static final String delete_msg = "/right/common/delete_msg";
	public static final String mark_unread_msg = "/right/common/edit_all_msg";
	public static final String get_message_setting = "/right/doctor/get_message_setting";
	public static final String set_message_setting = "/right/doctor/set_message_setting";
	
	//个人百科
	public static final String get_my_baike = "/right/doctor_baike/my_baike_home";
	public static final String get_other_baike = "/right/doctor_baike/other_baike_home";
	public static final String set_says = "/right/doctor_baike/set_says";
	public static final String set_says_pic = "/normal/baike/set_says_pic";
	public static final String get_says_detail = "/right/doctor_baike/get_says_detail";
	public static final String set_praise = "/right/doctor_baike/set_praise";	//点赞
	public static final String set_comment = "/right/doctor_baike/set_comment";	//评论说说
	public static final String get_bounty_list = "/right/doctor_baike/get_bounty_list";	//获取赏金列表
	public static final String get_fans_list = "/right/doctor_baike/get_attentionme_list";	//获取粉丝列表
	public static final String delete_comment = "/right/doctor_baike/delete_comment";	//删除评论
	public static final String set_attention = "/right/doctor_baike/set_attention";	//添加关注
	public static final String cancel_attention = "/right/doctor_baike/cancel_attention";	//取消关注
	public static final String get_my_attention = "/right/doctor_baike/get_myattention_list";	//获取我关注的医生列表
	public static final String set_share = "/right/doctor_baike/set_share";	//统计分享
	public static final String delete_says = "/right/doctor_baike/delete_says";	//删除说说
	public static final String VISITOR_LIST = "/right/doctor_baike/get_myvisitor_list";//访客列表
	public static final String get_says_list = "/right/doctor_baike/get_says_list";//说说列表
	public static final String JOIN_LIST = "/right/doctor_baike/get_myjoin_list";//我赞过/评论过/分享过的列表
	public static final String get_relateme_list = "/right/doctor_baike/get_relatedme_list";//赞过的/评论我的/分享我的列表
	public static final String get_myjoin_info = "/right/doctor_baike/get_myjoin_info";//我参与过的
	
	//问题池
	public static final String get_claim_list="/right/doctor_question/get_claim_list";  //我的认领问题列表
	public static final String get_claim_detail="/right/doctor_question/get_claim_detail";//我认领问题详细
	public static final String get_peer_list="/right/doctor_question/get_peer_list";//同行解答问题列表
	public static final String get_peer_detail="/right/doctor_question/get_peer_detail";//同行解答问题详细
	public static final String set_claim="/right/doctor_question/set_claim";				//认领问题
	public static final String set_close="/right/doctor_question/set_close";			//关闭问题
	public static final String set_praise_="/right/doctor_question/set_praise";		//问题点赞
	public static final String set_comment_="/right/doctor_question/set_comment"; 	//问题点评
	public static final String get_unclaim_list="/right/doctor_question/get_unclaim_list";//待认领的问题列表
	public static final String balance_plan="/right/doctor_question/balance_plan";//结算方案
	public static final String add_im_mass_log="/right/doctor/add_im_mass_log";//消息群发日志
	public static final String block_weitalk_users="/right/im/block_weitalk_users";//拉黑用户聊天
	public static final String enable_freetalk="/right/service_setting/enable_freetalk";//医生关闭/打开免费微聊
	
	//薏米花
	public static final String sign_in="/right/doctor_points/set_sign";//签到
	public static final String get_yimihua_home="/right/doctor_points/get_points_home";//获取薏米花首页信息
	public static final String get_score_exchange_record="/right/doctor_points/get_charge_list";//积分兑换记录
	public static final String get_score_earn_record="/right/doctor_points/get_points_list";//积分赚取明细
	public static final String get_points_charge_config="/normal/common/get_points_charge_config"; //获取薏米花兑换比例
	public static final String exchange_score="/right/doctor_points/set_charge"; //积分兑换
	
	//群聊相关	
	public static final String get_notice_list="/right/im/get_notice_list";//获取消息通知列表
	public static final String add_group_user="/right/im/add_group_user";//群组加人
	public static final String refused_add_group="/right/im/refused_add_group";//拒绝入群申请
	public static final String search_group_list="/right/im/search_group_lists"; //查找群组
	public static final String get_group_detail="/right/im/get_group_detail"; //群资料
	public static final String apply_to_group="/right/im/apply_to_group"; //申请入群
	public static final String get_doctor_detail_info="get_doctor_detail_info"; //获取医生详细信息
	
	public static final String REGISTER_GROUP = "/right/im/reg_im_group";				//注册群
	public static final String SET_GROUP_LOGO = "/normal/common/set_grouplogo_pic";		//设置群Logo
	public static final String GET_GROUP_LIST = "/right/im/group_list";					//群列表
	public static final String GET_GROUP_NOTICE_LAST = "/right/im/get_notice_lastest";	//最后一条群通知
	public static final String GET_GROUP_NOTICE_LIST = "/right/im/get_notice_list";		//群通知列表
	public static final String GROUP_EXIT = "/right/im/exit_to_group";					//解散群
	public static final String GROUP_KICK =	"/right/im/kick_group_users";				//踢出群
	
	public static long getLength(String path,
			ArrayList<BasicNameValuePair> params, int method) throws Exception {
		long length = 0;
		HttpEntity entity = getEntity(path, params, method);
		if (entity != null) {
			length = entity.getContentLength();
		}
		return length;
	}

	public static HttpEntity getEntity(String uri,
			ArrayList<BasicNameValuePair> params, int method)
			throws IOException {
		HttpEntity entity = null;
		HttpClient client = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
//		System.out.println("url = " + uri);
		StringBuilder sb = new StringBuilder(uri);
		HttpUriRequest request = null;
		switch (method) {
		case GET:
			if (params != null) {
				sb.append('?');
			}
			buildParams(sb, params);
			request = new HttpGet(sb.toString());
			break;
		case POST:
			/** 设置请求实体 */
			request = new HttpPost(sb.toString());
			if (params != null && !params.isEmpty()) {
				((HttpPost) request).setEntity(new UrlEncodedFormEntity(params,
						"utf-8"));
			}
			break;
		}
		/** 执行请求响应 */
		HttpResponse response = client.execute(request, httpContext);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			entity = response.getEntity();
		}
		return entity;
	}

	public static InputStream getInputStream(String uri,
			ArrayList<BasicNameValuePair> params, int method)
			throws IOException {
		InputStream in = null;
		HttpEntity entity = getEntity(uri, params, method);
		if (entity != null) {
			in = entity.getContent();
		}
		return in;
	}

	// public static String getString(String uri,
	// ArrayList<BasicNameValuePair> params, int method)
	// throws IOException {
	// String content = null;
	// HttpEntity entity = getEntity(uri, params, method);
	// if (entity != null) {
	// content = EntityUtils.toString(entity, "utf-8");
	// }
	// if (!TextUtils.isEmpty(content)) {
	// Log.i("life", content + "");
	// }
	//
	// return content;
	// }
	public static String getString(String uri,
			ArrayList<BasicNameValuePair> params, int method)
			throws IOException {
		String content = null;
//		Log.i("url", uri);
		// **********httpclient模式***/
		// HttpEntity entity = getEntity(uri, params, method);
		// if (entity != null) {
		// content = EntityUtils.toString(entity, "utf-8");
		// }
		// ***********httpclient模式**end*/

		switch (method) {
			case GET:
				// 暂没用到，工期较紧，先不写
				break;
			case POST:
				content = post(uri, params);
				break;
		}
		if (!TextUtils.isEmpty(content)) {
//			Log.i("life", content + "");
		}
		return content;
	}

	private static final String tag = HttpUtil.class.getSimpleName();
	

	/**
	 * multipart/form-data
	 * 
	 * @param actionUrl
	 * @param params
	 * @param files
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String post(String actionUrl,
			ArrayList<BasicNameValuePair> params) throws IOException {
//		Log.d(tag, "request-url=" + actionUrl);
		String result = null;
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(60 * 1000); // 缓存的最长时间
		conn.setConnectTimeout(60 * 1000);
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		DataOutputStream outStream = null;
		if (null != params) {
			for (BasicNameValuePair entry : params) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getName() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(TextUtils.isEmpty(entry.getValue()) ? "" : entry
						.getValue());
				sb.append(LINEND);
			}
//			Log.i("httptest", "test" + sb.toString());
			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
		}
//		Log.i("httptest", "testend");
		InputStream in = null;

		// 得到响应码
		int res = conn.getResponseCode();
//		Log.i(tag, res + "-----------------1");
		if (res == 200) {
			in = conn.getInputStream();
			result = convertStreamToString(in);
		}
		if (null != outStream) {
			outStream.close();
		}
		if (null != conn) {
			conn.disconnect();
		}
//		Log.d(tag, "result=" + result);
		return result;
	}

	static String convertStreamToString(InputStream is) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8 * 1024);
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			sb.delete(0, sb.length());
		} finally {
			closeStream(is);
		}
		return sb.toString();
	}

	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {

			}
		}
	}

	// 上传文件
	public static String uploadFile(String uri,
			ArrayList<BasicNameValuePair> params, byte[] bytes)
			throws IOException {
		String json = buildJosn(params);

		byte[] data = json.getBytes();

		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(30000);
		// 这是请求方式为POST
		conn.setRequestMethod("POST");
		// 设置post请求必要的请求头
		conn.setRequestProperty("Content-Type", "application/octet-stream");// 请求头,
																			// 必须设置
		conn.setDoOutput(true);// 准备写出
//		conn.setRequestProperty("Charset", "UTF-8");
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

		dos.write(ByteUtil.int2byte(data.length));
			
		dos.writeBytes(json);
		if (bytes != null && bytes.length != 0) {
			dos.write(bytes);
		}
		
		dos.flush();

		InputStream in = conn.getInputStream();

		String str = StreamUtil.getStreamString(in);
//		Log.i("life", str);
		if (in != null) {
			in.close();
		}
		conn.disconnect();
		dos.close();

		return str;
	}

	public static InputStream loadFile(String uri) throws IOException {
		URL url = new URL(uri);
		URLConnection conn = url.openConnection();
		return conn.getInputStream();
	}

	public static String buildJosn(ArrayList<BasicNameValuePair> params)
			throws IOException {
		StringWriter sw = new StringWriter();
		JsonWriter jw = new JsonWriter(sw);
		if (params != null && !params.isEmpty()) {
			jw.beginObject();
			for (BasicNameValuePair param : params) {
				jw.name(param.getName()).value(param.getValue());
			}
			jw.endObject();
			jw.close();
			sw.close();

		}

		return sw.toString();
	}

	public static StringBuilder buildParams(StringBuilder sb,
			ArrayList<BasicNameValuePair> params) {
		if (params != null && params.size() > 0) {
			for (BasicNameValuePair pair : params) {
				sb.append(pair.getName()).append('=').append(pair.getValue())
						.append('&');
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb;
	}
}
