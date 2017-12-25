package nl.lkvb.voice2remind.services;

import android.content.Context;
import android.content.Intent;

/**
 * Created by LaurentKlVB on 8-5-2017.
 */

public class ReminderStarterReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, ReminderStarterService.class);
        context.startService(service);
    }
}