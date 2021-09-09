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
package testPj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * <b>Description:</b><p>
 * 구현 클래스의 설명을 입력합니다.
 * @since 2021-06-10
 * @version 1.0
 * @author 홍선기(1073302)
 * @see
 * <pre>
 * 개정이력(Modification Information)
 * 수정일      수정자              수정내용
 * ----------  ---------           -------------------------------
 * 2021-06-10  0000000     최초생성
 * </pre>
 */
public class SocketClientMain {

  /**
   * 메소드 설명
   * @param param1 - 파라메터 설명
   * @param param2 - 파라메터 설명
   * @return 반환값
   * @exception Exception
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
      System.out.print("main 테스트");
      
      try {
        Socket socket = new Socket("10.10.153.190",41); //소켓 서버에 접속
        System.out.println("socket 서버에 접속성공!");
        
        //OutputStream -클라이언트에서 Server로 메시지 발송
        OutputStream out= socket.getOutputStream(); 
        //
        PrintWriter writer =new PrintWriter(out,true);
        
        writer.println("Client to server");
        
        InputStream input =socket.getInputStream();
        
        BufferedReader reader =new BufferedReader(new InputStreamReader(input));  
        
        System.out.println(reader.readLine());
        
        System.out.println("CLIENT SOCKET CLOSE");
        socket.close();
        
      }catch(IOException e)   {
        e.printStackTrace();
      }finally {
        //socket.close();
      } 
  }//END-MAIN

}//END-CLASS
