package com.application.onlineTestSeries.profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.FullImageView.FullImage;
import com.application.onlineTestSeries.HelperClasses.DateConverter;
import com.application.onlineTestSeries.HelperClasses.ProgressRequestBody;
import com.application.onlineTestSeries.Home.MainActivity;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.Utils.UtilsApp;
import com.application.onlineTestSeries.login.Models.UserData;
import com.application.onlineTestSeries.profile.model.profileUpdateResponse;
import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.util.Calendar;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.Utils.checkInternetState;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class profileActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks{

    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";
    CircleImageView userImage;
    ImageView userImageBackground;
    TextView userName,userDOB,userGender,userEmail;
    EditText etUserName,etContact,etDOB;
    Realm realmDB;
    Button btnUpdateProfile;
    Glide glide;
    String user_email,user_id,mediaPath;
    String realmUserImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//to hide keyboad
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Typeface customFont1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");

        userImage = findViewById(R.id.userImage);
        userImageBackground = findViewById(R.id.userImageBackground);
        userName = findViewById(R.id.userName);
        userDOB = findViewById(R.id.userDOB);
        userGender = findViewById(R.id.userGender);
        userEmail = findViewById(R.id.userEmail);
        etUserName = findViewById(R.id.etName);
        etContact = findViewById(R.id.etContact);
        etDOB = findViewById(R.id.etDOB);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnUpdateProfile.setTypeface(customFont1);
        etContact.setImeOptions(EditorInfo.IME_ACTION_DONE);


        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        RealmResults<UserData> results = realmDB.where(UserData.class).findAll();
        for (UserData item: results){
            //txtName.setText(item.getUsername());

            String name = item.getName();
            String dob = item.getDateofbirth();
            realmUserImage = item.getImage();
            user_email = item.getEmail();
            user_id = item.getUserId();
            if (!name.equals("")){
                userName.setText(item.getName());
                etUserName.setText(item.getName());
            }
            if (!dob.equals("")){
                userDOB.setText(new DateConverter().convertDate(item.getDateofbirth()));
                etDOB.setText(new DateConverter().convertDate(item.getDateofbirth()));
            }
            if (!item.getPnone().equals("")){
                etContact.setText(item.getPnone());
            }
            if (!realmUserImage.equals("")|| realmUserImage.equals("NoImage")){
                Glide.with(this).load(UtilsApp.webUrlHome+realmUserImage).into(userImage);
                Glide.with(this).load(UtilsApp.webUrlHome+realmUserImage).into(userImageBackground);
            }else {
                Glide.with(this).load(R.drawable.ic_account_circle_black_36dp).into(userImage);
                Glide.with(this).load(R.drawable.pink_purple_grdnt).into(userImageBackground);
            }
            userEmail.setText(user_email);
            userGender.setText(item.getGender());
        }

        etDOB.setInputType(InputType.TYPE_NULL);
        etDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        String mon = (monthOfYear < 10) ? "0" + monthOfYear : "" + monthOfYear;
                        String day = (dayOfMonth < 10) ? "0" + dayOfMonth : "" + dayOfMonth;
                        //String getDate = year+"/"+mon+"/"+day;
                        String getDate = day + "/" + mon + "/" + year;
                        etDOB.setText(getDate);
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int Date = calendar.get(Calendar.DAY_OF_MONTH);
                int Month = calendar.get(Calendar.MONTH);
                int Year = calendar.get(Calendar.YEAR);
                //Time date = new Time();
                DatePickerDialog d = new DatePickerDialog(profileActivity.this, dpd, Year, Month, Date);
                d.getDatePicker().setMaxDate(System.currentTimeMillis());
                d.show();
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Change Profile Picture","View Profile Picture"};

                AlertDialog.Builder builder = new AlertDialog.Builder(profileActivity.this);

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Change Profile Picture")) {
                            CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(profileActivity.this);

                        }else if (options[item].equals("View Profile Picture")) {
                            if (!realmUserImage.equals("")){
                                Intent intent =new Intent(profileActivity.this,FullImage.class);
                                intent.putExtra("ImagePath", UtilsApp.webUrlHome+realmUserImage);
                                startActivity(intent);
                            }
                        }

                    }
                });
                builder.show();
            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataValidate()){

                    hitApi();
                }
            }
        });

    }

    private void hitApi() {
        final String userName = etUserName.getText().toString();
        final String mobile = etContact.getText().toString();
        final String DOB = etDOB.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(profileActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);

        progressDialog.setMessage("Profile updating...");
        progressDialog.show();

        if (!checkInternetState.Companion.getInstance(this).isOnline()) {
            showWarning("No Internet Connection");
            progressDialog.hide();
        } else {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<profileUpdateResponse> userCall = service.profile_update(userName,user_email,mobile,new DateConverter().convertDate1(DOB), Integer.parseInt(user_id));
            userCall.enqueue(new Callback<profileUpdateResponse>() {
                @Override
                public void onResponse(Call<profileUpdateResponse> call, Response<profileUpdateResponse> response) {
                    progressDialog.hide();
                    if (response.isSuccessful()) {
                        if (response.body().getStatus()) {

                            realmDB.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    final UserData data = realm.where(UserData.class).equalTo("userId", user_id).findFirst();
                                    data.setDateofbirth(new DateConverter().convertDate1(DOB));
                                    data.setPnone(mobile);
                                    data.setName(userName);
                                }
                            });
                            showSuccess(response.body().getMsg());

                            Intent i = new Intent(profileActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            showError(response.body().getMsg());
                        }
                    } else {
                        showError("Something went wrong.");
                    }
                }

                @Override
                public void onFailure(Call<profileUpdateResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                    progressDialog.hide();
                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPath = "";
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = result.getUri();
                mediaPath = getRealPathFromURI(selectedImageUri);
                Log.e("mediaPath ",""+mediaPath +"  selectedImageUri  "+selectedImageUri);
                //Toast.makeText(profile.this, "media path  "+mediaPath, Toast.LENGTH_SHORT).show();
                if (!mediaPath.equals("")){

                    uploadImage(mediaPath);
                }else{
                    Toast.makeText(profileActivity.this, "do not get path", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    private void uploadImage(final String mediaPath) {
        final ProgressDialog progressDialog = new ProgressDialog(profileActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);

        progressDialog.setMessage("Profile updating...");
        progressDialog.show();
        File file = new File(mediaPath);
        ProgressRequestBody fileBody = new ProgressRequestBody(file, this);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("USER_IMAGE", file.getName(), fileBody);
        String extension = mediaPath.substring(mediaPath.lastIndexOf(".") + 1);
        //Toast.makeText(mContext, ""+extension, Toast.LENGTH_SHORT).show();
        if (!checkInternetState.Companion.getInstance(this).isOnline()) {
            showWarning("No Internet Connection");
            progressDialog.hide();
        }else {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<profileUpdateResponse> userCall = service.profile_image_update(Integer.parseInt(user_id),fileToUpload);
            userCall.enqueue(new Callback<profileUpdateResponse>() {
                @Override
                public void onResponse(Call<profileUpdateResponse> call, final Response<profileUpdateResponse> response) {
                    progressDialog.hide();
                    if (response.isSuccessful()) {
                        if (response.body().getStatus()) {

                            if (!mediaPath.equals("")){

                                Glide.with(profileActivity.this).load(mediaPath).into(userImage);
                                Glide.with(profileActivity.this).load(mediaPath).into(userImageBackground);
                            }

                            realmDB.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    final UserData data = realm.where(UserData.class).equalTo("userId", user_id).findFirst();
                                    data.setImage(response.body().getMsg());
                                }
                            });
                            showSuccess("Profile image updated");

                            /*Intent i = new Intent(profileActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);*/
                            Log.e("updatedImage",""+response.body().getMsg());
                        } else {
                            showError("Profile image not updated");
                        }
                    } else {
                        showError("Something went wrong.");
                    }
                }

                @Override
                public void onFailure(Call<profileUpdateResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                    progressDialog.hide();
                }
            });
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor =profileActivity.this.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    private boolean dataValidate() {

        if (etUserName.getText().toString().trim().equals("")) {
            showError("Name is empty");
            return false;
        }
        if (etContact.getText().toString().trim().equals("")) {
            showError("Mobile no. is empty");
            return false;
        }
        if (etContact.getText().toString().trim().length() != 10) {
            showError("10 digits required");
            return false;
        }
        if (etDOB.getText().toString().trim().equals("")) {
            showError("DOB is empty");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressUpdate(int percentage) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }
}
