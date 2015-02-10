package hfu.puigrodr.cityarounder.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import hfu.puigrodr.cityarounder.models.Location;

public class LocationListFragment extends ListFragment {

    private String[] mTitles;
    private OnListItemClickListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(inflater.getContext(), android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);

        // before onCreateView the setLocationList method is called from acitivity, setting mTitles
        if(mTitles != null){

            mAdapter.addAll(mTitles);
            mAdapter.notifyDataSetChanged();
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnListItemClickListener) activity;
        } catch (ClassCastException e) {

            throw new ClassCastException(activity.toString() + " must implement OnListItemClickListener");
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id){

        mListener.onListItemClick(position);
    }

    public interface OnListItemClickListener {
        public void onListItemClick(int position);
    }

    public void setLocationList(Location[] locations){

        mTitles = new String[locations.length];

        for(int i=0; i<locations.length; i++){
            mTitles[i] = locations[i].getTitle();
        }
    }
}