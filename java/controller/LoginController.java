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

import java.net.InetAddress;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hanwhalife.nbm.service.cm.MenuService;
import com.hanwhalife.nbm.service.cm.UserService;
import com.hanwhalife.nbm.service.cm.com.CommonComponent;
import com.hanwhalife.nbm.util.CommonUtil;
import com.hanwhalife.nbm.util.PropertyFactory;
import com.hanwhalife.nbm.util.Result;
import com.hanwhalife.nbm.util.StringUtil;
import com.hanwhalife.nbm.vo.cm.EsbUserInfoVO;




/**
 * <b>Description:</b>
 * <p>
 * LoginController 사용자 관련 컨트롤러 입니다.
 * 
 * @since 2019-07-03
 * @version 1.0
 * @author 1073302
 * @see
 * 
 *      <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-07-03  1073302     최초생성
 *      </pre>
 */
@Controller
public class LoginController {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

  @Autowired
  UserService userService;
  @Autowired
  MenuService menuService;
  
  
  /**
   * 로그인화면 호출
   * 로그인되어있으면 메인화면 호출
   * @param request
   *          - HttpServletRequest
   * @return model - Model
   * @exception Exception
   */
  @RequestMapping(value = "/index.do", method = RequestMethod.GET)
  public String index(final HttpServletRequest request, final Model model) throws Exception {
    LOGGER.debug("index === " + System.getProperty("SMSSO"));
    HttpSession session =  request.getSession();
    String mpno = (String)session.getAttribute("MPNO");
    LOGGER.debug("index mpno === " + mpno);
    if (StringUtil.isBlank(mpno)) {
      model.addAttribute("movePage", "/online/cm/main/login.xml");
    } else {
      model.addAttribute("movePage", "/online/cm/main/main.xml");
    }
    
    
    
    return "/websquare";
  }
  
  /**
   * 로그인
   * @param request
   *          - HttpServletRequest
   * @return model - Model
   * @exception Exception
   */
  @RequestMapping(value = "/login.do", method = RequestMethod.GET)
  public String login(final HttpServletRequest request, final Model model) throws Exception {
    LOGGER.debug("login === ");
    
    InetAddress OpServerIp1 = InetAddress.getByName("10.10.153.190");
    //InetAddress OpServerIp2 = InetAddress.getByName("10.10.163.30");
    //InetAddress OpServerIp3 = InetAddress.getByName("10.10.153.68");
    
    if (OpServerIp1.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress()) 
/*        || OpServerIp2.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())
        || OpServerIp3.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())*/) {
      model.addAttribute("server", "(개발서버)");
    }else {
      model.addAttribute("server","");
    }
    return "/common/login";
  }
  
  /**
   * 웹스퀘어 메인
   * @param request
   *          - HttpServletRequest
   * @return model - Model
   * @exception Exception
   */
  @RequestMapping(value = "/main.do", method = RequestMethod.GET)
  public String main(final HttpServletRequest request, final Model model) throws Exception {
    LOGGER.debug("main === ");
    
    
   
     model.addAttribute("movePage", "/online/cm/main/main.xml");
     return "/websquare";
  }
  
  
  /**
   * SSO 로그인 프로세스
   * @param request
   *          - HttpServletRequest
   * @return model - Model
   * @exception Exception
   */
  @RequestMapping(value = "/login_proc.do", method = RequestMethod.POST)
  public String loginProc(final HttpServletRequest request, final Model model) {
    String redirectUrl = "redirect:/main.do";
    String userId = StringUtil.nullToEmpty(request.getParameter("SM_USER"));
    try {
      // SSO에이전트에서 넘어온 사원정보
      if (userId.isEmpty()) {
        model.addAttribute("noticeMsg", "존재하지 않는 사원번호이거나 비밀번호가 맟지 않습니다.\\n확인 후 다시 시도바랍니다.");
        redirectUrl = "/common/notice";
      } else {
        // 시스템 업무사용자 정보 확인
        if (userService.executeSSOLogIn(userId) == false) {
          model.addAttribute("noticeMsg", "시스템 사용권한이 없는 사용자입니다.\\n업무사용자 등록 후 사용바랍니다.");
          redirectUrl = "/common/notice";
        }
      }
    } catch (Exception ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }
    return redirectUrl;
  }
  
  /**
   * ESB로그인
   * 
   * @param request - HttpServletRequest
   * @return String - string
   * @exception Exception
   */
  @RequestMapping(value = "/esb/executeLogIn.do", method = RequestMethod.POST)
  public String executeEsbLogIn(final HttpServletRequest request, final Model model) {
    LOGGER.debug("executeEsbLogIn == " );
    HttpSession session = request.getSession();
    String redirectUrl = "redirect:/main.do";
    
    String emplNo = request.getParameter("USER");
    String password = request.getParameter("PASSWORD");
    LOGGER.debug("encryptRsa");
    LOGGER.debug("emplNo == " + emplNo);
    LOGGER.debug("password == " + password);
    
    PrivateKey privateKey = (PrivateKey) session.getAttribute("rsaPrivateKey");
    if (privateKey == null) {
      throw new RuntimeException("암호화 비밀키 정보를 찾을 수 없습니다.");
    }
    try {
      emplNo = CommonUtil.decryptRsa(privateKey, emplNo);
      password = CommonUtil.decryptRsa(privateKey, password);
      
      LOGGER.debug("decryptRsa");
      LOGGER.debug("emplNo == " + emplNo);
      LOGGER.debug("password == " + password);
      
      // 세션 정보 업데이트
      session.invalidate();
      session = request.getSession();
      
      // 로그인 사원번호 / 비밀번호 세팅
      EsbUserInfoVO esbUserInfoVO = new EsbUserInfoVO();
      esbUserInfoVO.setEmplNo(emplNo);
      esbUserInfoVO.setPassWord(password);
      // ESB를 통한 사원정보 확인 TODO
      //esbUserInfoVO = CommonComponent.getPasswordChk(esbUserInfoVO);
      // 테스트를 위한 로그인성공여부       
      LOGGER.debug("esbUserInfoVO == " + esbUserInfoVO.toString());
      esbUserInfoVO.setSuccess(true);
      // 비밀번호 체크 성공
     if (esbUserInfoVO.isSuccess()) {
       
        // 시스템 업무사용자 정보 확인
       // if (userService.executeSSOLogIn(emplNo) == false) {
       
       
       
        switch(userService.executeLdapLogIn(emplNo,password)) {
         case "1":
           model.addAttribute("noticeMsg","사용자를 찾을 수 없음");
           redirectUrl = "/common/notice";
           break;
         case "2":
           model.addAttribute("noticeMsg","사용자는 암호를 재설정해야 합니다");
           redirectUrl = "/common/notice";
           break;
         case "3":
           model.addAttribute("noticeMsg","ID와 비밀번호가 일치하지 않습니다. 확인 후 다시 시도해 주십시오");
           redirectUrl = "/common/notice";
           break;
         case "4":
           model.addAttribute("noticeMsg","입력한 ID는 비활성화 상태입니다");
           redirectUrl = "/common/notice";
           break;
         case "5":
           model.addAttribute("noticeMsg","암호가 만료되었습니다");
           redirectUrl = "/common/notice";
           break;
         case "6":
           model.addAttribute("noticeMsg","AD에서 계정이 만료됨");
           redirectUrl = "/common/notice";
           break;
         case "7":
         default:
           //model.addAttribute("noticeMsg","정상");
           break;
           
             
         }
        
      
        
        
          // 업무사용자 등록 메시지
          //model.addAttribute("noticeMsg", "시스템 사용권한이 없는 사용자입니다.\\n업무사용자 등록 후 사용바랍니다.");
          //redirectUrl = "/common/notice";
        
      // 로그인 실패 시 메시지
      } else {
        redirectUrl = "/common/notice";
        // 비밀번호 오류
        if ("00".equals(esbUserInfoVO.getReturnCode())) {
          if (esbUserInfoVO.getTdayLognCnt() < 5) {
            model.addAttribute("noticeMsg", "올바른 비밀번호가 아닙니다.\\n"
                + "비밀번호가 5회이상 틀렸을 경우 일정시간 접근이 불가합니다."
                + "\\n("+ esbUserInfoVO.getTdayLognCnt()+"회 오류)");
          } else {
            model.addAttribute("noticeMsg", "비밀번호 5회 오류로 10분간 로그인이 불가합니다.\\n잠시후 다시 시도바랍니다.");
          }
        } else {
          model.addAttribute("noticeMsg", "로그인 오류가 발생했습니다.\\n확인 후 다시 시도바랍니다.");
        }
      }
    } catch (Exception ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }
    return redirectUrl;
  }
    
  /**
   * SSO 로그아웃 프로세스
   * @param request
   *          - HttpServletRequest
   * @return model - Model
   * @exception Exception
   */
  @RequestMapping(value = "/logout.do", method = RequestMethod.GET)
  public String logout(final HttpServletRequest request, final Model model) throws Exception {
    LOGGER.debug("logout === ");
    return "/common/logout";
  }
  
  /**
   * SSO 로그인 프로세스
   * @param request
   *          - HttpServletRequest
   * @return model - Model
   * @exception Exception
   */
  @RequestMapping(value = "/protect.do", method = RequestMethod.GET)
  public String protect(final HttpServletRequest request, final Model model) throws Exception {
    LOGGER.debug("protect === ");
    return "/common/protect";
  }
  
  /**
   * 로그인
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/sso/executeLogIn.do", method = RequestMethod.POST)
  public Map<String, Object> executeLogIn(final HttpServletRequest request, @RequestBody final Map<String, Object> param) throws Exception {
    LOGGER.debug("executeLogIn == " + param.toString());
    
   
   
    Result result = userService.executeLogIn(param);
    return result.getResult();
  }
  
  
  /**
   * 로그아웃
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/sso/executeLogout.do", method = RequestMethod.POST)
  public Map<String, Object> executeLogout(final HttpServletRequest request) throws Exception {
    LOGGER.debug("executeLogout == ");
    
    request.getSession().invalidate();
    Result result =  new Result();
    result.setData("SUCC_YN", "TRUE");
    
    return result.getResult();
  }
  
  /**
   * 로그아웃
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @RequestMapping(value = "/sessionTimeout.sbm", method = RequestMethod.POST)
  public void sessionTimeout(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    LOGGER.debug("sessionTimeout == ");
    
    request.getSession().invalidate();
    
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write("{\"rsMsg\":{\"statusCode\":\"E\", \"messageCode\" : \"E0013\", \"message\":\"세션이 종료 되었습니다. 로그인페이지로 이동합니다.\",\"popYn\":\"Y\"}}");
  }
  
  /**
   * 메인페이지 로드 후 사용자 메뉴세팅
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/cm/mainLoadded.sbm", method = RequestMethod.POST)
  public  Map<String, Object> mainLoadded(final HttpServletRequest request) throws Exception {
    LOGGER.debug("mainLoadded == ");
    HttpSession session = request.getSession();
    Result result =  new Result();
    Map<String, Object> paramMap = new HashMap<>();
    Map<String, Object> dma_scrnProc = new HashMap<>();
    //서버정보
    String serverDivi = "LOCAL";
    String localAddr = request.getLocalAddr();
    
    InetAddress OpServerIp1 = InetAddress.getByName("10.10.153.190");
//    InetAddress OpServerIp2 = InetAddress.getByName("10.10.163.30");
//    InetAddress OpServerIp3 = InetAddress.getByName("10.10.153.68");
        
    if (localAddr.equals(PropertyFactory.getProperty("system.prod1"))) {
      serverDivi = "AP1";
    } else if (localAddr.equals(PropertyFactory.getProperty("system.prod2"))) {
      serverDivi = "AP2";
    } else if (localAddr.equals(PropertyFactory.getProperty("system.dev"))) {
      serverDivi = "DEV";
    } 
    // 미수채권 운영기 서버 IP 연결 시 운영기 구분
    else if (OpServerIp1.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress()) 
/*        || OpServerIp2.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())
        || OpServerIp3.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())*/) {
      serverDivi = "개발서버";
    }
    
    
  

    
    dma_scrnProc.put("AUTH_GRP_ID", session.getAttribute("AUTH_GRP_ID"));
    paramMap.put("EMPL_NO", session.getAttribute("MPNO"));
    paramMap.put("AUTH_GRP_ID", session.getAttribute("AUTH_GRP_ID"));
    paramMap.put("START_MENU_LEVL", 1);
    paramMap.put("dma_scrnProc", dma_scrnProc);
    
    result.setData("dlt_menu", userService.selectMenuGrp(paramMap));

      result.setData("dlt_favorite", userService.selectQuickMenu(paramMap));  
      result.setData("dlt_authProcMain", (ArrayList<Map<String, Object>>) menuService.selectScrnProc(paramMap).getData("dlt_scrnProc"));
    Map<String, Object> sessionMap = new HashMap<String, Object>();
    
    sessionMap.put("EMPL_NO", (String)session.getAttribute("MPNO"));
    sessionMap.put("EMPL_NAME", (String)session.getAttribute("MPNM"));
    sessionMap.put("ORGN_CODE", (String)session.getAttribute("ORGN_CODE"));
    sessionMap.put("OGRN_NAME", (String)session.getAttribute("OGRN_NAME"));
    sessionMap.put("AUTH_GRP_ID", (String)session.getAttribute("AUTH_GRP_ID"));
    sessionMap.put("SERVER_DIVI", serverDivi);
    result.setData("dma_session", sessionMap);
    
    return result.getResult();
  }
  
  
  /**
   * 사용자 퀵메뉴 저장
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/cm/insertQuickMenu.sbm", method = RequestMethod.POST)
  public  Map<String, Object> insertQuickMenu(final HttpServletRequest request) throws Exception {
    LOGGER.debug("insertQuickMenu == ");
    Map<String, Object> paramMap = (Map<String, Object>) request.getAttribute("params");
    
    return userService.insertQuickMenu(paramMap).getResult();
  }
  
  /**
   * 사용자 퀵메뉴 삭제
   * 
   * @param request
   *          - HttpServletRequest
   * @return String - CommonMap to string
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value = "/cm/deleteQuickMenu.sbm", method = RequestMethod.POST)
  public  Map<String, Object> deleteQuickMenu(final HttpServletRequest request) throws Exception {
    LOGGER.debug("deleteQuickMenu == ");
    Map<String, Object> paramMap = (Map<String, Object>) request.getAttribute("params");
    
    return userService.deleteQuickMenu(paramMap).getResult();
  }
  
  
  

}
