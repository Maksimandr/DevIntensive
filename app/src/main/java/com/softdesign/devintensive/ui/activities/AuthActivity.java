package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.softdesign.devintensive.utils.SplashScreen;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Auth Activity";

    @BindView(R.id.login_btn)
    Button mSignIn;
    @BindView(R.id.remember_txt)
    TextView mRememberPassword;
    @BindView(R.id.login_email_et)
    EditText mLogin;
    @BindView(R.id.login_password_et)
    EditText mPassword;
    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;
    private UserDao mUserDao;
    private RepositoryDao mRepositoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");

        EventBus.getDefault().register(this);

        mDataManager = DataManager.getInstance();
        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");

        switch (view.getId()) {
            case R.id.login_btn:
                signIn();
                break;
            case R.id.remember_txt:
                rememberPassword();
                break;
        }
    }

    /**
     * Показывает сообщение в Snackbar
     *
     * @param message показываемое сообщение
     */
    private void showSnackbar(String message) {
        Log.d(TAG, "showSnackbar");

        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword() {
        Log.d(TAG, "rememberPassword");

        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    private void loginSuccess(UserModelRes userModel) {
        Log.d(TAG, "loginSuccess");

        saveUserProfileData(userModel);
        saveUserInDb();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(AuthActivity.this, UserListActivity.class);
                EventBus.getDefault().post(new SplashScreen(1));

                startActivity(loginIntent);
            }
        }, AppConfig.START_DELAY);

    }

    private void signIn() {
        Log.d(TAG, "signIn");

        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(), mPassword.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        EventBus.getDefault().post(new SplashScreen(200));
                        loginSuccess(response.body());
                    } else if (response.code() == 404) {
                        showSnackbar("Неверные логин или пароль");
                    } else {
                        showSnackbar("Всё пропало Шеф!!!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    showSnackbar("Всё пропало Шеф!!!");
                }
            });
        } else {
            showSnackbar("Сеть не доступна, попробуйте позже.");
        }
    }

    private void saveUserProfileData(UserModelRes userModel) {
        Log.d(TAG, "saveUserProfileData");

        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());

        List<String> userName = new ArrayList<>();
        userName.add(userModel.getData().getUser().getFirstName());
        userName.add(userModel.getData().getUser().getSecondName());
        mDataManager.getPreferencesManager().saveUserName(userName);

        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserProfileStatistic(userValues);

        List<String> userFields = new ArrayList<>();
        userFields.add(userModel.getData().getUser().getContacts().getPhone());
        userFields.add(userModel.getData().getUser().getContacts().getEmail());
        userFields.add(userModel.getData().getUser().getContacts().getVk());
        if (userModel.getData().getUser().getRepositories().getRepo().size() > 0) {
            userFields.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        } else {
            userFields.add("");
        }
        userFields.add(userModel.getData().getUser().getPublicInfo().getBio());

        mDataManager.getPreferencesManager().saveUserProfileFields(userFields);

        mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto()), userModel.getData().getUser().getPublicInfo().getUpdated());
        mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar()));
    }

    private void saveUserInDb() {
        Log.d(TAG, "saveUserInDb");

        Call<UserListRes> call = mDataManager.getUserListFromNetwork();
        call.enqueue(new Callback<UserListRes>() {

            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {

                try {
                    if (response.code() == 200) {
                        Log.d(TAG, "List request success");

                        List<Repository> allRepositories = new ArrayList<Repository>();
                        List<User> allUsers = new ArrayList<User>();

                        for (UserListRes.UserData userRes : response.body().getData()) {
                            allRepositories.addAll(getRepoListFromUserRes(userRes));
                            allUsers.add(new User(userRes));
                        }

                        mRepositoryDao.insertOrReplaceInTx(allRepositories);
                        mUserDao.insertOrReplaceInTx(allUsers);

                    } else {
                        Log.d(TAG, "List request FAIL!");
                        showSnackbar("Список пользователей не может быть получен");
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    showSnackbar("Что-то пошло не так");
                }

            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                Log.d(TAG, "List request Error: " + t.getMessage());
            }
        });
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {
        Log.d(TAG, "getRepoListFromUserRes");

        final String userId = userData.getId();

        List<Repository> repositories = new ArrayList<>();
        for (UserModelRes.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }

        return repositories;
    }

    @Subscribe
    public void onSplashScreen(SplashScreen event) {
        Log.d(TAG, "onSplashScreen");

        if (event.code == 200) {
            showProgress();
        } else if (event.code == 1){
            hideProgress();
        }
    }
}
