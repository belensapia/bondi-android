package com.bondi_android.checkin;

import android.view.View;
import android.widget.AutoCompleteTextView;

import com.bondi_android.R;

public class AutoCompleteLineaFocusListener implements
		View.OnFocusChangeListener {

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Checkear cómo funciona en un equipo real.
		if (v.getId() == R.id.checkinOkButton && !hasFocus) {
			((AutoCompleteTextView) v).performValidation();
		}
	}
}
