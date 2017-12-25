package nl.lkvb.voice2remind.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import nl.lkvb.voice2remind.R;
import nl.lkvb.voice2remind.services.ReminderStarterService;


/**
 * Class that shows the app logo for an amount of miliseconds and starts the speechActivity
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initReminders();

        initNavigation();
    }

    private void initReminders() {
        startService(new Intent(this, ReminderStarterService.class));
    }


    private void initNavigation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, SpeechActivity.class);

                startActivity(i);
                overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

}
