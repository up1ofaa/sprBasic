package com.hanwhalife.nbm.service.cm.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.hanwhalife.nbm.dao.CommonDAO;
import com.hanwhalife.nbm.esb.EsbCall;
import com.hanwhalife.nbm.esb.generated.Data;
import com.hanwhalife.nbm.esb.generated.Entity;
import com.hanwhalife.nbm.esb.hliesbclient.hliesbclient_if.Operation;
import com.hanwhalife.nbm.util.CommonUtil;
import com.hanwhalife.nbm.util.ConvertUtil;
import com.hanwhalife.nbm.util.PropertyFactory;
import com.hanwhalife.nbm.util.StringUtil;
import com.hanwhalife.nbm.vo.cm.ApprovalInfoVO;
import com.hanwhalife.nbm.vo.cm.EsbUserInfoVO;
import com.hanwhalife.nbm.vo.cm.ImgSystemVO;
import com.hanwhalife.nbm.vo.cm.ReportInfoVO;
import com.speno.TransferFile;
import com.speno.XtormHttpClient;

import korealife.uv.com.cm.SHA256CmCrypt;

/**
 * <b>Description:</b>
 * <p>
 * CommonComponent
 * 
 * @since 2019-07-17
 * @version 1.0
 * @author 홍선기(1073302)
 * @see
 * 
 *      <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-07-17  1073302     최초생성
 *      </pre>
 */
@Component
public class CommonComponent {

  private static final String NAMESPACE_COM = "commonMapper.";
  private static final String NAMESPACE_BN = "businessMapper.";
  private static final String NAMESPACE_USER = "userMapper.";
  
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonComponent.class);

  private static CommonDAO staticDao;

  @Autowired
  CommonDAO dao;

  @PostConstruct
  private void initDao() {
    staticDao = this.dao;
  }

  /**
   * 공통모듈 - 거래로그 등록
   * 
   * @param String
   *          MESG_ID
   * @return int
   * @exception Exception
   */
  public static boolean insertTxLog() {
    
    HttpSession session = CommonUtil.getSession();
    
    Map<String, Object> paramMap = new HashMap<String, Object>();
    String acesRout = (String)session.getAttribute("URI");
    String scrnId = (String)session.getAttribute("SCRN_ID");
    String dlngResl = (String)session.getAttribute("DLNG_RESL");
    String dlngRtmsCten = (String)session.getAttribute("DLNG_RTMS_CTEN");
    String mpno = (String)session.getAttribute("MPNO");
    String pgmId = PropertyFactory.getProperty("PGM_ID");
    
    if (StringUtil.isBlank(dlngResl)) {
      LOGGER.debug("insertTxLog no return code");
      return false;
    }
    // .do 는 제외
    if ( StringUtil.isBlank(acesRout) ||acesRout.indexOf(".do") > -1) {
      LOGGER.debug("insertTxLog do action servlet");
      return false;
    }
    // 거래조회관리 화면은 제외
    if ( StringUtil.isBlank(scrnId) || "NBCMTRX001M".equals(scrnId)) {
      LOGGER.debug("insertTxLog tx NBCMTRX001M");
      return false;
    }
    
    // 프로세스ID
    paramMap.put("ACES_ROUT", acesRout);
    // 화면ID
    paramMap.put("SCRN_ID", scrnId);
    // 처리결과
    paramMap.put("DLNG_RESL", dlngResl);
    // 처리결과메시지내용
    paramMap.put("DLNG_RTMS_CTEN", dlngRtmsCten);
    //처리시간
    paramMap.put("DLGS_TIME", "TEST");
    // 사원번호
    paramMap.put("MPNO", mpno);
    // 프로그램ID
    paramMap.put("PGM_ID", pgmId);
//    LOGGER.debug("insertTxLog paramMap == " + paramMap.toString());
    try {
      staticDao.insert(NAMESPACE_COM + "CMMNA004I001", paramMap);
    } catch (Exception ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }
    return true;
  }
  
  /**
   * 공통모듈 - 사용자 메세지 조회
   * 
   * @param String
   *          MESG_ID
   * @return Map<String, Object>
   * @exception Exception
   */
  public static Map<String, Object> selectMsgCode(final String MESG_ID) {
    Map<String, Object> retMap = new HashMap<String, Object>();
    try {
      retMap = staticDao.select(NAMESPACE_COM + "CMMSG001S001", MESG_ID);
    } catch (Exception ex) {
      return retMap;
    }
    
    return retMap;
  }
  
  /**
   * 공통모듈 - 사용자 메세지 조회
   * 
   * @param String
   *          MESG_ID
   * @return Map<String, Object>
   * @exception Exception
   */
  public static Map<String, Object> selectLoginInfo(final String EMPL_NO) {
    Map<String, Object> retMap = new HashMap<String, Object>();
    try {
      retMap = staticDao.select(NAMESPACE_USER + "CMUSR001S001", EMPL_NO);;
    } catch (Exception ex) {
      return retMap;
    }
    
    return retMap;
  }
  
  /**
   * 공통모듈 - D+ 영업일 조회
   * 
   * @param String 기준일자(8)
   * @param Integer 영업일 수
   * @param String 휴일포함여부(1)
   * @return String(8)
   * @exception Exception
   */
  public static String getBusinessDay(final String baseDay, final int offset, final String holydayYN) throws Exception {
    Map<String, Object> param = new HashMap<>();
    param.put("YMD", baseDay);
    param.put("OFFSET", offset);
    param.put("HLDY_YN", holydayYN);
    return (String)staticDao.selectObj(NAMESPACE_COM + "CMMNA007S001", param);
  }
  
  /**
   * 공통모듈 - 이미지시스템 업로드
   * 
   * @param ImgSystemVO
   *          imgSystemVO
   * @return void
   * @exception Exception
   */
  public static void imgUpload(final ImgSystemVO imgSystemVO) throws Exception {
    // 이미지 시스템 업로드
    boolean ret = false;  //결과값
    
    List<String> fileNames   = imgSystemVO.getFileNames();       //등록파일명
    List<String> fileRenames   = imgSystemVO.getFileRenames();       //등록파일명
    String empNo    = imgSystemVO.getEmpNo();        //사원번호
    String claimNum = imgSystemVO.getClaimNum();     //증권번호  
    String[] docTypes  = imgSystemVO.getDocType().split(",");      //문서타입
    String docType = "";
    String pgmId = imgSystemVO.getPgmId();
    HashMap<String, Object> paramMap = new HashMap<>();
    String systemCode = PropertyFactory.getProperty("img.systemCode");
  
  
    String fileRename = "";                                 //20210526 이미지서버업로드 로직 제거
   // XtormHttpClient xhclient = new XtormHttpClient();       //20210526 이미지서버업로드 로직 제거
   // HashMap<String, Object> imageRegInfo = new HashMap<>(); //20210526 이미지서버업로드 로직 제거
  
    //샘플은 단일파일 업로드 & DB등록이지만, 실제에서는 문서별로 복수이미지가 등록되면 루프를 사용해서 작성
    for (String fileNm : fileNames) {
     try {
        fileRename = fileRenames.get(fileNames.indexOf(fileNm));
        docType = docTypes[fileNames.indexOf(fileNm)];
        //XTORM 이미지 에이전트 파일 업로드 호출 (To-Be방식) (이미지서버 임시 폴더에 업로드)
        //ret = TransferFile.XtormFileUpload(systemCode, empNo, fileRename);  //암호화
        LOGGER.debug("1XtormFileUpload 결과 : [" + ret + "]");        

      } catch (Exception ex) {
        LOGGER.debug("2XtormFileUpload 결과 : [" + ex.getMessage() + "]");   
        ex.printStackTrace();
      }
      //파일전송이 성공이면...이미지파일 전송후에 이미지DB에 등록
    /*     if (ret) {
        //이미지서버DB에 저장할 이미지파일에 대한 정보를 셋팅
        imageRegInfo.clear();
                                                       // 항목                 업무별 설정값    // 
        imageRegInfo.put( "USERID"     , empNo );      // 사용자ID           
        imageRegInfo.put( "DOCTYPE"    , docType );    // 문서타입        
        imageRegInfo.put( "FILENAME"   , fileRename);      // 파일명 
        imageRegInfo.put( "SYSTEMCODE" , systemCode);       // 시스템코드(To-Be 신규항목 필수)              
        imageRegInfo.put( "INDEX01" , "HA"+claimNum );          // 인덱스01  (증권번호)
        imageRegInfo.put( "INDEX02" , "" );                // 인덱스02   
        imageRegInfo.put( "INDEX03" , "YP" );              // 인덱스03  (청약서심사여부)   
        imageRegInfo.put( "INDEX04" , empNo );             // 인덱스04  (사용자ID)     
        imageRegInfo.put( "INDEX05" , "" );                // 인덱스05         
        imageRegInfo.put( "INDEX06" , "" );                // 인덱스06  (자동인식내용)       
        imageRegInfo.put( "INDEX07" , "N" );               // 인덱스07  (자동인식내용)   
        imageRegInfo.put( "INDEX08" , "N" );               // 인덱스08  (자동인식내용)     
        imageRegInfo.put( "INDEX09" , "" );                // 인덱스09  (자동인식내용)       
        imageRegInfo.put( "INDEX10" , "NA" );              // 인덱스10  (자동인식내용)
        
        ret = xhclient.sendImageRegiserInfo(imageRegInfo);
          
        LOGGER.debug("4sendImageRegiserInfo 결과 : [" + ret + "]");
        // 이미지서버DB 등록 후
         
         */
       // if (ret) {//20210526 이미지서버업로드 로직 제거
          // 이미지관리 테이블 등록
          paramMap.clear();
          // 채권관리번호
          paramMap.put("BOND_ADMN_NO", claimNum);
          // 문서타입코드
          paramMap.put("DOCU_TYPE_CODE", docType);
          // 이미지파일명
          paramMap.put("IMAG_FILE_NAME", fileNm);
          // 서브시스템코드
          paramMap.put("SUB_SYS_CODE", systemCode);
          // 프로그램ID
          paramMap.put("PGM_ID", pgmId);
          // 사원번호
          paramMap.put("MPNO", empNo);
          staticDao.insert(NAMESPACE_COM + "CMIMG001I001", paramMap);
        //} //20210526 이미지서버업로드 로직 제거
      //} //20210526 이미지서버업로드 로직 제거
    }//end of for
    
    ///////////////////////////////////////////////////////////////////////////
    //도착정보 등록(★증권번호별로 마지막에 한번만 호출★) 만약실패해도, 이미지서버에서 배치프로세스가 실패건 재처리함
    ///////////////////////////////////////////////////////////////////////////
//    if(ret == true) {
//      XtormHttpClient xhclient = new XtormHttpClient();
//      
//      //이미지서버DB에 저장할 이미지파일에 대한 정보를 셋팅
//      HashMap NK21RegInfo = new HashMap();
//      // 항목                 업무별 설정값    //
//      NK21RegInfo.put( "USERID"     , empNo );              // 사용자ID 
//      NK21RegInfo.put( "SYSTEMCODE" , SystemCode);               // 시스템코드(To-Be 신규항목 필수)    
//      NK21RegInfo.put( "DOCTYPE"    , docType );    // 문서타입
//      NK21RegInfo.put( "INDEX01" , claimNum );          // 인덱스01  (증권번호)
//      NK21RegInfo.put( "INDEX02" , "" );                // 인덱스02   
//      NK21RegInfo.put( "INDEX03" , "YP" );              // 인덱스03  (청약서심사여부)   
//      NK21RegInfo.put( "INDEX04" , empNo );             // 인덱스04  (사용자ID)     
//      NK21RegInfo.put( "INDEX05" , "" );                // 인덱스05          
//      NK21RegInfo.put( "INDEX06" , "" );                // 인덱스06  (자동인식내용)        
//      NK21RegInfo.put( "INDEX07" , "N" );               // 인덱스07  (자동인식내용)   
//      NK21RegInfo.put( "INDEX08" , "N" );               // 인덱스08  (자동인식내용)      
//      NK21RegInfo.put( "INDEX09" , "" );                // 인덱스09  (자동인식내용)        
//      NK21RegInfo.put( "INDEX10" , "NA" );              // 인덱스10  (자동인식내용)
//      
//      //도착정보 등록(★증권번호별로 마지막에 한번만 호출★) 만약실패해도, 이미지서버에서 배치프로세스가 실패건 재처리함, 단 필수서류가 전부 이미지서버에 등록되었을 경우.
//      //NK21에 등록할 증권번호에 해당하는 필수문서(청약서,계약전알릴의무,부속서류,부속(개인정보동의서),상품설명서)중에 부족한 서류가 존재할시에는
//      //NK21도착정보 등록이 실패하고, 필수문서를 다시 이미지서버 등록후에 NK21을 호출해줘야함.
//      //단 적합성진단확인서의 경우에는 단독으로 NK21도착정보를 등록가능함.
//      ret = xhclient.sendNK21RegiserInfo(NK21RegInfo);
//      
//      LOGGER.debug("5sendNK21RegiserInfo 결과 : [" + ret + "]");
//    }
  }
  
  /**
   * 공통모듈 - 출력물관리 정보 등록
   * 
   * @param ReportInfoVO 출력물 정보 VO
   * @return boolean
   * @exception Exception
   */
  public static boolean insertReportInfo(final ReportInfoVO reportInfoVO) throws Exception {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    Map<String, Object> map = new HashMap<String, Object>();
    HttpSession session = CommonUtil.getSession();
    Gson gson = new Gson(); 
    
    String mpno = (String)session.getAttribute("MPNO");
    String pgmId = (String)session.getAttribute("SCRN_ID");
    String callSrvcName = (String)session.getAttribute("URI");
    
    // 오즈파일명
    paramMap.put("OZ_FILE_NAME", reportInfoVO.getOzFileName());
    // 호출서비스명
    paramMap.put("CALL_SRVC_NAME", callSrvcName);
    // 증명서분류코드
    paramMap.put("CRTF_CLSF_CODE", reportInfoVO.getCrtfClsfCode());
    // 요청OZ파라미터
    if (reportInfoVO.getRqstOzParm() == null) {
      map.put("PUBLISH", "Y");
    } else {
      
      if("NBBMBND001R".equals(reportInfoVO.getOzFileName()) || "NBBMBND002R".equals(reportInfoVO.getOzFileName())) {
        map.put(reportInfoVO.getDmaRpmtPlanName(), reportInfoVO.getRqstRpmtPlanOzParm());
        map.put(reportInfoVO.getDltListName(), reportInfoVO.getRqstDltListParm());
      }
      map.put(reportInfoVO.getDmaName(), reportInfoVO.getRqstOzParm());
      
      map.put("PUBLISH", "Y");
    }
    paramMap.put("RQST_OZ_PARM", gson.toJson(map));
    // 채권관리번호
    paramMap.put("BOND_ADMN_NO", reportInfoVO.getBondAdmnNo());
    // 고객ID
    paramMap.put("CUST_ID", reportInfoVO.getCustId());
    // 직인구분
    paramMap.put("OFSE_DVSN", reportInfoVO.getOfseDvsn());
    // 민원서류발급번호
    paramMap.put("CADC_ISNO", reportInfoVO.getCadcIsno());
    // 증명서발급용도
    paramMap.put("CRTF_ISSU_USE", reportInfoVO.getCrtfIssuUse());
    // 수령자명
    paramMap.put("REVR_NAME", reportInfoVO.getRevrName());
    // 수신처
    paramMap.put("RECV_PLAC", reportInfoVO.getRecvPlac());
    // 비고
    paramMap.put("REM", reportInfoVO.getRem());
    // 사원번호
    paramMap.put("MPNO", mpno);
    // 프로그램ID
    paramMap.put("PGM_ID", pgmId);
    
    LOGGER.debug("paramMap == " + paramMap.toString());
    if (staticDao.insert(NAMESPACE_COM + "CMRPT001I001", paramMap) == 0) {
      return false;
    }
    return true;
    
  }
  
  /**
   * 공통모듈 - 승인 정보 등록
   * 
   * @param ApprovalInfoVO 승인 정보 VO
   * @return String 결재ID
   * @exception Exception
   */
  public static String insertApprovalInfo(final ApprovalInfoVO approvalInfoVO) throws Exception {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    HttpSession session = CommonUtil.getSession();
    String retVal = "";
    
    approvalInfoVO.setEmplNo((String)session.getAttribute("MPNO"));
    approvalInfoVO.setPgmId((String)session.getAttribute("SCRN_ID"));
    approvalInfoVO.setRprtOgcd((String)session.getAttribute("ORGN_CODE"));
    
    // 채권관리번호
    paramMap.put("BOND_ADMN_NO", approvalInfoVO.getBondAdmnNo());
    // 고객ID
    paramMap.put("CUST_ID", approvalInfoVO.getCustId());
    // 매핑키1
    paramMap.put("MAPP_KEY_1", approvalInfoVO.getMappKey1());
    // 매핑키2
    paramMap.put("MAPP_KEY_2", approvalInfoVO.getMappKey2());
    // 매핑키3
    paramMap.put("MAPP_KEY_3", approvalInfoVO.getMappKey3());
    // 매핑키4
    paramMap.put("MAPP_KEY_4", approvalInfoVO.getMappKey4());
    // 매핑키5
    paramMap.put("MAPP_KEY_5", approvalInfoVO.getMappKey5());
    // 화면ID
    paramMap.put("SCRN_ID", approvalInfoVO.getPgmId());
    // 결재관리코드(2001:수납취소, 3001:법무신청)
    paramMap.put("APRV_ADCD", approvalInfoVO.getAprvAdcd());
    // 승인상태코드(10:승인요청)
    paramMap.put("RCNT_STAT_CODE", "10");
    // 상신기관코드
    paramMap.put("RPRT_OGCD", approvalInfoVO.getRprtOgcd());
    
    // 사원번호
    paramMap.put("MPNO", approvalInfoVO.getEmplNo());
    // 프로그램ID
    paramMap.put("PGM_ID", approvalInfoVO.getPgmId());
    
    LOGGER.debug("paramMap == " + paramMap.toString());
    retVal = (String)staticDao.insertRetVal(NAMESPACE_BN + "CMMNA006I001", paramMap);
    
    return retVal;
  }
  
  
  /**
   * 공통모듈 - 승인 정보 수정(승인신청건만 10 수정)
   * 
   * @param ApprovalInfoVO 승인 정보 VO
   * @return boolean
   * @exception Exception
   */
  public static boolean updateApprovalInfo(final ApprovalInfoVO approvalInfoVO) throws Exception {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    HttpSession session = CommonUtil.getSession();
    
    approvalInfoVO.setEmplNo((String)session.getAttribute("MPNO"));
    approvalInfoVO.setPgmId((String)session.getAttribute("SCRN_ID"));
    approvalInfoVO.setAprvOrgnCode((String)session.getAttribute("ORGN_CODE"));
    
    // 결재ID
    paramMap.put("APRV_ID", approvalInfoVO.getAprvId());
    // 승인상태코드
    paramMap.put("RCNT_STAT_CODE", approvalInfoVO.getRcntStatCode());
    // 결재기관코드
    paramMap.put("APRV_ORGN_CODE", approvalInfoVO.getAprvOrgnCode());
    
    // 사원번호
    paramMap.put("MPNO", approvalInfoVO.getEmplNo());
    
    LOGGER.debug("paramMap == " + paramMap.toString());
    if (staticDao.update(NAMESPACE_BN + "CMMNA006U001", paramMap) == 0) {
      return false;
    }
    return true;
  }
  
  
  
  /**
   * 공통모듈 - 승인이후 승인 정보 수정(승인완료건만 20 수정)
   * 
   * @param ApprovalInfoVO 승인 정보 VO
   * @return boolean
   * @exception Exception
   */
  public static boolean updateAftrApprovalInfo(final ApprovalInfoVO approvalInfoVO) throws Exception {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    HttpSession session = CommonUtil.getSession();
    
    approvalInfoVO.setEmplNo((String)session.getAttribute("MPNO"));
    approvalInfoVO.setPgmId((String)session.getAttribute("SCRN_ID"));
    approvalInfoVO.setAprvOrgnCode((String)session.getAttribute("ORGN_CODE"));
    
    // 결재ID
    paramMap.put("APRV_ID", approvalInfoVO.getAprvId());
    // 승인상태코드
    paramMap.put("RCNT_STAT_CODE", approvalInfoVO.getRcntStatCode());
    // 결재기관코드
    paramMap.put("APRV_ORGN_CODE", approvalInfoVO.getAprvOrgnCode());
    
    // 사원번호
    paramMap.put("MPNO", approvalInfoVO.getEmplNo());
    
    LOGGER.debug("paramMap == " + paramMap.toString());
    if (staticDao.update(NAMESPACE_BN + "CMMNA006U002", paramMap) == 0) {
      return false;
    }
    return true;
  }
  /**
   * 공통모듈 - ESB 비밀번호 체크
   * 
   * @param EsbUserInfoVO esbUserInfoVO
   * @return EsbUserInfoVO
   * @exception Exception
   */
  public static EsbUserInfoVO getPasswordChk(final EsbUserInfoVO esbUserInfoVO) throws Exception {
    EsbUserInfoVO retVo = new EsbUserInfoVO();
    String emplNo = esbUserInfoVO.getEmplNo();
    String password = esbUserInfoVO.getPassWord();
    String encPassword = "";
    retVo.setEmplNo(emplNo);

   /* 
    Map<String, Object> userMap = new HashMap<String, Object>();
    String EMPL_NO = emplNo;
    
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("EMPL_NO", emplNo);
    // ESB Operation
    com.hanwhalife.nbm.esb.hliesbclient.hliesbclient_if.ObjectFactory OFOperation = new com.hanwhalife.nbm.esb.hliesbclient.hliesbclient_if.ObjectFactory();
    Operation op = OFOperation.createOperation();

    // ESB Data (서비스정의서를 참고하여 입력값 세팅)
    com.hanwhalife.nbm.esb.generated.ObjectFactory obf = new com.hanwhalife.nbm.esb.generated.ObjectFactory();
    Data data = obf.createData();
    Entity entity = obf.createEntity();
    // Data Type 지정 [ Long/Integer=1, Double=2, String=3, Binary=4 ]
    // 사원번호
    entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // String Type
    entity.setName("AA_STR_001");
    entity.getVal().add(emplNo);
    data.getEntity().add(entity);
    // 비밀번호(3DES)
    entity = obf.createEntity();
    entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // String Type
    entity.setName("AA_STR_002");
    entity.getVal().add(SHA256CmCrypt.getEncString(password));
    data.getEntity().add(entity);
    // 비밀번호(SHA256)
    entity = obf.createEntity();
    entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // String Type
    entity.setName("AA_STR_003");
    encPassword = SHA256CmCrypt.SHA256_getEncString(password);
   
    entity.getVal().add(encPassword);
    
    //entity set
    data.getEntity().add(entity);
    
    op.setData(data);

    // ESBcall
    EsbCall esbCall = new EsbCall();
    Map<String, Object> returnMap = new HashMap<String, Object>();
    // ESB콜
    returnMap = esbCall.joltCall("SO00936", "LoginService", op);

    LOGGER.debug("===============  Request DATA ===============");

    int lpp_cnt = op.getData().getEntity().size();
    for (int i = 0; i < lpp_cnt; i++) {

      LOGGER.debug(op.getData().getEntity().get(i).getName()
          + " : " + op.getData().getEntity().get(i).getVal());
    }
    LOGGER.debug("===============  Reponse DATA ===============");
    
    // AA_STR_101 리턴코드
    // AA_STR_102 리턴메세지
    // AA_STR_104 성명
    // AA_STR_109 근무소속기관코드
    // AA_STR_110 기관명
     //
    retVo.setReturnCode(((ArrayList<String>)returnMap.get("AA_STR_101")).get(0));
    retVo.setReturnMsg(((ArrayList<String>)returnMap.get("AA_STR_102")).get(0));
    
    if ("01".equals(retVo.getReturnCode())) { // 00:정상
      LOGGER.debug("==== ESB SUCCESS   ............");
      LOGGER.debug(returnMap.toString());
      retVo.setSuccess(true);
      retVo.setEmplNm(((ArrayList<String>)returnMap.get("AA_STR_104")).get(0));
      retVo.setOrgnCode(((ArrayList<String>)returnMap.get("AA_STR_109")).get(0));
      retVo.setOrgnName(((ArrayList<String>)returnMap.get("AA_STR_110")).get(0));
      
      // 사용자정보
      userMap = staticDao.select(NAMESPACE_USER + "CMUSR001S001", EMPL_NO);
      if (userMap != null) {
        LOGGER.debug("===============  userMap not null ===============");
        retVo.setTdayLognCnt(ConvertUtil.strToInt(userMap.get("TDAY_LOGN_CNT").toString()));
        retVo.setTimeDiff(ConvertUtil.strToInt(userMap.get("TIMEDIFFERENCE").toString()));
//        if(retVo.getTdayLognCnt() > 4 && retVo.getTimeDiff() < 600) {
        if(retVo.getTdayLognCnt() > 4 && retVo.getTimeDiff() < 1) {
          LOGGER.debug("===============  5times incorrect ===============");
          //retVo.setSuccess(false);  반드시 false로 나중에 수정해야함
          retVo.setSuccess(true);
          retVo.setReturnCode("00");
          return retVo;
        }
        // 성공 시 횟수 초기화
        staticDao.update(NAMESPACE_USER + "CMUSR001U003", paramMap);
      }
      
    } else {  //ESB 호출 실패
      LOGGER.debug("==== ESB FAIL   ............");
      LOGGER.debug(returnMap.toString());
      retVo.setSuccess(false);
      
      if ("00".equals(retVo.getReturnCode())) {
        // 비밀번호 실패횟수 업데이트
        staticDao.update(NAMESPACE_USER + "CMUSR001U002", paramMap);
        // 사용자정보
        userMap = staticDao.select(NAMESPACE_USER + "CMUSR001S001", EMPL_NO);
        if (userMap != null) {
          retVo.setTdayLognCnt(ConvertUtil.strToInt(userMap.get("TDAY_LOGN_CNT").toString()));
          retVo.setTimeDiff(ConvertUtil.strToInt(userMap.get("TIMEDIFFERENCE").toString()));
        }
      }
    }
    
    */
    
    String ntUserId="NBMAdmin"; 
    String ntPasswd="[nbm]!234";
    String url="ldap://10.10.163.14";
    String domain="HLIFS.HANWHA.COM";
    String searchBase="dc=hlifs,dc=hanwha,dc=com";
    
    String baseRdn="ou=hanwha,dc=hlifs,dc=hanwha,dc=com";
    Hashtable<String, String> env=new Hashtable<String,String>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, url);
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    env.put(Context.SECURITY_PRINCIPAL,ntUserId);
    env.put(Context.SECURITY_CREDENTIALS,ntPasswd);
    
    try {
      LdapContext ctx=new InitialLdapContext(env,null);      

      
      Hashtable<String, String> usrEnv = new Hashtable<String,String>();
      
      usrEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      usrEnv.put(Context.PROVIDER_URL, url);
      usrEnv.put(Context.SECURITY_AUTHENTICATION, "simple");     
      usrEnv.put(Context.SECURITY_PRINCIPAL,emplNo+"@"+domain);
      usrEnv.put(Context.SECURITY_CREDENTIALS,password);
      new InitialLdapContext(usrEnv,null);
      
      
      retVo.setSuccess(true);
    
    }catch(javax.naming.AuthenticationException e) {
      String msg=e.getMessage();
      retVo.setSuccess(false);
    }catch(Exception nex) {
      nex.printStackTrace();
      retVo.setSuccess(false);
    }
    
    LOGGER.debug("===============  Reponse DATA ===============");
    
    return retVo;
  }

} // end of class
