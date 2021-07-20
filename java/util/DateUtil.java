package com.hanwhalife.nbm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <b>Description:</b><p>
 * 날짜 관련 Util함수 클래스입니다.
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
public final class DateUtil {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);
  
  /*
   * Comment for created constructors
   */
  private DateUtil() {
    // TODO Auto-generated constructor stub
  }
  
  /**
   * 공통모듈 - 현재날짜를 yyyyMMdd 형식으로 가져옵니다.
   * @return String
   * @exception Exception
   */
  public static String getToday() {
    return new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(new Date());
  }
  
  /**
   * 공통모듈 - 현재 Date Time을 format에 맞춰 반환합니다.
   * @return String
   * @exception Exception
   * @example getCurrentTime("yyyy-MM-dd HH:mm:ss"), getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS") 
   */
  public static String getCurrent(final String format) {
    return new SimpleDateFormat(format, Locale.KOREA).format(new Date());
  }

  /**
   * <pre>
   * 일자 더하기 또는 빼기
   * </pre>
   *
   * @param strDt
   * @param addDay
   * @return String
   */
  public static String addDay(final String strDt,final  int addDay) {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    Date date = null;
    try {
      date = sdf.parse(strDt);
    } catch (ParseException ex) {
      LOGGER.debug(ex.toString());
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, addDay);

    String strDay = sdf.format(cal.getTime());

    return strDay;

  }


}
