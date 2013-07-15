package com.bondi_android.checkin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bondi_android.R;
import com.bondi_android.tracking.TrackingLineActivity;

public class CheckinActivity extends Activity {
	public static final String SELECTED_LINE = "com.bondi.selectedLine";
	private Spinner spinnerLinea;
	private String selectedLine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkin);
		
		spinnerLinea = (Spinner)findViewById(R.id.spinnerLinea);
        
        final ArrayAdapter<CharSequence> adaptador = 
        	    ArrayAdapter.createFromResource(this,
        	        R.array.all_lines, android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         
        spinnerLinea.setAdapter(adaptador);

        spinnerLinea.setOnItemSelectedListener(
        	new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                    android.view.View v, int position, long id) {
                        selectedLine = adaptador.getItem(position).toString();
                }
         
                public void onNothingSelected(AdapterView<?> parent) {
                	selectedLine = null;
                }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.checkin, menu);
		return true;
	}
	
	public void startTracking(View view) {
		if (selectedLine==null) {
			//TODO show alert
		} else {
			Intent intent = new Intent(this, TrackingLineActivity.class);
			intent.putExtra(SELECTED_LINE, selectedLine);
	    	startActivity(intent);
		}
	}

}
