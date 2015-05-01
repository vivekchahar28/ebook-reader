package it.angrydroids.epub3reader;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DemoActivity extends Activity implements View.OnClickListener {
	Button chkCmd;
	Button forward;
	ToggleButton passTog;
	EditText input;
	TextView display;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);

		baconAndEggs();

		passTog.setOnClickListener(this);

		chkCmd.setOnClickListener(this);
		forward.setOnClickListener(this);

	}

	private void baconAndEggs() {
		// TODO Auto-generated method stub
		chkCmd = (Button) findViewById(R.id.bResults);
		forward = (Button) findViewById(R.id.bForward);
		passTog = (ToggleButton) findViewById(R.id.tbPassword);
		input = (EditText) findViewById(R.id.etCommands);
		display = (TextView) findViewById(R.id.tvResults);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.bResults:
			String check = input.getText().toString();
			display.setText(check);
			if (check.contentEquals("left")) {
				display.setGravity(Gravity.LEFT);
			} else if (check.contentEquals("center")) {
				display.setGravity(Gravity.CENTER);
			} else if (check.contentEquals("right")) {
				display.setGravity(Gravity.RIGHT);
			} else if (check.contentEquals("blue")) {
				display.setTextColor(Color.BLUE);
			} else if (check.contains("WTF")) {
				Random crazy = new Random();
				display.setText("WTF!!!");
				display.setTextSize(crazy.nextInt(75));
				display.setTextColor(Color.rgb(crazy.nextInt(265),
						crazy.nextInt(265), crazy.nextInt(265)));

			} else {
				display.setText("invalid");
				display.setGravity(Gravity.CENTER);
			}

			break;

		case R.id.tbPassword:

			if (passTog.isChecked() == true) {
				input.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			} else {
				input.setInputType(InputType.TYPE_CLASS_TEXT);
			}
			break;

		case R.id.bForward:
			Class mainActivityClass;
			display.setText("PPPPPPPPPPP");
			try {
				mainActivityClass = Class
						.forName("it.angrydroids.epub3reader.MainActivity");
				Intent ourIntent = new Intent(DemoActivity.this,
						mainActivityClass);
				startActivity(ourIntent);
				display.setText("XXXXXXXXXX");

			} catch (ClassNotFoundException e) {
				display.setText("TTTTTTTTT");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			display.setText("ZZZZZZZZZZ");

		}
	}
}
