package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListActivity extends AppCompatActivity {

    private static final String TAG = ConstantManager.TAG_PREFIX + "UserList Activity";

    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.user_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    private View mHeaderLayout;
    private ImageView mUserAvatar;
    private MenuItem mSearchItem;

    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private List<User> mUsers;
    private String mQuery;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(LayoutManager);

        mHandler = new Handler();

        setupToolBar();
        setupDrawer();
        loadUsersFromDb();


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        mSearchItem = menu.findItem(R.id.toolbar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchView.setQueryHint(getString(R.string.toolbar_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                showUserByQuery(search);
                return false;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {
            case android.R.id.home:
                mNavigationDrawer.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackBar(String message) {
        Log.d(TAG, "showSnackBar");

        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void loadUsersFromDb() {
        Log.d(TAG, "loadUsersFromDb");

        if (mDataManager.getUserListFromDb().size() == 0) {
            showSnackBar("Список пользователей не может быть загружен");
        } else {
            showUsers(mDataManager.getUserListFromDb());
        }

    }

    private void setupDrawer() {
        Log.d(TAG, "setupDrawer");

        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());

                switch (item.getItemId()) {
                    case R.id.user_profile_menu:
                        startActivity(new Intent(UserListActivity.this, MainActivity.class));
                        break;
                    case R.id.team_menu:
                        break;
                }
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        mHeaderLayout = mNavigationView.inflateHeaderView(R.layout.drawer_header);
        mUserAvatar = (ImageView) mHeaderLayout.findViewById(R.id.user_avatar);
        mUserAvatar.setImageDrawable(new RoundedAvatarDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.avatar)));
    }

    private void setupToolBar() {
        Log.d(TAG, "setupToolBar");

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showSnackbar(String message) {
        Log.d(TAG, "showSnackbar");

        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showUsers(List<User> users) {
        mUsers = users;
        mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int position) {
                UserDTO userDTO = new UserDTO(mUsers.get(position));

                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);

                startActivity(profileIntent);
            }
        });

        mRecyclerView.swapAdapter(mUsersAdapter, false);
    }

    private void showUserByQuery(String query) {
        mQuery = query;

        Runnable searchUsers = new Runnable() {
            @Override
            public void run() {
                showUsers(mDataManager.getUserListByName(mQuery));
            }
        };

        mHandler.removeCallbacks(searchUsers);
        mHandler.postDelayed(searchUsers, ConstantManager.SEARCH_DELAY);
    }

}
