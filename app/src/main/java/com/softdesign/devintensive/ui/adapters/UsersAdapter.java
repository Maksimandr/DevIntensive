package com.softdesign.devintensive.ui.adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.softdesign.devintensive.utils.UiHelper.getDisplayMetrics;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private static final String TAG = ConstantManager.TAG_PREFIX + "UsersAdapter ";

    private int mUserPhotoWidth, mUserPhotoHeight;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    List<String> items;
    List<String> itemsPendingRemoval;
    int lastInsertedIndex; // so we can add some more items for testing purposes
    boolean undoOn = false; // is undo on, you can turn it on from the toolbar menu
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    Context mContext;
    List<User> mUsers;
    UserViewHolder.CustomClickListener mCustomClickListener;

    public UsersAdapter(List<User> users, UserViewHolder.CustomClickListener customClickListener) {
        Log.d(TAG, "Constructor");

        mUsers = users;
        mUserPhotoWidth = getDisplayMetrics(0);
        mUserPhotoHeight = (int) (mUserPhotoWidth / ConstantManager.USER_PHOTO_PROFILE_RATIO);
        this.mCustomClickListener = customClickListener;

        items = new ArrayList<>();
        itemsPendingRemoval = new ArrayList<>();
        for (int i = 1; i <= mUsers.size(); i++) {
            items.add("Item " + i);
        }
    }

    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");

        mContext = parent.getContext();
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(final UsersAdapter.UserViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder");

        final User user = mUsers.get(position);
        final String userPhoto;

        if (user.getPhoto().isEmpty()) {
            Log.e(TAG, "onBindViewHolder: user with name " + user.getFullName() + " has empty photo");
            userPhoto = "null";
        } else {
            userPhoto = user.getPhoto();
        }

        DataManager.getInstance().getPicasso()
                .load(userPhoto)
                .error(holder.mDummy)
                .placeholder(holder.mDummy)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.mUserPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, " load from cache success");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(userPhoto)
                                .error(holder.mDummy)
                                .placeholder(holder.mDummy)
                                .fit()
                                .centerCrop()
                                .into(holder.mUserPhoto, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, " load from network success");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, " Could not fetch image");
                                    }
                                });
                    }
                });

        holder.mFullName.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
        holder.mProjects.setText(String.valueOf(user.getProjects()));

        if (user.getBio() == null || user.getBio().isEmpty()) {
            holder.mBio.setVisibility(View.GONE);
        } else {
            holder.mBio.setVisibility(View.VISIBLE);
            holder.mBio.setText(user.getBio());
        }
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount");
        return mUsers.size();
    }


    public void setUndoOn(boolean undoOn) {
        Log.d(TAG, "setUndoOn");
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        Log.d(TAG, "isUndoOn");
        return undoOn;
    }

    public void pendingRemoval(int position) {
        Log.d(TAG, "pendingRemoval");
        final String item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(items.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        Log.d(TAG, "remove");
        String item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
            items.remove(position);
            mUsers.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        Log.d(TAG, "isPendingRemoval");
        String item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = ConstantManager.TAG_PREFIX + "UserViewHolder ";

        protected AspectRatioImageView mUserPhoto;
        protected TextView mFullName, mRating, mCodeLines, mProjects, mBio;
        protected Button mShowMore;
        protected Drawable mDummy;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            Log.d(TAG, "Constructor");

            this.mListener = customClickListener;

            mUserPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo);
            mFullName = (TextView) itemView.findViewById(R.id.user_full_name_txt);
            mRating = (TextView) itemView.findViewById(R.id.rating_txt);
            mCodeLines = (TextView) itemView.findViewById(R.id.code_lines_txt);
            mProjects = (TextView) itemView.findViewById(R.id.projects_txt);
            mBio = (TextView) itemView.findViewById(R.id.bio_txt);
            mShowMore = (Button) itemView.findViewById(R.id.more_info_btn);

            mDummy = mUserPhoto.getContext().getResources().getDrawable(R.drawable.user_bg);
            mShowMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick");

            if (mListener != null) {
                mListener.onUserItemClickListener(getAdapterPosition());
            }
        }

        public interface CustomClickListener {

            void onUserItemClickListener(int position);
        }
    }
}
