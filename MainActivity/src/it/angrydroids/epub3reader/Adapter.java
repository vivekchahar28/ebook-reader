package it.angrydroids.epub3reader;


import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class Adapter extends BaseAdapter {
	private Context mContext;
	public List<String> filenames;

	// Gets the context so it can be used later
	public Adapter(Context c , List<String> items) {
		mContext = c;
		filenames = items;
	}

	// Total number of things contained within the adapter
	public int getCount() {
		return filenames.size();
	}

	// Require for structure, not really used in my code.
	public Object getItem(int position) {
		return filenames.get(position);
	}

	// Require for structure, not really used in my code. Can
	// be used to get the id of an item in the adapter for
	// manual control.
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position,View convertView, ViewGroup parent) {  
		Button btn;
		TextView tv;
		  if (convertView == null) {    
		   // if it's not recycled, initialize some attributes  
		   btn = new Button(mContext);
		   tv= new TextView(mContext);
		   btn.setLayoutParams(new GridView.LayoutParams(150, 155));
		   btn.setPadding(8, 8, 8, 8);
		   tv.setLayoutParams(new GridView.LayoutParams(20, 50));
		   tv.setPadding(8, 8, 8, 8);  
		   }   
		  else {  
		   btn = (Button) convertView;
		   tv = (TextView) convertView;
		  }    
		  btn.setText(filenames.get(position));
		  tv.setText(filenames.get(position));
		  tv.setId(position);
		  // filenames is an array of strings  
		  btn.setTextColor(Color.WHITE);  
		  //btn.setBackgroundResource(R.drawable.sample12);  
		  btn.setId(position);  
		  btn.setEnabled(false);
		  btn.setClickable(false);
		  btn.setFocusable(false);
		  
		  return btn;  
	 }
	
	
}

