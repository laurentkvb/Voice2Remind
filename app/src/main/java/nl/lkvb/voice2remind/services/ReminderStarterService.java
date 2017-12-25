package nl.lkvb.voice2remind.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import nl.lkvb.voice2remind.Voice2RemindApp;
import nl.lkvb.voice2remind.models.Reminder;
import nl.lkvb.voice2remind.utils.BundleKeys;
import nl.lkvb.voice2remind.utils.PreferenceUtils;

/**
 * Created by LaurentKlVB on 8-5-2017.
 */

public class ReminderStarterService extends IntentService {


    private static final String TAG = ReminderStarterService.class.getSimpleName();

    public ReminderStarterService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        List<Reminder> remindersList = PreferenceUtils.getInstance().getReminders();
        if (remindersList != null) {
            setAlarms(remindersList);
            Log.i(TAG, "Voice2Remind test - success reminders loaded");
        } else {
            Log.e(TAG, "Could not retrieve \remindersList\" of the \"PreferenceUtils\" object.");
            Log.e(TAG, "Are you sure the list is filled with reminders? \"PreferenceUtils.getInstance().getReminders()");
        }
        Log.e(TAG, "Voice2Remind test - onHandleIntent() method");
    }

    private void setAlarms(List<Reminder> remindersList) {
        for(Reminder reminder : remindersList){
                if(reminder.getmDateToRemind().after(Calendar.getInstance())){
                    startReminder(reminder);
                }
        }
    }

    private void startReminder(Reminder reminder) {
        Intent intent1 = new Intent(Voice2RemindApp.getContext(), NotificationReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.KEY_NOTIFICATION_MESSAGE, reminder.getmReminderText());
        intent1.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(Voice2RemindApp.getContext(), 123, intent1, 0);

        ((AlarmManager) Voice2RemindApp.getContext().getSystemService(ALARM_SERVICE)).cancel(pendingIntent);
        ((AlarmManager) Voice2RemindApp.getContext().getSystemService(ALARM_SERVICE))
                .set(AlarmManager.RTC_WAKEUP, reminder.getmDateToRemind().getTimeInMillis(), pendingIntent);

    }
}
