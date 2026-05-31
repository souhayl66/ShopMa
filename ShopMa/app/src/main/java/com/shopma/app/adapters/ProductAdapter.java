package com.shopma.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shopma.app.R;
import com.shopma.app.models.Product;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends BaseAdapter {

    private final Context context;
    private final List<Product> products;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products == null ? 0 : products.size();
    }

    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.ivProduct = convertView.findViewById(R.id.ivProduct);
            holder.tvTitle = convertView.findViewById(R.id.tvProductTitle);
            holder.tvCategory = convertView.findViewById(R.id.tvProductCategory);
            holder.tvPrice = convertView.findViewById(R.id.tvProductPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = getItem(position);

        holder.tvTitle.setText(product.getTitle());
        holder.tvCategory.setText(product.getCategory());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "%.2f$", product.getPrice()));

        // Load image with Glide
        Glide.with(context)
                .load(product.getImage())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.ivProduct);

        return convertView;
    }

    public void updateProducts(List<Product> newProducts) {
        products.clear();
        if (newProducts != null) {
            products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    // ViewHolder pattern for recycling
    static class ViewHolder {
        ImageView ivProduct;
        TextView tvTitle;
        TextView tvCategory;
        TextView tvPrice;
    }
}
