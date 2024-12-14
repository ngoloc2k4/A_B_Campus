package vn.lobie.campus.activities;

import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.textfield.TextInputLayout;
import vn.lobie.campus.R;
import vn.lobie.campus.database.DBHelper;
import vn.lobie.campus.utils.ValidationUtils;

public class RegisterActivity extends BaseActivity {
    private TextInputLayout layoutUsername;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutConfirmPassword;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        dbHelper = new DBHelper(this);
        setupToolbar(getString(R.string.register), true);
        initViews();
    }

    private void initViews() {
        layoutUsername = findViewById(R.id.layoutUsername);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);

        findViewById(R.id.btnRegister).setOnClickListener(v -> attemptRegistration());
        findViewById(R.id.txtBackToLogin).setOnClickListener(v -> finish());
    }

    private void attemptRegistration() {
        if (!validateInputs()) return;

        String username = layoutUsername.getEditText().getText().toString().trim();
        String email = layoutEmail.getEditText().getText().toString().trim();
        String password = layoutPassword.getEditText().getText().toString();

        showLoading();
        new Thread(() -> {
            long userId = dbHelper.registerUser(username, password, email);
            handler.post(() -> {
                hideLoading();
                if (userId != -1) {
                    showMessage(getString(R.string.registration_successful));
                    finish();
                } else {
                    showError(getString(R.string.registration_failed));
                }
            });
        }).start();
    }

    private boolean validateInputs() {
        boolean isValid = ValidationUtils.validateRequired(layoutUsername) &&
                         ValidationUtils.validateEmail(layoutEmail) &&
                         ValidationUtils.validatePassword(layoutPassword);

        String password = layoutPassword.getEditText().getText().toString();
        String confirmPassword = layoutConfirmPassword.getEditText().getText().toString();

        if (!password.equals(confirmPassword)) {
            layoutConfirmPassword.setError(getString(R.string.passwords_dont_match));
            isValid = false;
        } else {
            layoutConfirmPassword.setError(null);
        }

        return isValid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
