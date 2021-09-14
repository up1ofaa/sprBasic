
package testPj;


public class test1 {

  /**
   * JAVA로 STRING 배열속 문자열 찾아내기
   * STRING 배열 A
   * STRING 배열 B
   * 배열 B의 길이는 배열 A의 길이보돠 작다
   * 배열 B의 모든 문자열은 배열 A에 모두 포함되어 있다
   * 문제: 배열 A 중 배열 B에 없는 문자열을 찾아서 출력하는 코드
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

      String[] A= new String[] {"A1","a1","d1","D1","b1","B1"};
      String[] B= new String[] {"A1","a1","b1","B1"};
      
      for(int i=0; i<A.length; i++) {
        int chk=0;
        for(int j=0; j<B.length;j++) {        
          if(A[i].indexOf(B[j])>-1) {
            //System.out.println(A[i]+"문자 포함되어있음");
            chk++;
          }//end-if
        }//end-for(2)
        if(chk==0) {
        System.out.println(A[i]+"문자 포함되어있지 않음");
        }
      }//end-for(1)
     
  }//end-main
}//end-class
