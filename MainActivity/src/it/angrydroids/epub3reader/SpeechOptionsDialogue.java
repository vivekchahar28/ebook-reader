package it.angrydroids.epub3reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SpeechOptionsDialogue extends DialogFragment implements
		RecognitionListener {

	private TextView returnedText;
	public static boolean exit = false;
	private ToggleButton toggleButton;
	private ProgressBar progressBar;
	private SpeechRecognizer speech = null;
	private Intent recognizerIntent;
	ArrayList<String> matches;
	private String LOG_TAG = "VoiceRecognitionActivity";
	ListView listView;
	EpubNavigator navigator;
	FirstActivity act;

	public SpeechOptionsDialogue(EpubNavigator navigator) {
		this.navigator = navigator;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_speech, null);
		returnedText = (TextView) view.findViewById(R.id.textView1);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton1);
		listView = (ListView) view.findViewById(R.id.speech_list);
		progressBar.setVisibility(View.INVISIBLE);
		speech = SpeechRecognizer.createSpeechRecognizer(getActivity()
				.getApplicationContext());
		speech.setRecognitionListener(this);
		recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
				"en");
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				getActivity().getPackageName());
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					progressBar.setVisibility(View.VISIBLE);
					progressBar.setIndeterminate(true);
					speech.startListening(recognizerIntent);
				} else {
					progressBar.setIndeterminate(false);
					progressBar.setVisibility(View.INVISIBLE);
					speech.stopListening();
				}
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View itemView,
					int position, long itemId) {
				String matchSelected = matches.get(position);

				// Toast.makeText(getActivity().getApplicationContext(),matchSelected,
				// Toast.LENGTH_LONG).show();

			}
		});

		return view;

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (speech != null) {
			speech.destroy();
			Log.i(LOG_TAG, "destroy");
		}

	}

	@Override
	public void onBeginningOfSpeech() {
		Log.i(LOG_TAG, "onBeginningOfSpeech");
		progressBar.setIndeterminate(false);
		progressBar.setMax(10);
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		Log.i(LOG_TAG, "onBufferReceived: " + buffer);
	}

	@Override
	public void onEndOfSpeech() {
		Log.i(LOG_TAG, "onEndOfSpeech");
		progressBar.setIndeterminate(true);
		toggleButton.setChecked(false);
	}

	@Override
	public void onError(int errorCode) {
		String errorMessage = getErrorText(errorCode);
		Log.d(LOG_TAG, "FAILED " + errorMessage);
		returnedText.setText(errorMessage);
		toggleButton.setChecked(false);
	}

	@Override
	public void onEvent(int arg0, Bundle arg1) {
		Log.i(LOG_TAG, "onEvent");
	}

	@Override
	public void onPartialResults(Bundle arg0) {
		Log.i(LOG_TAG, "onPartialResults");
	}

	@Override
	public void onReadyForSpeech(Bundle arg0) {
		Log.i(LOG_TAG, "onReadyForSpeech");
	}

	@Override
	public void onResults(Bundle results) {
		Log.i(LOG_TAG, "onResults");
		matches = results
				.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		/*
		 * String text = ""; for (String result : matches) text += result +
		 * "\n";
		 * 
		 * returnedText.setText(text);
		 */

		for (int i = 0; i < matches.size(); i++) {
			String voiceInput = matches.get(i).toLowerCase();
			if (voiceInput.equals("table of contents")) {
				//Toast.makeText(getActivity().getApplicationContext(),
						//"match found", Toast.LENGTH_LONG).show();
				// BookView.loadPageOnSpeech(Environment.getExternalStorageDirectory()+"/epubtemp/0/Toc.html");
				if (navigator.exactlyOneBookOpen() == true
						|| navigator.isParallelTextOn() == true)
					navigator.displayTOC(0);
				dismiss();
				return;
			} else if (BookView.chapterPaths.get(voiceInput) != null) {
				//Toast.makeText(getActivity().getApplicationContext(),
					//	BookView.chapterPaths.get(voiceInput),
					//	Toast.LENGTH_SHORT).show();
				dismiss();
				BookView.loadPageOnSpeech(BookView.chapterPaths.get(voiceInput));
				return;
			} else if (voiceInput.equals("close book")) {
				navigator.closeView(0);
				return;
			} else if (voiceInput.equals("library")) {
				navigator.closeView(0);
				try {
					Class mainActivityClass = Class
							.forName("it.angrydroids.epub3reader.MainActivity");
					Intent ourIntent = new Intent(getActivity()
							.getApplicationContext(), mainActivityClass);
					startActivity(ourIntent);

				} catch (ClassNotFoundException e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			} else if (voiceInput.equals("exit")) {
				exit = true;
				dismiss();
				Intent intent = new Intent(getActivity()
						.getApplicationContext(), FirstActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("EXIT", true);
				startActivity(intent);
				return;
			} else if (voiceInput.contains("find")) {
				String toFind = null;
				int flag = 0;
				try {
					voiceInput = voiceInput+" ";
					toFind = voiceInput.substring(5, voiceInput.length()-1);
				} catch (Exception e) {
					flag = 1;
				}
				FragmentManager fManager = getFragmentManager();
				if (flag == 1)
					toFind = "Enter string to search!!!";
				FindInPageDialogue findInPageDialog = new FindInPageDialogue(
						toFind);
				findInPageDialog.show(fManager, "FindInPageDialogue");
				dismiss();
				return;
			}

		}

		listView.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, matches));
	}

	@Override
	public void onRmsChanged(float rmsdB) {
		Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
		progressBar.setProgress((int) rmsdB);
	}

	public static String getErrorText(int errorCode) {
		String message;
		switch (errorCode) {
		case SpeechRecognizer.ERROR_AUDIO:
			message = "Audio recording error";
			break;
		case SpeechRecognizer.ERROR_CLIENT:
			message = "Client side error";
			break;
		case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
			message = "Insufficient permissions";
			break;
		case SpeechRecognizer.ERROR_NETWORK:
			message = "Network error";
			break;
		case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
			message = "Network timeout";
			break;
		case SpeechRecognizer.ERROR_NO_MATCH:
			message = "No match";
			break;
		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
			message = "RecognitionService busy";
			break;
		case SpeechRecognizer.ERROR_SERVER:
			message = "error from server";
			break;
		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
			message = "No speech input";
			break;
		default:
			message = "Didn't understand, please try again.";
			break;
		}
		return message;
	}

}