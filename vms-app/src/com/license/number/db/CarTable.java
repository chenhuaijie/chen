package com.license.number.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.database.DatabaseUtilsCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.license.number.entity.Car;


public class CarTable implements BaseColumns {
  public static final String TAG = "CarTable" ;
  public static final String NAME = "car" ;
  
  //columns
  public static final String NUMBER_COL = "number";
  public static final String CREATION_DATE_COL = "creation_date" ;
  
  
  private static final String AUTHORITY           = CarProvider.AUTHORITY;
  public static final Uri     CONTENT_URI         = Uri.parse("content://" + AUTHORITY + "/" + NAME);
  public static final Uri     CONTENT_ID_URI_BASE = Uri.parse("content://" + AUTHORITY + "/" + NAME + "/");
  public static final String  CONTENT_TYPE        = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + NAME;
  public static final String  CONTENT_ITEM_TYPE   = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + NAME;
  public static final String  DEFAULT_SORT_ADDR   = CREATION_DATE_COL + " DESC";
   
  
  private CarTable(){}
  public static void create(SQLiteDatabase db){
	  
	  
    db.execSQL("CREATE TABLE "+NAME+"(" 
    		+_ID              +" INTEGER PRIMARY KEY ,"
    		+NUMBER_COL       +" TEXT NOT NULL ,"
    		+CREATION_DATE_COL+" LONG NOT NULL)"
    		) ;
  }
  //VERSION7 later
  public static void upgrade(SQLiteDatabase db, int oldVersion , int newVersion){
  }
  
  private static boolean match(Uri uri) {
    return uri.toString().startsWith(CONTENT_URI.toString());
  }
  
  private static boolean matchOne(Uri uri) {
    return uri.toString().startsWith(CONTENT_ID_URI_BASE.toString());
  }
  
  
  public static Cursor query(SQLiteDatabase db, Uri uri, String[] projection, 
      String selection, String[] selectionArgs, String sortAddr) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    qb.setTables(NAME);
    
    if (!match(uri))
      throw new IllegalArgumentException("Unknown URI " + uri);
    
    if (matchOne(uri)) {
      qb.appendWhere(_ID + "=?");
      selectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[] { uri.getLastPathSegment() });
    }
    if (TextUtils.isEmpty(sortAddr)) {
      sortAddr = DEFAULT_SORT_ADDR;
    }
    Cursor c = qb.query(db, projection, selection, selectionArgs, null /* group */, null /* filter */, sortAddr);
    return c;
  }
  //insert
  public static Uri insert(SQLiteDatabase db, Context context, Uri uri, ContentValues values) {
    Log.i(TAG, "insert " + uri.toString());
   if (!match(uri)) {
     throw new IllegalArgumentException("Unknown URI " + uri);
   }
   String number = values.getAsString(NUMBER_COL);
   SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
   qb.setTables(NAME);
//   qb.appendWhere(NAME_COL + "=?");
   String selection = NAME+"."+NUMBER_COL + "=?" ;
   String[] selectionArgs = DatabaseUtilsCompat.appendSelectionArgs(null, new String[] { number });
   Cursor cursor = qb.query(db, null, selection, selectionArgs, null, null, null);
   
   if (cursor!=null && !cursor.moveToFirst()){
     Log.v(TAG, "not find in table: " + uri.toString());
     long rowId = db.insert(NAME, null, values);
     // Log.v(TAG, "insert " + uri.toString());
     cursor.close() ;
     if (rowId >= 0) {
       Uri noteUri = ContentUris.withAppendedId(CONTENT_ID_URI_BASE, rowId);
       context.getContentResolver().notifyChange(noteUri, null);
       return noteUri;
     }
     throw new SQLException("Failed to insert row into " + uri);
   }else if (cursor!=null){
     cursor.close() ;
   }
   return null ;
 }
  //delete
  public static int delete(SQLiteDatabase db, Context context, Uri uri, String where, String[] whereArgs) {
    Log.i(TAG, "delete " + uri);
    String finalWhere;
    int count;
    if (matchOne(uri)) {
      finalWhere = DatabaseUtilsCompat.concatenateWhere(_ID + " = " + ContentUris.parseId(uri), where);
      count = db.delete(NAME, finalWhere, whereArgs);
    } else if (match(uri)) {
      count = db.delete(NAME, where, whereArgs);
    } else {
      throw new IllegalArgumentException("Unknown URI " + uri);
    }
    context.getContentResolver().notifyChange(CONTENT_URI, null);
    return count;
  }
  //update
  public static int update(SQLiteDatabase db, Context context, Uri uri, ContentValues values, String where, String[] whereArgs) {
    Log.i(TAG, "update " + uri.toString());
    int count;
    String finalWhere;
    if (matchOne(uri)) {
      finalWhere = DatabaseUtilsCompat.concatenateWhere(_ID + " = " + ContentUris.parseId(uri), where);
      count = db.update(NAME, values, finalWhere, whereArgs);
    } else if (match(uri)) {
      if (values.getAsString(NUMBER_COL)!=null)
        values.remove(NUMBER_COL) ;
      count = db.update(NAME, values, where, whereArgs);
    } else {
      throw new IllegalArgumentException("Unknown URI " + uri);
    }
    context.getContentResolver().notifyChange(uri, null);
    return count;
  }
  public static ContentValues toContentValues(Car car){
    ContentValues values = new ContentValues();
    if(car.number!=null)
      values.put(NUMBER_COL, car.number) ;
    if(car.createDate>0)
      values.put(CREATION_DATE_COL, car.createDate) ;
    return values ;
  }
  public static Car fromCursor(Cursor cursor){
	Car car = new Car() ;
    int index = cursor.getColumnIndex(NUMBER_COL) ;
    if(index>0)
    	car.number = cursor.getString(index);
    index = cursor.getColumnIndex(CREATION_DATE_COL) ;
    if(index>0)
       car.createDate = cursor.getLong(index) ;
    return car ;
  }
}
