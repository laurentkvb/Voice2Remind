package nl.lkvb.voice2remind.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import nl.lkvb.voice2remind.Voice2RemindApp;
import nl.lkvb.voice2remind.models.Reminder;

/**
 * PreferenceUtils. Making @{link SharedPreferences} easy.
 */
public class PreferenceUtils {

    private static final String KEY_SHARED_PREFERENCES = "sharedPreferences";
    private static final String KEY_FIRST_RUN = "KEY_FIRST_RUN";

    //KEYS for objects
    private static final String KEY_TUTORIAL_COMPLETED = "KEY_TUTORIAL_COMPLETED";
    private static final String KEY_REMINDERS_LIST = "KEY_REMINDERS_LIST";
    private static final String TAG = PreferenceUtils.class.getSimpleName();

    private static PreferenceUtils sInstance = new PreferenceUtils();

    private SharedPreferences mSharedPreferences;
    private Gson mGson;

    private PreferenceUtils() {
        mSharedPreferences = Voice2RemindApp.getContext().getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    /**
     * Get an instance of the PreferenceUtils.
     *
     * @return Instance
     */
    public static synchronized PreferenceUtils getInstance() {
        return sInstance;
    }


    public void resetApp() {
        mSharedPreferences.edit().putBoolean(KEY_TUTORIAL_COMPLETED, false).apply();
    }


    public boolean isFirstRun() {
        return mSharedPreferences.getBoolean(KEY_FIRST_RUN, true);
    }


    public void setDidTutorial() {
        mSharedPreferences.edit().putBoolean(KEY_TUTORIAL_COMPLETED, true).apply();
    }

    public boolean tutorialIsCompleted() {
        return mSharedPreferences.getBoolean(KEY_TUTORIAL_COMPLETED, false);
    }


    public List<Reminder> getReminders() {

        String json = mSharedPreferences.getString(KEY_REMINDERS_LIST, null);
        Type type = new TypeToken<ArrayList<Reminder>>() {
        }.getType();

        List<Reminder> reminderList = mGson.<ArrayList<Reminder>>fromJson(json, type);
        if(reminderList == null){
            reminderList = new ArrayList<>();
        } else if(reminderList.size() == 0){
            reminderList = new ArrayList<>();

        }
        return reminderList;
    }

    public void saveReminders(List<Reminder> reminderList) {
        ArrayList<Reminder> listToArrayList = (ArrayList<Reminder>) reminderList;
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        String json = mGson.toJson(listToArrayList);

        editor.putString(KEY_REMINDERS_LIST, json);
        editor.commit();
    }

    public void saveReminder(Reminder reminder) {
        List<Reminder> reminderList = getReminders() != null ? getReminders() : new ArrayList<Reminder>();

        reminderList.add(reminder);
        saveReminders(reminderList);

    }

    public void replaceReminder(Reminder reminder, int position) {
        List<Reminder> reminderList = getReminders() != null ? getReminders() : new ArrayList<Reminder>();

        reminderList.set(position, reminder);
        saveReminders(reminderList);

    }



    public void printAllReminders() {
        if (PreferenceUtils.getInstance().getReminders() != null) {
            for (int i = 0; i < PreferenceUtils.getInstance().getReminders().size(); i++) {
                Log.e(TAG, PreferenceUtils.getInstance().getReminders().get(i).toString(i));
            }
        } else {
            Log.e(TAG, "Reminders list is empty");
        }
    }
}



