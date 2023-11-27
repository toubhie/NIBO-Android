package com.nigeria.locateme.locateme.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nigeria.locateme.locateme.entities.Contact;
import com.nigeria.locateme.locateme.entities.UserLocation;

import java.util.ArrayList;
import java.util.List;

public class SQLController{

	private SQLiteDatabase database;
	//private final Context cnt;
	DBHelper helper;
	public static Cursor c; // so that i can use it another class to call for size of database.
	
	public SQLController(Context co){
		//this.cnt = co;
		helper = new DBHelper(co);
		database = helper.getWritableDatabase();
	} 
	/*public void open{
		helper = new DBHelper(cnt);
	}*/
	public void insertData(String name, String lat, String longi){
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.USER_NAME, name);
		cv.put(DBHelper.USER_LAT, lat);
		cv.put(DBHelper.USER_LONGI, longi);
		database.insert(DBHelper.TABLE_USER, null, cv);
	}
	
	public Cursor readData(){
		String[] columns = new String[]{DBHelper.USER_ID,DBHelper.USER_NAME,DBHelper.USER_LAT,
		DBHelper.USER_LONGI};
		c = database.query(DBHelper.TABLE_USER, columns, null, null, null, null, null);
		if( c != null){
			c.moveToFirst();
		}
		return c;
	}

	// Adding new contact
	public long addSavedLocationToSQLiteDB(UserLocation userLocation) {

		long insertedId = 0;
		try {

			ContentValues values = new ContentValues();
			values.put(DBHelper.LONGITUDE, userLocation.getLongitude());
			values.put(DBHelper.LATITUDE, userLocation.getLatitude());
			values.put(DBHelper.DESCRIPTION, userLocation.getDescription());
			values.put(DBHelper.IS_SYNCED_TO_ONLINE_DB, userLocation.getIsSyncedToOnlineDBStatus());
			values.put(DBHelper.IS_DELETED, userLocation.getIsDeleted());
			values.put(DBHelper.MYSQL_ID, userLocation.getMysqlId());

			// Inserting Row
			insertedId = database.insert(DBHelper.TABLE_LOCATION, null, values);
			database.close(); // Closing database connection

			return insertedId;
		} catch (Exception e){
			e.printStackTrace();
		}

		return insertedId;
	}

	// Getting All Contacts in DB
	public List<UserLocation> getAllSavedLocationsFromSQLiteDB() {
		List<UserLocation> userLocationList = new ArrayList<UserLocation>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + DBHelper.TABLE_LOCATION;

		Cursor cursor = database.rawQuery(selectQuery, null);

		// looping through all rows and adding to list

		Log.i(Constants.TAG_NIBO, "Cursor: " + cursor);

		if (cursor.moveToFirst()) {
			do {
				UserLocation userLocation = new UserLocation();

				userLocation.setSqliteId(Integer.parseInt(cursor.getString(0)));
				userLocation.setMysqlId(Integer.parseInt(cursor.getString(1)));
				userLocation.setLongitude(cursor.getString(2));
				userLocation.setLatitdue(cursor.getString(3));
				userLocation.setDescription(cursor.getString(4));
				userLocation.setIsSyncedToOnlineDBStatus(cursor.getString(5));
				userLocation.setIsDeleted(Integer.parseInt(cursor.getString(6)));

				// Adding saved locations to list
				userLocationList.add(userLocation);
			} while (cursor.moveToNext());

		}
		// return saved locations list
		return userLocationList;
	}

	// Updating single contact
	public int updateContactFromSQLiteDBById(UserLocation userLocation) {

		ContentValues values = new ContentValues();

		values.put(DBHelper.LONGITUDE, userLocation.getLongitude());
		values.put(DBHelper.LATITUDE, userLocation.getLatitude());
		values.put(DBHelper.DESCRIPTION, userLocation.getDescription());
		values.put(DBHelper.MYSQL_ID, userLocation.getMysqlId());
		values.put(DBHelper.IS_SYNCED_TO_ONLINE_DB, userLocation.getIsSyncedToOnlineDBStatus());
		values.put(DBHelper.IS_DELETED, userLocation.getIsDeleted());

		// updating row
		return database.update(DBHelper.TABLE_LOCATION, values, DBHelper.LOCATION_ID + " = ?",
				new String[] { String.valueOf(userLocation.getSqliteId()) });
	}

	// Get saved locations count for user
	public int getSavedLocationsCountForUserOld() {
		int count = 0;

		String[] columns = new String[]{DBHelper.USER_ID,DBHelper.USER_NAME,DBHelper.USER_LAT,
				DBHelper.USER_LONGI};
		c = database.query(DBHelper.TABLE_USER, columns, null, null, null, null, null);
		if( c != null){
			c.moveToFirst();

			count = c.getCount();
		}

		// return count
		return count;
	}

	public int getSavedLocationsCountForUser() {
		int count = 0;
		String countQuery = "SELECT * FROM " + DBHelper.TABLE_LOCATION;

		Cursor cursor = database.rawQuery(countQuery, null);

		Log.i(Constants.TAG_NIBO, "Cursor: " + cursor);

		if(cursor != null && !cursor.isClosed()){
			count = cursor.getCount();
			cursor.close();
		}

		// return count
		return count;
	}
	
	/*public int update(long userid, String username, String lat, String longi){
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.USER_NAME, username);
		cv.put(DBHelper.USER_LAT, lat);
		cv.put(DBHelper.USER_LONGI, longi);
		int i = database.update(DBHelper.TABLE_USER, cv, DBHelper.USER_ID + " = "+ userid, null);
		return i;
	}*/

	public int update(long userid, String username){
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.USER_NAME, username);
		int i = database.update(DBHelper.TABLE_USER, cv, DBHelper.USER_ID + " = "+ userid, null);
		return i;
	}
	
	public void delete(long userid){
		database.delete(DBHelper.TABLE_USER, DBHelper.USER_ID+ " = "+ userid, null);
	}

	public void deleteAllUserRecordsFromTable(){
		try{
			database.delete(DBHelper.TABLE_USER, null, null);

		} catch (Exception e){
			e.printStackTrace();
		}
	}
		
}

