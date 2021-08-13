/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hanwhalife.nbm.batch.service.bm.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanwhalife.nbm.batch.dao.CommonBatchDAO;
import com.hanwhalife.nbm.batch.service.bm.NBBMBND820JService;
import com.hanwhalife.nbm.service.bm.com.ESBComponent;
import com.hanwhalife.nbm.service.bm.impl.BndTrrcServiceImpl;

/**
 * 
 * @since 2019-08-09
 * @version 1.0
 * @author 1073405
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-08-09  1073405     최초생성
 * </pre>
 */
@Service
public class NBBMBND820JServiceImpl implements NBBMBND820JService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(BndTrrcServiceImpl.class);
  
  @Autowired
  CommonBatchDAO dao;
  
  /**
   * selectESB 고객정보조회(ESB 호출)
   * 
   * @param paramMap
   * @return Map<String, Object>
   * @exception Exception
   */
  @Override
  public Map<String, Object> selectESB(final Map<String, Object> paramMap) throws Exception {
    
    /* ===========================================================================
     * >> 함수 호출 : esbCustInfoInq (고객정보조회)
     * ---------------------------------------------------------------------------
     * paramMap
     *  - MDBT_CUST_ID      | 주채무자고객ID
     * ---------------------------------------------------------------------------
     * returnMap
     * ---------------------------------------------------------------------------
     *  - RETURN_CODE       | 처리결과코드 ( 00-정상, 99-오류 ) 
     *  - RETURN_MSG        | 처리메시지 
     *  =========================================================================== */
    Map<String, Object> returnMap = ESBComponent.esbCustInfoInq(paramMap);
    
    return returnMap;
  };
}
