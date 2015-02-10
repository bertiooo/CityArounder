package hfu.puigrodr.cityarounder.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

import hfu.puigrodr.cityarounder.R;
import hfu.puigrodr.cityarounder.controller.Repository;
import hfu.puigrodr.cityarounder.controller.UserIdentifier;
import hfu.puigrodr.cityarounder.models.Tour;

public class ChooseTourDialogFragment extends DialogFragment {

    private ChooseTourDialogListener mListener;
    private String[] tourIds;
    private String[] mTitles;
    private ArrayAdapter<String> mAdapter;
    private Context mContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        mContext = getActivity();
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1);

        QueryTask task = new QueryTask();
        task.execute();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.form_btn_add_tour);
        builder.setAdapter(mAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onChooseTourClick(tourIds[which], mTitles[which]);
            }
        });

        return builder.create();
    }

    private class QueryTask extends AsyncTask<String, Void, Tour[]> {

        @Override
        protected Tour[] doInBackground(String... params) {

            Repository repository = new Repository(mContext);
            return repository.findToursByUser(new UserIdentifier(mContext).getId());
        }

        @Override
        protected void onPostExecute(Tour[] tours){

            tourIds = new String[tours.length];
            mTitles = new String[tours.length];

            for(int i=0; i<tours.length; i++){
                tourIds[i] = tours[i].getId();
                mTitles[i] = tours[i].getTitle();
            }

            if(tourIds.length > 0){

                mAdapter.addAll(mTitles);
                mAdapter.notifyDataSetChanged();
            } else {

                mAdapter.add("Sie haben noch keine Touren. Bitte eine neue Tour erstellen.");
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (ChooseTourDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ChooseTourDialogListener");
        }
    }

    public interface ChooseTourDialogListener {
        public void onChooseTourClick(String tourId, String title);
    }
}
