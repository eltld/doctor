package net.ememed.doctor2.util;

public class UserPreferenceWrapper {
	
	private static final String FORMAT_ACCESS_KEY = "accesskey=%s#%s#%s; Path=/; HttpOnly";

	public static boolean isLogin() {
		return SharePrefUtil.getBoolean(Conast.LOGIN);
	}

	public static String getAccessKey() {
		if (!isLogin()) {
			throw new IllegalArgumentException("the user is not login");
		}
		return String.format(FORMAT_ACCESS_KEY, SharePrefUtil.getString(Conast.USER_TYPE),
				SharePrefUtil.getString(Conast.Doctor_ID),
				SharePrefUtil.getString(Conast.ACCESS_TOKEN));
	}
	
	/**
	 * 获取Token
	 * @return
	 */
	public static String getToken() {
		return SharePrefUtil.getString(Conast.ACCESS_TOKEN);
	}
	
	/**
	 * 获取医生Id
	 * @return
	 */
	public static String getMemberId() {
		return SharePrefUtil.getString(Conast.Doctor_ID);
	}

	/**
	 * 是否审核通过
	 * @return
	 */
	public static boolean isValidated () {
		return SharePrefUtil.getBoolean(Conast.VALIDATED) && SharePrefUtil.getBoolean(Conast.LOGIN);
	}
	
	/**
	 * 获取审核状态
	 * @return
	 */
	public static String getAuditStatus() {
		return SharePrefUtil.getString(Conast.AUDIT_STATUS);
	}

	/**
	 * 获取审核失败原因
	 * @return
	 */
	public static String getAuditRefuse() {
		return SharePrefUtil.getString(Conast.AUDIT_REFUSE);
	}
	
	public static String getCertificateHome() {
		return SharePrefUtil.getString(Conast.CERTIY_TYPE_CERT);
	}
	
}
