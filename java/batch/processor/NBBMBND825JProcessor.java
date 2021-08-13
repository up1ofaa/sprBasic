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

import com.hanwhalife.nbm.batch.vo.bm.NBBMBND825JVO;

/**
 * reader 출력 타입과 writer의 입력타입을 제너릭 변수로 받는 ItemProcessor를 상속받는 NBBMBND820JProcessor클래스입니다.
 * process()에서는 reader의 출력값(NBBMBND825JV0 arg0)을 받아 처리 후 writer에 넘겨줍니다.
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
public class NBBMBND825JProcessor implements ItemProcessor<NBBMBND825JVO, NBBMBND825JVO> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NBBMBND825JProcessor.class);
  
  @Override
  public NBBMBND825JVO process(final NBBMBND825JVO arg0) throws Exception {
    LOGGER.debug("NBBMBND825JProcessor == " + arg0.toString());
    NBBMBND825JVO procMap = arg0;
    String dlgsDate       = procMap.getDlgsDate();    // 입금일자
    String bondAdmnNo     = procMap.getBondAdmnNo();  // 채권번호
    String fnplPrno       = procMap.getFnplPrno();    // 고유번호
    String custName       = procMap.getCustName();    // 성명
    String dlgsAmt        = procMap.getDlgsAmt();     // 입금액

    procMap.setDlgsDate(dlgsDate);        // 입금일자
    procMap.setBondAdmnNo(bondAdmnNo);    // 채권번호
    procMap.setFnplPrno(fnplPrno);        // 고유번호
    procMap.setCustName(custName);        // 성명
    procMap.setDlgsAmt(dlgsAmt);          // 입금액
    
    return procMap;
  }
}
