package vn.lobie.campus.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import vn.lobie.campus.R;
import vn.lobie.campus.database.DBHelper;
import vn.lobie.campus.models.Transaction;
import vn.lobie.campus.utils.DialogUtils;
import vn.lobie.campus.utils.DataRefreshManager;

public class TransactionDetailActivity extends BaseActivity {
    private static final String EXTRA_TRANSACTION_ID = "transaction_id";
    private DBHelper dbHelper;
    private Transaction currentTransaction;
    private int transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        
        dbHelper = new DBHelper(this);
        setupToolbar(getString(R.string.transaction_detail), true);
        
        transactionId = getIntent().getIntExtra(EXTRA_TRANSACTION_ID, -1);
        if (transactionId != -1) {
            loadTransactionDetails();
        } else {
            finish();
        }
    }

    private void loadTransactionDetails() {
        showLoading();
        new Thread(() -> {
            currentTransaction = dbHelper.getTransactionById(transactionId);
            handler.post(() -> {
                hideLoading();
                if (currentTransaction != null) {
                    displayTransaction();
                } else {
                    showError(getString(R.string.transaction_not_found));
                    finish();
                }
            });
        }).start();
    }

    private void displayTransaction() {
        ((TextView) findViewById(R.id.txtAmount)).setText(
            String.format("$%.2f", currentTransaction.getAmount()));
        ((TextView) findViewById(R.id.txtCategory)).setText(
            currentTransaction.getCategory());
        ((TextView) findViewById(R.id.txtNote)).setText(
            currentTransaction.getTitle());
        ((TextView) findViewById(R.id.txtDate)).setText(
            currentTransaction.getDate());
            
        int colorRes = currentTransaction.getType().equals("income") ? 
            R.color.green_light : R.color.red_light;
        findViewById(R.id.typeIndicator).setBackgroundResource(colorRes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transaction_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_delete) {
            confirmDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDelete() {
        DialogUtils.showConfirmation(this, 
            getString(R.string.confirm_delete_transaction),
            this::deleteTransaction);
    }

    private void deleteTransaction() {
        showLoading();
        new Thread(() -> {
            boolean success = dbHelper.deleteTransaction(transactionId);
            handler.post(() -> {
                hideLoading();
                if (success) {
                    DataRefreshManager.getInstance().notifyDataChanged();
                    showMessage(getString(R.string.transaction_deleted));
                    finish();
                } else {
                    showError(getString(R.string.delete_failed));
                }
            });
        }).start();
    }
}
