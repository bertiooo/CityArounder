package hfu.puigrodr.cityarounder.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import hfu.puigrodr.cityarounder.R;

/**
 * Home Screen
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * reacts when one of the main buttons is tapped
     * @param view reference to the button widget
     */
    public void handleMenuButtons(View view){

        Intent intent = new Intent();

        switch(view.getId()){
            case R.id.main_button_staedte:
                intent.setClass(this, ViewPagerActivity.class);
                break;
            case R.id.main_button_mytours:
                intent.setClass(this, ViewPagerActivity.class);
                intent.putExtra("pageItem", 3);
                break;
            case R.id.main_button_demo:
                intent.setClass(this, FormLocationActivity.class);
                break;
            case R.id.main_button_map:
                intent.setClass(this, LocationsActivity.class);
                break;
        }

        startActivity(intent);
    }
}
