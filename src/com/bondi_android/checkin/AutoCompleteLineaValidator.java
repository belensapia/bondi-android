package com.bondi_android.checkin;

import android.widget.AutoCompleteTextView;

public class AutoCompleteLineaValidator implements
		AutoCompleteTextView.Validator {
	
	private CheckinActivity activity;
	
	public AutoCompleteLineaValidator(CheckinActivity activity) {
		this.activity = activity;
	}

	@Override
	public boolean isValid(CharSequence text) {
		return activity.isValid(text.toString());
	}

	@Override
	public CharSequence fixText(CharSequence invalidText) {
		activity.fixText();
		return "";
	}
}