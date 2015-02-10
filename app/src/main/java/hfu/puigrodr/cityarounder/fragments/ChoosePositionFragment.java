package hfu.puigrodr.cityarounder.fragments;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import hfu.puigrodr.cityarounder.R;

/**
 * fragment for choosing position of location
 */
public class ChoosePositionFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mGoogleMap;
    private LatLng mPosition;

    public ChoosePositionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_position, container, false);

        Bundle args = getArguments();
        mPosition = new LatLng(args.getDouble("latitude"), args.getDouble("longitude"));

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.formMapLocation);
        mapFragment.getMapAsync(this);

        return view;
    }

    public void onDestroyView(){

        super.onDestroyView();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.formMapLocation);
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        ft.remove(mapFragment);
        ft.commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this);

        CameraPosition camera = new CameraPosition(mPosition, 16, 0, 0);
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
        onMapClick(mPosition);

        mGoogleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        mPosition = latLng;

        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions().position(latLng));
    }

    public LatLng getPosition(){
        return mPosition;
    }
}
