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

import com.hanwhalife.nbm.batch.vo.bm.NBBMBND821JVO;

/**
 * reader 출력 타입과 writer의 입력타입을 제너릭 변수로 받는 ItemProcessor를 상속받는 NBBMBND820JProcessor클래스입니다.
 * process()에서는 reader의 출력값(NBBMBND821JV0 arg0)을 받아 처리 후 writer에 넘겨줍니다.
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
public class NBBMBND821JProcessor implements ItemProcessor<NBBMBND821JVO, NBBMBND821JVO> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NBBMBND821JProcessor.class);
  
  @Override
  public NBBMBND821JVO process(final NBBMBND821JVO arg0) throws Exception {
    LOGGER.debug("NBBMBND821JProcessor == " + arg0.toString());
    NBBMBND821JVO procMap = arg0;
//    String bondAdmnNo  = procMap.getBondAdmnNo();    // 채권관리번호
//    String bdamEndDate = procMap.getBdamEndDate();    // 채권관리종결일자
//    String bondEndRscd = procMap.getBondEndRscd();    // 채권종결사유코드
//    String etc         = procMap.getEtc();            // 예비 : 코드명
//    
//    procMap.setBondAdmnNo(bondAdmnNo);      // 채권관리번호
//    procMap.setBdamEndDate(bdamEndDate);    // 채권관리종결일자
//    procMap.setBondEndRscd(bondEndRscd);    // 채권종결사유코드
//    procMap.setEtc(etc);                    // 예비 : 코드명
    procMap.setMpno("BMBND821");            // 처리자사번
    
    return procMap;
  }
}
