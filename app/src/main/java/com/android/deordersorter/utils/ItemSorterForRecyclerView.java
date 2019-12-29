package com.android.deordersorter.utils;
import android.content.Context;

import com.android.deordersorter.R;
import com.android.deordersorter.database.ItemEntity;

import java.util.ArrayList;

public class ItemSorterForRecyclerView {

    public static ArrayList<ItemEntity> sortAllCasesByType(ArrayList<ItemEntity> arrayListToBeSorted, Context context) {

        //just a placeholder to change view for each type of case. the value will never be -5 anyways.
        String itemTypePlaceholder = "-5";
        //sorting array lists. there will be a lot of these, for each type of case! :)

        ArrayList<ItemEntity> masterList = new ArrayList<>();


        ArrayList<ItemEntity> sixMKingsArrayList = new ArrayList<>();
        ArrayList<ItemEntity> softSixMKingsArrayList = new ArrayList<>();
        ArrayList<ItemEntity> twelveMKingsList = new ArrayList<>();
        ArrayList<ItemEntity> softTwelveMKingsList = new ArrayList<>();
        ArrayList<ItemEntity> sixMHundredsArrayList = new ArrayList<>();
        ArrayList<ItemEntity> softSixMHundredsArrayList = new ArrayList<>();
        ArrayList<ItemEntity> twelveMHundredsArrayList = new ArrayList<>();
        ArrayList<ItemEntity> softTwelveMHundredsArrayList = new ArrayList<>();
        ArrayList<ItemEntity> oneTwentiesList = new ArrayList<>();
        ArrayList<ItemEntity> sixPointFoursList = new ArrayList<>();
        ArrayList<ItemEntity> otherTypeArrayList = new ArrayList<>();

        if (arrayListToBeSorted.size() > 0) {
            for (int x = 0; x < arrayListToBeSorted.size(); x++) {
                ItemEntity itemToBeSorted = arrayListToBeSorted.get(x);
                String caseType = itemToBeSorted.getCaseType();
                if (caseType.equals(context.getString(R.string.six_m_king_item_type))) {
                    //check if view is greater than a pallet, if it is then set the information.
                    if (Integer.parseInt(itemToBeSorted.getCaseQuantity()) >= 54) {
                        itemToBeSorted.setSimplifiedCaseQuantity(formatCasesByPalletAndLooseNonHeader(Integer.valueOf(itemToBeSorted.getCaseQuantity()), 54));
                    }
                    sixMKingsArrayList.add(itemToBeSorted);
                } else if (caseType.equals(context.getString(R.string.soft_six_m_king_item_type))) {
                    if (Integer.parseInt(itemToBeSorted.getCaseQuantity()) >= 54) {
                        itemToBeSorted.setSimplifiedCaseQuantity(formatCasesByPalletAndLooseNonHeader(Integer.valueOf(itemToBeSorted.getCaseQuantity()), 54));
                    }
                    softSixMKingsArrayList.add(itemToBeSorted);
                } else if (caseType.equals(context.getString(R.string.twelve_m_king_item_type))) {
                    if (Integer.parseInt(itemToBeSorted.getCaseQuantity()) >= 27) {
                        itemToBeSorted.setSimplifiedCaseQuantity(formatCasesByPalletAndLooseNonHeader(Integer.valueOf(itemToBeSorted.getCaseQuantity()), 27));
                    }
                    twelveMKingsList.add(itemToBeSorted);
                } else if (caseType.equals(context.getString(R.string.soft_twelve_m_king_item_type))) {
                    if (Integer.parseInt(itemToBeSorted.getCaseQuantity()) >= 27) {
                        itemToBeSorted.setSimplifiedCaseQuantity(formatCasesByPalletAndLooseNonHeader(Integer.valueOf(itemToBeSorted.getCaseQuantity()), 27));
                    }
                    softTwelveMKingsList.add(itemToBeSorted);
                } else if (caseType.equals(context.getString(R.string.six_mhundred_item_type))) {
                    if (Integer.parseInt(itemToBeSorted.getCaseQuantity()) >= 42) {
                        itemToBeSorted.setSimplifiedCaseQuantity(formatCasesByPalletAndLooseNonHeader(Integer.valueOf(itemToBeSorted.getCaseQuantity()), 42));
                    }
                    sixMHundredsArrayList.add(itemToBeSorted);
                } else if (caseType.equals(context.getString(R.string.soft_six_mhundred_item_type))) {
                    if (Integer.parseInt(itemToBeSorted.getCaseQuantity()) >= 42) {
                        itemToBeSorted.setSimplifiedCaseQuantity(formatCasesByPalletAndLooseNonHeader(Integer.valueOf(itemToBeSorted.getCaseQuantity()), 42));
                    }
                    softSixMHundredsArrayList.add(itemToBeSorted);
                } else if (caseType.equals(context.getString(R.string.twelve_m_hundred_item_type))) {
                    if (Integer.parseInt(itemToBeSorted.getCaseQuantity()) >= 21) {
                        itemToBeSorted.setSimplifiedCaseQuantity(formatCasesByPalletAndLooseNonHeader(Integer.valueOf(itemToBeSorted.getCaseQuantity()), 21));
                    }
                    twelveMHundredsArrayList.add(itemToBeSorted);
                } else if (caseType.equals(context.getString(R.string.soft_twelve_m_hundred_item_type))) {
                    if (Integer.parseInt(itemToBeSorted.getCaseQuantity()) >= 21) {
                        itemToBeSorted.setSimplifiedCaseQuantity(formatCasesByPalletAndLooseNonHeader(Integer.valueOf(itemToBeSorted.getCaseQuantity()), 21));
                    }
                    softTwelveMHundredsArrayList.add(itemToBeSorted);
                } else if (caseType.equals(context.getString(R.string.one_twenty_item_type))) {
                    if (Integer.parseInt(itemToBeSorted.getCaseQuantity()) >= 36) {
                        itemToBeSorted.setSimplifiedCaseQuantity(formatCasesByPalletAndLooseNonHeader(Integer.valueOf(itemToBeSorted.getCaseQuantity()), 36));
                    }
                    oneTwentiesList.add(itemToBeSorted);
                } else if (caseType.equals(context.getString(R.string.six_point_four))) {
                    sixPointFoursList.add(itemToBeSorted);
                } else if (caseType.equals(context.getString(R.string.other_item_type))) {
                    otherTypeArrayList.add(itemToBeSorted);
                }
            }
            //all items are now sorted, need to combine them and add header views.

            if (!twelveMKingsList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.twelve_m_king_item_type), itemTypePlaceholder);
                // add case quantities up and set Header to be the totals.
                int totalTwelveMKings = AddTotalsOfType(twelveMKingsList);
                itemEntity.setCaseQuantity(formatCasesByPalletAndLoose(totalTwelveMKings, 27));
                itemEntity.setCaseId(-500005);
                //here we just add the finished items to the master list.
                masterList.add(itemEntity);
                masterList.addAll(twelveMKingsList);
            }

            if (!softTwelveMKingsList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.soft_twelve_m_king_item_type), itemTypePlaceholder);
                // add case quantities up and set Header to be the totals.
                int totalSoftTwelveMKings = AddTotalsOfType(softTwelveMKingsList);
                itemEntity.setCaseQuantity(formatCasesByPalletAndLoose(totalSoftTwelveMKings, 27));
                //here we just add the finished items to the master list.
                itemEntity.setCaseId(-500006);
                masterList.add(itemEntity);
                masterList.addAll(softTwelveMKingsList);
            }
            if (!sixMKingsArrayList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.six_m_king_item_type), itemTypePlaceholder);
                int totalsixMKings = AddTotalsOfType(sixMKingsArrayList);
                itemEntity.setCaseQuantity(formatCasesByPalletAndLoose(totalsixMKings, 54));
                itemEntity.setCaseId(-500007);
                //here we just add the finished items to the master list.
                masterList.add(itemEntity);
                masterList.addAll(sixMKingsArrayList);
            }

            if (!softSixMKingsArrayList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.soft_six_m_king_item_type), itemTypePlaceholder);
                // add case quantities up and set Header to be the totals.
                int totalSoftSixMKings = AddTotalsOfType(softSixMKingsArrayList);
                itemEntity.setCaseQuantity(formatCasesByPalletAndLoose(totalSoftSixMKings, 54));
                itemEntity.setCaseId(-5000008);
                //here we just add the finished items to the master list.
                masterList.add(itemEntity);
                masterList.addAll(softSixMKingsArrayList);
            }
            if (!twelveMHundredsArrayList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.twelve_m_hundred_item_type), itemTypePlaceholder);
                int totalTwelveMHundreds = AddTotalsOfType(twelveMHundredsArrayList);
                itemEntity.setCaseQuantity(formatCasesByPalletAndLoose(totalTwelveMHundreds, 21));
                itemEntity.setCaseId(-5000009);
                masterList.add(itemEntity);
                masterList.addAll(twelveMHundredsArrayList);
            }

            if (!softTwelveMHundredsArrayList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.soft_twelve_m_hundred_item_type), itemTypePlaceholder);
                // add case quantities up and set Header to be the totals.
                int totalSoftTwelveMHundreds = AddTotalsOfType(softTwelveMHundredsArrayList);
                itemEntity.setCaseQuantity(formatCasesByPalletAndLoose(totalSoftTwelveMHundreds, 21));
                //here we just add the finished items to the master list.
                itemEntity.setCaseId(-5000010);
                masterList.add(itemEntity);
                masterList.addAll(softTwelveMHundredsArrayList);
            }
            if (!sixMHundredsArrayList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.six_mhundred_item_type), itemTypePlaceholder);
                int totalSixMHundreds = AddTotalsOfType(sixMHundredsArrayList);
                itemEntity.setCaseQuantity(formatCasesByPalletAndLoose(totalSixMHundreds, 42));
                masterList.add(itemEntity);
                itemEntity.setCaseId(-5000011);
                masterList.addAll(sixMHundredsArrayList);
            }

            if (!softSixMHundredsArrayList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.soft_six_mhundred_item_type), itemTypePlaceholder);
                // add case quantities up and set Header to be the totals.
                int totalSoftSixMHundreds = AddTotalsOfType(softSixMHundredsArrayList);

                itemEntity.setCaseQuantity(formatCasesByPalletAndLoose(totalSoftSixMHundreds, 42));
                //itemEntity.setCaseQuantity(String.valueOf(totalSoftSixMHundreds));
                itemEntity.setCaseId(-5000012);
                masterList.add(itemEntity);
                masterList.addAll(softSixMHundredsArrayList);
            }

            if (!oneTwentiesList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.one_twenty_item_type), itemTypePlaceholder);
                int totalOneTwenties = AddTotalsOfType(oneTwentiesList);
                itemEntity.setCaseQuantity(formatCasesByPalletAndLoose(totalOneTwenties, 36));
                itemEntity.setCaseId(-5000013);
                masterList.add(itemEntity);
                masterList.addAll(oneTwentiesList);
            }
            if (!sixPointFoursList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.six_point_four), itemTypePlaceholder);
                int totalSixPointFours = AddTotalsOfType(sixPointFoursList);
                itemEntity.setCaseQuantity(String.valueOf(totalSixPointFours));
                itemEntity.setCaseId(-5000014);
                masterList.add(itemEntity);
                masterList.addAll(sixPointFoursList);
            }

            if (!otherTypeArrayList.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(context.getString(R.string.other_item_type), itemTypePlaceholder);
                // add case quantities up and set Header to be the totals.
                int totalOtherItem = AddTotalsOfType(otherTypeArrayList);
                itemEntity.setCaseQuantity(String.valueOf(totalOtherItem));
                itemEntity.setCaseId(-5000016);
                //here we just add the finished items to the master list.
                masterList.add(itemEntity);
                masterList.addAll(otherTypeArrayList);
            }


        }


        return masterList;
    }

    private static int AddTotalsOfType(ArrayList<ItemEntity> caseTypeArrayList) {

        int total = 0;
        for (int x = 0; x < caseTypeArrayList.size(); x++ ) {
            total += Integer.parseInt(caseTypeArrayList.get(x).getCaseQuantity());
        }
        return total;


    }

    private static String formatCasesByPalletAndLoose(int total, int fullPalletAmount) {

        int leftOverCases = total;
        int fullPallets = 0;
        while (leftOverCases >= fullPalletAmount) {
            leftOverCases -= fullPalletAmount;
            fullPallets += 1;
        }

        if (fullPallets > 0) {
            String formattedParsedTotal = (total + "\n(P: " + fullPallets + " R: " + leftOverCases + ")");
            return formattedParsedTotal;
        }
        else {
            return String.valueOf(total);
        }

    }


    private static String formatCasesByPalletAndLooseNonHeader(int total, int fullPalletAmount) {

        int leftOverCases = total;
        int fullPallets = 0;
        while (leftOverCases >= fullPalletAmount) {
            leftOverCases -= fullPalletAmount;
            fullPallets += 1;
        }

        if (fullPallets > 0) {
            String formattedParsedTotal = ("(P: " + fullPallets + " R: " + leftOverCases + ")");
            return formattedParsedTotal;
        }
        else {
            return String.valueOf(total);
        }

    }


}
