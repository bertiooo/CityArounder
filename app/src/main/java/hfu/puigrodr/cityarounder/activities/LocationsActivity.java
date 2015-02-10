package hfu.puigrodr.cityarounder.activities;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import hfu.puigrodr.cityarounder.R;
import hfu.puigrodr.cityarounder.controller.Repository;
import hfu.puigrodr.cityarounder.fragments.LocationListFragment;
import hfu.puigrodr.cityarounder.models.Location;

public class LocationsActivity extends ActionBarActivity implements OnMapReadyCallback,
        ActionBar.OnNavigationListener, LocationListFragment.OnListItemClickListener, GoogleMap.OnMarkerClickListener{

    private SupportMapFragment mMapFragment;
    private LocationListFragment mListFragment;

    private GoogleMap mGoogleMap;
    private Location[] mLocations;
    private HashMap<Marker, Integer> mHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);


        Intent intent = getIntent();
        Bundle arguments = intent.getBundleExtra("locations");

        if(arguments != null) {

            //set spinner
            SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.locations_views, R.layout.spinner_item);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(spinnerAdapter, this);

        } else { // nur Karte -> von Home screen

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.dragView);
            layout.setVisibility(RelativeLayout.GONE);
        }

        //gmaps
        initializeGoogleMapsFragment();

        if(mLocations == null && arguments != null){

            String searchBy = arguments.getString("tab_name");
            String parentKey = arguments.getString("reference_id");

            // Get location Objects
            QueryTask task = new QueryTask();
            task.execute(searchBy, parentKey);

        } else {

            initializeLocations();
        }
    }

    private class QueryTask extends AsyncTask<String, Void, Location[]> {

        @Override
        protected Location[] doInBackground(String... params) {

            String searchBy = params[0];
            String parentKey = params[1];

            Repository repository = new Repository(LocationsActivity.this);
            switch(searchBy){
                case "Städte":
                    mLocations = repository.findLocationsByCity(parentKey);
                    break;
                case "Touren":
                case "Meine Touren":
                    mLocations = repository.findLocationsByTour(parentKey);
                    break;
                case "Kategorien":
                    mLocations = repository.findLocationsByCategory(parentKey);
                    break;
            }

            return mLocations;
        }

        @Override
        protected void onPostExecute(Location[] locations){

            initializeLocations();
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {

        switch(itemPosition){
            case 0:
                initializeGoogleMapsFragment();
                break;
            case 1:
                initializeListFragment();
                break;
        }

        return true;
    }

    /**
     * initializes Google Maps Fragment
     */
    public void initializeGoogleMapsFragment(){

        if(mMapFragment == null){

            // initialize GoogleMaps
            CameraPosition initCamera = new CameraPosition(new LatLng(47.9518, 8.4979), 14, 0, 0);

            GoogleMapOptions options = new GoogleMapOptions();
            options.camera(initCamera);

            mMapFragment = SupportMapFragment.newInstance(options);
            mMapFragment.getMapAsync(this);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentWrapper, mMapFragment);

        fragmentTransaction.commit();
    }

    /**
     * initializes List Fragment
     */
    public void initializeListFragment(){

        if(mListFragment == null){

            mListFragment = new LocationListFragment();
            mListFragment.setLocationList(mLocations);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentWrapper, mListFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mGoogleMap = map;
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setMyLocationEnabled(true);
        initializeLocations();
    }

    //whether googleMap is earlier ready than repository with asynctask
    private void initializeLocations(){

        if(mGoogleMap != null && mLocations != null){

            mHashMap = new HashMap<>();

            //CAREFUL!!!
            boolean firstLocationSet = false;
            for(int i=0; i<mLocations.length; i++){

                if(mLocations[i] != null){

                    LatLng latlng = mLocations[i].getLatlng();

                    if(latlng != null){

                        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latlng.latitude, latlng.longitude)));

                        //remember marker position
                        mHashMap.put(marker, i);

                        //set camera position
                        if(!firstLocationSet){
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                            firstLocationSet = true;
                        }
                    }
                }
            }

            //erste Location in InfoFenster laden
            slideUpContent(mLocations[0]);
        }
    }

    @Override
    public void onListItemClick(int position) {
        slideUpContent(mLocations[position]);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        int position = mHashMap.get(marker);

        slideUpContent(mLocations[position]);
        return false;
    }

    private void slideUpContent(Location location){

        //set location information
        TextView title = (TextView) findViewById(R.id.locationTitle);
        title.setText(location.getTitle());

        TextView category = (TextView) findViewById(R.id.locationCategory);
        category.setText("Kategorie: " + location.getCategory());

        TextView description = (TextView) findViewById(R.id.locationDescription);
        description.setText(location.getDescription());

        String imageId = location.getImageId();
        if(imageId != null && !imageId.equals("")){

            LoadImageTask task = new LoadImageTask();
            task.execute(imageId);
        }

        // start sliding up
        /*
        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        int slideablePanelHeight = 100;
        int animationDuration = 800;

        SlidingUpPanelAnimation animation = new SlidingUpPanelAnimation(slidingUpPanelLayout, slideablePanelHeight, animationDuration);
        slidingUpPanelLayout.startAnimation(animation);
        */
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            String imageId = params[0];

            Repository repository = new Repository(LocationsActivity.this);
            return repository.findImageById(imageId);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){

            ImageView imageView = (ImageView) findViewById(R.id.locationImage);
            imageView.setImageBitmap(bitmap);
        }
    }
}
