package nl.lkvb.voice2remind.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.ui.SpeechProgressView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nl.lkvb.voice2remind.R;
import nl.lkvb.voice2remind.dialogs.ReminderConfirmationDialog;
import nl.lkvb.voice2remind.models.Reminder;
import nl.lkvb.voice2remind.utils.DateUtils;
import nl.lkvb.voice2remind.utils.PreferenceUtils;

/**
 * SpeechActivity that handles the speech to text conversion and the
 * creation of a reminder.
 */
public class SpeechActivity extends AppCompatActivity {

    private static final String TAG = SpeechActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO_CODE = 101;
    private static String[] MONTHS_WRITTEN = new String[12];

    private Speech mSpeech;

    private Holder mHolder;

    private Activity mActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO uncomment for langauge
//        initChangeLocale();

        initActivity();

        initViews();

        initPermission();

        initOnClickListeners();

//        initTest();

    }

    private void initTest() {
        List<Reminder> reminderList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Calendar calendar =Calendar.getInstance();
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + i);
            reminderList.add(new Reminder(("test " + String.valueOf(i)), calendar ));
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - i);

            reminderList.add(new Reminder(("test " + String.valueOf(i)), calendar ));
        }

        PreferenceUtils.getInstance().saveReminders(reminderList);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Speech.getInstance().unregisterDelegate();
    }

    private void initChangeLocale() {
//        Locale current = getResources().getConfiguration().locale;
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void initActivity() {
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        initMonthsArray();

    }

    /**
     * Based on the language of the user's phone, the months will be set.
     * Default language is English.
     */
    private void initMonthsArray() {
        MONTHS_WRITTEN = getResources().getStringArray(R.array.months_written_array);
    }

    private void initOnClickListeners() {
        mHolder.mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHolder.mControlsView.getVisibility() == View.GONE) {
                    Log.e(TAG, "Showing controlsview");

                    startVoiceSpeech();
                    toggleControlsView(false);

                } else {
                    Log.e(TAG, "Hiding controlsview");
                    toggleControlsView(true);
                    mSpeech.stopListening();
                }
            }
        });

        mHolder.mExplanationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initHelpView();
                //Testing purposes
//                initVariablesFromSpeechResult("Er is een trein ontspoord geraakt om 10 uur Er is een trein ontspoord geraakt om 10 uur Er is een trein ontspoord geraakt om 10 uur Er is een trein ontspoord geraakt om 10 uur");
//                Toast.makeText(mActivity, "Disable in initOnClickListeners() -> mHolder.mExplanationLayout.setOnClickListener", Toast.LENGTH_SHORT).show();
            }
        });


        mHolder.mSwitchViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SpeechActivity.this, ReminderListActivity.class));
                overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );

            }
        });

    }

    private void initHelpView() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SpeechActivity.this/*mActivity*/, R.style.DefaultDialogTheme); //TODO of mActivity gebruikem

        View view = getLayoutInflater().inflate(R.layout.help_popup, null);
        alertDialogBuilder.setView(view);

//        alertDialogBuilder.setMessage(getString(R.string.how_to_say_text));

        AlertDialog alertDialog = alertDialogBuilder.create();

        PreferenceUtils.getInstance().printAllReminders();

        alertDialog.show();
    }

    /**
     * Method to hide/show the controls view.
     *
     * @param hide true is controls view has to be hidden else show it.
     */
    private void toggleControlsView(boolean hide) {
        Log.e(TAG, "Wat is hide : " + String.valueOf(hide));
        if (hide) {
            if (mHolder.mControlsView.getVisibility() != View.GONE) {
                mHolder.mControlsView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
                mHolder.mControlsView.setVisibility(View.GONE);
            }
            mHolder.mRecordBtn.setBackground(getResources().getDrawable(R.drawable.blue_gradient_background));
//            mHolder.mRecordBtn.setBackground(getResources().getDrawable(R.drawable.record_btn_selector));


        } else {
            mHolder.mControlsView.setVisibility(View.VISIBLE);
            mHolder.mControlsView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
            mHolder.mRecordBtn.setBackground(getResources().getDrawable(R.drawable.red_gradient_background));
        }
    }

    private void initViews() {
        mHolder = new Holder();

        mHolder.mControlsView = findViewById(R.id.fullscreen_content_controls);
        mHolder.mRecordBtn = (ImageView) findViewById(R.id.record_btn);
        mHolder.mReminderText = (TextSwitcher) findViewById(R.id.fullscreen_content);
        mHolder.mDateText = (TextView) findViewById(R.id.editText1);
        mHolder.mExplanationLayout = (LinearLayout) findViewById(R.id.explanation_layout);
        mHolder.mSwitchViewBtn = (ImageView) findViewById(R.id.switch_view_btn);

        mHolder.mControlsView.setVisibility(View.GONE);

        initTextSwitcher(mHolder.mReminderText, getString(R.string.reminder_text));
    }

    private void initTextSwitcher(TextSwitcher view, final String hint) {
        view.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView myText = new TextView(SpeechActivity.this);
                myText.setText(hint);
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(30);
//                myText.setTextSize(50);
                myText.setMaxLines(5);
                myText.setEllipsize(TextUtils.TruncateAt.END);
                myText.setTypeface(null, Typeface.BOLD);
                myText.setTextColor(getResources().getColor(R.color.blue_text_color));
                return myText;
            }
        });
        // Declare the in and out animations and initialize them
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        // set the animation type of textSwitcher
        view.setInAnimation(in);
        view.setOutAnimation(out);
    }


    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_DENIED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_REQUEST_RECORD_AUDIO_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private void startVoiceSpeech() {
        mSpeech = Speech.getInstance();
//        mSpeech.setTransitionMinimumDelay(1000);
        mSpeech.setStopListeningAfterInactivity(2000);
        mSpeech.stopListening();
        try {
            mSpeech.startListening((SpeechProgressView) findViewById(R.id.progress), new SpeechDelegate() {
                @Override
                public void onStartOfSpeech() {
                    resetText();
                    Log.i(TAG, "speech recognition is now active");
                }

                @Override
                public void onSpeechRmsChanged(float value) {
//                    Log.d(TAG, "rms is now: " + value);
                }

                @Override
                public void onSpeechPartialResults(List<String> results) {
                    String wholeText = android.text.TextUtils.join(",", results);
                    results = Arrays.asList(wholeText.split(" "));
                    speechToTextCalculation(wholeText, results);
                }

                @Override
                public void onSpeechResult(String result) {
                    Log.i(TAG, "klaar : result: " + result);
                    showReminderDialog(result);
                }


            });
        } catch (SpeechRecognitionNotAvailable exc) {
            Log.e(TAG, "Speech recognition is not available on this device!");
            // You can prompt the user if he wants to install Google App to have
            // speech recognition, and then you can simply call:
            //
            SpeechUtil.redirectUserToGoogleAppOnPlayStore(this);
            //
            // to redirect the user to the Google App page on Play Store
        } catch (GoogleVoiceTypingDisabledException exc) {
            Log.e(TAG, "Google voice typing must be enabled!");
        }

//        mSpeech.setLocale(new Locale("en"));
//        Locale current = getResources().getConfiguration().locale;
    }

    private void speechToTextCalculation(String wholeText, List<String> results) {
        if (!wholeText.isEmpty()) {
            if (results.size() > 1) {

                String dateDay = results.get(0);
                String dateMonth = results.get(1);
                String date = dateDay + " " + dateMonth;

                mHolder.mDateText.setText(getString(R.string.today));
                mHolder.mReminderText.setText(wholeText);

                int dateDayToInt = Integer.valueOf(dateDay);
                Log.e(TAG, "Test nummer = " + dateDayToInt);

                if (results.size() > 2 && dateDayToInt != 0) {
                    String reminder = wholeText.substring(date.length());

                    mHolder.mReminderText.setText(reminder);
                    mHolder.mDateText.setText(date);

                } else {
                    mHolder.mReminderText.setText(wholeText);

                }

            }
        }
    }


    /**
     * Resets the "date" and "reminder" textviews.
     */
    public void resetText() {
        mHolder.mDateText.setText(getString(R.string.date_text));
        mHolder.mReminderText.setText(getString(R.string.reminder_text));
    }

    private void showReminderDialog(String result) {
        toggleControlsView(true);


        if (result != null) {
            if (!result.isEmpty()) {
                String date = result.split(" ")[0] + " " + result.split(" ")[1];
                mHolder.mReminderText.setText(result.substring(date.length()));
                initVariablesFromSpeechResult(result);
            } else {
                Snackbar.make(mHolder.mControlsView, getString(R.string.snackbar_no_words_detected_text), Snackbar.LENGTH_LONG).show();
            }
//            mHolder.mRecordBtn.state
//            mHolder.mRecordBtn.setBackgroundResource(getResources().getDrawable(R.drawable.blue_gradient_background));
//            mHolder.mRecordBtn.setBackground(getResources().getDrawable(R.drawable.record_btn_selector));
        }
        mHolder.mRecordBtn.setBackground(getResources().getDrawable(R.drawable.record_btn_selector));

        //This is done because background removes
        final ImageView mRecordBtn = mHolder.mRecordBtn;
        ((ViewGroup)findViewById(R.id.layout)).removeView(mHolder.mRecordBtn);
        ((ViewGroup)findViewById(R.id.layout)).addView(mRecordBtn);

//        mHolder.mRecordBtn.invalidate();

    }

    private void initVariablesFromSpeechResult(String result) {
        String[] resultArray = result.split(" ");
        String dayFromArray = resultArray[0];
        String monthFromArray = resultArray[1];

        int dayIndex;
        String reminderWritten;
        String dateWritten;
        Calendar calendar;

        try {
            dayIndex = Integer.valueOf(dayFromArray);
            int monthIndex = monthToInteger(monthFromArray);

            calendar = dateToCalendar(dayIndex, monthIndex);
            calendar.set(Calendar.SECOND, (calendar.get(Calendar.SECOND) + 10)); // 5 ff change

            dateWritten = resultArray[0] + " " + resultArray[1];
            reminderWritten = result.substring(1 + dateWritten.length());

            showConfirmationDialog(reminderWritten, dateWritten, calendar);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            calendar = Calendar.getInstance();
            dateWritten = DateUtils.getDayMonthFormatted(calendar);
            reminderWritten = result;
            showConfirmationDialog(reminderWritten, dateWritten, calendar);
        }


    }

    private void showConfirmationDialog(final String reminder, final String dateWritten, final Calendar calendar) {
        AlertDialog alertDialog = ReminderConfirmationDialog.getInstance(this, reminder, dateWritten, calendar);
        alertDialog.show();
    }

    /**
     * Return calendar object from "day" and "month" index
     * @param dayIndex
     * @param monthIndex
     * @return
     */
    private Calendar dateToCalendar(int dayIndex, int monthIndex) {
        if (monthIndex != -1 && dayIndex != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, dayIndex);
            calendar.set(Calendar.MONTH, monthIndex);
            calendar.set(Calendar.YEAR, 2017);

            if (calendar.before(Calendar.getInstance())) {
                calendar.set(Calendar.YEAR, 2018);
            }

            return calendar;

        }
        return null;
    }

    /**
     * Return month number from text.
     *
     * @param month
     * @return month number if month is found else -1 to indicate it
     * was not found.
     */
    private int monthToInteger(String month) {
        for (int i = 0; i < MONTHS_WRITTEN.length; i++) {
            if (MONTHS_WRITTEN[i].equals(month) || MONTHS_WRITTEN[i].toLowerCase().equals(month)) {
                return i;
            }
        }
        return -1;
    }


    private class Holder {
        private LinearLayout mExplanationLayout;

        private TextSwitcher mReminderText;
        private TextView mDateText;
        private View mControlsView;
        private ImageView mRecordBtn;
        private ImageView mSwitchViewBtn;
    }
}