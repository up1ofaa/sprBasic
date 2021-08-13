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
package com.hanwhalife.nbm.batch.processor.bm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.hanwhalife.nbm.batch.vo.bm.NBBMBND823JVO;

/**
 * reader 출력 타입과 writer의 입력타입을 제너릭 변수로 받는 ItemProcessor를 상속받는 NBBMBND820JProcessor클래스입니다.
 * process()에서는 reader의 출력값(NBBMBND823JV0 arg0)을 받아 처리 후 writer에 넘겨줍니다.
 * null을 리턴하면 write하지 않습니다.
 * 
 * @since 2019-09-05
 * @version 1.0
 * @author 1073405
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-09-05  1073405     최초생성
 * </pre>
 */
public class NBBMBND823JProcessor implements ItemProcessor<NBBMBND823JVO, NBBMBND823JVO> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NBBMBND823JProcessor.class);
  
  @Override
  public NBBMBND823JVO process(final NBBMBND823JVO arg0) throws Exception {
    LOGGER.debug("NBBMBND823JProcessor == " + arg0.toString());
    NBBMBND823JVO procMap = arg0;
    
    String bondAdmnNo   = arg0.getBondAdmnNo();     // 채권관리번호  
    String chrgOrgnCode = arg0.getChrgOrgnCode();   // 담당기관코드  
    String deptName     = arg0.getDeptName();       // 부서명        
    String chgmMpno     = arg0.getChgmMpno();       // 담당자사번    
    String trcoChgmName = arg0.getTrcoChgmName();   // 위탁사담당자명
    String etc          = arg0.getEtc();            // 예비                      
    String mpno         = "BMBND823";               // 처리자사번           
    String pgmId        = "BMBND823";               // 프로그램ID           
    
    procMap.setBondAdmnNo   (bondAdmnNo.trim());
    procMap.setChrgOrgnCode (chrgOrgnCode.trim());
    procMap.setDeptName     (deptName.trim());
    procMap.setChgmMpno     (chgmMpno.trim());
    procMap.setTrcoChgmName (trcoChgmName.trim());
    procMap.setEtc          (etc.trim());
    procMap.setMpno         (mpno.trim());
    procMap.setPgmId        (pgmId.trim());
    
    return procMap;
  }
}
