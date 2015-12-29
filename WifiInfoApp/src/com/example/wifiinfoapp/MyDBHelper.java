package com.example.wifiinfoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @建立database的表格
 */

public class MyDBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "Sensor.db";
	public static final int VERSION = 1;
	private static SQLiteDatabase database;

	public MyDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(ItemDAO.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + ItemDAO.TABLE_NAME);
		onCreate(db);
	}
	public static SQLiteDatabase getDatabase(Context context) {
		if (database == null || !database.isOpen()) {
			database = new MyDBHelper(context, DATABASE_NAME, null, VERSION)
					.getWritableDatabase();
		}

		return database;
	}

}
