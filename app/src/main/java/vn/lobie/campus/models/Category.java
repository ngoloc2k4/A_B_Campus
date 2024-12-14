package vn.lobie.campus.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    private int id;
    private String name;
    private String type;
    private int transactionCount;

    public Category(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.transactionCount = 0;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int count) { this.transactionCount = count; }

    // Parcelable implementation
    protected Category(Parcel in) {
        id = in.readInt();
        name = in.readString();
        type = in.readString();
        transactionCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeInt(transactionCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
