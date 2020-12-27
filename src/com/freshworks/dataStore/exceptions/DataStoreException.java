package com.freshworks.dataStore.exceptions;
public class DataStoreException extends Exception implements java.io.Serializable
{
public DataStoreException(String message)
{
super(message);
}
}