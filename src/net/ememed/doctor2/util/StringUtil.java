package net.ememed.doctor2.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * @author July
 */

public class StringUtil {

    public static final String URL_REG_EXPRESSION = "^(https?://)?([a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+)+(/*[A-Za-z0-9/\\-_&:?\\+=//.%]*)*";
    public static final String EMAIL_REG_EXPRESSION = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";
    //    public static final String MOBILE_REG_EXPRESSION = "^(1)\\d{10}$";
    public static final String MOBILE_REG_EXPRESSION = "^((13[0-9])|(15[^4,\\D])|(18[0-9])|(14[0-9])|(17[0-9]))\\d{8}$";
    public static final String NUMBER_REG_EXPRESSION = "[0-9]*";
    public static final String IP_REG_EXPRESSION = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}";
    public static final String IDENTITY_REG_EXPRESSION = "^\\d{15}|\\d{18}$";
    public static final String IDENTITY_REG_EXPRESSION_15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    public static final String IDENTITY_REG_EXPRESSION_18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$";

    public static String parseFileNameWithTime(Long time) {
        Date date = new Date(time);
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateTime = dateFm.format(date);
        return dateTime;
    }

    /**
     * @param s
     * @return
     */
    public static boolean isBlank(String s) {
        if (s == null) {
            return true;
        }
        return Pattern.matches("\\s*", s);
    }

    /**
     * @param s
     * @return
     */
    public static boolean isEmail(String s) {
        if (s == null) {
            return true;
        }
        return Pattern.matches(EMAIL_REG_EXPRESSION, s);
    }

    /**
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * @param s
     * @return
     */
    public static boolean isMobile(String s) {
        if (s == null) {
            return false;
        }
        return Pattern.matches(MOBILE_REG_EXPRESSION, s);
    }

    /**
     * @param s
     * @return
     */
    public static boolean isUrl(String s) {
        if (s == null) {
            return false;
        }
        return Pattern.matches(URL_REG_EXPRESSION, s);
    }

    /**
     * @param s
     * @return
     */
    public static boolean isNumber(String s) {
        if (s == null) {
            return false;
        }
        return Pattern.matches(NUMBER_REG_EXPRESSION, s);
    }

    /**
     * У�����֤(15/18λ)
     *
     * @param s
     * @return
     */
    public static boolean isSimpleIdentity(String s) {
        if (s == null) {
            return false;
        }
        return Pattern.matches(IDENTITY_REG_EXPRESSION, s);
    }

    /**
     * ����У�����֤(15/18λ)
     *
     * @param s
     * @return
     */
    public static boolean isIdentity(String s) {
        if (s == null) {
            return false;
        }
        return (Pattern.matches(IDENTITY_REG_EXPRESSION_15, s) || Pattern.matches(IDENTITY_REG_EXPRESSION_18, s));
    }
}
