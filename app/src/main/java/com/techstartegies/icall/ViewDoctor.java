package com.techstartegies.icall;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.techstartegies.adapter.TabsPagerAdapter;

public class ViewDoctor extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter tabsPagerAdapter;
    private ActionBar actionBar;
    CreateGroup cg;
    //
    private String[] tabs = { "Active Doctor", "Pending Doctor", "Inactive Doctor" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_doctor);

        viewPager = (ViewPager)findViewById(R.id.pager);
        actionBar = getActionBar();
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        //Inializations
        viewPager.setAdapter(tabsPagerAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //Adding Tabs
        for(String tab_name : tabs){
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }




}
