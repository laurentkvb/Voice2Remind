package nl.lkvb.voice2remind.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import java.util.Collections;
import java.util.List;

import nl.lkvb.voice2remind.R;
import nl.lkvb.voice2remind.adapters.ReminderListAdapter;
import nl.lkvb.voice2remind.dialogs.ReminderConfirmationDialog;
import nl.lkvb.voice2remind.models.Reminder;
import nl.lkvb.voice2remind.utils.PreferenceUtils;

import static nl.lkvb.voice2remind.Voice2RemindApp.getContext;

/**
 * Created by LaurentKlVB on 16-5-2017.
 */

public class ReminderListActivity extends AppCompatActivity {

    private String TAG = ReminderListActivity.class.getSimpleName();
    private Holder mHolder;
    private List<Reminder> mReminderList;
    private ReminderListAdapter mReminderListAdapter;
    private Reminder mDeletedReminder;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
            mDeletedReminder = mReminderList.get(position);
            mReminderListAdapter.removeItem(position);


            initToast(position, viewHolder);

        }


    };
    private ReminderListAdapter.OnItemClickListener onItemClickListener = new ReminderListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, Reminder ReminderName, int position) {
            showEditDialog(position, ReminderName);
        }
    };

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity();

        initViews();

        initAdapter();

        initDialog();

        Log.e(TAG, "Was deze");

    }

    private void initDialog() {
        ReminderConfirmationDialog.setmOnReminderSaved(new ReminderConfirmationDialog.OnReminderSaved() {
            @Override
            public void OnReminderSaved(int position, Reminder reminder) {
                mReminderList.set(position, reminder);
                mReminderListAdapter.notifyItemChanged(position, reminder);
                Collections.sort(mReminderList);

                mReminderListAdapter.notifyItemRangeChanged(0, mReminderList.size());

            }
        });
    }

    private void initToast(final int position, final RecyclerView.ViewHolder viewHolder) {
        SuperActivityToast.create(this, new Style(), Style.TYPE_BUTTON)
                .setButtonText("Undo")
                .setButtonIconResource(R.drawable.ic_error_black_24dp)
                .setOnButtonClickListener("good_tag_name", null, new SuperActivityToast.OnButtonClickListener() {
                    @Override
                    public void onClick(View view, Parcelable token) {
                        Log.e(TAG, "bitch ass nigga");
                        mReminderList.add(mDeletedReminder);
                        PreferenceUtils.getInstance().saveReminder(mDeletedReminder);
                        Collections.sort(mReminderList);
                        mReminderListAdapter.notifyItemInserted(position);

//                        mHolder.mRecyclerView.getada
                        /*.findViewById(R.id.item_reminder_layout)*/
//                        mHolder.mRecyclerView.getLayoutManager().findViewByPosition(position+1).findViewById(R.id.item_reminder_layout)/*.findViewById(R.id.item_reminder_layout)*/.setAlpha(0.5f);
//                        mHolder.mRecyclerView.getLayoutManager().findViewByPosition(position-1).findViewById(R.id.item_reminder_layout)/*.findViewById(R.id.item_reminder_layout)*/.setAlpha(0.5f);
//                        mReminderListAdapter.notifyItemRangeChanged(0, mReminderList.size());
                    }
                })
                .setProgressBarColor(Color.WHITE)
                .setText(getString(R.string.reminder_deleted))
                .setDuration(Style.DURATION_SHORT)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor("0099cc"))
                .setAnimations(Style.ANIMATIONS_POP).show();
    }

    private void showEditDialog(final int position, Reminder reminder) {
        AlertDialog alertDialog = ReminderConfirmationDialog.getInstance(this, reminder, position);
        alertDialog.show();
    }


    /**
     * Method to initialize the adapter for the different channels.
     */
    private void initAdapter() {

        mHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mReminderList = PreferenceUtils.getInstance().getReminders();

        Collections.sort(mReminderList);

        mReminderListAdapter = new ReminderListAdapter(getContext(), mReminderList);

        mReminderListAdapter.setmOnItemClickListener(onItemClickListener);

        mHolder.mRecyclerView.setAdapter(mReminderListAdapter);
        itemTouchHelper.attachToRecyclerView(mHolder.mRecyclerView);

    }


    private void initViews() {
        mHolder = new Holder();

        mHolder.mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reminder_list);

    }

    private void initActivity() {
        setContentView(R.layout.activity_reminder_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }

    @Override
    public void onBackPressed() {
        //TODO change it
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private class Holder {
        RecyclerView mRecyclerView;
    }
}
