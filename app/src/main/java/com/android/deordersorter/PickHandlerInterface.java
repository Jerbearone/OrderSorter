package com.android.deordersorter;

import com.android.deordersorter.database.ItemEntity;

import java.util.ArrayList;

public interface PickHandlerInterface {
    void passPickList(ArrayList<ItemEntity> pickedList);

    //todo implement passing of information after each pick to add up cases.

    void subtractFromTOtal(int caseQuantity);

    void addToTotal(int caseQuantity);
}
