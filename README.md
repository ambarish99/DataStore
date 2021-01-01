# DataStore
This is a file based key-vaue dataStore created in java, for storing data in a file. It supports basic CRD(create,read,delete) operations

## Steps of installation
* Create a folder named "DataStore" in C:\ drive of your windows system and clone this repository in that folder.(All the files and folders must be under DataStore folder).
* Now to establish automatic deletetion of records which are expired or their time-to-live peroid is over. place **DataStoreRun.bat** file in startup programs of windows.
* To find that startup folder press **windows+R** key and then in text box type **shell:startup** then click **OK**. The required folder will get open, in that folder put **DataStoreRun.bat** file.
* Now the installation setup is done.

## How to use this library
### To instantiate the library
```sh
DataStoreInterface dataStore=DataStore.getInstance();
```
or
```sh
DataStoreInterface dataStore=DataStore.getInstance("C:\\path to desired file\\file.abc"); 
```
* If path is not provided then it will get instantiated in the default location C:\dataStoreData\store.data otherwise in the file name provided.
* Client can instantiate in multiple files but for any file only it's one instnace will be returned.
### To store data in the file
```sh
public void create(String key,String jasonString,long time-to-live);
ex: dataStore.create("ABC","{\"abc\":\"pqr\"}",67577);
```
* Third parameter time-to-live(in seconds) is optional, if provided then the key will be valid for only upto that durtion of time.
* If key already exists or json is invalid then it will throw DatStoreException.
* If fle size exceeds 1GB then it will throw DatStoreException.
* If key passed is more than 32 characters then it will throw DatStoreException.
* If the JSON passed is more than 16 KB then it will throw DataStoreException
### To get json data 
```sh
public String get(String key);
ex: dataStore.get("ABC");
```
* To get data, pass the key for that data. If key is found then it will return JSON string for that, otherwise it will return null. this method throws DataStoreException.
### To delete data
```sh
public boolean delete(String key);
ex: dataStore.delete("ABC");
```
* This method will delete the key and data against that key if key is found. If found deleted then it will return true otherwise false. this method throws DataStoreException.

### To compile and run the program
**To compile:**  Add jar files in dist folder to your classpath
```sh
javac -classpath c:\DataStore\dist\*;. yourFileName.java
```
**To run:** Add jar files in dist folder to your classpath
```sh
java -classpath c:\DataStore\dist\*;. classFileName
```
## Description
* The code written in library is thread safe.
* Not more than one process can access the same file at any instant of time.
* This library is tested on windows-10 operating system.
* JDK must be installed in your system to use this library.
## A full code snipet for understanding
```sh
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
      
      //create and key and strore key value pair to store
      dsi.create("ABC","{\"name\":\"abc\"}" );
      dsi.create("XYZ","{\"name\":\"xyz\"}" );
      //get method to get  json against key ABC
      String abc=dsi.get("ABC");
      System.out.println(abc);

      String xyz=dsi.get("XYZ");
      System.out.println(xyz);
      
      //delete key and value against key ABC, it will return true if deleted successfully elese return false if key not found
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

```

## Thanking you

