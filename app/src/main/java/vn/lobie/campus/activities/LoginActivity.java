package vn.lobie.campus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import vn.lobie.campus.MainActivity;
import vn.lobie.campus.R;
import vn.lobie.campus.database.DBHelper;
import vn.lobie.campus.utils.ValidationUtils;
import vn.lobie.campus.utils.SharedPrefsUtils;

public class LoginActivity extends BaseActivity {
    private TextInputLayout layoutEmail, layoutPassword;
    private CheckBox chkRememberMe;
    private DBHelper dbHelper;
    private SharedPrefsUtils prefsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        dbHelper = new DBHelper(this);
        prefsUtils = new SharedPrefsUtils(this);
        
        initViews();
        checkSavedCredentials();
    }

    private void initViews() {
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        chkRememberMe = findViewById(R.id.chkRememberMe);
        
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView txtRegister = findViewById(R.id.txtRegister);
        TextView txtForgotPassword = findViewById(R.id.txtForgotPassword);

        btnLogin.setOnClickListener(v -> attemptLogin());
        txtRegister.setOnClickListener(v -> startActivity(
            new Intent(this, RegisterActivity.class)
        ));
        txtForgotPassword.setOnClickListener(v -> handleForgotPassword());
    }

    private void checkSavedCredentials() {
        String savedEmail = prefsUtils.getSavedEmail();
        String savedPassword = prefsUtils.getSavedPassword();
        
        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            layoutEmail.getEditText().setText(savedEmail);
            layoutPassword.getEditText().setText(savedPassword);
            chkRememberMe.setChecked(true);
            attemptLogin();
        }
    }

    private void attemptLogin() {
        if (!validateInputs()) return;

        String email = layoutEmail.getEditText().getText().toString().trim();
        String password = layoutPassword.getEditText().getText().toString();

        showLoading();
        new Thread(() -> {
            boolean success = dbHelper.validateUser(email, password);
            handler.post(() -> {
                hideLoading();
                if (success) {
                    handleSuccessfulLogin(email, password);
                } else {
                    showError(getString(R.string.invalid_credentials));
                }
            });
        }).start();
    }

    private boolean validateInputs() {
        return ValidationUtils.validateEmail(layoutEmail) &&
               ValidationUtils.validateRequired(layoutPassword);
    }

    private void handleSuccessfulLogin(String email, String password) {
        if (chkRememberMe.isChecked()) {
            prefsUtils.saveCredentials(email, password);
        } else {
            prefsUtils.clearCredentials();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void handleForgotPassword() {
        // Implement password recovery logic
        showMessage(getString(R.string.feature_coming_soon));
    }
}