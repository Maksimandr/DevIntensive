package com.softdesign.devintensive.ui.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.List;

public class RepositoriesAdapter extends BaseAdapter {

    private static final String TAG = ConstantManager.TAG_PREFIX + "ReposAdapter ";

    private List<String> mRepoList;

    public RepositoriesAdapter(Context context, List<String> repoList) {
        Log.d(TAG, "Constructor");

        mRepoList = repoList;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private Context mContext;
    private LayoutInflater mInflater;

    @Override
    public int getCount() {
        return mRepoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRepoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;

        if (itemView == null) {
            itemView = mInflater.inflate(R.layout.item_repositories_list, parent, false);
        }

        TextView repoName = (TextView) itemView.findViewById(R.id.git_txt);
        repoName.setText(mRepoList.get(position));

        return itemView;
    }
}
