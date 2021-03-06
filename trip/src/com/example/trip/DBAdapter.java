package com.example.trip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager; 
import android.util.Log;

public class DBAdapter{
	static final String KEY_ROWID = "_id"; 
	static final String KEY_LAT = "lat";
	static final String KEY_LNG = "lng";
	static final String KEY_DATE = "date";
	static final String KEY_STATE = "state";
	static final String TAG = "DBAdapter"; 
	static final String DATABASE_NAME = "MyDB";
	static final String DATABASE_TABLE = "tmpLATLNG";
	static final int DATABASE_VERSION = 1; 
	static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (_id INTEGER primary key AUTOINCREMENT, " 
			+ "lat text not null, lng text not null, date text not null, state text not null);"; 
	final Context context;

	DatabaseHelper DBHelper;
	SQLiteDatabase db;
	public DBAdapter(Context ctx) { 
		this.context = ctx;
		DBHelper = new DatabaseHelper(context); 
		
		//table �̸� ����. id�� �ϸ� ���� ��.
		
		
	}
	private static class DatabaseHelper extends SQLiteOpenHelper{ 
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override 
		public void onCreate(SQLiteDatabase db) { 
			try { 
				db.execSQL(DATABASE_CREATE); 
			} catch (SQLException e) {
				e.printStackTrace(); 
			}
		} 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
			Log.w(TAG, "Upgrading database from version " + oldVersion+ " to " 
					+ newVersion+ ", which will destroy all old data"); 
			db.execSQL("DROP TABLE IF EXISTS tmpLATLNG");
			onCreate(db); 
		}
	}
	// ---opens the database--- 
	public DBAdapter open() throws SQLException{ 
		db = DBHelper.getWritableDatabase();
		return this;
	}
	// ---closes the database---
	public void close() {
		DBHelper.close(); 
	}
	//---insert a contact into the database---
	public long insertContact(String lat, String lng, String date, String state) { 
		ContentValues initialValues= new ContentValues(); 
		initialValues.put(KEY_LAT, lat);
		initialValues.put(KEY_LNG, lng);
		initialValues.put(KEY_DATE, date);
		initialValues.put(KEY_STATE, state);
		return db.insert(DATABASE_TABLE, null, initialValues); 
	} 
	//---deletes a particular contact---
	public boolean deleteContact(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	} 
	// ---retrieves all the contacts--- 
	public Cursor getAllContacts() { 
		return db.query(DATABASE_TABLE, new String[] { 
				KEY_ROWID, KEY_LAT, KEY_LNG , KEY_DATE, KEY_STATE}, null, null, null, null, null);
	} 
	// ---retrieves a particular contact--- 
	public Cursor getContact(long rowId) throws SQLException{ 
		Cursor mCursor= db.query(true, DATABASE_TABLE, new String[] { 
				KEY_ROWID, KEY_LAT, KEY_LNG , KEY_DATE, KEY_STATE}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor!= null) {
			mCursor.moveToFirst(); 
		}
		return mCursor; 
	} // ---updates a contact--- 
	public boolean updateContact(long rowId, String lat, String lng, String date, String state) { 
		ContentValues args= new ContentValues();
		args.put(KEY_LAT, lat);
		args.put(KEY_LNG, lng); 
		args.put(KEY_DATE, date);
		args.put(KEY_STATE, state);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0; 
	}
}// class DBAdapter
