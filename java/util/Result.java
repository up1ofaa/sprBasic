package com.hanwhalife.nbm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.hanwhalife.nbm.service.cm.com.CommonComponent;

/**
 * <b>Description:</b>
 * <p>
 * Result 화면에 결과를 리턴하는 객체클래스입니다.
 * 
 * @since 2019-06-20
 * @version 1.0
 * @author 홍선기(1073302)
 * @see
 * 
 *      <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-06-20  1073302     최초생성
 *      </pre>
 */
public class Result {

  private static final Logger LOGGER = LoggerFactory.getLogger(Result.class);

  private Map<String, Object> resultMap = new HashMap<String, Object>();
  private Map<String, Object> rsMsg = new HashMap<String, Object>();

  // 기본메세지코드
  private final static String STATUS_DEFAULT = "S";
  // 결과값에 대한 메시지 key명
  private final static String MESSAGE_KEY = "rsMsg";
  // 여부 N
  private final static String STATUS_N = "N";

  public void setData(final String id, final String data) {
    resultMap.put(id, data);
  }

  public void setData(final String id, final Map data) {
    resultMap.put(id, data);
  }

  public void setData(final String id, final List data) {
    resultMap.put(id, data);
  }
  
  public Object getData(final String id) {
    return resultMap.get(id);
  }

  public Map<String, Object> getResult() {
    if (resultMap.get(MESSAGE_KEY) == null) {
      setMsgResult(STATUS_DEFAULT, "", "", STATUS_N);
    } else {
      CommonUtil.getSession().setAttribute("DLNG_RESL", rsMsg.get("statusCode"));
      CommonUtil.getSession().setAttribute("DLNG_RTMS_CTEN", rsMsg.get("message"));
      CommonComponent.insertTxLog();
    }
    
    return resultMap;
  }
  
  public String toString() {
    return resultMap.toString();
  }

  /**
   * 메세지 처리 - 공통 메세지 처리
   * 
   * @date 2019.06.20
   * @param msgId
   *          : 메세지 ID
   * @returns void
   * @author 1073302
   * @example result.setMsg("S0001");
   */
  public void setMsg(final String msgId) {
    Map<String, Object> msgMap = new HashMap<>();
    String msg = "";
    String msgType = "";
    String popYn = "";
    String msgIdParam = StringUtil.nullToEmpty(msgId);
    LOGGER.debug("msgId == " + msgId);
    msgMap = CommonComponent.selectMsgCode(msgIdParam);
    if ( msgMap != null && msgMap.size() > 0) {
      msg = msgMap.get("MESG_CTEN").toString();
      msgType = msgMap.get("MESG_TPCD").toString();
      popYn = msgMap.get("POPU_YN").toString();
      setMsgResult(msgType, msgIdParam, msg, popYn);
    } else {
      setMsgResult(STATUS_DEFAULT, msgIdParam, msg, STATUS_N);
    }
}

  /**
   * 메세지 처리 - 공통 메세지 처리
   * 
   * @date 2019.06.20
   * @param msgId
   *          : 메세지 ID
   * @param args
   *          : 바인딩 문자열
   * @returns void
   * @author 1073302
   * @example result.setMsg("S9999", "코드 조회가 ", "완료되었습니다.", ...);
   */
  public void setMsg(final String msgId, final Object... args) {
    Map<String, Object> msgMap = new HashMap<>();
    String msg = "";
    String msgType = "";
    String popYn = "";
    String msgIdParam = StringUtil.nullToEmpty(msgId);
    
    msgMap = CommonComponent.selectMsgCode(msgIdParam);
    if ( msgMap != null && msgMap.size() > 0) {
      msg = String.format(msgMap.get("MESG_CTEN").toString(), args);
      msgType = msgMap.get("MESG_TPCD").toString();
      popYn = msgMap.get("POPU_YN").toString();
      setMsgResult(msgType, msgIdParam, msg, popYn);
    } else {
      setMsgResult(STATUS_DEFAULT, msgIdParam, msg, STATUS_N);
    }

  }

  /**
   * 메세지 처리
   * 
   * @date 2019.06.20
   * @param statusCode
   *          : 메세지상태코드
   * @param messageCode
   *          : 메세지코드,
   * @param message
   *          : 메시지
   * @param popYn
   *          : 팝업여부
   * @returns void
   * @author 1073302
   */
  private void setMsgResult(final String statusCode, final String messageCode, final String message, final String popYn) {
    LOGGER.debug("return MSG = [" + messageCode + "]" + message);
    rsMsg.put("statusCode", statusCode);
    rsMsg.put("messageCode", messageCode);
    rsMsg.put("message", message );
    rsMsg.put("popYn", getDefaultStatusMessage(popYn, STATUS_N));

    resultMap.put(MESSAGE_KEY, rsMsg);
  }

  private String getDefaultStatusMessage(final String message, final String defMessage) {
    if (StringUtils.isEmpty(message)) {
      return defMessage;
    }
    return message;
  };
}
