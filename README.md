# DataStore
This is a file based key-vaue dataStore, for storing data in a file.

## Steps of installation
* Create a folder named "DataStore" in C:\ drive of your windows system and clone this repository in that folder.(All the files and folders must be under DataStore folder).
* Now to establish automatic deletetion of records which are expired or their time-to-live peroid is over. place **DataStoreRun.bat** file in startup programs of windows.
* To find tbat stratup folder press **windows+R** key and then in text box type **shell:startup** then click **OK**. The required folder will get open, in that folder put **DataStoreRun.bat** file.
* Now the installation setup is done.

## How to use ths library
### To instantiate the library
```sh
DataStoreInterface dataStore=DataStore.getInstance();
```
or
```sh
DataStoreInterface dataStore=DataStore.getInstance("C:\path to desired file\file.abc"); 
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


