package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;
import com.softdesign.devintensive.utils.UserScoreBehavior;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private boolean mCurrentEditMode;
    private List<EditText> mUserInfoView;
    private List<ImageView> mUserInfoImgAct;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;

    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private ImageView mUserAvatar;
    private ImageView mProfileImage;
    private NavigationView mNavigationView;
    private View mHeaderLayout;
    private FloatingActionButton mFab;
    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private UserScoreBehavior mUserScoreBehavior;
    private LinearLayout mUserScore;
    private NestedScrollView mNestedScrollView;
    private AppBarLayout mAppBarLayout;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private CoordinatorLayout.LayoutParams mCoordinatorLayoutParams = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();


        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mProfilePlaceholder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mUserScore = (LinearLayout) findViewById(R.id.user_score);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);


        mHeaderLayout = mNavigationView.inflateHeaderView(R.layout.drawer_header);
        mUserAvatar = (ImageView) mHeaderLayout.findViewById(R.id.user_avatar);
        mUserAvatar.setImageDrawable(new RoundedAvatarDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.avatar)));

        int[] mUserInfoResId = {R.id.phone_et, R.id.email_et, R.id.vk_et, R.id.git_et, R.id.bio_et};
        mUserInfoView = new ArrayList<>();
        for (int i = 0; i < mUserInfoResId.length; i++) {
            mUserInfoView.add((EditText) findViewById(mUserInfoResId[i]));
        }

        int[] mUserInfoIvResId = {R.id.phone_iv, R.id.email_iv, R.id.vk_iv, R.id.git_iv};
        mUserInfoImgAct = new ArrayList<>();
        for (int i = 0; i < mUserInfoIvResId.length; i++) {
            mUserInfoImgAct.add((ImageView) findViewById(mUserInfoIvResId[i]));
            mUserInfoImgAct.get(i).setOnClickListener(this);
        }

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        loadUserInfoValue();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.userphoto) // TODO: 07.07.2016 сделать плайсхолдер и transform + crop
                .into(mProfileImage);

        if (savedInstanceState != null) {
            mCurrentEditMode = savedInstanceState.getBoolean(ConstantManager.EDIT_MODE_KEY, false);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserInfoValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    /**
     * Обрабатывает нажатия на кнопки
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                mCurrentEditMode = !mCurrentEditMode;
                changeEditMode(mCurrentEditMode);
                break;

            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;

            case R.id.phone_iv:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mUserInfoView.get(0).getText().toString()));
                if (dialIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(dialIntent);
                }
                break;
            case R.id.email_iv:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mUserInfoView.get(1).getText().toString()));
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                }
                break;
            case R.id.vk_iv:
                Intent vkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + mUserInfoView.get(2).getText().toString()));
                if (vkIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(vkIntent);
                }
                break;
            case R.id.git_iv:
                Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + mUserInfoView.get(3).getText().toString()));
                if (gitIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(gitIntent);
                }
                break;

        }
    }

    /**
     * Обрабатывает нажатие кнопки Back
     */
    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    /**
     * Сохраняет текущее состояние режима редактирования
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    /**
     * Открывает Drawer при нажатии на кновку меню в Toolbar
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Получение результата из другой Activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                Log.d(TAG, resultCode + ", " + data.toString());
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                Log.d(TAG, resultCode + ", " + mPhotoFile.toString());
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
        }
    }

    /**
     * Показывает сообщение в Snackbar
     *
     * @param message
     */
    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Выполняет что-то с задержкой
     */
    private void RanWithDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        }, 5000);
    }

    /**
     * Задает Toolbar и заменяет им Actionbar
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();

        mUserScoreBehavior = new UserScoreBehavior();
        mCoordinatorLayoutParams = (CoordinatorLayout.LayoutParams) mUserScore.getLayoutParams();
        mCoordinatorLayoutParams.setBehavior(mUserScoreBehavior);

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Задает обработчик выбора пункта меню в Drawer
     */
    private void setupDrawer() {
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**
     * Меняет статус режима редактирования
     *
     * @param mode
     */
    private void changeEditMode(boolean mode) {
        for (EditText userValue : mUserInfoView) {
            userValue.setEnabled(mode);
            userValue.setFocusable(mode);
            userValue.setFocusableInTouchMode(mode);
        }
        mUserInfoView.get(0).requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(mUserInfoView.get(0), InputMethodManager.SHOW_IMPLICIT);
        }
        if (mode) {
            mFab.setImageResource(R.drawable.ic_check_black_24dp);

            showProfilePlaceholder();
            hideUserScore();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

        } else {
            mFab.setImageResource(R.drawable.ic_edit_black_24dp);

            hideProfilePlaceholder();
            showUserScore();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

            saveUserInfoValue();

        }
    }

    /**
     * Загружает сохранённые пользовательские данные
     */
    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            if (userData.get(i) == null) {
                continue;
            }
            mUserInfoView.get(i).setText(userData.get(i));
        }
    }

    /**
     * Сохраняет пользовательские данные
     */
    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoView) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void loadPhotoFromGallery() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            takeGalleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_chooser_message)), ConstantManager.REQUEST_GALLERY_PICTURE);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.EXTERNAL_STORAGE_REQUEST_PERMITION_CODE);

            Snackbar.make(mCoordinatorLayout, R.string.permition_request_message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.permition_request_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }

    }

    private void loadPhotoFromCamera() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            String[] permissions;
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                permissions = new String[] {android.Manifest.permission.CAMERA};
            } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                permissions = new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            } else {
                permissions = new String[] {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            }

            ActivityCompat.requestPermissions(this, permissions, ConstantManager.CAMERA_REQUEST_PERMITION_CODE);

            Snackbar.make(mCoordinatorLayout, R.string.permition_request_message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.permition_request_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMITION_CODE || requestCode == ConstantManager.EXTERNAL_STORAGE_REQUEST_PERMITION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    private void hideUserScore() {
        mCoordinatorLayoutParams.setBehavior(null);
        mCoordinatorLayoutParams.height = 0;
        mNestedScrollView.setPadding(mNestedScrollView.getPaddingLeft(), 0, mNestedScrollView.getPaddingRight(), mNestedScrollView.getPaddingBottom());
    }

    private void showUserScore() {
        mCoordinatorLayoutParams.setBehavior(mUserScoreBehavior);

        mUserScore.setY(mNestedScrollView.getY());
        mCoordinatorLayoutParams.height = (int) (mNestedScrollView.getY() * 0.227 + 107.776);
        mNestedScrollView.setPadding(mNestedScrollView.getPaddingLeft(), mCoordinatorLayoutParams.height, mNestedScrollView.getPaddingRight(), mNestedScrollView.getPaddingBottom());
    }

    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery), getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.user_profile_dialog_title);
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int chooseItem) {
                        switch (chooseItem) {
                            case 0:
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                break;
                            case 2:
                                dialog.cancel();
                                break;

                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        File image = new File(storageDir, imageFileName);

        ContentValues values= new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage) // TODO: 07.07.2016 сделать плайсхолдер и transform + crop
                .into(mProfileImage);
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMITION_REQUEST_SETTINGS_CODE);
    }
}