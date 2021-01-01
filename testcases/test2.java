import com.freshworks.dataStore.exceptions.*;
import com.freshworks.dataStore.interfaces.*;
import com.freshworks.dataStore.*;
class abc extends Thread
{
   abc()
   {
      start();
   }
   public void run()
   {
      try {
         DataStoreInterface dsf=DataStore.getInstance();
         dsf.create("PQR", "{\"class\":\"10th\"}");
         String g=dsf.get("ABC");
         System.out.println("g=="+g);
      } catch (Exception e) {
         e.printStackTrace();
         System.out.println(e.getMessage());
      }
   }
}
class pqr extends Thread
{
   pqr()
   {
      start();
   }
   public void run()
   {
      try{
         DataStoreInterface dsf=DataStore.getInstance();
         dsf.create("ABC", "{\"class\":\"12th\"}");
         dsf.delete("PQR");
      }catch(Exception e)
      {
         e.printStackTrace();
         System.out.println(e.getMessage());
      }
   }
}
public class test2 {
   public static void main(String ss[]) {
     pqr x=new pqr();
     abc y=new abc(); 
   }
}
