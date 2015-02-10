package hfu.puigrodr.cityarounder.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

import hfu.puigrodr.cityarounder.R;
import hfu.puigrodr.cityarounder.dialogs.ChooseCategoryDialogFragment;
import hfu.puigrodr.cityarounder.dialogs.ChooseTourDialogFragment;
import hfu.puigrodr.cityarounder.dialogs.NewTourDialogFragment;
import hfu.puigrodr.cityarounder.controller.Repository;
import hfu.puigrodr.cityarounder.controller.UserIdentifier;
import hfu.puigrodr.cityarounder.fragments.ChoosePositionFragment;
import hfu.puigrodr.cityarounder.fragments.FormLocationFragment;
import hfu.puigrodr.cityarounder.models.Location;
import hfu.puigrodr.cityarounder.models.Tour;

public class FormLocationActivity extends ActionBarActivity implements NewTourDialogFragment.NewTourDialogListener,
        ChooseTourDialogFragment.ChooseTourDialogListener, ChooseCategoryDialogFragment.ChooseCategoryDialogListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Tour mNewTour;
    private Location mNewLocation;
    private GoogleApiClient mGoogleApiClient;

    private FormLocationFragment mFormFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_location);

        // set general form fragment
        mFormFragment = new FormLocationFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.formFragmentContainer, mFormFragment, "formLocationFragment");
        fragmentTransaction.commit();

        // get location of the user
        buildGoogleApiClient();

        // initialize Tour and Location
        String newId = generateNewUUID();
        mNewTour = new Tour(newId, "newTourTitle");
        mNewTour.setUser(new UserIdentifier(this).getId());

        newId = generateNewUUID();
        mNewLocation = new Location(newId, "newLocationTitle");
        mNewLocation.setLatlng(new LatLng(47.9518, 8.4979)); // default position
    }

    private class QueryTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            Repository repository = new Repository(FormLocationActivity.this);

            // if new tour was created, send it over to couchdb
            if(!mNewTour.getTitle().equals("newTourTitle")){
                repository.addTour(mNewTour);
            }

            //Titel
            EditText titleView = (EditText) findViewById(R.id.formTitle);
            mNewLocation.setTitle(titleView.getText().toString());
            //Tour -> addNewTour oder chooseTour
            //Category -> chooseCategory
            //Beschreibung
            EditText descriptionView = (EditText) findViewById(R.id.formDescription);
            mNewLocation.setDescription(descriptionView.getText().toString());
            //Standort -> setPosition oder onCreate
            //Foto
            Bitmap bitmap = mFormFragment.getBitmap();
            if(bitmap != null){

                String imageId = generateNewUUID();
                mNewLocation.setImageId(imageId);
                repository.addImage(imageId, bitmap);
            } else {
                mNewLocation.setImageId(""); //ansonsten Fehler bei Übertragung zu CouchDB
            }

            repository.addLocation(mNewLocation);

            return null;
        }

        @Override
        protected void onPostExecute(Void empty){

            Intent intent = new Intent(FormLocationActivity.this, ViewPagerActivity.class);
            intent.putExtra("toast_created", "ok");

            startActivity(intent);
        }
    }

    /**
     * generates new IDs for couchdb
     * @return String new ID
     */
    public String generateNewUUID(){

        String newId = UUID.randomUUID().toString();
        newId = newId.replace("-", "");

        return newId;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    // ------------------------ Google API -> Device Location -------------------------------

    private void buildGoogleApiClient(){

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        android.location.Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            LatLng latlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mNewLocation.setLatlng(latlng);
            mFormFragment.setGoogleMapsPosition(latlng);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GoogleAPI", "Connection suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GoogleAPI", "Connection failed.");
    }

    // ------------------------ Google API END -------------------------------

    // ------------------------ Form Buttons -------------------------------

    public void sendFormular(View view){

        QueryTask task = new QueryTask();
        task.execute();
    }

    /**
     * reacts when in form the button Neue Tour is pressed
     * @param view button view
     */
    public void addNewTour(View view){

        DialogFragment dialog = new NewTourDialogFragment();
        dialog.show(getSupportFragmentManager(), "NewTourDialogFragment");
    }

    /**
     * reacts when in form the button Tour waehlen is pressed
     * @param view button view
     */
    public void chooseTour(View view){

        DialogFragment dialog = new ChooseTourDialogFragment();
        dialog.show(getSupportFragmentManager(), "ChooseTourDialogFragment");
    }

    /**
     * reacts when in form the button Kategorie waehlen is pressed
     * @param view button view
     */
    public void chooseCategory(View view){

        DialogFragment dialog = new ChooseCategoryDialogFragment();
        dialog.show(getSupportFragmentManager(), "ChooseCategoryDialogFragment");
    }

    public void setPosition(View view){

        ChoosePositionFragment fragment = (ChoosePositionFragment) getFragmentManager().findFragmentByTag("ChoosePositionFragment");
        LatLng latlng = fragment.getPosition();
        mNewLocation.setLatlng(latlng);

        getFragmentManager().popBackStack();
        mFormFragment.setGoogleMapsPosition(latlng);
    }

    /**
     * reacts when in form the button Standort waehlen is pressed
     * @param view button view
     */
    public void choosePosition(View view){

        // initialize map fragment
        ChoosePositionFragment mapFragment = new ChoosePositionFragment();

        Bundle args = new Bundle();
        args.putDouble("latitude", mNewLocation.getLatlng().latitude);
        args.putDouble("longitude", mNewLocation.getLatlng().longitude);
        mapFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.formFragmentContainer, mapFragment, "ChoosePositionFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * reacts when the user hits button Foto waehlen
     * @param view button view
     */
    public void getPicture(View view){

        mFormFragment.getPictureFromDevice();
    }

    // ------------------------ Form Buttons END -------------------------------

    // ------------------------ Dialog Buttons -------------------------------

    @Override
    public void onAddTourClick(String title, String city) {

        TextView textView = (TextView) findViewById(R.id.formTour);
        textView.setText(title);

        mNewTour.setCity(city);
        mNewTour.setTitle(title);
        mNewLocation.setTour(mNewTour.getId());

        mFormFragment.setTour(title);
    }

    public void onChooseTourClick(String tourId, String title){

        mNewTour.setTitle("newTourTitle"); //dirty hack, siehe oben
        mNewLocation.setTour(tourId);

        TextView textView = (TextView) findViewById(R.id.formTour);
        textView.setText(title);

        mFormFragment.setTour(title);
    }

    @Override
    public void onChooseCategory(String category) {

        mNewLocation.setCategory(category);

        TextView textView = (TextView) findViewById(R.id.formCategory);
        textView.setText(category);

        mFormFragment.setCategory(category);
    }

    // ------------------------ Dialog Buttons END -------------------------------
}
