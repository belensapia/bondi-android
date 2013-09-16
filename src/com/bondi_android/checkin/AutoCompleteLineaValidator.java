package com.bondi_android.checkin;

import java.util.Arrays;

import com.bondi_android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class AutoCompleteLineaValidator implements
		AutoCompleteTextView.Validator {
	
	private String[] allBuses;
	private Context context;
	
	public AutoCompleteLineaValidator(String[] allBuses, Context context) {
		this.allBuses = allBuses;
		this.context = context;
	}

	@Override
	public boolean isValid(CharSequence text) {
		Arrays.sort(allBuses);
		if (Arrays.binarySearch(allBuses, text.toString()) >= 0) {
			return true;
		}

		return false;
	}

	@Override
	public CharSequence fixText(CharSequence invalidText) {
		//This is called when the text written is not in the list of buses.
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources()
				.getString(
						R.string.checkin_selectValidBus_title));
		builder.setMessage(context.getResources()
				.getString(
						R.string.checkin_selectValidBus_message));
		builder.setPositiveButton("OK", null);
		AlertDialog dialog = builder.show();

		// Must call show() prior to fetching text view
		TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
		return "";
	}
}