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
package com.hanwhalife.nbm.batch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.hanwhalife.nbm.batch.vo.NBCMTMP001JVO;

/**
 * <b>Description:</b><p>
 * reader 출력 타입과 writer의 입력타입을 제너릭변수로 받는   ItemProcessor를 상속받는 NBCMTMP001JProcessor클래스입니다.
 * process()에서는 reader의 출력값(NBCMTMP001JVO arg0)을 받아 처리 후 writer에 넘겨줍니다. null을 리턴하면 write하지 않습니다.
 * @since 2019-07-22
 * @version 1.0
 * @author 1073302
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-07-22  1073302     최초생성
 * </pre>
 */

public class NBCMTMP001JProcessor implements ItemProcessor<NBCMTMP001JVO, NBCMTMP001JVO> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NBCMTMP001JProcessor.class);
  /* (non-Javadoc)
   * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
   */
  @Override
  public NBCMTMP001JVO process(final NBCMTMP001JVO arg0) throws Exception {
    // TODO Auto-generated method stub
    LOGGER.debug("NBCMTMP001JProcessor == " + arg0.toString());
    NBCMTMP001JVO procMap = arg0;

    procMap.setPgmId("SYS");
    
    return procMap;
  }

}
