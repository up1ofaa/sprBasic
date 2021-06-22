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
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
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
public class CommonJobListener implements JobExecutionListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonJobListener.class);
  
  @Autowired
  CommonBatchService commonBatchService;
  
  /* (non-Javadoc)
   * @see org.springframework.batch.core.JobExecutionListener#afterJob(org.springframework.batch.core.JobExecution)
   */
  @Override
  public void afterJob(final JobExecution arg0) {
    // TODO Auto-generated method stub
    LOGGER.debug("afterJob === " + arg0.toString());
    
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("LAOT_STDD_DATE", new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(arg0.getStartTime()));
    paramMap.put("BTCH_JOB_ID", arg0.getJobInstance().getJobName());
    paramMap.put("MPNO", "0000000");
    paramMap.put("DLNG_END_TIME", new SimpleDateFormat("HHmmss", Locale.KOREA).format(arg0.getEndTime()));
    paramMap.put("BTCH_STAT", arg0.getExitStatus().getExitCode()); // STANDBY/EXECUTING/FAILED/COMPLETED
    paramMap.put("DLNG_DURA", " ");
    LOGGER.debug("beforeJob paramMap === " + paramMap.toString());
    commonBatchService.updateBatchInfo(paramMap);
  }

  /* (non-Javadoc)
   * @see org.springframework.batch.core.JobExecutionListener#beforeJob(org.springframework.batch.core.JobExecution)
   */
  @Override
  public void beforeJob(final JobExecution arg0) {
    // TODO Auto-generated method stub
    LOGGER.debug("beforeJob === " + arg0.getJobParameters().getParameters().toString());
    LOGGER.debug("beforeJob arg0 === " + arg0.toString());
    Map<String, Object> paramMap = new HashMap<String, Object>();
    JobParameters jobParams = arg0.getJobParameters();
    
    paramMap.put("LAOT_STDD_DATE", new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(arg0.getStartTime()));
    paramMap.put("BTCH_JOB_ID", arg0.getJobInstance().getJobName());
    paramMap.put("MPNO", "0000000");
    paramMap.put("PGM_ID", "SYSTEM");
    paramMap.put("BTCH_PGM_ID", arg0.getJobInstance().getJobName());
    paramMap.put("DLNG_STRT_TIME", new SimpleDateFormat("HHmmss", Locale.KOREA).format(arg0.getStartTime()));
    paramMap.put("DLNG_END_TIME", " ");
    paramMap.put("ARGU_1", jobParams.getString("arg1", ""));
    paramMap.put("ARGU_2", jobParams.getString("arg2", ""));
    paramMap.put("ARGU_3", jobParams.getString("arg3", ""));
    paramMap.put("ARGU_4", jobParams.getString("arg4", ""));
    paramMap.put("ARGU_5", jobParams.getString("arg5", ""));
    paramMap.put("BTCH_DVSN", "1");
    paramMap.put("BTCH_STAT", ExitStatus.EXECUTING.getExitCode()); // STANDBY/EXECUTING/FAILED/COMPLETED
    paramMap.put("DLNG_DURA", " ");
    paramMap.put("DLNG_CNT", 0);
    paramMap.put("BTCH_QUE_ID", arg0.getId());
    paramMap.put("SHLL_NAME", jobParams.getString("shllName", ""));
    LOGGER.debug("beforeJob paramMap === " + paramMap.toString());
    commonBatchService.insertBatchInfo(paramMap);
  }

}
