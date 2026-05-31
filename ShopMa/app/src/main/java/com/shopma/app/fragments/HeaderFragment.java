package com.shopma.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shopma.app.R;
import com.shopma.app.database.DatabaseHelper;

public class HeaderFragment extends Fragment {

    public static final String ARG_USERNAME = "username";

    private TextView tvWelcome;
    private TextView tvCartCount;
    private String username = "";

    public static HeaderFragment newInstance(String username) {
        HeaderFragment fragment = new HeaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username != null ? username : "");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, container, false);
        tvWelcome   = view.findViewById(R.id.tvWelcome);
        tvCartCount = view.findViewById(R.id.tvCartCount);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateHeader();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHeader();
    }


    public void updateHeader() {
        if (tvWelcome == null || tvCartCount == null || getContext() == null) return;

        // Safe welcome text
        if (username != null && !username.isEmpty()) {
            tvWelcome.setText(getString(R.string.welcome_message, username));
        } else {
            tvWelcome.setText(R.string.app_name);
        }

        // Cart count
        int count = DatabaseHelper.getInstance(getContext()).getCartItemCount();
        tvCartCount.setText(getString(R.string.articles_in_cart, count));
    }

    public void setUsername(String name) {
        this.username = (name != null) ? name : "";
        updateHeader();
    }
}
