package hfu.puigrodr.cityarounder.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import hfu.puigrodr.cityarounder.models.Location;
import hfu.puigrodr.cityarounder.models.Tour;

/**
 * Created by Alberto on 01.02.2015.
 * abstract level of receiving and updating data to couchDB.
 * Http Requests are done in QueryBuilder
 */
public class Repository {

    public final static String urlServer = "cityarounder.iriscouch.com";

    private Context mContext;

    public Repository(Context context){
        mContext = context;
    }

    /**
     * @return String[] a list of city names
     */
    public String[] findAllCities() {

        String[] cities = null;

        try {

            JSONObject response = this.query("GET", "tours", "_design/tours/_view/by_city", "group_level=1", null);
            JSONArray objects = response.getJSONArray("rows");

            int length = objects.length();
            cities = new String[length];

            for(int i=0; i<length; i++) {

                JSONObject object = objects.getJSONObject(i);
                cities[i] = object.getString("key");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Fehler abfangen
        if(cities == null){
            cities = new String[0];
        }

        return cities;
    }

    /**
     * @return Tour[] all Tours in database
     */
    public Tour[] findAllTours() {

        Tour[] tours = null;

        try {

            JSONObject response = this.query("GET", "tours", "_design/tours/_view/by_title", null, null);
            JSONArray objects = response.getJSONArray("rows");

            int length = objects.length();
            tours = new Tour[length];

            for(int i=0; i<length; i++) {

                JSONObject object = objects.getJSONObject(i);
                String title = object.getString("key");
                String id = object.getString("id");
                String city = object.getString("value");

                Tour tour = new Tour(id, title);
                tour.setCity(city);
                tours[i] = tour;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Fehler abfangen
        if(tours == null){
            tours = new Tour[0];
        }

        return tours;
    }

    /**
     * @param userId the IDs of the tours
     * @return list of tours => Tour objects
     */
    public Tour[] findToursByUser(String userId){

        Tour[] tours = null;

        try {

            JSONObject response = this.query("GET", "tours", "_design/tours/_view/by_user", "key=\"" + userId + "\"", null);
            JSONArray objects = response.getJSONArray("rows");

            int length = objects.length();
            tours = new Tour[length];

            for(int i=0; i<length; i++) {

                JSONObject object = objects.getJSONObject(i);
                String id = object.getString("id");
                String title = object.getString("value");

                Tour tour = new Tour(id, title);
                //Stadt?
                tours[i] = tour;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Fehler abfangen
        if(tours == null){
            tours = new Tour[0];
        }

        return tours;
    }

    /**
     * @param city the name of the city
     * @return list of tours => Tour objects
     */
    public Tour[] findToursByCity(String city){

        Tour[] tours = null;

        try {

            JSONObject response = this.query("GET", "tours", "_design/tours/_view/by_city", "reduce=false&key=\"" + city + "\"", null);
            JSONArray objects = response.getJSONArray("rows");

            int length = objects.length();
            tours = new Tour[length];

            for(int i=0; i<length; i++) {

                JSONObject object = objects.getJSONObject(i);
                String id = object.getString("id");
                String title = object.getString("value");

                Tour tour = new Tour(id, title);
                tours[i] = tour;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Fehler abfangen
        if(tours == null){
            tours = new Tour[0];
        }

        return tours;
    }

    /**
     * @param tourID the IDs of the tour
     * @return list of locations => titles
     */
    public Location[] findLocationsByTour(String tourID){

        Location[] locations = null;

        try {

            JSONObject response = this.query("GET", "locations/_design/locations", "_view/by_tour", "key=\"" + tourID + "\"", null);
            JSONArray objects = response.getJSONArray("rows");

            int length = objects.length();
            locations = new Location[length];

            for(int i=0; i<length; i++) {

                JSONObject object = objects.getJSONObject(i);
                JSONObject doc = object.getJSONObject("value");

                String id = doc.getString("_id");
                String title = doc.getString("title");
                JSONArray arrDouble = doc.getJSONArray("latlng");
                LatLng latlng = new LatLng(arrDouble.getDouble(0), arrDouble.getDouble(1));
                String description = doc.getString("description");
                String imageId = doc.getString("imageId");
                String category = doc.getString("category");

                Location location = new Location(id, title);
                location.setLatlng(latlng);
                location.setDescription(description);
                location.setImageId(imageId);
                location.setCategory(category);

                locations[i] = location;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Fehler abfangen
        if(locations == null){
            locations = new Location[0];
        }

        return locations;
    }

    /**
     * @param city the name of the city
     * @return Location[] location objects
     */
    public Location[] findLocationsByCity(String city){

        ArrayList<Location> locations = new ArrayList<>();

        Tour[] tours = this.findToursByCity(city);

        for(Tour tour : tours){

            Location[] locationsPerTour = this.findLocationsByTour(tour.getId());

            if(locationsPerTour.length > 0){
                Collections.addAll(locations, locationsPerTour);
            }
        }

        return locations.toArray(new Location[locations.size()]);
    }


    /**
     * @param category the name of the category
     * @return Location[] location objects
     */
    public Location[] findLocationsByCategory(String category){

        Location[] locations = null;

        try {

            String encoded_category = URLEncoder.encode(category, "UTF-8");
            JSONObject response = this.query("GET", "locations/_design/locations", "_view/by_category", "key=\"" + encoded_category + "\"", null);
            JSONArray objects = response.getJSONArray("rows");

            int length = objects.length();
            locations = new Location[length];

            for(int i=0; i<length; i++) {

                JSONObject object = objects.getJSONObject(i);
                JSONObject doc = object.getJSONObject("value");

                String id = doc.getString("_id");
                String title = doc.getString("title");
                JSONArray arrDouble = doc.getJSONArray("latlng");
                LatLng latlng = new LatLng(arrDouble.getDouble(0), arrDouble.getDouble(1));
                String description = doc.getString("description");
                String imageId = doc.getString("imageId");

                Location location = new Location(id, title);
                location.setLatlng(latlng);
                location.setDescription(description);
                location.setImageId(imageId);
                location.setCategory(category);

                locations[i] = location;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Fehler abfangen
        if(locations == null){
            locations = new Location[0];
        }

        return locations;
    }

    /**
     * gets base64 encoded image from couchdb and turns it into bitmap
     * @param imageId the image id stored in images
     * @return Bitmap bitmap final bitmap
     */
    public Bitmap findImageById(String imageId){

        Bitmap bitmap = null;

        try {

            JSONObject response = this.query("GET", "images", imageId, "attachments=true", null);
            JSONObject attachment = response.getJSONObject("_attachments");
            JSONObject image = attachment.getJSONObject(imageId + ".jpg");
            String base64 = image.getString("data");

            byte[] decodeByte = Base64.decode(base64, 0);
            bitmap = BitmapFactory.decodeByteArray(decodeByte, 0, decodeByte.length);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * uploads a tour to couchdb
     * @param tour tour object with newly generated id
     */
    public void addTour(Tour tour){

        try {

            JSONObject tourObj = new JSONObject();
            tourObj.put("city", tour.getCity());
            tourObj.put("title", tour.getTitle());
            tourObj.put("user", tour.getUser());

            this.query("PUT", "tours", tour.getId(), null, tourObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * uploads a location to couchdb
     * @param location location object with newly generated id
     */
    public void addLocation(Location location){

        try {

            JSONObject locationObj = new JSONObject();
            locationObj.put("tour", location.getTour());
            locationObj.put("description", location.getDescription());
            locationObj.put("title", location.getTitle());
            locationObj.put("category", location.getCategory());
            locationObj.put("imageId", location.getImageId());

            LatLng latlng = location.getLatlng();
            JSONArray arrLatLng = new JSONArray();
            arrLatLng.put(latlng.latitude);
            arrLatLng.put(latlng.longitude);
            locationObj.put("latlng", arrLatLng);

            this.query("PUT", "locations", location.getId(), null, locationObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * saves image base64 encoded in couchdb
     */
    public void addImage(String imageId, Bitmap bitmap){

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String mimeType = "image/jpeg";

            JSONObject imageContent = new JSONObject();
            imageContent.put("content_type", mimeType);
            imageContent.put("data", base64);

            JSONObject imageData = new JSONObject();
            imageData.put(imageId + ".jpg", imageContent);

            JSONObject imageObj = new JSONObject();
            imageObj.put("_attachments", imageData);

            this.query("PUT", "images", imageId, null, imageObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * executes the final request by calling QueryBuilder class
     * @param method "GET", "POST" or "UPDATE"
     * @param database whether to search in cities, tours, locations
     * @param document the individual ID if necessary
     * @param params options to handle
     * @return JSONObject the information by response
     */
    public JSONObject query(String method, String database, String document, String params, JSONObject body) throws JSONException {

        QueryBuilder queryBuilder = new QueryBuilder(urlServer, mContext);
        queryBuilder.setMethod(method);

        String urlPath = "/";
        if(!(database == null || database.equals(""))) {
            urlPath += database + "/";
        }
        if(!(document == null || document.equals(""))) {
            urlPath += document + "/";
        }
        if(!(params == null || params.equals(""))) {
            urlPath += "?" + params;
        }

        queryBuilder.setUrlPath(urlPath);

        if(body != null){
            queryBuilder.setBody(body.toString());
        }

        String response = queryBuilder.execute();
        response = (response == null) ? "{}" : response;

        return new JSONObject(response);
    }
}