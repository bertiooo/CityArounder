package hfu.puigrodr.cityarounder.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import hfu.puigrodr.cityarounder.R;
import hfu.puigrodr.cityarounder.controller.BitmapController;

/**
 * contains the general form to create a location
 */
public class FormLocationFragment extends Fragment {

    //Camera Usage
    private final static int REQUEST_CODE = 100;
    private Bitmap mBitmap;
    private String mImagePath;

    // Google Map Lite
    private MapView mMapView;
    private GoogleMap mMap;

    //restoring form values
    private String mTour;
    private String mCategory;
    private LatLng mLatLng;

    public FormLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_location, container, false);

        // initialize restoring values
        if(mTour != null){
            TextView tour = (TextView) view.findViewById(R.id.formTour);
            tour.setText(mTour);
        }

        if(mCategory != null){ // from backstack
            TextView category = (TextView) view.findViewById(R.id.formCategory);
            category.setText(mCategory);
        }

        // initialize Google Maps
        mMapView = (MapView) view.findViewById(R.id.formMapLite);
        mMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        mMap = mMapView.getMap();

        // current Location is found out by activity
        if(mLatLng == null){
            LatLng defaultPosition = new LatLng(47.9518, 8.4979);
            setGoogleMapsPosition(defaultPosition);
        } else { // from backstack
            setGoogleMapsPosition(mLatLng);
        }

        return view;
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();

        if(mBitmap != null){
            ImageView imageView = (ImageView) getView().findViewById(R.id.formImage);
            imageView.setImageBitmap(mBitmap);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void getPictureFromDevice(){

        Uri imageUri = createNewFileUri();
        mImagePath = imageUri.getPath();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, REQUEST_CODE);
    }

    private Uri createNewFileUri(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CityArounder");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("CityArounder", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {

                Toast.makeText(getActivity(), "Image saved to:\n" + mImagePath, Toast.LENGTH_LONG).show();

                ImageView imageView = (ImageView) getActivity().findViewById(R.id.formImage);
                mBitmap = BitmapController.getBitmapByPath(mImagePath, imageView);
                imageView.setImageBitmap(mBitmap);

                Button button = (Button) getActivity().findViewById(R.id.formImageButton);
                button.setText("Anderes Foto aufnehmen");
            }
        }
    }

    public Bitmap getBitmap(){
        return mBitmap;
    }

    public void setTour(String tour){
        this.mTour = tour;
    }

    public void setCategory(String category){
        this.mCategory = category;
    }

    public void setGoogleMapsPosition(LatLng latlng){

        mLatLng = latlng;

        if(mMap != null){
            mMap.addMarker(new MarkerOptions().position(latlng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        }
    }
}
