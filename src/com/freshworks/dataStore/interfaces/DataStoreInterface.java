package com.freshworks.dataStore.interfaces;
import com.freshworks.dataStore.exceptions.*;
public interface DataStoreInterface
{
public String getFilePath();
public void create(String key,String value) throws DataStoreException;
public void create(String key,String value,long life) throws DataStoreException;
public String get(String key) throws DataStoreException;
public boolean delete(String key) throws DataStoreException;
public  void createFile() throws DataStoreException;
}