package com.android.deordersorter.models;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.deordersorter.database.ItemDatabase;

public class AddItemViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final ItemDatabase mItemDatabase;
    private final String mItemId;

    public AddItemViewModelFactory(ItemDatabase itemDatabase, String itemId) {
        mItemDatabase = itemDatabase;
        mItemId = itemId;

    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AddItemViewModel(mItemDatabase, mItemId);
    }
}
