package com.application.onlineTestSeries.forgotPassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.forgotPassword.model.forgotPasswordResponse;
import com.application.onlineTestSeries.login.View.loginActivity;

import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.Utils.checkInternetState;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class forgotPasswordValidate extends AppCompatActivity {
    private final TextWatcher mTextEditorWatcherConfirmPassword = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.toString().trim().length()>=1){
                imgConfirmPassword.setVisibility(View.GONE);
                imgConfirmPassword1.setVisibility(View.VISIBLE);
            }else {
                imgConfirmPassword1.setVisibility(View.GONE);
                imgConfirmPassword.setVisibility(View.VISIBLE);
            }

            if (s.toString().trim().length()>0 && s.toString().trim().length()<3){
                viewConfirmPassword.setBackgroundResource(R.color.colorRed);
            }if (s.toString().trim().length()>3 && s.toString().trim().length()<5){
                viewConfirmPassword.setBackgroundResource(R.color.colorYellow);
            }if (s.toString().trim().length()>5 && s.toString().trim().length()<8){
                viewConfirmPassword.setBackgroundResource(R.color.green);
            }
            if (s.toString().trim().length()<1 ){
                viewConfirmPassword.setBackgroundResource(R.color.colorGrey);
            }
        }
    };
    Button btnCreate;
    ImageView imgPassword,imgPassword1,imgConfirmPassword,imgConfirmPassword1;
    View viewPassword,viewConfirmPassword;
    private final TextWatcher mTextEditorWatcherPassword = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.toString().trim().length()>=1){
                imgPassword.setVisibility(View.GONE);
                imgPassword1.setVisibility(View.VISIBLE);
            }else {
                imgPassword1.setVisibility(View.GONE);
                imgPassword.setVisibility(View.VISIBLE);
            }

            if (s.toString().trim().length()>0 && s.toString().trim().length()<3){
                viewPassword.setBackgroundResource(R.color.colorRed);
            }if (s.toString().trim().length()>3 && s.toString().trim().length()<5){
                viewPassword.setBackgroundResource(R.color.colorYellow);
            }if (s.toString().trim().length()>5 && s.toString().trim().length()<8){
                viewPassword.setBackgroundResource(R.color.green);
            }if (s.toString().trim().length()<1 ){
                viewPassword.setBackgroundResource(R.color.colorGrey);
            }
        }
    };
    TextView tvShowEmail;
    EditText etCode,etPassword,etConfirmPassword;
    LinearLayout layoutPassword;
    private final TextWatcher mTextEditorWatcherCode = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            //inputgroupname.setText(s.toString());
            // textCount.setText(String.valueOf(s.length()));


        }

        public void afterTextChanged(Editable s) {
            if (s.toString().trim().length()!=6){
                layoutPassword.setVisibility(View.GONE);
            }else {
                layoutPassword.setVisibility(View.VISIBLE);
            }
        }
    };
    String emailIntent;
    private Boolean togglepassword = false;
    private Boolean toggleConfirmPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_validate);
        setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Typeface customFont1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");

        tvShowEmail =findViewById(R.id.tvShowEmail);
        etCode =findViewById(R.id.etCode);
        etPassword =findViewById(R.id.etPassword);
        etConfirmPassword =findViewById(R.id.etConfirmPassword);
        layoutPassword =findViewById(R.id.layoutPassword);
        btnCreate =findViewById(R.id.btnProceed);
        imgPassword =findViewById(R.id.imgPassword);
        imgPassword1 =findViewById(R.id.imgPassword1);
        imgConfirmPassword =findViewById(R.id.imgConfirmPassword);
        imgConfirmPassword1 =findViewById(R.id.imgConfirmPassword1);
        viewPassword = findViewById(R.id.viewPassword);
        viewConfirmPassword = findViewById(R.id.viewConfirmPassword);
        btnCreate.setTypeface(customFont1);
        emailIntent= getIntent().getStringExtra("etEmail");
        tvShowEmail.setText(emailIntent);
        etCode.addTextChangedListener(mTextEditorWatcherCode);
        etPassword.addTextChangedListener(mTextEditorWatcherPassword);
        etConfirmPassword.addTextChangedListener(mTextEditorWatcherConfirmPassword);

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
        imgConfirmPassword1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleConfirmPassword==false){
                    imgConfirmPassword1.setImageResource(R.drawable.ic_hide_password);
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    toggleConfirmPassword = true;
                }else{
                    imgConfirmPassword1.setImageResource(R.drawable.ic_show_password);
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    toggleConfirmPassword =false;
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()){
                    hitApi();
                }
            }
        });

    }

    private void hitApi() {
        final String password = etPassword.getText().toString().trim();
        final int keyCode = Integer.parseInt(etCode.getText().toString().trim());

        if (!checkInternetState.Companion.getInstance(this).isOnline()){
            showWarning("No Internet Connection");
        }else{
            final ProgressDialog progressDialog = new ProgressDialog(forgotPasswordValidate.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Changing Password...");
            progressDialog.show();
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<forgotPasswordResponse> userCall = service.updatepassword(emailIntent,password,keyCode);
            userCall.enqueue(new Callback<forgotPasswordResponse>() {
                @Override
                public void onResponse(Call<forgotPasswordResponse> call, Response<forgotPasswordResponse> response) {
                    progressDialog.hide();
                    if (response.isSuccessful()){
                        if (response.body().getStatus()) {

                            showSuccess(response.body().getMessage());
                            Intent intent =new Intent(forgotPasswordValidate.this,loginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            showError(response.body().getMessage());
                        }
                    }else{
                        showError("Something went wrong.");
                    }
                }

                @Override
                public void onFailure(Call<forgotPasswordResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                    progressDialog.hide();
                }
            });
        }
    }

    private boolean validateData() {
        if (etPassword.getText().toString().trim().equals("")) {
            showError("Password is empty");
            return false;
        }
        if (etPassword.getText().toString().trim().length() <= 5) {
            showError("Minimum 6 digit required password");
            return false;
        }
        if (etConfirmPassword.getText().toString().trim().equals("")) {
            showError("Confirm Password is empty");
            return false;
        }
        if (etConfirmPassword.getText().toString().trim().length() <= 5) {
            showError("Minimum 6 digit required password");
            return false;
        }
        if (!etConfirmPassword.getText().toString().trim().equals(etPassword.getText().toString())){
            showError("confirm password is not matched");
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
