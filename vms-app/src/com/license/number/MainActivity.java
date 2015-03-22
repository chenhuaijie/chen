package com.license.number;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import loader.ExecutorServiceLoader;
import loader.ResultOrException;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.database.DatabaseUtilsCompat;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.license.number.db.CarTable;
import com.license.number.entity.Car;

public class MainActivity extends SherlockFragmentActivity {

	private static final String TAG = "MainActivity";
	private static final int LOADER_CURSOR = 0;
	private static final int LOADER_IMPORT_DATA = 1;

	private EditText editNumber;
	private Button btnAdd;
	private Button btnDel;
	private ListView listView;

	protected CarAdapter adapter;

	private String keyword = "";

	private View loading ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		setContentView(R.layout.main);
		listView = (ListView) findViewById(R.id.listView1);
		editNumber = (EditText) findViewById(R.id.edit_number);
		btnAdd = (Button) findViewById(R.id.btn_add);
		btnDel = (Button) findViewById(R.id.btn_del);

		loading = findViewById(R.id.loading);
		loading.setVisibility(View.GONE) ;
		
		editNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				keyword = s.toString();
				startOrderCursorLoader();
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 8) {
					Toast.makeText(MainActivity.this, "你输入的字数已经超过了限制！",
							Toast.LENGTH_SHORT).show();
					s.delete(8, s.length());
				}
			}
		});
		btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String number = editNumber.getText().toString().toUpperCase();
				if (!TextUtils.isEmpty(number) && number.length() == 8) {
					if (adapter.getCount() == 0) {
						Car car = new Car();
						car.setNumber(number);
						car.setCreateDate(new Date().getTime());
						ContentValues values = CarTable.toContentValues(car);
						getContentResolver().insert(CarTable.CONTENT_URI,
								values);
						editNumber.setText("");
					} else {
						Toast.makeText(MainActivity.this,
								"The license number already exists！",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(MainActivity.this,
							"The license number's length less than eight！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		btnDel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("sysout",
						"" + Arrays.toString(adapter.getSelected().toArray()));
				Long[] ids = new Long[adapter.getSelected().size()];
				adapter.getSelected().toArray(ids);
				for (int i = 0; i < ids.length; i++) {
					Uri url = ContentUris.withAppendedId(
							CarTable.CONTENT_ID_URI_BASE, ids[i]);
					getContentResolver().delete(url, null, null);
				}

			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int arg2, long id) {
				if (!adapter.isMutiMode) {
					btnAdd.setVisibility(View.GONE);
					adapter.toggleSelectId(id);
				}
				return true;
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int arg2, long id) {
				if (adapter.isMutiMode) {
					adapter.toggleSelectId(id);
				}
			}
		});
		startOrderCursorLoader();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search_menu_item:
			btnAdd.setVisibility(View.GONE);
			adapter.reset();
			editNumber.setText("");
			return true;
		case R.id.add_menu_item:
			btnAdd.setVisibility(View.VISIBLE);
			adapter.reset();
			return true;
		case R.id.import_menu_item:
			adapter.reset();
			importData();
			return true;
		case R.id.help_menu_item:
			adapter.reset();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void importData() {
		new MyAsynTask().execute();
	}

	private static class ViewHolder {
		public TextView txtId;
		public CheckedTextView checked;
		public TextView txtNumber;
	}

	public class CarAdapter extends CursorAdapter {
		private static final String TAG = "CarAdapter";
		private List<Long> selected;
		private boolean isMutiMode = false;

		public CarAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
			selected = new ArrayList<Long>();
		}

		@Override
		public void bindView(View view, final Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			Car car = CarTable.fromCursor(cursor);
			holder.txtNumber.setText(car.number);
			String id = cursor
					.getString(cursor.getColumnIndex(BaseColumns._ID));
			holder.txtId.setText(id);
			if (isMutiMode) {
				holder.checked.setVisibility(View.VISIBLE);
				if (selected.contains(Long.parseLong(id))) {
					holder.checked.setChecked(true);
				} else {
					holder.checked.setChecked(false);
				}
			} else {
				holder.checked.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public View newView(final Context context, Cursor cursor,
				ViewGroup parent) {
			Log.v(TAG, "newView " + cursor.getPosition());
			LayoutInflater inflater = LayoutInflater.from(context);
			ViewHolder holder = new ViewHolder();
			View view;
			view = inflater.inflate(R.layout.car_item, parent, false);
			holder.checked = (CheckedTextView) view
					.findViewById(R.id.check_flag);
			holder.txtNumber = (TextView) view.findViewById(R.id.txt_number);
			holder.txtId = (TextView) view.findViewById(R.id.txt_id);
			view.setTag(holder);

			return view;
		}

		public void reset() {
			selected.clear();
			isMutiMode = false;
			btnDel.setVisibility(View.GONE);
			btnDel.setText("Delete");
			notifyDataSetChanged();
		}

		public void toggleSelectId(long id) {
			if (!selected.contains(id)) {
				selected.add(id);
				isMutiMode = true;
				if (btnDel.getVisibility() != View.VISIBLE) {
					btnDel.setVisibility(View.VISIBLE);
				}
				btnDel.setText("Delete(" + selected.size() + ")");
			} else {
				selected.remove(id);
				if (selected.size() == 0) {
					isMutiMode = false;
					if (btnDel.getVisibility() == View.VISIBLE)
						btnDel.setVisibility(View.GONE);
				}
			}
			notifyDataSetChanged();
		}

		public boolean isMutiMode() {
			return isMutiMode;
		}

		public List<Long> getSelected() {
			return selected;
		}
	}
	/*public void startImportDataLoader() {
		ImportDataLoaderCallback importDataLoaderCallback = new ImportDataLoaderCallback();
		LoaderManager lm = getSupportLoaderManager();
		if (lm.getLoader(LOADER_IMPORT_DATA) == null) {
			lm.initLoader(LOADER_IMPORT_DATA, null, importDataLoaderCallback);
		} else {
			lm.restartLoader(LOADER_IMPORT_DATA, null, importDataLoaderCallback);
		}
	}

	protected class ImportDataLoaderCallback implements
			LoaderManager.LoaderCallbacks<ResultOrException<Boolean, Exception>> {

		@Override
		public Loader<ResultOrException<Boolean, Exception>> onCreateLoader(
				int arg0, Bundle arg1) {
			return new ImportDataLoader(MainActivity.this);
		}

		@Override
		public void onLoadFinished(
				Loader<ResultOrException<Boolean, Exception>> arg0,
				ResultOrException<Boolean, Exception> arg1) {
			
		}
		@Override
		public void onLoaderReset(
				Loader<ResultOrException<Boolean, Exception>> arg0) {}


	}
	public static class ImportDataLoader extends ExecutorServiceLoader<Boolean>{
		
		public ImportDataLoader(Context context) {
			super(context);
		}

		@Override
		protected ResultOrException<Boolean, Exception> loadInBackground() {
			try {
				if (StorageUtil.isSDCardExist()) {
					List<String> lists = FileUtils.readFromSD("license.txt");
					for (String license : lists) {
						if (TextUtils.isEmpty(license))
							continue;
						Car car = new Car();
						car.setCreateDate(new Date().getTime());
						car.setNumber(license.toUpperCase());
						ContentValues values = CarTable.toContentValues(car);
						getContext().getContentResolver().insert(CarTable.CONTENT_URI, values);
					}
				}
			} catch (Exception e) {
				new ResultOrException<Boolean, Exception>(false);
			}
			
			return new ResultOrException<Boolean, Exception>(true);
		}
	}*/
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
			Uri baseUri = CarTable.CONTENT_URI;
			String selection = null;
			String[] selectionArgs = new String[] {};

			if (keyword.length() > 0) {
				selection = CarTable.NUMBER_COL + " LIKE ?";
				selectionArgs = DatabaseUtilsCompat.appendSelectionArgs(
						new String[] {}, new String[] { "%" + keyword + "%" });
			}
			return new CursorLoader(MainActivity.this, baseUri, null,
					selection, selectionArgs, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			// Log.v(TAG, "CursorLoaderCallback", "onLoadFinished");
			if (data != null) {
				data.setNotificationUri(getContentResolver(),
						CarTable.CONTENT_URI);
			}

			if (adapter == null) {
				int flags = CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;
				adapter = new CarAdapter(MainActivity.this, data, flags);
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
			Log.v(TAG, "CursorLoaderCallback onLoaderReset");
			Cursor oldCursor = adapter.swapCursor(null);
			if (oldCursor != null) {
				oldCursor.close();
			}
		}
	}
	public class MyAsynTask extends AsyncTask<Void, Integer, Boolean>{

		@Override  
        protected void onPreExecute() {  
            Log.i(TAG, "onPreExecute() called");  
            loading.setVisibility(View.VISIBLE) ;
        }  
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				if (StorageUtil.isSDCardExist()) {
					List<String> lists = FileUtils.readFromSD("license.txt");
					for (String license : lists) {
						if (TextUtils.isEmpty(license))
							continue;
						Car car = new Car();
						car.setCreateDate(new Date().getTime());
						car.setNumber(license.toUpperCase());
						ContentValues values = CarTable.toContentValues(car);
						getContentResolver().insert(CarTable.CONTENT_URI, values);
					}
				}
			} catch (Exception e) {
				return  false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result)
				Toast.makeText(MainActivity.this, "Import successful!", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(MainActivity.this, "The import failed!", Toast.LENGTH_LONG).show();
				
			loading.setVisibility(View.GONE) ;
		}
		
		
	}
}
