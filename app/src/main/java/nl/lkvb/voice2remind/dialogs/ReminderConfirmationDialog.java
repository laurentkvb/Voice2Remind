package nl.lkvb.voice2remind.dialogs;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import nl.lkvb.voice2remind.R;
import nl.lkvb.voice2remind.models.Reminder;
import nl.lkvb.voice2remind.services.NotificationReceiver;
import nl.lkvb.voice2remind.utils.BundleKeys;
import nl.lkvb.voice2remind.utils.DateUtils;
import nl.lkvb.voice2remind.utils.PreferenceUtils;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by LaurentKlVB on 7-5-2017.
 */

public class ReminderConfirmationDialog {

    private static final String TAG = ReminderConfirmationDialog.class.getSimpleName();
    private static AlertDialog.Builder mDialogBuilderInstance;
    private static Activity mSpeechActivity;

    private static String mReminderText;
    private static String mDateText;

    private static Calendar mCalendar;
    private static AlertDialog mReminderConfirmDialog;

    //Views
    private static EditText reminderTextView;
    private static int mReminderPositionInStorage = -1;

    private static OnReminderSaved mOnReminderSaved;


    public static AlertDialog getInstance(Activity activity, String reminder, String dateText, Calendar calendar) {
        mDialogBuilderInstance = new AlertDialog.Builder(activity, R.style.DefaultDialogTheme);


        mReminderPositionInStorage = -1;

        mReminderText = reminder;
        mDateText = dateText;
        mCalendar = calendar;

        mSpeechActivity = activity;

        initReminderDialog();

        return mReminderConfirmDialog;
    }

    public static AlertDialog getInstance(Activity activity, Reminder reminder, int reminderPositionInStorage) {
        mDialogBuilderInstance = new AlertDialog.Builder(activity, R.style.DefaultDialogTheme);

        mReminderPositionInStorage = reminderPositionInStorage;

        mReminderText = reminder.getmReminderText();
        mDateText = DateUtils.getDateFormatted(reminder.getmDateToRemind(), true);
        mCalendar = reminder.getmDateToRemind();

        mSpeechActivity = activity;

        initReminderDialog();

        return mReminderConfirmDialog;
    }

    private static void initReminderDialog() {
        // Main view
        View timeDialog = mSpeechActivity.getLayoutInflater().inflate(R.layout.reminder_confirm_popup, null);

        TextView dateTextView = (TextView) timeDialog.findViewById(R.id.reminder_date_textview);
        TextView hourMinuteTextView = (TextView) timeDialog.findViewById(R.id.reminder_hourminute_textview);
        reminderTextView = (EditText) timeDialog.findViewById(R.id.reminder_text_view);

        View closeBtn = timeDialog.findViewById(R.id.popup_close_btn);

        String dateText = DateUtils.isToday(mCalendar) ? mSpeechActivity.getString(R.string.today) + ", " + mDateText : mDateText;

        dateTextView.setText(dateText);
        hourMinuteTextView.setText(mReminderPositionInStorage == -1 ? getCurrentHourMinute() : DateUtils.getHourMinuteFormatted(mCalendar));

        reminderTextView.setText(mReminderText);

        mDialogBuilderInstance.setView(timeDialog);

        initOnclickListenerReminderDialog(closeBtn, hourMinuteTextView, dateTextView, reminderTextView);
    }

    private static String getCurrentHourMinute() {
        return DateUtils.getHourMinuteFormatted(Calendar.getInstance());
    }


    private static void initOnclickListenerReminderDialog(View closeBtn, final TextView hourMinuteTextView, final TextView dateTextView, EditText reminderTextView) {

        if (mCalendar.equals(Calendar.getInstance())||mCalendar.after(Calendar.getInstance()) || mReminderPositionInStorage == -1) {

            mDialogBuilderInstance.setPositiveButton(mSpeechActivity.getString(R.string.btn_setreminder), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String reminderString = reminderViewText();
                    mCalendar.set(Calendar.SECOND, 0);
                    Reminder reminder = new Reminder(reminderString, mCalendar);

                    saveReminder(reminder);
                }
            });
        }



        mDialogBuilderInstance.setNegativeButton(mSpeechActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //date textview
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mSpeechActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);

                        changeCalendarTime(year, month, day);
                        dateTextView.setText(DateUtils.getDateFormatted(calendar, true));

                    }

                },
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            }
        });

        //hour minute textview
        hourMinuteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCalendar.set(Calendar.MINUTE, minute);

                        String hourString = minute < 10 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
                        String minuteString = minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute);

                        hourMinuteTextView.setText(hourString + ":" + minuteString);
                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
                timePickerDialog.setOnCancelListener(new TimePickerDialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
                timePickerDialog.show();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReminderConfirmDialog.dismiss();
            }
        });

        mReminderConfirmDialog = mDialogBuilderInstance.create();

    }

    private static void saveReminder(Reminder reminder) {
        if (mReminderPositionInStorage != -1) {
            PreferenceUtils.getInstance().replaceReminder(reminder, mReminderPositionInStorage);
            if (mOnReminderSaved != null) {
                mOnReminderSaved.OnReminderSaved(mReminderPositionInStorage, reminder);
            }
        } else {
            PreferenceUtils.getInstance().saveReminder(reminder);
        }
        startAlarm(reminder);
        showConfirmReminderDialog();
    }

    private static void showConfirmReminderDialog() {
        String date = DateUtils.getDateFormatted(mCalendar);

        String text = mSpeechActivity.getString(R.string.confirm_message_reminder, date);

        Toast.makeText(mSpeechActivity, text, Toast.LENGTH_SHORT).show();
    }


    private static void startAlarm(Reminder reminder) {
        Intent intent1 = new Intent(mSpeechActivity.getApplicationContext(), NotificationReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.KEY_NOTIFICATION_MESSAGE, reminder.getmReminderText());
        intent1.putExtras(bundle);

        Log.e(TAG, "Datum alarm :  " + DateUtils.getDateFormatted(mCalendar));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mSpeechActivity, 123, intent1, 0);

        ((AlarmManager) mSpeechActivity.getApplicationContext().getSystemService(ALARM_SERVICE)).cancel(pendingIntent);
        ((AlarmManager) mSpeechActivity.getApplicationContext().getSystemService(ALARM_SERVICE))
                .set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent);

    }


    /**
     * Changes the global @mCalendar variable.
     *
     * @param year
     * @param month
     * @param day
     */
    private static void changeCalendarTime(int year, int month, int day) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
    }

    private static String reminderViewText() {
        return reminderTextView.getText().toString();
    }

    public static void setmOnReminderSaved(OnReminderSaved mOhoinReminderSaved) {
        mOnReminderSaved = mOhoinReminderSaved;
    }

    public interface OnReminderSaved {
        void OnReminderSaved(int position, Reminder reminder);
    }

}
