package com.nigeria.locateme.locateme.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	
	//create the whole information about the table
	public static final String TABLE_USER = "nibouserinfo";
	public static final String USER_ID = "_id";
	public static final String USER_NAME = "name";
	public static final String USER_LAT = "lat";
	public static final String USER_LONGI = "longi";

	public static final String TABLE_LOCATION = "savedLocations";
	public static final String LOCATION_ID = "locationId";
	public static final String MYSQL_ID = "mysqlId";
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String DESCRIPTION = "description";
	public static final String IS_DELETED = "isDeleted";
	public static final String IS_SYNCED_TO_ONLINE_DB = "isSyncedToOnlineDB";
	
	//create database information 
	private static final String DB_NAME = "NIBO_USER.DB";
	private static final int DB_VERSION = 4;
	
	//create a table query
	private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER
	+"(" 
	+USER_ID +	" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
	+USER_NAME + " TEXT NOT NULL, "
	+USER_LAT + " TEXT NOT NULL, "
	+USER_LONGI + " TEXT NOT NULL);";

	//create a locations query
	private static final String CREATE_TABLE_SAVED_LOCATIONS = "CREATE TABLE " + TABLE_LOCATION + "("
			+ LOCATION_ID +	" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
			+ MYSQL_ID + " INTEGER, "
			+ LONGITUDE + " TEXT, "
			+ LATITUDE + " TEXT, "
			+ DESCRIPTION + " TEXT, "
			+ IS_SYNCED_TO_ONLINE_DB + " TEXT, "
			+ IS_DELETED + " INTEGER "
			+ ");";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_USER);
		Log.i(Constants.TAG_NIBO, CREATE_TABLE_SAVED_LOCATIONS);
		db.execSQL(CREATE_TABLE_SAVED_LOCATIONS);
		Log.e("check", "created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
		onCreate(db);	
	}

}
