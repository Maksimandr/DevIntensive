package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
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

        showSnackbar(userModel.getData().getToken());

        saveUserProfileData(userModel);

        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void signIn() {
        Log.d(TAG, "signIn");

        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(), mPassword.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccess(response.body());
                    } else if (response.code() == 404) {
                        showSnackbar("Неверные логин или пароль");
                    } else {
                        showSnackbar("Всё пропало Шеф!!!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    // TODO: 12.07.2016  обработать ошибки
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
                userModel.getData().getUser().getProfileValues().getRaiting(),
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

        if(mDataManager.getPreferencesManager().getUserPhotoUpdated().compareToIgnoreCase(userModel.getData().getUser().getPublicInfo().getUpdated())<0){
            mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto()), userModel.getData().getUser().getPublicInfo().getUpdated());
        }

        mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar()));
    }
}
