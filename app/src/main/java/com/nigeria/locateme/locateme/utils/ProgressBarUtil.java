package com.nigeria.locateme.locateme.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;

/**
 * Created by Williamz on 5/10/2017.
 */

public class ProgressBarUtil {
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final Context context, final boolean show, final View mProgressView, final View mFormView) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
                // for very easy animations. If available, use these APIs to fade-in
                // the custom_progress_bar spinner.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    mFormView.animate().setDuration(Constants.PROGRESS_ANIM_TIME).alpha(
                            show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });

                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    mProgressView.animate().setDuration(Constants.PROGRESS_ANIM_TIME).alpha(
                            show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });
                } else {
                    // The ViewPropertyAnimator APIs are not available, so simply show
                    // and hide the relevant UI components.
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            }
        });
    }
}
