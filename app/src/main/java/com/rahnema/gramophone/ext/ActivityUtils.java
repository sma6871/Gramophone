package com.rahnema.gramophone.ext;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.rahnema.gramophone.R;

/**
 * @author Payam1991 on 9/18/2017.
 */

public class ActivityUtils {
    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId, boolean addToBackStack) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_player, R.anim.exit_player, R.anim.pop_enter, R.anim.pop_exit);


        transaction.replace(frameId, fragment);

        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }
}
