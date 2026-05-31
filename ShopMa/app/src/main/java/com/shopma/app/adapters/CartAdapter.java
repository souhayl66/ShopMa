package com.shopma.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.shopma.app.R;
import com.shopma.app.database.DatabaseHelper;
import com.shopma.app.models.CartItem;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends BaseAdapter {

    private final Context context;
    private List<CartItem> items;
    private final CartChangeListener listener;

    public interface CartChangeListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<CartItem> items, CartChangeListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public CartItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvCartItemName);
            holder.tvQuantity = convertView.findViewById(R.id.tvQuantity);
            holder.tvPrice = convertView.findViewById(R.id.tvCartItemPrice);
            holder.btnDecrease = convertView.findViewById(R.id.btnDecrease);
            holder.btnIncrease = convertView.findViewById(R.id.btnIncrease);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem item = getItem(position);
        DatabaseHelper db = DatabaseHelper.getInstance(context);

        holder.tvName.setText(item.getTitle());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvPrice.setText(String.format(Locale.getDefault(), "%.2f$", item.getSubtotal()));

        holder.btnDecrease.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;
            db.updateCartItemQuantity(item.getId(), newQty);
            if (newQty <= 0) {
                items.remove(position);
            } else {
                item.setQuantity(newQty);
            }
            notifyDataSetChanged();
            if (listener != null) listener.onCartChanged();
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            db.updateCartItemQuantity(item.getId(), newQty);
            item.setQuantity(newQty);
            notifyDataSetChanged();
            if (listener != null) listener.onCartChanged();
        });

        return convertView;
    }

    public void updateItems(List<CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvQuantity;
        TextView tvPrice;
        Button btnDecrease;
        Button btnIncrease;
    }
}
