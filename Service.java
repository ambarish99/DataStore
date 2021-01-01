import java.io.*;
import java.util.*;
import java.nio.channels.*;
import java.util.concurrent.*;

public final class Service {
   public void deletExpiredRecords(File file)
   {
      long time=System.currentTimeMillis()/1000;
      try {
         boolean isDeleted=false;
         RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
         FileChannel channel = randomAccessFile.getChannel();
         FileLock lock = null;
         
            while(true)
            {
             try
             {
                 lock = channel.tryLock();
                 break;
             }catch(Exception e)
             {
                 //nothing
             }
            }

         long toDeleteLength=0;
         StringBuffer buffer=new StringBuffer();
         while(randomAccessFile.getFilePointer()<file.length()) buffer.append(randomAccessFile.readLine()+"\n");
         String data=buffer.toString();
         buffer.delete(0,buffer.length());
         randomAccessFile.seek(0);

         Scanner scanner = new Scanner(data);
         scanner.useDelimiter("[|~|]");
         String k=null,pair=null;
         while (scanner.hasNext()) {
             pair=scanner.next();
             if(pair.indexOf(":=:")==-1) continue;
             k=pair.substring(pair.indexOf("$")+1);
             long life=Long.parseLong(k);
             if(life<=time && life!=-1){
                 toDeleteLength+=(pair+"|~|").getBytes("UTF-8").length;
                 isDeleted=true;
                 continue;
             }
             randomAccessFile.writeBytes(pair+"|~|");
         }
         scanner.close();
         if(isDeleted) randomAccessFile.setLength(randomAccessFile.length()-toDeleteLength);
         
         if (lock != null)
         {
             lock.release();
         }
         channel.close();
         randomAccessFile.close();

     } catch (Exception e) {
         e.printStackTrace();
     }
   }
   public void iterateEachFile()
   {
        try {
            File file=new File("C:\\dataStoreData\\stores.path");
            RandomAccessFile raf=new RandomAccessFile(file,"rw");
            String line="";
            while(raf.getFilePointer()<file.length())
            {
                line=raf.readLine();
                File dataFile=new File(line);
                deletExpiredRecords(dataFile);
            }
            raf.close();
        } catch (Exception e) {
            //TODO: handle exception
        }

   }

   public static void main(String ss[])
   {
        Service service=new Service();
        Runnable runable=new Runnable(){
            public void run()
            {
                service.iterateEachFile();
            }
        };
        ScheduledExecutorService service2=Executors.newSingleThreadScheduledExecutor();
        service2.scheduleAtFixedRate(runable, 0, 5, TimeUnit.MINUTES);


   }
}
