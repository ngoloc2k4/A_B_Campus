package vn.lobie.campus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Base64;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import vn.lobie.campus.models.Transaction;
import vn.lobie.campus.models.Category;
import vn.lobie.campus.utils.DBStatic;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CampusExpenseDB";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "Users";
    private static final String TABLE_CATEGORIES = "Categories";
    private static final String TABLE_TRANSACTIONS = "Transactions";
    private static final String TABLE_BUDGET = "Budget";

    // Common column names
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_CATEGORY_ID = "category_id";

    // Default Categories
    private static final String DEFAULT_FOOD_CATEGORY = "Food";
    private static final String DEFAULT_TRANSPORT_CATEGORY = "Transport";
    private static final String DEFAULT_SALARY_CATEGORY = "Salary";
    private static final String DEFAULT_ENTERTAINMENT_CATEGORY = "Entertainment";
    private static final String DEFAULT_OTHER_CATEGORY = "Other";
    private static final String DEFAULT_OTHER_INCOME_CATEGORY = "Other Income";
    private static final String DEFAULT_FREELANCE_CATEGORY = "Freelance";
    private static final String DEFAULT_GIFT_CATEGORY = "Gift";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL,"
                + "hashed_password TEXT NOT NULL,"
                + "email TEXT NOT NULL"
                + ")";

        // Create Categories table
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "type TEXT NOT NULL"
                + ")";

        // Create Transactions table
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER,"
                + "amount REAL NOT NULL,"
                + KEY_CATEGORY_ID + " INTEGER,"
                + "type TEXT NOT NULL,"
                + "date TEXT NOT NULL,"
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(" + KEY_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + KEY_CATEGORY_ID + ")"
                + ")";

        // Create Budget table
        String CREATE_BUDGET_TABLE = "CREATE TABLE " + TABLE_BUDGET + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY,"
                + "budget_amount REAL NOT NULL,"
                + "remaining_balance REAL NOT NULL,"
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ") ON DELETE CASCADE"
                + ")";

        // Execute the SQL statements
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
        db.execSQL(CREATE_BUDGET_TABLE);

        // Insert default categories
        insertDefaultCategories(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        // Expense categories
        insertCategory(db, DEFAULT_FOOD_CATEGORY, "expense");
        insertCategory(db, DEFAULT_TRANSPORT_CATEGORY, "expense");
        insertCategory(db, DEFAULT_ENTERTAINMENT_CATEGORY, "expense");
        insertCategory(db, DEFAULT_OTHER_CATEGORY, "expense");

        // Income categories
        insertCategory(db, DEFAULT_SALARY_CATEGORY, "income");
        insertCategory(db, DEFAULT_OTHER_INCOME_CATEGORY, "income");
        insertCategory(db, DEFAULT_FREELANCE_CATEGORY, "income");
        insertCategory(db, DEFAULT_GIFT_CATEGORY, "income");
    }

    private void insertCategory(SQLiteDatabase db, String name, String type) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("type", type);
        db.insert(TABLE_CATEGORIES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    public boolean executeTransactionSafely(DbOperation operation) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            operation.execute(db);
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DBHelper", "Transaction error: " + e.getMessage());
            return false;
        } finally {
            try {
                db.endTransaction();
            } catch (Exception e) {
                Log.e("DBHelper", "Error ending transaction: " + e.getMessage());
            }
        }
    }

    public interface DbOperation {
        void execute(SQLiteDatabase db);
    }

    // User Operations
    public long registerUser(String username, String password, String email) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("hashed_password", hashPassword(password));
        values.put("email", email);

        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;
        
        try {
            userId = db.insertOrThrow(TABLE_USERS, null, values);
            if (userId != -1) {
                initializeUserBudget(userId);
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error registering user: " + e.getMessage());
        }
        
        return userId;
    }

    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"user_id", "username", "email"};
        String selection = "email = ? AND hashed_password = ?";
        String[] selectionArgs = {email, hashPassword(password)};
        
        try {
            Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                DBStatic.setUserInfo(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
                );
                cursor.close();
                return true;
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e("DBHelper", "Error validating user: " + e.getMessage());
        }
        return false;
    }

    // Transaction Operations
    public long addTransaction(String note, double amount, String category, String type) {
        final long[] transactionId = {-1};
        
        executeTransactionSafely(db -> {
            ContentValues values = new ContentValues();
            values.put("user_id", DBStatic.getUserId());
            values.put("note", note);
            values.put("amount", amount);
            values.put("category_id", getCategoryId(db, category, type));
            values.put("type", type);
            values.put("date", getCurrentDateTime());

            transactionId[0] = db.insertOrThrow(TABLE_TRANSACTIONS, null, values);
            if (transactionId[0] != -1) {
                updateBudgetForTransaction(db, amount, type);
            }
        });

        return transactionId[0];
    }

    public boolean deleteTransaction(int transactionId) {
        return executeTransactionSafely(db -> {
            Transaction transaction = getTransactionById(transactionId);
            if (transaction != null) {
                db.delete(TABLE_TRANSACTIONS, "transaction_id = ?", 
                    new String[]{String.valueOf(transactionId)});
                updateBudgetForTransaction(db, transaction.getAmount(), 
                    transaction.getType().equals("income") ? "expense" : "income");
            }
        });
    }

    // Category Operations
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        
        try {
            Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, "type, name");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    categories.add(new Category(
                        cursor.getInt(cursor.getColumnIndex("category_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("type"))
                    ));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting categories: " + e.getMessage());
        }
        
        return categories;
    }

    public boolean addCustomCategory(String name, String type) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("type", type);
        
        try {
            return getWritableDatabase().insert(TABLE_CATEGORIES, null, values) != -1;
        } catch (Exception e) {
            Log.e("DBHelper", "Error adding category: " + e.getMessage());
            return false;
        }
    }

    // Budget Operations
    public boolean updateBudget(double amount) {
        ContentValues values = new ContentValues();
        values.put("budget_amount", amount);
        values.put("remaining_balance", amount);
        
        try {
            int result = getWritableDatabase().update(TABLE_BUDGET, values,
                "user_id = ?", new String[]{String.valueOf(DBStatic.getUserId())});
            return result > 0;
        } catch (Exception e) {
            Log.e("DBHelper", "Error updating budget: " + e.getMessage());
            return false;
        }
    }

    // Utility Methods
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e("DBHelper", "Error hashing password: " + e.getMessage());
            return password; // Fallback, not secure for production
        }
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(new Date());
    }

    private int getCategoryId(SQLiteDatabase db, String categoryName, String type) {
        Cursor cursor = db.query(TABLE_CATEGORIES,
            new String[]{"category_id"},
            "name = ? AND type = ?",
            new String[]{categoryName, type},
            null, null, null);
            
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        if (cursor != null) cursor.close();
        return -1;
    }

    private void updateBudgetForTransaction(SQLiteDatabase db, double amount, String type) {
        String operator = "income".equals(type) ? "+" : "-";
        db.execSQL(
            "UPDATE " + TABLE_BUDGET + 
            " SET remaining_balance = remaining_balance " + operator + " ? " +
            "WHERE user_id = ?",
            new Object[]{amount, DBStatic.getUserId()}
        );
        refreshFinancialData(db);
    }

    private void refreshFinancialData(SQLiteDatabase db) {
        loadUserFinancialData(DBStatic.getUserId());
    }

    private void initializeUserBudget(long userId) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("budget_amount", 0.0);
        values.put("remaining_balance", 0.0);
        
        try {
            getWritableDatabase().insert(TABLE_BUDGET, null, values);
        } catch (Exception e) {
            Log.e("DBHelper", "Error initializing budget: " + e.getMessage());
        }
    }
}
