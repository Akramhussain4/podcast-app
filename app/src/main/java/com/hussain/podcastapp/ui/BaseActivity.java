package com.hussain.podcastapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hussain.podcastapp.IBaseView;
import com.hussain.podcastapp.customview.TransparentLoadAnimation;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {

    private static final String TAG = PodcastListFragment.class.getName();

    private TransparentLoadAnimation loadAnimation;

    protected final void onCreate(Bundle savedInstanceState, int resourceLayout) {
        super.onCreate(savedInstanceState);
        setContentView(resourceLayout);
        ButterKnife.bind(this);
        initProgress();
    }

    private void initProgress() {
        loadAnimation = new TransparentLoadAnimation(this);
    }

    @Override
    public void showAnimation(boolean show) {
        synchronized ("SHOW_LOADER_ANIMATION") {
            runOnUiThread((() -> {
                if (loadAnimation == null) {
                    Log.d(TAG, "This progress animation is null");
                    return;
                }
                if (show) {
                    loadAnimation.showProgress();
                } else {
                    loadAnimation.hideProgress();
                }

            }));
        }
    }
}
