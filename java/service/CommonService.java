package com.hanwhalife.nbm.service.cm;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hanwhalife.nbm.util.Result;


/**
 * <b>Description:</b><p>
 * CommonService.
 * @since 2019-06-17
 * @version 1.0
 * @author 홍선기(1073302)
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-06-17  1073302     최초생성
 * </pre>
 */
public interface CommonService {

  /**
   * 공통 코드그룹 리스트 조회
   * @param paramMap Map<String, Object> 
   * @return Result
   * @exception Exception
   */
  public Result selectCommonGroup(Map<String, Object> paramMap) throws Exception;

  /**
   * 공통 코드그룹을 등록/수정/삭제 합니다.
   * @param paramMap Map<String, Object> 
   * @return Result
   * @exception Exception
   */
  public Result saveCommonGrp(Map<String, Object> paramMap) throws Exception;

  /**
   * 공통 코드그룹 세부를 등록/수정/삭제 합니다.
   * @param paramList - Map<String, Object>
   * @return Result
   * @exception Exception
   */
  public Result saveCommonGrpCode(Map<String, Object> paramList) throws Exception;

  /**
   * selectCommonGroupCode
   * @param Map<String, Object> paramMap
   * @return Result
   * @exception Exception
   */
  public Result selectCommonGroupCode(Map<String, Object> paramMap) throws Exception;

  //	/**
  //	 * 공통모듈 - 사용자 메세지 조회
  //	 * @param Map<String, Object> paramMap
  //	 * @return Result
  //	 * @exception Exception
  //	 */
  //	public Map<String, Object> selectMsgCode(String msgId) throws Exception;
  //	public Map<String, Object> selectMsgCode(Map<String, Object> paramMap) throws Exception;

  /**
   * 공통모듈 - 세부 공통코드리스트 조회(화면용)
   * @param paramMap Map<String, Object>
   * @return ArrayList<Map<String, Object>>
   * @exception Exception
   */
  public Result selectCommonCode(Map<String, Object> paramMap) throws Exception;
  /**
   * 공통모듈 - 세부 공통코드리스트 조회(서버용)
   * @param paramMap Map<String, Object>
   * @param order String
   * @return ArrayList<Map<String, Object>>
   * @exception Exception
   */
  //public ArrayList<Map<String, Object>> selectCommonCode(Map<String, Object> paramMap, String order) throws Exception;

  /**
   * 사용자메세지 리스트 조회 
   * @param paramMap Map<String, Object>
   * @return Result
   * @exception Exception
   */
  public Result selectMessageCode(Map<String, Object> paramMap) throws Exception;

  /**
   * 사용자메세지 저장 
   * @param paramMap Map<String, Object>
   * @return Result
   * @exception Exception
   */
  public Result insertMsgCode(Map<String, Object> paramMap) throws Exception;

  /**
   * 사용자메세지 삭제
   * @param paramMap Map<String, Object>
   * @return Result
   * @exception Exception
   */
  public Result deleteMsgCode(Map<String, Object> paramMap) throws Exception;

  /**
   * 거래로그 리스트 조회
   * @param paramMap Map<String, Object>
   * @return Result
   * @exception Exception
   */
  public Result selectTxLog(Map<String, Object> paramMap) throws Exception;
  
  /**
   * 이미지시스템 파일 업로드
   * @param HttpServletRequest
   * @return Result
   * @exception Exception
   */
  public Result imgFileUpload(HttpServletRequest request) throws Exception;
  
  /**
   * 이미지시스템 파일 업로드
   * @param HttpServletRequest
   * @return Result
   * @exception Exception
   */
  public Result selectImgFileUpload(Map<String, Object> paramMap) throws Exception;
  
  /**
   * 출력물 관리 리스트 조회
   * @param HttpServletRequest
   * @return Result
   * @exception Exception
   */
  public Result selectReportInfo(Map<String, Object> paramMap) throws Exception;
  
  /**
   * 배치이력 리스트 조회
   * @param HttpServletRequest
   * @return Result
   * @exception Exception
   */
  public Result selectBatchInfo(Map<String, Object> paramMap) throws Exception;



}
