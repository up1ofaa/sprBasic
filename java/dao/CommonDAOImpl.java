package com.hanwhalife.nbm.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;



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


@Repository
public class CommonDAOImpl  implements CommonDAO {

  @Autowired
  @Resource(name="sqlSession")
  SqlSessionTemplate sqlSessionTemplate;

  /** 등록 */
  @Override
  public int insert(final String statement, final Map<String, Object> paramMap) throws SQLException {
    return sqlSessionTemplate.insert(statement, paramMap);
  }
  @Override
  public Object insertRetVal(final String statement, final Map<String, Object> paramMap) throws SQLException {
    if (sqlSessionTemplate.insert(statement, paramMap) < 1) {
      return 0;
    }

    return paramMap.get("retVal");
  }

  /** 수정 */
  @Override
  public int update(final String statement, final Map<String, Object> paramMap) throws SQLException {
    return sqlSessionTemplate.update(statement, paramMap);
  }

  /** 삭제 */
  @Override
  public int delete(final String statement, final Map<String, Object> paramMap) throws SQLException {
    return sqlSessionTemplate.delete(statement, paramMap);
  }

  /** 조회 */
  @Override
  public Map<String, Object> select(final String statement, final Map<String, Object> paramMap) throws SQLException {
    return (Map<String, Object>)sqlSessionTemplate.selectOne(statement, paramMap);
  }
  @Override
  public Map<String, Object> select(final String statement, final String param) throws SQLException {
    return sqlSessionTemplate.selectOne(statement, param);
  }
  @Override
  public Object selectObj(final String statement, final Map<String, Object> paramMap) throws SQLException {
    return sqlSessionTemplate.selectOne(statement, paramMap);
  }
  @Override
  public Object selectObj(final String statement, final String param) throws SQLException {
    return sqlSessionTemplate.selectOne(statement, param);
  }
  @Override
  public ArrayList<Map<String, Object>> selectList(final String statement, final Map<String, Object> paramMap) throws SQLException {
    return (ArrayList)sqlSessionTemplate.selectList(statement, paramMap);
  }
  @Override
  public ArrayList<Map<String, Object>> selectList(final String statement, final String paramStr) throws SQLException {
    return (ArrayList)sqlSessionTemplate.selectList(statement, paramStr);
  }



}
