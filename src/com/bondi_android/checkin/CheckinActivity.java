package com.bondi_android.checkin;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bondi_android.R;
import com.bondi_android.tracking.TrackingLineActivity;
import com.bondi_android.widget.InstantAutoComplete;

public class CheckinActivity extends Activity {
	public static final String SELECTED_LINE = "com.bondi.selectedLine";
	private String[] allBuses;
	private InstantAutoComplete autoCompleteLineaView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkin);
		allBuses = getResources().getStringArray(R.array.all_lines);

		autoCompleteLineaView = (InstantAutoComplete) findViewById(R.id.autoCompleteLinea);
		autoCompleteLineaView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, allBuses));

		// This validator and onFocusChange are not deleted just in case they
		// are needed later.
		autoCompleteLineaView
				.setValidator(new AutoCompleteLineaValidator(this));
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
			if (isValid(autoCompleteLineaView.getText().toString())) {
				Intent intent = new Intent(this, TrackingLineActivity.class);
				intent.putExtra(SELECTED_LINE, autoCompleteLineaView.getText()
						.toString());
				startActivity(intent);
			} else {
				fixText();
			}
		}
	}

	// package view
	boolean isValid(String text) {
		Arrays.sort(allBuses);
		if (Arrays.binarySearch(allBuses, text.toString()) >= 0) {
			return true;
		}
		return false;
	}

	// package view
	void fixText() {
		autoCompleteLineaView.setText("");
		// This is called when the text written is not in the list of buses.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getResources().getString(
				R.string.checkin_selectValidBus_title));
		builder.setMessage(this.getResources().getString(
				R.string.checkin_selectValidBus_message));
		builder.setPositiveButton("OK", null);
		AlertDialog dialog = builder.show();

		// Must call show() prior to fetching text view
		TextView messageView = (TextView) dialog
				.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
	}
}
