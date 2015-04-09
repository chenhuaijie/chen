package com.license.number;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.license.number.network.LocClient;
import com.utils.SharedPreferenceHelper;

public class SettingActivity extends SherlockFragmentActivity{

	EditText txtEditText ;
	SharedPreferenceHelper helper ;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.setting);
		helper =new SharedPreferenceHelper(this);
		txtEditText = (EditText)findViewById(R.id.editText1);
		findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String serverAddr = txtEditText.getText().toString();
				if(!TextUtils.isEmpty(serverAddr)){
					helper.putServerAddress(serverAddr);
					LocClient.init(SettingActivity.this);
					finish();
				}else{
					Toast.makeText(SettingActivity.this, "服务器地址不能为空！", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		String serverAddress  = helper.getServerAddress();
		
		if(!TextUtils.isEmpty(serverAddress)){
			txtEditText.setText(serverAddress);
		}
		
	}
}
