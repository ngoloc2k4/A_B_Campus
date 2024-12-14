package vn.lobie.campus.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import vn.lobie.campus.R;
import vn.lobie.campus.adapters.TransactionAdapter;
import vn.lobie.campus.models.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;
import vn.lobie.campus.utils.DBStatic;
import vn.lobie.campus.database.DBHelper;
import vn.lobie.campus.utils.DialogUtils;

public class HomeFragment extends Fragment implements DataRefreshManager.DataRefreshListener {
    private ProgressBar progressBudget;
    private TextView txtBudgetPercentage;
    private PieChart pieChart;
    private ListView listTransactions;
    private TextView txtUserName, txtBalanceAmount, txtBudgetRemaining;
    private TextView txtTotalIncome, txtTotalExpense;
    private DBHelper dbHelper;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(requireContext());
        DataRefreshManager.getInstance().registerListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(view);
        setupPieChart();
        loadData();
        setupData();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataRefreshManager.getInstance().unregisterListener(this);
    }

    private void initializeViews(View view) {
        progressBudget = view.findViewById(R.id.progressBudget);
        txtBudgetPercentage = view.findViewById(R.id.txtBudgetPercentage);
        pieChart = view.findViewById(R.id.pieChart);
        listTransactions = view.findViewById(R.id.listLatestTransactions);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtBalanceAmount = view.findViewById(R.id.txtBalanceAmount);
        txtBudgetRemaining = view.findViewById(R.id.txtBudgetRemaining);
        txtTotalIncome = view.findViewById(R.id.txtTotalIncome);
        txtTotalExpense = view.findViewById(R.id.txtTotalExpense);
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
    }

    private void updateBudgetProgress(int percentage) {
        progressBudget.setProgress(percentage);
        txtBudgetPercentage.setText(getString(R.string.budget_used_percentage, percentage));

        // Change progress bar color based on percentage
        int colorResId;
        if (percentage < 50) {
            colorResId = R.color.green_light;
        } else if (percentage < 80) {
            colorResId = R.color.progress_color;
        } else {
            colorResId = R.color.red_light;
        }
        progressBudget.setProgressTintList(
            getResources().getColorStateList(colorResId, null)
        );
    }

    private void loadData() {
        DialogUtils.showLoading(requireContext());
        new Thread(() -> {
            try {
                List<Transaction> transactions = dbHelper.getLatestTransactions(
                    DBStatic.getUserId(), 8);
                dbHelper.loadUserFinancialData(DBStatic.getUserId());
                
                handler.post(() -> {
                    updateTransactionsList(transactions);
                    updateFinancialData();
                    DialogUtils.hideLoading();
                });
            } catch (Exception e) {
                handler.post(() -> {
                    DialogUtils.showError(requireContext(), 
                        "Error loading data: " + e.getMessage());
                    DialogUtils.hideLoading();
                });
            }
        }).start();
    }

    private void updateTransactionsList(List<Transaction> transactions) {
        TransactionAdapter adapter = new TransactionAdapter(getContext(), transactions);
        adapter.setOnItemClickListener(transaction -> {
            Intent intent = new Intent(getActivity(), TransactionDetailActivity.class);
            intent.putExtra("transaction_id", transaction.getId());
            startActivity(intent);
        });
        listTransactions.setAdapter(adapter);
    }

    private void updateFinancialData() {
        txtUserName.setText(getString(R.string.welcome_user, DBStatic.getUserFullName()));
        
        double totalIncome = DBStatic.getTotalIncome();
        double totalExpense = DBStatic.getTotalExpense();
        double budget = DBStatic.getCurrentBudget();
        double balance = totalIncome - totalExpense;

        txtTotalIncome.setText(String.format("$%.2f", totalIncome));
        txtTotalExpense.setText(String.format("$%.2f", totalExpense));
        txtBalanceAmount.setText(String.format("$%.2f", balance));

        if (budget > 0) {
            int percentage = (int)((totalExpense / budget) * 100);
            updateBudgetProgress(percentage);
            txtBudgetRemaining.setText(String.format("$%.2f remaining", budget - totalExpense));
        }

        updatePieChartData(totalIncome, totalExpense);
    }

    private void updatePieChartData(double totalIncome, double totalExpense) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) totalIncome, "Income"));
        entries.add(new PieEntry((float) totalExpense, "Expense"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
            getResources().getColor(R.color.green_light),
            getResources().getColor(R.color.red_light)
        );

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    @Override
    public void onDataRefresh() {
        if (isAdded()) {
            loadData();
        }
    }

    private void showLoading() {
        // Show loading indicator
    }

    private void hideLoading() {
        // Hide loading indicator
    }

    private void setupData() {
        txtUserName.setText(getString(R.string.welcome_user, DBStatic.getUserFullName()));
        // Additional setup code
    }
}
