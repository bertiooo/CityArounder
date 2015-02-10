package hfu.puigrodr.cityarounder.controller;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by Alberto on 02.02.2015.
 * used to identify the user and get tours that are only referenced / created by him.
 */
public class UserIdentifier {

    private String userId;

    public UserIdentifier(Context context){

        String keyName = "hfu.puigrodr.cityarounder.USER_IDENTIFIER";
        SharedPreferences sharedPref = context.getSharedPreferences(keyName, Context.MODE_PRIVATE);

        /* reads ID, otherwise if it doesn't exist, creates new one */
        userId = sharedPref.getString("user_id", "NO_ID");
        if(userId.equals("NO_ID")){
            String newId = UUID.randomUUID().toString();
            userId = newId;

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("user_id", newId);
            editor.apply();
        }
    }

    public String getId(){
        return this.userId;
    }
}
