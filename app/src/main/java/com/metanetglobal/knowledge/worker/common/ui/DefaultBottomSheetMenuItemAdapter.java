package com.metanetglobal.knowledge.worker.common.ui;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.common.Utils;
import com.metanetglobal.knowledge.worker.common.bean.CommCodeDTO;

import java.util.List;

/**
 * BottomSheet Menu List Item Adapter
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         android.support
 * @see         CommCodeDTO
 */
public class DefaultBottomSheetMenuItemAdapter extends RecyclerView.Adapter {
    private List<CommCodeDTO> mList;
    private ItemListener mListener;

    /**
     * Constructor of DefaultBottomSheetMenuItemAdapter
     *
     * @param list  List of Common Code
     */
    public DefaultBottomSheetMenuItemAdapter(List<CommCodeDTO> list) {
        this(list, null);
    }

    /**
     * Constructor of DefaultBottomSheetMenuItemAdapter
     *
     * @param list  List of Common Code
     * @param listener  Item Click Listener
     */
    public DefaultBottomSheetMenuItemAdapter(List<CommCodeDTO> list, ItemListener listener) {
        this.mList = list;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_default_bottomsheet_menu_list, parent, false);
        vh = new ItemViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder != null && holder instanceof ItemViewHolder) {
            if(mList != null && mList.size() > 0 && holder.getAdapterPosition() > -1) {
                ((ItemViewHolder) holder).textView.setText(Utils.nullCheck(mList.get(holder.getAdapterPosition()).getMarkName()));

                ((ItemViewHolder) holder).itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onItemClick(mList.get(holder.getAdapterPosition()));
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * Add Item to List
     *
     * @param item  Added item
     */
    public void add(CommCodeDTO item) {
        if(mList != null) {
            mList.add(item);
        }
    }

    /**
     * Add All Item to List
     *
     * @param itemList  Item list
     */
    public void addAll(List<CommCodeDTO> itemList) {
        if(mList != null) {
            mList.addAll(itemList);
        }
    }

    /**
     * Remove item from List
     *
     * @param position  position of removed item
     */
    public void remove(int position) {
        if(mList != null) {
            mList.remove(position);
        }
    }

    /**
     * Clear List
     */
    public void clear() {
        if(mList != null) {
            mList.clear();
        }
    }

    /**
     * Set Item Click Listener
     *
     * @param listener  Item Click Listener
     */
    public void setListener(ItemListener listener) {
        this.mListener = listener;
    }

    /**
     * ViewHolder of Item
     */
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemLayout;
        public TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemLayout = (RelativeLayout) itemView.findViewById(R.id.default_bottom_sheet_menu_item_layout);
            textView = (TextView) itemView.findViewById(R.id.default_bottom_sheet_menu_item_title);
        }
    }

    /**
     * Item Click Listener
     */
    public interface ItemListener {
        void onItemClick(CommCodeDTO item);
    }
}
