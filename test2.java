
package testPj;

import java.io.BufferedReader;
import java.io.FileReader;

public class test2 {

  /**
   * 메소드 설명
   * DB ORACLE
   * SAM 파일은 100만 RECORD존재
   * JAVA, ORACLE, LINUX SHELL, PL-SQL
   * 기존에 존재하지 않는 대상은 INSERT
   * 기존에 존재하는 대상은 UPDATE
   * DB처리 50건 이상 오류 발생시 재실행
   * 재실행 횟수는 5회까지만 허용
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    //변수선언
    String filePath ="/app/data/nbmImg/";
    
 
    int failCnt=0;
    INT workCnt=0;
    
    while(workCnt<5 && failCnt>=50){
       try {
         failCnt=executeFailCnt(filePath);
        }finally {
         workCnt++;
         System.out.println();
        }//end-try
    }
    
  }//end-main

  private static int executeFailCnt(String filePath){
   int succCnt=0;
   int failCnt=0;
   int recCnt=0;
    BufferedReader br =null;
    StringBuffer sb =new StringBuffer();
    int recCnt=0;
    try{
      br =new BufferedReader(new FileReader(filePath));
          
          String line =null;
          while((line=br.readLine())!=null) {
            System.out.println(line);
            sb.append(line);
          } 
       succCnt++;
    }catch(Exception e){
     failCnt++; 
    }finally{
      recCnt++; 
     return failCnt;
    } 
  }  
  
  
}//end-class