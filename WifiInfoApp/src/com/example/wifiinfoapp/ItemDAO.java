package com.example.wifiinfoapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @這個是用來處理database的資料的
 */

public class ItemDAO {

	public static final String TABLE_NAME = "item";
	public static final String KEY_ID = "_id";
	public static final String SSID_COLUMN = "SSID";
	public static final String LEVEL_COLUMN = "Level";
	public static final String BSSID_COLUMN = "BSSID";
	
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ SSID_COLUMN 
			+ " TEXT NOT NULL, "
			+ LEVEL_COLUMN
			+ " TEXT NOT NULL, " 
			+BSSID_COLUMN
			+ " TEXT NOT NULL) " ;
		
	private SQLiteDatabase db;

	public ItemDAO(Context context) {
		db = MyDBHelper.getDatabase(context);

	}

	public void close() {
		db.close();
	}

	public Item insert(Item item) {
		ContentValues cv = new ContentValues();
		cv.put(SSID_COLUMN, item.getSSID());
		cv.put(LEVEL_COLUMN, item.getLevel());
		cv.put(BSSID_COLUMN, item.getBSSID());
		
		long id = db.insert(TABLE_NAME, null, cv);
		item.setId(id);
		return item;
	}

	public boolean update(Item item) {
		ContentValues cv = new ContentValues();
		cv.put(SSID_COLUMN, item.getSSID());
		cv.put(LEVEL_COLUMN, item.getLevel());
		cv.put(BSSID_COLUMN, item.getBSSID());
		String where = KEY_ID + "=" + item.getId();
		return db.update(TABLE_NAME, cv, where, null) > 0;
	}

	public boolean delete(long id) {
		String where = KEY_ID + "=" + id;
		return db.delete(TABLE_NAME, where, null) > 0;
	}

	public List<Item> getAll() {
		List<Item> result = new ArrayList<Item>();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				null, null);
		while (cursor.moveToNext()) {
			result.add(getRecord(cursor));
		}
		cursor.close();
		return result;
	}

	public Item get(long id) {
		Item item = null;
		String where = KEY_ID + "=" + id;
		Cursor result = db.query(TABLE_NAME, null, where, null, null, null,
				null, null);
		if (result.moveToFirst()) {
			item = getRecord(result);
		}
		result.close();
		return item;
	}

	public Item getRecord(Cursor cursor) {
		Item result = new Item();
		result.setId(cursor.getLong(0));
		result.setSSID(cursor.getString(1));
		result.setLevel(cursor.getString(2));
		result.setBSSID(cursor.getString(3));
		return result;
	}

	public int getCount() {
		int result = 0;
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		return result;
	}

}
