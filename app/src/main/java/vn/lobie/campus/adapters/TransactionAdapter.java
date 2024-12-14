package vn.lobie.campus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import vn.lobie.campus.R;
import vn.lobie.campus.models.Transaction;
import java.util.List;

public class TransactionAdapter extends ArrayAdapter<Transaction> {
    private Context context;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        super(context, 0, transactions);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction, parent, false);
        }

        Transaction transaction = getItem(position);
        if (transaction != null) {
            TextView titleView = convertView.findViewById(R.id.txtTransactionTitle);
            TextView categoryView = convertView.findViewById(R.id.txtTransactionCategory);
            TextView amountView = convertView.findViewById(R.id.txtTransactionAmount);
            TextView dateView = convertView.findViewById(R.id.txtTransactionDate);

            titleView.setText(transaction.getTitle());
            categoryView.setText(transaction.getCategory());
            dateView.setText(transaction.getDate());

            String amountText = String.format("$%.2f", transaction.getAmount());
            amountView.setText(amountText);

            // Set color based on transaction type
            int colorResId = transaction.getType().equals("income") ? 
                R.color.green_light : R.color.red_light;
            amountView.setTextColor(ContextCompat.getColor(context, colorResId));
        }

        return convertView;
    }
}
