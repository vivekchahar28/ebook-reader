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

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnInitListener,
		OnUtteranceCompletedListener {
	String selectedText = "selected";
	protected EpubNavigator navigator;
	protected int bookSelector;
	protected int panelCount;
	protected String[] settings;
	private int brightness;
	private ContentResolver cResolver;
	private Window window;
	private static float speechRate = 0.75f;
	private ActionMode mActionMode = null;
	WebView mWebView;
	private TextSelectionSupport mTextSelectionSupport;
	public static boolean nightMode = false;
	public static boolean speak = false;
	public static TextToSpeech tts;
	static final int check = 1111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		navigator = new EpubNavigator(2, this);

		panelCount = 0;
		settings = new String[8];

		// LOADSTATE
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		loadState(preferences);
		navigator.loadViews(preferences);
		if (panelCount == 0) {
			bookSelector = 0;
			Intent goToChooser = new Intent(this, FileChooser.class);
			startActivityForResult(goToChooser, 0);
		}

		tts = new TextToSpeech(MainActivity.this, MainActivity.this);

	}

	protected void onResume() {
		super.onResume();
		if (panelCount == 0) {
			SharedPreferences preferences = getPreferences(MODE_PRIVATE);
			navigator.loadViews(preferences);
		}
	}

	@Override
	protected void onPause() {
		if (tts != null) {
			tts.stop();
		}
		super.onPause();
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		Editor editor = preferences.edit();
		saveState(editor);
		editor.commit();

	}

	// load the selected book
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (panelCount == 0) {
			SharedPreferences preferences = getPreferences(MODE_PRIVATE);
			navigator.loadViews(preferences);
		}

		if (resultCode == Activity.RESULT_OK) {
			String path = data.getStringExtra(getString(R.string.bpath));
			navigator.openBook(path, bookSelector);
		}
		if (requestCode == check && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			/*FragmentManager speechManager = getFragmentManager();
			SpeechOptionsDialogue speechDialogue = new SpeechOptionsDialogue(results);
			speechDialogue.show(speechManager, "speechDialogue");
			*/
		
		}

		//super.onActivityResult(requestCode, resultCode, data);
	}

	// ---- Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (navigator.isParallelTextOn() == false
				&& navigator.exactlyOneBookOpen() == false) {
			menu.findItem(R.id.meta1).setVisible(true);
			menu.findItem(R.id.meta2).setVisible(true);
			menu.findItem(R.id.toc1).setVisible(true);
			menu.findItem(R.id.toc2).setVisible(true);
			menu.findItem(R.id.FirstFront).setVisible(true);
			menu.findItem(R.id.SecondFront).setVisible(true);
			if (speak) {
				menu.findItem(R.id.StopReading).setVisible(true);
				//menu.findItem(R.id.speed).setVisible(true);
			} else {
				menu.findItem(R.id.StopReading).setVisible(false);
				//menu.findItem(R.id.speed).setVisible(false);
			}
		}

		if (navigator.exactlyOneBookOpen() == false) {
			menu.findItem(R.id.Synchronize).setVisible(true);
			menu.findItem(R.id.Align).setVisible(true);
			menu.findItem(R.id.SyncScroll).setVisible(true);
			menu.findItem(R.id.StyleBook1).setVisible(true);
			menu.findItem(R.id.StyleBook2).setVisible(true);
			menu.findItem(R.id.firstAudio).setVisible(true);
			menu.findItem(R.id.secondAudio).setVisible(true);
			if (speak) {
				menu.findItem(R.id.StopReading).setVisible(true);
				//menu.findItem(R.id.speed).setVisible(true);
			} else {
				menu.findItem(R.id.StopReading).setVisible(false);
				//menu.findItem(R.id.speed).setVisible(false);
			}
		}

		if (navigator.exactlyOneBookOpen() == true
				|| navigator.isParallelTextOn() == true) {
			menu.findItem(R.id.meta1).setVisible(false);
			menu.findItem(R.id.meta2).setVisible(false);
			menu.findItem(R.id.toc1).setVisible(false);
			menu.findItem(R.id.toc2).setVisible(false);
			menu.findItem(R.id.FirstFront).setVisible(false);
			menu.findItem(R.id.SecondFront).setVisible(false);
			if (speak) {
				menu.findItem(R.id.StopReading).setVisible(true);
				//menu.findItem(R.id.speed).setVisible(true);
			} else {
				menu.findItem(R.id.StopReading).setVisible(false);
				//menu.findItem(R.id.speed).setVisible(false);
			}
		}

		if (navigator.exactlyOneBookOpen() == true) {
			menu.findItem(R.id.Synchronize).setVisible(false);
			menu.findItem(R.id.Align).setVisible(false);
			menu.findItem(R.id.SyncScroll).setVisible(false);
			menu.findItem(R.id.StyleBook1).setVisible(false);
			menu.findItem(R.id.StyleBook2).setVisible(false);
			menu.findItem(R.id.firstAudio).setVisible(false);
			menu.findItem(R.id.secondAudio).setVisible(false);
			if (speak) {
				menu.findItem(R.id.StopReading).setVisible(true);
				//menu.findItem(R.id.speed).setVisible(true);
			} else {
				menu.findItem(R.id.StopReading).setVisible(false);
				//menu.findItem(R.id.speed).setVisible(false);
			}
		}

		// if there is only one view, option "changeSizes" is not visualized
		if (panelCount == 1)
			menu.findItem(R.id.changeSize).setVisible(false);
		else
			menu.findItem(R.id.changeSize).setVisible(true);

		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.FirstEPUB:
			bookSelector = 0;
			Intent goToChooser1 = new Intent(this, FileChooser.class);
			goToChooser1.putExtra(getString(R.string.second),
					getString(R.string.time));
			startActivityForResult(goToChooser1, 0);
			return true;

		case R.id.SecondEPUB:
			bookSelector = 1;
			Intent goToChooser2 = new Intent(this, FileChooser.class);
			goToChooser2.putExtra(getString(R.string.second),
					getString(R.string.time));
			startActivityForResult(goToChooser2, 0);
			return true;

		case R.id.Front:
			if (navigator.exactlyOneBookOpen() == true
					|| navigator.isParallelTextOn() == true)
				chooseLanguage(0);
			return true;

		case R.id.FirstFront:
			chooseLanguage(0);
			return true;

		case R.id.SecondFront:
			if (navigator.exactlyOneBookOpen() == false)
				chooseLanguage(1);
			else
				errorMessage(getString(R.string.error_onlyOneBookOpen));
			return true;

		case R.id.PconS:
			try {
				boolean yes = navigator.synchronizeView(1, 0);
				if (!yes) {
					errorMessage(getString(R.string.error_onlyOneBookOpen));
				}
			} catch (Exception e) {
				errorMessage(getString(R.string.error_cannotSynchronize));
			}
			return true;

		case R.id.SconP:
			try {
				boolean ok = navigator.synchronizeView(0, 1);
				if (!ok) {
					errorMessage(getString(R.string.error_onlyOneBookOpen));
				}
			} catch (Exception e) {
				errorMessage(getString(R.string.error_cannotSynchronize));
			}
			return true;

		case R.id.Synchronize:

			boolean sync = navigator.flipSynchronizedReadingActive();
			if (!sync) {
				errorMessage(getString(R.string.error_onlyOneBookOpen));
			}
			return true;

		case R.id.Metadata:
			if (navigator.exactlyOneBookOpen() == true
					|| navigator.isParallelTextOn() == true) {
				navigator.displayMetadata(0);
			} else {
			}
			return true;

		case R.id.meta1:
			if (!navigator.displayMetadata(0))
				errorMessage(getString(R.string.error_metadataNotFound));
			return true;

		case R.id.meta2:
			if (!navigator.displayMetadata(1))
				errorMessage(getString(R.string.error_metadataNotFound));
			return true;

		case R.id.tableOfContents:
			openTOC();
			return true;

		case R.id.toc1:
			if (!navigator.displayTOC(0))
				errorMessage(getString(R.string.error_tocNotFound));
			return true;
		case R.id.toc2:
			if (navigator.displayTOC(1))
				errorMessage(getString(R.string.error_tocNotFound));
			return true;
		case R.id.changeSize:
			try {
				DialogFragment newFragment = new SetPanelSize();
				newFragment.show(getFragmentManager(), "");
			} catch (Exception e) {
				errorMessage(getString(R.string.error_cannotChangeSizes));
			}
			return true;
		case R.id.Style: // work in progress...
			try {
				if (navigator.exactlyOneBookOpen() == true) {
					DialogFragment newFragment = new ChangeCSSMenu();
					newFragment.show(getFragmentManager(), "");
					bookSelector = 0;
				}
			} catch (Exception e) {
				errorMessage(getString(R.string.error_CannotChangeStyle));
			}
			return true;

		case R.id.StyleBook1:
			try {
				{
					DialogFragment newFragment = new ChangeCSSMenu();
					newFragment.show(getFragmentManager(), "");
					bookSelector = 0;
				}
			} catch (Exception e) {
				errorMessage(getString(R.string.error_CannotChangeStyle));
			}
			return true;

		case R.id.StyleBook2:
			try {
				{
					DialogFragment newFragment = new ChangeCSSMenu();
					newFragment.show(getFragmentManager(), "");
					bookSelector = 1;
				}
			} catch (Exception e) {
				errorMessage(getString(R.string.error_CannotChangeStyle));
			}
			return true;

			/*
			 * case R.id.SyncScroll: syncScrollActivated = !syncScrollActivated;
			 * return true;
			 */

		case R.id.audio:
			if (navigator.exactlyOneBookOpen() == true)
				if (!navigator.extractAudio(0))
					errorMessage(getString(R.string.no_audio));
			return true;
		case R.id.firstAudio:
			if (!navigator.extractAudio(0))
				errorMessage(getString(R.string.no_audio));
			return true;
		case R.id.secondAudio:
			if (!navigator.extractAudio(1))
				errorMessage(getString(R.string.no_audio));
			return true;
		case R.id.StopReading:
			tts.stop();
			return true;
		case R.id.brightness_dialog:
			showDialog();
			return true;
		case R.id.night_mode:
			toggleNightMode();
			return true;
		case R.id.incSpeed:
			speechRate += 0.05f;
			tts.stop();
			return true;
		case R.id.decSpeed:
			speechRate -= 0.05f;
			if (speechRate < 0)
				speechRate = 0f;
			tts.stop();
			return true;
		case R.id.find_in_page:
			FragmentManager fManager = getFragmentManager();
			FindInPageDialogue findInPageDialog = new FindInPageDialogue();
			findInPageDialog.show(fManager, "FindInPageDialogue");
			return true;
		case R.id.speech_recognize:
			FragmentManager speechManager = getFragmentManager();
			SpeechOptionsDialogue speechDialogue = new SpeechOptionsDialogue(navigator);
			speechDialogue.show(speechManager, "speechDialogue");
			
		
		}

		return super.onOptionsItemSelected(item);
	}

	public  void openTOC() {
		// TODO Auto-generated method stub
		if (navigator.exactlyOneBookOpen() == true
				|| navigator.isParallelTextOn() == true)
			navigator.displayTOC(0);
	}

	void toggleNightMode() {

		cResolver = getContentResolver();
		window = getWindow();

		try {
			brightness = android.provider.Settings.System.getInt(cResolver,
					android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("Error", "cannot access system brightness");
			e.printStackTrace();
		}
		if (!nightMode) {

			brightness = 20;

			setBackColor(getString(R.string.black_rgb));
			setColor(getString(R.string.white_rgb));
		} else {
			brightness = 100;
			setColor(getString(R.string.black_rgb));
			setBackColor(getString(R.string.white_rgb));
		}

		android.provider.Settings.System.putInt(cResolver,
				android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
		LayoutParams layoutpars = window.getAttributes();
		layoutpars.screenBrightness = brightness / (float) 255;
		window.setAttributes(layoutpars);
		bookSelector = 0;
		nightMode = !nightMode;
		setFontType("");
		setFontSize("");
		setLineHeight("");
		setAlign("");
		setMarginLeft("");
		setMarginRight("");
		setCSS();
		final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("spinColorValue", 4);
		editor.putInt("spinBackValue", 4);
		editor.commit();
	}

	// ----

	// ---- Panels Manager
	public void addPanel(SplitPanel p) {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.add(R.id.MainLayout, p, p.getTag());
		fragmentTransaction.commit();

		panelCount++;
	}

	public void attachPanel(SplitPanel p) {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.attach(p);
		fragmentTransaction.commit();

		panelCount++;
	}

	public void detachPanel(SplitPanel p) {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.detach(p);
		fragmentTransaction.commit();

		panelCount--;
	}

	public void removePanelWithoutClosing(SplitPanel p) {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.remove(p);
		fragmentTransaction.commit();

		panelCount--;
	}

	public void removePanel(SplitPanel p) {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.remove(p);
		fragmentTransaction.commit();

		panelCount--;
		if (panelCount <= 0)
			finish();
	}

	// ----

	// ---- Language Selection
	public void chooseLanguage(int book) {

		String[] languages;
		languages = navigator.getLanguagesBook(book);
		if (languages.length == 2)
			refreshLanguages(book, 0, 1);
		else if (languages.length > 0) {
			Bundle bundle = new Bundle();
			bundle.putInt(getString(R.string.tome), book);
			bundle.putStringArray(getString(R.string.lang), languages);

			LanguageChooser langChooser = new LanguageChooser();
			langChooser.setArguments(bundle);
			langChooser.show(getFragmentManager(), "");
		} else {
			errorMessage(getString(R.string.error_noOtherLanguages));
		}
	}

	public void refreshLanguages(int book, int first, int second) {
		navigator.parallelText(book, first, second);
	}

	// ----

	// ---- Change Style
	public void setCSS() {
		navigator.changeCSS(bookSelector, settings);
	}

	public void setBackColor(String my_backColor) {
		settings[1] = my_backColor;
	}

	public void setColor(String my_color) {
		settings[0] = my_color;
	}

	public void setFontType(String my_fontFamily) {
		settings[2] = my_fontFamily;
	}

	public void setFontSize(String my_fontSize) {
		settings[3] = my_fontSize;
	}

	public void setLineHeight(String my_lineHeight) {
		if (my_lineHeight != null)
			settings[4] = my_lineHeight;
	}

	public void setAlign(String my_Align) {
		settings[5] = my_Align;
	}

	public void setMarginLeft(String mLeft) {
		settings[6] = mLeft;
	}

	public void setMarginRight(String mRight) {
		settings[7] = mRight;
	}

	// ----

	// change the views size, changing the weight
	protected void changeViewsSize(float weight) {
		navigator.changeViewsSize(weight);
	}

	public int getHeight() {
		LinearLayout main = (LinearLayout) findViewById(R.id.MainLayout);
		return main.getMeasuredHeight();
	}

	public int getWidth() {
		LinearLayout main = (LinearLayout) findViewById(R.id.MainLayout);
		return main.getWidth();
	}

	// Save/Load State
	protected void saveState(Editor editor) {
		navigator.saveState(editor);
	}

	protected void loadState(SharedPreferences preferences) {
		if (!navigator.loadState(preferences))
			errorMessage(getString(R.string.error_cannotLoadState));
	}

	public void errorMessage(String message) {
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onUtteranceCompleted(String utteranceId) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				speak = false;
			}
		});

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		tts.setOnUtteranceCompletedListener(this);

	}

	public void callMe(String selectedText2) {
		if (tts.isSpeaking()) {
			tts.stop();
		}
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sampleText");
			// Toast.makeText(MainActivity.this,Float.toString(speechRate) ,
			// Toast.LENGTH_SHORT);
			tts.setSpeechRate(speechRate);
			MainActivity.speak = true;
			int i;
			if (selectedText2.length() > 4000) {
				int start = 0;
				int end = 4000 - 1;
				ArrayList<String> strings = new ArrayList<String>();
				for ( i = 0; end < selectedText2.length()-1; i++) {
					try{
						strings.add(selectedText2.substring(start, end));
					}catch(StringIndexOutOfBoundsException e){
						Log.e(Integer.toString(i), "sdsadasdadada");
						break;
					}
					start = end + 1;
					end = 4000 + start - 1;
					if (end >= selectedText2.length())
						end = selectedText2.length() - 1;
				}

				//Log.e( "ads",strings.get(1) );
				
				for ( i = 0; i < strings.size();) {
					if (!tts.isSpeaking()) {
						MainActivity.tts.speak(strings.get(i),
								TextToSpeech.QUEUE_FLUSH, params);
						i++;
					}
				}
			} else {
				MainActivity.tts.speak(selectedText2, TextToSpeech.QUEUE_FLUSH,
						params);
			}

	}

	public void showDialog() {

		FragmentManager manager = getFragmentManager();

		Brightness myDialog = new Brightness();

		myDialog.show(manager, "Brightness");
	}

	public static void callMeDict(String query) {
		// TODO Auto-generated method stub
		if (!tts.isSpeaking()) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sampleText");
			tts.setSpeechRate(0.50f);
			MainActivity.tts.speak(query, TextToSpeech.QUEUE_FLUSH, params);
			MainActivity.speak = true;
		}
	}

}
