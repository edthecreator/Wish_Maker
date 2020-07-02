package com.eddelacruz.wishmaker.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eddelacruz.wishmaker.Models.Friends;
import com.eddelacruz.wishmaker.R;

import java.util.ArrayList;

public class SearchUsers extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static String TAG = "FRIENDS ADAPTER";

        private Context context;
        private ArrayList<Friends> friendsList = new ArrayList<>();
        private FriendsAdapter.onRecyclerViewItemClickListener mItemClickListener;

        public SearchUsers() { }

        public SearchUsers(Context context, ArrayList<Friends> list) {
                this.friendsList = list;
                this.context = context;
                }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_search_item, parent, false);
                return new FriendsViewHolder(view, parent.getContext(), viewType);
                }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if(holder instanceof FriendsViewHolder) {
                try {
                FriendsViewHolder v = (FriendsViewHolder) holder;
                Friends fpos = friendsList.get(position);
                v.name.setText(fpos.getName());
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
                return friendsList == null ? 0 : friendsList.size();
                }

        @Override
        public int getItemViewType(int position) {
                return 0;
                }

        private class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name = (TextView) itemView.findViewById(R.id.name);
            ImageView icon = (ImageView) itemView.findViewById(R.id.icon);
            ImageView addFriend = (ImageView) itemView.findViewById(R.id.addFriendIcon);

            public FriendsViewHolder(@NonNull final View itemView, final Context context, int viewType) {
                super(itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(v, getAdapterPosition(), friendsList.get(getAdapterPosition()).getUid(), friendsList.get(getAdapterPosition()).getName(), friendsList.get(getAdapterPosition()).getImage());
                }
            }
        }
            public void setOnItemClickListener(FriendsAdapter.onRecyclerViewItemClickListener mItemClickListener) {
                this.mItemClickListener = mItemClickListener;
            }

        public interface onRecyclerViewItemClickListener {
            void onItemClickListener(View view, int position, String uid, String name, String url);
        }

}
