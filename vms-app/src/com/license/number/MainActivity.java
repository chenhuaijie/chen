package com.license.number;

import java.io.IOException;
import java.util.Date;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.license.number.db.AppPOJOTable;
import com.license.number.entity.AppPOJO;
import com.license.number.entity.Car;
import com.license.number.network.LocClient;
import com.utils.AlertDialogFragment;
import com.utils.CommUtils;
import com.utils.SharedPreferenceHelper;

public class MainActivity extends SherlockFragmentActivity {

	private static final String TAG = "MainActivity";
	private static final int LOADER_CURSOR = 0;

	
	private EditText editNumber;
	private Button btnSearch;
	private ListView listView;
	private TextView alert_txt;
	protected AppPojoAdapter adapter;

	private LinearLayout search_llayout ;
	private LinearLayout pojo_item_lLayout ;
	
	InputMethodManager inputManager  ;
	
	private View loading;

	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				alert_txt.setVisibility(View.GONE);
				break;
			}
			
		};
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		setContentView(R.layout.main);
		
		alert_txt = (TextView) findViewById(R.id.alert_txt);
		alert_txt.setVisibility(View.GONE);

		pojo_item_lLayout = (LinearLayout)findViewById(R.id.pojo_item) ;
		pojo_item_lLayout.setVisibility(View.GONE);
		listView = (ListView) findViewById(R.id.listView1);
		listView.setVisibility(View.GONE);
		
		search_llayout = (LinearLayout)findViewById(R.id.search_llayout) ;
		
		editNumber = (EditText) findViewById(R.id.edit_number);
		editNumber.requestFocus();
		btnSearch = (Button) findViewById(R.id.btn_search);

		loading = findViewById(R.id.loading);
		loading.setVisibility(View.GONE);

		inputManager =
                (InputMethodManager)editNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		initHolder(pojo_item_lLayout);
		
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String number = editNumber.getText().toString().toUpperCase();
				if (!TextUtils.isEmpty(number)) {
					new MyAsynTask().execute();
				} else {
					Toast.makeText(MainActivity.this,
							"The license number's length less than eight！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int arg2, long id) {
				
				DialogFragment networkFragment = new AlertDialogFragment();
				Bundle  bundle = new Bundle();
				bundle.putLong(BaseColumns._ID, id) ;
				networkFragment.setArguments(bundle);
				networkFragment.show(getFragmentManager(), "NetworkAlert");
				return true;
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int arg2, long id) {
			}
		});
		
		
		SharedPreferenceHelper helper = new SharedPreferenceHelper(this);
		String ServerAddress  = helper.getServerAddress();
		
		if(TextUtils.isEmpty(ServerAddress)){
			Toast.makeText(this, "服务器地址不能为空！", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this,SettingActivity.class);
			startActivity(intent); 
			
		}else{
			LocClient.init(this);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search_menu_item:// search
			search_llayout.setVisibility(View.VISIBLE);
			pojo_item_lLayout.setVisibility(View.GONE);
			listView.setVisibility(View.INVISIBLE);
			editNumber.setFocusable(true);
			editNumber.requestFocus();
			inputManager.showSoftInput(editNumber, 0);
			editNumber.setText("");
			break;
		case R.id.history_menu_item:// history
			inputManager.hideSoftInputFromWindow(editNumber.getWindowToken(), 0);
			
			pojo_item_lLayout.setVisibility(View.GONE);
			search_llayout.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			startOrderCursorLoader();
			break;
		case R.id.setting_menu_item:
			Intent intent = new Intent(this,SettingActivity.class);
			startActivity(intent); 
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private static class ViewHolder {
		
		private TextView createDate ;
		
		public LinearLayout car_lLayout;
		public TextView car_name;
		public TextView car_status;
		public TextView car_plate_num;

		public LinearLayout sbook_lLayout;
		public TextView sbook_isexpire;
		public TextView sbook_usedept;
		public TextView sbook_startdate;
		public TextView sbook_enddate;

		public LinearLayout user_lLayout;
		public TextView user_dept;
		public TextView user_nickname;
		public TextView user_phonenum;
	}

	public class AppPojoAdapter extends CursorAdapter {
		private static final String TAG = "CarAdapter";

		public AppPojoAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);

		}

		@Override
		public void bindView(View view, final Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();

			AppPOJO appPOJO;
			try {
				appPOJO = AppPOJOTable.fromCursor(cursor);
				setHolderValues(appPOJO,holder);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public View newView(final Context context, Cursor cursor,
				ViewGroup parent) {
			Log.v(TAG, "newView " + cursor.getPosition());
			LayoutInflater inflater = LayoutInflater.from(context);
			View view;
			view = inflater.inflate(R.layout.app_pojo_item, parent, false);
			initHolder(view);
			return view;
		}

		public void reset() {

		}
	}
	private void setHolderValues(AppPOJO appPOJO,ViewHolder holder){
		if(appPOJO!=null){
			holder.createDate.setText(CommUtils.formatDate(appPOJO.getCreateDate(),1));
			if (appPOJO.getCar() != null) {
				holder.car_lLayout.setVisibility(View.VISIBLE);
				Car car = appPOJO.getCar();
				holder.car_name.setText(car.getName());
				holder.car_status.setText(car.getStatusName());
				holder.car_plate_num.setText(car.getPlate_num());
			} else {
				holder.car_lLayout.setVisibility(View.GONE);
			}
		}
		/*if (appPOJO.getBook() != null) {
			holder.sbook_lLayout.setVisibility(View.VISIBLE);
			SBook book = appPOJO.getBook();
			holder.sbook_isexpire.setText(book.getIsexpireString());
			holder.sbook_usedept.setText(book.getUsedept());
			holder.sbook_startdate.setText(book.getStartDateString());
			holder.sbook_enddate.setText(book.getEndDateString());

		} else {
			holder.sbook_lLayout.setVisibility(View.GONE);
		}
		if (appPOJO.getUser() != null) {
			holder.user_lLayout.setVisibility(View.VISIBLE);
			User user = appPOJO.getUser();

			holder.user_dept.setText(user.getDept());
			holder.user_nickname.setText(user.getNickname());
			holder.user_phonenum.setText(user.getPhonenumber());

		} else {
			holder.user_lLayout.setVisibility(View.GONE);
		}*/
	}
	private void initHolder(View view){
		ViewHolder holder = new ViewHolder();
		holder.createDate = (TextView) view.findViewById(R.id.createdate);
		
		holder.car_lLayout = (LinearLayout) view
				.findViewById(R.id.car_llayout);
		holder.car_name = (TextView) view.findViewById(R.id.car_name);
		holder.car_status = (TextView) view.findViewById(R.id.car_status);
		holder.car_plate_num = (TextView) view
				.findViewById(R.id.car_plate_num);

		holder.sbook_lLayout = (LinearLayout) view
				.findViewById(R.id.sbook_llayout);
		holder.sbook_isexpire = (TextView) view
				.findViewById(R.id.sbook_isexpire);
		holder.sbook_usedept = (TextView) view
				.findViewById(R.id.sbook_usedept);
		holder.sbook_startdate = (TextView) view
				.findViewById(R.id.sbook_startdate);
		holder.sbook_enddate = (TextView) view
				.findViewById(R.id.sbook_enddate);

		holder.user_lLayout = (LinearLayout) view
				.findViewById(R.id.user_llayout);
		holder.user_dept = (TextView) view.findViewById(R.id.user_dept);
		holder.user_nickname = (TextView) view
				.findViewById(R.id.user_nickname);
		holder.user_phonenum = (TextView) view
				.findViewById(R.id.user_phonenum);

		view.setTag(holder);
	}
	public void startOrderCursorLoader() {
		CursorLoaderCallback cursorLoaderCallback = new CursorLoaderCallback();
		LoaderManager lm = getSupportLoaderManager();
		if (lm.getLoader(LOADER_CURSOR) == null) {
			lm.initLoader(LOADER_CURSOR, null, cursorLoaderCallback);
		} else {
			lm.restartLoader(LOADER_CURSOR, null, cursorLoaderCallback);
		}
	}

	protected class CursorLoaderCallback implements
			LoaderManager.LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
			Log.v(TAG, "CursorLoaderCallback onCreateLoader");
			Uri baseUri = AppPOJOTable.CONTENT_URI;
			String selection = null;
			String[] selectionArgs = new String[] {};

			return new CursorLoader(MainActivity.this, baseUri, null,
					selection, selectionArgs, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			// Log.v(TAG, "CursorLoaderCallback", "onLoadFinished");
			if (data != null) {
				data.setNotificationUri(getContentResolver(),
						AppPOJOTable.CONTENT_URI);
			}

			if (adapter == null) {
				int flags = CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;
				adapter = new AppPojoAdapter(MainActivity.this, data, flags);
				listView.setAdapter(adapter);
			} else {
				Log.v(TAG,
						"CursorLoaderCallback onLoadFinished, reuse old adapter ");
				Cursor oldCursor = adapter.swapCursor(data);
				if (listView.getAdapter() == null) {
					listView.setAdapter(adapter);
				}
				if (oldCursor != null) {
					oldCursor.close();
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}

	public class MyAsynTask extends AsyncTask<Void, Integer, AppPOJO> {

		@Override
		protected void onPreExecute() {
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected AppPOJO doInBackground(Void... params) {
			try {
				AppPOJO appPOJO = LocClient.reportPosi("");

				if (appPOJO.isExists()) {
					
					appPOJO.setCreateDate(new Date());
					
					ContentValues values = AppPOJOTable
							.toContentValues(appPOJO);
					getContentResolver().insert(AppPOJOTable.CONTENT_URI,
							values);
				}
				return appPOJO;
			} catch (com.license.number.network.HttpException e) {
				e.printStackTrace();
				return null ;
			} catch (IOException e) {
				e.printStackTrace();
				return null ;
			} catch (Exception e) {
				e.printStackTrace();
				return null ;
			} 
		}

		@Override
		protected void onPostExecute(AppPOJO appPOJO) {
			if (appPOJO!=null && appPOJO.isExists()) {
				alert_txt.setVisibility(View.GONE);
				if(listView.getVisibility() != View.VISIBLE){
					setHolderValues(appPOJO, (ViewHolder)pojo_item_lLayout.getTag());
					pojo_item_lLayout.setVisibility(View.VISIBLE);
				}
			} else {
				alert_txt.setVisibility(View.VISIBLE);
				pojo_item_lLayout.setVisibility(View.GONE);
				listView.setVisibility(View.INVISIBLE);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						handler.obtainMessage(1).sendToTarget();
					}
				},3000);
			}
			
			loading.setVisibility(View.GONE);
		}

	}
}
