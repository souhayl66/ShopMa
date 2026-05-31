package com.shopma.app.api;

import com.shopma.app.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("products")
    Call<List<Product>> getAllProducts();

    @GET("products/categories")
    Call<List<String>> getCategories();

    @GET("products/category/{cat}")
    Call<List<Product>> getProductsByCategory(@Path("cat") String category);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") int id);
}
