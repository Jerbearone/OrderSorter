package com.android.deordersorter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.deordersorter.PickRecyclerView.PickRecyclerViewAdapter;
import com.android.deordersorter.PickRecyclerView.SwipeToDeleteCallback;
import com.android.deordersorter.database.ItemDatabase;
import com.android.deordersorter.database.ItemEntity;
import com.android.deordersorter.utils.ItemSorterForRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PickActivity extends AppCompatActivity {

    ArrayList<Integer> allItemQuantities;
    ArrayList<String> allItems;
    ArrayList<ItemEntity> initialItemsToBeSorted = new ArrayList<>();
    ArrayList<ItemEntity> completelySortedArrayList = new ArrayList<>();
    RecyclerView itemRecyclerView;
    ItemTouchHelper itemTouchHelper;
    CoordinatorLayout recyclerCoodinatorLayout;
    LinearLayoutManager layoutManager;
    private ItemDatabase itemDatabase;
    boolean listWasSorted = false;
    private String Tag = PickActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        itemRecyclerView = findViewById(R.id.pick_recycler_view);
        recyclerCoodinatorLayout = findViewById(R.id.recycler_coordinator_layout);

        if (listWasSorted) {

        } else {

            itemDatabase = ItemDatabase.getInstance(getApplicationContext());
            //setup recyclerView
            //setup items for arrayAdapter
            Intent infoIntent = getIntent();
            allItemQuantities = infoIntent.getIntegerArrayListExtra("itemsQuantities");
            allItems = infoIntent.getStringArrayListExtra("processedSkus");

            //turn arrays into ItemEntity
            sortAsyncTask asyncTask = new sortAsyncTask(this);
            asyncTask.execute(null, null);
        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void setupRecyclerView() {
        PickRecyclerViewAdapter itemAdapter = new PickRecyclerViewAdapter(completelySortedArrayList, this);
        itemAdapter.setHasStableIds(true);
        itemRecyclerView.setAdapter(itemAdapter);
        layoutManager = new LinearLayoutManager(this);
        itemRecyclerView.setLayoutManager(layoutManager);


        itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(itemAdapter));
        itemTouchHelper.attachToRecyclerView(itemRecyclerView);

    }



    private class sortAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<PickActivity> weakReference;
        sortAsyncTask(PickActivity context) {
            weakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //this loop will add the case type to each case before sorting.
            for (int x = 0; x < allItems.size(); x++) {
                ItemEntity itemEntity = new ItemEntity(allItems.get(x));
                ItemEntity theItemCheck = itemDatabase.itemDao().findItemBySku(itemEntity.getItemCode());
                theItemCheck.setCaseQuantity(Integer.toString(allItemQuantities.get(x)));

                if (theItemCheck.getCaseType() == null) {
                    Log.e(Tag, "for some reason item check is null. ");

                } else {
                    initialItemsToBeSorted.add(theItemCheck);
                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            PickActivity pickActivity = weakReference.get();
            if (pickActivity == null || pickActivity.isFinishing()) {
                return;
            }

            completelySortedArrayList = ItemSorterForRecyclerView.sortAllCasesByType(initialItemsToBeSorted, getApplicationContext());
            //TODO need to add to onsavedinstancestate so the UI doesn't have to be reloaded.
            listWasSorted = true;

            //after database quries on background thread, we will sort all the items so they can
            // be displayed in recyclerView


            //Toast.makeText(PickActivity.this, initialItemsToBeSorted.get(0).getCaseType(), Toast.LENGTH_SHORT).show();
            setupRecyclerView();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pick_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.go_home_option_menu) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogue);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to reset?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    Intent homeIntent = new Intent(PickActivity.this, MainActivity.class);
                    startActivity(homeIntent);

                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            //here is where intent was.
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
}
