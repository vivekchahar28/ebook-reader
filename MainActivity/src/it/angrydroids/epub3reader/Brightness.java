package it.angrydroids.epub3reader;

import android.app.DialogFragment;
import android.content.ContentResolver;
import android.media.audiofx.BassBoost.Settings;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView.FindListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Brightness extends DialogFragment {

	private SeekBar brightbar;
	private int brightness;
	private ContentResolver cResolver;
	private Window window;
	TextView txtPerc;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.brightness, null);
		getDialog().setTitle("Slide To Change Brightness");
		brightbar = (SeekBar) view.findViewById(R.id.brightbar);
		txtPerc = (TextView) view.findViewById(R.id.txtPercentage);
		cResolver = getActivity().getContentResolver();
		window = getActivity().getWindow();
		brightbar.setMax(255);
		brightbar.setKeyProgressIncrement(1);

		try {
			brightness = android.provider.Settings.System.getInt(cResolver,
					android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("Error", "cannot access system brightness");
			e.printStackTrace();
		}
		brightbar.setProgress(brightness);

		brightbar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

						android.provider.Settings.System
								.putInt(cResolver,
										android.provider.Settings.System.SCREEN_BRIGHTNESS,
										brightness);

						LayoutParams layoutpars = window.getAttributes();
						layoutpars.screenBrightness = brightness / (float) 255;
						window.setAttributes(layoutpars);

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// TODO Auto-generated method stub

						if (progress <= 20) {
							brightness=20;
						}
						else brightness=progress;

					float perc = (brightness/(float)255)*100;
					
					txtPerc.setText((int)perc+"%");
					}	
				});

		return view;
	}
}