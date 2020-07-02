package com.eddelacruz.wishmaker.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.eddelacruz.wishmaker.FriendFragments.FriendRequestFragment;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Models.Currency;
import com.eddelacruz.wishmaker.Models.Friends;
import com.eddelacruz.wishmaker.R;

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;

public class CurrencyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = "FRIENDS ADAPTER";

    private Context context;
    private String symbol;
    // private FriendRequestFragment friendRequestFragment;
    private ArrayList<Currency> friendsList = new ArrayList<>();
    private CurrencyAdapter.onRecyclerViewItemClickListener mItemClickListener;

    public CurrencyAdapter() { }

    public CurrencyAdapter(Context context, ArrayList<Currency> list) {
        this.friendsList = list;
        this.context = context;
        // this.friendRequestFragment = (FriendRequestFragment) friendrequestfragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_list_item, parent, false);
        return new CurrencyAdapter.RequestViewHolder(view, parent.getContext(), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CurrencyAdapter.RequestViewHolder) {
            try {
                CurrencyAdapter.RequestViewHolder v = (CurrencyAdapter.RequestViewHolder) holder;
                Currency fpos = friendsList.get(position);
                v.currencyIcon.setImageDrawable(fpos.getSymbol());
                v.currencyName.setText(fpos.getName());

                if(fpos.getName().equals(DataManager.getInstance().getUser().getstatus()))
                    v.checkImage.setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return friendsList == null ? 0 : friendsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void removeRequest(int pos) {
        friendsList.remove(pos);
        notifyItemChanged(pos);
    }

    private class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView currencyIcon = itemView.findViewById(R.id.currencyIcon);
        TextView currencyName = itemView.findViewById(R.id.currencyName);
        ImageView checkImage = itemView.findViewById(R.id.checkImage);

        public RequestViewHolder(@NonNull final View itemView, final Context context, int viewType) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition(), friendsList.get(getAdapterPosition()).getSymbol(), friendsList.get(getAdapterPosition()).getName());
            }
        }
    }
    public void setOnItemClickListener(CurrencyAdapter.onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position, Drawable symbol, String name);
    }

}

