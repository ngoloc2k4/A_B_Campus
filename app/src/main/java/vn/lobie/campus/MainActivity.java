package vn.lobie.campus;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.fragment.app.Fragment;
import vn.lobie.campus.activities.BaseActivity;
import vn.lobie.campus.database.DBHelper;
import vn.lobie.campus.utils.DBStatic;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import vn.lobie.campus.fragments.HomeFragment;
import vn.lobie.campus.fragments.TransactionsFragment;
import vn.lobie.campus.fragments.BudgetFragment;
import vn.lobie.campus.fragments.ChartsFragment;

public class MainActivity extends BaseActivity {
    private static final String KEY_SELECTED_TAB = "selected_tab";
    private int currentTabId = R.id.nav_home;
    private BottomNavigationView bottomNav;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState != null) {
            currentTabId = savedInstanceState.getInt(KEY_SELECTED_TAB, R.id.nav_home);
        }
        
        initViews();
        setupNavigation();
        checkUserSession();
    }

    private void checkUserSession() {
        if (DBStatic.getUserId() == -1) {
            // No active session, go to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        loadUserData();
    }

    private void loadUserData() {
        showLoading();
        new Thread(() -> {
            DBHelper dbHelper = new DBHelper(this);
            dbHelper.loadUserFinancialData(DBStatic.getUserId());
            handler.post(() -> {
                hideLoading();
                loadInitialFragment();
            });
        }).start();
    }

    private void initViews() {
        bottomNav = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            String title = "";
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
                title = "Home";
            } else if (itemId == R.id.nav_transactions) {
                fragment = new TransactionsFragment();
                title = "Transactions";
            } else if (itemId == R.id.nav_budget) {
                fragment = new BudgetFragment();
                title = "Budget";
            } else if (itemId == R.id.nav_charts) {
                fragment = new ChartsFragment();
                title = "Charts";
            }

            currentTabId = itemId;
            return loadFragment(fragment, title);
        });
    }

    private boolean loadFragment(Fragment fragment, String title) {
        if (fragment != null) {
            getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.content_frame, fragment)
                .commit();
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
            return true;
        }
        return false;
    }

    private void loadInitialFragment() {
        loadFragment(new HomeFragment(), "Home");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_TAB, currentTabId);
    }

    @Override
    public void onBackPressed() {
        if (bottomNav.getSelectedItemId() == R.id.nav_home) {
            super.onBackPressed();
        } else {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }
}