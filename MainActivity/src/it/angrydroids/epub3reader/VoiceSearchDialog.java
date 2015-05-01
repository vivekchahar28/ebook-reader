package it.angrydroids.epub3reader;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class VoiceSearchDialog extends DialogFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//getDialog().setTitle(query);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		
		View view = inflater.inflate(R.layout.dictionary_dialog, null);
		

		return view;
	}
}
