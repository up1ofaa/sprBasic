/*
필요 lib
commons-lang3-3.3.2.jar
*/

package testPj;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Conversion;


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
   * 문자열 정수형으로 변환
   * 
   * @param str
   *          : String 문자열
   * @param defaultVal
   *          : int 문자열 값이 null 일 경우 값
   * @return int
   */
  public static int strToInt(final String str, final int defaultVal) {

    return (int)strToDouble(str, defaultVal);
  }

  /**
   * 문자열 정수형으로 변환
   * 
   * @param str
   *          : String 문자열
   * @return int
   */
  public static int strToInt(final String str) {
    return (int)strToDouble(str, 0);
  }
  
  /**
   * 문자열 double 형으로 변환
   * 
   * @param str
   *          : String 문자열
   * @return double
   */
  public static double strToDouble(final String str) {
    return strToDouble(str, 0);
  }
  
  /**
   * 문자열 double 형으로 변환
   * 
   * @param str
   *          : String 문자열
   * @param defaultVal
   *          : double 문자열 값이 null 일 경우 값
   * @return double
   */
  public static double strToDouble(final String str, final double defaultVal) {
    if (str.isEmpty()) {
      return defaultVal;
    }

    try {
      return Double.parseDouble(str.trim());
    } catch (NumberFormatException ne) {

    }

    return defaultVal;
  }

  
}
