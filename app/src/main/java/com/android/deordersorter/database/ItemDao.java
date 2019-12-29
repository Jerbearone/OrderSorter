package com.android.deordersorter.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM ItemEntity WHERE itemCode = :Sku")
    LiveData<ItemEntity> findItemByItemCode(String Sku);

    @Query("Select * From ItemEntity WHERE itemCode = :item")
    ItemEntity findItemBySku(String item);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertItem(ItemEntity itemEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItem(ItemEntity itemEntity);
}
