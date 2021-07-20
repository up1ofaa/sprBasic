package com.hanwhalife.nbm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <b>Description:</b><p>
 * Socket통신 관련 클래스입니다.
 * @since 2019-07-22
 * @version 1.0
 * @author 1073302
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2019-07-22  1073302     최초생성
 * </pre>
 */
@Component
public class SocketClient {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);
  private static final String CHARSET = "euc-kr";
  
  private Socket socket = new Socket();
  private BufferedWriter writer;
  private BufferedReader reader;
  private OutputStream out;
  private InputStream in;
  
  /**
   * socket open
   * 
   * @return void
   */
  public void setSocket(final String ip, final int port) throws IOException {
    SocketAddress addr = new InetSocketAddress(ip, port);
    socket.connect(addr);
    
    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),CHARSET));
    reader= new BufferedReader(new InputStreamReader(socket.getInputStream(),CHARSET));
    in = socket.getInputStream();
    out = socket.getOutputStream();
    
  }
    
  /**
   * send massage
   * 
   * @return void
   */
  public void send(final int headerLen, final int length, final String msg) throws IOException {
    PrintWriter out = new PrintWriter(writer, true);
    out.println(msg);
//    writer.write(msg);
//    writer.flush();
  }
//  /**
//   * @Deprecated
//   * send(int headerLen, int length, String msg) 사용 바랍니다.
//   */
//  @Deprecated
//  public void send(String msg) throws IOException {
//    PrintWriter out = new PrintWriter(writer, true);
//    out.println(msg);
//  }
  
  /**
   * send massage
   * @param msg - 보낼 메시지
   * @param length - 메시지길이
   * @param headerLen - 헤더길이
   * @return void
   * @throws Exception 
   */
  public String sendToRet(final int headerLen, final int length, final String msg) throws IOException {
    LOGGER.debug("sendToRet == ");
    String str = "";
    //String sendMsg = StringUtil.leftPad(length, headerLen, "0") + msg;
    String sendMsg=msg;//길이 제거요청 2021.06.29 박여경대리님
    LOGGER.debug("sendMsg 개행제거 전 len == " + sendMsg.getBytes().length);
    //sendMsg=sendMsg.replaceAll("\n", "");//개행제거
    //LOGGER.debug("sendMsg 개행제거 후 len == " + sendMsg.getBytes().length);
    LOGGER.debug("sendMsg str ==" + sendMsg+"//END");
    // 전송
    //PrintWriter out = new PrintWriter(writer, true);//autoFlush true는 개행시 flush됨
    PrintWriter out = new PrintWriter(writer);
    
    //out.println(sendMsg);
    out.write(sendMsg);
    //out.print(sendMsg);
    out.flush();
  
    // 응답
    byte[] arr = new byte[headerLen];
    in.read(arr);
    int len = ConvertUtil.strToInt(new String(arr));
    
    LOGGER.debug("recieve len == " + len);
    byte[] recieve = new byte[len];
    in.read(recieve);
    str = new String(recieve, CHARSET);
    LOGGER.debug("recieve str ==" + str+"//END");
    
    return str;
  }
  
  
  /**
   * send massage
   * //전문길이부 제거
   * @param msg - 보낼 메시지
   * @param length - 메시지길이
   * @param headerLen - 헤더길이
   * @return void
   * @throws Exception 
   */
  public String sendToRetVrac(final int headerLen, final int length, final String msg) throws IOException {
    LOGGER.debug("sendToRet == ");
    String str = "";
    //String sendMsg = StringUtil.leftPad(length, headerLen, "0") + msg;
    String sendMsg=msg;//길이 제거요청 2021.06.29 박여경대리님
    LOGGER.debug("sendMsg 개행제거 전 len == " + sendMsg.getBytes().length);
    //sendMsg=sendMsg.replaceAll("\n", "");//개행제거
    //LOGGER.debug("sendMsg 개행제거 후 len == " + sendMsg.getBytes().length);
    LOGGER.debug("sendMsg str ==" + sendMsg+"//END");
    // 전송
    //PrintWriter out = new PrintWriter(writer, true);//autoFlush true는 개행시 flush됨
    PrintWriter out = new PrintWriter(writer);
    
    //out.println(sendMsg);
    out.write(sendMsg);
    //out.print(sendMsg);
    out.flush();
  
    // 응답
    byte[] arr = new byte[headerLen];
    in.read(arr);
    int len = ConvertUtil.strToInt(new String(arr));
    
   
    byte[] recieve = new byte[length];
    in.read(recieve);
    str = new String(recieve, CHARSET);
    LOGGER.debug("recieve len == " + str.length());
    LOGGER.debug("recieve str ==" + str+"//END");
    
    return str;
  }
  
  /**
   * sendToRet(int headerLen, int length, String msg)를 사용바랍니다.
   * @param msg - 보낼 메시지
   * @param length - 메시지길이
   * @param headerLen - 헤더길이
   * @return void
   * @throws Exception 
   */
//  @Deprecated
//  public String sendToRet(String msg) throws IOException {
//    String str = "";
//    // 전송
//    PrintWriter out = new PrintWriter(writer, true);
//    out.println(msg);
//    // 응답
//    byte[] arr = new byte[4];
//    in.read(arr);
//    int len = ConvertUtil.strToInt(new String(arr));
//    
//    byte[] recieve = new byte[len];
//    in.read(recieve);
//    str = new String(recieve, charSet);
//    
//    return str;
//  }
  
  /**
   * sendToRet(int headerLen, int length, String msg)를 사용바랍니다.
   * @param msg - 보낼 메시지
   * @param length - 메시지길이
   * @param headerLen - 헤더길이
   * @return void
   * @throws Exception 
   */
//  @Deprecated
//  public String sendToRet(String msg, int length, int headerLen) throws IOException {
//    String str = "";
//    try {
//      byte[] sendLen = new byte[headerLen];
//      sendLen = ConvertUtil.intToBtyes(length, headerLen);
//      byte[] sendMsg = new byte[length + headerLen];
//      System.arraycopy(sendLen, 0, sendMsg, 0, headerLen);
//      System.arraycopy(msg.getBytes(charSet), 0, sendMsg, headerLen, length);
//      
//      out.write(sendMsg,0,sendMsg.length);
//      
//      byte[] arr = new byte[headerLen];
//      in.read(arr);
//      int len = ConvertUtil.btyesToInt(arr,headerLen);
//      
//      byte[] recieve = new byte[len];
//      in.read(recieve);
//      str = new String(recieve, charSet);
//    } catch (Exception e) {
//      // TODO: handle exception
//      closeSocket();
//      e.printStackTrace();
//    }
//    return str;
//  }

  /**
   * socket close
   * 
   * @return void
   */
  public void closeSocket() {
    try {
      if (socket != null) {
        socket.close();
      }
      if (writer != null) {
        writer.close();
      }
      if (reader != null) {
        reader.close();
      }
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
