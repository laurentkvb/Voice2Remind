package nl.lkvb.voice2remind.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import nl.lkvb.voice2remind.R;
import nl.lkvb.voice2remind.Voice2RemindApp;
import nl.lkvb.voice2remind.activities.SpeechActivity;
import nl.lkvb.voice2remind.utils.BundleKeys;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by LaurentKlVB on 5-5-2017.
 */

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context pContext, Intent pIntent) {
        String message = pIntent.getExtras().getString(BundleKeys.KEY_NOTIFICATION_MESSAGE);
        sendNotification(message);
    }

    private void sendNotification(String message) {
        NotificationManager nm = (NotificationManager) Voice2RemindApp.getContext().getSystemService(NOTIFICATION_SERVICE);
        Intent mainActivityIntent = new Intent(Voice2RemindApp.getContext(), SpeechActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(Voice2RemindApp.getContext(), 0, mainActivityIntent, 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(Voice2RemindApp.getContext())
                .setContentTitle(Voice2RemindApp.getContext().getResources().getString(R.string.reminder_trigger_title))
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_assignment_white_24dp)
                .setContentIntent(pIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        nm.notify(0, notification);

    }
}
