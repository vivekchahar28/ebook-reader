package it.angrydroids.epub3reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.DialogFragment;
import android.content.Context;

public class Notes extends DialogFragment implements View.OnClickListener {

	EditText editor;
	Button bt_notes, add_notes,discard_notes;
	String fileName = FileChooser.book_name + "notes.txt";
	String combined,temp_discard;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.notes, null);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		try {
			File mediaStorageDir;

			// folder name
			mediaStorageDir = new File(
					Environment.getExternalStorageDirectory(), "/Notes/");

			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.d("App", "failed to create directory");
				}
			}
			
			if (!fileExists(getActivity().getApplicationContext(), fileName)) {
				// OutputStreamWriter out = new
				// OutputStreamWriter(getActivity().openFileOutput(Environment.getExternalStorageDirectory()+
				// "/Notes/"+ fileName, 0));

				FileOutputStream stream = new FileOutputStream(new File(
						Environment.getExternalStorageDirectory() + "/Notes/"
								+ fileName));
			}
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard, "/Notes/" + fileName);
			StringBuilder text = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
				temp_discard=text.toString();
			}
			br.close();

			String full_note = text.toString();
			editor = (EditText) view.findViewById(R.id.notes_edittxt);
			editor.setText(full_note);
		} catch (Exception e) {
			e.printStackTrace();
		}

		bt_notes = (Button) view.findViewById(R.id.notes_done);
		add_notes = (Button) view.findViewById(R.id.notes_add_to);
		discard_notes = (Button) view.findViewById(R.id.notes_discard);
		
		discard_notes.bringToFront();
		
		bt_notes.setOnClickListener(this);
		add_notes.setOnClickListener(this);
		discard_notes.setOnClickListener(this);
		setCancelable(false);
		return view;
	}

	public void writeFile(String fileName, EditText v) throws IOException {
		FileOutputStream stream = new FileOutputStream(new File(
				Environment.getExternalStorageDirectory() + "/Notes/"
						+ fileName));
		try {
			stream.write(v.getText().toString().getBytes());
		} catch (Exception e) {
		} finally {
			stream.close();
		}
	}

	@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.notes_add_to:
				combined=editor.getText().toString()+"\n"+BookView.selectedText;
				editor.setText(combined);
			break;
			case R.id.notes_discard:
				editor.setText(temp_discard);
				dismiss();
				break;
			case R.id.notes_done:
				dismiss();
				try {
					writeFile(fileName, editor);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
		}
	}

	public boolean fileExists(Context context, String filename) {
		File file = new File(Environment.getExternalStorageDirectory(),
				"/Notes/" + filename);
		if (file == null || !file.exists()) {
			return false;
		}
		return true;
	}
}
