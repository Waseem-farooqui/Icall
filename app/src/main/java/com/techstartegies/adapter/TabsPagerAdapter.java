package com.techstartegies.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.techstartegies.model.ActiveDoctorFragment;
import com.techstartegies.model.InactiveDoctorFragment;
import com.techstartegies.model.PendingDoctorFragment;

/**
 * Created by Waseem on 18-Nov-15.
 * This class will provide the Fragment views to the Tab
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                // Active Doctors fragment activity
                return new ActiveDoctorFragment();
            case 1:
                // Pending Doctors activity
                return new PendingDoctorFragment();
            case 2:
                // Inactive Doctor activity
                return new InactiveDoctorFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
