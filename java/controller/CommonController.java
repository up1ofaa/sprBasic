/*
 * Copyright 2008-2009 the original author or authors.
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
package com.hanwhalife.nbm.controller.cm;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hanwhalife.nbm.service.cm.CommonService;
import com.hanwhalife.nbm.service.cm.com.BatchExecutionComponent;
import com.hanwhalife.nbm.util.DateUtil;
import com.hanwhalife.nbm.util.PropertyFactory;
import com.hanwhalife.nbm.util.Result;
import com.hanwhalife.nbm.util.StringUtil;
import com.hanwhalife.nbm.vo.cm.BatchExecutionVO;

/**
 * <b>Description:</b>
 * <p>
 * CommonController 공통 컨트롤러 입니다.
 * 
 * @since 2019-05-21
 * @version 1.0
 * @author 1073302
 * @see
 * 
 *      <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-05-21  1073302     최초생성
 *      </pre>
 */
@RestController
public class CommonController {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonController.class);

  @Autowired
  CommonService commonService;
  
  @Autowired
  @Resource(name="dataSource")
  BasicDataSource dataSource;
  
  /**
   * 공통그룹 조회
   * 
   * @param request
   *          - HttpServletRequest
   * @return Map<String, Object>
   * @exception Exception
   */
  @RequestMapping(value = "/cm/selectCommonGroup.sbm", method = RequestMethod.POST)
  public Map<String, Object> selectCommonGroup(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    return commonService.selectCommonGroup(param).getResult();
  }

  /**
   * 코드그룹 저장
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @RequestMapping(value = "/cm/saveCommonGrp.sbm", method = RequestMethod.POST)
  public Map<String, Object> saveCommonGrp(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    LOGGER.debug("saveCommonGrp == " + param.toString());
    return commonService.saveCommonGrp(param).getResult();
  }

  /**
   * 그룹코드세부 저장
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @RequestMapping(value = "/cm/saveCommonGrpCode.sbm", method = RequestMethod.POST)
  public Map<String, Object> saveCommonGrpCode(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    return commonService.saveCommonGrpCode(param).getResult();
  }

  /**
   * 코드그룹세부 리스트 조회
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @RequestMapping(value = "/cm/selectCommonCodeList.sbm", method = RequestMethod.POST)
  public Map<String, Object> selectCommonGroupCodeList(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    return commonService.selectCommonGroupCode(param).getResult();
  }

  /**
   * 공통모듈 - 코드그룹세부 리스트 조회
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @RequestMapping(value = "/cm/selectCommonCode.sbm", method = RequestMethod.POST)
  public Map<String, Object> selectCommonCode(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    LOGGER.debug("selectCommonCode == " + param.toString());
    return commonService.selectCommonCode(param).getResult();
  }

  /**
   * 사용자메세지 리스트 조회
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @RequestMapping(value = "/cm/selectMessageCode.sbm", method = RequestMethod.POST)
  public Map<String, Object> selectMessageCode(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    LOGGER.debug("selectMessageCode == " + param.toString());
    return commonService.selectMessageCode(param).getResult();
  }

  /**
   * 사용자메세지 저장
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @RequestMapping(value = "/cm/insertMsgCode.sbm", method = RequestMethod.POST)
  public Map<String, Object> insertMsgCode(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    LOGGER.debug("insertMsgCode == " + param.toString());
    return commonService.insertMsgCode(param).getResult();
  }

  /**
   * 사용자메세지 삭제
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @RequestMapping(value = "/cm/deleteMsgCode.sbm", method = RequestMethod.POST)
  public Map<String, Object> deleteMsgCode(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    LOGGER.debug("deleteMsgCode == " + param.toString());
    return commonService.deleteMsgCode(param).getResult();
  }
  
  /**
   * 거래로그 리스트 조회
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @RequestMapping(value = "/cm/selectTxLog.sbm", method = RequestMethod.POST)
  public Map<String, Object> selectTxLog(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    LOGGER.debug("selectTxLog == " + param.toString());
    return commonService.selectTxLog(param).getResult();
  }
  
  /**
   * 파일 업로드
   * @param request - HttpServletRequest
   * @return 
   * @return model - Model
   * @exception Exception
   */
  @RequestMapping(value = "/cm/fileUpload.do", method=RequestMethod.POST)
  public Map<String, Object> fileUpload(final HttpServletRequest request) throws Exception {
    LOGGER.debug("fileUpload === ");
    String uploadPath       = PropertyFactory.getProperty("filePath") + DateUtil.getToday();
    File dir = new File(uploadPath);
    
    if (dir.exists() == false) {
      dir.mkdirs();
    }
    
    String saveFileName     = null;
    String uploadFileName   = null;
    long uploadFileSize     = 0;
    String OriginalfileName = null;
    String callbackFn       = "parent." + request.getParameter("CALLBACK");
    
    MultipartHttpServletRequest multiPartRequest = (MultipartHttpServletRequest) request;
    
    Iterator<String> iter = multiPartRequest.getFileNames();
    int index = 1;
    while (iter.hasNext()) {
      uploadFileName      = iter.next();
      MultipartFile mfile = (MultipartFile) multiPartRequest.getFile(uploadFileName);
      uploadFileSize      = mfile.getSize();
      OriginalfileName    = mfile.getOriginalFilename();
      
      String fileExtension = OriginalfileName.substring(OriginalfileName.lastIndexOf("."));
      String fileName      = OriginalfileName.substring(0, OriginalfileName.lastIndexOf("."));
      saveFileName = uploadPath + "/" + fileName + fileExtension;
      index++;
      LOGGER.debug("saveFileName = " + saveFileName);
      mfile.transferTo(new File(saveFileName));
    }

    Map<String, Object> resultMap = new HashMap<String, Object>();
    resultMap.put("CALLBACK", callbackFn);
    resultMap.put("FILE_ID", "");

    Result result = new Result();
    result.setData("resultMap", resultMap);
    result.setMsg("S0016");
    return result.getResult();
              
  }
  
  /**
   * 이미지 시스템 업로드
   * 
   * @param request
   *          - HttpServletRequest
   * @return void
   * @exception Exception
   */
  @RequestMapping(value = "/cm/imgFileUpload.do")
  public Map<String, Object> imgFileUpload(final HttpServletRequest request) throws Exception {
    LOGGER.debug("imgFileUpload == " + request.getMethod());
    return commonService.imgFileUpload(request).getResult();
    
  }
  
  /**
   * 이미지 시스템 업로드 리스트 조회
   * 
   * @param request
   *          - HttpServletRequest
   * @return void
   * @exception Exception
   */
  @RequestMapping(value = "/cm/selectImgFileUpload.sbm", method=RequestMethod.POST)
  public Map<String, Object> selectImgFileUpload(final HttpServletRequest request) throws Exception {
    LOGGER.debug("selectImgFileUpload == " + request.getMethod());
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    return commonService.selectImgFileUpload(param).getResult();
    
  }
  
  /**
   * 출력물관리 리스트 조회
   * 
   * @param request
   *          - HttpServletRequest
   * @return Map<String, Object>
   * @exception Exception
   */
  @RequestMapping(value = "/cm/selectReportInfo.sbm", method = RequestMethod.POST)
  public Map<String, Object> selectReportInfo(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    LOGGER.debug("selectReportInfo == " + param.toString());
    return commonService.selectReportInfo(param).getResult();
  }
  
  /**
   * 배치이력 리스트 조회
   * 
   * @param request
   *          - HttpServletRequest
   * @return Map<String, Object>
   * @exception Exception
   */
  @RequestMapping(value = "/cm/selectBatchInfo.sbm", method = RequestMethod.POST)
  public Map<String, Object> selectBatchInfo(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    LOGGER.debug("selectBatchInfo == " + param.toString());
    return commonService.selectBatchInfo(param).getResult();
  }
  
  /**
   * 온배치 실행
   * 
   * @param request
   *          - HttpServletRequest
   * @return Map<String, Object>
   * @exception Exception
   */
  @RequestMapping(value = "/cm/executeBatch.sbm", method = RequestMethod.POST)
  public Map<String, Object> executeBatch(final HttpServletRequest request) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    Result result = new Result();
    LOGGER.debug("executeBatch == " + param.toString());
    Map<String, Object> dma_executeBatch = (Map<String, Object>) param.get("dma_executeBatch");
    //배치VO 세팅
    BatchExecutionVO batchExecutionVO = new BatchExecutionVO();
    String shllName = (String)dma_executeBatch.get("SHLL_NAME");
    batchExecutionVO.setBatchShName(shllName);
    batchExecutionVO.setArgu1(StringUtil.nullToEmpty(dma_executeBatch.get("ARGU_1")));
    batchExecutionVO.setArgu2(StringUtil.nullToEmpty(dma_executeBatch.get("ARGU_2")));
    batchExecutionVO.setArgu3(StringUtil.nullToEmpty(dma_executeBatch.get("ARGU_3")));
    batchExecutionVO.setArgu4(StringUtil.nullToEmpty(dma_executeBatch.get("ARGU_4")));
    batchExecutionVO.setArgu5(StringUtil.nullToEmpty(dma_executeBatch.get("ARGU_5")));
    // 배치 실행
    BatchExecutionComponent.executeBatch(batchExecutionVO);
    result.setMsg("S0023", shllName);
    return result.getResult();
  }
  
  @RequestMapping(value = "/cm/executeQuery.sbm")
  public Map<String, Object> executeQuery(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    Result result = new Result();
    LOGGER.debug("executeQuery == " + param.toString());
    Map<String, Object> dma_msg = (Map<String, Object>) param.get("dma_msg");
    
    Connection connection = dataSource.getConnection();
    Statement statement =  connection.createStatement();
    String sql = StringUtil.nullToEmpty((String)dma_msg.get("SQL")); 
    int cnt = 0;
    try {
      if ("".equals(sql) == false) {
        cnt = statement.executeUpdate(sql);
        LOGGER.debug("executeUpdate count == " + cnt);
        connection.commit();
      }
    } catch (Exception ex) {
      // TODO: handle exception
      connection.rollback();
      throw ex;
    } finally {
      statement.close();
      connection.close();
    }
    result.setMsg("S0005", cnt + "건의 처리가 완료");
    
    return result.getResult();
  }
  
}
