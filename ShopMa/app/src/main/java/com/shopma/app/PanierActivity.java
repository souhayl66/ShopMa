package com.shopma.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.shopma.app.adapters.CartAdapter;
import com.shopma.app.database.DatabaseHelper;
import com.shopma.app.fragments.HeaderFragment;
import com.shopma.app.models.CartItem;

import java.util.List;
import java.util.Locale;

public class PanierActivity extends AppCompatActivity implements CartAdapter.CartChangeListener {

    private String username;
    private ListView lvCartItems;
    private TextView tvTotal;
    private TextView tvEmptyCart;
    private Button btnCommander;
    private CartAdapter adapter;
    private DatabaseHelper db;
    private HeaderFragment headerFragment;

    private static final String CHANNEL_ID = "shopma_orders";
    private static final int NOTIF_ID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        username = getIntent().getStringExtra("username");
        db = DatabaseHelper.getInstance(this);

        createNotificationChannel();
        setupViews();
        setupHeader();
        loadCart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
        if (headerFragment != null) headerFragment.updateHeader();
    }

    private void setupViews() {
        lvCartItems = findViewById(R.id.lvCartItems);
        tvTotal = findViewById(R.id.tvTotal);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        btnCommander = findViewById(R.id.btnCommander);

        btnCommander.setOnClickListener(v -> passerCommande());
    }

    private void setupHeader() {
        headerFragment = HeaderFragment.newInstance(username);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.headerContainer, headerFragment)
                .commit();
    }

    private void loadCart() {
        List<CartItem> items = db.getCartItems();

        if (items.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            lvCartItems.setVisibility(View.GONE);
            btnCommander.setEnabled(false);
            tvTotal.setText("0.00$");
        } else {
            tvEmptyCart.setVisibility(View.GONE);
            lvCartItems.setVisibility(View.VISIBLE);
            btnCommander.setEnabled(true);

            if (adapter == null) {
                adapter = new CartAdapter(this, items, this);
                lvCartItems.setAdapter(adapter);
            } else {
                adapter.updateItems(items);
            }
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = db.getCartTotal();
        tvTotal.setText(String.format(Locale.getDefault(), "%.2f$", total));
    }

    private void passerCommande() {
        List<CartItem> items = db.getCartItems();
        if (items.isEmpty()) {
            Toast.makeText(this, R.string.panier_empty, Toast.LENGTH_SHORT).show();
            return;
        }


        long orderId = db.createOrder();

        if (orderId > 0) {

            db.clearCart();


            loadCart();
            if (headerFragment != null) headerFragment.updateHeader();


            sendOrderNotification();

            Toast.makeText(this, R.string.order_success, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCartChanged() {
        updateTotal();
        if (db.getCartItemCount() == 0) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            lvCartItems.setVisibility(View.GONE);
            btnCommander.setEnabled(false);
        }
        if (headerFragment != null) headerFragment.updateHeader();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notif_channel_name);
            String description = getString(R.string.notif_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendOrderNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.notif_order_title))
                .setContentText(getString(R.string.notif_order_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIF_ID, builder.build());
        }
    }
}
