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
package com.hanwhalife.nbm.batch.tasklet;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * <b>Description:</b><p>
 * ESB 배치 호출 tasklet
 * @since 2019-09-17
 * @version 1.0
 * @author 이진섭(1073421)
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-09-17  이진섭(1073421)     최초생성
 * </pre>
 */
public class ClogErpBatchTasklet implements Tasklet {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClogErpBatchTasklet.class);
      


  @Override
  public RepeatStatus execute (final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
     
    try {
      String urlStr="";
      //현재 접속한 서버의 ip주소확인하기
      InetAddress local;
      local = InetAddress.getLocalHost();
      String idAddr = local.getHostAddress();
      String parmaStr;
     
      LOGGER.debug("NBRCRCC100JProcessor2 == "+idAddr);
      
      if(idAddr.equals("10.10.153.190")) {
        //urlStr="http://vdevesbap.hanwhalifefs.com:7602";
        urlStr="http://vdevesbap.hanwhalifefs.com:7602/bat/EaiBatCall";
        //urlStr="vdevesbap.hanwhalifefs.com:7602/bat/EaiBatCall";
        //urlStr="http://vdevesbap.hanwhalifefs.com/bat/EaiBatCall?eaibatMsg=NBMERPDD0001       202106110000000001 @@";
        //urlStr="http://vdevesbap.hanwhalifefs.com:7602/bat/EaiBatCall?eaibatMsg=NBMERPDD0001       202106110000000001 @@";       
      }else {
        //urlStr="https://esb.hanwhalifefs.com:8602/bat/EaiBatCall?eaibatMsg=NBMERPDD0001       202106110000000001 @@";  
        urlStr="https://esb.hanwhalifefs.com:8602/bat/EaiBatCall";
      }
      String result="";
      LOGGER.debug("url   : "+urlStr);
      //urlStr=URLEncoder.encode(urlStr,"UTF-8");
      URL url = new URL(urlStr);
      HttpURLConnection conn =(HttpURLConnection) url.openConnection();    

      JSONObject jdtb = new JSONObject ();  
      jdtb.put("eaibatMsg","NBMERPDD0001       202106110000000001 @@");
      
      StringBuilder postData= new StringBuilder(); 
      postData.append(URLEncoder.encode("eaibatMsg","utf-8"));
      postData.append('=');
      postData.append(URLEncoder.encode(String.valueOf("NBMERPDD0001       202106110000000001 @@"),"utf-8"));   
      

      //parmaStr= jdtb.toString();
      parmaStr=postData.toString();

      LOGGER.debug("SEND JSONO object: [" + jdtb.toString().replace("\\", "") );
      
      //--------------------------
      //   전송 모드 설정 - 기본적인 설정
      //--------------------------
      conn.setDefaultUseCaches(false);
      conn.setDoInput(true); // 서버에서 읽기 모드 지정
      conn.setDoOutput(true); // 서버로 쓰기 모드 지정
      conn.setRequestMethod("POST"); // 전송 방식은 POST
      //conn.setRequestProperty("content-type", "application/json; utf-8");
      //conn.setRequestProperty("content-type", "text/xml; charset:UTF-8;");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      //conn.setRequestProperty("Accept", "application/json");
      //conn.setRequestProperty("eaibatMsg", "NBMERPDD0001       202106110000000001 @@");
      conn.setConnectTimeout(10000);
      conn.setReadTimeout(10000);
      conn.setUseCaches(false);
      conn.setDefaultUseCaches(false);


/*      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
      wr.write(parmaStr);
      //wr.write(parmaStr.replace("\\", ""));
      wr.flush();
  */    
      conn.getOutputStream().write(parmaStr.getBytes("utf-8"));
      
      
      StringBuilder sb = new StringBuilder();
      int HttpResult = conn.getResponseCode();
      if (HttpResult == HttpURLConnection.HTTP_OK) {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line = null;
        while((line = br.readLine()) != null) {
          //sb.append(line + "\n");
          sb.append(line);
        }
        br.close();
        LOGGER.debug("StringBuilder : [" + sb.toString());
      }else {
        LOGGER.debug("conn.getResponseCode    : [" + conn.getResponseCode());
        LOGGER.debug("conn.getResponseMessage : [" + conn.getResponseMessage());
      }
      
//      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
//      String str = null;
//      while ((str = in.readLine()) != null) {
//        builder.append(str);
//      }
      conn.disconnect();      

      result =sb.toString();
      

      System.out.println("HTTP body   :"+result); 
      LOGGER.debug("HTTP body   : "+result);
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.debug("Exception: "+e.getMessage());
    }
    
    return RepeatStatus.FINISHED;
    
  }  
  

  
}
