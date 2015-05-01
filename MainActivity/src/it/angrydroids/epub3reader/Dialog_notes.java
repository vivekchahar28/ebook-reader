package it.angrydroids.epub3reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class Dialog_notes extends DialogFragment implements
		View.OnClickListener {
	String showing_text;
	String book_title;
	EditText edtext;
	String tContents;
	Button bt_close;

	public Dialog_notes(String s1, String s) {
		book_title = s1;
		showing_text = s;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		
		View view = inflater.inflate(R.layout.dialog_notes, null);
		
		bt_close = (Button) view.findViewById(R.id.dialog_notes_close);
		bt_close.bringToFront();
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		edtext = (EditText) view.findViewById(R.id.dialog_notes_edtext);
		edtext.setText(showing_text);
		setCancelable(false);

		
		
		bt_close.setOnClickListener(this);
		return view;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dismiss();
		try {
			writeFile(book_title + ".txt", edtext);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeFile(String fileName, EditText v) throws IOException {
		FileOutputStream stream = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/Notes/"+fileName));
		try{
			stream.write(v.getText().toString().getBytes());
		}catch(Exception e)
		{}finally{
			stream.close();
		}
	}
	
}