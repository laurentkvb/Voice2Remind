package nl.lkvb.voice2remind.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import nl.lkvb.voice2remind.R;
import nl.lkvb.voice2remind.models.Reminder;
import nl.lkvb.voice2remind.utils.DateUtils;
import nl.lkvb.voice2remind.utils.PreferenceUtils;


/**
 * ReminderListAdapter Adapter for the channels:
 * - Coffee Machine
 * - IFTT
 */
public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.Holder> {

    public String TAG = getClass().getSimpleName();
    private Context mContext;
    private OnLongItemClickListener mOnLongItemClickListener;
    private OnItemClickListener mOnItemClickListener;
    private Holder mHolder;
    private View mDeletedView;

    private List<Reminder> mReminderList;

    public ReminderListAdapter(Context mContext, List<Reminder> mChannelList) {
        this.mContext = mContext;
        this.mReminderList = mChannelList;
    }

    public View getmDeletedView() {
        return mDeletedView;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_reminder_list, parent, false);

        mHolder = new Holder(v);
        return mHolder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Reminder soundBoardItem = mReminderList.get(position);
        holder.bind(soundBoardItem, position);
        onClick(holder.mLayout, soundBoardItem, position);
    }

    @Override
    public int getItemViewType(int position) {
//        return position;
        return super.getItemViewType(position);
    }

    private void onClick(final View clickableView, final Reminder ReminderName, final int position) {
        clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(clickableView, ReminderName, position);
                }
            }

        });

        clickableView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongItemClickListener != null) {
                    mOnLongItemClickListener.onLongItemClick(clickableView, ReminderName, position);
                    return true;
                }
                return false;
            }
        });

    }

    public void removeItem(int position) {
        if (removeItemSafe(position)) {
            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, getItemCount());
            PreferenceUtils.getInstance().saveReminders(mReminderList);
        }
    }


    private boolean removeItemSafe(int position) {
        if (mReminderList.size() >= position + 1) {
            mReminderList.remove(position);
            return true;
        }
        return false;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setmOnLongItemClickListener(OnLongItemClickListener mOnLongItemClickListener) {
        this.mOnLongItemClickListener = mOnLongItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mReminderList.size();
    }

    @Override
    public long getItemId(int position) {
        Log.e(TAG, "ik qwe");
        return super.getItemId(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, Reminder ReminderName, int position);
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(View v, Reminder ReminderName, int position);
    }


    public class Holder extends RecyclerView.ViewHolder {

        public CardView mLayout;
        private TextView mTitle;
        private TextView mSubTitle;

        public Holder(View itemView) {
            super(itemView);
            mLayout = (CardView) itemView.findViewById(R.id.item_reminder_layout);
            mTitle = (TextView) itemView.findViewById(R.id.item_reminder_title);
            mSubTitle = (TextView) itemView.findViewById(R.id.item_reminder_subtitle);
        }

        void bind(Reminder ReminderItem, int position) {
            float boi = 0.5F;
            mTitle.setText(ReminderItem.getmReminderText());
            mSubTitle.setText(DateUtils.getDateFormatted(ReminderItem.getmDateToRemind()));
            if (ReminderItem.getmDateToRemind().before(Calendar.getInstance())) {
//                mLayout.setAlpha(boi); // TODO uncomment
            }
        }


    }

}
