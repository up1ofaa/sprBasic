package com.hanwhalife.nbm.batch.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * <b>Description:</b><p>
 * Spring의 MyBatis 연동 지원 공통 DAO 클래스.
 * @since 2019.04.07
 * @version 1.0
 * @author 홍선기(1073302)
 * @see
 * <pre>
 * 개정이력Modification Information
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019.04.07  홍선기(1073302)     최초생성
 * </pre>
 */

public interface CommonBatchDAO {
  /**
   * 등록
   * @param statement - sql mapping 쿼리ID
   * @param paramMap - Map<String, Object>
   * @return int 등록 결과 건수
   * @exception SQLException
   */
  public int insert(String statement, Map<String, Object> paramMap) throws SQLException;

  /**
   * 결과값이 있는 등록
   * @param statement - sql mapping 쿼리ID
   * @param paramMap - 등록할 정보가 담긴 CommonMap
   * @return selectKey 반환값
   * @exception SQLException
   */
  public Object insertRetVal(String statement, Map<String, Object> paramMap) throws SQLException;

  /**
   * 수정
   * @param statement - sql mapping 쿼리ID
   * @param paramMap - Map<String, Object>
   * @return 수정 결과 건수
   * @exception SQLException
   */
  public int update(String statement, Map<String, Object> paramMap) throws SQLException;

  /**
   * 삭제
   * @param statement - sql mapping 쿼리ID
   * @param paramMap - Map<String, Object>
   * @return 삭제 결과 건수
   * @exception SQLException
   */
  public int delete(String statement, Map<String, Object> paramMap) throws SQLException;

  /**
   * 조회
   * @param statement - sql mapping 쿼리ID
   * @param paramMap - Map<String, Object>
   * @return Map<String, Object>
   * @exception SQLException
   */
  public Map<String, Object> select(String statement, Map<String, Object> paramMap) throws SQLException;
  /**
   * 조회
   * @param statement - sql mapping 쿼리ID
   * @param param - String
   * @return 단건 결과 Map<String, Object>
   * @exception SQLException
   */
  public Map<String, Object> select(String statement, String param) throws SQLException;
  /**
   * 조회
   * @param statement - sql mapping 쿼리ID
   * @param param -  Map<String, Object>
   * @return 단건 결과 Object
   * @exception SQLException
   */
  public Object selectObj(String statement, Map<String, Object> paramMap) throws SQLException;
  /**
   * 조회
   * @param statement - sql mapping 쿼리ID
   * @param param - 조회할 조건
   * @return 단건 결과 Object
   * @exception SQLException
   */
  public Object selectObj(String statement, String param) throws SQLException;
  /**
   * 리스트조회
   * @param statement - sql mapping 쿼리ID
   * @param param - Map<String, Object>
   * @return 다건 결과 array list
   * @exception SQLException
   */
  public ArrayList<Map<String, Object>> selectList(String statement, Map<String, Object> paramMap) throws SQLException;
  /**
   * 리스트조회
   * @param statement - sql mapping 쿼리ID
   * @param param - String
   * @return 다건 결과 array list
   * @exception SQLException
   */
  public ArrayList<Map<String, Object>> selectList(String statement, String paramStr) throws SQLException;

}
