import com.freshworks.dataStore.DataStore;
import com.freshworks.dataStore.exceptions.*;
import com.freshworks.dataStore.interfaces.DataStoreInterface;
public class test3 {
   public static void main(String ss[])
   {
      try {
         DataStoreInterface dsi;
         //get instanece of store at default location - C:\dataStoreData\store.data
         dsi=DataStore.getInstance();
         dsi.create("LMN","{\"name\":\"lmn\"}",40);
         System.out.println("LMN created");
         dsi.create("RQR","{\"name\":\"rqr\"}",100);
         System.out.println("RQR created");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
