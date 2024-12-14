package vn.lobie.campus.models;

public class Transaction {
    private int id;
    private String title;
    private double amount;
    private String type; // "income" or "expense"
    private String date;
    private String category;

    public Transaction(int id, String title, double amount, String type, String date, String category) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.category = category;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getDate() { return date; }
    public String getCategory() { return category; }
}
