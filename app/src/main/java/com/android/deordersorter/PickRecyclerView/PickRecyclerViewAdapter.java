package com.android.deordersorter.PickRecyclerView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.deordersorter.PickHandlerInterface;
import com.android.deordersorter.R;
import com.android.deordersorter.database.ItemEntity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class PickRecyclerViewAdapter extends RecyclerView.Adapter<PickRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ItemEntity> mAllItems;
    private ArrayList<ItemEntity> mReverseDeleteItemsList = new ArrayList<>();
    private ArrayList<Integer> mReversDeleteItemPositionsList = new ArrayList<>();
    private Context mContext;
    private int headerCount;
    private PickHandlerInterface mPickHandlerInterface;

    public PickRecyclerViewAdapter(ArrayList<ItemEntity> allItems, Context context, PickHandlerInterface pickHandlerInterface) {
        mAllItems = allItems;
        mContext = context;
        mPickHandlerInterface = pickHandlerInterface;
    }

    @Override
    public long getItemId(int position) {
        return mAllItems.get(position).getCaseId();
    }

    @Override
    public int getItemViewType(int position) {
        return mAllItems.get(position).getCaseId();
    }

    @NonNull
    @Override
    public PickRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemRowView = layoutInflater.inflate(R.layout.single_item_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemRowView);
        return viewHolder;
    }

    public void SendInformationToActivity() {
        mPickHandlerInterface.passPickList(mReverseDeleteItemsList);
    }

    public ArrayList<String> getAllItems() {
        ArrayList<String> allItems = new ArrayList<>();
        for (int i = 0; i < mAllItems.size(); i++) {
            if (!mAllItems.get(i).getCaseType().equals("-5")) {
                allItems.add(mAllItems.get(i).getItemCode());
            }


        }
        return allItems;
    }

    public ArrayList<String> getAllQuantities() {
        ArrayList<String> allQuantities = new ArrayList<>();
        for (int i = 0; i < mAllItems.size(); i++) {

            if (!mAllItems.get(i).getCaseType().equals("-5")) {
                allQuantities.add(mAllItems.get(i).getCaseQuantity());
            }
        }
        return allQuantities;
    }

    @Override
    public void onBindViewHolder(@NonNull PickRecyclerViewAdapter.ViewHolder holder, int position) {
        String itemNumber = mAllItems.get(position).getItemCode();
        TextView itemNumberTextView = holder.itemNumber;

        TextView caseQuantityTextView = holder.caseQuantity;
        itemNumberTextView.setText(itemNumber);

        String caseQuantity = mAllItems.get(position).getCaseQuantity();
        String simplifiedQuantity = mAllItems.get(position).getSimplifiedCaseQuantity();
        if (simplifiedQuantity != null) {
            TextView simplifiedQuantityTextView = holder.simplifiedQuantityView;
            simplifiedQuantityTextView.setText(simplifiedQuantity);
            simplifiedQuantityTextView.setVisibility(View.VISIBLE);

        }
        //int caseQuantity = Integer.parseInt(mAllItems.get(position).getCaseQuantity());

        if (mAllItems.get(position).getCaseType().equals("-5")) {
            //this means that this view is for the case type textView
            if (holder.itemView.getTag() == null) {
                headerCount += 1;
            }

            holder.itemView.setTag("Header");

            if (mAllItems.get(position).getCaseQuantity() != null) {
                String totalString = "Total: " + caseQuantity;
                caseQuantityTextView.setText(totalString);
            }
            int colorId = mContext.getResources().getColor(R.color.colorAccent);
            holder.itemView.setBackgroundColor(colorId);
            holder.itemNumber.setTextColor(Color.WHITE);
            holder.caseQuantity.setTextColor(Color.WHITE);


        } else {
            holder.itemView.setTag("Items");
            String tailcodeChecker = mAllItems.get(position).getItemCode();
            if (tailcodeChecker.length() > 5) {
                String tailcode;
                tailcode = tailcodeChecker.substring(tailcodeChecker.length() - 3);
                if (!tailcode.equals("215") && !tailcode.equals("000") && !tailcode.equals("075")) {
                    int colorId = mContext.getResources().getColor(R.color.colorGreenTailcode);
                    holder.itemView.setBackgroundColor(colorId);

                    //checks if there is a simplifiedCaseQuantity, if there is then set it.
                    caseQuantityTextView.setText(String.valueOf(caseQuantity));

                } else {
                    holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorLight));
                    caseQuantityTextView.setText(String.valueOf(caseQuantity));

                }


            }else {
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorLight));
                caseQuantityTextView.setText(String.valueOf(caseQuantity));
            }
        }


    }

    @Override
    public int getItemCount() {
        return mAllItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView caseQuantity;
        public TextView itemNumber;
        public TextView simplifiedQuantityView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemNumber = itemView.findViewById(R.id.list_item_sku);
            caseQuantity = itemView.findViewById(R.id.list_item_quantity);
            simplifiedQuantityView = itemView.findViewById(R.id.simplified_quantity_text_view);

        }

    }

    public void deleteItem(int position) {
        mReverseDeleteItemsList.add(mAllItems.get(position));
        mReversDeleteItemPositionsList.add(position);
        //subtract from pallet total
        mPickHandlerInterface.addToTotal(Integer.parseInt(mAllItems.get(position).getCaseQuantity()));
        mAllItems.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
        SendInformationToActivity();


    }

    private void showUndoSnackbar() {

        View view = ((Activity) mContext).findViewById(R.id.recycler_coordinator_layout);

        Snackbar snackbar;

        if (mAllItems.size() == headerCount) {
            snackbar = Snackbar.make(view, R.string.snack_bar_text,
                    Snackbar.LENGTH_INDEFINITE);

        } else {
            snackbar = Snackbar.make(view, R.string.snack_bar_text,
                    Snackbar.LENGTH_LONG);

        }

        snackbar.setAction(R.string.snackbard_undo_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete();
            }
        });

        //this is to change the snackbars message color.
        View snackBarView = snackbar.getView();
        TextView snackBarText = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackBarText.setTextColor(mContext.getResources().getColor(R.color.colorLight));
        //show the snackbar..
        snackbar.show();
    }

    private void undoDelete() {
        //handles what happens when the undo snackbar is called.
        if (!mReversDeleteItemPositionsList.isEmpty()) {

            mAllItems.add(mReversDeleteItemPositionsList.get(mReversDeleteItemPositionsList.size() - 1),
                    mReverseDeleteItemsList.get(mReverseDeleteItemsList.size() - 1));
            //subtract from pallet total
            mPickHandlerInterface.subtractFromTOtal(Integer.parseInt(mReverseDeleteItemsList.get(mReversDeleteItemPositionsList.size() - 1).getCaseQuantity()));
            notifyItemInserted(mReversDeleteItemPositionsList.get(mReversDeleteItemPositionsList.size() - 1));
            mReverseDeleteItemsList.remove(mReverseDeleteItemsList.size() - 1);
            mReversDeleteItemPositionsList.remove(mReversDeleteItemPositionsList.size() - 1);
            showUndoSnackbar();
            SendInformationToActivity();

        }

    }
}
