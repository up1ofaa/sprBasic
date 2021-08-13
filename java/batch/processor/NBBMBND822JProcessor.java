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

import com.hanwhalife.nbm.batch.vo.bm.NBBMBND822JVO;

/**
 * reader 출력 타입과 writer의 입력타입을 제너릭 변수로 받는 ItemProcessor를 상속받는 NBBMBND820JProcessor클래스입니다.
 * process()에서는 reader의 출력값(NBBMBND822JV0 arg0)을 받아 처리 후 writer에 넘겨줍니다.
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
public class NBBMBND822JProcessor implements ItemProcessor<NBBMBND822JVO, NBBMBND822JVO> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NBBMBND822JProcessor.class);
  
  @Override
  public NBBMBND822JVO process(final NBBMBND822JVO arg0) throws Exception {
    LOGGER.debug("NBBMBND822JProcessor == " + arg0.toString());
    NBBMBND822JVO procMap = arg0;
    
    String bondAdmnNo   = arg0.getBondAdmnNo();     // 채권관리번호         
    String custId       = arg0.getCustId();         // 고객번호             
    String cnslDate     = arg0.getCnslDate();       // 상담일자             
    String cnslDttm     = arg0.getCnslDttm();       // 상담일시             
    String atvtDvcd     = arg0.getAtvtDvcd();       // 활동구분코드         
    String cnslMthdDvcd = arg0.getCnslMthdDvcd();   // 상담방법구분코드     
    String cnslPrtrDvcd = arg0.getCnslPrtrDvcd();   // 상담관계자구분코드   
    String prtrName     = arg0.getPrtrName();       // 관계자명             
    String custCnno     = arg0.getCustCnno();       // 고객연락처           
    String cnslReslCode = arg0.getCnslReslCode();   // 상담결과코드         
    String promDttm     = arg0.getPromDttm();       // 약속일시             
    String cntaCnslCten = arg0.getCntaCnslCten();   // 접촉상담내용         
    String noteCten     = arg0.getNoteCten();       // 비고내용             
    String etc          = arg0.getEtc();            // 예비                 
    String mpno         = "BMBND822";               // 처리자사번           
    String pgmId        = "BMBND822";               // 프로그램ID           
    
    procMap.setBondAdmnNo   (bondAdmnNo.trim());
    procMap.setCustId       (custId.trim());
    procMap.setCnslDate     (cnslDate.trim());
    procMap.setCnslDttm     (cnslDttm.trim());
    procMap.setAtvtDvcd     (atvtDvcd.trim());
    procMap.setCnslMthdDvcd (cnslMthdDvcd.trim());
    procMap.setCnslPrtrDvcd (cnslPrtrDvcd.trim());
    procMap.setPrtrName     (prtrName.trim());
    procMap.setCustCnno     (custCnno.trim());
    procMap.setCnslReslCode (cnslReslCode.trim());
    procMap.setPromDttm     (promDttm.trim());
    procMap.setCntaCnslCten (cntaCnslCten.trim());
    procMap.setNoteCten     (noteCten.trim());
    procMap.setEtc          (etc.trim());
    procMap.setMpno         (mpno.trim());
    procMap.setPgmId        (pgmId.trim());
    
    return procMap;
  }
}
