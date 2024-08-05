package com.newmview.wifi.SlidingTab;

/**
 * Created by Sharad Gupta on 12/2/2016.
 */

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.newmview.wifi.fragment.NeighboringCellFragment;
import com.newmview.wifi.fragment.NetworkMonitorFragment;
import com.newmview.wifi.fragment.Sim2NetworkAndCellInfo;
import com.newmview.wifi.fragment.ViewMapFragment;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    public NetworkMonitorFragment tab1;
    public ViewMapFragment tab2;
    private NeighboringCellFragment tab3;
    private Sim2NetworkAndCellInfo tab4;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }


    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            tab1 = new NetworkMonitorFragment();


            return tab1;
        }
        else    if(position==1)         // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            tab2 = new ViewMapFragment();

            return tab2;
        }
        else if(position==2)
        {
            tab3=new NeighboringCellFragment();
            return tab3;
        }
        else 
        {

            tab4=new Sim2NetworkAndCellInfo();
            return  tab4;
        }
        


    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

}

