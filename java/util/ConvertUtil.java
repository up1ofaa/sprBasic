package com.hanwhalife.nbm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.lang3.Conversion;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <b>Description:</b><p>
 * 변환 관련 클래스입니다.
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
public final class ConvertUtil {
  private static final long serialVersionUID = 1L;

  /*
   * Comment for created constructors
   */
  private ConvertUtil() {
    // TODO Auto-generated constructor stub
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
   * 문자열 long 형으로 변환
   * 
   * @param str
   *          : String 문자열
   * @param defaultVal
   *          : long 문자열 값이 null 일 경우 값
   * @return long
   */
  public static long strToLong(final String str, final long defaultVal) {

    return (long)strToDouble(str, defaultVal);
  }

  /**
   * 문자열 long 형으로 변환
   * 
   * @param str
   *          : String 문자열
   * @return long
   */
  public static long strToLong(final String str) {
    return (long)strToDouble(str, 0);
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

  /**
   * Object타입을 byte[]형으로 변환
   * 
   * @param p_object
   *          : Serializable Object
   * @return byte[]
   */
  public static byte[] objectToBytes(final Serializable p_object ) throws Exception {
    byte[] returnVal = null;
    ByteArrayOutputStream baos = null;
    try {
      baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream( baos );
      oos.writeObject( p_object );
      oos.flush();
      returnVal = encodeBytesForEAI(baos.toByteArray());
    } finally {
      try {
        if (baos != null) {
          baos.close();
        }
      } catch ( Exception ex ) {
      }
    }
    return returnVal;
  }

  /**
   * ESB Server와의 통신을 위해 binary data에 필드 구분자를 encode해주는 역활을 수행하는 함수이다.
   * @param p_bytes
   * @return byte[]
   */
  public static byte[] encodeBytesForEAI(final byte[] p_bytes ) throws Exception {
    byte[] returnVal = null;
    byte[] CONVERT_BYTE_DEFINE_ENCODE = { 0x1D, 0x01, 0x1D, 0x02, 0x1D, 0x03 };
    ByteArrayOutputStream ret_baos = null;
    try {
      // object를 byte로 만든 것에 필드 구분자가 존재하는 경우에 대한 처리를 수행하기 위해
      // 추가된 code.
      ret_baos = new ByteArrayOutputStream();
      for (int i = 0; i < p_bytes.length; i++) {
        if (p_bytes[i] == 0x1D) {
          ret_baos.write( CONVERT_BYTE_DEFINE_ENCODE, 0, 1 );
          ret_baos.write( CONVERT_BYTE_DEFINE_ENCODE, 1, 1 );
        } else if (p_bytes[i] == 0x1E) {
          ret_baos.write( CONVERT_BYTE_DEFINE_ENCODE, 2, 1 );
          ret_baos.write( CONVERT_BYTE_DEFINE_ENCODE, 3, 1 );
        } else if (p_bytes[i] == 0x1F) {
          ret_baos.write( CONVERT_BYTE_DEFINE_ENCODE, 4, 1 );
          ret_baos.write( CONVERT_BYTE_DEFINE_ENCODE, 5, 1 );
        } else {
          ret_baos.write( p_bytes, i, 1 );
        }
      }
      // for statement...
      returnVal = ret_baos.toByteArray();
    } finally {
      try {
        if (ret_baos != null) {
          ret_baos.close();
        }
      } catch ( Exception ex ) {
      }
    }
    return returnVal;
  }

  /**
   * BytesToObject함수와 반대의 동작을 수행하는 함수로 Object로 되어 있는 것을 bytes로 변환하는 기능을 수행한다.
   * Serializable p_object : 변환할 Object로 해당 class는 반드시 serialization되어 있어야 한다.
   * @param p_objectbytes
   * @return Serializable
   */
  public static Serializable bytesToObject(final byte[] p_objectbytes ) throws Exception {
    Serializable returnVal = null;
    ByteArrayInputStream bais = null;
    try {
      bais = new ByteArrayInputStream(  decodeBytesForEAI(p_objectbytes) );
      ObjectInputStream ois = new ObjectInputStream( bais );
      returnVal = (Serializable) ois.readObject();
    } finally {
      try {
        if (bais != null) {
          bais.close();
        }
      } catch ( Exception ex ) {
      }
    }
    return returnVal;
  }

  /**
   * ESB Server와의 통신을 위해 binary data에 필드 구분자를 decode해주는 역활을 수행하는 함수이다.
   * @param p_objectbytes
   * @return byte[]
   */
  public static byte[] decodeBytesForEAI(final byte[] p_objectbytes ) throws Exception {
    byte[] returnVal = null;
    byte[] CONVERT_BYTE_DEFINE_DECODE = { 0x1D, 0x1E, 0x1F };
    ByteArrayOutputStream ret_baos = null;
    try {
      ret_baos = new ByteArrayOutputStream();
      for (int i = 0; i < p_objectbytes.length; i++) {
        if (p_objectbytes[i] == 0x1D) {
          i++;
          if (p_objectbytes[i] == 0x01) {
            ret_baos.write( CONVERT_BYTE_DEFINE_DECODE, 0, 1 );
          } else if (p_objectbytes[i] == 0x02) {
            ret_baos.write( CONVERT_BYTE_DEFINE_DECODE, 1, 1 );
          } else if (p_objectbytes[i] == 0x03) {
            ret_baos.write( CONVERT_BYTE_DEFINE_DECODE, 2, 1 );
          }
        } else {
          ret_baos.write( p_objectbytes, i, 1 );
        }
      }
      returnVal = ret_baos.toByteArray();
    } finally {
      try {
        if (ret_baos != null) {
          ret_baos.close();
        }
      } catch ( Exception ex ) {
      }
    }
    return returnVal;
  }

  /**
   * int intVal를 bytes[byteLen]로 변환합니다.
   * @param intVal - 변환할 int value 
   * @param byteLen - 변환될 byte[] 길이
   * @return byte[]
   */
  public static byte[] intToBtyes(final int intVal, final int byteLen) throws Exception {
    byte[] byteArr = new byte[byteLen];
    //    byteArr[0] = (byte)(intVal >> 0 & 0xff);
    //    byteArr[1] = (byte)(intVal >> 8 & 0xff);
    //    byteArr[2] = (byte)(intVal >> 16  & 0xff);
    //    byteArr[3] = (byte)(intVal >> 24  & 0xff);
    //    return byteArr;
    return Conversion.intToByteArray(intVal, 0, byteArr, 0, byteLen);
  }

  /**
   * byte[byteLen]을  int로 변환합니다.
   * @param byteArr - 변환될 byte[] value 
   * @param byteLen - 변환할 byte[] 길이
   * @return int
   */
  public static int btyesToInt(final byte[] byteArr, final int byteLen) throws Exception {
    int ret = 0 ;
    //    ret = (((int)byteArr[3] & 0xff) << 24) |
    //          (((int)byteArr[2] & 0xff) << 16) |
    //          (((int)byteArr[1] & 0xff) << 8 ) |
    //          ((int)byteArr[0] & 0xff);
    //    return ret;
    return Conversion.byteArrayToInt(byteArr, 0, ret, 0, byteLen);
  }

  /**
   * 16진 문자열을 byte 배열로 변환한다.
   */
  public static byte[] hexToByteArray(final String hex) {
    if (hex == null || hex.length() % 2 != 0) {
      return new byte[]{};
    }

    byte[] bytes = new byte[hex.length() / 2];
    byte value = 0x00;
    int len = hex.length();
    for (int i = 0; i < len; i += 2) {
      value = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
      bytes[(int) Math.floor(i / 2)] = value;
    }
    return bytes;
  }

}
