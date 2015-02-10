package hfu.puigrodr.cityarounder.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import hfu.puigrodr.cityarounder.R;

public class ChooseCategoryDialogFragment extends DialogFragment {

    private ChooseCategoryDialogListener mListener;
    private String[] categories;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        categories = getResources().getStringArray(R.array.categories);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.form_btn_add_category)
                .setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mListener.onChooseCategory(categories[which]);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (ChooseCategoryDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ChooseCategoryDialogListener");
        }
    }

    public interface ChooseCategoryDialogListener {
        public void onChooseCategory(String category);
    }
}
