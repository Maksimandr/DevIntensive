package com.softdesign.devintensive.ui.activities;

import android.Manifest;
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
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private boolean mCurrentEditMode;

    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private DataManager mDataManager;

    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.user_photo_img)
    ImageView mProfileImage;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.user_stat)
    LinearLayout mUserStat;
    @BindView(R.id.nested_scroll)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.user_stat_rating)
    TextView mUserStatValueRating;
    @BindView(R.id.user_stat_code_lines)
    TextView mUserStatValueCodeLines;
    @BindView(R.id.user_stat_projects)
    TextView mUserStatValueProjects;

    private View mHeaderLayout;
    private ImageView mUserAvatar;

    private List<EditText> mUserInfoViews;
    private List<TextView> mUserStatViews;
    private List<ImageView> mUserInfoImgAct;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private CoordinatorLayout.LayoutParams mCoordinatorLayoutParams = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        mHeaderLayout = mNavigationView.inflateHeaderView(R.layout.drawer_header);
        mUserAvatar = (ImageView) mHeaderLayout.findViewById(R.id.user_avatar);
        mUserAvatar.setImageDrawable(new RoundedAvatarDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.avatar)));


        int[] mUserInfoResId = {R.id.phone_et, R.id.email_et, R.id.vk_et, R.id.git_et, R.id.bio_et};
        mUserInfoViews = new ArrayList<>();
        for (int i = 0; i < mUserInfoResId.length; i++) {
            mUserInfoViews.add((EditText) findViewById(mUserInfoResId[i]));
        }

        mUserStatViews = new ArrayList<>();
        mUserStatViews.add(mUserStatValueRating);
        mUserStatViews.add(mUserStatValueCodeLines);
        mUserStatViews.add(mUserStatValueProjects);

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
        initUserFields();
        initUserProfileStatistic();

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_bg)
                .into(mProfileImage);

        if (savedInstanceState != null) {
            mCurrentEditMode = savedInstanceState.getBoolean(ConstantManager.EDIT_MODE_KEY, false);
            changeEditMode();
        }
    }

    /**
     * Метод вызывается непосредственно перед тем, как активность становится видимой пользователю
     * Чтение из базы данных
     * Запуск сложной анимации
     * Запуск потоков, отслеживания показаний датчиков, запросов к GPS, таймеров, сервисов или других процессов,
     * которые нужны исключительно для обновления пользовательского интерфейса
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    /**
     * Метод вызывается после onStart().Также может вызываться послеonPause().
     * запуск воспроизведения анимации, аудио и видео
     * инициализации компонентов
     * регистрации любых широковещательных приемников или других процессов, которые вы освободили/приостановили вonPause()
     * выполнение любых другие инициализации, которые должны происходить, когда активность вновь активна (камера).
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * Метод вызывается после сворачивания текущей активности или перехода к новому.
     * От onPause()можно перейти к вызову либо onResume(), либо onStop().
     * <p/>
     * остановка анимации, аудио и видео
     * фиксация несохраненных данных (легкие процессы)
     * освобождение системных ресурсов
     * остановка сервисов, подписок, широковещательных сообщений
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        saveUserFields();
    }

    /**
     * Метод вызывается, когда окно становится невидимым для пользователя.
     * Это может произойти при её уничтожении, или если была запущена другая активность (существующая или новая),
     * перекрывшая окно текущей активности.
     * <p/>
     * запись в базу данных
     * приостановка сложной анимации
     * приостановка потоков, отслеживания показаний датчиков, запросов к GPS, таймеров,
     * сервисов или других процессов, которые нужны исключительно для обновления пользовательского интерфейса
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /**
     * Метод вызывается по окончании работы активности, при вызове методаfinish()или в случае, когда система уничтожает этот экземпляр активности для освобождения ресурсов.
     * <p/>
     * высвобождение ресурсов
     * дополнительная перестраховка если ресурсы не были выгружены или процессы не были остановлены ранее
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /**
     * Если окно возвращается в приоритетный режим после вызоваonStop(), то в этом случае вызывается методonRestart().
     * Т.е. вызывается после того, как активность была остановлена и снова была запущена пользователем.
     * Всегда сопровождается вызовом метода onStart().
     * <p/>
     * используется для специальных действий, которые должны выполняться только при повторном запуске активности
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    /**
     * Обрабатывает нажатия на экран
     *
     * @param v view на которое произшло нажатие
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {
            case R.id.fab:
                changeEditMode();
                break;

            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;

            case R.id.phone_iv:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mUserInfoViews.get(0).getText().toString())));
                break;

            case R.id.email_iv:
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mUserInfoViews.get(1).getText().toString())));
                break;

            case R.id.vk_iv:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + mUserInfoViews.get(2).getText().toString())));
                break;

            case R.id.git_iv:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + mUserInfoViews.get(3).getText().toString())));
                break;

        }
    }

    /**
     * Обрабатывает нажатие кнопки Back
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    /**
     * Сохраняет текущее состояние режима редактирования
     *
     * @param outState набор параметров приложения
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

        outState.putBoolean(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    /**
     * Открывает Drawer при нажатии на кновку меню в Toolbar
     *
     * @param item view на которое произшло нажатие в Toolbar
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");

        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Получение результата из другой Activity (получает фото для профиля пользователя)
     *
     * @param requestCode идентификатор запроса
     * @param resultCode  код результата запроса
     * @param data        возвращённые данные
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);
                }
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

    /**
     * Задает Toolbar и заменяет им Actionbar
     */
    private void setupToolbar() {
        Log.d(TAG, "setupToolbar");

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Задает обработчик выбора пункта меню в Drawer
     */
    private void setupDrawer() {
        Log.d(TAG, "setupDrawer");

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
     * Меняет статус режима редактирования и вызывает проверку введённых данных
     */
    private void changeEditMode() {
        Log.d(TAG, "changeEditMode");

        if (mCurrentEditMode && !checkInputUserData()) {
            return;
        }

        mCurrentEditMode = !mCurrentEditMode;

        for (EditText userValue : mUserInfoViews) {
            userValue.setEnabled(mCurrentEditMode);
            userValue.setFocusable(mCurrentEditMode);
            userValue.setFocusableInTouchMode(mCurrentEditMode);
        }

        if (mCurrentEditMode) {
            mFab.setImageResource(R.drawable.ic_check_black_24dp);

            showProfilePlaceholder();
            hideUserStat();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

            mUserInfoViews.get(0).requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(mUserInfoViews.get(0), 0);
            }

        } else {
            mFab.setImageResource(R.drawable.ic_edit_black_24dp);

            hideProfilePlaceholder();
            showUserStat();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

            saveUserFields();
        }
    }

    /**
     * Загружает сохранённые пользовательские данные
     */
    private void initUserFields() {
        Log.d(TAG, "initUserFields");

        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileFields();
        for (int i = 0; i < userData.size(); i++) {
            if (userData.get(i) == null) {
                continue;
            }
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Сохраняет пользовательские данные
     */
    private void saveUserFields() {
        Log.d(TAG, "saveUserFields");

        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileFields(userData);
    }

    private void initUserProfileStatistic() {
        Log.d(TAG, "initUserProfileStatistic");

        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileStatistic();
        for (int i = 0; i < userData.size(); i++) {
            mUserStatViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Запрашивает разрешение на доступ к внешней памяти и фото для профиля пользователя из галлереи
     */
    private void loadPhotoFromGallery() {
        Log.d(TAG, "loadPhotoFromGallery");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            takeGalleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_chooser_message)), ConstantManager.REQUEST_GALLERY_PICTURE);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, R.string.permition_request_message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.permition_request_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    /**
     * Запрашивает разрешение на доступ к внешней памяти и камере и фото для профиля пользователя с камеры
     */
    private void loadPhotoFromCamera() {
        Log.d(TAG, "loadPhotoFromCamera");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                permissions = new String[]{Manifest.permission.CAMERA};
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            } else {
                permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            }

            ActivityCompat.requestPermissions(this, permissions, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, R.string.permition_request_message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.permition_request_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    /**
     * Обрабатывает результат запросов на доступ к внешней памяти и камере
     *
     * @param requestCode  идентификатор запроса
     * @param permissions  разрешения
     * @param grantResults код результата
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        if (requestCode == ConstantManager.EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPhotoFromGallery();
            }
        } else if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE) {
            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    || (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                loadPhotoFromCamera();
            }
        }
    }

    /**
     * Скрывает раздел со статистикой пользователя (во время редактирования данных профиля)
     */
    private void hideUserStat() {
        Log.d(TAG, "hideUserStat");

        mUserStat.setVisibility(View.GONE);
    }

    /**
     * Показывает раздел со статистикой пользователя
     */
    private void showUserStat() {
        Log.d(TAG, "showUserStat");

        mUserStat.setVisibility(View.VISIBLE);
    }

    /**
     * Скрывает ProfilePlaceholder
     */
    private void hideProfilePlaceholder() {
        Log.d(TAG, "hideProfilePlaceholder");

        mProfilePlaceholder.setVisibility(View.GONE);
    }

    /**
     * Показывает ProfilePlaceholder (во время редактирования данных профиля)
     */
    private void showProfilePlaceholder() {
        Log.d(TAG, "showProfilePlaceholder");

        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    /**
     * Закрепляет Toolbar, не позволяя ему сворачиваться
     */
    private void lockToolbar() {
        Log.d(TAG, "lockToolbar");

        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Делает Toolbar сворачиваемым
     */
    private void unlockToolbar() {
        Log.d(TAG, "unlockToolbar");

        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Создает меню выбора источника для получения фото для профиля пользователя
     *
     * @param id идентификатор
     * @return объект Dialog
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Log.d(TAG, "onCreateDialog");

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

    /**
     * Создает новый файл для сохранения в него фото полученного с камеры
     *
     * @return объект File
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        Log.d(TAG, "createImageFile");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        }

        File image = new File(storageDir, imageFileName);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    /**
     * Вставляет изображение в профиль пользователя
     *
     * @param selectedImage изображение для профиля пользователя
     */
    private void insertProfileImage(Uri selectedImage) {
        Log.d(TAG, "insertProfileImage");

        Picasso.with(this)
                .load(selectedImage)
                .resize(getResources().getDimensionPixelSize(R.dimen.profile_image_size_256), getResources().getDimensionPixelSize(R.dimen.profile_image_size_256))
                .centerCrop()
                .into(mProfileImage);
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage, new Date().toString());
    }

    /**
     * открывает настройки разрешений приложения
     */
    private void openApplicationSettings() {
        Log.d(TAG, "openApplicationSettings");

        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    /**
     * Выполняет проверку данных, введенных пользователем в профиль
     *
     * @return true если данные корректные
     */
    private boolean checkInputUserData() {
        Log.d(TAG, "checkInputUserData");

        return (checkInputPhone() && checkInputEmail() && checkInputVk() && checkInputGit());
    }

    /**
     * Выполняет проверку данных, введенных пользователем в поле телефона, на соответствие шаблону
     *
     * @return true если данные корректные
     */
    private boolean checkInputPhone() {
        Log.d(TAG, "checkInputPhone");

        String tel = mUserInfoViews.get(0).getText().toString();
        String message;

        if (tel.length() >= 11 && tel.length() <= 20) {
            Pattern pattern = Pattern.compile("[^0-9 +()-]");
            Matcher matcher = pattern.matcher(tel);
            if (!matcher.find()) {
                return true;
            } else {
                message = getString(R.string.check_input_phone_symbols);
            }
        } else {
            message = getString(R.string.check_input_phone_length);
        }

        mUserInfoViews.get(0).requestFocus();
        mUserInfoViews.get(0).setError(message);
        return false;
    }

    /**
     * Выполняет проверку данных, введенных пользователем в поле почты, на соответствие шаблону
     *
     * @return true если данные корректные
     */
    private boolean checkInputEmail() {
        Log.d(TAG, "checkInputEmail");

        String email = mUserInfoViews.get(1).getText().toString();
        String message;

        String[] hostPart = email.split("@");
        if (hostPart.length == 2) {
            String[] domainPart = hostPart[1].split("\\.");
            if (domainPart.length == 2) {
                if (hostPart[0].length() >= 3 && domainPart[0].length() >= 2 && domainPart[1].length() >= 2) {
                    return true;
                }
            }
        }
        message = getString(R.string.check_input_email);
        mUserInfoViews.get(1).requestFocus();
        mUserInfoViews.get(1).setError(message);
        return false;
    }

    /**
     * Выполняет проверку данных, введенных пользователем в поле страницы в ВК, на соответствие шаблону
     *
     * @return true если данные корректные
     */
    private boolean checkInputVk() {
        Log.d(TAG, "checkInputVk");

        String vk = mUserInfoViews.get(2).getText().toString();
        String message;
        String urlPart;

        int i = vk.indexOf(getString(R.string.url_vk_com));
        if (i >= 0) {
            urlPart = vk.substring(i);
            if (urlPart.length() > getString(R.string.url_vk_com).length()) {
                mUserInfoViews.get(2).setText(urlPart);
                return true;
            } else {
                message = getString(R.string.check_input_vk_nopage);
            }

        } else {
            message = getString(R.string.check_input_vk);
        }

        mUserInfoViews.get(2).requestFocus();
        mUserInfoViews.get(2).setError(message);
        return false;
    }

    /**
     * Выполняет проверку данных, введенных пользователем в поле репозитория GIT, на соответствие шаблону
     *
     * @return true если данные корректные
     */
    private boolean checkInputGit() {
        Log.d(TAG, "checkInputGit");

        String git = mUserInfoViews.get(3).getText().toString();
        String message;
        String urlPart;

        int i = git.indexOf(getString(R.string.url_github_com));
        if (i >= 0) {
            urlPart = git.substring(i);
            if (urlPart.length() > getString(R.string.url_github_com).length()) {
                mUserInfoViews.get(3).setText(urlPart);
                return true;
            } else {
                message = getString(R.string.check_input_git_norepo);
            }

        } else {
            message = getString(R.string.check_input_git);
        }

        mUserInfoViews.get(3).requestFocus();
        mUserInfoViews.get(3).setError(message);
        return false;
    }

//    private static final String IMGUR_CLIENT_ID = "...";
//    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
//
//    private final OkHttpClient client = new OkHttpClient();
//
//    public void run() throws Exception {
//        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("title", "Square Logo")
//                .addFormDataPart("image", "logo-square.png",
//                        RequestBody.create(MEDIA_TYPE_PNG, new File("website/static/logo-square.png")))
//                .build();
//
//        Request request = new Request.Builder()
//                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
//                .url("https://api.imgur.com/3/image")
//                .post(requestBody)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
//        System.out.println(response.body().string());
//    }
}