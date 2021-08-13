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

import com.hanwhalife.nbm.batch.vo.bm.NBBMBND824JVO;

/**
 * reader 출력 타입과 writer의 입력타입을 제너릭 변수로 받는 ItemProcessor를 상속받는 NBBMBND820JProcessor클래스입니다.
 * process()에서는 reader의 출력값(NBBMBND824JV0 arg0)을 받아 처리 후 writer에 넘겨줍니다.
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
public class NBBMBND824JProcessor implements ItemProcessor<NBBMBND824JVO, NBBMBND824JVO> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NBBMBND824JProcessor.class);
  
  @Override
  public NBBMBND824JVO process(final NBBMBND824JVO arg0) throws Exception {
    LOGGER.debug("NBBMBND824JProcessor == " + arg0.toString());
    NBBMBND824JVO procMap = arg0;
    
    String stddYm       = arg0.getStddYm();         // 기준년월                
    String bondAdmnNo   = arg0.getBondAdmnNo();     // 채권관리번호            
    String laaiDt       = arg0.getLaaiDt();         // 최종실사일자            
    String laaiReslCten = arg0.getLaaiReslCten();   // 최종실사결과내용        
    String lafcIssuDt   = arg0.getLafcIssuDt();     // 최종초본발급일자        
    String labpDt       = arg0.getLabpDt();         // 최종기초재산일자        
    String laapInveRqdt = arg0.getLaapInveRqdt();   // 최종정밀재산조사신청일자
    String dscvPropYn   = arg0.getDscvPropYn();     // 발견재산여부            
    String ladpRpexDt   = arg0.getLadpRpexDt();     // 최종발견재산실익검토일자
//    String arinCnt      = arg0.getArinCnt();        // 연체정보건수            
//    String arinAmt      = arg0.getArinAmt();        // 연체정보금액            
//    String niceArreCnt  = arg0.getNiceArreCnt();    // NICE연체건수            
//    String niceAram     = arg0.getNiceAram();       // NICE연체금액            
    String noteCten     = arg0.getNoteCten();       // 비고내용                      
    String mpno         = "BMBND824";               // 처리자사번           
    String pgmId        = "BMBND824";               // 프로그램ID           
    
    procMap.setStddYm       (stddYm.trim());      
    procMap.setBondAdmnNo   (bondAdmnNo.trim());  
    procMap.setLaaiDt       (laaiDt.trim());      
    procMap.setLaaiReslCten (laaiReslCten.trim());
    procMap.setLafcIssuDt   (lafcIssuDt.trim());  
    procMap.setLabpDt       (labpDt.trim());      
    procMap.setLaapInveRqdt (laapInveRqdt.trim());
    procMap.setDscvPropYn   (dscvPropYn.trim());  
    procMap.setLadpRpexDt   (ladpRpexDt.trim());  
//    procMap.setArinCnt      (arinCnt.trim());     
//    procMap.setArinAmt      (arinAmt.trim());     
//    procMap.setNiceArreCnt  (niceArreCnt.trim()); 
//    procMap.setNiceAram     (niceAram.trim());    
    procMap.setNoteCten     (noteCten.trim());    
    procMap.setMpno         (mpno.trim());
    procMap.setPgmId        (pgmId.trim());
    
    return procMap;
  }
}
