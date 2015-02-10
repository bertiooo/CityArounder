package hfu.puigrodr.cityarounder.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import hfu.puigrodr.cityarounder.R;
import hfu.puigrodr.cityarounder.fragments.CustomListFragment;

/**
 * Second main screen
 * exposes lists in different categories to choose from
 */
public class ViewPagerActivity extends ActionBarActivity {

    public static final String[] TABS = {"Städte", "Kategorien", "Touren", "Meine Touren"};
    private boolean mShowToast = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        ListPageAdapter listPageAdapter = new ListPageAdapter(getSupportFragmentManager());

        ViewPager viewPager  = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(listPageAdapter);

        //Set Color
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.rgb(75, 145, 219));

        // Get page item
        Intent intent = getIntent();
        int pageItem = intent.getIntExtra("pageItem", 0);
        viewPager.setCurrentItem(pageItem);

        //if new location was created, make a toast
        String created = intent.getStringExtra("toast_created");
        if(mShowToast && created != null){
            Toast.makeText(this, "Ort erfolgreich erstellt.", Toast.LENGTH_LONG).show();
            mShowToast = false;
        }
    }

    public class ListPageAdapter extends FragmentPagerAdapter {

        public ListPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            CustomListFragment fragment = new CustomListFragment();

            Bundle args = new Bundle();
            args.putInt("page_position", position);

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return TABS.length;
        }

        @Override
        public CharSequence getPageTitle(int position){

            if(position < TABS.length){
                return TABS[position];
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_pager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

            Intent intent = new Intent(this, FormLocationActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
