package vn.lobie.campus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import vn.lobie.campus.R;
import vn.lobie.campus.dialogs.AddTransactionDialog;

public class TransactionsFragment extends Fragment {
    private RecyclerView recyclerTransactions;
    private FloatingActionButton fabAddTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        initializeViews(view);
        setupListeners();
        return view;
    }

    private void initializeViews(View view) {
        recyclerTransactions = view.findViewById(R.id.recyclerTransactions);
        fabAddTransaction = view.findViewById(R.id.fabAddTransaction);
    }

    private void setupListeners() {
        fabAddTransaction.setOnClickListener(v -> showAddTransactionDialog());
    }

    private void showAddTransactionDialog() {
        new AddTransactionDialog(requireContext(), (amount, category, note, type) -> {
            // Handle new transaction
        }).show();
    }
}
