package com.hanwhalife.nbm.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * <b>Description:</b><p>
 * 문자열 관련 Util함수 클래스입니다.
 * @since 2019-07-01
 * @version 1.0
 * @author 1073302
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-07-01  1073302     최초생성
 * </pre>
 */
@Component
public final class StringUtil {
  private static final long serialVersionUID = 1L;
  private static final String CHARSET = "euc-kr";
  
  /**
   * CPSP 대응
   */
  private StringUtil() {
    // TODO Auto-generated method stub
  }

  /**
   * 문자열 왼쪽에 원하는 문자로 채움
   * 
   * @param str
   *          : String 문자열
   * @param size
   *          : Padding Length
   * @param padStr
   *          : Padding String
   * @return String
   */
  public static String leftPad(final String str, final int size, final String padStr ) {
    return StringUtils.leftPad(str, size, padStr);
  }
  /**
   * int 왼쪽에 원하는 문자로 채움
   * 
   * @param iStr
   *          : 문자로 변환 할 숫자
   * @param size
   *          : Padding Length
   * @param padStr
   *          : Padding String
   * @return String
   */
  public static String leftPad(final int iStr, final int size, final String padStr ) {
    return StringUtils.leftPad(String.valueOf(iStr), size, padStr);
  }
  
  /**
   * 문자열 오른쪽에 원하는 문자로 채움
   * 
   * @param str
   *          : String 문자열
   * @param size
   *          : Padding Length
   * @param padStr
   *          : Padding String
   * @return String
   */
  public static String rightPad(final String str, final int size, final String padStr ) {
    return StringUtils.rightPad(str, size, padStr);
  }
  
  /**
   * 문자열 오른쪽에 원하는 문자로 채움(byte길이)
   * 
   * @param str
   *          : String 문자열
   * @param size
   *          : Padding Length
   * @param padStr
   *          : Padding String
   * @return String
   */
  public static String leftPadByte(final String str, final int size, final String padStr ) {
    int len = size;
    try {
      len += (str.length() - str.getBytes(CHARSET).length);
    } catch (UnsupportedEncodingException ex) {
//      ex.printStackTrace();
    }
    return StringUtils.leftPad(str, len, padStr);
  }
  /**
   * 숫자 오른쪽에 원하는 문자로 채움(byte길이)
   * 
   * @param iStr
   *          : 문자열로 변환 할 숫자
   * @param size
   *          : Padding Length
   * @param padStr
   *          : Padding String
   * @return String
   */
  public static String leftPadByte(final int iStr, final int size, final String padStr ) {
    String str = String.valueOf(iStr);
    int len = size;
    try {
      len += (str.length() - str.getBytes(CHARSET).length);
    } catch (UnsupportedEncodingException ex) {
//      ex.printStackTrace();
    }
    return StringUtils.leftPad(str, len, padStr);
  }
  /**
   * 문자열 왼쪽에 원하는 문자로 채움(byte길이)
   * 
   * @param str
   *          : String 문자열
   * @param size
   *          : Padding Length
   * @param padStr
   *          : Padding String
   * @return String
   */
  public static String rightPadByte(final String str, final int size, final String padStr ) {
    int len = size;
    try {
      len += (str.length() - str.getBytes(CHARSET).length);
    } catch (UnsupportedEncodingException usee) {
      usee.printStackTrace();
    }
    return StringUtils.rightPad(str, len, padStr);
  }
  
  /**
   * 문자열이 null이거나 ""이거나 공백으로 채워져있으면 True, 아니면 False를 리턴합니다.
   * @param str
   *          : String 문자열
   * @return boolean
   */
  public static boolean isBlank(final String str) {
    return StringUtils.isBlank(str);
  }
  
  /**
   * 문자열이 숫자형식인지 확인합니다.
   * @param str
   *          : String 문자열
   * @return boolean
   */
  public static boolean isNumeric(final String str) {
    return StringUtils.isNumeric(str);
  }
  
  /**
   * 문자열이 null이면 빈값('')을 리턴합니다.
   * @param obj
   *          : Object
   * @return String
   */
  public static String nullToEmpty(final Object obj) {
    return obj == null ? "" : (String)obj;
  }
  
  
  /**
   * 입력전화번호를 마스킹 처리한 값[##-####-####]으로 리턴합니다.
   * @param String num : 입력전화번호
   *        String mask : 마스킹여부
   * @return String maskNum : 리턴전화번호
   */
  public static String phoneNumberMask(final String num, final String mask) {
    String maskNum = "";
    
    if ("".equals(StringUtil.nullToEmpty(num))) {
      return maskNum;
    }
    
    String tempNum = "";
    tempNum = num.replaceAll("-","");
    
    if (tempNum.length() == 11) {
      if ("Y".equals(mask)) {
        maskNum = tempNum.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-****-$3");
      } else {
        maskNum = tempNum.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
      }
    } else if (tempNum.length() == 8) {
      maskNum = tempNum.replaceAll("(\\d{4})(\\d{4})", "$1-$2");
    } else {
      if (tempNum.indexOf("02") == 0) {
        if ("Y".equals(mask)) {
          maskNum = tempNum.replaceAll("(\\d{2})(\\d{3,4})(\\d{4})", "$1-****-$3");
        } else {
          maskNum = tempNum.replaceAll("(\\d{2})(\\d{3,4})(\\d{4})", "$1-$2-$3");
        }
      } else {
        if ("Y".equals(mask)) {
          maskNum = tempNum.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-****-$3");
        } else {
          maskNum = tempNum.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
        }
      }
    }
      
    return maskNum;
    
  }
  
}
