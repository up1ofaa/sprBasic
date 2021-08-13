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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.hanwha.common.endecrpt.VO.EncDecVO;
import com.hanwha.common.endecrpt.manager.PrivateInfoEncDecManager;
import com.hanwhalife.nbm.batch.service.bm.NBBMBND820JService;
import com.hanwhalife.nbm.batch.vo.bm.NBBMBND820JVO;

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
public class NBBMBND820JProcessor implements ItemProcessor<NBBMBND820JVO, NBBMBND820JVO> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NBBMBND820JProcessor.class);
  private JobExecution jobExecuton;
  private StepExecution stepExecuton;
  
  @Autowired
  NBBMBND820JService batchService;
  
  @Override
  public NBBMBND820JVO process(final NBBMBND820JVO arg0) throws Exception {
    LOGGER.debug("NBBMBND820JProcessor == " + arg0.toString());
    NBBMBND820JVO procMap = arg0;
    
    Map<String, Object> paramMap = new HashMap<String, Object>();
    String rsdnRgstNo     = ""; // 주민등록번호
    String housTelNoDdd   = ""; // 자택전화번호_지역
    String housTelNoBth   = ""; // 자택전화번호_국
    String housTelNoDtal  = ""; // 자택전화번호_세부
    String hpBsmn         = ""; // 핸드폰_사업자
    String hpBrh          = ""; // 핸드폰_국 
    String hpDtal         = ""; // 핸드폰_세부
    String housFrntAddr   = ""; // 자택앞주소 
    String housDtalAddr   = ""; // 자택세부주소
    String wkplFrntAddr   = ""; // 직장앞주소 
    String wkplDtalAddr   = ""; // 직장세부주소
    String etc            = "";
    
    
    /*********************************************************
     * 1. 복호화처리 ( 고객상세주소, 고객연락처 )
     *********************************************************/
    PrivateInfoEncDecManager encManager = new PrivateInfoEncDecManager();
    EncDecVO encVo = new EncDecVO();
    
    //procMap.setCustAddrTot(procMap.getCustAddr()
    //                       + encManager.decryptAddr(procMap.getCustDtalAddr(), encVo, "EUC-KR")); // 고객주소
    //procMap.setCustCnno(encManager.decryptTel(procMap.getCustCnno(), encVo));                     // 고객연락처
    
    /*********************************************************
     * 2. 모듈호출 ( 고객정보조회 )
     *********************************************************/
    /* ===========================================================================
     * >> 함수 호출 : selectESB (고객정보조회(ESB 호출))
     * ---------------------------------------------------------------------------
     * paramMap
     *  - MDBT_CUST_ID      | 주채무자고객ID
     * ---------------------------------------------------------------------------
     * returnMap
     * ---------------------------------------------------------------------------
     *  - RETURN_CODE       | 처리결과코드 ( 00-정상, 99-오류 ) 
     *  - RETURN_MSG        | 처리메시지 
     *  =========================================================================== */
    paramMap.put("MDBT_CUST_ID", procMap.getCustId());
    /*//ESB로 주민번호, 연락처, 주소 가져오는 로직 제거=> 실제 테이블에서 가져올 예정
    Map<String, Object> returnMap = (Map<String, Object>)batchService.selectESB(paramMap);

    if ("00".equals(returnMap.get("RETURN_CODE"))) {
      rsdnRgstNo     = returnMap.get("RSDN_RGST_NO").toString();      // 주민등록번호
      housTelNoDdd   = returnMap.get("HOUS_TEL_NO_DDD").toString();   // 자택전화번호_지역
      housTelNoBth   = returnMap.get("HOUS_TEL_NO_BRH").toString();   // 자택전화번호_국
      housTelNoDtal  = returnMap.get("HOUS_TEL_NO_DTAL").toString();  // 자택전화번호_세부
      hpBsmn         = returnMap.get("HP_BSMN").toString();           // 핸드폰_사업자
      hpBrh          = returnMap.get("HP_BRH").toString();            // 핸드폰_국 
      hpDtal         = returnMap.get("HP_DTAL").toString();           // 핸드폰_세부
      housFrntAddr   = returnMap.get("HOUS_FRNT_ADDR").toString();    // 자택앞주소 
      housDtalAddr   = returnMap.get("HOUS_DTAL_ADDR").toString();    // 자택세부주소
      wkplFrntAddr   = returnMap.get("WKPL_FRNT_ADDR").toString();    // 직장앞주소 
      wkplDtalAddr   = returnMap.get("WKPL_DTAL_ADDR").toString();    // 직장세부주소
      
      stepExecuton.setExitStatus(ExitStatus.COMPLETED);
    } else {
      stepExecuton.setExitStatus(ExitStatus.FAILED);
    }
    
    procMap.setRsdnRgstNo(rsdnRgstNo);                                // 주민등록번호
    procMap.setHousTelNo(housTelNoDdd+housTelNoBth+housTelNoDtal);    // 자택전화번호
    procMap.setHpNo(hpBsmn+hpBrh+hpDtal);                             // 휴대전화번호
    procMap.setHousAddr(housFrntAddr+housDtalAddr);               // 자택주소
    procMap.setWkplAddr(wkplFrntAddr+wkplDtalAddr);               // 직장주소
    procMap.setEtc(etc);
    */
    return arg0;
  }
  
  
  @BeforeStep
  public void beforeStep(final StepExecution stepExecution) {
    stepExecuton = stepExecution;
    jobExecuton = stepExecution.getJobExecution();
    LOGGER.debug("NBBMBND820JProcessor beforeStep getExitStatus == " + stepExecuton.getExitStatus() );
  }
  
  @AfterStep
  public void afterStep(final StepExecution stepExecution) {
    stepExecuton = stepExecution;
    jobExecuton = stepExecution.getJobExecution();
    // 다음 Step 데이터
    jobExecuton.getExecutionContext().put("arg1", stepExecuton.getJobParameters().getString("arg1"));
    LOGGER.debug("NBBMBND820JProcessor afterStep getExitStatus == " + stepExecuton.getExitStatus() );
  }
}
