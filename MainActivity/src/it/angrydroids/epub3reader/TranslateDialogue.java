package it.angrydroids.epub3reader;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TranslateDialogue extends DialogFragment {

	WebView mWebView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		dismiss();
		View view = inflater.inflate(R.layout.translate, null);
		mWebView = (WebView) view.findViewById(R.id.translateWV);
		mWebView.loadUrl("https://translate.google.co.in/#auto/hi/"+BookView.selectedText);
		return view;

	}
}
