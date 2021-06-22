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
package com.hanwhalife.nbm.batch.runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.NestedRuntimeException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
/**
 * <b>Description:</b><p>
 * 구현 클래스의 설명을 입력합니다.
 * @since 2019-10-01
 * @version 1.0
 * @author 1073302
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-10-01  1073302     최초생성
 * </pre>
 */
public final class CommandLineJobRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineJobRunner.class);

  /**
   * singleton 오류 대응
   */
  private CommandLineJobRunner() {
    // singleton 오류 대응
  }

  public static void main(final String[] args) {

    // default locale 세팅
    Locale.setDefault(Locale.KOREAN);
    LOGGER.info("Locale : " + Locale.getDefault());

    // //////////////////////////////////////////////////
    // argument 처리
    // //////////////////////////////////////////////////
    String jobName = args[0];
    String[] parameters = createParamters(args);
    LOGGER.info("jobName : {}", jobName);
    LOGGER.info("parameters : {}", parameters);

    Assert.hasText(jobName, "첫번째 인수인 jobName이 없습니다.");

    // //////////////////////////////////////////////////
    // Spring 설정 & 배치 실행
    // //////////////////////////////////////////////////

    ConfigurableApplicationContext context = null;

    try {
      // 배치 ID xml 확인
      String jobNamePath = createJobNamePath(jobName);

      // ApplicationContext 생성
      context = new ClassPathXmlApplicationContext(jobNamePath);
      JobLauncher launcher = context.getBean(JobLauncher.class);
      JobParametersConverter jobParametersConverter = new DefaultJobParametersConverter();

      Assert.notNull(launcher, "A JobLauncher must be provided.  Please add one to the configuration.");

      Job job = (Job) context.getBean(jobName);
      JobParameters jobParameters = jobParametersConverter
          .getJobParameters(StringUtils.splitArrayElementsIntoProperties(parameters, "="));

      Assert.notNull(job, "A Job must be provided.  Please add one to the configuration.");

      // Batch Job을 실행한다.
      JobExecution jobExecution = launcher.run(job, jobParameters);

      // 실행 로그
      LOGGER.warn("실행완료");
      LOGGER.warn("CommandLineRunner's Job Information");
      LOGGER.warn("jobName=" + jobExecution.getJobInstance().getJobName());
      LOGGER.warn("jobParamters=" + jobParameters.toString());
      LOGGER.warn("jobExecutionTime="
          + (jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime()) / 1000f + "s");

    } catch (NestedRuntimeException exception) {
      LOGGER.debug("CommandLineRunner's Error::{}", exception);
    } catch (RuntimeException exception) {
      LOGGER.debug("CommandLineRunner's Error::{}", exception);
    } catch (JobExecutionAlreadyRunningException exception) {
      LOGGER.debug("CommandLineRunner's Error::{}", exception);
    } catch (JobInstanceAlreadyCompleteException exception) {
      LOGGER.debug("CommandLineRunner's Error::{}", exception);
    } catch (JobParametersInvalidException exception) {
      LOGGER.debug("CommandLineRunner's Error::{}", exception);
    } catch (org.springframework.batch.core.repository.JobRestartException exception) {
      LOGGER.debug("CommandLineRunner's Error::{}", exception);
    } catch (Exception exception) {
      LOGGER.debug("CommandLineRunner's Error::{}", exception);
    } finally {
      if (context != null) {
        context.close();
        LOGGER.debug("ApplicationContex closed");
      }
    }
  }

  /**
   * 
   * @param args
   * @return
   */
  public static String createJobNamePath(final String jobName) {
    String jobNamePath = "/batch/job/";
//    jobNamePath += jobName.toLowerCase().substring(0, 2) + "/";
    jobNamePath += jobName.substring(2, 4) + "/";
    jobNamePath += jobName + ".xml";

    // example 임시
    if (jobNamePath.indexOf("TMP") > -1) {
      jobNamePath = "/batch/job/sample/" + jobName + ".xml";
    }
    LOGGER.debug("jobNamePath : {}", jobNamePath);

    return jobNamePath;
  }

  /**
   * 
   * @param args
   * @return
   */
  public static String[] createParamters(final String[] args) {

    List<String> newargs = new ArrayList<String>(Arrays.asList(args));
    // newargs.add("timestamp=" + new Date().getTime());

    List<String> params = new ArrayList<String>();
 // jobparameter가 같을 경우 spring batch가 실행되지 않으므로 미실행 방지용으로 임시 job parameter
    params.add("d=" + Calendar.getInstance().getTimeInMillis());
    int count = 0;
    for (String arg : newargs) {
      switch (count) {
      case 0:
        // jobName = arg;
        break;
      default:
        params.add(arg);
        break;
      }
      count++;
    }
    return params.toArray(new String[params.size()]);

  }
}
