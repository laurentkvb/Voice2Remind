package nl.lkvb.voice2remind;

import android.app.Application;
import android.content.Context;

import net.gotev.speech.Speech;

/**
 * Created by LaurentKlVB on 5-5-2017.
 */
public class Voice2RemindApp extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

        Speech.init(this);
    }

    public static Context getContext() {
        return sContext;
    }

}
