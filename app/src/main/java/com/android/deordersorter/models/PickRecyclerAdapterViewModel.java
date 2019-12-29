package com.android.deordersorter.models;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.android.deordersorter.PickRecyclerView.PickRecyclerViewAdapter;
import com.android.deordersorter.database.ItemEntity;

import java.util.ArrayList;

public class PickRecyclerAdapterViewModel extends AndroidViewModel {


    private Context mContext;
    private ArrayList<ItemEntity> mAllItems;
    PickRecyclerViewAdapter mAdapter;

    public PickRecyclerAdapterViewModel(@NonNull Application application, ArrayList<ItemEntity> mAllItems) {
        super(application);
        mContext = getApplication().getApplicationContext();
        PickRecyclerViewAdapter mAdapter = new PickRecyclerViewAdapter(mAllItems, mContext);
    }
}
