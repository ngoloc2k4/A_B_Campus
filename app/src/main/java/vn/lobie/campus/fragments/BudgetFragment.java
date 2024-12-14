package vn.lobie.campus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputLayout;
import vn.lobie.campus.utils.ValidationUtils;
import vn.lobie.campus.database.DBHelper;
import vn.lobie.campus.R;

public class BudgetFragment extends Fragment {
    private TextInputLayout layoutBudgetAmount;
    private MaterialButton btnUpdateBudget;
    private RecyclerView recyclerCategories;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        initializeViews(view);
        setupListeners();
        loadCurrentBudget();
        return view;
    }

    private void initializeViews(View view) {
        layoutBudgetAmount = view.findViewById(R.id.layoutBudgetAmount);
        btnUpdateBudget = view.findViewById(R.id.btnUpdateBudget);
        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        dbHelper = new DBHelper(requireContext());
    }

    private void setupListeners() {
        btnUpdateBudget.setOnClickListener(v -> {
            if (validateInput()) {
                updateBudget();
            }
        });
    }

    private boolean validateInput() {
        return ValidationUtils.validateAmount(layoutBudgetAmount);
    }

    private void updateBudget() {
        double amount = Double.parseDouble(
            layoutBudgetAmount.getEditText().getText().toString()
        );
        
        showLoading();
        new Thread(() -> {
            boolean success = dbHelper.updateBudget(amount);
            handler.post(() -> {
                hideLoading();
                if (success) {
                    showMessage("Budget updated successfully");
                    loadCurrentBudget();
                } else {
                    showError("Failed to update budget");
                }
            });
        }).start();
    }

    private void loadCurrentBudget() {
        // Implementation will follow in next response
    }
}
