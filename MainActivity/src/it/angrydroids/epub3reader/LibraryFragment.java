package it.angrydroids.epub3reader;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LibraryFragment extends Fragment implements View.OnClickListener {
	Button goToLibrary;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		return inflater.inflate(R.layout.library_fragment_layout, container,
				false);

	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		goToLibrary = (Button) getActivity().findViewById(R.id.bLibrary);
		goToLibrary.setOnClickListener(this);
		
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.bLibrary:
			try {
				Class mainActivityClass = Class
						.forName("it.angrydroids.epub3reader.MainActivity");
				Intent ourIntent = new Intent(getActivity().getApplicationContext()
						,mainActivityClass);
				startActivity(ourIntent);
				

			} catch (ClassNotFoundException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			

		}
	}

}
