package com.android.deordersorter.models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.android.deordersorter.database.ItemDatabase;
import com.android.deordersorter.database.ItemEntity;

public class AddItemViewModel extends ViewModel {
    private LiveData<ItemEntity> item;
    private final static String TAG = AddItemViewModel.class.getSimpleName();

    public AddItemViewModel(ItemDatabase mItemDatabase, String mItemId) {
        Log.d(TAG, "AddItemViewModel: retreiving tasks from ItemDatabase");
        item = mItemDatabase.itemDao().findItemByItemCode(mItemId);

    }

    public LiveData<ItemEntity> getItem () {
        return item;
    }
}
