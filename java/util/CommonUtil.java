package com.hanwhalife.nbm.util;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Map;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Component
public final class CommonUtil {
  private static final long serialVersionUID = 1L;
  
  /*
   * Comment for created constructors
   */
  private CommonUtil() {
    // TODO Auto-generated constructor stub
  }
  
  /**
   * 공통모듈 - 조회 DataCollection에 결과리스트의 마지막 데이터ROW를 CTS값에 추가하여 조회 DataCollection을
   * 반환합니다.
   * 
   * @param search
   *          - 조회 DataCollection
   * @param result
   *          - 결과값 반환 리스트
   * @return Map<String, Object>
   * @exception Exception
   */
  public static Map<String, Object> setCTS(final Map<String, Object> search, final ArrayList<Map<String, Object>> result)
      throws Exception {
    if (search.containsKey("ROW_NUM") == false || result.size() == 0) {
      return search;
    }

    for (String key : search.keySet()) {
      if (key.startsWith("CTS_")) {
        search.put(key, result.get(result.size() - 1).get(key.replace("CTS_", "")));
      }
    }
    return search;
  }
  
  /**
   * 공통모듈 - 세션정보를 가져옵니다.
   * @return HttpSession
   * @exception Exception
   */
  public static HttpSession getSession() {
    HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    return req.getSession();
  }
  
  /**
   * 공통모듈 - Map<k,v>타입 value가 null일때 빈값으로 변환.
   * @param map
   * @return Map<String, Object>
   * @exception Exception
   */
  public static Map<String, Object> mapNullValues(final Map<String, Object> map) throws Exception {
    for (String key : map.keySet()) {
      if (map.get(key) == null) {
        map.put(key, "");
      }
    }
    return map;
  }
  
  /**
   * 공통모듈 - RSA decrypt.
   * @param map
   * @return Map<String, Object>
   * @exception Exception
   */
  public static String decryptRsa(final PrivateKey privateKey, final String securedValue) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    byte[] encryptedBytes = ConvertUtil.hexToByteArray(securedValue);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
    String decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩 주의.
    return decryptedValue;
  }
  
  
}
