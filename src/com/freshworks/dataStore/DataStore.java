package com.freshworks.dataStore;

import com.freshworks.dataStore.interfaces.*;
import com.freshworks.dataStore.exceptions.*;
import java.util.*;
import java.io.*;
import java.nio.channels.*;
import com.google.gson.*;

public class DataStore implements DataStoreInterface {
    private static List<DataStoreInterface> dataStoreList = null;
    private final  String filePath;
    private static final String serviceFile="C:\\dataStoreData\\stores.path";

    public  void createFile() throws DataStoreException
    {
        try{
            File file = new File(this.filePath);
            int index=filePath.lastIndexOf("\\");
            File folder=new File(this.filePath.substring(0,index));
            boolean created= folder.mkdirs();
            if (!file.exists())
                file.createNewFile();
            
            file = new File(DataStore.serviceFile);
            index = DataStore.serviceFile.lastIndexOf("\\");
            folder = new File(DataStore.serviceFile.substring(0, index));
            created = folder.mkdirs();
            if (!file.exists())
                file.createNewFile();

            RandomAccessFile raf=new RandomAccessFile(file, "rw");
            while(raf.getFilePointer()<file.length())
            {
                String line=raf.readLine();
                if(line.equals(filePath))
                {
                    raf.close();
                    return;
                }
            }
            raf.seek(file.length());
            raf.writeBytes(filePath+"\n");
            raf.close();
        }
        catch(Exception e)
        {
            throw new DataStoreException(e.getMessage());
        }
    }

    private DataStore(String path) {
        filePath = path.trim();
        
    }

    private DataStore() {
        filePath = "C:\\dataStoreData\\store.data";
    }

    public String getFilePath() {
        return this.filePath;
    }

    public static DataStoreInterface getInstance(String path) throws DataStoreException {
        try {
            DataStoreInterface dataStoreInterface;
            if (dataStoreList == null) {
                dataStoreList = new LinkedList<DataStoreInterface>();
                dataStoreInterface = new DataStore(path);
                dataStoreInterface.createFile();
                dataStoreList.add(dataStoreInterface);
                return dataStoreInterface;
            }
            for (DataStoreInterface ds : dataStoreList) {
                if (ds.getFilePath().equals(path)) {
                    return ds;
                }
            }
            dataStoreInterface = new DataStore(path);
            dataStoreInterface.createFile();
            dataStoreList.add(dataStoreInterface);
            return dataStoreInterface;
        } catch (Exception e) {
            throw new DataStoreException(e.getMessage());
        }
    }

    public static DataStoreInterface getInstance() throws DataStoreException {
        try {
            DataStoreInterface dataStoreInterface;
            if (dataStoreList == null) {
                dataStoreList = new LinkedList<DataStoreInterface>();
                dataStoreInterface = new DataStore();
                dataStoreInterface.createFile();
                dataStoreList.add(dataStoreInterface);
                return dataStoreInterface;
            }
            for (DataStoreInterface ds : dataStoreList) {
                if (ds.getFilePath().equals("C:\\dataStoreData\\store.data")) {
                    return ds;
                }
            }
            dataStoreInterface = new DataStore();
            dataStoreInterface.createFile();
            dataStoreList.add(dataStoreInterface);
            return dataStoreInterface;
        } catch (Exception e) {
            throw new DataStoreException(e.getMessage());
        }
    }



    public  boolean delete(String key) throws DataStoreException {
        try {
            boolean isDeleted=false;
            File file = new File(filePath);
            if (!file.exists())
                throw new DataStoreException("Data store file - '" + filePath + "' does not exist");
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
                
            

            String toDelete="";
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
                k=pair.substring(0,pair.indexOf(":=:"));
                if(k.equals(key)){
                    toDelete=pair+"|~|";
                    isDeleted=true;
                    continue;
                }
                randomAccessFile.writeBytes(pair+"|~|");
            }
            scanner.close();
            if(isDeleted) randomAccessFile.setLength(randomAccessFile.length()-toDelete.getBytes("UTF-8").length);
            
            if (lock != null)
            {
                lock.release();
            }
            channel.close();
            randomAccessFile.close();
            return isDeleted;

        } catch (Exception e) {
            
            throw new DataStoreException(e.getMessage());
        }
    }


    public  void create(String key, String value) throws DataStoreException {
        try {
            key=key.trim();
            value=value.trim();
            //check length of key
            if(key.length()>32) throw new DataStoreException("Key passed in arguments must not be more than 32 characters");
            //check size of json for 16 KB
            if(value.getBytes("UTF-8").length>16384) throw new DataStoreException("Value against any key must not exceed 16 Kilobytes");
            try {
                JsonParser parser = new JsonParser();
                parser.parse(value);
            } catch (JsonSyntaxException ex) {
                throw new DataStoreException("Json Value passed in argument is not a valid JSON object");
            }

            File file = new File(filePath);

            //check file size must note be more than 1 GB
            double len=file.length();
            if(value.getBytes("UTF-8").length+len+key.getBytes("UTF-8").length>=1073741824)
            {
                throw new DataStoreException("Can not store data as file size will exceed 1 GB");
            }
            

            //find if key already exists or not
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("[|~|]");
            String k=null;
            while (scanner.hasNext()) {
                k=scanner.next();
                if(k.indexOf(":=:")==-1) continue;
                k=k.substring(0,k.indexOf(":=:"));
                if(k.equals(key)){
                    scanner.close();
                   /* randomAccessFile.close();
                    if (lock != null)
                        lock.release(); */
                    throw new DataStoreException("The key: '"+key+"' passed in arguments already exists in the data store file");
                }
            }
            scanner.close();            

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            //apply lock
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

            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.writeBytes(key);
            randomAccessFile.writeBytes(":=:");
            randomAccessFile.writeBytes(value);
            randomAccessFile.writeBytes("$-1");
            randomAccessFile.writeBytes("|~|");

            if (lock != null)
            {
                lock.release();
            }
            channel.close();
            randomAccessFile.close();

        } catch (Exception e) {
            throw new DataStoreException(e.getMessage());
        }
    } // create method



    public  void create(String key, String value,long life) throws DataStoreException {
        try {
            key=key.trim();
            value=value.trim();
            //check life validaion
            if(life<=0) throw new DataStoreException("invalid time to live paremeter: "+life+", must be greater than 0 ");
            //check length of key
            if(key.length()>32) throw new DataStoreException("Key passed in arguments must not be more than 32 characters");
            //check size of json for 16 KB
            if(value.getBytes("UTF-8").length>16384) throw new DataStoreException("Value against any key must not exceed 16 Kilobytes");
            try {
                JsonParser parser = new JsonParser();
                parser.parse(value);
            } catch (JsonSyntaxException ex) {
                throw new DataStoreException("Json Value passed in argument is not a valid JSON object");
            }

            File file = new File(filePath);

            //check file size must note be more than 1 GB
            double len=file.length();
            if(value.getBytes("UTF-8").length+len+key.getBytes("UTF-8").length>=1073741824)
            {
                throw new DataStoreException("Can not store data as file size will exceed 1 GB");
            }
            

            //find if key already exists or not
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("[|~|]");
            String k=null;
            while (scanner.hasNext()) {
                k=scanner.next();
                if(k.indexOf(":=:")==-1) continue;
                k=k.substring(0,k.indexOf(":=:"));
                if(k.equals(key)){
                    scanner.close();
                   /* randomAccessFile.close();
                    if (lock != null)
                        lock.release(); */
                    throw new DataStoreException("The key: '"+key+"' passed in arguments already exists in the data store file");
                }
            }
            scanner.close();

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            //apply lock
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

               long time=(System.currentTimeMillis()/1000)+life;
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.writeBytes(key);
            randomAccessFile.writeBytes(":=:");
            randomAccessFile.writeBytes(value);
            randomAccessFile.writeBytes("$"+time);
            randomAccessFile.writeBytes("|~|");

            if (lock != null)
            {
                lock.release();
            }
            channel.close();
            randomAccessFile.close();

        } catch (Exception e) {
            throw new DataStoreException(e.getMessage());
        }
    } // create method overloaded


    public  String get(String key) throws DataStoreException {
        try {
            File file = new File(filePath);
            if (!file.exists())
                throw new DataStoreException("Data store file - '" + filePath + "' does not exist");
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            FileChannel channel = randomAccessFile.getChannel();
            FileLock lock = null;
            
                while(true)
               {
                try
                {
                    lock = channel.tryLock(0, Long.MAX_VALUE, true);
                    break;
                }catch(Exception e)
                {
                    //nothing
                }
               }
            
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("[|~|]");
            String pair=null,value=null,k=null;
            while (scanner.hasNext()) {
                pair=scanner.next();                
                if(pair.indexOf(":=:")==-1) continue;
                k=pair.substring(0,pair.indexOf(":=:"));
                if(k.equals(key)){
                    value=pair.substring(pair.indexOf(":=:")+3,pair.indexOf("$"));
                    scanner.close();
                    if (lock != null)
                        lock.release();
                    channel.close();
                    randomAccessFile.close();
                    return value;
                }
            }
            scanner.close();
        
            if (lock != null)
            {
                lock.release();
            }
            
            channel.close();
            randomAccessFile.close();
            return null;
        } catch (Exception e) {
            throw new DataStoreException(e.getMessage());
        }
    } // get method

}
