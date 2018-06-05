package com.application.onlineTestSeries.PYQP_TEST_ANSWERS;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.HelperClasses.ScreenshotType;
import com.application.onlineTestSeries.HelperClasses.ScreenshotUtils;
import com.application.onlineTestSeries.Home.MainActivity;
import com.application.onlineTestSeries.PYQPTest.Test.TestResult.model.GetTestResultResponse;
import com.application.onlineTestSeries.PYQPTest.Test.TestResult.model.resultData;
import com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test.PreviousTestActivity;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestResponse;
import com.application.onlineTestSeries.PracticeTest.Test.SingleDataHolder.ServerDataGetter;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.login.Models.UserData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.Utils.checkInternetState;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.*;


public class PYQP_previousResult extends AppCompatActivity {

    private static List<practiceTestData> ConvertedQuestionData;
    LinearLayout rootContent;

    TextView tvTotalQus,tvUnAttemptedQus,tvCorrectAns,tvWrongAns,btnFinish,view_answer,tvTotalScore,tvTestTimeTaken,tvViewRank;
    String userID,testID;
    Realm realmDB;
    LinearLayout layoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyqp_previous_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Result of "+getIntent().getStringExtra("testName"));

        testID = getIntent().getStringExtra("testID");
        ConvertedQuestionData = new ArrayList<>();
        //ConvertedQuestionData = ServerTestGetter.getInstance().getConvertedQuestionData();
        Typeface customFont1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }
        RealmResults<UserData> results = realmDB.where(UserData.class).findAll();
        for (UserData item: results){
            userID = item.getUserId();

        }



        tvTotalScore = findViewById(R.id.tvTotalScore);
        tvTotalQus =findViewById(R.id.tvTotalQus);
        tvUnAttemptedQus =findViewById(R.id.attemptedQus);
        tvCorrectAns =findViewById(R.id.CorrectAns);
        tvWrongAns =findViewById(R.id.wrongAns);
        btnFinish =findViewById(R.id.btnFinish);
        rootContent = findViewById(R.id.root_content);
        tvTestTimeTaken =findViewById(R.id.tvTestTimeTaken);
        //btnFinish.setTypeface(customFont1);
        tvViewRank = findViewById(R.id.tvViewRank);
        view_answer = findViewById(R.id.view_answer);
        layoutMain = findViewById(R.id.layoutMain);

        getResultFromServer();


        new getQuestionList().execute();


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PYQP_previousResult.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });

        view_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PYQP_previousResult.this, PreviousTestActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });

    }

    private void getResultFromServer() {
        if (!checkInternetState.Companion.getInstance(this).isOnline()){
            showWarning("No Internet Connection");
        }else{
            final ProgressDialog progressDialog = new ProgressDialog(PYQP_previousResult.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<GetTestResultResponse> userCall = service.get_final_result(Integer.parseInt(userID),testID);
            userCall.enqueue(new Callback<GetTestResultResponse>() {
                @Override
                public void onResponse(Call<GetTestResultResponse> call, Response<GetTestResultResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()){
                        layoutMain.setVisibility(View.VISIBLE);
                        if (response.body().getStatus()) {

                            int correctAns=0,totalQus=0,IncorrectAns=0;
                            float negativeMark = 0;
                            long seconds = 0;
                            List<resultData> arr = response.body().getData();
                            for (resultData item: arr) {
                                Log.d("Hello", "onResponse:"+item.getTIMETAKEN());
                                correctAns = Integer.parseInt(item.getCORRECTANSWER());
                                IncorrectAns = Integer.parseInt(item.getWRONGANSWER());
                                totalQus = Integer.parseInt(item.getTOTALQUESTION());
                                negativeMark = Float.parseFloat(item.getPYQPTESTMARKNEGATIVE());

                                if (!item.getTIMETAKEN().equals("")){
                                    seconds = Long.parseLong(item.getTIMETAKEN());
                                }else{
                                    seconds = 0;
                                }
                            }

                            tvTestTimeTaken.setText(getDurationString(seconds));

                            int unattempted =  totalQus - (correctAns + IncorrectAns);
                            float getMArksWithNegative = correctAns - (IncorrectAns*negativeMark);
                            tvCorrectAns.setText(String.valueOf(correctAns));
                            tvWrongAns.setText(String.valueOf(IncorrectAns));
                            tvUnAttemptedQus.setText(String.valueOf(unattempted));
                            tvTotalQus.setText("Total Questions: "+totalQus);
                            tvTotalScore.setText(getMArksWithNegative+"/"+totalQus);


                        }else{
                            showError(response.body().getMsg());
                        }

                        tvViewRank.setText(String.valueOf(response.body().getRank())+"/"+String.valueOf(response.body().getOutoff()));


                    }else{
                        showError("Something went wrong.");
                    }
                }

                @Override
                public void onFailure(Call<GetTestResultResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                    progressDialog.dismiss();
                }
            });
        }

    }

    private String getDurationString(long seconds) {

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    private String twoDigitString(long number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    public class getQuestionList extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<practiceTestResponse> userCall = service.get_submit_pyqp_answer(userID,testID);
            userCall.enqueue(new Callback<practiceTestResponse>() {
                @Override
                public void onResponse(Call<practiceTestResponse> call, Response<practiceTestResponse> response) {
                    if (response != null && response.isSuccessful()) {
                        int count = 1;

                        for (practiceTestData question: response.body().getData()) {
                            String ans;
                            boolean ansGiv;
                            int ansNumber;
                            int ansVal= 0;
                            if (question.getServeranswer() == null){
                                ans = answerNotViewed;
                                ansGiv = false;
                                ansNumber = 0;
                                ansVal = 0; //notViewed
                            }else if(question.getServeranswer().equals("0")){
                                ans = answerSkip;
                                ansGiv = false;
                                ansNumber = 0;
                                ansVal = 1; //Skipped
                            }else{
                                ans = answerGiven;
                                ansGiv = true;
                                ansNumber = Integer.parseInt(question.getServeranswer());
                                ansNumber = Integer.parseInt(question.getServeranswer());
                                if (ansNumber == Integer.parseInt(question.getQUESTIONCORRECTOPTION())){
                                    ansVal = 2; //Correct
                                }else{
                                    ansVal = 3; //Incorrect
                                }
                            }
                            practiceTestData pd = new practiceTestData(question.getQUESTIONID(),question.getQUESTIONTITLE(),question.getQUESTIONOPTION1(),question.getQUESTIONOPTION2(),question.getQUESTIONOPTION3(),question.getQUESTIONOPTION4(),question.getQUESTIONOPTION5(),question.getQUESTIONCORRECTOPTION(),question.getQUESTIONANSDESC(),question.getQUESTIONSORTID(),question.getQUESTIONDELSTATUS(),question.getCATEGORYIDFK(),question.getQUESTIONDATE(),count,ansNumber,ansGiv,ans,testID,ansVal);
                            //String qUESTIONID, String qUESTIONTITLE, String qUESTIONOPTION1, String qUESTIONOPTION2, String qUESTIONOPTION3, String qUESTIONOPTION4, String qUESTIONOPTION5, String qUESTIONCORRECTOPTION, String qUESTIONANSDESC, String qUESTIONSORTID, String qUESTIONDELSTATUS, String cATEGORYIDFK, String qUESTIONDATE
                            ConvertedQuestionData.add(pd);
                            count++;
                        }

                        ServerDataGetter.getInstance().setConvertedQuestionData(ConvertedQuestionData);
                    } else {
                        //Toast.makeText( , "data not found", Toast.LENGTH_SHORT).show();
                        Log.d("userQuestion","data not found");
                    }
                }

                @Override
                public void onFailure(Call<practiceTestResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //progress.dismiss();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.menu_result, menu);
        return super.onCreateOptionsMenu(menu);
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
        if (id == R.id.shareScreenShot){
            takeScreenshot(ScreenshotType.FULL);
        }

        return super.onOptionsItemSelected(item);
    }
    private void takeScreenshot(ScreenshotType screenshotType) {
        Bitmap b = null;
        switch (screenshotType) {
            case FULL:
                //If Screenshot type is FULL take full page screenshot i.e our root content.
                b = ScreenshotUtils.getScreenShot(rootContent);
                break;
        }

        //If bitmap is not null
        if (b != null) {

            File saveFile = ScreenshotUtils.getMainDirectoryName(this);//get the path to save screenshot
            File file = ScreenshotUtils.store(b, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path
            shareScreenshot(file);//finally share screenshot
        } else
            //If bitmap is null show toast message
            Toast.makeText(this, "Failed to take screenshot!!", Toast.LENGTH_SHORT).show();
    }
    private void shareScreenshot(File file) {
        Uri uri = Uri.fromFile(file);//Convert file path into Uri for sharing
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "Result");
        intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
        startActivity(Intent.createChooser(intent, "Share Screenshot"));
    }

    private void showError(String s) {
        Toasty.error(getApplicationContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showWarning(String s) {
        Toasty.warning(getApplicationContext(), s, Toast.LENGTH_LONG, true).show();
    }

}
