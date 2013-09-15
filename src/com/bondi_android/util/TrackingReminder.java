package com.bondi_android.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bondi_android.MainActivity;
import com.bondi_android.R;
import com.bondi_android.tracking.TrackingLineActivity;

public class TrackingReminder implements Runnable {

	private Context context;

	public TrackingReminder(Context context) {
		this.context = context;
	}

	public void run() {
		if (!TrackingLineActivity.isActivityVisible()) {
			Toast.makeText(context, "RUN", Toast.LENGTH_SHORT).show();
			// Prepare intent which is triggered if the
			// notification is selected
			final Intent intent = new Intent(this.context, MainActivity.class);
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			PendingIntent pIntent = PendingIntent.getActivity(this.context, 0,
					intent, 0);

			// Build notification
			Notification noti = new Notification.Builder(this.context)
					.setContentTitle(
							context.getResources()
									.getString(
											R.string.tracking_reminder_notification_title))
					.setContentText(
							context.getResources()
									.getString(
											R.string.tracking_reminder_notification_content))
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(pIntent).build();
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			
			// Hide the notification after its selected
			noti.flags |= Notification.FLAG_AUTO_CANCEL;

			notificationManager.notify(0, noti);
		}
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
