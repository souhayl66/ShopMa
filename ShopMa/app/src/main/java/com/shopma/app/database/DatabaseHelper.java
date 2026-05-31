package com.shopma.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shopma.app.models.CartItem;
import com.shopma.app.models.Commande;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shopma.db";
    private static final int DATABASE_VERSION = 1;

    // Table Panier
    public static final String TABLE_PANIER = "panier";
    public static final String COL_PANIER_ID = "id";
    public static final String COL_PANIER_PRODUCT_ID = "product_id";
    public static final String COL_PANIER_TITLE = "title";
    public static final String COL_PANIER_PRICE = "price";
    public static final String COL_PANIER_QUANTITY = "quantity";

    private static final String CREATE_TABLE_PANIER =
            "CREATE TABLE " + TABLE_PANIER + " (" +
            COL_PANIER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_PANIER_PRODUCT_ID + " INTEGER, " +
            COL_PANIER_TITLE + " TEXT, " +
            COL_PANIER_PRICE + " REAL, " +
            COL_PANIER_QUANTITY + " INTEGER)";

    //  Table Commandes
    public static final String TABLE_COMMANDES = "commandes";
    public static final String COL_CMD_ID = "id";
    public static final String COL_CMD_DATE = "date";
    public static final String COL_CMD_NB_ARTICLES = "nb_articles";
    public static final String COL_CMD_MONTANT = "montant_total";
    public static final String COL_CMD_STATUT = "statut";

    private static final String CREATE_TABLE_COMMANDES =
            "CREATE TABLE " + TABLE_COMMANDES + " (" +
            COL_CMD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_CMD_DATE + " TEXT, " +
            COL_CMD_NB_ARTICLES + " INTEGER, " +
            COL_CMD_MONTANT + " REAL, " +
            COL_CMD_STATUT + " TEXT)";

    // Singleton
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANIER);
        db.execSQL(CREATE_TABLE_COMMANDES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANIER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMANDES);
        onCreate(db);
    }

    // PANIER OPERATIONS


    public void addToCart(int productId, String title, double price) {
        SQLiteDatabase db = getWritableDatabase();
        // Check if product already in cart
        Cursor cursor = db.query(TABLE_PANIER, null,
                COL_PANIER_PRODUCT_ID + "=?",
                new String[]{String.valueOf(productId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Update quantity
            int currentQty = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANIER_QUANTITY));
            int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANIER_ID));
            ContentValues values = new ContentValues();
            values.put(COL_PANIER_QUANTITY, currentQty + 1);
            db.update(TABLE_PANIER, values, COL_PANIER_ID + "=?",
                    new String[]{String.valueOf(rowId)});
            cursor.close();
        } else {
            if (cursor != null) cursor.close();
            // Insert new
            ContentValues values = new ContentValues();
            values.put(COL_PANIER_PRODUCT_ID, productId);
            values.put(COL_PANIER_TITLE, title);
            values.put(COL_PANIER_PRICE, price);
            values.put(COL_PANIER_QUANTITY, 1);
            db.insert(TABLE_PANIER, null, values);
        }
    }


    public List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANIER, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CartItem item = new CartItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANIER_ID)));
                item.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANIER_PRODUCT_ID)));
                item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANIER_TITLE)));
                item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANIER_PRICE)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANIER_QUANTITY)));
                items.add(item);
            }
            cursor.close();
        }
        return items;
    }


    public void updateCartItemQuantity(int cartItemId, int newQuantity) {
        SQLiteDatabase db = getWritableDatabase();
        if (newQuantity <= 0) {
            db.delete(TABLE_PANIER, COL_PANIER_ID + "=?",
                    new String[]{String.valueOf(cartItemId)});
        } else {
            ContentValues values = new ContentValues();
            values.put(COL_PANIER_QUANTITY, newQuantity);
            db.update(TABLE_PANIER, values, COL_PANIER_ID + "=?",
                    new String[]{String.valueOf(cartItemId)});
        }
    }


    public void clearCart() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PANIER, null, null);
    }


    public int getCartItemCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_PANIER_QUANTITY + ") FROM " + TABLE_PANIER, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }


    public double getCartTotal() {
        List<CartItem> items = getCartItems();
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    // COMMANDES OPERATIONS


    public long createOrder() {
        List<CartItem> items = getCartItems();
        if (items.isEmpty()) return -1;

        int nbArticles = 0;
        double total = 0;
        for (CartItem item : items) {
            nbArticles += item.getQuantity();
            total += item.getSubtotal();
        }

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date());

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CMD_DATE, date);
        values.put(COL_CMD_NB_ARTICLES, nbArticles);
        values.put(COL_CMD_MONTANT, total);
        values.put(COL_CMD_STATUT, "en_cours");

        return db.insert(TABLE_COMMANDES, null, values);
    }


    public List<Commande> getCommandes() {
        List<Commande> commandes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMMANDES, null, null, null, null, null,
                COL_CMD_ID + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Commande cmd = new Commande();
                cmd.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_CMD_ID)));
                cmd.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_CMD_DATE)));
                cmd.setNbArticles(cursor.getInt(cursor.getColumnIndexOrThrow(COL_CMD_NB_ARTICLES)));
                cmd.setMontantTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CMD_MONTANT)));
                cmd.setStatut(cursor.getString(cursor.getColumnIndexOrThrow(COL_CMD_STATUT)));
                commandes.add(cmd);
            }
            cursor.close();
        }
        return commandes;
    }
}
