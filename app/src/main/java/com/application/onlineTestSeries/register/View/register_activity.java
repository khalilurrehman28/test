package com.application.onlineTestSeries.register.View;

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
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Utils.checkInternetState;
import com.application.onlineTestSeries.login.View.loginActivity;
import com.application.onlineTestSeries.register.Models.RegisterResponse;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class register_activity extends AppCompatActivity {
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
    EditText etUserName, etEmail, etPassword, etConfirmPassword, etMobile, etDOB;
    Button btnRegister;
    Spinner dateOfMonth,monthOfYear,yearNumber;
    TextView BtnGoToLogin,tvGender;
    RadioButton userMale, userFemale;
    ImageView imgPassword,imgPassword1,imgConfirmPassword,imgConfirmPassword1;
    View viewPassword,viewConfirmPassword;
    ArrayAdapter<String> spinnerAdapterDate;
    ArrayAdapter<String> spinnerAdapterMonth;
    ArrayAdapter<String> spinnerAdapterYear;
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
    private Boolean togglepassword = false;
    private Boolean toggleConfirmPassword = false;
    static boolean status;
    CountDownTimer countDownTimer;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//to hide keyboad
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/LatoLight.ttf");
        Typeface customFont1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");

        etUserName = findViewById(R.id.userName);
        etEmail = findViewById(R.id.userEmail);
        etPassword = findViewById(R.id.userPassword);
        etConfirmPassword = findViewById(R.id.userPasswordConfirm);
        etMobile = findViewById(R.id.userMobile);
        //etDOB = findViewById(R.id.dateofbirth);
        dateOfMonth = findViewById(R.id.date);
        monthOfYear = findViewById(R.id.month);
        yearNumber = findViewById(R.id.year);

        btnRegister = findViewById(R.id.registerUser);
        BtnGoToLogin = findViewById(R.id.goLogin);
        userMale = findViewById(R.id.userMale);
        userFemale = findViewById(R.id.userFemale);
        tvGender= findViewById(R.id.tvGender);
        etMobile.setImeOptions(EditorInfo.IME_ACTION_DONE);

        etEmail.setTypeface(customFont);
        etPassword.setTypeface(customFont);
        etUserName.setTypeface(customFont);
        etConfirmPassword.setTypeface(customFont);
        etMobile.setTypeface(customFont);
        //etDOB.setTypeface(customFont);
        tvGender.setTypeface(customFont);
        btnRegister.setTypeface(customFont1);
        BtnGoToLogin.setTypeface(customFont1);

        imgPassword =findViewById(R.id.imgPassword);
        imgPassword1 =findViewById(R.id.imgPassword1);
        imgConfirmPassword =findViewById(R.id.imgConfirmPassword);
        imgConfirmPassword1 =findViewById(R.id.imgConfirmPassword1);
        viewPassword = findViewById(R.id.viewPassword);
        viewConfirmPassword = findViewById(R.id.viewConfirmPassword);

        etPassword.addTextChangedListener(mTextEditorWatcherPassword);
        etConfirmPassword.addTextChangedListener(mTextEditorWatcherConfirmPassword);

        spinnerAdapterDate = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapterMonth = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);

        progressDialog = new ProgressDialog(register_activity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);

        progressDialog.setMessage("Please wait...");

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



        spinnerAdapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateOfMonth.setAdapter(spinnerAdapterDate);
        spinnerAdapterDate.add("Date");
        for (int i = 1; i <=31 ; i++) {
            spinnerAdapterDate.add(String.valueOf(i));
        }
        spinnerAdapterDate.notifyDataSetChanged();

        spinnerAdapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthOfYear.setAdapter(spinnerAdapterMonth);
        spinnerAdapterMonth.add("Month");
        for (int i = 1; i <=12 ; i++) {
            spinnerAdapterMonth.add(String.valueOf(i));
        }
        spinnerAdapterMonth.notifyDataSetChanged();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        spinnerAdapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearNumber.setAdapter(spinnerAdapterYear);
        spinnerAdapterYear.add("Year");
        for (int i = 1950; i <=year ; i++) {
            spinnerAdapterYear.add(String.valueOf(i));
        }
        spinnerAdapterYear.notifyDataSetChanged();
        userMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFemale.setChecked(false);
                userMale.setChecked(true);
            }
        });

        userFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMale.setChecked(false);
                userFemale.setChecked(true);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()) {
                    getDataFromServer();
                }
            }
        });
        BtnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

    }

    private void getDataFromServer() {
        if (!checkInternetState.Companion.getInstance(this).isOnline()) {
            showWarning("No Internet Connection");
        } else {
            hitApi();
        }
    }

    private void hitApi() {
        String userName = etUserName.getText().toString();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        String mobile = etMobile.getText().toString();
       // String DOB = etDOB.getText().toString().trim();
        String DOB = dateOfMonth.getSelectedItem().toString()+"/"+monthOfYear.getSelectedItem().toString()+"/"+yearNumber.getSelectedItem().toString();
        int gender;
        if (userMale.isChecked()) {
            gender = 1;
        } else {
            gender = 2;
        }

        countDownTimer.start();
        progressDialog.show();

        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<RegisterResponse> userCall = service.registerUsers(userName, email, mobile, gender, DOB, password);
        userCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        countDownTimer.onFinish();
                        showSuccess(response.body().getMessage());
                        Intent i = new Intent(register_activity.this, loginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        progressDialog.dismiss();
                        showError(response.body().getMessage());
                    }
                } else {
                    progressDialog.dismiss();
                    showError("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
                progressDialog.dismiss();
            }
        });

    }

    private boolean validateData() {
        //Toast.makeText(this, ""+dateOfMonth.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

        if (etUserName.getText().toString().trim().equals("")) {
            showError("Name is empty");
            return false;
        }

        if (etEmail.getText().toString().trim().equals("")) {
            showError("Email is empty");
            return false;
        }
        if (!isEmailValid(etEmail.getText().toString().trim())) {
            showError("Email type is not correct");
            return false;
        }
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
        if (!etConfirmPassword.getText().toString().trim().equals(etPassword.getText().toString().trim())) {
            showWarning("Confirm Password is not match with password");
            return false;
        }
        if (etMobile.getText().toString().trim().equals("")) {
            showError("Mobile no. is empty");
            return false;
        }
        if (etMobile.getText().toString().trim().length() != 10) {
            showError("10 digits required");
            return false;
        }
        if (dateOfMonth.getSelectedItem().toString().trim().equals("Date")) {
            showError("Select Date in DOB");
            return false;
        }

        if (monthOfYear.getSelectedItem().toString().trim().equals("Month")) {
            showError("Select Month in DOB");
            return false;
        }

        if (yearNumber.getSelectedItem().toString().trim().equals("Year")) {
            showError("Select Year in DOB");
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
