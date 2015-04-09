package com.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;

import com.license.number.db.AppPOJOTable;

public class AlertDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("确定删除！")
			.setTitle("提示");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
					Bundle bundle = getArguments() ;
					Long _ID= bundle.getLong(BaseColumns._ID);
					 Uri noteUri = ContentUris.withAppendedId(AppPOJOTable.CONTENT_ID_URI_BASE, _ID);
					getActivity().getContentResolver().delete(noteUri, null, null);
	           }
	    });
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               AlertDialogFragment.this.getDialog().cancel();
	           }
	    });		
		
		AlertDialog dialog = builder.create();		
		
		return dialog;
		
	}
	
}
