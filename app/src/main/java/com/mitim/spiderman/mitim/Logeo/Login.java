package com.mitim.spiderman.mitim.Logeo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.mitim.spiderman.mitim.R;

/**
 * Created by Spiderman on 7/31/2017.
 */

public class Login extends AppCompatActivity implements TabLayout.OnTabSelectedListener
{
    //This is our tablayout
    private TabLayout tabLayout;
    //This is our viewPager
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        iniciarTab();
    }

    private void iniciarTab()
    {
        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Ingresar"));
        tabLayout.addTab(tabLayout.newTab().setText("Registrarse"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //Below lines of code shows you where to add that line
        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
