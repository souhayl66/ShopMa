package com.shopma.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.chip.Chip;
import com.shopma.app.adapters.ProductAdapter;
import com.shopma.app.api.RetrofitClient;
import com.shopma.app.database.DatabaseHelper;
import com.shopma.app.fragments.HeaderFragment;
import com.shopma.app.models.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RechercheActivity extends AppCompatActivity {

    private String username;
    private EditText etSearchCategory;
    private ProgressBar progressBar;
    private TextView tvResultsLabel;
    private ListView lvResults;
    private ProductAdapter adapter;
    private HeaderFragment headerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);

        username = getIntent().getStringExtra("username");

        setupViews();
        setupHeader();
        setupChips();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (headerFragment != null) headerFragment.updateHeader();
    }

    private void setupViews() {
        etSearchCategory = findViewById(R.id.etSearchCategory);
        progressBar = findViewById(R.id.progressBar);
        tvResultsLabel = findViewById(R.id.tvResultsLabel);
        lvResults = findViewById(R.id.lvResults);

        adapter = new ProductAdapter(this, new ArrayList<>());
        lvResults.setAdapter(adapter);

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            String query = etSearchCategory.getText().toString().trim();
            if (!query.isEmpty()) {
                searchByCategory(query);
            } else {
                Toast.makeText(this, "Entrez une catégorie", Toast.LENGTH_SHORT).show();
            }
        });

        // Click on result: add to cart
        lvResults.setOnItemClickListener((parent, view, position, id) -> {
            Product product = adapter.getItem(position);
            showAddToCartDialog(product);
        });
    }

    private void setupHeader() {
        headerFragment = HeaderFragment.newInstance(username);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.headerContainer, headerFragment)
                .commit();
    }

    private void setupChips() {
        Chip chipElec = findViewById(R.id.chipElectronics);
        Chip chipJew = findViewById(R.id.chipJewelery);
        Chip chipMen = findViewById(R.id.chipMensClothing);
        Chip chipWomen = findViewById(R.id.chipWomensClothing);

        View.OnClickListener chipListener = v -> {
            String cat = ((Chip) v).getText().toString();
            etSearchCategory.setText(cat);
            searchByCategory(cat);
        };

        chipElec.setOnClickListener(chipListener);
        chipJew.setOnClickListener(chipListener);
        chipMen.setOnClickListener(chipListener);
        chipWomen.setOnClickListener(chipListener);
    }

    private void searchByCategory(String category) {
        progressBar.setVisibility(View.VISIBLE);
        tvResultsLabel.setVisibility(View.GONE);

        RetrofitClient.getInstance().getApiService()
                .getProductsByCategory(category)
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.updateProducts(response.body());
                            tvResultsLabel.setText(getString(R.string.results_for, category));
                            tvResultsLabel.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(RechercheActivity.this,
                                    R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RechercheActivity.this,
                                R.string.network_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddToCartDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle(product.getTitle())
                .setMessage(String.format("Prix : %.2f$\nCatégorie : %s",
                        product.getPrice(), product.getCategory()))
                .setPositiveButton("Ajouter au panier", (dialog, which) -> {
                    DatabaseHelper.getInstance(this)
                            .addToCart(product.getId(), product.getTitle(), product.getPrice());
                    Toast.makeText(this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
                    if (headerFragment != null) headerFragment.updateHeader();
                })
                .setNegativeButton("Fermer", null)
                .show();
    }
}
