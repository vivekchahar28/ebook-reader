package it.angrydroids.epub3reader;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class DictionaryDialogSearch extends DialogFragment {
	StringBuilder string;
	String query;
	TextView tv,tv_head;
	Button bSearchOnWeb;
	
	public DictionaryDialogSearch(String query, StringBuilder string) {
		// TODO Auto-generated constructor stub
		this.query = query;
		this.string = string;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//getDialog().setTitle(query);
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View view  = inflater.inflate(R.layout.dictionary_dialog_search, null);
		tv = (TextView) view.findViewById(R.id.TEXT_STATUS_ID);
		tv.setText(string);
		bSearchOnWeb = (Button) view.findViewById(R.id.bSearchOnWeb);
		
		bSearchOnWeb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri
						.parse("http://www.google.com/search?q="
								+ query);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				dismiss();
				startActivity(intent);
			}
		});
		return view;
		
	}
}
