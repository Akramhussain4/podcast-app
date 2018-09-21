package com.hussain.podcastapp.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.hussain.podcastapp.R;
import com.hussain.podcastapp.customview.TransparentLoadAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {

    private static final String TAG = BaseActivity.class.getName();

    private TransparentLoadAnimation loadAnimation;
    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    protected final void onCreate(Bundle savedInstanceState, int resourceLayout) {
        super.onCreate(savedInstanceState);
        setContentView(resourceLayout);
        ButterKnife.bind(this);
        initProgress();
        setUpToolBar();
    }

    private void initProgress() {
        loadAnimation = new TransparentLoadAnimation(this);
    }

    protected void setUpToolBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mToolbar.setTitleTextColor(Color.WHITE);
        }
        onToolBarSetUp(mToolbar, getSupportActionBar());
    }

    public abstract void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showAnimation(false);
    }
}
