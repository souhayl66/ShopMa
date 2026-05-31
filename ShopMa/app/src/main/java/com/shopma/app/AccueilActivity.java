package com.shopma.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.shopma.app.database.DatabaseHelper;

public class AccueilActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "shopma_prefs";

    private String username;
    private TextView tvCartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        // Get username from intent or prefs
        username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            username = prefs.getString("username", "");
        }

        setupToolbar();
        setupWelcome();
        setupCategoryCards();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        // Setup cart badge in custom action layout
        MenuItem cartItem = menu.findItem(R.id.action_cart);
        View actionView = cartItem.getActionView();
        if (actionView != null) {
            tvCartBadge = actionView.findViewById(R.id.tvCartBadge);
            actionView.findViewById(R.id.btnCart).setOnClickListener(v -> openCart());
            updateCartBadge();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            openProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupWelcome() {
        TextView tvWelcome = findViewById(R.id.tvWelcomeMessage);
        tvWelcome.setText(getString(R.string.welcome_message, username));
    }

    private void setupCategoryCards() {
        CardView cardAll = findViewById(R.id.cardAllProducts);
        CardView cardElec = findViewById(R.id.cardElectronique);
        CardView cardBijoux = findViewById(R.id.cardBijoux);
        CardView cardHomme = findViewById(R.id.cardModeHomme);
        CardView cardFemme = findViewById(R.id.cardModeFemme);
        CardView cardRetrait = findViewById(R.id.cardPointsRetrait);

        cardAll.setOnClickListener(v -> openCatalogue(null));
        cardElec.setOnClickListener(v -> openCatalogue("electronics"));
        cardBijoux.setOnClickListener(v -> openCatalogue("jewelery"));
        cardHomme.setOnClickListener(v -> openCatalogue("men's clothing"));
        cardFemme.setOnClickListener(v -> openCatalogue("women's clothing"));
        cardRetrait.setOnClickListener(v -> openPointsRetrait());
    }

    private void openCatalogue(String category) {
        Intent intent = new Intent(this, CatalogueActivity.class);
        intent.putExtra("username", username);
        if (category != null) {
            intent.putExtra("category", category);
        }
        startActivity(intent);
    }

    private void openPointsRetrait() {
        Intent intent = new Intent(this, PointsRetraitActivity.class);
        startActivity(intent);
    }

    private void openCart() {
        Intent intent = new Intent(this, PanierActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void openProfile() {
        Intent intent = new Intent(this, ProfilActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void updateCartBadge() {
        if (tvCartBadge == null) return;
        int count = DatabaseHelper.getInstance(this).getCartItemCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }
}
