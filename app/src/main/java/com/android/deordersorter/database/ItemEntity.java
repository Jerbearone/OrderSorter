package com.android.deordersorter.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ItemEntity {
    @PrimaryKey(autoGenerate = true)
    private int caseId;

    private String caseQuantity;
    private String itemCode;
    private String caseType;
    private String simplifiedCaseQuantity;
    //TODO make optional overloaded simplifiedCaseQuantity for the recyclerView that will have full pallets and leftovers

    public ItemEntity( String itemCode, String caseType, int caseId) {
        this.itemCode = itemCode;
        this.caseType = caseType;
        this.caseId = caseId;
    }

    @Ignore
    public ItemEntity(String itemCode) {
        this.itemCode = itemCode;
    }

    @Ignore
    public ItemEntity( String itemCode, String caseType) {
        this.itemCode = itemCode;
        this.caseType = caseType;
    }

    @Ignore
    public ItemEntity(String itemCode, String caseType, String caseQuantity) {
        this.itemCode = itemCode;
        this.caseType = caseType;
        this.caseQuantity = caseQuantity;
    }

    @Ignore
    public ItemEntity(String itemCode, String caseType, String caseQuantity, String simplifiedCaseQuantity) {
        this.itemCode = itemCode;
        this.caseType = caseType;
        this.caseQuantity = caseQuantity;
        this.simplifiedCaseQuantity = simplifiedCaseQuantity;
    }

    public String getSimplifiedCaseQuantity() {
        return simplifiedCaseQuantity;
    }

    public void setSimplifiedCaseQuantity(String simplifiedCaseQuantity) {
        this.simplifiedCaseQuantity = simplifiedCaseQuantity;
    }

    public String getCaseQuantity() {
        return caseQuantity;
    }

    public void setCaseQuantity(String caseQuantity) {
        this.caseQuantity = caseQuantity;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }


    public String getCaseType() {
        return caseType;
    }


    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }
}
