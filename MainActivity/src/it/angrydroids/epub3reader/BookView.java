/*
The MIT License (MIT)

Copyright (c) 2013, V. Giacometti, M. Giuriato, B. Petrantuono

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package it.angrydroids.epub3reader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import it.angrydroids.epub3reader.QuickAction.OnDismissListener;

// Panel specialized in visualizing EPUB pages
public class BookView extends SplitPanel implements OnDismissListener {
	public static ViewStateEnum state = ViewStateEnum.books;
	protected String viewedPage;
	protected WebView view;
	protected float swipeOriginX, swipeOriginY;
	private TextSelectionSupport mTextSelectionSupport;
	public static String selectedText;
	protected QuickAction mContextMenu;
	static WebView mWebView ;
	protected boolean mContextMenuVisible = false;
	int[] ids;
	public static Map<String,String> chapterPaths;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater
				.inflate(R.layout.activity_book_view, container, false);

		// for dictionary
		ids = new int[26];
		for (int i = 0; i < 26; i++) {

			ids[i] = this.getResources().getIdentifier(
					Character.toString((char) (i + 97)), "raw",
					getActivity().getPackageName());
		}// for dictionary

		chapterPaths = new HashMap<String, String>();
		
		readyMap();
		
		return v;
	}

	private void readyMap() {
		// TODO Auto-generated method stub
		File sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard, "/epubtemp/" + "toc.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line;
		int count = 0;
		try {
			while ((line = br.readLine()) != null) {
				String key=line.split("~")[0];
		    	String val=line.replaceFirst(key+"~", "");
		    	Log.e(key.toLowerCase(), val);
		    	chapterPaths.put(key.toLowerCase(), val);
		    	count++;
			}
			
			chapterPaths.put("max_chapters", Integer.toString(count));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityCreated(Bundle saved) {
		super.onActivityCreated(saved);
		view = (WebView) getView().findViewById(R.id.Viewport);
		mWebView = view;
		// enable JavaScript for cool things to happen!
		view.getSettings().setJavaScriptEnabled(true);

		// ----- SWIPE PAGE
		/*view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (state == ViewStateEnum.books)
					swipePage(v, event, 0);
				WebView view = (WebView) v;
				return view.onTouchEvent(event);
			}
		});*/

		// ----- NOTE & LINK
		/*
		 * view.setOnLongClickListener(new OnLongClickListener() {
		 * 
		 * @Override public boolean onLongClick(View v) {
		 * 
		 * Message msg = new Message(); msg.setTarget(new Handler() {
		 * 
		 * @Override public void handleMessage(Message msg) {
		 * super.handleMessage(msg); String url = msg.getData().getString(
		 * getString(R.string.url)); if (url != null) navigator.setNote(url,
		 * index); } }); view.requestFocusNodeHref(msg);
		 * 
		 * return false; } });
		 */

		mTextSelectionSupport = TextSelectionSupport.support(getActivity(),
				view);

		mTextSelectionSupport
				.setSelectionListener(new TextSelectionSupport.SelectionListener() {
					@Override
					public void startSelection() {
					}

					@Override
					public void selectionChanged(String text) {
						selectedText = text;
						Rect handleRect = new Rect();
						handleRect.left = 300;
						handleRect.top = 30;
						handleRect.right = 500;
						handleRect.bottom = 200;
						showContextMenu(handleRect);
						// Toast.makeText(getActivity().getApplicationContext(),
						// text, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void endSelection() {

					}
				});

		view.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				try {
					navigator.setBookPage(url, index);
				} catch (Exception e) {
					errorMessage(getString(R.string.error_LoadPage));
				}
				return true;
			}
			public void onScaleChanged(WebView view, float oldScale,
					float newScale) {
				mTextSelectionSupport.onScaleChanged(oldScale, newScale);
			}
		});
		
		/*view.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				try {
					navigator.setBookPage(url, index);
				} catch (Exception e) {
					errorMessage(getString(R.string.error_LoadPage));
				}
				return true;
			}
		});*/

		// view.loadUrl("file:///android_asset/content.html");
		loadPage(viewedPage);

	}

	public void loadPage(String path) {

		viewedPage = path;
		if (created) {
			view.loadUrl(path);

		}
	}

	// rectangular box
	protected void showContextMenu(Rect displayRect) {

		// Don't show this twice
		if (mContextMenuVisible) {
			return;
		}

		// Don't use empty rect
		// if(displayRect.isEmpty()){
		if (displayRect.right <= displayRect.left) {
			return;
		}

		// Copy action item
		ActionItem buttonCopy = new ActionItem();

		buttonCopy.setTitle("Copy");
		buttonCopy.setActionId(1);
		buttonCopy.setIcon(getResources().getDrawable(R.drawable.icon_copy));

		// Highlight action item
		ActionItem buttonSearchWab = new ActionItem();

		buttonSearchWab.setTitle("  Search Web");
		buttonSearchWab.setActionId(2);
		buttonSearchWab.setIcon(getResources().getDrawable(
				R.drawable.icon_search));

		ActionItem buttonDictionary = new ActionItem();

		buttonDictionary.setTitle("Dictionary Meaning");
		buttonDictionary.setActionId(3);
		buttonDictionary.setIcon(getResources().getDrawable(
				R.drawable.icon_dictionary));

		ActionItem buttonRead = new ActionItem();

		buttonRead.setTitle(" Read loud");
		buttonRead.setActionId(4);
		buttonRead.setIcon(getResources().getDrawable(R.drawable.icon_read));

		ActionItem buttonNotes = new ActionItem();

		buttonNotes.setTitle("   Add to notes");
		buttonNotes.setActionId(5);
		buttonNotes.setIcon(getResources().getDrawable(R.drawable.icon_notes));

		ActionItem buttonShare = new ActionItem();

		buttonShare.setTitle("   Share");
		buttonShare.setActionId(6);
		buttonShare.setIcon(getResources().getDrawable(R.drawable.icon_share));

		ActionItem buttonTranslate = new ActionItem();

		buttonTranslate.setTitle("   Translate");
		buttonTranslate.setActionId(7);
		buttonTranslate.setIcon(getResources().getDrawable(R.drawable.google_translate));

		
		// The action menu
		mContextMenu = new QuickAction(getActivity().getApplicationContext());
		mContextMenu.setOnDismissListener(this);

		// Add buttons
		mContextMenu.addActionItem(buttonCopy);

		mContextMenu.addActionItem(buttonSearchWab);

		mContextMenu.addActionItem(buttonDictionary);

		mContextMenu.addActionItem(buttonRead);

		mContextMenu.addActionItem(buttonNotes);

		mContextMenu.addActionItem(buttonShare);
		mContextMenu.addActionItem(buttonTranslate);

		// setup the action item click listener
		mContextMenu
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

					@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						// TODO Auto-generated method stub
						switch (actionId) {
						case 1:
							// Do Button 1 stuff
							ClipboardManager clipboard = (ClipboardManager) getActivity()
									.getSystemService(Context.CLIPBOARD_SERVICE);
							ClipData clip = ClipData.newPlainText(
									"simple text", selectedText);
							clipboard.setPrimaryClip(clip);
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Copied to clipboard", Toast.LENGTH_SHORT)
									.show();
							//view.findAllAsync(selectedText);
							
							break;
						case 2:
							Uri uri = Uri
									.parse("http://www.google.com/search?q="
											+ selectedText);
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);
							break;

						case 3:
							showDialog(selectedText.trim());
							break;

						case 4:
							if (selectedText != null) {
								new MainActivity().callMe(selectedText);
							}
							break;
						case 5:
							showDialog();
							break;
						case 6:
							Intent sharingIntent = new Intent(
									android.content.Intent.ACTION_SEND);
							sharingIntent.setType("text/plain");
							sharingIntent.putExtra(
									android.content.Intent.EXTRA_SUBJECT,
									"Subject Here");
							sharingIntent.putExtra(
									android.content.Intent.EXTRA_TEXT,
									selectedText);
							startActivity(Intent.createChooser(sharingIntent,
									"Share via"));
							break;
						case 7:
								FragmentManager tManager = getFragmentManager();
								TranslateDialogue translate = new TranslateDialogue();
								translate.show(tManager, "translate");
								
						}

						mContextMenuVisible = false;
						view.loadUrl("javascript: android.selection.clearSelection();");

					}

				});

		mContextMenuVisible = true;
		mContextMenu.show(view, displayRect);
	}

	public void onDismiss() {
		// clearSelection();
		mContextMenuVisible = false;
	}

	// Change page
	void swipePage(View v, MotionEvent event, int book) {
		int action = MotionEventCompat.getActionMasked(event);

		switch (action) {
		case (MotionEvent.ACTION_DOWN):
			swipeOriginX = event.getX();
			swipeOriginY = event.getY();
			break;

		case (MotionEvent.ACTION_UP):
			int quarterWidth = (int) (screenWidth * 0.25);
			float diffX = swipeOriginX - event.getX();
			float diffY = swipeOriginY - event.getY();
			float absDiffX = Math.abs(diffX);
			float absDiffY = Math.abs(diffY);

			if ((diffX > quarterWidth) && (absDiffX > absDiffY)) {
				try {
					navigator.goToNextChapter(index);
				} catch (Exception e) {
					errorMessage(getString(R.string.error_cannotTurnPage));
				}
			} else if ((diffX < -quarterWidth) && (absDiffX > absDiffY)) {
				try {
					navigator.goToPrevChapter(index);
				} catch (Exception e) {
					errorMessage(getString(R.string.error_cannotTurnPage));
				}
			}
			break;
		}

	}

	@Override
	public void saveState(Editor editor) {
		super.saveState(editor);
		editor.putString("state" + index, state.name());
		editor.putString("page" + index, viewedPage);
	}

	@Override
	public void loadState(SharedPreferences preferences) {
		super.loadState(preferences);
		loadPage(preferences.getString("page" + index, ""));
		state = ViewStateEnum.valueOf(preferences.getString("state" + index,
				ViewStateEnum.books.name()));
	}

	public void showDialog(String query) {
		
		FragmentManager manager = getFragmentManager();
		
		if (query.length() == 0) {
			return;
		}
		StringBuilder string = new StringBuilder();
		if (query.contains(" ")) {
			//string = new StringBuilder("Too many words!!!!! Search on web!!!");
			string.append("Too many words!!!!!\nTo search on web press the button below!!!");
			DictionaryDialogSearch myDialog = new DictionaryDialogSearch(query, string);
			myDialog.show(manager, "DictionaryDialogSearch");
			return;
		} else {
			search(query.toLowerCase());
		}
		
	}

	private void search(String query) {
		FragmentManager manager = getFragmentManager();
		String tContents = "";
		StringBuilder string = new StringBuilder();
		int id;
		try{
			int idx = (int) query.charAt(0) - 97;
			 id = ids[idx];
		}catch(Exception e){
			string.append("\nNo such word found\n"+"To search on web press the button below!!!\n");
			DictionaryDialogSearch myDialog = new DictionaryDialogSearch(query, string);
			myDialog.show(manager, "DictionaryDialogSearch");
			return;
		}
		InputStream XmlFileInputStream = getResources().openRawResource(id);
		tContents = readTextFile(XmlFileInputStream);

		String[] pairs = tContents.split("\n");

		int flag = 0;
		for (String str : pairs) {
			String key = str.split(" ")[0];
			String val = str.replaceFirst(key + " ", "");

			if (key.toLowerCase().equals(query.toLowerCase())) {
				/*
				 * if (flag == 0) tv2.append(query + "\n" + val + "\n\n"); else
				 * tv2.append(val + "\n\n");
				 */
				string.append(val + "\n\n");
				flag = 1;
			}

		}
		if (flag == 0) {
			string.append("\nNo such word found\n"+"To search on web press the button below!!!");
			// tv2.append(query + "\n" + "\nNo such word found" + "\n\n");
			DictionaryDialogSearch myDialog = new DictionaryDialogSearch(query, string);
			myDialog.show(manager, "DictionaryDialogSearch");
			return;
		}
		DictionaryDialog myDialog = new DictionaryDialog(query, string);
		myDialog.show(manager, "DictionaryDialogSearch");
		return;
	}

	public String readTextFile(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {

		}
		return outputStream.toString();
	}

	public void showDialog() {

		FragmentManager manager = getFragmentManager();
		Notes myDialog = new Notes();
		myDialog.show(manager, "Notes");
	}
	
	@SuppressLint("NewApi")
	public static void findInPageFn(String query) {
		// TODO Auto-generated method stub
		//Toast.makeText(getActivity().getApplicationContext(), "in find", Toast.LENGTH_LONG).show();
		mWebView.findAllAsync(query);
	}
	
	
	public static void loadPageOnSpeech(String query) {
		// TODO Auto-generated method stub
		mWebView.loadUrl(query);
	}
}
