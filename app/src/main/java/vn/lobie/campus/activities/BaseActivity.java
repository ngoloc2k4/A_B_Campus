package vn.lobie.campus.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import vn.lobie.campus.R;
import vn.lobie.campus.utils.DialogUtils;
import vn.lobie.campus.utils.WeakHandler;

public abstract class BaseActivity extends AppCompatActivity {
    protected WeakHandler<BaseActivity> handler;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new WeakHandler<>(this);
    }

    protected void setupToolbar(String title, boolean showBack) {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
                getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
            }
        }
    }

    protected void showError(String message) {
        DialogUtils.showError(this, message);
    }

    protected void showLoading() {
        DialogUtils.showLoading(this);
    }

    protected void hideLoading() {
        DialogUtils.hideLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks();
        }
    }
}
