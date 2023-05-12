package com.metanetglobal.knowledge.worker.schedule;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.common.Utils;
import com.metanetglobal.knowledge.worker.common.utils.OnChildItemClickListener;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleListItemDTO;

import java.util.List;

/**
 * Schedule List Adapter
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         android.support
 */
public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {
    private final String TAG = ScheduleListAdapter.class.getSimpleName();

    private OnChildItemClickListener<ScheduleListItemDTO> childItemClickListener;
    private List<ScheduleListItemDTO> mList;

    /**
     * Default Constructor
     *
     * @param list          List
     * @param listener      ChildItemClickListener
     */
    public ScheduleListAdapter(List<ScheduleListItemDTO> list, OnChildItemClickListener listener) {
        this.mList = list;
        this.childItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_schedule, parent, false);

        final ViewHolder holder = new ViewHolder(v);

        ((ViewHolder) holder).contentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(childItemClickListener != null && mList != null) {
                    childItemClickListener.onChildItemClick(mList.get(holder.getAdapterPosition()));
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder != null && holder instanceof ScheduleListAdapter.ViewHolder) {
            ((ViewHolder) holder).countTextView.setText(String.valueOf(position + 1));

            String startTime = Utils.nullCheck(mList.get(holder.getAdapterPosition()).getStartTime());
            String timeText = "";
            if(startTime != null && startTime.length() > 3) {
                String sHour = startTime.substring(0, 2);
                String sMinute = startTime.substring(2, 4);
                timeText = sHour + ":" + sMinute + " ~ ";
            }

            String endTime = Utils.nullCheck(mList.get(holder.getAdapterPosition()).getEndTime());
            if(endTime != null && endTime.length() > 3) {
                String eHour = endTime.substring(0, 2);
                String eMinute = endTime.substring(2, 4);
                if(timeText.length() > 0) {
                    timeText += (eHour + ":" + eMinute);
                } else {
                    timeText += (" ~ " + eHour + ":" + eMinute);
                }
            }

            ((ViewHolder) holder).timeTextView.setText(timeText);
            ((ViewHolder) holder).taskTextView.setText(Utils.nullCheck(mList.get(holder.getAdapterPosition()).getTaskNm()));
            ((ViewHolder) holder).typeTextView.setText(Utils.nullCheck(mList.get(holder.getAdapterPosition()).getDutyCdNm()));
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout contentsLayout;
        TextView countTextView;
        TextView timeTextView;
        TextView taskTextView;
        TextView typeTextView;

        ViewHolder(View itemView) {
            super(itemView);

            contentsLayout = (LinearLayout) itemView.findViewById(R.id.schedule_list_item_contents_layout);
            countTextView = (TextView) itemView.findViewById(R.id.schedule_list_item_count_textview);
            timeTextView = (TextView) itemView.findViewById(R.id.schedule_list_item_time_textview);
            taskTextView = (TextView) itemView.findViewById(R.id.schedule_list_item_task_textview);
            typeTextView = (TextView) itemView.findViewById(R.id.schedule_list_item_type_textview);
        }
    }
}
