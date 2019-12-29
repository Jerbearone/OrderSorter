package com.android.deordersorter.PickRecyclerView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        private PickRecyclerViewAdapter mAdapter;
        public SwipeToDeleteCallback(PickRecyclerViewAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            mAdapter = adapter;
        }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mAdapter.deleteItem(position);

    }

    @Override
    public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.itemView.getTag().equals("Header")) {
            return 0;
        }
        return super.getSwipeDirs(recyclerView, viewHolder);
    }
}
