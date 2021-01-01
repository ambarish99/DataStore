import java.io.File;
import java.io.RandomAccessFile;
import java.util.RandomAccess;

import com.freshworks.dataStore.DataStore;
import com.freshworks.dataStore.exceptions.*;
import com.freshworks.dataStore.interfaces.DataStoreInterface;
public class test1 {
   public static void main(String arg[])
   {
      try{
         DataStoreInterface dsi,dsi2,dsi3;
      //get instanece of store at default location - C:\dataStoreData\store.data
      dsi=DataStore.getInstance();
   
      //code to check validation for json greater than 16 kb
      //read file ans.json and stored in buffer then passed to sting object and tried to crate key value in store
      File file=new File("C:\\del\\ans.json");
      RandomAccessFile raf=new RandomAccessFile(file, "rw");
      StringBuffer buffer=new StringBuffer();
      while(raf.getFilePointer()<file.length())
      {
         buffer.append(raf.readLine());
      }
      raf.close();
      

      String str=buffer.toString();
      //create key value in database
      //dsi.create("ABC", str);  //here str is greater than 16 KB
      
      //create and key and strore key value pair to store
      dsi.create("ABC","{\"name\":\"abc\"}" );
      dsi.create("XYZ","{\"name\":\"xyz\"}" );
      //get method to get  json against key ABC
      String abc=dsi.get("ABC");
      System.out.println(abc);

      String xyz=dsi.get("XYZ");
      System.out.println(xyz);
      
      //delete key and value against key ABC, it will return true if deletyed successfully elese return false if key not found
      boolean isDel=dsi.delete("ABC");
      System.out.println(isDel);
      isDel=dsi.delete("XYZ");
      System.out.println(isDel);

      dsi2=DataStore.getInstance();
      System.out.println(dsi2.get("ABC"));

      dsi3=DataStore.getInstance("C:\\ddStore\\pqr.aa");
      dsi3.create("PQR", "{\"name\":\"pqr\"}");
         

      }catch(Exception e)
      {
         e.printStackTrace();
         System.out.println(e.getMessage());
      }
   }
   
}
