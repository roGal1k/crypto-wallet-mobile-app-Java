package com.cuttlesystems.cuttlewallet;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.cuttlesystems.cuttlewallet.R;
import com.cuttlesystems.cuttlewallet.ui.dashboard.DashboardFragment;
import com.cuttlesystems.cuttlewallet.ui.home.HomeFragment;
import com.cuttlesystems.cuttlewallet.ui.notifications.NotificationsFragment;
import com.google.android.material.navigation.NavigationBarView;

public class NaviGate implements NavigationBarView.OnItemSelectedListener {

    HomeFragment homeFragment;
    DashboardFragment dashboardFragment;
    NotificationsFragment notificationsFragment;

    FragmentManager fragmentManager;

    public NaviGate(FragmentManager manager){
        fragmentManager = manager;
        homeFragment = new HomeFragment();
        dashboardFragment = new DashboardFragment();
        notificationsFragment = new NotificationsFragment();
    }

    public void goHomeFragment()
    {
        fragmentManager.beginTransaction().replace(
                R.id.frame_layout, homeFragment).commit();
    }


    //public DashboardFragment getDashboardFragment(){
    //    return dashboardFragment;

    //}

    public HomeFragment getHomeFragment(){
        return homeFragment;
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //case R.id.navigation_dashboard:
            //    fragmentManager.beginTransaction().replace(
            //            R.id.frame_layout, dashboardFragment).commit();
            //    return true;
            case R.id.navigation_wallet:
                fragmentManager.beginTransaction().replace(
                        R.id.frame_layout, homeFragment).commit();
                return true;
            case R.id.navigation_support:
                fragmentManager.beginTransaction().replace(
                        R.id.frame_layout, notificationsFragment).commit();
                return true;
            default:
                return true;
        }
    }
}
