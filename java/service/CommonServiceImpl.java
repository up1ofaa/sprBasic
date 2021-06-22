package com.hanwhalife.nbm.service.cm.impl;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hanwhalife.nbm.dao.CommonDAO;
import com.hanwhalife.nbm.service.cm.CommonService;
import com.hanwhalife.nbm.service.cm.com.BatchExecutionComponent;
import com.hanwhalife.nbm.service.cm.com.CommonComponent;
import com.hanwhalife.nbm.util.CommonUtil;
import com.hanwhalife.nbm.util.ConvertUtil;
import com.hanwhalife.nbm.util.Result;
import com.hanwhalife.nbm.util.StringUtil;
import com.hanwhalife.nbm.vo.cm.BatchExecutionVO;
import com.hanwhalife.nbm.vo.cm.ImgSystemVO;

/**
 * <b>Description:</b>
 * <p>
 * CommonServiceImpl
 * 
 * @since 2019-06-17
 * @version 1.0
 * @author 홍선기(1073302)
 * @see
 * 
 *      <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-06-17  1073302     최초생성
 *      </pre>
 */
@Service
public class CommonServiceImpl implements CommonService {

  private static final String NAMESPACE = "commonMapper.";
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Autowired
  CommonDAO dao;

  /*
   * 
   * */
  @Override
  public Result selectCommonGroup(final Map<String, Object> paramMap) throws Exception {
    Result result = new Result();
    Map<String, Object> dma_search = (Map<String, Object>) paramMap.get("dma_search");
    // 조회구분
    String searchTp = dma_search.get("SEARCH_TP").toString();
    // 현재건수
    int currentCnt = (int)dma_search.get("CURRENT_CNT");
    // 전체건수
    int totalCnt = 0;
    // 조회||전체조회||재조회 시 전체건수 조회
    if ("A".equals(searchTp) || "S".equals(searchTp) || "R".equals(searchTp)) {
      totalCnt = (int)dao.selectObj(NAMESPACE + "CMCOD001S001", dma_search);
    } else {
      totalCnt = (int)dma_search.get("TOTAL_CNT");
    }
    // 공통코드 리스트 조회
    ArrayList<Map<String, Object>> resultSet = dao.selectList(NAMESPACE + "CMCOD001L001", dma_search);
    result.setData("dma_search", CommonUtil.setCTS(dma_search, resultSet));
    result.setData("dlt_commonGrp", resultSet);
    // 현재 조회건수 
    currentCnt += resultSet.size();
    // 조회건수 set
    dma_search.put("CURRENT_CNT", currentCnt);
    dma_search.put("TOTAL_CNT", totalCnt);
    
    //재조회는 메시지코드 제외
    if ("R".equals(searchTp)) {
      return result;
    }
    // 조회건수 < 전체건수
    if (currentCnt < totalCnt) {
      if ("A".equals(searchTp)) {
        result.setMsg("S0008"); // 조회가 완료 되었습니다.(전체조회)
      } else {
        result.setMsg("S0007"); // 조회가 완료 되었습니다.(다음)
      }
    } else {
      result.setMsg("S0001"); // 조회가 완료 되었습니다.
    }
    return result;
  }

  @Override
  public Result selectCommonGroupCode(final Map<String, Object> paramMap) throws Exception {
    Result result = new Result();
    Map<String, Object> dma_commonGrp = (Map<String, Object>) paramMap.get("dma_commonGrpCd");
    result.setData("dlt_commonCode", dao.selectList(NAMESPACE + "CMCOD002L001", dma_commonGrp));
    return result;
  }

  @Override
  @Transactional(noRollbackFor=Exception.class)
  public Result saveCommonGrp(final Map<String, Object> paramMap) throws Exception {
    ArrayList<Map<String, Object>> paramList = (ArrayList<Map<String, Object>>) paramMap.get("dlt_commonGrp");
    Result result = new Result();
    String codeGrp = "";
    String errStr = "";
    String rowStatus = "";
    int cnt = 0;
    // 공통그룹
    for (Map<String, Object> param : paramList) {
      LOGGER.debug("param == " + param.toString());
      param.put("MPNO", paramMap.get("MPNO"));
      param.put("PGM_ID", paramMap.get("PGM_ID"));
      codeGrp = param.get("CODE_GRP").toString();
      rowStatus = param.get("rowStatus").toString();
      
      try {
        if ("C".equals(rowStatus)) { // 입력
          cnt = dao.insert(NAMESPACE + "CMCOD001I001", param);
        } else if ("U".equals(rowStatus)) { // 수정
          cnt = dao.update(NAMESPACE + "CMCOD001U001", param);
        } else if ("D".equals(rowStatus)) { // 삭제
          cnt = dao.delete(NAMESPACE + "CMCOD001D001", param);
        }
        // 처리건수
        if (cnt == 0) {
          errStr += String.format("</br>%s은(는) 처리되지 않았습니다.", codeGrp);
        }
        // 중복키 발생
      } catch (DuplicateKeyException de) {
        de.printStackTrace();
        errStr += String.format("</br>%s은(는) 이미 등록되어있는 코드그룹입니다", codeGrp);
        // 데이터 정합성 오류(부정확한 컬럼 / notnull / 바인딩오류)
      } catch (DataIntegrityViolationException de) {
        de.printStackTrace();
        errStr += String.format("</br>%s은(는) 데이터 정합성이 맞지않아 처리되지 않았습니다.", codeGrp);
      } catch (UncategorizedDataAccessException ue) {
        ue.printStackTrace();
        errStr += String.format("</br>%s은(는) 데이터 정합성이 맞지않아 처리되지 않았습니다.", codeGrp);
      }
    }
    if ("".equals(errStr)) {
      result.setMsg("S0000");
    } else {
      result.setMsg("I0003", errStr);
    }
    return result;
  }

  @Override
  @Transactional(noRollbackFor=Exception.class)
  public Result saveCommonGrpCode(final Map<String, Object> paramMap) throws Exception {
    LOGGER.debug("CommonServiceImpl saveCommonGrpCode === ");
    Result result = new Result();

    ArrayList<Map<String, Object>> paramList = (ArrayList<Map<String, Object>>) paramMap.get("dlt_commonCode");
    String codeVal = "";
    String errStr = "";
    String rowStatus = "";
    int cnt = 0;
    int size = paramList.size();
    // 공통그룹
    for (int i = 0; i < size; i++) {
      LOGGER.debug("paramList == " + paramList.get(i).toString());
      Map<String, Object> param = (Map<String, Object>) paramList.get(i);
      param.put("MPNO", paramMap.get("MPNO"));
      param.put("PGM_ID", paramMap.get("PGM_ID"));
      codeVal = param.get("CODE_VAL").toString();
      rowStatus = param.get("rowStatus").toString();
      LOGGER.debug("paramMap(" + i + ") == " + param.toString());
      try {
        if ("C".equals(rowStatus)) { // 입력
          cnt = dao.insert(NAMESPACE + "CMCOD002I001", param);
        } else if ("U".equals(rowStatus)) { // 수정
          cnt = dao.update(NAMESPACE + "CMCOD002U001", param);
        } else if ("D".equals(rowStatus)) { // 삭제
          cnt = dao.delete(NAMESPACE + "CMCOD002D001", param);
        }
        // 처리건수
        if (cnt == 0) {
          errStr += String.format("</br>%s은(는) 처리되지 않았습니다.", codeVal);
        }
        // 중복키 발생
      } catch (DuplicateKeyException de) {
        de.printStackTrace();
        errStr += String.format("</br>%s은(는) 이미 등록되어있는 코드그룹입니다", codeVal);
        // 데이터 정합성 오류(부정확한 컬럼 / notnull / 바인딩오류)
      } catch (DataIntegrityViolationException de) {
        de.printStackTrace();
        errStr += String.format("</br>%s은(는) 데이터 정합성이 맞지않아 처리되지 않았습니다.", codeVal);
      } catch (UncategorizedDataAccessException ue) {
        ue.printStackTrace();
        errStr += String.format("</br>%s은(는) 데이터 정합성이 맞지않아 처리되지 않았습니다.", codeVal);
      }
//      
//      if ("C".equals(rowStatus)) { // 입력
//        cnt = dao.insert(NAMESPACE + "CMCOD002I001", param);
//      } else if ("U".equals(rowStatus)) { // 수정
//        cnt = dao.update(NAMESPACE + "CMCOD002U001", param);
//      } else if ("D".equals(rowStatus)) { // 삭제
//        cnt = dao.delete(NAMESPACE + "CMCOD002D001", param);
//      }
    }

    // 공통코드 조회
    if (errStr.trim().isEmpty()) {
      result.setMsg("S0000");
    } else {
      result.setMsg("S0012", errStr);
    }
    return result;
  }

  /*
   * 공통모듈 - 공통코드리스트를 조회
   */
  @Override
  public Result selectCommonCode(final Map<String, Object> paramMap) throws Exception {
    Result result = new Result();
    Map<String, Object> commonCode = new HashMap<String, Object>();
    Map<String, Object> param = (Map<String, Object>) paramMap.get("dma_commonGrp");
    String[] arrCodeGrp = param.get("CODE_GRP").toString().split(",");
    String[] arrOrder = param.get("ORDER").toString().split(",");

    for (int i = 0; i < arrCodeGrp.length; i++) {
      // 코드그룹
      commonCode.put("CODE_GRP", arrCodeGrp[i]);
      // 정렬순서
      switch (arrOrder[i]) {
      case "1":
        commonCode.put("OUTP_ORD", "OUTP_ORD1");
        break;
      case "2":
        commonCode.put("OUTP_ORD", "OUTP_ORD2");
        break;
      case "3":
        commonCode.put("OUTP_ORD", "OUTP_ORD3");
        break;
      default:
        commonCode.put("OUTP_ORD", "OUTP_ORD1");
        arrOrder[i] = "1";
        break;
      }
      result.setData("dlt_" + arrCodeGrp[i] + arrOrder[i], dao.selectList(NAMESPACE + "CMCOD002L002", commonCode));
    }

    return result;
  }

  /*
   * 사용자메시지 리스트 조회
   */
  @Override
  public Result selectMessageCode(final Map<String, Object> param) throws Exception {
    Result result = new Result();
    //현재 조회건수
    int currentCnt = 0;
    //전체 조회건수
    int totalCnt = 0;
    // 조회조건
    Map<String, Object> dma_search = (Map<String, Object>) param.get("dma_search");
    
    currentCnt = ConvertUtil.strToInt(dma_search.get("CURRENT_CNT").toString());
    totalCnt = ConvertUtil.strToInt(dma_search.get("TOTAL_CNT").toString());
    
    // 다음조회가 아니면 전체건수 조회
    if ("N".equals(dma_search.get("SEARCH_TP").toString()) == false) {
      totalCnt = (int)dao.selectObj(NAMESPACE + "CMMSG001S002", dma_search);
    }
    //사용자메시지 조회
    ArrayList<Map<String, Object>> dlt_msg = dao.selectList(NAMESPACE + "CMMSG001L001", dma_search);
    
    result.setData("dma_search", CommonUtil.setCTS(dma_search, dlt_msg));
    result.setData("dlt_msg", dlt_msg);
    
    // 현재 조회건수 
    currentCnt += dlt_msg.size();
    // 조회건수 set
    dma_search.put("CURRENT_CNT", currentCnt);
    dma_search.put("TOTAL_CNT", totalCnt);
    
    // 재조회시 메시지코드 세팅안함
    if ("R".equals(dma_search.get("SEARCH_TP").toString())) {
      return result;
    }
    
    // 조회건수 < 전체건수
    if (currentCnt < totalCnt) {
      if ("A".equals(dma_search.get("SEARCH_TP").toString())) {
        result.setMsg("S0008"); // 조회가 완료 되었습니다.(전체조회)
      } else {
        result.setMsg("S0007"); // 조회가 완료 되었습니다.(다음)
      }
    } else {
      result.setMsg("S0001"); // 조회가 완료 되었습니다.
    }
    return result;
  }

  /*
   * 사용자메시지 등록/수정
   */
  @Override
  public Result insertMsgCode(final Map<String, Object> paramMap) throws Exception {
    Result result = new Result();

    // 메시지정보
    Map<String, Object> dma_msg = (Map<String, Object>) paramMap.get("dma_msg");

    dma_msg.put("MPNO", paramMap.get("MPNO"));
    dma_msg.put("PGM_ID", paramMap.get("PGM_ID"));

//    LOGGER.debug(" paramMap == " + paramMap.toString());
    try {
      // 메시지ID가 없으면 입력
      if (dma_msg.get("MESG_ID").toString().isEmpty()) {
        // 메시지 코드 리턴
        String msgId = dao.insertRetVal(NAMESPACE + "CMMSG001I001", dma_msg).toString();
        String msgTpcd = dma_msg.get("MESG_TPCD").toString();
        if (msgId.isEmpty()) {
          result.setMsg("W0002"); // 저장할 내용이 없습니다.
        } else {
          result.setMsg("S0011", msgTpcd+msgId); // 메시지등록이 완료되었습니다.
        }
      } else { // 메시지ID가 있으면 수정
        if (dao.update(NAMESPACE + "CMMSG001U001", dma_msg) > 0) {
          result.setMsg("S0003"); // 수정이 완료되었습니다.
        } else {
          result.setMsg("W0003"); // 수정할 내용이 없습니다.
        }
      }
      // 중복
    } catch (DuplicateKeyException de) {
      LOGGER.debug(" Exception == " + de.getClass());
      de.printStackTrace();
    }

    return result;
  }

  /*
   * 사용자메시지 삭제
   */
  @Override
  public Result deleteMsgCode(final Map<String, Object> paramMap) throws Exception {
    Result result = new Result();
    // 삭제할 메시지
    Map<String, Object> dma_msg = (Map<String, Object>) paramMap.get("dma_msg");
    if (dao.delete(NAMESPACE + "CMMSG001D001", dma_msg) > 0) {
      result.setMsg("S0004"); // 삭제가 완료되었습니다.
    } else {
      result.setMsg("W0004"); // 삭제할 내용이 없습니다.
    }

    return result;
  }
  
  /*
   * 거래로그 리스트 조회
   */
  @Override
  public Result selectTxLog(final Map<String, Object> param) throws Exception {
    Result result = new Result();
    //현재 조회건수
    int currentCnt = 0;
    //전체 조회건수
    int totalCnt = 0;
    
    Map<String, Object> dma_search = (Map<String, Object>) param.get("dma_search");
    currentCnt = ConvertUtil.strToInt(dma_search.get("CURRENT_CNT").toString());
    totalCnt = ConvertUtil.strToInt(dma_search.get("TOTAL_CNT").toString());
    // 조회 / 전체조회 시 전체건수 조회
    if ("A".equals(dma_search.get("SEARCH_TP").toString()) ||
        "S".equals(dma_search.get("SEARCH_TP").toString())) {
      totalCnt = (int)dao.selectObj(NAMESPACE + "CMMNA004S001", dma_search);
    }
    // 거래로그 조회
    ArrayList<Map<String, Object>> dlt_txLog = dao.selectList(NAMESPACE + "CMMNA004L001", dma_search);
    
    // 리턴 데이터 set
    result.setData("dma_search", CommonUtil.setCTS(dma_search, dlt_txLog));
    result.setData("dlt_txLog", dlt_txLog);
    // 현재 조회건수 
    currentCnt += dlt_txLog.size();
    // 조회건수 set
    dma_search.put("CURRENT_CNT", currentCnt);
    dma_search.put("TOTAL_CNT", totalCnt);
    
    // 조회건수 < 전체건수
    if (currentCnt < totalCnt) {
      if ("A".equals(dma_search.get("SEARCH_TP").toString())) {
        result.setMsg("S0008"); // 조회가 완료 되었습니다.(전체조회)
      } else {
        result.setMsg("S0007"); // 조회가 완료 되었습니다.(다음)
      }
    } else {
      result.setMsg("S0001"); // 조회가 완료 되었습니다.
    }
    return result;
  }

  /**
   * 이미지시스템 파일 업로드
   * @param HttpServletRequest request
   * @return Result
   * @exception Exception
   */
  @Override
  public Result imgFileUpload(final HttpServletRequest request) throws Exception {
    //이미지시스템 업로드 파일 경로
    String sendPath = "/app/sw/was/servers/node11/nbmImg/";  //jboss realPath

    LOGGER.debug("UNTRANSMITTED_PATH1 == " + sendPath);

    
    Result result = new Result(); // 리턴객체
    Map<String, Object> resultMap = new HashMap<String, Object>();
    ImgSystemVO imgSystemVO = new ImgSystemVO();
    String empNo = (String)request.getSession().getAttribute("MPNO");
    String pgmId = (String)request.getSession().getAttribute("SCRN_ID");
    String claimNum = request.getParameter("claimNum");
    String docType = request.getParameter("docType");
    String callbackFn = "parent." + request.getParameter("CALLBACK");
    String sendFileName = ""; // 보낼파일명
    String orgnFile = ""; // 업로드폴더에 복사할 파일
    String fileExt = "";
    String newFile = "";
    List<String> uploadFileNames = new ArrayList<>(); // 업로드할 파일
    List<String> uploadFileRenames = new ArrayList<>(); // 파일명 변경(채권번호+seq)
    int idx = (int)dao.selectObj(NAMESPACE + "CMIMG001S003", claimNum); //채권번호를 파라미터로 등록번호채번
    
   
    
    //현재 접속한 서버의 ip주소확인하기
    InetAddress local;
    local = InetAddress.getLocalHost();
    String idAddr = local.getHostAddress();
    String jobName = "imageSave";   // job명
    LOGGER.debug("connect client ip addr == " + idAddr);
    LOGGER.debug("jobName == " + jobName);
    
    File dir = new File(sendPath);
    // 폴더가 존재하지않으면 생성
    if (dir.exists() == false) {
      dir.mkdirs();
    }
    
    MultipartHttpServletRequest multiPartRequest = (MultipartHttpServletRequest) request;
    // 파일명 가져오기
    Iterator<String> fileNames = multiPartRequest.getFileNames();
    // 이미지시스템 업로드 폴더로 파일 복사
    while (fileNames.hasNext()) {
      sendFileName = fileNames.next();
      
      MultipartFile file = (MultipartFile) multiPartRequest.getFile(sendFileName);
      orgnFile = file.getOriginalFilename();
      fileExt = orgnFile.substring(orgnFile.lastIndexOf("."));
      newFile = claimNum+idx+fileExt;
      //이미지시스템 업로드 경로에 복사
      file.transferTo(new File(sendPath+newFile));
      uploadFileNames.add(orgnFile);
      uploadFileRenames.add(newFile);
      idx++;
      LOGGER.debug("imgFileUpload orgnFile == " + orgnFile);
      
      // 배치VO 이미지 전송 쉘
      BatchExecutionVO batchExecutionVO = new BatchExecutionVO();
      batchExecutionVO.setBatchShName(jobName);
      batchExecutionVO.setArgu1(StringUtil.nullToEmpty(idAddr));
      batchExecutionVO.setArgu2(StringUtil.nullToEmpty(newFile));
      BatchExecutionComponent.executeBatch(batchExecutionVO);   // job 실행
      
      
    }
    imgSystemVO.setFileNames(uploadFileNames);
    imgSystemVO.setFileRenames(uploadFileRenames);
    imgSystemVO.setEmpNo(empNo);
    imgSystemVO.setClaimNum(claimNum);
    imgSystemVO.setDocType(docType);
    imgSystemVO.setPgmId(pgmId);
    LOGGER.debug("imgSystemVO == " + imgSystemVO.toString());
    
    // 이미지시스템 업로드
    CommonComponent.imgUpload(imgSystemVO);
    
    // 업로드 폴더 임시파일 삭제 
   /* for (File file : dir.listFiles()) { 
      LOGGER.debug("temp dir file name == " + file.getName());
      if (file.isFile() && uploadFileRenames.contains(file.getName())) {
        LOGGER.debug("delete file == " + file.getName());
        file.delete();
      }
    }
    */
    resultMap.put("CALLBACK", callbackFn);
    resultMap.put("FILE_ID", "");

    result.setData("resultMap", resultMap);
    result.setMsg("S0016");
    return result;
  }
  
  /*
   * 이미지 시스템 업로드 리스트 조회
   */
  @Override
  public Result selectImgFileUpload(final Map<String, Object> param) throws Exception {
    Result result = new Result();
    //현재 조회건수
    int currentCnt = 0;
    //전체 조회건수
    int totalCnt = 0;
    
    Map<String, Object> dma_search = (Map<String, Object>) param.get("dma_search");
    currentCnt = ConvertUtil.strToInt(dma_search.get("CURRENT_CNT").toString());
    totalCnt = ConvertUtil.strToInt(dma_search.get("TOTAL_CNT").toString());
    // 조회 / 전체조회 시 전체건수 조회
    if ("A".equals(dma_search.get("SEARCH_TP").toString()) ||
        "S".equals(dma_search.get("SEARCH_TP").toString())) {
      totalCnt = (int)dao.selectObj(NAMESPACE + "CMIMG001S002", dma_search);
    }
    // 이미지 시스템 업로드 리스트 조회
    ArrayList<Map<String, Object>> dlt_imgFileUpload = dao.selectList(NAMESPACE + "CMIMG001L002", dma_search);
    
    // 리턴 데이터 set
    result.setData("dma_search", CommonUtil.setCTS(dma_search, dlt_imgFileUpload));
    result.setData("dlt_imgFileUpload", dlt_imgFileUpload);
    // 현재 조회건수 
    currentCnt += dlt_imgFileUpload.size();
    // 조회건수 set
    dma_search.put("CURRENT_CNT", currentCnt);
    dma_search.put("TOTAL_CNT", totalCnt);
    
    // 조회건수 < 전체건수
    if (currentCnt < totalCnt) {
      if ("A".equals(dma_search.get("SEARCH_TP").toString())) {
        result.setMsg("S0008"); // 조회가 완료 되었습니다.(전체조회)
      } else {
        result.setMsg("S0007"); // 조회가 완료 되었습니다.(다음)
      }
    } else {
      result.setMsg("S0001"); // 조회가 완료 되었습니다.
    }
    return result;
  }
  
  /*
   * 출력물 관리 리스트 조회
   */
  @Override
  public Result selectReportInfo(final Map<String, Object> param) throws Exception {
    Result result = new Result();
    //현재 조회건수
    int currentCnt = 0;
    //전체 조회건수
    int totalCnt = 0;
    
    Map<String, Object> dma_search = (Map<String, Object>) param.get("dma_search");
    currentCnt = ConvertUtil.strToInt(dma_search.get("CURRENT_CNT").toString());
    totalCnt = ConvertUtil.strToInt(dma_search.get("TOTAL_CNT").toString());
    // 조회 / 전체조회 시 전체건수 조회
    if ("A".equals(dma_search.get("SEARCH_TP").toString()) ||
        "S".equals(dma_search.get("SEARCH_TP").toString())) {
      totalCnt = (int)dao.selectObj(NAMESPACE + "CMRPT001S001", dma_search);
    }
    // 이미지 시스템 업로드 리스트 조회
    ArrayList<Map<String, Object>> dlt_imgFileUpload = dao.selectList(NAMESPACE + "CMRPT001L001", dma_search);
    
    // 리턴 데이터 set
    result.setData("dma_search", CommonUtil.setCTS(dma_search, dlt_imgFileUpload));
    result.setData("dlt_reportInfo", dlt_imgFileUpload);
    // 현재 조회건수 
    currentCnt += dlt_imgFileUpload.size();
    // 조회건수 set
    dma_search.put("CURRENT_CNT", currentCnt);
    dma_search.put("TOTAL_CNT", totalCnt);
    
    // 조회건수 < 전체건수
    if (currentCnt < totalCnt) {
      if ("A".equals(dma_search.get("SEARCH_TP").toString())) {
        result.setMsg("S0008"); // 조회가 완료 되었습니다.(전체조회)
      } else {
        result.setMsg("S0007"); // 조회가 완료 되었습니다.(다음)
      }
    } else {
      result.setMsg("S0001"); // 조회가 완료 되었습니다.
    }
    return result;
  }
  
  /*
   * 배치이력 리스트 조회
   */
  @Override
  public Result selectBatchInfo(final Map<String, Object> param) throws Exception {
    Result result = new Result();
    //현재 조회건수
    int currentCnt = 0;
    //전체 조회건수
    int totalCnt = 0;
    
    Map<String, Object> dma_search = (Map<String, Object>) param.get("dma_search");
    currentCnt = ConvertUtil.strToInt(dma_search.get("CURRENT_CNT").toString());
    totalCnt = ConvertUtil.strToInt(dma_search.get("TOTAL_CNT").toString());
    // 조회 / 전체조회 시 전체건수 조회
    if ("A".equals(dma_search.get("SEARCH_TP").toString()) ||
        "S".equals(dma_search.get("SEARCH_TP").toString())) {
      totalCnt = (int)dao.selectObj(NAMESPACE + "CMBAT001S001", dma_search);
    }
    // 배치이력 리스트 조회
    ArrayList<Map<String, Object>> dlt_batchInfo = dao.selectList(NAMESPACE + "CMBAT001L001", dma_search);
    
    // 리턴 데이터 set
    result.setData("dma_search", CommonUtil.setCTS(dma_search, dlt_batchInfo));
    result.setData("dlt_batchInfo", dlt_batchInfo);
    // 현재 조회건수 
    currentCnt += dlt_batchInfo.size();
    // 조회건수 set
    dma_search.put("CURRENT_CNT", currentCnt);
    dma_search.put("TOTAL_CNT", totalCnt);
    
    // 조회건수 < 전체건수
    if (currentCnt < totalCnt) {
      if ("A".equals(dma_search.get("SEARCH_TP").toString())) {
        result.setMsg("S0008"); // 조회가 완료 되었습니다.(전체조회)
      } else {
        result.setMsg("S0007"); // 조회가 완료 되었습니다.(다음)
      }
    } else {
      result.setMsg("S0001"); // 조회가 완료 되었습니다.
    }
    return result;
  }
  
}

