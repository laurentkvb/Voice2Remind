package nl.lkvb.voice2remind.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

import nl.lkvb.voice2remind.utils.DateUtils;

/**
 * Created by LaurentKlVB on 7-5-2017.
 */

public class Reminder implements Serializable, Comparable<Reminder>  {

    private String mReminderText;
    private Calendar mDateToRemind;

    public Reminder(String reminderText, Calendar dateToRemind) {
        this.mReminderText = reminderText;
        this.mDateToRemind = dateToRemind;
    }

    /**
     * ate formatted as day, month, year
     *
     * @param withYear
     * @return
     */
    private String getDateToRemindFormatted(boolean withYear) {
        return DateUtils.getDateFormatted(mDateToRemind, withYear);
    }

    /**
     * Date formatted as day, month, year, hh:mm
     *
     * @return date formatted as day, month, year, hh:mm.
     */
    private String getDateToRemindFormatted() {
        return DateUtils.getDateFormatted(mDateToRemind);
    }

    public String getmReminderText() {
        return mReminderText;
    }

    public void setmReminderText(String mReminderText) {
        this.mReminderText = mReminderText;
    }

    public Calendar getmDateToRemind() {
        return mDateToRemind;
    }

    public void setmDateToRemind(Calendar mDateToRemind) {
        this.mDateToRemind = mDateToRemind;
    }

    public String toString(int iterator) {
        return String.valueOf(iterator + 1) + ". Reminder text: " + mReminderText + " - Date: " + getDateToRemindFormatted();
    }


    @Override
    public int compareTo(@NonNull Reminder o) {
        return o.getmDateToRemind().compareTo(getmDateToRemind());
    }
}
