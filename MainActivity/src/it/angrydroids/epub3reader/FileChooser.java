package it.angrydroids.epub3reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FileChooser extends Activity {

	static List<File> epubs;
	static List<String> names;
	ArrayAdapter<String> adapter;
	static File selected;
	boolean firstTime;
	static String book_name;
	static int handle_close1=0;;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_chooser_layout);
		// finds all epub files by the method epubList() and returns it into
		// epubs variable

		if ((epubs == null) || (epubs.size() == 0)) {
			epubs = epubList(Environment.getExternalStorageDirectory());
		}

		ListView list = (ListView) findViewById(R.id.fileListView);
		names = fileNames(epubs);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, names);

		registerForContextMenu(findViewById(R.id.fileListView));

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View itemView,
					int position, long itemId) {
				
				handle_close1=1;
				selected = epubs.get(position);
				Intent resultIntent = new Intent();
				// TODO: hardcoded string
				resultIntent.putExtra("bpath", selected.getAbsolutePath());
				setResult(Activity.RESULT_OK, resultIntent);
				book_name = epubs.get(position).getName().replace(".epub", "");
				finish();
			}
		});

		list.setAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pdflist_long_press, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int listPosition = info.position;
		String fileName = (((ListView) (findViewById(R.id.fileListView)))
				.getItemAtPosition(listPosition)).toString();
		File fileSelected = epubs.get(listPosition);

		Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_LONG).show();
		switch (item.getItemId()) {

		case R.id.share_file:
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("application/epub");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"EBook  " + fileName);
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					"Please find the attachments");
			emailIntent.putExtra(
					Intent.EXTRA_STREAM,
					Uri.parse(fileSelected.getAbsolutePath()));
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));

			break;
		case R.id.delete_file:
			try {
				File sdcard = Environment.getExternalStorageDirectory();
				File file = new File(fileSelected.getAbsolutePath());
				file.delete();

			} catch (Exception e) {
					
			}
			epubs = epubList(Environment.getExternalStorageDirectory());
			names.clear();
			names.addAll(fileNames(epubs));
			this.adapter.notifyDataSetChanged();
			
			break;

		}
		return super.onContextItemSelected(item);
	}

	// TODO: hardcoded string
	private List<String> fileNames(List<File> files) {
		List<String> res = new ArrayList<String>();
		for (int i = 0; i < files.size(); i++) {
			res.add(files.get(i).getName().replace(".epub", ""));
			
		}
		return res;
	}

	// TODO: hardcoded string
	// TODO: check with mimetype, not with filename extension
	private List<File> epubList(File dir) {
		List<File> res = new ArrayList<File>();
		if (dir.isDirectory()) {
			File[] f = dir.listFiles();
			if (f != null) {
				for (int i = 0; i < f.length; i++) {
					if (f[i].isDirectory()) {
						res.addAll(epubList(f[i]));
					} else {
						String lowerCasedName = f[i].getName().toLowerCase();
						if (lowerCasedName.endsWith(".epub")) {
							res.add(f[i]);
						}

						/*
						 * NOTE: future if ((lowerCasedName.endsWith(".epub"))
						 * || (lowerCasedName.endsWith(".e0"))) { res.add(f[i]);
						 * }
						 */
					}
				}
			}
		}
		return res;
	}

	private void refreshList() {
		epubs = epubList(Environment.getExternalStorageDirectory());
		names.clear();
		names.addAll(fileNames(epubs));
		this.adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_chooser, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.update:
			refreshList();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
