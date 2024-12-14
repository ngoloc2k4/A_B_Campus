package vn.lobie.campus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import vn.lobie.campus.R;

public class ChartsFragment extends Fragment {
    private PieChart pieChartIncome;
    private BarChart barChartTrends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);
        initializeViews(view);
        setupCharts();
        return view;
    }

    private void initializeViews(View view) {
        pieChartIncome = view.findViewById(R.id.pieChartIncome);
        barChartTrends = view.findViewById(R.id.barChartTrends);
    }

    private void setupCharts() {
        setupPieChart();
        setupBarChart();
        loadChartData();
    }

    private void setupPieChart() {
        pieChartIncome.setUsePercentValues(true);
        pieChartIncome.getDescription().setEnabled(false);
        pieChartIncome.setDrawHoleEnabled(true);
        pieChartIncome.setHoleColor(Color.WHITE);
        pieChartIncome.setTransparentCircleRadius(61f);
        pieChartIncome.getLegend().setEnabled(true);
    }

    private void setupBarChart() {
        barChartTrends.getDescription().setEnabled(false);
        barChartTrends.setDrawGridBackground(false);
        barChartTrends.getLegend().setEnabled(true);

        XAxis xAxis = barChartTrends.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChartTrends.getAxisLeft().setDrawGridLines(true);
        barChartTrends.getAxisRight().setEnabled(false);
    }

    private void loadChartData() {
        showLoading();
        new Thread(() -> {
            try {
                // Load data from database
                ArrayList<PieEntry> pieEntries = loadPieChartData();
                ArrayList<BarEntry> barEntries = loadBarChartData();
                
                handler.post(() -> {
                    updatePieChart(pieEntries);
                    updateBarChart(barEntries);
                    hideLoading();
                });
            } catch (Exception e) {
                handler.post(() -> {
                    showError("Error loading chart data");
                    hideLoading();
                });
            }
        }).start();
    }

    private ArrayList<PieEntry> loadPieChartData() {
        // Implementation will follow in next response
    }

    private ArrayList<BarEntry> loadBarChartData() {
        // Implementation will follow in next response
    }
}
