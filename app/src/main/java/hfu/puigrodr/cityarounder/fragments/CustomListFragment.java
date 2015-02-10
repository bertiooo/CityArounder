package hfu.puigrodr.cityarounder.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import hfu.puigrodr.cityarounder.R;
import hfu.puigrodr.cityarounder.activities.LocationsActivity;
import hfu.puigrodr.cityarounder.activities.ViewPagerActivity;
import hfu.puigrodr.cityarounder.controller.Repository;
import hfu.puigrodr.cityarounder.controller.UserIdentifier;
import hfu.puigrodr.cityarounder.models.Tour;

public class CustomListFragment extends ListFragment {

    private ArrayAdapter<String> mAdapter;
    private Context mContext;
    private String[] mIds;
    private String[] mTitles;
    private String mTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mContext = inflater.getContext();

        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1);

        setListAdapter(mAdapter);

        Bundle args = getArguments();
        mTab = ViewPagerActivity.TABS[args.getInt("page_position")];

        if(mTitles == null){

            mAdapter.add("Wird geladen...");
            QueryTask task = new QueryTask();
            task.execute();

        } else {

            mAdapter.addAll(mTitles);
            mAdapter.notifyDataSetChanged();
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private class QueryTask extends AsyncTask<String, Void, String[]> {


        @Override
        protected String[] doInBackground(String... params) {

            mTitles = null;

            Repository repository = new Repository(mContext);
            switch(mTab){
                case "Städte":
                    mTitles = repository.findAllCities();
                    mIds = mTitles;
                    break;
                case "Kategorien":
                    mTitles = getResources().getStringArray(R.array.categories);
                    mIds = mTitles;
                    break;
                case "Touren":
                    Tour[] tours = repository.findAllTours();

                    mTitles = new String[tours.length];
                    mIds = new String[tours.length];

                    for(int i=0; i<tours.length; i++){
                        mIds[i] = tours[i].getId();
                        mTitles[i] = tours[i].getTitle();
                    }
                    break;
                case "Meine Touren":
                    Tour[] tours1 = repository.findToursByUser(new UserIdentifier(mContext).getId());

                    mTitles = new String[tours1.length];
                    mIds = new String[tours1.length];

                    for(int i=0; i<tours1.length; i++){
                        mIds[i] = tours1[i].getId();
                        mTitles[i] = tours1[i].getTitle();
                    }
                    break;
            }

            return mTitles;
        }

        @Override
        protected void onPostExecute(String[] listItems){

            mTitles = listItems;

            if(listItems != null && listItems.length > 0){

                mAdapter.remove("Wird geladen...");
                mAdapter.addAll(listItems);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id){

        Intent intent = new Intent(mContext, LocationsActivity.class);

        Bundle args = new Bundle();
        args.putString("tab_name", mTab);
        args.putString("reference_id", mIds[position]);
        intent.putExtra("locations", args);

        startActivity(intent);
    }
}