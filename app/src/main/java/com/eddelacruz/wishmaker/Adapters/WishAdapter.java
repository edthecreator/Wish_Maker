package com.eddelacruz.wishmaker.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Models.Wishes;
import com.eddelacruz.wishmaker.R;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class WishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = "WISH ADAPTER";

    private Context context;
    private ArrayList<Wishes> wishlist = new ArrayList<>();
    private WishAdapter.onRecyclerViewItemClickListener mItemClickListener;

    public WishAdapter() { }

    public WishAdapter(Context context, ArrayList<Wishes> list) {
        this.wishlist = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wish_list_display, parent, false);
        return new WishlistViewHolder(view, parent.getContext(), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof WishlistViewHolder) {
            WishlistViewHolder v = (WishlistViewHolder) holder;
            Wishes fpos = wishlist.get(position);

            try {
                if(!fpos.getName().equals(""))
                    v.name.setText(fpos.getName());

                Locale locale = Locale.getDefault();
                Currency currency = Currency.getInstance(locale);
                String symbol = currency.getSymbol().replaceAll("\\w", "");

                String price =  symbol + String.format(Locale.US, "%.2f", fpos.getPrice());
                v.price.setText(price);


                if(!fpos.getImage().equals("")) {
                    if (URLUtil.isValidUrl(fpos.getImage())) {
                        Glide.with(v.icon.getContext()).load(Uri.parse(fpos.getImage())).centerCrop().into(v.icon);
                    }
                }
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return wishlist == null ? 0 : wishlist.size();
    }

    @Override
    public int getItemViewType(int position) { return 0; }

    private class WishlistViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        ImageView icon = (ImageView) itemView.findViewById(R.id.icon);
        TextView  name = (TextView) itemView.findViewById(R.id.name);
        TextView  price = (TextView) itemView.findViewById(R.id.price);

        public WishlistViewHolder(@NonNull final View itemView, final Context context, int viewType) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                int pos = getAdapterPosition();
                mItemClickListener.onItemClickListener(v, wishlist.get(pos).getName(), getAdapterPosition(), wishlist.get(pos).getLink());
            }
        }
    }

    public void setOnItemClickListener(WishAdapter.onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, String name, int position, String link);
    }
}

