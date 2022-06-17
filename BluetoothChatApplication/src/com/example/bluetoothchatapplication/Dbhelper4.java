package com.example.bluetoothchatapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Dbhelper4 extends SQLiteOpenHelper {
	
	static String database_name="Application4";
	String table_name="device4";
	String column_1="id";
	String column_2="userone";
	String column_3="usertwo";
	String column_4="message";
	SQLiteDatabase db;

	public Dbhelper4(Context context) {
		super(context, database_name, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db=db;
		String create_table_query="create table "+table_name+" (id INTEGER PRIMARY KEY AUTOINCREMENT,userone TEXT,usertwo TEXT,message TEXT);";
		db.execSQL(create_table_query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		this.db=db;
		String drop_table_query="drop if table exists "+table_name;
		db.execSQL(drop_table_query);
	}
	
	public boolean insert_message(String user1,String user2,String message){
		db=getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(column_2, user1);
		values.put(column_3, user2);
		values.put(column_4, message);
		long result=db.insert(table_name, null, values);
		if(result==-1){
			return false;
		}
		else{
			return true;
		}
	}

	public Cursor retrieve_chat(){
		db=getReadableDatabase();
		String select_query="select * from "+table_name;
		Cursor data=db.rawQuery(select_query, null);
		return data;
	}
	
//	public boolean valide_chat(){
//		db=getReadableDatabase();
//		boolean valid=false;
//		String validator="select * from "+table_name;
//		Cursor name=db.rawQuery(validator,null);
//		while(name.moveToNext()){
//			if(name.getString(1).equals("Me") && name.getString(2).equals(Sample.connecteddevice)){
//				valid=true;
//			}
//			else{
//				valid=false;
//			}
//		}
//		return valid;
//	}
}
