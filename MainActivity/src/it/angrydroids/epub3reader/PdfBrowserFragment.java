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

import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import pdf.render.view.MyPdfViewer;
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
import pdf.render.view.*;

public class PdfBrowserFragment extends ListFragment {

	ArrayList<String> names = new ArrayList<String>();
	ArrayList<String> datas = new ArrayList<String>();
	List<String> pdffiles;
	List<File> pdfs;
	static File selected;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.pdf_browser_layout, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if ((pdfs == null) || (pdfs.size() == 0)) {
			pdfs = pdfList(new File(Environment.getExternalStorageDirectory(),""));
		}

		pdffiles = fileNames(pdfs);

		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, pdffiles));

		registerForContextMenu(getListView());

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View itemView,
					int position, long itemId) {
				String fname = pdffiles.get(position);

				Intent intent = new Intent(getActivity()
						.getApplicationContext(), MyPdfViewer.class);
				intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME,
						Environment.getExternalStorageDirectory() + "/ebooks/"
								+ fname + ".pdf");
				startActivity(intent);

			}
		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.pdflist_long_press, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int listPosition = info.position;
		String fileName = getListView().getItemAtPosition((listPosition))
				.toString();
		switch (item.getItemId()) {

		case R.id.share_file:
			Toast.makeText(getActivity().getApplicationContext(), fileName,
					Toast.LENGTH_LONG).show();
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("application/pdf");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { "singhjaspreet1303@gmail.com" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"attached pdf book");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					"Pleas find the attachments");
			emailIntent.putExtra(
					Intent.EXTRA_STREAM,
					Uri.parse(Environment.getExternalStorageDirectory()
							+ "/ebook/" + fileName + ".pdf"));
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));

			break;
		case R.id.delete_file:

			try {
				File sdcard = Environment.getExternalStorageDirectory();
				File file = new File(sdcard, "/ebook/" + fileName + ".pdf");
				file.delete();

			} catch (Exception e) {
			}

			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			PdfBrowserFragment pfrag = new PdfBrowserFragment();
			transaction.replace(R.id.container, pfrag, "pdfBrowserFragment");
			transaction.commit();
			break;

		}
		return super.onContextItemSelected(item);
	}

	private List<File> pdfList(File dir) {
		List<File> res = new ArrayList<File>();
		if (dir.isDirectory()) {
			File[] f = dir.listFiles();
			if (f != null) {
				for (int i = 0; i < f.length; i++) {
					if (f[i].isDirectory()) {
						res.addAll(pdfList(f[i]));
					} else {
						String lowerCasedName = f[i].getName().toLowerCase();
						if (lowerCasedName.endsWith(".pdf")) {
							res.add(f[i]);
						}
					}
				}
			}
		}
		return res;
	}

	private List<String> fileNames(List<File> files) {
		List<String> res = new ArrayList<String>();
		for (int i = 0; i < files.size(); i++) {
			res.add(files.get(i).getName().replace(".pdf", ""));
		}
		return res;
	}

}
