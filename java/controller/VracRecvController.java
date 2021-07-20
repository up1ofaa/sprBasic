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
package com.hanwhalife.nbm.controller.rc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.maven.surefire.shade.org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hanwhalife.nbm.dao.CommonDAO;
import com.hanwhalife.nbm.service.cm.com.CommonComponent;
import com.hanwhalife.nbm.service.rc.RecvCommService;
import com.hanwhalife.nbm.service.rc.VracRecvService;
import com.hanwhalife.nbm.util.DateUtil;
import com.hanwhalife.nbm.util.Result;
import com.hanwhalife.nbm.util.StringUtil;
import com.hanwhalife.nbm.vo.cm.ApprovalInfoVO;

/**
 * <b>Description:</b><p>
 * 수납관리 입금내역관리, 입금내역건별처리, 수납거래내역 취소
 * @since 2019-06-14
 * @version 1.0
 * @author 이진섭(1073421)
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일                    수정자              수정내용
 * ----------  ---------    -------------------------------
 * 2019-06-14   1073421     최초생성
 * </pre>
 */
@Controller
public class VracRecvController {
    
  private static final Logger LOGGER = LoggerFactory.getLogger(VracRecvController.class);      
  private static final String NAMESPACE = "vracTelgMapper.";
  private static final String NAMESPACE_rec = "vracRecvMapper.";
  
  @Autowired
  VracRecvService vracRecvService;
  
  @Autowired
  RecvCommService recvCommService;
  
  @Autowired
  CommonDAO dao;
  
  /**
   * 수납거래내역 조회
   * @param request - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/rc/rcp/selectRcptIndv.sbm", method=RequestMethod.POST)
  public Map<String, Object> selectRcptIndv(final HttpServletRequest request) throws Exception {
    
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");    
    Map<String, Object> dma_search = (Map<String, Object>) param.get("dma_search");
    
    Result result = new Result();
    
    ArrayList<Map<String, Object>> resultset = vracRecvService.selectRcptIndv(dma_search); 
    
    if (resultset.size() > 0) {
      dma_search.put("CTS_BOND_ADMN_NO", resultset.get(resultset.size()-1).get("BOND_ADMN_NO"));
      dma_search.put("CTS_DLGS_SQNO", resultset.get(resultset.size()-1).get("DLGS_SQNO").toString());
      dma_search.put("CTS_DLGS_DATE", resultset.get(resultset.size()-1).get("DLGS_DATE"));
    }

    result.setData("dlt_rcptIndv", resultset);
    result.setData("dma_search", dma_search);

    result.setMsg("S0005", "수납거래내역 조회가 완료"); 
    
    return result.getResult();        
  }    
  
  /**
   * 수납거래내역 취소
   * @param request - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/rc/rcp/cancelRcptIndv.sbm", method=RequestMethod.POST)
  public Map<String, Object> cancelRcptIndv(final HttpServletRequest request) throws Exception {
      
    Map<String, Object> paramMap = (Map<String, Object>) request.getAttribute("params");    
    Map<String, Object> resultMap = vracRecvService.cancelRcptIndv(paramMap);

    Result result = new Result();
    
    if ("S".equals(resultMap.get("status"))) {
      result.setMsg("S0005", "수납거래내역 취소가 완료");            
    } else {
      result.setMsg("E0012", resultMap.get("procRow")+"번째 수납거래내역 취소");
    }
    
    return result.getResult();        
  }        
  
  /**
   * 입금내역관리 조회
   * @param request - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/rc/rcp/selectRcptBondMapp.sbm", method=RequestMethod.POST)  
  public Map<String, Object> selectRcptBondMapp(final HttpServletRequest request) throws Exception {
        
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");    
    Map<String, Object> dma_search = (Map<String, Object>) param.get("dma_search");
    
    // 관리기관
    String[] chrgOrgnCodeList = null;
    if ( !StringUtil.isBlank(StringUtil.nullToEmpty(dma_search.get("CHRG_ORGN_CODE"))) ) {
      chrgOrgnCodeList = dma_search.get("CHRG_ORGN_CODE").toString().split(" ");
      
      if (chrgOrgnCodeList.length > 0) {
        dma_search.put("chrgOrgnCodeList", chrgOrgnCodeList);
      } 
    }

    Result result = new Result();
    
    ArrayList<Map<String, Object>> resultList = vracRecvService.selectRcptBondMapp(dma_search);
    
    ArrayList<Map<String, Object>> dlt_vracRecvList = new ArrayList<Map<String, Object>>();
    
    if (resultList.size() > 0) {
      dma_search.put("CTS_DLGS_DATE", resultList.get(resultList.size()-1).get("DLGS_DATE"));
      if (resultList.get(resultList.size()-1).containsKey("TELG_SEQ_NO") && !"".equals(resultList.get(resultList.size()-1).get("TELG_SEQ_NO"))) {
        dma_search.put("CTS_TELG_SEQ_NO", resultList.get(resultList.size()-1).get("TELG_SEQ_NO"));  
      }      
      if (resultList.get(resultList.size()-1).containsKey("DLGS_TIME") && !"".equals(resultList.get(resultList.size()-1).get("DLGS_TIME"))) {
        dma_search.put("CTS_DLGS_TIME", resultList.get(resultList.size()-1).get("DLGS_TIME"));  
      }
      if (resultList.get(resultList.size()-1).containsKey("FINA_ORGN_CODE") && !"".equals(resultList.get(resultList.size()-1).get("FINA_ORGN_CODE"))) {
        dma_search.put("CTS_FINA_ORGN_CODE", resultList.get(resultList.size()-1).get("FINA_ORGN_CODE"));  
      }      
      
      BigDecimal calcBondBaln = new BigDecimal("0");//채무잔액 
      BigDecimal trafBondBaln = new BigDecimal("0");//처리후잔액
      BigDecimal recvAmt = new BigDecimal("0");//입금금액
      
      BigDecimal remainAmt = new BigDecimal("0");//나머지 계산용
      
      BigDecimal ucolBondBaln = new BigDecimal("0");//채무잔액
      BigDecimal jdacBaln = new BigDecimal("0");//비용잔액      
      
      BigDecimal ucolInteBaln = new BigDecimal("0");//채무이자잔액(미수이자회수대상)
      BigDecimal totIncc = new BigDecimal("0");//산출이자
      
      for (Map<String, Object> tempMap : resultList) {
        
        calcBondBaln = BigDecimal.ZERO;
        trafBondBaln = BigDecimal.ZERO;
        recvAmt = BigDecimal.ZERO;
        totIncc = BigDecimal.ZERO;
                
        if (!"".equals(StringUtils.defaultString(tempMap.get("BOND_ADMN_NO"))) 
            && !"".equals(StringUtils.defaultString(tempMap.get("CUST_ID")))
            && "1".equals(tempMap.get("RECV_PROG_STCD"))) { //1.가수/입금

          recvAmt = BigDecimal.valueOf(Double.parseDouble(tempMap.get("RECV_AMT").toString()));          
          ucolBondBaln = BigDecimal.valueOf(Double.parseDouble(tempMap.get("UCOL_BOND_BALN").toString()));
          jdacBaln = BigDecimal.valueOf(Double.parseDouble(tempMap.get("JDAC_BALN").toString()));

          //이자산출 키:채권관리번호, 고객ID
          Map<String, Object> resulInccRtim = recvCommService.selectInccBal(tempMap);
          
          tempMap.put("UCOL_INTE_BALN", resulInccRtim.get("UCOL_INTE_BALN"));//이자잔액
          tempMap.put("DLBF_INTE_BALN", resulInccRtim.get("UCOL_INTE_BALN"));//차감전 이자잔액 = 이자잔액
          tempMap.put("UCIT_RCAT", resulInccRtim.get("INTE_AMT"));//이자발생금액

          ucolInteBaln = BigDecimal.valueOf(Double.parseDouble(tempMap.get("UCOL_INTE_BALN").toString()));
          
          tempMap.put("DLBF_PRCP_BALN", ucolBondBaln);
          tempMap.put("DLBF_COST_BALN", jdacBaln);
          tempMap.put("DLBF_INTE_BALN", ucolInteBaln);

          calcBondBaln = ucolBondBaln.add(jdacBaln);//채무잔액 = 원금잔액 + 비용잔액
          calcBondBaln = calcBondBaln.add(ucolInteBaln);//채무잔액 = 채무잔액 + 이자잔액
          
          tempMap.put("CALC_BOND_BALN", calcBondBaln);         
          //처리후잔액=채무잔액-입금내역 trafBondBaln               
          trafBondBaln = calcBondBaln.subtract(recvAmt);            
          tempMap.put("TRAF_BOND_BALN", trafBondBaln);
          
          /*
           * 입금금액과 총채무잔액이 같으면 일괄처리대상(아닌건은 개별처리화면에서 별도 처리)
           * 에서 전체채무잔액보다 입금금액이 작거나 같으면 처리
           * 원금, 비용, 이자를 금액과 확인후 수납금액으로 SET
           * 입금액을 수납원금, 수납비용, 수납이자
           * RECV_BOND_AMT, RECV_JDAF_COST, RECV_UCIT_AMT
           */
          if (calcBondBaln.compareTo(recvAmt) >= 0) {
              
            //채권잔액이 입금금액보다 크면 전부 원금상환
            if (ucolBondBaln.compareTo(recvAmt) > 0) {
              
              tempMap.put("RECV_BOND_AMT", recvAmt);//수납원금
              tempMap.put("RECV_JDAF_COST", 0);//수납비용
              tempMap.put("RECV_UCIT_AMT", 0);//수납이자
              
              tempMap.put("DLAF_PRCP_BALN", ucolBondBaln.subtract(recvAmt));//처리후원금잔액-입금금액
              tempMap.put("DLAF_COST_BALN", jdacBaln);//처리후비용잔액
              tempMap.put("CALC_JDAC_BALN", jdacBaln);//처리후비용잔액 
              tempMap.put("DLAF_INTE_BALN", ucolInteBaln);//처리후이자잔액                
              
            } else {//채권잔액원금보다 크면
              
              tempMap.put("RECV_BOND_AMT", ucolBondBaln);//수납원금=채권잔액
              remainAmt = recvAmt.subtract(ucolBondBaln);
              
              tempMap.put("DLAF_PRCP_BALN", 0);//처리후원금잔액 0

              //비용이 남은금액보다 크면
              if (jdacBaln.compareTo(remainAmt) > 0) {                  
                tempMap.put("RECV_JDAF_COST", remainAmt);//수납비용
                tempMap.put("RECV_UCIT_AMT", 0);//수납이자                
                
                tempMap.put("DLAF_COST_BALN", jdacBaln.subtract(remainAmt));//처리후비용잔액
                tempMap.put("CALC_JDAC_BALN", jdacBaln.subtract(remainAmt));//처리후비용잔액
                tempMap.put("DLAF_INTE_BALN", ucolInteBaln);//처리후이자잔액                                  
              } else {                  
                remainAmt = remainAmt.subtract(jdacBaln);
                tempMap.put("RECV_JDAF_COST", jdacBaln);//수납비용
                tempMap.put("RECV_UCIT_AMT", remainAmt);//수납이자=나머지금액
                
                tempMap.put("DLAF_COST_BALN", 0);//처리후비용잔액
                tempMap.put("CALC_JDAC_BALN", 0);//처리후비용잔액
                tempMap.put("DLAF_INTE_BALN", ucolInteBaln.subtract(remainAmt));//처리후이자잔액                                                   
              }              
            }            
          }
                    
        }

        dlt_vracRecvList.add(tempMap);
        
      }
      
    }
    
    result.setData("dlt_vracRecvList", dlt_vracRecvList);
    result.setData("dma_search", dma_search);

    result.setMsg("S0005", "입금내역 조회가 완료"); 
      
    return result.getResult();
  }
  
  /**
   * 입금내역관리 일괄수납
   * @param request - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/rc/rcp/insertRcptList.sbm", method=RequestMethod.POST)
  public Map<String, Object> insertRcptList(final HttpServletRequest request) throws Exception {
    
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    
    //ldt_vracRecvList 일괄수납처리대상
    ArrayList<Map<String, Object>> ldt_vracRecvList = (ArrayList<Map<String, Object>>) param.get("ldt_vracRecvList");
        
    Result result = new Result();
    
    //아래 건별처리 부분을 반복처리 한다.    
    for (Map<String, Object> inputMap : ldt_vracRecvList) {
      
      //사용자정보
      inputMap.put("MPNO", param.get("MPNO"));
      inputMap.put("PGM_ID", param.get("PGM_ID"));
      inputMap.put("ORGN_CODE", param.get("ORGN_CODE"));  

      //수납
      //Map<String, Object> resultInsertRcpt = this.insertCommRcpt(inputMap);
      Map<String, Object> resultInsertRcpt = this.recvRefin(inputMap);
      
      if ("E".equals(resultInsertRcpt.get("status").toString())) {
        result.setMsg("E0002");
      } else {
        result.setMsg("S0005", "입금내역 일괄처리가 완료");
      }
      
    }
        
    return result.getResult();
  }
  
  /**
   * 건별입금내역관리
   * @param request - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/rc/rcp/selectTempRcpt.sbm", method=RequestMethod.POST)
  public Map<String, Object> selectTempRcpt(final HttpServletRequest request) throws Exception {
        
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    Map<String, Object> dma_search = (Map<String, Object>) param.get("dma_search");
    
    Result result = new Result();
        
    //1.채권기본정보 조회(BMBND001S001) key:채권관리번호
    Map<String, Object> resultBondMap = vracRecvService.selectBondInfo(dma_search);
    
    LOGGER.debug("resultBondMap ??? : " + resultBondMap);
    
    result.setData("dma_bondInfo", resultBondMap); //채권기본
    
    Map<String, Object> inccSearchMap = new HashMap<String, Object>(); 

    inccSearchMap.put("BOND_ADMN_NO", dma_search.get("BOND_ADMN_NO"));
    inccSearchMap.put("CUST_ID", dma_search.get("CUST_ID"));
    
    //4.채권자금리+기본정보 조회(BMBND001S001) key:채권관리번호, 고객ID
    Map<String, Object> dma_apcnRato = vracRecvService.selectRatoInfo(inccSearchMap);     
    
    //4.2 수납이자금액정보 조회(BMBND004S003) key:채권관리번호
    //Map<String, Object> dma_recvIamt = vracRecvService.selecRecvIamtInfo(inccSearchMap);  
    
    //4.1 이자 산출 key:채권관리번호, 고객ID            
    ArrayList<Map<String, Object>> dlt_inccInfo = new ArrayList<Map<String, Object>>();
    
    Map<String, Object> incc = new HashMap<String, Object>(); 
    
    //반제이면 이자내역 조회
    if(dma_apcnRato == null) {
      
      BigDecimal ucitAmt = new BigDecimal("0");
      BigDecimal ucitInteBaln = new BigDecimal("0");
      incc.put("UCIT_AMT",ucitAmt); //이자금액셋팅
      incc.put("UCIT_INTE_BALN",ucitInteBaln); //이자금액셋팅
    }else {
      
      dma_apcnRato.put("UCIT_AMT",dma_apcnRato.get("UCIT_AMT")); //이자금액셋팅
      dma_apcnRato.put("UCIT_INTE_BALN",dma_apcnRato.get("UCIT_INTE_BALN")); //이자잔액셋팅
      dma_apcnRato.put("RCKN_DATE",dma_apcnRato.get("LAST_INCC_DT")); //조회된 기산일자
      
      dlt_inccInfo = vracRecvService.selectInccCten(dma_search);
        
      if (dlt_inccInfo.size() > 0) {
         //조회된 기산일자 (거래내역에 기산일자)
          dma_apcnRato.put("RCKN_DATE", dlt_inccInfo.get(0).get("RCKN_DATE"));
      }
    }
    
    result.setData("dlt_inccInfo", dlt_inccInfo); //이자내역      
    if(dma_apcnRato == null) {
      result.setData("dma_apcnRato", incc); //채무자별 
      
    }else {
      result.setData("dma_apcnRato", dma_apcnRato); //채무자별 
    }
    
    Map<String, Object> mattSearchMap = new HashMap<String, Object>();
    
    if(dma_apcnRato != null && !"".equals(dma_apcnRato.get("MATT_IMAG_ACCT_NO"))) {

      mattSearchMap.put("MATT_IMAG_ACCT_NO", dma_apcnRato.get("MATT_IMAG_ACCT_NO"));
    
      //2.기회수내역 조회(BMBND040L002) key:채권관리번호
      
      ArrayList<Map<String, Object>> resultRcptInfoList = vracRecvService.selectRcptInfo(dma_search,mattSearchMap);
      
      result.setData("dlt_rcptInfo", resultRcptInfoList); //기회수내역
      
      //3.가상계좌 입금내역 조회(RCRCP900L001) key:금융기관코드, 가상계좌번호, 채권관리번호(법조치, 개인회생)
      ArrayList<Map<String, Object>> resulTempRcptList = vracRecvService.selectTempRcpt(dma_search,mattSearchMap);  
      
      //함인휘 추가 및 수정 시작
      ArrayList<Map<String, Object>> rcptList = new ArrayList<Map<String, Object>>();//이자내역
      
      if (resulTempRcptList.size() > 0) {
        for (Map<String, Object> tempMap:resulTempRcptList) {          
          tempMap.put("MATT_IMAG_ACCT_NO", dma_apcnRato.get("MATT_IMAG_ACCT_NO"));
          
          rcptList.add(tempMap);
        }
      }
      
      result.setData("dlt_tempRcpt", rcptList); //입금내역
      //함인휘 추가 및 수정 끝
    }
    //5.거래내역 조회 key:채권관리번호, 거래내역 일련번호
    Map<String, Object> resultDlgsCtenMap = new HashMap<String, Object>();
    
    Map<String, Object> dma_recvInfo = new HashMap<String, Object>();//수납처리
    Map<String, Object> dma_xcpyInfo = new HashMap<String, Object>();//과납송금
    
    //반제이면 이전 처리 정보 조회
    if (dma_search.containsKey("RECV_PROG_STCD") && "2".equals(dma_search.get("RECV_PROG_STCD"))) {
      //거래내역 조회 (BMBND040S002) key:채권관리번호, 거래일련번호
      resultDlgsCtenMap = vracRecvService.selectDlgsCten(dma_search);
      if (resultDlgsCtenMap != null 
          && !"".equals(StringUtils.defaultString(resultDlgsCtenMap.get("BOND_ADMN_NO")))) {
        
        dma_recvInfo.put("DLBF_PRCP_BALN", resultDlgsCtenMap.get("DLBF_PRCP_BALN")); //처리전원금
        dma_recvInfo.put("DLBF_COST_BALN", resultDlgsCtenMap.get("DLBF_COST_BALN")); //처리전비용
        dma_recvInfo.put("DLBF_INTE_BALN", resultDlgsCtenMap.get("DLBF_INTE_BALN")); //처리전이자                      
        dma_recvInfo.put("RDAT_PRCP", resultDlgsCtenMap.get("RDAT_PRCP")); //감액원금
        dma_recvInfo.put("RDAT_COST", resultDlgsCtenMap.get("RDAT_COST")); //감액비용
        dma_recvInfo.put("RDAT_INTE", resultDlgsCtenMap.get("RDAT_INTE")); //감액이자      
        dma_recvInfo.put("RECV_BOND_AMT", resultDlgsCtenMap.get("RECV_PRCP")); //수납원금
        dma_recvInfo.put("RECV_JDAF_COST", resultDlgsCtenMap.get("DLGS_COST")); //거래비용
        dma_recvInfo.put("RECV_UCIT_AMT", resultDlgsCtenMap.get("RECV_IAMT")); //수납이자      
        dma_recvInfo.put("DLAF_PRCP_BALN", resultDlgsCtenMap.get("DLAF_PRCP_BALN")); //처리후원금
        dma_recvInfo.put("DLAF_COST_BALN", resultDlgsCtenMap.get("DLAF_COST_BALN")); //처리후비용
        dma_recvInfo.put("DLAF_INTE_BALN", resultDlgsCtenMap.get("DLAF_INTE_BALN")); //처리후이자                      
        dma_recvInfo.put("UCIT_RCAT", resultDlgsCtenMap.get("UCIT_RCAT")); //미수이자회수금액
        dma_recvInfo.put("DLGS_DPST_AMT", resultDlgsCtenMap.get("DLGS_DPST_AMT")); //거래공탁금액
        dma_recvInfo.put("DPAT_INTE_RCAT", resultDlgsCtenMap.get("DPAT_INTE_RCAT")); //공탁금이자회수금액
        
        dma_xcpyInfo.put("RMTN_RQST_DATE", resultDlgsCtenMap.get("RMTN_RQST_DATE")); //송금요청일자
        dma_xcpyInfo.put("RMTN_SEQ_NO", resultDlgsCtenMap.get("RMTN_SEQ_NO")); //송금일련번호 *
        dma_xcpyInfo.put("RECV_BKCD", resultDlgsCtenMap.get("RECV_BKCD")); //입금은행코드
        dma_xcpyInfo.put("RECV_ACCT_NO", resultDlgsCtenMap.get("RECV_ACCT_NO")); //입금계좌번호
        dma_xcpyInfo.put("DPSR_NAME", resultDlgsCtenMap.get("DPSR_NAME")); //예금주명
        dma_xcpyInfo.put("DPSR_RELN_CODE", resultDlgsCtenMap.get("DPSR_RELN_CODE")); //예금주관계코드
        dma_xcpyInfo.put("RQST_AMT", resultDlgsCtenMap.get("RQST_AMT")); //요청금액
        dma_xcpyInfo.put("DLNG_STAT_CODE", resultDlgsCtenMap.get("DLNG_STAT_CODE")); //송금처리상태코드 *
        
        result.setData("dma_recvInfo", dma_recvInfo); //수납처리
        result.setData("dma_xcpyInfo", dma_xcpyInfo); //과납송금        
      }

    }
    
    result.setData("dma_search", dma_search);

    result.setMsg("S0005", "가상계좌건별입금내역관리 조회가 완료");     
    
    return result.getResult();
  }

  /**
   * 건별입금내역관리 수납처리
   * @param request - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/rc/rcp/insertRcpt.sbm", method=RequestMethod.POST)
  public Map<String, Object> insertRcpt(final HttpServletRequest request) throws Exception {
    
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");
    
    LOGGER.debug("insertRcpt param : " + param);

    //기본조회조건(채권관리번호, 금융기관코드, 가상계좌번호)
    Map<String, Object> dma_search = (Map<String, Object>) param.get("dma_search");

    //조회정보
    Map<String, Object> dma_apcnRato = (Map<String, Object>) param.get("dma_apcnRato");  

    //수납정보
    Map<String, Object> dma_recvInfo = (Map<String, Object>) param.get("dma_recvInfo");

    //감면정보;
    Map<String, Object> dma_redcInfo = (Map<String, Object>) param.get("dma_redcInfo");

    //선택된 입금정보
    ArrayList<Map<String, Object>> ldt_tempRcpt = (ArrayList<Map<String, Object>>) param.get("ldt_tempRcpt");
    
    //수납처리시 사용할 기본정보
    Map<String, Object> inputMap = new HashMap<String, Object>(); 
    
    //사용자정보 Set
    inputMap.put("MPNO", param.get("MPNO"));
    inputMap.put("PGM_ID", param.get("PGM_ID"));
    inputMap.put("ORGN_CODE", param.get("ORGN_CODE"));  

    //화면 전송 data 셋팅
    Map<String, Object> dma_xcpyInfo = (Map<String, Object>) param.get("dma_xcpyInfo");
    inputMap.putAll(dma_xcpyInfo); //과납정보
    inputMap.putAll(dma_recvInfo); //수납정보 Set
    inputMap.putAll(dma_search);   //기본조회조건 Set (입금 구분코드 셋팅을 위해서 처리)
    inputMap.putAll(dma_apcnRato);   //기본조회조건 Set (입금 구분코드 셋팅을 위해서 처리)
    inputMap.putAll(dma_redcInfo);   //감면정보 Set
    
    //리스트 정보 중 체크된 단일건(입금내역)만 넘어온다. 필터링 된 리스트
    for (Map<String, Object> tempRcptMap:ldt_tempRcpt) {
      inputMap.putAll(tempRcptMap);//입금정보 Set
    }    
      
    Result result = new Result();
    int dlgsSqno = 0; 

    
    //가상계좌번호 셋팅 : 입금받은 부서가상계좌번호 -> 개인별 가상계좌번호
    inputMap.put("IMAG_ACCT_NO", inputMap.get("MATT_IMAG_ACCT_NO"));
    
    //입금구분 처리는 화면 콤보박스의 값을 셋팅
    inputMap.put("RECV_DVSN", dma_search.get("RECV_DVSN"));


    LOGGER.debug("dma_redcInfo : " + dma_redcInfo);

    //감면처리 금액이 있을 경우 감면처리를 수납거래로 셋팅
    if (dma_redcInfo.get("RDAT_SUM")!= null) {
      
      if (Double.parseDouble(dma_redcInfo.get("RDAT_SUM").toString()) > 0) {
        inputMap.put("DLGS_AMT", dma_redcInfo.get("RDAT_SUM"));
        inputMap.put("RECV_BOND_AMT", dma_redcInfo.get("RDAT_PRCP"));
        inputMap.put("RECV_JDAF_COST", dma_redcInfo.get("RDAT_COST"));
        inputMap.put("RECV_UCIT_AMT", dma_redcInfo.get("RDAT_INTE"));
        inputMap.put("RDAT_YN", "Y");
      }
      
    }
    
    Map<String, Object> resultInsertRcpt = this.recvRefin(inputMap);    
    dlgsSqno = Integer.parseInt(resultInsertRcpt.get("dlgsSqno").toString());
    
    if ("E".equals(resultInsertRcpt.get("status").toString())) {
      result.setMsg("E0002");
    }          
    
    //처리건 입금일자를 다시 담아서 리턴
    dma_search.put("DLGS_DATE", inputMap.get("DLGS_DATE"));
    dma_search.put("DLGS_SQNO", dlgsSqno);//반제거래일련번호
    dma_search.put("RECV_PROG_STCD", "2");//진행상태 반제
    
    result.setData("dma_search", dma_search);
    
    return result.getResult();
    
  }  
  
  /**
   * 건별 수납처리 (2020.12.08)
   * @param request - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  
  protected Map<String, Object> recvRefin (final Map<String, Object> inputMap) throws Exception { 
    LOGGER.debug("recvRefin inputMap : " + inputMap);
    
    inputMap.put("ORDS_BOND_ADNO", inputMap.get("BOND_ADMN_NO")); //원거래채권관리번호
    inputMap.put("ORDS_SQNO", inputMap.get("DLGS_SQNO"));         //원거래일련번호
    
    // 처리 변수 선언
    Map<String, Object> param = inputMap;
    
    // DB 처리 변수 선언
    Map<String, Object> paramMap = new HashMap<String, Object>();
    
    // DB 처리 변수 선언
    Map<String, Object> paramBond = new HashMap<String, Object>();
    
    // 입금 전 원장 내역 조회
    paramMap.put("IMAG_ACCT_NO",  param.get("IMAG_ACCT_NO"));
    paramMap.put("BOND_ADMN_NO",  param.get("BOND_ADMN_NO"));
    
    LOGGER.debug("001 paramMap : "+ paramMap);

      //부서가상계좌일 경우 채권관리번호로 조회
    if ("56213795323684".equals(param.get("IMAG_ACCT_NO")) || 
        "56213795323699".equals(param.get("IMAG_ACCT_NO")) || 
        "56213795323700".equals(param.get("IMAG_ACCT_NO"))
        ) {
      
      paramBond = dao.select(NAMESPACE + "BMBND001S002", paramMap);
      
    } else {

      //가상계좌번호로
      paramBond = dao.select(NAMESPACE + "BMBND001S001", paramMap);
      
    }

    //------------------------------------------------------------------------------
    //(채권별)비교금액 : paramBond.COMP_AMT
    //(채권별)수납 전 원금잔액 : paramBond.BF_UCOL_BOND_BALN
    //(채권별)수납 후 원금잔액 : paramBond.AF_UCOL_BOND_BALN
    //(채권별)수납 전 비용잔액 : paramBond.BF_JDAC_BALN
    //(채권별)수납 후 비용잔액 : paramBond.AF_JDAC_BALN
    //(채권별)수납 전 이자잔액 : paramBond.BF_UCOL_INTE_BALN
    //(채권별)수납 후 이자잔액 : paramBond.AF_UCOL_INTE_BALN
    //(채권별)수납원금 : paramBond.RECV_BOND
    //(채권별)수납비용 : paramBond.RECV_JDAC
    //(채권별)수납이자 : paramBond.RECV_INTE
    //------------------------------------------------------------------------------
    
    param.put("BOND_ADMN_NO",paramBond.get("BOND_ADMN_NO"));
    param.put("DEBT_PRTR_SEQ",paramBond.get("DEBT_PRTR_SEQ"));
        
    paramBond.put("COMP_AMT",param.get("DLGS_AMT"));
    paramBond.put("BF_UCOL_BOND_BALN",paramBond.get("UCOL_BOND_BALN"));
    paramBond.put("BF_JDAC_BALN",paramBond.get("JDAC_BALN"));
    paramBond.put("BF_UCOL_INTE_BALN",paramBond.get("UCOL_INTE_BALN"));

    //수납관련 비교 처리 금액 선언
    BigDecimal paramBond_COMP_AMT = new BigDecimal (paramBond.get("COMP_AMT").toString()); //비교금액
    BigDecimal paramBond_RECV_AMT = new BigDecimal (0); //상환금액
    BigDecimal paramBond_BF_UCOL_BOND_BALN = new BigDecimal (paramBond.get("UCOL_BOND_BALN").toString()); //수납 전 원금잔액
    BigDecimal paramBond_BF_JDAC_BALN = new BigDecimal (paramBond.get("JDAC_BALN").toString()); //수납 전 비용잔액
    BigDecimal paramBond_BF_UCOL_INTE_BALN = new BigDecimal (paramBond.get("UCOL_INTE_BALN").toString()); //수납 전 이자잔액

   
    // 입력한 금액이 있으면 그대로 수납
    BigDecimal recvBondAmt   = BigDecimal.valueOf(Double.parseDouble(param.get("RECV_BOND_AMT").toString()));
    BigDecimal RecvJdafCost  = BigDecimal.valueOf(Double.parseDouble(param.get("RECV_JDAF_COST").toString()));
    BigDecimal RecvUcitAmt   = BigDecimal.valueOf(Double.parseDouble(param.get("RECV_UCIT_AMT").toString()));
    
    //수납원금, 수납비용, 수납이자가 정해진 상태로 전달되면 그대로 해당 금액을 수납
    if (recvBondAmt.compareTo(BigDecimal.ZERO) > 0 ||
        RecvJdafCost.compareTo(BigDecimal.ZERO) > 0 ||
        RecvUcitAmt.compareTo(BigDecimal.ZERO) > 0
        ) {
      
      paramBond.put("RECV_BOND",  recvBondAmt);
      paramBond.put("RECV_JDAC",  RecvJdafCost);
      paramBond.put("RECV_INTE",  RecvUcitAmt);
            
    //아니면 수납기준에 따라 수납할 원금, 비용, 이자금액을 확인한다.
    } else {
        
      //1) 원금잔액 비교 차감
      //비교금액 = 비교금액 - 수납 전 원금잔액
      //if 비교금액 > 0
      //  수납원금 = 수납 전 원금잔액 
      //else
      //  수납원금 = 비교금액
      

      LOGGER.debug("002 paramBond_COMP_AMT : "+ paramBond_COMP_AMT);
      LOGGER.debug("002 paramBond_BF_UCOL_BOND_BALN : "+ paramBond_BF_UCOL_BOND_BALN);
      LOGGER.debug("002 paramBond_BF_JDAC_BALN : "+ paramBond_BF_JDAC_BALN);
      LOGGER.debug("002 paramBond_BF_UCOL_INTE_BALN : "+ paramBond_BF_UCOL_INTE_BALN);
      
      paramBond_COMP_AMT = paramBond_COMP_AMT.subtract(paramBond_BF_UCOL_BOND_BALN);

      LOGGER.debug("003 paramBond_COMP_AMT : "+ paramBond_COMP_AMT);
      
      paramBond_RECV_AMT = BigDecimal.ZERO;
      if (paramBond_COMP_AMT.compareTo(BigDecimal.ZERO) >= 0) {
        paramBond_RECV_AMT = paramBond_BF_UCOL_BOND_BALN;      
        paramBond.put("RECV_BOND",  paramBond_RECV_AMT);
      } else {
        paramBond_RECV_AMT = paramBond_BF_UCOL_BOND_BALN.add(paramBond_COMP_AMT);
        paramBond.put("RECV_BOND",  paramBond_RECV_AMT);
      }

      LOGGER.debug("004 paramBond.get(RECV_BOND) : "+ paramBond.get("RECV_BOND"));

      //2) 비용잔액 비교 차감
      //비교금액 = 비교금액 - 수납 전 비용잔액
      //if 비교금액 > 0
      //  수납비용 = 수납 전 비용잔액
      //else
      //  수납비용 = 비교금액
      LOGGER.debug("005 paramBond_COMP_AMT : "+ paramBond_COMP_AMT);
      LOGGER.debug("005 paramBond_BF_JDAC_BALN : "+ paramBond_BF_JDAC_BALN);


      LOGGER.debug("006 paramBond_COMP_AMT : "+ paramBond_COMP_AMT);

      paramBond_RECV_AMT = BigDecimal.ZERO;
      if (paramBond_COMP_AMT.compareTo(BigDecimal.ZERO) > 0) {
      
        paramBond_COMP_AMT = paramBond_COMP_AMT.subtract(paramBond_BF_JDAC_BALN);

        if (paramBond_COMP_AMT.compareTo(BigDecimal.ZERO) >= 0) {
          paramBond_RECV_AMT = paramBond_BF_JDAC_BALN;      
          paramBond.put("RECV_JDAC",  paramBond_RECV_AMT);
        } else {
          paramBond_RECV_AMT = paramBond_BF_JDAC_BALN.add(paramBond_COMP_AMT);
          paramBond.put("RECV_JDAC", paramBond_RECV_AMT);
        }

      } else {
      
        paramBond_RECV_AMT = BigDecimal.ZERO;
        paramBond.put("RECV_JDAC", paramBond_RECV_AMT);

      }

      LOGGER.debug("007 paramBond.get(RECV_JDAC) : "+ paramBond.get("RECV_JDAC"));

      //3) 이자잔액 비교 차감
      //비교금액 = 비교금액 - 이자잔액
      //if 비교금액 > 0
      //  수납이자 = 이자잔액
      //else
      //  수납이자 = 비교금액
      LOGGER.debug("008 paramBond_COMP_AMT : "+ paramBond_COMP_AMT);
      LOGGER.debug("008 paramBond_BF_UCOL_INTE_BALN : "+ paramBond_BF_UCOL_INTE_BALN);
      LOGGER.debug("009 paramBond_COMP_AMT : "+ paramBond_COMP_AMT);

      paramBond_RECV_AMT = BigDecimal.ZERO;
      if (paramBond_COMP_AMT.compareTo(BigDecimal.ZERO) > 0) {

        paramBond_COMP_AMT = paramBond_COMP_AMT.subtract(paramBond_BF_UCOL_INTE_BALN);

        if (paramBond_COMP_AMT.compareTo(BigDecimal.ZERO) >= 0) {
          paramBond_RECV_AMT = paramBond_BF_UCOL_INTE_BALN;      
          paramBond.put("RECV_INTE",  paramBond_RECV_AMT);
        } else {
          paramBond_RECV_AMT = paramBond_BF_UCOL_INTE_BALN.add(paramBond_COMP_AMT);
          paramBond.put("RECV_INTE", paramBond_RECV_AMT);
        }

      } else {

        paramBond_RECV_AMT = BigDecimal.ZERO;
        paramBond.put("RECV_INTE", paramBond_RECV_AMT);
      }

      LOGGER.debug("010 paramBond.get(RECV_INTE) : "+ paramBond.get("RECV_INTE"));
      
    }

    //수납후 잔액 계산용 변수
    BigDecimal paramBond_RECV_BOND = new BigDecimal (paramBond.get("RECV_BOND").toString()); //수납원금
    BigDecimal paramBond_RECV_JDAC = new BigDecimal (paramBond.get("RECV_JDAC").toString()); //수납비용
    BigDecimal paramBond_RECV_INTE = new BigDecimal (paramBond.get("RECV_INTE").toString()); //수납이자

    LOGGER.debug("011 paramBond_RECV_BOND : "+ paramBond_RECV_BOND);
    LOGGER.debug("011 paramBond_RECV_JDAC : "+ paramBond_RECV_JDAC);
    LOGGER.debug("011 paramBond_RECV_INTE : "+ paramBond_RECV_INTE);

    //수납 후 원금잔액 = 수납 전 원금잔액 - 수납원금
    LOGGER.debug("012 paramBond_BF_UCOL_BOND_BALN : "+ paramBond_BF_UCOL_BOND_BALN);
    LOGGER.debug("012 paramBond_RECV_BOND : "+ paramBond_RECV_BOND);
    BigDecimal paramBond_AF_UCOL_BOND_BALN = paramBond_BF_UCOL_BOND_BALN.subtract(paramBond_RECV_BOND);    
    LOGGER.debug("013 paramBond_AF_UCOL_BOND_BALN : "+ paramBond_AF_UCOL_BOND_BALN);

    //수납 후 비용잔액 = 수납 전 비용잔액 - 수납비용
    LOGGER.debug("014 paramBond_BF_JDAC_BALN : "+ paramBond_BF_JDAC_BALN);
    LOGGER.debug("014 paramBond_RECV_JDAC : "+ paramBond_RECV_JDAC);
    BigDecimal paramBond_AF_JDAC_BALN = paramBond_BF_JDAC_BALN.subtract(paramBond_RECV_JDAC);
    LOGGER.debug("015 paramBond_AF_JDAC_BALN : "+ paramBond_AF_JDAC_BALN);

    //수납 후 이자잔액 = 수납 전 이자잔액 - 수납이자
    LOGGER.debug("016 paramBond_BF_UCOL_INTE_BALN : "+ paramBond_BF_UCOL_INTE_BALN);
    LOGGER.debug("016 paramBond_RECV_INTE : "+ paramBond_RECV_INTE);
    BigDecimal paramBond_AF_UCOL_INTE_BALN = paramBond_BF_UCOL_INTE_BALN.subtract(paramBond_RECV_INTE);
    LOGGER.debug("017 paramBond_AF_UCOL_INTE_BALN : "+ paramBond_AF_UCOL_INTE_BALN);

    //수납후 잔액 계산 결과 셋팅
    paramBond.put("AF_UCOL_BOND_BALN", paramBond_AF_UCOL_BOND_BALN);
    paramBond.put("AF_JDAC_BALN", paramBond_AF_JDAC_BALN);
    paramBond.put("AF_UCOL_INTE_BALN", paramBond_AF_UCOL_INTE_BALN);
    LOGGER.debug("018 paramBond_AF_UCOL_BOND_BALN : "+ paramBond_AF_UCOL_BOND_BALN);
    LOGGER.debug("018 paramBond_AF_JDAC_BALN : "+ paramBond_AF_JDAC_BALN);
    LOGGER.debug("018 paramBond_AF_UCOL_INTE_BALN : "+ paramBond_AF_UCOL_INTE_BALN);

    //금액 확인
    LOGGER.debug("BF_UCOL_BOND_BALN : "+ paramBond.get("BF_UCOL_BOND_BALN"));
    LOGGER.debug("BF_JDAC_BALN : "+ paramBond.get("BF_JDAC_BALN"));
    LOGGER.debug("BF_UCOL_INTE_BALN : "+ paramBond.get("BF_UCOL_INTE_BALN"));

    LOGGER.debug("RECV_BOND : "+ paramBond.get("RECV_BOND"));
    LOGGER.debug("RECV_JDAC : "+ paramBond.get("RECV_JDAC"));
    LOGGER.debug("RECV_INTE : "+ paramBond.get("RECV_INTE"));

    LOGGER.debug("AF_UCOL_BOND_BALN : "+ paramBond.get("AF_UCOL_BOND_BALN"));
    LOGGER.debug("AF_JDAC_BALN : "+ paramBond.get("AF_JDAC_BALN"));
    LOGGER.debug("AF_UCOL_INTE_BALN : "+ paramBond.get("AF_UCOL_INTE_BALN"));

    //------------------------------------------------------------------------------
    // param 셋팅
    //------------------------------------------------------------------------------

    param.put("LAST_CHMN_MPNO", param.get("MPNO"));  /*최종변경자사번 */
    param.put("FRST_RGTR_MPNO", param.get("MPNO"));  /*최초등록자사번 */
    
    if ("01".equals(param.get("RECV_DVSN"))) {
      param.put("FUND_DVCD", "4"); //현금가수금반제
    } else if ("02".equals(param.get("RECV_DVSN"))) { 
      param.put("FUND_DVCD", "6"); //부서계좌반제
    } else { //가상계좌
      if ("7".equals(param.get("FUND_DVCD"))) { //부서계좌가수금
        param.put("FUND_DVCD", "6"); //부서계좌반제          
      }
      else {
        param.put("FUND_DVCD", "2"); //미수채권반제
      }
    }
    //------------------------------------------------------------------------------
    // TB_BMBND040 INSERT : 거래내역 등록
    //------------------------------------------------------------------------------
    paramMap.put("BOND_ADMN_NO", param.get("BOND_ADMN_NO"));  /*채권관리번호 */
    //paramMap.put("DLGS_SQNO", param.get("DLGS_SQNO"));  /*거래일련번호 */
    paramMap.put("LAST_CHMN_MPNO", param.get("LAST_CHMN_MPNO"));  /*최종변경자사번 */
    //paramMap.put("LAST_CHNG_DTTM", param.get("LAST_CHNG_DTTM"));  /*최종변경일시 */
    paramMap.put("PGM_ID", param.get("PGM_ID"));  /*프로그램ID */
    paramMap.put("FRST_RGTR_MPNO", param.get("FRST_RGTR_MPNO"));  /*최초등록자사번 */
    paramMap.put("FRST_RGST_DTTM", param.get("FRST_RGST_DTTM"));  /*최초등록일시 */
    paramMap.put("DLGS_DATE", param.get("DLGS_DATE"));  /*거래일자 */
    paramMap.put("DLGS_STCD", "01");  /*거래상태코드 01:정상*/
    paramMap.put("BOND_DLGS_KDCD", "20");  /*채권거래종류코드 20:미수채권일반수납 */
    paramMap.put("BOND_DVCD", "01");  /*채권구분코드 01:미수채권*/
    paramMap.put("DLGS_AMT", param.get("DLGS_AMT"));  /*거래금액 */
    paramMap.put("TRNF_PRCP", 0);  /*이관원금 */

    paramMap.put("RECV_PRCP", paramBond.get("RECV_BOND"));  /*수납원금 */
    paramMap.put("RECV_IAMT", paramBond.get("RECV_INTE"));  /*수납이자금액 */
    paramMap.put("DLGS_COST", paramBond.get("RECV_JDAC"));  /*거래비용금액 */

    paramMap.put("DLGS_DPST_AMT", 0);  /*거래공탁금액 */
    paramMap.put("DPAT_INTE_RCAT", 0);  /*공탁금이자회수금액 */

    paramMap.put("UCIT_RCAT", 0);  /*미수이자회수금액 */
    paramMap.put("FUND_DVCD", param.get("FUND_DVCD"));  /*자금구분코드 */
    paramMap.put("RCVY_DVCD", param.get("RCVY_DVCD"));  /*회수구분코드 */
    //paramMap.put("COMS_YN", param.get("COMS_YN"));  /*위탁여부 */
    paramMap.put("CNCL_RESN_CTEN", "");  /*취소사유내용 */
    paramMap.put("DLNG_ORGN_CODE", param.get("ORGN_CODE"));  /*처리기관코드 */
    paramMap.put("DLNG_MAN_MPNO", param.get("MPNO"));  /*처리자사번 */
    paramMap.put("RCNM_MPNO", param.get("MPNO"));  /*승인자사번 */    
    //paramMap.put("DLGS_ONCE", param.get("DLGS_ONCE"));  /*거래일시 */
    paramMap.put("RCNT_STAT_CODE", "20");  /*승인상태코드 */
    //paramMap.put("RCNT_DATE", param.get("RCNT_DATE"));  /*승인일자 */
    paramMap.put("FINA_ORGN_CODE", param.get("FINA_ORGN_CODE"));  /*금융기관코드 */
    paramMap.put("VRAC_DLGS_DT", param.get("DLGS_DATE"));  /*가상계좌거래일자 */
    paramMap.put("DLGS_TIME", param.get("DLGS_TIME"));  /*거래시각 */
    paramMap.put("TELG_DVSN_CODE", param.get("TELG_DVSN_CODE"));  /*전문구분코드 */
    paramMap.put("TELG_SEQ_NO", param.get("TELG_SEQ_NO"));  /*전문일련번호 */
    paramMap.put("ORDS_BOND_ADNO", param.get("ORDS_BOND_ADNO"));  /*원거래채권관리번호 */
    paramMap.put("ORDS_SQNO", param.get("ORDS_SQNO"));  /*원거래일련번호 */
    paramMap.put("IMAG_ACCT_NO", param.get("IMAG_ACCT_NO"));  /*가상계좌번호 */
    //paramMap.put("CPAY_YN", param.get("CPAY_YN"));  /*완납여부 */

    paramMap.put("DLBF_PRCP_BALN", paramBond.get("BF_UCOL_BOND_BALN"));  /*처리전원금잔액 */
    paramMap.put("DLBF_COST_BALN", paramBond.get("BF_JDAC_BALN"));  /*처리전비용잔액 */
    paramMap.put("DLBF_INTE_BALN", paramBond.get("BF_UCOL_INTE_BALN"));  /*처리전이자잔액 */

    paramMap.put("DLAF_PRCP_BALN", paramBond.get("AF_UCOL_BOND_BALN"));  /*처리후원금잔액 */
    paramMap.put("DLAF_COST_BALN", paramBond.get("AF_JDAC_BALN"));  /*처리후비용잔액 */
    paramMap.put("DLAF_INTE_BALN", paramBond.get("AF_UCOL_INTE_BALN"));  /*처리후이자잔액 */

    //paramMap.put("DLNG_DATE", param.get("DLNG_DATE"));  /*처리일자 */
    
    LOGGER.debug("BMBND040I003 paramMap : "+ paramMap);
    
    int retVal = 0; 

    if ("Y".equals(param.get("RDAT_YN"))) {
      //감면처리 거래 생성
      paramMap.put("RCVY_DVCD", param.get("RD_RCVY_DVCD"));  /*감면 거래의 회수구분코드 */
      
      retVal = dao.insert(NAMESPACE + "BMBND040I005", paramMap);
    } else {
      //가수금반제처리 거래 생성
      retVal = dao.insert(NAMESPACE + "BMBND040I004", paramMap);
    }

    //------------------------------------------------------------------------------
    // 원금 잔액 전액 상환시 원금상환여부 update
    //------------------------------------------------------------------------------
    //  TB_BMBND008 update
    if (((BigDecimal) paramBond.get("AF_UCOL_BOND_BALN")).compareTo(BigDecimal.ZERO)==0) {
      paramMap.put("BOND_ADMN_NO", param.get("BOND_ADMN_NO"));  /*채권관리번호 */
      paramMap.put("DEBT_PRTR_SEQ", param.get("DEBT_PRTR_SEQ"));  /*채무관계자순번 */
      
      int resultCnt = dao.update(NAMESPACE + "BMBND008U002", paramMap);
      //미수채권기본 이력 생성
      if (resultCnt == 1) {
        dao.insert(NAMESPACE + "BMBND002I001", paramMap);
      }
    }


    //------------------------------------------------------------------------------
    // TB_BMBND001 UPDATE : 비용잔액
    //------------------------------------------------------------------------------
    //TB_BMBND001 셋팅
    paramMap.put("BOND_ADMN_NO", param.get("BOND_ADMN_NO"));  /*채권관리번호 */
    paramMap.put("JDAC_BALN", paramBond_AF_JDAC_BALN);  /*법무비용잔액 */

    LOGGER.debug("BMBND002I001 paramMap : "+ paramMap);

    //if 수납비용 > 0
    //  update할 비용잔액 = 비용잔액 - 수납비용
    //  001update

    if (((BigDecimal) paramBond.get("RECV_JDAC")).compareTo(BigDecimal.ZERO)>0) {
      int resultCnt = dao.update(NAMESPACE + "BMBND001U003", paramMap);
      //미수채권기본 이력 생성
      if (resultCnt == 1) {
        dao.insert(NAMESPACE + "BMBND002I001", paramMap);
      }
    }

    //------------------------------------------------------------------------------
    // TB_BMBND008 UPDATE : 이자잔액
    //------------------------------------------------------------------------------
    //TB_BMBND008 셋팅
    paramMap.put("BOND_ADMN_NO", param.get("BOND_ADMN_NO"));  /*채권관리번호 */
    paramMap.put("DEBT_PRTR_SEQ", param.get("DEBT_PRTR_SEQ"));  /*채무관계자순번 */
    paramMap.put("UCOL_INTE_BALN", paramBond.get("AF_UCOL_INTE_BALN"));  /*이자잔액 */

    //  TB_BMBND008 update
    if (((BigDecimal) paramBond.get("RECV_INTE")).compareTo(BigDecimal.ZERO)>0) {
      int resultCnt = dao.update(NAMESPACE + "BMBND008U001", paramMap);
      //미수채권기본 이력 생성
      if (resultCnt == 1) {
        dao.insert(NAMESPACE + "BMBND002I001", paramMap);
      }
    }
    
    //------------------------------------------------------------------------------
    // TB_BMBND300 insert : 원금수납이 없고, 이자나 비용만 수납될 경우 거래내역 생성
    //------------------------------------------------------------------------------
    //  TB_BMBND300 insert
    if (((BigDecimal) paramBond.get("RECV_BOND")).compareTo(BigDecimal.ZERO)==0) {

      //------------------------------------------------------------------------------
      //  TB_BMBND300 INSERT
      //------------------------------------------------------------------------------
      //TB_BMBND300 INSERT 항목 셋팅
      paramMap.put("BOND_ADMN_NO", paramBond.get("BOND_ADMN_NO"));  /*채권관리번호*/

      Map<String, Object> dlgsMap = dao.select(NAMESPACE + "BMBND040S002", paramMap);
      int dlgsSqno = Integer.parseInt(dlgsMap.get("DLGS_SQNO").toString());
      paramMap.put("DLGS_SQNO", dlgsSqno); //반제 거래일련번호

      paramMap.put("BOND_SEQ", 1);  /*채권순번*/
      paramMap.put("LAST_CHMN_MPNO", param.get("LAST_CHMN_MPNO"));  /*최종변경자사번 */
      //paramMap.put("LAST_CHNG_DTTM", param.get("LAST_CHNG_DTTM"));  /*최종변경일시*/
      paramMap.put("PGM_ID", param.get("PGM_ID"));  /*프로그램ID*/
      paramMap.put("FRST_RGTR_MPNO", param.get("FRST_RGTR_MPNO"));  /*최초등록자사번 */
      paramMap.put("FRST_RGST_DTTM", param.get("FRST_RGST_DTTM"));  /*최초등록일시 */
      paramMap.put("DLGS_DATE", param.get("DLGS_DATE"));  /*거래일자 */
      paramMap.put("DLGS_STCD", "01");  /*거래상태코드 : 01 정상 */
      paramMap.put("BOND_DLGS_KDCD", "20");  /*채권거래종류코드 : 20 미수채권일반수납 */
      paramMap.put("DLGS_AMT", param.get("DLGS_AMT"));  /*거래금액*/
      paramMap.put("TRNF_PRCP", 0);  /*이관원금*/
      paramMap.put("RECV_PRCP", 0);  /*수납원금*/
      paramMap.put("RECV_IAMT", paramBond.get("RECV_INTE"));  /*수납이자금액*/
      paramMap.put("UCIT_RCAT", 0);  /*미수이자회수금액*/
      paramMap.put("TRAF_BOND_BALN", 0);  /*거래후채권잔액 */

      LOGGER.debug("BMBND300I002 paramMap : "+ paramMap);

      //  TB_BMBND300 insert
      int result_TB_BMBND300 = dao.insert(NAMESPACE + "BMBND300I002", paramMap);

    }

    //------------------------------------------------------------------------------
    //  보유 채권 별 처리
    //------------------------------------------------------------------------------
    //잔액 리스트 조회
    
    ArrayList<Map<String, Object>> paramMaplList = new ArrayList<Map<String, Object>>();

    paramMap.put("FINA_ORGN_CODE", "088"); //화면에서 입력시 금융기관코드가 들어오지 않아 강제 셋팅함 
    
    /* 
     * 금융서비스에서는 편취가 없으므로 채권관리번호로만 상계순서 조회하도록 수정하여 주석처리함.
     * 2021.06.17

    //부서가상계좌일 경우 채권관리번호로 조회
    if ("56213795323684".equals(param.get("IMAG_ACCT_NO")) || 
        "56213795323699".equals(param.get("IMAG_ACCT_NO")) || 
        "56213795323700".equals(param.get("IMAG_ACCT_NO"))
        ) {
      
      paramMaplList = dao.selectList(NAMESPACE + "BMBND100L003", paramMap);
      
    } else {

      //아니면 가상계좌번호를 조건으로 채무관계자별로 해당하는 건만 조회
      paramMaplList = dao.selectList(NAMESPACE + "BMBND100L002", paramMap);
      
    }
    */

    //채권관리번호에 해당하는 채권을 상계순서로 정렬하여 조회
    paramMaplList = dao.selectList(NAMESPACE + "BMBND100L004", paramMap);

    for (Map<String, Object> paramSeq:paramMaplList) {

      //------------------------------------------------------------------------------
      //(순번별)비교금액 : paramSeq.COMP_AMT
      //(순번별)수납 전 원금잔액 : paramSeq.BF_UCOL_BOND_BALN
      //(순번별)수납 후 원금잔액 : paramSeq.AF_UCOL_BOND_BALN
      //(순번별)수납 전 비용잔액 : paramSeq.BF_UCOL_BOND_BALN
      //(순번별)수납 후 비용잔액 : paramSeq.AF_UCOL_BOND_BALN
      //(순번별)수납 전 이자잔액 : paramSeq.BF_UCOL_BOND_BALN
      //(순번별)수납 후 이자잔액 : paramSeq.AF_UCOL_BOND_BALN
      //(순번별)수납원금 : paramSeq.RECV_BOND
      //(순번별)수납비용 : paramSeq.RECV_JDAC
      //(순번별)수납이자 : paramSeq.RECV_INTE
      //(순번별)수납원금 : paramSeq.RECV_BOND
      //------------------------------------------------------------------------------

      paramSeq.put("BF_UCOL_BOND_BALN",paramSeq.get("UCOL_BOND_BALN"));
      //paramSeq.put("BF_JDAC_BALN",paramSeq.get("JDAC_BALN"));
      //paramSeq.put("BF_UCOL_INTE_BALN",paramSeq.get("UCOL_INTE_BALN"));

      //1) 원금잔액 비교 차감
      //(채권별)수납원금 = (채권별)수납원금 - (순번별)원금잔액
      //if (채권별)수납원금 > 0
      //  (순번별)수납원금 = (순번별)원금잔액
      //else
      //  (순번별)수납원금 = (채권별)수납원금

      BigDecimal paramSeq_BF_UCOL_BOND_BALN = new BigDecimal (paramSeq.get("UCOL_BOND_BALN").toString()); //(순번별)수납 전 원금잔액      
      BigDecimal paramSeq_RECV_AMT = new BigDecimal (0); //상환금액

      LOGGER.debug("S001 paramSeq_BF_UCOL_BOND_BALN : "+ paramSeq_BF_UCOL_BOND_BALN);
      LOGGER.debug("S002 paramBond_RECV_BOND : "+ paramBond_RECV_BOND);
      paramBond_RECV_BOND = paramBond_RECV_BOND.subtract(paramSeq_BF_UCOL_BOND_BALN);
      LOGGER.debug("S003 paramBond_RECV_BOND : "+ paramBond_RECV_BOND);
      
      paramSeq_RECV_AMT = BigDecimal.ZERO;
      if (paramBond_RECV_BOND.compareTo(BigDecimal.ZERO) >= 0) {
        paramSeq_RECV_AMT = paramSeq_BF_UCOL_BOND_BALN;
        paramSeq.put("RECV_BOND",  paramSeq_RECV_AMT);
      } else {
        paramSeq_RECV_AMT = paramSeq_BF_UCOL_BOND_BALN.add(paramBond_RECV_BOND);
        paramSeq.put("RECV_BOND",  paramSeq_RECV_AMT);
      }
      LOGGER.debug("S004 paramSeq.get(RECV_BOND) : "+ paramSeq.get("RECV_BOND"));

      LOGGER.debug("S005 paramBond_RECV_JDAC : "+ paramBond_RECV_JDAC);

      //2) 비용잔액 비교 차감 : 비용 차감은 1회만 한다.(채권별로 단일하게 괸리하므로)
      if (paramBond_RECV_JDAC.compareTo(BigDecimal.ZERO) >= 0) {
        paramSeq.put("RECV_JDAC",  paramBond_RECV_JDAC);
        paramBond_RECV_JDAC = BigDecimal.ZERO; //1회 처리후 재처리 방지
      } else {
        paramSeq.put("RECV_JDAC",  0);
        paramBond_RECV_JDAC = BigDecimal.ZERO; //1회 처리후 재처리 방지
      }
      LOGGER.debug("S006 paramSeq.get(RECV_JDAC) : "+ paramSeq.get("RECV_JDAC"));

      LOGGER.debug("S007 paramBond_RECV_INTE : "+ paramBond_RECV_INTE);
      //3) 이자잔액 비교 차감 : 이자 차감은 1회만 한다. (채권별로 단일하게 괸리하므로)
      if (paramBond_RECV_INTE.compareTo(BigDecimal.ZERO) >= 0) {
        paramSeq.put("RECV_INTE",  paramBond_RECV_INTE);
        paramBond_RECV_INTE = BigDecimal.ZERO; //1회 처리후 재처리 방지
      } else {
        paramSeq.put("RECV_INTE",  0);
        paramBond_RECV_INTE = BigDecimal.ZERO; //1회 처리후 재처리 방지
      }
      LOGGER.debug("S008 paramSeq.get(RECV_INTE) : "+ paramSeq.get("RECV_INTE"));

      //수납후 잔액 계산
      BigDecimal paramSeq_RECV_BOND = new BigDecimal (paramSeq.get("RECV_BOND").toString()); //(순번별)수납원금
      BigDecimal paramSeq_RECV_JDAC = new BigDecimal (paramSeq.get("RECV_JDAC").toString()); //(순번별)수납비용
      BigDecimal paramSeq_RECV_INTE = new BigDecimal (paramSeq.get("RECV_INTE").toString()); //(순번별)수납이자

      LOGGER.debug("S009 paramSeq_RECV_BOND : "+ paramSeq_RECV_BOND);
      LOGGER.debug("S010 paramSeq_RECV_JDAC : "+ paramSeq_RECV_JDAC);
      LOGGER.debug("S011 paramSeq_RECV_INTE : "+ paramSeq_RECV_INTE);

      //채권 순번별 수납 후 원금잔액 = 채권 순번별 수납 전 원금잔액 - 채권 순번별 수납원금
      LOGGER.debug("S013 paramSeq_BF_UCOL_BOND_BALN : "+ paramSeq_BF_UCOL_BOND_BALN);
      LOGGER.debug("S014 paramSeq_RECV_BOND : "+ paramSeq_RECV_BOND);
      BigDecimal paramSeq_AF_UCOL_BOND_BALN = paramSeq_BF_UCOL_BOND_BALN.subtract(paramSeq_RECV_BOND);
      LOGGER.debug("S015 paramSeq_AF_UCOL_BOND_BALN : "+ paramSeq_AF_UCOL_BOND_BALN);

      //채권 순번별 수납 후 비용잔액 = 채권 순번별 수납 전 비용잔액 - 채권 순번별 수납비용
      //BigDecimal paramSeq_AF_JDAC_BALN = paramSeq_BF_JDAC_BALN.subtract(paramSeq_RECV_JDAC);

      //채권 순번별 수납 후 이자잔액 = 채권 순번별 수납 전 이자잔액 - 채권 순번별 수납이자
      //BigDecimal paramSeq_AF_UCOL_INTE_BALN = paramSeq_BF_UCOL_INTE_BALN.subtract(paramSeq_RECV_INTE);

      paramSeq.put("AF_UCOL_BOND_BALN", paramSeq_AF_UCOL_BOND_BALN);      
      LOGGER.debug("S016 paramSeq.get(AF_UCOL_BOND_BALN) : "+ paramSeq.get("AF_UCOL_BOND_BALN"));
      //paramSeq.put("AF_JDAC_BALN", paramSeq_AF_JDAC_BALN);
      //paramSeq.put("AF_UCOL_INTE_BALN", paramSeq_AF_UCOL_INTE_BALN);

      //금액 확인
      LOGGER.debug("S016 paramSeq_BF_UCOL_BOND_BALN : "+ paramSeq_BF_UCOL_BOND_BALN);
      //LOGGER.debug("paramSeq_BF_JDAC_BALN : "+ paramSeq.get("BF_JDAC_BALN"));
      //LOGGER.debug("paramSeq_BF_UCOL_INTE_BALN : "+ paramSeq.get("BF_UCOL_INTE_BALN"));

      LOGGER.debug("paramSeq_RECV_BOND : "+ paramSeq.get("RECV_BOND"));
      LOGGER.debug("paramSeq_RECV_JDAC : "+ paramSeq.get("RECV_JDAC"));
      LOGGER.debug("paramSeq_RECV_INTE : "+ paramSeq.get("RECV_INTE"));

      LOGGER.debug("paramSeq_AF_UCOL_BOND_BALN : "+ paramSeq.get("AF_UCOL_BOND_BALN"));
      //LOGGER.debug("paramSeq_AF_JDAC_BALN : "+ paramSeq.get("AF_JDAC_BALN"));
      //LOGGER.debug("paramSeq_AF_UCOL_INTE_BALN : "+ paramSeq.get("AF_UCOL_INTE_BALN"));

      //------------------------------------------------------------------------------
      //  TB_BMBND100 UPDATE
      //------------------------------------------------------------------------------

      //TB_BMBND100 update 항목 셋팅
      paramMap.put("BOND_ADMN_NO", param.get("BOND_ADMN_NO"));  /*채권관리번호 */
      paramMap.put("BOND_SEQ", paramSeq.get("BOND_SEQ"));  /*채권순번 */
      paramMap.put("UCOL_BOND_BALN", paramSeq_AF_UCOL_BOND_BALN);  /*원금잔액 = 거래후채권잔액 */
      
      LOGGER.debug("BMBND100U003 paramMap : "+ paramMap);

      dao.update(NAMESPACE + "BMBND100U003", paramMap);

      //------------------------------------------------------------------------------
      //  미수채권상세 이력 생성(BMBND101I002)
      //------------------------------------------------------------------------------
      
      dao.insert(NAMESPACE + "BMBND101I002", paramMap);

      //------------------------------------------------------------------------------
      //  TB_BMBND300 INSERT
      //------------------------------------------------------------------------------
      //TB_BMBND300 INSERT 항목 셋팅
      paramMap.put("BOND_ADMN_NO", paramBond.get("BOND_ADMN_NO"));  /*채권관리번호*/

      Map<String, Object> dlgsMap = dao.select(NAMESPACE + "BMBND040S002", paramMap);
      int dlgsSqno = Integer.parseInt(dlgsMap.get("DLGS_SQNO").toString());
      paramMap.put("DLGS_SQNO", dlgsSqno); //반제 거래일련번호

      paramMap.put("BOND_SEQ", paramSeq.get("BOND_SEQ"));  /*채권순번*/
      paramMap.put("LAST_CHMN_MPNO", param.get("LAST_CHMN_MPNO"));  /*최종변경자사번 */
      //paramMap.put("LAST_CHNG_DTTM", param.get("LAST_CHNG_DTTM"));  /*최종변경일시*/
      paramMap.put("PGM_ID", param.get("PGM_ID"));  /*프로그램ID*/
      paramMap.put("FRST_RGTR_MPNO", param.get("FRST_RGTR_MPNO"));  /*최초등록자사번 */
      paramMap.put("FRST_RGST_DTTM", param.get("FRST_RGST_DTTM"));  /*최초등록일시 */
      paramMap.put("DLGS_DATE", param.get("DLGS_DATE"));  /*거래일자 */
      paramMap.put("DLGS_STCD", "01");  /*거래상태코드 : 01 정상 */
      paramMap.put("BOND_DLGS_KDCD", "20");  /*채권거래종류코드 : 20 미수채권일반수납 */
      paramMap.put("DLGS_AMT", param.get("DLGS_AMT"));  /*거래금액*/
      paramMap.put("TRNF_PRCP", 0);  /*이관원금*/

      paramMap.put("RECV_PRCP", paramSeq_RECV_BOND);  /*수납원금*/
      paramMap.put("RECV_IAMT", paramSeq_RECV_INTE);  /*수납이자금액*/
      paramMap.put("UCIT_RCAT", 0);  /*미수이자회수금액*/
      paramMap.put("TRAF_BOND_BALN", paramSeq_AF_UCOL_BOND_BALN);  /*거래후채권잔액 = 거래후채권잔액 */

      LOGGER.debug("BMBND300I002 paramMap : "+ paramMap);

      //  TB_BMBND300 insert
      int result_TB_BMBND300 = dao.insert(NAMESPACE + "BMBND300I002", paramMap);
      
      //  update할 수납원금이 없으면 for 중지
      if (paramBond_RECV_BOND.compareTo(BigDecimal.ZERO) <= 0) {

        break;

      }

    } // end for
  
    //------------------------------------------------------------------------------
    //  TB_RCRCP001 update
    //------------------------------------------------------------------------------

    Map<String, Object> pendMap =new HashMap<String, Object>(); 
    
    if ("Y".equals(param.get("RDAT_YN"))) {
      //감면처리 거래 시 skip
      
    } else {
      //가수금반제 처리 시
      paramMap.put("DLGS_AMT", param.get("DLGS_AMT"));
      paramMap.put("PEND_BALN", param.get("DLGS_AMT"));
      paramMap.put("DLGS_SQNO", paramMap.get("ORDS_SQNO"));
      paramMap.put("MPNO", paramMap.get("RCNM_MPNO"));
      
      LOGGER.debug("RCRCP001U001 paramMap : "+ paramMap);
      
      dao.update(NAMESPACE_rec + "RCRCP001U001", paramMap);
      
      //같은 전문에 가수생성 미결잔액이 0이 되면 입금내역 전문에 상태를 반제(03)로 반영      
      pendMap = dao.select(NAMESPACE_rec + "RCRCP001S002", paramMap);
      
      LOGGER.debug("RCRCP001S002 pendMap : "+ pendMap);
    }
    
    
    
    //------------------------------------------------------------------------------
    //  TB_RCRCP900 update
    //------------------------------------------------------------------------------
    if ("Y".equals(param.get("RDAT_YN"))) {
      //감면처리 거래 시 skip
      
    } else {
      //가수금반제 처리 시
      if (pendMap != null && pendMap.containsKey("BOND_ADMN_NO") && !"".equals(pendMap.get("BOND_ADMN_NO"))) {
        pendMap.put("MPNO", paramMap.get("MPNO")); /*최종변경자사번*/
        pendMap.put("PGM_ID", paramMap.get("PGM_ID")); /*프로그램ID*/
        pendMap.put("RECV_RFLT_STCD", "03");
        dao.update(NAMESPACE_rec + "RCRCP900U001", pendMap);
      }
      
    }
    
    Map<String, Object> resultMap = new HashMap<String, Object>();
    
    resultMap.put("status", "S");
    resultMap.put("msg", "");
    resultMap.put("dlgsSqno", retVal);
    
    return resultMap;
        
  }

  
  /**
   * 가상계좌건별입금내역관리 일반수납
   * @param request - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */  
  protected Map<String, Object> insertCommRcpt(final Map<String, Object> inputMap) throws Exception {
 
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("insertCommRcpt == start");    
    }    

    LOGGER.debug("insertCommRcpt inputMap : " + inputMap);
    
    Map<String, Object> resultMap = new HashMap<String, Object>();
    Map<String, Object> ucitMap = new HashMap<String, Object>();

    inputMap.put("ORDS_BOND_ADNO", inputMap.get("BOND_ADMN_NO")); //원거래채권관리번호
    inputMap.put("ORDS_SQNO", inputMap.get("DLGS_SQNO"));         //원거래일련번호
    
    ucitMap.put("MPNO", inputMap.get("MPNO"));
    ucitMap.put("PGM_ID", inputMap.get("PGM_ID"));
    ucitMap.put("BOND_ADMN_NO", inputMap.get("BOND_ADMN_NO")); //채권관리번호
    ucitMap.put("UCIT_AMT", inputMap.get("UCIT_AMT")); //미수이자 원금액
       
    //미수이자여부 
    String ucitRactYn = inputMap.get("UCIT_RCAT_YN").toString();
    String ucitInsYn = null;
    if (inputMap.get("UCIT_INS_YN") !=null) {
      ucitInsYn = inputMap.get("UCIT_INS_YN").toString();
    }
    String bondKindCode = inputMap.get("BOND_KIND_CODE").toString();
    
    //미수이자 회수 대상이나 미수이자 미생성(수수료채권의 경우 MIG시 정리 가능)된건은 임의로 미수이자를 생성 후 정리
    if ("Y".equals(ucitInsYn) && "10".equals(bondKindCode)) {
      vracRecvService.updateUcitInfo(ucitMap);
    }
    
    //미수채권상세 정보 조회 (BMBND100L001 일반, BMBND007L001 미수이자회수금액 있을시)    
    ArrayList<Map<String, Object>> bondDetailList = vracRecvService.selectBondDetailInfo(inputMap);
    
    Map<Integer, BigDecimal> sumInccMap = new HashMap<Integer, BigDecimal>();//채권순번별 합산용
    
    //미수이자 수납이 아니면 이자금액산출
    ArrayList<Map<String, Object>> inccList = new ArrayList<Map<String, Object>>();//이자내역    
    if (!"Y".equals(ucitRactYn)) {
      Map<String, Object> inccSearchMap = new HashMap<String, Object>();
      
      inccSearchMap.put("BOND_ADMN_NO", inputMap.get("BOND_ADMN_NO"));
      inccSearchMap.put("CUST_ID", inputMap.get("CUST_ID"));       
      inccSearchMap.put("DLGS_DATE", inputMap.get("DLGS_DATE"));
        
      ArrayList<Map<String, Object>> resulInccRtim = recvCommService.selectInccRtim(inccSearchMap);
          
      if (resulInccRtim.size() > 0) {
        
        BigDecimal totIncc = new BigDecimal(resulInccRtim.get(0).get("TOT_INCC").toString());
        if (totIncc.compareTo(BigDecimal.ZERO) > 0) {        
          //필드명 매핑
          for (Map<String, Object> tempMap:resulInccRtim) {          
            tempMap.put("INCC_SEQ", tempMap.get("DLGS_SQNO"));
            tempMap.put("RCKN_DATE", tempMap.get("AF_DLGS_DATE"));//최종기산일
            
            if ("10".equals(bondKindCode)) {//환수수수료채권은 첫번째 정보에 통금액만 사용
              tempMap.put("BOND_SEQ", bondDetailList.get(0).get("BOND_SEQ"));//현재 채권상세 중 천번째 에 달아준다.
            }
            
            inccList.add(tempMap);
          }                                    
        }
      }             
    }
    
    //수납정보
    BigDecimal recvBondAmt = new BigDecimal(inputMap.get("RECV_BOND_AMT").toString());//(수납원금)
    BigDecimal recvUcitAmt = new BigDecimal(inputMap.get("RECV_UCIT_AMT").toString());//(수납이자)
     
    //채권상세거래내역 리스트   
    ArrayList<Map<String, Object>> bondDetaiDlgslList = new ArrayList<Map<String, Object>>();

    //채권상세(순번)별 이자금액 생성(inccList 채권순번별 합산)               
    if (!"Y".equals(ucitRactYn)) {
      for (Map<String, Object> tempMap:inccList) {
        int bondSeq = (int)Double.parseDouble(tempMap.get("BOND_SEQ").toString());
              
        if (sumInccMap.containsKey(bondSeq)) {
          sumInccMap.put(bondSeq, sumInccMap.get(bondSeq).add(BigDecimal.valueOf(Double.parseDouble(tempMap.get("INCC_AMT").toString()))));
        } else {
          sumInccMap.put(bondSeq, BigDecimal.valueOf(Double.parseDouble(tempMap.get("INCC_AMT").toString())));
        }      
      }            
    }
    //채권상세 수정 및 채권상세거래내역 입력 자료 생성
    BigDecimal ucolBondLaln = new BigDecimal("0");
    BigDecimal inccAmt = new BigDecimal("0");
    
    for (Map<String, Object> tempMap:bondDetailList) {
      
      tempMap.put("DLGS_DATE", inputMap.get("DLGS_DATE"));//거래일자=입금일자
      tempMap.put("DLGS_AMT", inputMap.get("DLGS_AMT"));//거래금액=입금금액
      tempMap.put("BOND_DLGS_KDCD", inputMap.get("BOND_DLGS_KDCD"));//채권거래종류코드
      
      tempMap.put("RECV_IAMT", 0);//기본값 셋
      
      if (!"Y".equals(ucitRactYn)) {
        tempMap.put("UCIT_RCAT", 0);//미수이자회수금액 --원금 상환 이후에 발생
      }
       
      ucolBondLaln = BigDecimal.valueOf(Double.parseDouble(tempMap.get("UCOL_BOND_BALN").toString()));//미수채권잔액
       
      if (recvBondAmt.compareTo(ucolBondLaln) >= 0) {//수납금액이 채권순번에 잔액보다 크거나 같으면 
        recvBondAmt = recvBondAmt.subtract(ucolBondLaln);//수납금액에서 잔액을 차감       
        tempMap.put("UCOL_BOND_BALN", BigDecimal.ZERO); //미수채권순번잔액  0
        tempMap.put("TRAF_BOND_BALN", BigDecimal.ZERO); //거래후채권잔액 상세에 들어가는 미수채권잔액과 동일
        tempMap.put("RECV_PRCP", ucolBondLaln);//채권순번에 수납금액은 미수채권순번잔액
         
      } else {
        tempMap.put("UCOL_BOND_BALN", ucolBondLaln.subtract(recvBondAmt)); //미수채권순번잔액-수납금액
        tempMap.put("TRAF_BOND_BALN", ucolBondLaln.subtract(recvBondAmt)); //거래후채권잔액 상세에 들어가는 미수채권잔액과 동일
        tempMap.put("RECV_PRCP", recvBondAmt);//채권순번에 수납금액은 수납금액       
         
        recvBondAmt = BigDecimal.ZERO;//수납금액이 잔액보다 작으니 사용 후에 0 처리
      }
      
      /*
      * 수납이자금액이 존재하면 채권순번별로 수납이자금액을 생성하여준다. 
      * 현재 채무자 미수회수대상 이자금액과 이자잔액 
      */
      if (sumInccMap.size() > 0) {//이자계산내역을 순번별로 합산
        
        LOGGER.debug("sumInccMap.toString()===>"+sumInccMap.toString());
        
        if (sumInccMap.containsKey(Integer.parseInt(tempMap.get("BOND_SEQ").toString()))) {
          inccAmt = BigDecimal.valueOf(Double.parseDouble(sumInccMap.get(Integer.parseInt(tempMap.get("BOND_SEQ").toString())).toString()));//채권순번에 이자금액
          
          tempMap.put("UCIT_AMT", inccAmt);//이자금액
          
          if (recvUcitAmt.compareTo(BigDecimal.ZERO) > 0) {//미수회수이자는 뒤에서 쿼리 금액 분배
            
            if (recvUcitAmt.compareTo(inccAmt) >= 0) { //            
              tempMap.put("RECV_IAMT", inccAmt);
              recvUcitAmt = recvUcitAmt.subtract(inccAmt);//수납이자금액에서 채권수번별이자금액을 차감            
              tempMap.put("UCOL_INTE_BALN", 0);//이자자액            
            } else {
              tempMap.put("RECV_IAMT", recvUcitAmt);//수납이자금액이 이자금액보다 작으므로            
              tempMap.put("UCOL_INTE_BALN", inccAmt.subtract(recvUcitAmt));//이자잔액            
              recvUcitAmt = BigDecimal.ZERO;//수납이자금액이 잔액보다 작으니 사용 후에 0 처리
            }          
            
          } else {
            tempMap.put("RECV_IAMT", 0);//수납이자금액 발생하지 않아 0          
            tempMap.put("UCOL_INTE_BALN", inccAmt);//이자잔액          
          }          
        } else {
          tempMap.put("UCIT_AMT", 0);//해당 순번에 이자 발생 하지 않음이자금액
          tempMap.put("UCOL_INTE_BALN", 0);//이자잔액
        }
        
        LOGGER.debug("sumInccMap tempMap.toString()===>"+tempMap.toString());
      } else {
        tempMap.put("UCIT_AMT", 0);
      }
      
      bondDetaiDlgslList.add(tempMap);
      
    }
         
    if (bondDetaiDlgslList.size() > 0) {     
      Map<String, Object> resultRfmtMap = vracRecvService.saveRfmtAmt(inputMap, bondDetaiDlgslList, inccList);        
      
      resultMap.put("status", resultRfmtMap.get("status"));
      resultMap.put("msg", resultRfmtMap.get("msg"));
      resultMap.put("dlgsSqno", resultRfmtMap.get("dlgsSqno"));                                    
    }               
     
    return resultMap;
  }
  
  /**
   * 가상계좌건별입금내역관리 일반수납 미수이자회수
   * @param request - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */  
//  protected Map<String, Object> insertUcitRact(final Map<String, Object> inputMap) throws Exception {
//    
//    Map<String, Object> resultMap = new HashMap<String, Object>();
//     
//    inputMap.put("ORDS_BOND_ADNO", inputMap.get("BOND_ADMN_NO")); //원거래채권관리번호
//    inputMap.put("ORDS_SQNO", inputMap.get("DLGS_SQNO"));         //원거래일련번호      
//    
//    Map<String, Object> resultRfmtMap = vracRecvService.saveUcitRact(inputMap);        
//    
//    resultMap.put("status", resultRfmtMap.get("status"));
//    resultMap.put("msg", resultRfmtMap.get("msg"));
//    resultMap.put("dlgsSqno", resultRfmtMap.get("dlgsSqno"));         
//      
//    
//    return resultMap;
//  }
 
  /**
  * 가상계좌건별입금내역관리 과납송금상태 변경
  * @param request - HttpServletRequest
  * @return String - CommonMap to string
  * @exception Exception
  */   
  @ResponseBody
  @RequestMapping(value = "/rc/rcp/approveXcpyRvsn.sbm", method=RequestMethod.POST)
  public Map<String, Object> approveXcpyRvsn(final HttpServletRequest request) throws Exception {  
    Result result = new Result();
    
    Map<String, Object> param = (Map<String, Object>) request.getAttribute("params");            
    Map<String, Object> inputMap = (Map<String, Object>) param.get("dma_xcpyInfo");    
    
    inputMap.put("MPNO", param.get("MPNO"));
    inputMap.put("PGM_ID", param.get("PGM_ID"));
    
    ApprovalInfoVO approvalInfoVO = new ApprovalInfoVO();

    // 결재ID
    approvalInfoVO.setAprvId(inputMap.get("APRV_ID").toString());
    // 승인상태코드
    approvalInfoVO.setRcntStatCode(inputMap.get("RCNT_STAT_CODE").toString());
    // 결재기관코드
    approvalInfoVO.setAprvOrgnCode(param.get("ORGN_CODE").toString());      
    // 사원번호
    approvalInfoVO.setEmplNo(param.get("MPNO").toString());      
    
    if (CommonComponent.updateApprovalInfo(approvalInfoVO)) {
      int updateResultInt = vracRecvService.updateRmtnResult(inputMap);
      
      if (updateResultInt < 0) {
        result.setMsg("E0003");
      } else {
        if ("02".equals(inputMap.get("DLNG_STAT_CODE"))) {
          result.setMsg("S0013");//승인
        } else if ("04".equals(inputMap.get("DLNG_STAT_CODE"))) {
          result.setMsg("S0018");//반려
        }
      }      
    } else {
      result.setMsg("E0003");
    }
    
    return result.getResult();
    
  }
  
}
