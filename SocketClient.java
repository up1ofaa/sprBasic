package testPj;

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

public class SocketClient {

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

  }

  
  /**
   * send massage
   * @param msg - 보낼 메시지
   * @param length - 메시지길이
   * @param headerLen - 헤더길이
   * @return void
   * @throws Exception 
   */
  public String sendToRet(final int headerLen, final int length, final String msg) throws IOException {

    String str = "";
    String sendMsg = StringUtil.leftPad(length, headerLen, "0") + msg;
    // 전송
    PrintWriter out = new PrintWriter(writer, true);
    
    out.println(sendMsg);
    // 응답
    byte[] arr = new byte[headerLen];
    in.read(arr);
    int len = StringUtil.strToInt(new String(arr));
    

    byte[] recieve = new byte[len];
    in.read(recieve);
    str = new String(recieve, CHARSET);

    
    return str;
  }
  
  
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
