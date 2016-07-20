package com.softdesign.devintensive.ui.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.softdesign.devintensive.utils.UiHelper.getDisplayMetrics;

public class ProfileUserActivity extends BaseActivity {

    private static final String TAG = ConstantManager.TAG_PREFIX + "ProfileUserActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.user_photo_img)
    ImageView mProfileImage;
    @BindView(R.id.bio_et)
    EditText mUserBio;
    @BindView(R.id.user_stat_rating)
    TextView mUserRating;
    @BindView(R.id.user_stat_code_lines)
    TextView mUserCodeLines;
    @BindView(R.id.user_stat_projects)
    TextView mUserProjects;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;

    private ListView mRepoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");

        mRepoListView = (ListView) findViewById(R.id.repositories_list);
        setupToolBar();
        initProfileData();

    }

    private void setupToolBar(){
        Log.d(TAG, "setupToolbar");

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initProfileData(){
        Log.d(TAG, "initProfileData");
        UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);

        final String userPhoto;
        final List<String> repositories = userDTO.getRepositories();
        final RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(this, repositories);

        mRepoListView.setAdapter(repositoriesAdapter);

        mRepoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(mCollapsingToolbarLayout, "Репозиторий " + repositories.get(position), Snackbar.LENGTH_LONG).show();
            }
        });

        mUserBio.setText(userDTO.getBio());
        mUserRating.setText(userDTO.getRating());
        mUserCodeLines.setText(userDTO.getCodeLines());
        mUserProjects.setText(userDTO.getProjects());
        mCollapsingToolbarLayout.setTitle(userDTO.getFullName());

        if (userDTO.getPhoto().isEmpty()) {
            Log.e(TAG, "onBindViewHolder: user with name " + userDTO.getFullName() + " has empty photo");
            userPhoto = "null";
        } else {
            userPhoto = userDTO.getPhoto();
        }

        DataManager.getInstance().getPicasso()
                .load(userPhoto)
                .error(R.drawable.user_bg)
                .placeholder(R.drawable.user_bg)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(mProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, " load from cache success");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(userPhoto)
                                .error(R.drawable.user_bg)
                                .placeholder(R.drawable.user_bg)
                                .fit()
                                .centerCrop()
                                .into(mProfileImage, new Callback() {
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
    }

}
