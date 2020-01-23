package com.android.deordersorter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.deordersorter.database.ItemDatabase;
import com.android.deordersorter.database.ItemEntity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int READ_REQUEST_CODE = 42;
    private final String TAG = MainActivity.class.getSimpleName();
    Uri fileUri = null;
    Button fileSelectorButton;
    Button saveButton;
    Button proceedButton;
    FirebaseVisionImage orderImage;
    FirebaseVisionTextRecognizer orderRecognizer;
    TextView resultsTextView;
    TextView quantityTextView;
    TextView unParsedTextView;
    TextView itemNumberTextView;


    ArrayList<String> processedLines = new ArrayList<>();
    ArrayList<String> processedCaseQuantityList = new ArrayList<>();
    ArrayList<String> processedSkusList = new ArrayList<>();
    ArrayList<String> finishedSkuList = new ArrayList<>();
    ArrayList<String> itemsToAddToDatabase = new ArrayList<>();
    ArrayList<Integer> quantityItemsList = new ArrayList<>();

    String finalProcessedString;
    StringBuilder stringBuilder = new StringBuilder();
    String sortedFinalString;
    String itemTypeSelection;

    int totalScannedQuantity;
    int totalQuantity;

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
    Spinner scanTypeSelectorSpinner;
    String spinnerChoice = "RJ";

    private ItemDatabase itemDatabase;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup db
        itemDatabase = ItemDatabase.getInstance(getApplicationContext());

        //just for the optionsView
        scanTypeSelectorSpinner = findViewById(R.id.item_type_spinner);
        ArrayAdapter<CharSequence> spinnerArrayAdapter =
                ArrayAdapter.createFromResource(this, R.array.ScanSelectionSpinnerOptions, android.R.layout.simple_spinner_item);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scanTypeSelectorSpinner.setAdapter(spinnerArrayAdapter);
        scanTypeSelectorSpinner.setOnItemSelectedListener(this);

        sixMKingView = findViewById(R.id.six_m_king_view);
        softSixMKingView = findViewById(R.id.soft_six_m_king_view);

        twelveMKingView = findViewById(R.id.twelve_m_king_view);
        softTwelveMKingView = findViewById(R.id.soft_twelve_m_king_view);
        sixMHundredView = findViewById(R.id.six_m_hundred_view);
        softSixMHundredView = findViewById(R.id.soft_six_m_hundred_view);
        twelveMHundredView = findViewById(R.id.twelve_m_hundred_view);
        softTwelveMHundredView = findViewById(R.id.soft_twelve_m_hundred_view);
        oneTwentyView = findViewById(R.id.one_twenty_view);
        sixPointFourView = findViewById(R.id.six_point_four_view);
        otherTypeTextView = findViewById(R.id.other_type_text_view);
        caseTypeSelectionView = findViewById(R.id.case_update_type_selection_text_view);


        itemNumberTextView = findViewById(R.id.item_number_text_view);
        saveButton = findViewById(R.id.save_button);
        proceedButton = findViewById(R.id.next_activity_button);
        resultsTextView = findViewById(R.id.results_text_view);
        quantityTextView = findViewById(R.id.quantity_text_view);
        unParsedTextView = findViewById(R.id.unparsedResultsTextView);
        orderRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        fileSelectorButton = findViewById(R.id.file_selector_button_view);
        verifyStoragePermissions(MainActivity.this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_go_to_update_activity) {
            // Do nothing but close the dialog
            Intent updateItemIntent = new Intent(MainActivity.this, UpdateItemActivity.class);
            startActivity(updateItemIntent);

            //here is where intent was.
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void checkDatabaseForItem() {
        if (processedSkusList.isEmpty()) {
            Toast.makeText(this, "No items to check", Toast.LENGTH_LONG).show();
        } else {


            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    //itterate through each item that was scanned to check if in DB and add to list.

                    boolean itemsNeedToBeAdded = false;

                    for (int x = 0; x < processedSkusList.size(); x++) {
                        final ItemEntity itemBeingChecked = new ItemEntity(processedSkusList.get(x));
                        ItemEntity theItemCheck = itemDatabase.itemDao().findItemBySku(itemBeingChecked.getItemCode());

                        if (theItemCheck == null) {
                            itemsToAddToDatabase.add(itemBeingChecked.getItemCode());
                            itemsNeedToBeAdded = true;

                        } /*else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "All items in db", Toast.LENGTH_LONG).show();
                                }
                            });
                        }*/

                    }

                    if (itemsNeedToBeAdded) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Must add items before continuing", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "All items in DB", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    if (!itemsToAddToDatabase.isEmpty()) {
                        //after loop to make first index the item in arrayList
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                itemNumberTextView.setText(itemsToAddToDatabase.get(0));

                            }
                        });

                    }
                }
            });

        }
    }


    public void setImageFromFile() {
        try {
            orderImage = FirebaseVisionImage.fromFilePath(this, fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void processOrderImage() {
        orderRecognizer.processImage(orderImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                for (FirebaseVisionText.TextBlock processedBlock : firebaseVisionText.getTextBlocks()) {
                    processedLines.add(processedBlock.getText());
                    //TODO test to see how i can get just the info needed
                    /*for (FirebaseVisionText.Line processedLine : processedBlock.getLines()) {
                        String processedString = processedLine.getText();
                        processedLines.add(processedString);

                }*/
                    processedLines.add("New Block... \n\n\n");
                }
                //resultsTextView.setText(firebaseVisionText.getText());
                for (int x = 0; x < processedLines.size(); x++) {
                    stringBuilder.append(processedLines.get(x));
                    stringBuilder.append("\n");

                }

                unParsedTextView.setText(processedLines.toString());
                //finalProcessedString = stringBuilder.toString();
                finalProcessedString = firebaseVisionText.getText();
                //the final String after processing. Will use regular expression on this.
                //TODO set this later to see raw text from image
                //unParsedTextView.setText(finalProcessedString);

                if (spinnerChoice.equals("PM")) {
                    performPatterMatchPhillipMorris();
                } else if (spinnerChoice.equals("ITG")) {
                    performPatternMatchItg();
                } else if (spinnerChoice.equals("PDF File")) {
                    PerformPatternMatchPDF();
                    //todo handle all pdf files here, maybe remove other options later.


                } else {
                    PerformPatternMatchRJ();
                }
                totalQuantityPatternMatch();

                if (spinnerChoice.equals("PM")) {
                    caseQuantityPatternMatchPhillipMorris();

                } else if (spinnerChoice.equals("PDF File")) {
                    Log.e(TAG, "onSuccess: " + processedSkusList.size() + " " + processedCaseQuantityList.size() );
                } else {
                    caseQuantitiesPatternMatch();
                }
                mergeSkusAndQuantities();
                extractQuantities();
                addUpItems();


            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (data != null) {
                fileUri = data.getData();

                //call methods to get result from image..
                if (!spinnerChoice.equals("PDF File")) {
                    setImageFromFile();
                    processOrderImage();
                } else {
                    //todo file URI will be a pdf file here.. parse it here.
                    Log.e(TAG, "onActivityResult:  " + fileUri.getPath());
                    //it is a pdf file, so lets handle that.

                    PerformPatternMatchPDF();
                    mergeSkusAndQuantities();
                    extractQuantities();
                    addUpItems();
                }


            }
        }
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".

        if (spinnerChoice.equals("PDF File")) {
            intent.setType("*/*");
        } else {
            intent.setType("image/*");
        }

        startActivityForResult(intent, READ_REQUEST_CODE);

    }

    public void PerformPatternMatchRJ() {
        //regex to handle finding case sku numbers.
        //TODO implement spinner or like view to make categories for regular expressions.
        //this pattern is for RJ specifically.
        Pattern itemSearch = Pattern.compile("\\d{2}\\s+\\d{5}\\s+\\d{3}");
        Matcher itemMatcher = itemSearch.matcher(finalProcessedString);
        StringBuilder combinedSkuBuilder = new StringBuilder();

        while (itemMatcher.find()) {
            String singleItem = itemMatcher.group();
            processedSkusList.add(singleItem);

            // finds all item numbers.
        }
        for (int x = 0; x < processedSkusList.size(); x++) {
            combinedSkuBuilder.append(processedSkusList.get(x));
            combinedSkuBuilder.append("\n");
        }

    }

    public void performPatternMatchItgPDF() {

        //todo test this method..

        Pattern itemSearch = Pattern.compile("(\\d+)\\s+CS\\s+(\\d{5})");
        Matcher itemMatcher = itemSearch.matcher(finalProcessedString);
        StringBuilder combinedSkuBuilder = new StringBuilder();
        processedSkusList.clear();

        while (itemMatcher.find()) {
            String singleItem = itemMatcher.group(2);
            processedSkusList.add(singleItem);
            String singleQuantity = itemMatcher.group(1);
            processedCaseQuantityList.add(singleQuantity);
        }

        for (int x = 0; x < processedSkusList.size(); x++) {
            combinedSkuBuilder.append(processedSkusList.get(x));
            combinedSkuBuilder.append("\n");
        }
        Log.e(TAG, "performPatternMatchItgPDF: " + processedSkusList.get(1));
    }

    public void performPatternMatchItg() {
        //TODO test this.
        Pattern itemSearch = Pattern.compile("\\d{5}\\s+");
        Matcher itemMatcher = itemSearch.matcher(finalProcessedString);
        StringBuilder combinedSkuBuilder = new StringBuilder();
        processedSkusList.clear();

        while (itemMatcher.find()) {

            String singleItem = itemMatcher.group();
            processedSkusList.add(singleItem);
        }

        for (int x = 0; x < processedSkusList.size(); x++) {
            combinedSkuBuilder.append(processedSkusList.get(x));
            combinedSkuBuilder.append("\n");
        }


    }

    public void PerformPatternMatchPDF() {
        PDFBoxResourceLoader.init(getApplicationContext());

        String parsedText = null;
        PDDocument pdDocument = null;
        File pdfFile = new File(fileUri.getPath());
        String pdfPath = pdfFile.getAbsolutePath();
        Log.e(TAG, "PerformPatternMatchPDF: " + pdfPath);
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, fileUri);
        String docName = documentFile.getName();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + docName);
        Log.e(TAG, "PerformPatternMatchPDF: " + docName);

        try {
            pdDocument = PDDocument.load(file);
            Log.e(TAG, "PerformPatternMatchPDF: Path is: " + pdfPath);

            try {
                PDFTextStripper pdfTextStripper = new PDFTextStripper();
                //pdfTextStripper.setStartPage(0);
                parsedText = pdfTextStripper.getText(pdDocument);
                finalProcessedString = parsedText;

                //raw text from the pdf file
                //todo this is where we can filter the information, after the pdf is parsed.
                unParsedTextView.setText(parsedText);
                performPatternMatchItgPDF();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e(TAG, "PerformPatternMatchPDF: " + "PDF file Loading didn't work");
            e.printStackTrace();
        }


    }

    public void performPatterMatchPhillipMorris() {
        Pattern itemSearch = Pattern.compile("(C)([o|O|0-9]{1})(\\d\\d\\d\\d\\s*)-(\\s*[0|o|O]{5})(\\w?\\w?)");
        Matcher itemMatcher = itemSearch.matcher(finalProcessedString);
        StringBuilder combinedSkuBuilder = new StringBuilder();
        processedSkusList.clear();

        while (itemMatcher.find()) {
            if (itemMatcher.group(2).equals("o") || itemMatcher.group(2).equals("O")) {
                if (itemMatcher.groupCount() == 5) {
                    String singleItem = "0" + itemMatcher.group(3) + itemMatcher.group(5);
                    processedSkusList.add(singleItem);

                } else {
                    String singleItem = "0" + itemMatcher.group(3);
                    processedSkusList.add(singleItem);
                }


            } else {
                if (itemMatcher.groupCount() == 5) {
                    String singleItem = itemMatcher.group(2) + itemMatcher.group(3) + itemMatcher.group(5);
                    processedSkusList.add(singleItem);

                } else {
                    String singleItem = itemMatcher.group(2) + itemMatcher.group(3);
                    processedSkusList.add(singleItem);

                }

            }
        }

        for (int x = 0; x < processedSkusList.size(); x++) {
            combinedSkuBuilder.append(processedSkusList.get(x));
            combinedSkuBuilder.append("\n");
        }
    }

    public void addUpItems() {
        if (!quantityItemsList.isEmpty()) {
            totalScannedQuantity = 0;
            for (int x = 0; x < quantityItemsList.size(); x++) {
                totalScannedQuantity += quantityItemsList.get(x);


            }
            quantityTextView.setText(String.valueOf(totalScannedQuantity));

        }
    }

    public void totalQuantityPatternMatch() {
        Pattern quantitySearch = Pattern.compile("(Qty:\\s*)(\\d*)");
        Matcher quantityMatch = quantitySearch.matcher(finalProcessedString);
        while (quantityMatch.find()) {
            String totalQuantityString = quantityMatch.group(2);
            Log.d(MainActivity.class.getSimpleName(), "totalQuantityPatternMatch: " + totalQuantityString);
            //TODO make quantity integer for comparison to totalScannedQuantity
            if (!totalQuantityString.isEmpty()) {
                totalQuantity = Integer.parseInt(totalQuantityString);
            }
        }


    }

    public void caseQuantitiesPatternMatch() {
        Pattern caseQuantitySearch = Pattern.compile("\\d\\d*\\sCS");
        Matcher caseQuantityMatch = caseQuantitySearch.matcher(finalProcessedString);
        while (caseQuantityMatch.find()) {
            processedCaseQuantityList.add(caseQuantityMatch.group());

        }
    }

    public void caseQuantityPatternMatchPhillipMorris() {
        Pattern caseQuantitySearch = Pattern.compile("[1-9]{1}\\d?\\d?\\n");
        Matcher caseQuantityMatch = caseQuantitySearch.matcher(finalProcessedString);
        while (caseQuantityMatch.find()) {
            processedCaseQuantityList.add(caseQuantityMatch.group());

        }

    }

    public void extractQuantities() {
        if (!processedCaseQuantityList.isEmpty()) {

            if (spinnerChoice.equals("PM")) {

                for (int x = 0; x < processedCaseQuantityList.size(); x++) {
                    String stringToConvert = processedCaseQuantityList.get(x);
                    int strippedInt = Integer.parseInt(stringToConvert.trim());


                    quantityItemsList.add(strippedInt);
                    Log.e(MainActivity.class.getSimpleName(), "addQuantities: " + quantityItemsList.get(x));

                }

            } else {

                for (int x = 0; x < processedCaseQuantityList.size(); x++) {
                    String itemBeingStripped = processedCaseQuantityList.get(x);
                    StringBuilder removeStringFromInt = new StringBuilder(itemBeingStripped);
                    if (!spinnerChoice.equals("PDF File")) {
                        removeStringFromInt.setLength(removeStringFromInt.length() - 3);
                    }
                    int strippedInt = Integer.parseInt(removeStringFromInt.toString());


                    quantityItemsList.add(strippedInt);
                    Log.e(MainActivity.class.getSimpleName(), "addQuantities: " + quantityItemsList.get(x));

                }
            }
        }
    }

    public void mergeSkusAndQuantities() {
        //method will be called on post image process to combine the Skus and quantities.
        StringBuilder sortedStringBuilder = new StringBuilder();
        if (processedSkusList.size() == processedCaseQuantityList.size()) {
            for (int x = 0; x < processedSkusList.size(); x++) {
                sortedStringBuilder.append(processedSkusList.get(x)).append
                        ("  ").append(processedCaseQuantityList.get(x)).append("\n");
            }
            sortedFinalString = sortedStringBuilder.toString();


            resultsTextView.setText(sortedFinalString);

            //add all items from processedSkuList to finishedSkuList before it is cleared.
            finishedSkuList.addAll(processedSkusList);

            //here we will check database for items..
            checkDatabaseForItem();

        } else {
            Toast.makeText(this,
                    "Sku / quantity do not match, retake image " + processedSkusList.size() + " | " + processedCaseQuantityList.size(), Toast.LENGTH_LONG).show();
            clearStringsAndLists();
        }
    }

    public void clearStringsAndLists() {
        processedLines.clear();
        processedCaseQuantityList.clear();
        processedSkusList.clear();
        stringBuilder.setLength(0);
        finalProcessedString = "";


    }

    public void ItemClicked(View view) {
        if (view.getId() == R.id.six_m_king_view) {
            itemTypeSelection = getString(R.string.six_m_king_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.soft_six_m_king_view) {
            itemTypeSelection = getString(R.string.soft_six_m_king_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.twelve_m_king_view) {
            itemTypeSelection = getString(R.string.twelve_m_king_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.soft_twelve_m_king_view) {
            itemTypeSelection = getString(R.string.soft_twelve_m_king_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.six_m_hundred_view) {
            itemTypeSelection = getString(R.string.six_mhundred_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.soft_six_m_hundred_view) {
            itemTypeSelection = getString(R.string.soft_six_mhundred_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.twelve_m_hundred_view) {
            itemTypeSelection = getString(R.string.twelve_m_hundred_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.soft_twelve_m_hundred_view) {
            itemTypeSelection = getString(R.string.soft_twelve_m_hundred_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.one_twenty_view) {
            itemTypeSelection = getString(R.string.one_twenty_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.six_point_four_view) {
            itemTypeSelection = getString(R.string.six_point_four);
            caseTypeSelectionView.setText(itemTypeSelection);
        } else if (view.getId() == R.id.other_type_text_view) {
            itemTypeSelection = getString(R.string.other_item_type);
            caseTypeSelectionView.setText(itemTypeSelection);

        } else if (view.getId() == R.id.file_selector_button_view) {
            clearStringsAndLists();
            performFileSearch();
        } else if (view.getId() == R.id.next_activity_button) {
            //TODO make sure next activity is called correctly through this button.
            if (!quantityItemsList.isEmpty() && finishedSkuList.size() == quantityItemsList.size()) {

                if (itemsToAddToDatabase.isEmpty()) {
                    //start next activity
                    Intent goToPickActivity = new Intent(getApplicationContext(), PickActivity.class);
                    goToPickActivity.putStringArrayListExtra("processedSkus", finishedSkuList);
                    goToPickActivity.putIntegerArrayListExtra("itemsQuantities", quantityItemsList);
                    //TODO start activity with information
                    startActivity(goToPickActivity);

                } else {
                    Toast.makeText(this, "Add all items to database", Toast.LENGTH_SHORT).show();
                }


            }

        } else if (view.getId() == R.id.save_button) {

            //implement database transaction here.
            if (itemsToAddToDatabase.isEmpty()) {
                Toast.makeText(this, "Database has all items", Toast.LENGTH_SHORT).show();
                itemNumberTextView.setText(getString(R.string.database_has_all_items));
            } else {
                Toast.makeText(this, "Adding item to DB", Toast.LENGTH_SHORT).show();
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        ItemEntity itemToInsert = new ItemEntity(itemsToAddToDatabase.get(0), itemTypeSelection);
                        itemsToAddToDatabase.remove(0);
                        //update textView after removal to show item going to be added.
                        if (!itemsToAddToDatabase.isEmpty()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    itemNumberTextView.setText(itemsToAddToDatabase.get(0));

                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    itemNumberTextView.setText(getString(R.string.database_has_all_items));
                                }
                            });
                        }

                        itemDatabase.itemDao().InsertItem(itemToInsert);
                    }
                });
            }
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        spinnerChoice = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

