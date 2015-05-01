package it.angrydroids.epub3reader;

import it.angrydroids.epub3reader.FirstActivity.PlaceholderFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

public class MyFragment extends ListFragment {

	ArrayList<String> names = new ArrayList<String>();
	ArrayList<String> datas = new ArrayList<String>();
	List<String> txtfiles;
	String temp_data, temp_book;
	List<File> txts;
	static File selected;
	String fname;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.my_fragment_layout, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if ((txts == null) || (txts.size() == 0)) {
			txts = txtList(new File(Environment.getExternalStorageDirectory()
					+ "/Notes/"));
		}

		txtfiles = fileNames(txts);

		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, txtfiles));

		registerForContextMenu(getListView());

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View itemView,
					int position, long itemId) {
				 fname= txtfiles.get(position);

				try {
					if (!fileExists(getActivity().getApplicationContext(),

					fname)) {
						OutputStreamWriter out = new OutputStreamWriter(
								getActivity()
										.openFileOutput(
												Environment
														.getExternalStorageDirectory()
														+ "/Notes/"
														+ fname
														+ ".txt", 0));
						String s = "";
						out.write(s);
					}

					File sdcard = Environment.getExternalStorageDirectory();
					File file = new File(sdcard, "/Notes/" + fname + ".txt");
					StringBuilder text = new StringBuilder();
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line;

					while ((line = br.readLine()) != null) {
						text.append(line);
						text.append('\n');
					}
					br.close();

					temp_book = txtfiles.get(position);
					temp_data = text.toString();

					showDialog();

				} catch (Exception e) {
				}
			}
		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.list_long_press, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int listPosition = info.position;
		String fileName = getListView().getItemAtPosition((listPosition))
				.toString();
		switch (item.getItemId()) {

		case R.id.share_text:
			Toast.makeText(getActivity().getApplicationContext(), fileName,
					Toast.LENGTH_LONG).show();
			try {
				File sdcard = Environment.getExternalStorageDirectory();
				File file = new File(sdcard, "/Notes/" + fileName + ".txt");
				StringBuilder text = new StringBuilder();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;

				while ((line = br.readLine()) != null) {
					text.append(line);
					text.append('\n');
				}
				br.close();

				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, "Notes\n\n"+text.toString());
				sendIntent.setType("text/plain");
				sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"attached notes for book " + fileName);

				startActivity(Intent
						.createChooser(sendIntent, "Send email..."));

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.share_attachment:

			Toast.makeText(getActivity().getApplicationContext(), fileName,
					Toast.LENGTH_LONG).show();
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/plain");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"notes for book " + fileName);
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					"Please find the attachments");
			emailIntent.putExtra(
					Intent.EXTRA_STREAM,
					Uri.parse(Environment.getExternalStorageDirectory()
							+ "/Notes/" + fileName + ".txt"));
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));

			break;
		case R.id.delete_file:

			try {
				File sdcard = Environment.getExternalStorageDirectory();
				File file = new File(sdcard, "/Notes/" + fileName + ".txt");
				file.delete();

			} catch (Exception e) {
			}

			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			MyFragment frag = new MyFragment();
			transaction.replace(R.id.container, frag, "VivzFragment");
			transaction.commit();

			break;

		}
		return super.onContextItemSelected(item);
	}

	public void showDialog() {

		FragmentManager manager = getFragmentManager();
		Dialog_notes myDialog = new Dialog_notes(temp_book, temp_data);
		myDialog.show(manager, "Dialog_notes");

	}

	public boolean fileExists(Context context, String filename) {
		File file = new File(Environment.getExternalStorageDirectory(),
				"/Notes/" + filename + ".txt");
		if (file == null || !file.exists()) {
			return false;
		}
		return true;
	}

	private List<File> txtList(File dir) {
		List<File> res = new ArrayList<File>();
		if (dir.isDirectory()) {
			File[] f = dir.listFiles();
			if (f != null) {
				for (int i = 0; i < f.length; i++) {
					if (f[i].isDirectory()) {
						res.addAll(txtList(f[i]));
					} else {
						String lowerCasedName = f[i].getName().toLowerCase();
						if (lowerCasedName.endsWith(".txt")
								&& lowerCasedName.contains("notes")) {
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

	private List<String> fileNames(List<File> files) {
		List<String> res = new ArrayList<String>();
		for (int i = 0; i < files.size(); i++) {
			res.add(files.get(i).getName().replace(".txt", ""));
			/*
			 * NOTE: future res.add(files.get(i).getName().replace(".epub",
			 * "").replace(".e0", ""));
			 */
		}
		return res;
	}
}