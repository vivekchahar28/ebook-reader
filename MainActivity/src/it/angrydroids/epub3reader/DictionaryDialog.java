package it.angrydroids.epub3reader;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DictionaryDialog extends DialogFragment {
	StringBuilder string;
	String query;
	TextView tv,tv_head;
	Button bPronounce;

	public DictionaryDialog(String query, StringBuilder string) {
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
		
		
		View view = inflater.inflate(R.layout.dictionary_dialog, null);
		tv = (TextView) view.findViewById(R.id.TEXT_STATUS_ID);
		tv_head=(TextView) view.findViewById(R.id.WORD);
		tv_head.setText(query);
		tv.setText(string);
		bPronounce = (Button) view.findViewById(R.id.bPronounce);

		bPronounce.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.callMeDict(query);
			}
		});

		return view;
	}
}