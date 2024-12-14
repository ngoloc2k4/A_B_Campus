package vn.lobie.campus.utils;

import android.text.TextUtils;
import android.util.Patterns;
import com.google.android.material.textfield.TextInputLayout;

public class ValidationUtils {
    public static boolean validateRequired(TextInputLayout inputLayout) {
        String text = inputLayout.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            inputLayout.setError("This field is required");
            return false;
        }
        inputLayout.setError(null);
        return true;
    }

    public static boolean validateEmail(TextInputLayout inputLayout) {
        String email = inputLayout.getEditText().getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputLayout.setError("Invalid email address");
            return false;
        }
        inputLayout.setError(null);
        return true;
    }

    public static boolean validateAmount(TextInputLayout inputLayout) {
        try {
            double amount = Double.parseDouble(
                inputLayout.getEditText().getText().toString()
            );
            if (amount <= 0) {
                inputLayout.setError("Amount must be greater than 0");
                return false;
            }
            inputLayout.setError(null);
            return true;
        } catch (NumberFormatException e) {
            inputLayout.setError("Invalid amount");
            return false;
        }
    }

    public static boolean validatePassword(TextInputLayout inputLayout) {
        String password = inputLayout.getEditText().getText().toString();
        if (password.length() < 6) {
            inputLayout.setError("Password must be at least 6 characters");
            return false;
        }
        inputLayout.setError(null);
        return true;
    }
}
