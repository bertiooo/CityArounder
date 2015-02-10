package hfu.puigrodr.cityarounder.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import hfu.puigrodr.cityarounder.R;

public class NewTourDialogFragment extends DialogFragment {

    private NewTourDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View formView = inflater.inflate(R.layout.dialog_new_tour, null);

        final EditText editTextTitle = (EditText) formView.findViewById(R.id.dialog_new_tour_name);
        final EditText editTextCity = (EditText) formView.findViewById(R.id.dialog_new_tour_city);

        builder.setTitle(R.string.form_btn_new_tour);
        builder.setView(formView)
                .setPositiveButton(R.string.dialog_tour_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String city = editTextCity.getText().toString();
                        String title = editTextTitle.getText().toString();

                        mListener.onAddTourClick(title, city);
                    }
                })
                .setNegativeButton(R.string.dialog_tour_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        NewTourDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (NewTourDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NewTourDialogListener");
        }
    }

    public interface NewTourDialogListener {
        public void onAddTourClick(String title, String city);
    }
}
