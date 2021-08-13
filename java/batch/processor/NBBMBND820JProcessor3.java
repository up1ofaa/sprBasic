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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.hanwhalife.nbm.batch.service.bm.NBBMBND820JService;
import com.hanwhalife.nbm.batch.vo.bm.NBBMBND820JVO3;

/**
 * reader 출력 타입과 writer의 입력타입을 제너릭 변수로 받는 ItemProcessor를 상속받는 NBBMBND820JProcessor클래스입니다.
 * process()에서는 reader의 출력값(NBBMBND820JVO arg0)을 받아 처리 후 writer에 넘겨줍니다.
 * null을 리턴하면 write하지 않습니다.
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
public class NBBMBND820JProcessor3 implements ItemProcessor<NBBMBND820JVO3, NBBMBND820JVO3> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NBBMBND820JProcessor3.class);
  private JobExecution jobExecuton;
  private StepExecution stepExecuton;
  
  @Autowired
  NBBMBND820JService batchService;
  
  @Override
  public NBBMBND820JVO3 process(final NBBMBND820JVO3 arg0) throws Exception {
    LOGGER.debug("NBBMBND820JProcessor3 == " + arg0.toString());
    NBBMBND820JVO3 procMap = arg0;
    
    Map<String, Object> paramMap = new HashMap<String, Object>();
    String interfaceId   = arg0.getInterfaceId();    // 채권관리번호         
    String stddYmd       = arg0.getStddYmd();        // 고객번호             
    String rowCnt        = arg0.getRowCnt();         // 상담일자             
    String state         = arg0.getState();          // 상담일시             
    String extraInfo     = arg0.getExtraInfo();      // 활동구분코드         
    String postFix       = arg0.getPostFix();        // 상담방법구분코드     
    String filePath      = arg0.getFilePath();       // 상담관계자구분코드   
    String fileNm        = arg0.getFileNm();         // 관계자명             
      
    Path path =Paths.get(filePath);
    
    long lineCount=Files.lines(path).count();
    rowCnt=Long.toString(lineCount);
    
    procMap.setInterfaceId   (interfaceId.trim());
    procMap.setStddYmd       (stddYmd.trim());
    procMap.setRowCnt        (rowCnt.trim());
    procMap.setState         (state.trim());
    procMap.setExtraInfo     (extraInfo.trim());
    procMap.setPostFix       (postFix.trim());
    procMap.setFilePath       (filePath.trim());
    procMap.setFileNm       (fileNm.trim());

    
    return procMap;
  }
  
  
  @BeforeStep
  public void beforeStep(final StepExecution stepExecution) {
    stepExecuton = stepExecution;
    jobExecuton = stepExecution.getJobExecution();
    LOGGER.debug("NBBMBND820JProcessor3 beforeStep getExitStatus == " + stepExecuton.getExitStatus() );
  }
  
  @AfterStep
  public void afterStep(final StepExecution stepExecution) {
    stepExecuton = stepExecution;
    jobExecuton = stepExecution.getJobExecution();
    // 다음 Step 데이터
    jobExecuton.getExecutionContext().put("arg1", stepExecuton.getJobParameters().getString("arg1"));
    LOGGER.debug("NBBMBND820JProcessor3 afterStep getExitStatus == " + stepExecuton.getExitStatus() );
  }
}
