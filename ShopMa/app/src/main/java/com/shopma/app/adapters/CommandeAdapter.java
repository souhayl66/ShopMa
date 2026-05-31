package com.shopma.app.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.shopma.app.R;
import com.shopma.app.models.Commande;

import java.util.List;
import java.util.Locale;

public class CommandeAdapter extends BaseAdapter {

    private final Context context;
    private final List<Commande> commandes;

    public CommandeAdapter(Context context, List<Commande> commandes) {
        this.context = context;
        this.commandes = commandes;
    }

    @Override
    public int getCount() {
        return commandes == null ? 0 : commandes.size();
    }

    @Override
    public Commande getItem(int position) {
        return commandes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return commandes.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_commande, parent, false);
            holder = new ViewHolder();
            holder.tvNumber = convertView.findViewById(R.id.tvCommandeNumber);
            holder.tvInfo = convertView.findViewById(R.id.tvCommandeInfo);
            holder.tvAmount = convertView.findViewById(R.id.tvCommandeAmount);
            holder.tvStatut = convertView.findViewById(R.id.tvStatut);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Commande cmd = getItem(position);

        holder.tvNumber.setText(context.getString(R.string.commande_number, cmd.getId()));
        holder.tvInfo.setText(context.getString(R.string.commande_date_articles,
                cmd.getDate(), cmd.getNbArticles()));
        holder.tvAmount.setText(String.format(Locale.getDefault(), "%.2f$", cmd.getMontantTotal()));

        // Set status badge with appropriate color
        if (cmd.isEnCours()) {
            holder.tvStatut.setText(R.string.statut_en_cours);
            setStatutBackground(holder.tvStatut, R.color.status_en_cours);
        } else {
            holder.tvStatut.setText(R.string.statut_livree);
            setStatutBackground(holder.tvStatut, R.color.status_livree);
        }

        return convertView;
    }

    private void setStatutBackground(TextView tvStatut, int colorRes) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(24);
        drawable.setColor(ContextCompat.getColor(context, colorRes));
        tvStatut.setBackground(drawable);
    }

    static class ViewHolder {
        TextView tvNumber;
        TextView tvInfo;
        TextView tvAmount;
        TextView tvStatut;
    }
}
