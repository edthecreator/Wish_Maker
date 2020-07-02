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
import com.eddelacruz.wishmaker.Models.Lists;
import com.eddelacruz.wishmaker.R;

import java.util.ArrayList;

public class ListsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = "LISTS ADAPTER";

    private Context context;
    private ArrayList<Lists> lists = new ArrayList<>();
    private ListsAdapter.onRecyclerViewItemClickListener mItemClickListener;

    public ListsAdapter() { }

    public ListsAdapter(Context context, ArrayList<Lists> list) {
        this.lists = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lists_display, parent, false);
        return new ListsAdapter.ListsViewHolder(view, parent.getContext(), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListsAdapter.ListsViewHolder) {
            ListsAdapter.ListsViewHolder v = (ListsAdapter.ListsViewHolder) holder;
            Lists fpos = lists.get(position);
            try {
                v.name.setText(fpos.getName());
                Log.d(TAG, "LIST IMAGE URL : " + fpos.getImage());
                if( URLUtil.isValidUrl(fpos.getImage())) {
                    Glide.with(v.icon.getContext()).load(Uri.parse(fpos.getImage())).centerCrop().into(v.icon);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return lists == null ? 0 : lists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private class ListsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon = (ImageView) itemView.findViewById(R.id.icon);
        TextView name = (TextView) itemView.findViewById(R.id.name);

        public ListsViewHolder(@NonNull final View itemView, final Context context, int viewType) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition(), lists.get(getAdapterPosition()).getName());
            }
        }
    }

    public void setOnItemClickListener(ListsAdapter.onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position, String name);
    }
}
