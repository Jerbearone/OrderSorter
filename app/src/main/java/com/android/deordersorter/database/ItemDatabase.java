package com.android.deordersorter.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {ItemEntity.class}, version = 2, exportSchema = false)
public abstract class ItemDatabase extends RoomDatabase {

    private final static String LogTag = ItemDatabase.class.getSimpleName();
    private final static Object LOCK = new Object();
    private final static String DATABASENAME = "theItemDatabase";
    private static ItemDatabase sInstance;

    public static ItemDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LogTag, "getInstance: Creating new Database");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        ItemDatabase.class, ItemDatabase.DATABASENAME).createFromAsset("theItemDatabase")
                        .addMigrations(MIGRATION_1_2)
                        .build();

                // Room.databaseBuilder(context.getApplicationContext()),
                        //ItemDatabase.class, ItemDatabase.DATABASENAME).build();
            }
        }
        Log.d(LogTag, "getting database Instance");
        return sInstance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE itemEntity ADD COLUMN simplifiedCaseQuantity TEXT");

        }
    };
    public abstract ItemDao itemDao();
}
