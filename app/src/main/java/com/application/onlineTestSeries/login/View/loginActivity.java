package com.application.onlineTestSeries.login.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.Home.MainActivity;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Utils.checkInternetState;
import com.application.onlineTestSeries.forgotPassword.fogotPassswordActivity;
import com.application.onlineTestSeries.login.Models.LoginData;
import com.application.onlineTestSeries.login.Models.UserData;
import com.application.onlineTestSeries.register.View.register_activity;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class loginActivity extends AppCompatActivity {
    EditText etEmail,etPassword;
    Button BtnLogin;
    TextView BtnGoSignUp;
    Realm realmDB;
    ImageView imgPassword,imgPassword1;
    static boolean status;
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            //inputgroupname.setText(s.toString());
            // textCount.setText(String.valueOf(s.length()));
        }

        public void afterTextChanged(Editable s) {
            if (s.toString().trim().length()>=1){
                imgPassword.setVisibility(View.GONE);
                imgPassword1.setVisibility(View.VISIBLE);
            }else {
                imgPassword1.setVisibility(View.GONE);
                imgPassword.setVisibility(View.VISIBLE);
            }
        }
    };
    TextView btnForgotPassword;
    private Boolean togglepassword = false;
    CountDownTimer countDownTimer;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//to hide keyboad
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/LatoLight.ttf");
        Typeface customFont1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");

        progressDialog = new ProgressDialog(loginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);

        progressDialog.setMessage("Authenticating...");


        etEmail = findViewById(R.id.userEmail);
        etPassword = findViewById(R.id.userPassword);
        BtnLogin = findViewById(R.id.userSubmit);
        BtnGoSignUp = findViewById(R.id.goSignUp);
        imgPassword =findViewById(R.id.imgPassword);
        imgPassword1 =findViewById(R.id.imgPassword1);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        etPassword.addTextChangedListener(mTextEditorWatcher);

        etEmail.setTypeface(customFont);
        etPassword.setTypeface(customFont);
        btnForgotPassword.setTypeface(customFont1);
        BtnLogin.setTypeface(customFont1);
        BtnGoSignUp.setTypeface(customFont1);
        imgPassword1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (togglepassword==false){
                    imgPassword1.setImageResource(R.drawable.ic_hide_password);
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    togglepassword = true;
                }else{
                    imgPassword1.setImageResource(R.drawable.ic_show_password);
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    togglepassword =false;
                }
            }
        });

        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()){
                    getDataFromServer();
                }
            }
        });
        BtnGoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginActivity.this, register_activity.class));
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginActivity.this, fogotPassswordActivity.class));

            }
        });

        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (!status) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showWarning("Connection time out please try again");
                }
            }
        };

    }

    private void getDataFromServer() {
        if (!checkInternetState.Companion.getInstance(this).isOnline()){
            showWarning("No Internet Connection");
        }else{
            hitApi();
        }
    }

    private void hitApi() {
        countDownTimer.start();
        progressDialog.show();
        final String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<LoginData> userCall = service.getData(email,password);
        userCall.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                //progressDialog.dismiss();
                if (response.isSuccessful()){
                    if (response.body().getStatus()) {

                        RealmResults<UserData> result = realmDB.where(UserData.class).equalTo("userId", response.body().getUserData().getUserId()).findAll();
                        if (result.size() == 0){
                            realmDB.beginTransaction();
                            UserData data = realmDB.createObject(UserData.class,response.body().getUserData().getUserId());
                            data.setValidity(response.body().getUserData().getValidity());
                            data.setStartDate(response.body().getUserData().getStartDate());
                            data.setActive(response.body().getUserData().getActive());
                            data.setEmail(response.body().getUserData().getEmail());
                            data.setDateofbirth(response.body().getUserData().getDateofbirth());
                            data.setGender(response.body().getUserData().getGender());
                            data.setFirstInstall(response.body().getUserData().getFirstInstall());
                            data.setPnone(response.body().getUserData().getPnone());
                            data.setImage(response.body().getUserData().getImage());
                            data.setName(response.body().getUserData().getName());
                            realmDB.commitTransaction();
                            status = true;
                        }
                        countDownTimer.onFinish();
                        showSuccess(response.body().getMessage());
                        Intent i = new Intent(loginActivity.this,MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }else{
                        progressDialog.dismiss();
                        showError(response.body().getMessage());
                    }
                }else{
                    progressDialog.dismiss();
                    showError("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                Log.d("onFailure", t.toString());
                progressDialog.dismiss();
            }
        });

    }

    private boolean validateData() {

        if (etEmail.getText().toString().trim().equals("")){
            showError("Email is empty");
            return false;
        }
        if (!isEmailValid(etEmail.getText().toString().trim())) {
            showError("Email type is not correct");
            return false;
        }
        if (etPassword.getText().toString().trim().equals("")){
            showError("Password is empty");
            return false;
        }

        return true;
    }

    private void showError(String s) {
        Toasty.error(getApplicationContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showWarning(String s) {
        Toasty.warning(getApplicationContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showSuccess(String s) {
        Toasty.success(getApplicationContext(), s, Toast.LENGTH_LONG, true).show();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
