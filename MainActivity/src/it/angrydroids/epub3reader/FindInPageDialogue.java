package it.angrydroids.epub3reader;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FindInPageDialogue extends DialogFragment {

	EditText editText;
	Button findButton;
	String toFind = null;
	
	public FindInPageDialogue() {
		// TODO Auto-generated constructor stub
	}
	
	public FindInPageDialogue(String toFind){
		this.toFind = toFind;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.find_in_page, null);
		editText = (EditText) view.findViewById(R.id.query);
		findButton = (Button) view.findViewById(R.id.find);
		
		if(toFind != null){
			editText.setText(toFind);
		}
		findButton.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				BookView.findInPageFn( editText.getText().toString());
			}
		});
		
		return view;

	}
}
