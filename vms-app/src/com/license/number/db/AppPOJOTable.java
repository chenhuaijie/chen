package com.license.number.db;

import java.util.Date;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.database.DatabaseUtilsCompat;
import android.text.TextUtils;
import android.util.Log;

import com.license.number.entity.AppPOJO;
import com.license.number.entity.Car;
import com.license.number.entity.SBook;
import com.license.number.entity.User;


public class AppPOJOTable implements BaseColumns {
  public static final String TAG = "AppPOJOTable" ;
  public static final String NAME = "AppPOJO" ;
  
  //columns
  
  /*
   
{
    "book": {
        "book_enddate": 1427126400000,
        "book_idsbook": "65cd4e5a-afe2-4b86-9c59-11591369993b",
        "book_isexpire": "Y",
        "book_lease": "11111",
        "book_lessee": "11111",
        "book_startdate": 1425830400000,
        "book_usedept": "1111",
    },
    "car": {
        "car_carid": "946b6a84-b523-4d73-ad17-620f22bad3a3",
        "car_name": "3",
        "car_origin": "3",
        "car_plate_num": "323",
        "car_status": "Y",
        "car_type": "3"
    },
    "user": {
        "user_company": "3",
        "user_dept": "3",
        "user_email": "3",
        "user_name": "3",
        "user_nickname": "3",
        "user_phonenumber": "3",
        "user_role": "user",
        "user_userid": "1d265e9b-a23a-40db-9b32-1931a54a6dc4"
    }
}
    
   * */
  public static final String SBOOK_ID_COL					= "sbook_id" 		;
  public static final String SBOOK_ENDDATE_COL				= "sbook_enddate" 	;
  public static final String SBOOK_STARTDATE_COL			= "sbook_startdate" ;
  public static final String SBOOK_ISEXPIRE_COL				= "sbook_isexpire" 	;
  public static final String SBOOK_LEASE_COL				= "sbook_lease" 	;
  public static final String SBOOK_LESSEE_COL				= "sbook_lessee" 	;
  public static final String SBOOK_USEDEPT_COL				= "sbook_usedept" 	;
  
  public static final String CAR_ID_COL						= "car_id" 			;
  public static final String CAR_NAME_COL					= "car_name" 		;
  public static final String CAR_ORIGIN_COL					= "car_origin" 		;
  public static final String CAR_PLATE_NUM_COL				= "car_plate_num" 	;
  public static final String CAR_STATUS_COL					= "car_status" 		;
  public static final String CAR_TYPE_COL					= "car_type" 		;
  
  public static final String USER_ID_COL					= "user_id" 		;
  public static final String USER_COMPANY_COL				= "user_company" 	;
  public static final String USER_DEPT_COL					= "user_dept" 		;
  public static final String USER_EMAIL_COL					= "user_email" 		;
  public static final String USER_NAME_COL					= "user_name" 		;
  public static final String USER_NICKNAME_COL				= "user_nickname" 	;
  public static final String USER_PHONENUMBER_COL			= "user_phonenumber";
  public static final String USER_ROLE_COL					= "user_role" 		;
  
  
  public static final String CREATION_DATE_COL = "creation_date" ;
  
  
  private static final String AUTHORITY           = AppPOJOProvider.AUTHORITY;
  public static final Uri     CONTENT_URI         = Uri.parse("content://" + AUTHORITY + "/" + NAME);
  public static final Uri     CONTENT_ID_URI_BASE = Uri.parse("content://" + AUTHORITY + "/" + NAME + "/");
  public static final String  CONTENT_TYPE        = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + NAME;
  public static final String  CONTENT_ITEM_TYPE   = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + NAME;
  public static final String  DEFAULT_SORT_ADDR   = CREATION_DATE_COL + " DESC";
   
  
  private AppPOJOTable(){}
  public static void create(SQLiteDatabase db){
	  
	  
    db.execSQL("CREATE TABLE "+NAME+"(" 
    		+_ID              		+" INTEGER PRIMARY KEY ,"
    		
    		+SBOOK_ID_COL		     +" TEXT  DEFALUT '',"
    		+SBOOK_ENDDATE_COL	     +" TEXT  ,"
    		+SBOOK_STARTDATE_COL     +" TEXT  ,"
    		+SBOOK_ISEXPIRE_COL	     +" TEXT  ,"
    		+SBOOK_LEASE_COL	     +" TEXT  ,"
    		+SBOOK_LESSEE_COL	     +" TEXT  ,"
    		+SBOOK_USEDEPT_COL	     +" TEXT  ,"
    		                         
    		+CAR_ID_COL			     +" TEXT NOT NULL ,"
    		+CAR_NAME_COL		     +" TEXT  ,"
    		+CAR_ORIGIN_COL		     +" TEXT  ,"
    		+CAR_PLATE_NUM_COL	     +" TEXT  ,"
    		+CAR_STATUS_COL		     +" TEXT  ,"
    		+CAR_TYPE_COL		     +" TEXT  ,"
    		                         
    		+USER_ID_COL		     +" TEXT  ,"
    		+USER_COMPANY_COL	     +" TEXT  ,"
    		+USER_DEPT_COL		     +" TEXT  ,"
    		+USER_EMAIL_COL		     +" TEXT  ,"
    		+USER_NAME_COL		     +" TEXT  ,"
    		+USER_NICKNAME_COL	     +" TEXT  ,"
    		+USER_PHONENUMBER_COL    +" TEXT  ,"
    		+USER_ROLE_COL		     +" TEXT  ,"
    		+CREATION_DATE_COL		+ " LONG "
    		+")"
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
     long rowId = db.insert(NAME, null, values);
     // Log.v(TAG, "insert " + uri.toString());
     if (rowId >= 0) {
       Uri noteUri = ContentUris.withAppendedId(CONTENT_ID_URI_BASE, rowId);
       context.getContentResolver().notifyChange(noteUri, null);
       return noteUri;
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
    	count = db.update(NAME, values, where, whereArgs);
    } else {
      throw new IllegalArgumentException("Unknown URI " + uri);
    }
    context.getContentResolver().notifyChange(uri, null);
    return count;
  }
  public static ContentValues toContentValues(AppPOJO appPOJO){
    ContentValues values = new ContentValues();
    
    if(appPOJO.getCreateDate()!=null)
    	values.put(CREATION_DATE_COL, appPOJO.getCreateDate().getTime()) ;
   
	if(appPOJO.getBook()!=null){
	   SBook sBook = appPOJO.getBook() ;
	   values.put(SBOOK_ID_COL		, sBook.getIdsbook());
	   values.put(SBOOK_ENDDATE_COL	, sBook.getEndDateString());
	   values.put(SBOOK_STARTDATE_COL,sBook.getStartDateString());
	   values.put(SBOOK_ISEXPIRE_COL, sBook.getIsexpire());
	   values.put(SBOOK_LEASE_COL	, sBook.getLease());
	   values.put(SBOOK_LESSEE_COL	, sBook.getLessee());
	   values.put(SBOOK_USEDEPT_COL	, sBook.getUsedept());
   }
   if(appPOJO.getCar()!=null){
	   Car car = appPOJO.getCar() ;
	   values.put(CAR_ID_COL		, car.getCarid());
	   values.put(CAR_NAME_COL		, car.getName());
	   values.put(CAR_ORIGIN_COL	, car.getOrigin());
	   values.put(CAR_PLATE_NUM_COL	, car.getPlate_num());
	   values.put(CAR_STATUS_COL	, car.getStatus());
	   values.put(CAR_TYPE_COL		, car.getType());
	   
   }
   if(appPOJO.getUser()!=null){
	   User user = appPOJO.getUser() ;
	   values.put(USER_ID_COL			, user.getUserid());
	   values.put(USER_COMPANY_COL		, user.getCompany());
	   values.put(USER_DEPT_COL			, user.getDept());
	   values.put(USER_EMAIL_COL		, user.getEmail());
	   values.put(USER_NAME_COL			, user.getName());
	   values.put(USER_NICKNAME_COL		, user.getNickname());
	   values.put(USER_PHONENUMBER_COL	, user.getPhonenumber());
	   values.put(USER_ROLE_COL			, user.getRole());
   
   }
    
    return values ;
  }
  public static AppPOJO fromCursor(Cursor cursor) throws Exception{
	AppPOJO appPOJO = new AppPOJO() ;
	 SBook sBook = null ;
   int index = cursor.getColumnIndex(SBOOK_ID_COL);
   if(index>0 && !TextUtils.isEmpty(cursor.getString(index))){
	   sBook = new SBook() ;
	   sBook.setIdsbook(cursor.getString(index));
	   index = cursor.getColumnIndex(SBOOK_ENDDATE_COL	   );
	   if(index>0) 
		   sBook.setEndDateString(cursor.getString(index));
	   if(index>0) 
		   sBook.setStartDateString(cursor.getString(index));
	   index = cursor.getColumnIndex(SBOOK_ISEXPIRE_COL)   ;
	   if(index>0) 
		   sBook.setIsexpire(cursor.getString(index));
	   index = cursor.getColumnIndex(SBOOK_LEASE_COL	   );
	   if(index>0) 
		   sBook.setLease(cursor.getString(index));
	   index = cursor.getColumnIndex(SBOOK_LESSEE_COL	   );
	   if(index>0) 
		   sBook.setLessee(cursor.getString(index));
	   index = cursor.getColumnIndex(SBOOK_USEDEPT_COL	   );
	   if(index>0)
		   sBook.setUsedept(cursor.getString(index)) ;
   }
	   
		
	Car car = null;
	index = cursor.getColumnIndex(CAR_ID_COL			);
	if(index>0&&!TextUtils.isEmpty(cursor.getString(index))){
		car = new Car();
		car.setCarid(cursor.getString(index)) ;
		index = cursor.getColumnIndex(CAR_NAME_COL		   );
		if(index>0)
			car.setName(cursor.getString(index)) ;
		index = cursor.getColumnIndex(CAR_ORIGIN_COL	)   ;
		if(index>0)
			car.setOrigin(cursor.getString(index)) ;
		index = cursor.getColumnIndex(CAR_PLATE_NUM_COL	   );
		if(index>0)
			car.setPlate_num(cursor.getString(index)) ;
		index = cursor.getColumnIndex(CAR_STATUS_COL	)   ;
		if(index>0)
			car.setStatus(cursor.getString(index)) ;
		index = cursor.getColumnIndex(CAR_TYPE_COL		   );
		if(index>0)
			car.setType(cursor.getString(index)) ;
	}

	User user = null ;
    index = cursor.getColumnIndex(USER_ID_COL);
	if(index>0 && !TextUtils.isEmpty(cursor.getString(index))){
		user = new User();
		user.setUserid(cursor.getString(index)) ;
		index = cursor.getColumnIndex(USER_COMPANY_COL	   );
		if(index>0)
			user.setCompany(cursor.getString(index)) ;
		index = cursor.getColumnIndex(USER_DEPT_COL		   );
		if(index>0)
			user.setDept(cursor.getString(index)) ;
		index = cursor.getColumnIndex(USER_EMAIL_COL	)   ;
		if(index>0)
			user.setEmail(cursor.getString(index)) ;
		index = cursor.getColumnIndex(USER_NAME_COL		   );
		if(index>0)
			user.setName(cursor.getString(index)) ;
		index = cursor.getColumnIndex(USER_NICKNAME_COL	   );
		if(index>0)
			user.setNickname(cursor.getString(index)) ;
		index = cursor.getColumnIndex(USER_PHONENUMBER_COL );
		if(index>0)
			user.setPhonenumber(cursor.getString(index)) ;
		index = cursor.getColumnIndex(USER_ROLE_COL		   );
		if(index>0)
			user.setRole(cursor.getString(index)) ;
	}
    appPOJO.setBook(sBook) ;
    appPOJO.setCar(car) ;
    appPOJO.setUser(user) ;
    
    index = cursor.getColumnIndex(CREATION_DATE_COL);
    if(index>0)
		appPOJO.setCreateDate(new Date(cursor.getLong(index))) ;
    
    return appPOJO ;
  }
}
