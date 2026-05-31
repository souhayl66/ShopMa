package com.shopma.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.shopma.app.adapters.ProductAdapter;
import com.shopma.app.api.RetrofitClient;
import com.shopma.app.database.DatabaseHelper;
import com.shopma.app.fragments.HeaderFragment;
import com.shopma.app.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogueActivity extends AppCompatActivity {

    private String username;
    private String category;
    private ProgressBar progressBar;
    private TextView tvError;
    private ListView lvProducts;
    private ProductAdapter adapter;
    private final List<Product> productList = new ArrayList<>();
    private HeaderFragment headerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        username = getIntent().getStringExtra("username");
        category = getIntent().getStringExtra("category"); // null means all products

        setupViews();
        setupHeader();
        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (headerFragment != null) headerFragment.updateHeader();
    }

    private void setupViews() {
        progressBar = findViewById(R.id.progressBar);
        tvError     = findViewById(R.id.tvError);
        lvProducts  = findViewById(R.id.lvProducts);

        adapter = new ProductAdapter(this, productList);
        lvProducts.setAdapter(adapter);


        lvProducts.setOnItemClickListener((parent, view, position, id) ->
                showProductDialog(adapter.getItem(position)));


        lvProducts.setOnItemLongClickListener((parent, view, position, id) -> {
            shareProduct(adapter.getItem(position));
            return true;
        });
    }

    private void setupHeader() {
        headerFragment = HeaderFragment.newInstance(username);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.headerContainer, headerFragment)
                .commit();
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        lvProducts.setVisibility(View.GONE);

        Call<List<Product>> call;
        if (category != null && !category.isEmpty()) {
            call = RetrofitClient.getInstance().getApiService()
                    .getProductsByCategory(category);
        } else {
            call = RetrofitClient.getInstance().getApiService()
                    .getAllProducts();
        }

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call,
                                   Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateProducts(response.body());
                    lvProducts.setVisibility(View.VISIBLE);
                    if (response.body().isEmpty()) {
                        tvError.setText("Aucun produit trouvé.");
                        tvError.setVisibility(View.VISIBLE);
                    }
                } else {
                    showNetworkError();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showNetworkError();
            }
        });
    }

    private void showNetworkError() {
        tvError.setText(R.string.network_error);
        tvError.setVisibility(View.VISIBLE);
        lvProducts.setVisibility(View.VISIBLE);
    }

    private void showProductDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle(product.getTitle())
                .setMessage(String.format(Locale.getDefault(),
                        "Prix : %.2f$\nCatégorie : %s\n\n%s",
                        product.getPrice(),
                        product.getCategory(),
                        product.getDescription()))
                .setPositiveButton("Ajouter au panier", (dialog, which) -> {
                    DatabaseHelper.getInstance(this)
                            .addToCart(product.getId(), product.getTitle(), product.getPrice());
                    Toast.makeText(this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
                    if (headerFragment != null) headerFragment.updateHeader();
                })
                .setNeutralButton("Partager", (dialog, which) -> shareProduct(product))
                .setNegativeButton("Fermer", null)
                .show();
    }


    private void shareProduct(Product product) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_text, product.getTitle(), product.getPrice()));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_product)));
    }
}
