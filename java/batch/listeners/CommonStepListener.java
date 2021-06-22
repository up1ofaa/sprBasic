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
package com.hanwhalife.nbm.batch.listeners;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.hanwhalife.nbm.batch.service.cm.CommonBatchService;

/**
 * <b>Description:</b><p>
 * 구현 클래스의 설명을 입력합니다.
 * @since 2019-07-29
 * @version 1.0
 * @author 1073302
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-07-29  1073302     최초생성
 * </pre>
 */
public class CommonStepListener implements StepExecutionListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonStepListener.class);
  
  @Autowired
  CommonBatchService commonBatchService;
  
  /* (non-Javadoc)
   * @see org.springframework.batch.core.StepExecutionListener#beforeStep(org.springframework.batch.core.StepExecution)
   */
  @Override
  public void beforeStep(final StepExecution arg0) {
    // TODO Auto-generated method stub
    LOGGER.debug("beforeStep === " + arg0.getJobExecution().getJobInstance().getJobName());
    LOGGER.debug("beforeStep === " + arg0.toString());
  }
  
  /* (non-Javadoc)
   * @see org.springframework.batch.core.StepExecutionListener#afterStep(org.springframework.batch.core.StepExecution)
   */
  @Override
  public ExitStatus afterStep(final StepExecution arg0) {
    // TODO Auto-generated method stub
    String jobName = arg0.getJobExecution().getJobInstance().getJobName();
    LOGGER.debug("afterStep === " + jobName);
    LOGGER.debug("afterStep === " + arg0.toString());
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("BTCH_JOB_ID", jobName);
    paramMap.put("LAOT_STDD_DATE", new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(arg0.getStartTime()));
    paramMap.put("BTCH_STAT", ExitStatus.EXECUTING.getExitCode()); // STANDBY/EXECUTING/FAILED/COMPLETED
    paramMap.put("DLNG_CNT", arg0.getWriteCount());
    paramMap.put("MPNO", "0000000");
    commonBatchService.updateBatchInfoDlngCnt(paramMap);
    return arg0.getExitStatus();
  }

}
