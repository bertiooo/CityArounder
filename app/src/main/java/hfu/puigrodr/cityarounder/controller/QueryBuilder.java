package hfu.puigrodr.cityarounder.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import hfu.puigrodr.cityarounder.dialogs.ConnectivityErrorDialogFragment;

/**
 * Created by Alberto on 01.02.2015.
 * used to connect with iriscouch
 */
public class QueryBuilder{

    private String urlServer;
    private String method;
    private String urlPath;
    private String body;

    private boolean isConnected;

    public QueryBuilder(String urlServer, Context context) {

        this.urlServer = urlServer;
        this.body = "";
        this.isConnected = checkConnectivity(context);
    }

    // check internet connection
    private boolean checkConnectivity(Context context){

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {

            // display error
            FragmentActivity fragmentActivity = (FragmentActivity) context; // bad thing
            DialogFragment dialog = new ConnectivityErrorDialogFragment();
            dialog.show(fragmentActivity.getSupportFragmentManager(), "ConnectivityErrorDialogFragment");
            return false;
        }
    }

    public void setMethod(String method){
        this.method = method;
    }

    public void setUrlPath(String urlPath){
        this.urlPath = urlPath;
    }

    public void setBody(String body){
        this.body = body;
    }

    public String execute(){

        String result = null;

        if(this.isConnected) {

            try {

                String urlQuery = this.urlServer + this.urlPath;
                URL url = new URL("http://" + urlQuery);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(this.method);
                conn.setRequestProperty("Accept", "application/json");

                if(!this.body.equals("")){

                    conn.setDoOutput(true);
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(this.body);
                    out.close();
                }

                InputStream in = conn.getInputStream();
                result = IOUtils.toString(in);

                conn.disconnect();

            } catch(Exception e) {

                e.printStackTrace();
            }
        }

        return result;
    }
}
