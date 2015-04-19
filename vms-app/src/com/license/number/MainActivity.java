package com.license.number;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.license.number.entity.AppEJB;
import com.license.number.entity.AppPOJO;
import com.license.number.entity.Car;
import com.license.number.network.AppClient;

public class MainActivity extends SherlockFragmentActivity {

	private static final String TAG = "MainActivity";

	
	private EditText editNumber;
	private Button btnSearch;
	private ListView listView;
	private TextView alert_txt;
	protected BookBaseAdpter bookBaseAdpter;

	
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

		listView = (ListView) findViewById(R.id.listView1);
		
		
		editNumber = (EditText) findViewById(R.id.edit_number);
		editNumber.requestFocus();
		btnSearch = (Button) findViewById(R.id.btn_search);

		loading = findViewById(R.id.loading);
		loading.setVisibility(View.GONE);

		inputManager =
                (InputMethodManager)editNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String number = editNumber.getText().toString().toUpperCase();
				if (!TextUtils.isEmpty(number)) {
					new MyAsynTask().execute(number);
				} else {
					Toast.makeText(MainActivity.this,
							R.string.not_empty,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		bookBaseAdpter = new BookBaseAdpter(this) ;
		listView.setAdapter(bookBaseAdpter);
	}


	private static class ViewHolder {
		public LinearLayout car_lLayout;
		public TextView car_name;
		public TextView car_status;
		public TextView car_plate_num;
		
		private LinearLayout driver_lLayout;
		public TextView sbook_driver ;

		public LinearLayout usedept_llayout;
		public TextView lessee;
	}
	
	public class BookBaseAdpter extends BaseAdapter{

		LayoutInflater lInflater ;
		List<AppPOJO> data ;
		public BookBaseAdpter(Context context){
			lInflater = LayoutInflater.from(context);
			data = new ArrayList<AppPOJO>();
		}
		public void setData(List<AppPOJO> data){
			this.data = data ;
			notifyDataSetChanged();
		}
		public void clear(){
			this.data.clear() ;
			notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if(convertView==null){
				convertView = lInflater.inflate(R.layout.app_pojo_item, null);
				initHolder(convertView);
			}
			setHolderValues(data.get(position), (ViewHolder)convertView.getTag());
			return convertView;
		}
		
	}

	
	private void setHolderValues(AppPOJO appPOJO,ViewHolder holder){
		if(appPOJO!=null){
			
			if (appPOJO.getCar() != null) {
				holder.car_lLayout.setVisibility(View.VISIBLE);
				Car car = appPOJO.getCar();
				holder.car_name.setText(car.getName());
				holder.car_status.setText(car.getStatusName());
				holder.car_plate_num.setText(car.getPlate_num());
				if (appPOJO.getBook() != null) {
					holder.driver_lLayout.setVisibility(View.VISIBLE);
					holder.sbook_driver.setText(appPOJO.getBook().getDriver());
					holder.usedept_llayout.setVisibility(View.VISIBLE);
					holder.lessee.setText(appPOJO.getBook().getLease());
				}else {
					holder.driver_lLayout.setVisibility(View.GONE);
					holder.usedept_llayout.setVisibility(View.GONE);
				}
			} else {
				holder.car_lLayout.setVisibility(View.GONE);
			}
		}
	}
	private void initHolder(View view){
		ViewHolder holder = new ViewHolder();
		
		holder.car_lLayout = (LinearLayout) view
				.findViewById(R.id.car_llayout);
		holder.car_name = (TextView) view.findViewById(R.id.car_name);
		holder.car_status = (TextView) view.findViewById(R.id.car_status);
		holder.car_plate_num = (TextView) view
				.findViewById(R.id.car_plate_num);
	
		holder.driver_lLayout = (LinearLayout)view.findViewById(R.id.driver_llayout);
		holder.sbook_driver =(TextView)view.findViewById(R.id.driver);
		
		holder.usedept_llayout = (LinearLayout)view.findViewById(R.id.usedept_llayout) ;
		holder.lessee = (TextView)view.findViewById(R.id.lessee);
		
		view.setTag(holder);
	}
	
	public class Result{
		private boolean hasResult = false ;
		private AppEJB appEJB ;
		private Exception exception ;
		
	}
	public class MyAsynTask extends AsyncTask<String, Integer, Result> {

		@Override
		protected void onPreExecute() {
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected Result doInBackground(String... params) {
			Result result = new Result();
			try {
				String plate_num = editNumber.getText().toString();
				AppEJB appEJB = AppClient.reportPosi(plate_num);
				result.hasResult = true ;
				result.appEJB = appEJB ;
			}  catch (Exception e) {
				e.printStackTrace();
				result.hasResult = false ;
				result.exception = e ;
			} 
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			if(result.hasResult){
				AppEJB appEJB = result.appEJB ;
				if (appEJB!=null && appEJB.isExists()) {
					alert_txt.setVisibility(View.GONE);
					bookBaseAdpter.setData(appEJB.getCars());
				} else {
					alert_txt.setVisibility(View.VISIBLE);
					bookBaseAdpter.clear();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							handler.obtainMessage(1).sendToTarget();
						}
					},3000);
				}
			}else if(result.exception!=null){
				Toast.makeText(MainActivity.this, result.exception.getMessage(), Toast.LENGTH_LONG).show();
			}
			loading.setVisibility(View.GONE);
		}
	}
}
