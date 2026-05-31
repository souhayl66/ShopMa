package com.shopma.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shopma.app.adapters.CommandeAdapter;
import com.shopma.app.database.DatabaseHelper;
import com.shopma.app.fragments.HeaderFragment;
import com.shopma.app.models.Commande;

import java.util.List;

public class CommandesActivity extends AppCompatActivity {

    private String username;
    private ListView lvCommandes;
    private TextView tvNoCommandes;
    private HeaderFragment headerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commandes);

        username = getIntent().getStringExtra("username");

        setupViews();
        setupHeader();
        loadCommandes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCommandes();
        if (headerFragment != null) headerFragment.updateHeader();
    }

    private void setupViews() {
        lvCommandes = findViewById(R.id.lvCommandes);
        tvNoCommandes = findViewById(R.id.tvNoCommandes);
    }

    private void setupHeader() {
        headerFragment = HeaderFragment.newInstance(username);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.headerContainer, headerFragment)
                .commit();
    }

    private void loadCommandes() {
        List<Commande> commandes = DatabaseHelper.getInstance(this).getCommandes();

        if (commandes.isEmpty()) {
            tvNoCommandes.setVisibility(View.VISIBLE);
            lvCommandes.setVisibility(View.GONE);
        } else {
            tvNoCommandes.setVisibility(View.GONE);
            lvCommandes.setVisibility(View.VISIBLE);
            CommandeAdapter adapter = new CommandeAdapter(this, commandes);
            lvCommandes.setAdapter(adapter);
        }
    }
}
