package testMain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
		
		Connection  con= null;
		CallableStatement cstmt= null;
		
		String filePath="C:\\Users\\hy\\Desktop\\";
		String fileNm  ="testCust.sam";
		
		
		int workCnt=0;
		int recCnt=0;	
		int failCnt=0;
		int succCnt=0;	
		BufferedReader 	br	= null;
		String 			line= null;
		StringBuffer 	sb 	= new StringBuffer();
		while((workCnt<5 && failCnt>=50)|| workCnt==0) {//while(1)	
		try {//try(1)	
			 System.out.println("시작"+(workCnt+1));
		     recCnt=0;	 
			 failCnt=0;  
			 succCnt=0;	 
			 br= new BufferedReader(new FileReader(filePath+fileNm));	
			 while((line=br.readLine())!=null) {//while(2)	
				sb.append(line);
				try{//try(2)
					con=getConnection();
					String squery="";
					squery +=" { call omvc01.omvc01_u01( ? ,?              "; //output
					squery +="                          ,?, ? ,? ,? ,?  )} "; //input
					cstmt = con.prepareCall(squery);
					cstmt.setString(1,line);
					int i= cstmt.executeUpdate();	
					if(i > 0) {						
					}else {
						new Exception("insert or update 실패");
					}
				}catch(Exception e) {
					//System.out.println((recCnt+1)+"번째 에러발생 custId:"+line);
					//failCnt++;
				}finally {
					try {//try(3)
						con.setAutoCommit(true);
						cstmt.close();
						con.close();
						succCnt++;
					}catch(Exception e) {	
						failCnt++;
					}//end-try(3)
					recCnt++;
				}//end-try(2)		
			}//end-while(2)			
		}catch(Exception e){
			System.out.println((workCnt+1)+"번째 작업 실패========================");
			System.out.println("에러내용:"+e.getMessage());
			System.out.println("====================================");
		}finally {
			System.out.println((workCnt+1)+"번째 작업 결과========================");
			System.out.println("전체 Record : "+(recCnt)+"건");
			System.out.println("성공 Record : "+(succCnt)+"건");
			System.out.println("실패 Record : "+(failCnt)+"건");
			System.out.println("====================================");
			workCnt++;	
		}//end-try(1)
		}//end-while(1)

	}//end-main	
			
	public static Connection getConnection()
    {
        Connection conn = null;
        try {
            String user = "system"; 
            String pw 	= "ora4321";
            String url 	= "jdbc:oracle:thin:@localhost:1521:oradb";
            
            Class.forName("oracle.jdbc.driver.OracleDriver");        
            conn = DriverManager.getConnection(url, user, pw);
            
            //System.out.println("Database connection complete");
            
        } catch (ClassNotFoundException cnfe) {
            System.out.println("DB classNotFound Excetpion"+cnfe.toString());
        } catch (SQLException sqle) {
            System.out.println("DB Sql Excetpion"+sqle.toString());
        } catch (Exception e) {
            System.out.println("Unkonwn error");
            e.printStackTrace();
        }
        return conn;     
    }//end-DB getConnection

}//end-class
