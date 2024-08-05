package com.newmview.wifi.helper;

/**
 * Created by functionapps on 10/30/2018.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

import androidx.core.view.ViewCompat;

public class BottomNavigationViewHelper extends CoordinatorLayout.Behavior<BottomNavigationView> {


    public BottomNavigationViewHelper() {
        super();

        Log.i("Bottom", "M called from main class");

    }

    public BottomNavigationViewHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, BottomNavigationView child, View dependency) {
        boolean dependsOn = dependency instanceof FrameLayout;
        Log.i("Bottom", "On layout depends" + " " + dependsOn);

        return dependsOn;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child,
                                       @NonNull View directTargetChild, @NonNull View target, int axes, int type) {

        return  type== ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child, @NonNull View target, int dx,
                                  int dy, @NonNull int[] consumed, int type) {
        if (dy < 0) {
            Log.i("Bottom", "On nested pre scroll y<0");


            showBottomNavigationView(child);
        } else if (dy > 0) {
            Log.i("Bottom", "On nested pre scroll y>0");

            hideBottomNavigationView(child);
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, BottomNavigationView child, View directTargetChild, View target, int nestedScrollAxes) {
        System.out.println("onstart nested control called");

        Log.i("Bottom", "On nested scroll");


        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;

    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, BottomNavigationView child, View target, int dx, int dy, int[] consumed) {
        System.out.println("onnestedpre control called");
        if (dy < 0) {
            Log.i("Bottom", "On nested pre scroll y<0");


            showBottomNavigationView(child);
        } else if (dy > 0) {
            Log.i("Bottom", "On nested pre scroll y>0");

            hideBottomNavigationView(child);
        }
    }
    private void hideBottomNavigationView(BottomNavigationView view) {
        Log.i("Bottom", "On nested pre scroll y>0");

        view.animate().translationY(view.getHeight());
    }

    private void showBottomNavigationView(BottomNavigationView view) {
        Log.i("Bottom", "show bottom navigation view");


        view.animate().translationY(0);
    }


    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShifting(false);
               // item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());

            }


        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }
}
