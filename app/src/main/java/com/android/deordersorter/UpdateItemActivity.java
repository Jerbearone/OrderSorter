package com.android.deordersorter;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.deordersorter.database.ItemDatabase;
import com.android.deordersorter.database.ItemEntity;

public class UpdateItemActivity extends AppCompatActivity {

    //text_views
    TextView sixMKingView;
    TextView softSixMKingView;
    TextView twelveMKingView;
    TextView softTwelveMKingView;
    TextView sixMHundredView;
    TextView softSixMHundredView;
    TextView twelveMHundredView;
    TextView softTwelveMHundredView;
    TextView oneTwentyView;
    TextView sixPointFourView;
    TextView otherTypeTextView;
    TextView caseTypeSelectionView;

    Button updateButton;
    EditText itemNumberEditText;
    String itemTypeSelection;
    ItemDatabase itemDatabase;
    String itemNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        itemDatabase = ItemDatabase.getInstance(getApplicationContext());

        sixMKingView = findViewById(R.id.update_six_m_king_view);
        softSixMKingView = findViewById(R.id.update_soft_six_m_king_view);
        twelveMKingView = findViewById(R.id.update_twelve_m_king_view);
        softTwelveMKingView = findViewById(R.id.update_soft_twelve_m_king_view);
        sixMHundredView = findViewById(R.id.update_six_m_hundred_view);
        softSixMHundredView = findViewById(R.id.update_soft_six_m_hundred_view);
        twelveMHundredView = findViewById(R.id.update_twelve_m_hundred_view);
        softTwelveMHundredView = findViewById(R.id.update_soft_twelve_m_hundred_view);
        oneTwentyView = findViewById(R.id.update_one_twenty_view);
        sixPointFourView = findViewById(R.id.update_six_point_four_view);
        otherTypeTextView = findViewById(R.id.update_other_type_text_view);
        itemNumberEditText = findViewById(R.id.update_item_edit_text_box);
        caseTypeSelectionView = findViewById(R.id.case_update_type_selection_text_view);
        updateButton = findViewById(R.id.update_button);


    }

    public void ItemTypeClicked(View view) {
        if (view.getId() == R.id.update_six_m_king_view) {
            itemTypeSelection = getString(R.string.six_m_king_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        }else if (view.getId() == R.id.update_soft_six_m_king_view){
            itemTypeSelection = getString(R.string.soft_six_m_king_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        }  else if (view.getId() == R.id.update_twelve_m_king_view) {
            itemTypeSelection = getString(R.string.twelve_m_king_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.update_soft_twelve_m_king_view) {
            itemTypeSelection = getString(R.string.soft_twelve_m_king_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        }else if (view.getId() == R.id.update_six_m_hundred_view) {
            itemTypeSelection = getString(R.string.six_mhundred_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        }else if (view.getId() == R.id.update_soft_six_m_hundred_view) {
            itemTypeSelection = getString(R.string.soft_six_mhundred_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.update_twelve_m_hundred_view) {
            itemTypeSelection = getString(R.string.twelve_m_hundred_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId()== R.id.update_soft_twelve_m_hundred_view) {
            itemTypeSelection = getString(R.string.soft_twelve_m_hundred_item_type);
        } else if (view.getId() == R.id.update_one_twenty_view) {
            itemTypeSelection = getString(R.string.one_twenty_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.update_six_point_four_view) {
            itemTypeSelection = getString(R.string.six_point_four);
            caseTypeSelectionView.setText(itemTypeSelection);
        }else if (view.getId() == R.id.update_other_type_text_view) {
            itemTypeSelection = getString(R.string.other_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);

        }else if (view.getId() == R.id.update_button) {
            itemNumber = itemNumberEditText.getText().toString();


            //implement database transaction here.
            if (itemNumber.matches("")) {
                Toast.makeText(this, "Input an item number", Toast.LENGTH_SHORT).show();
            } else {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        ItemEntity itemBeingChecked = itemDatabase.itemDao().findItemBySku(itemNumber);

                        if (itemBeingChecked == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UpdateItemActivity.this, "Item not in database", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else {

                            if (itemTypeSelection == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UpdateItemActivity.this, "Select an item type to update", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                itemBeingChecked.setCaseType(itemTypeSelection);
                                itemDatabase.itemDao().updateItem(itemBeingChecked);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UpdateItemActivity.this, "Item updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }


                    }
                });
            }
        }


    }




}
