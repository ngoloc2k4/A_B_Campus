package vn.lobie.campus.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import androidx.appcompat.app.AlertDialog;
import vn.lobie.campus.R;

public class DialogUtils {
    private static Dialog loadingDialog;

    public static void showLoading(Context context) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }

        loadingDialog = new Dialog(context);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.getWindow().setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        loadingDialog.show();
    }

    public static void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public static void showError(Context context, String message) {
        new AlertDialog.Builder(context)
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show();
    }

    public static void showConfirmation(Context context, String message, 
                                      Runnable onConfirm) {
        new AlertDialog.Builder(context)
            .setTitle(R.string.confirm)
            .setMessage(message)
            .setPositiveButton(R.string.yes, (dialog, which) -> onConfirm.run())
            .setNegativeButton(R.string.no, null)
            .show();
    }
}
