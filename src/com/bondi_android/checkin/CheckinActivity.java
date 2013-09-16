package com.bondi_android.checkin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.bondi_android.R;
import com.bondi_android.tracking.TrackingLineActivity;

public class CheckinActivity extends Activity {
	public static final String SELECTED_LINE = "com.bondi.selectedLine";
	private String[] allBuses;
	private AutoCompleteTextView autoCompleteLineaView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkin);
		allBuses = getResources().getStringArray(R.array.all_lines);

		autoCompleteLineaView = (AutoCompleteTextView) findViewById(R.id.autoCompleteLinea);
		autoCompleteLineaView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, allBuses));
		autoCompleteLineaView.setValidator(new AutoCompleteLineaValidator(
				allBuses, this));
		autoCompleteLineaView
				.setOnFocusChangeListener(new AutoCompleteLineaFocusListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.checkin, menu);
		return true;
	}

	public void startTracking(View view) {
		if (autoCompleteLineaView.getText() != null
				&& !autoCompleteLineaView.getText().toString().isEmpty()) {
			Intent intent = new Intent(this, TrackingLineActivity.class);
			intent.putExtra(SELECTED_LINE, autoCompleteLineaView.getText()
					.toString());
			startActivity(intent);
		}
	}

}
