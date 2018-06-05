package com.application.onlineTestSeries.PYQPTest.Test.TestResult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
import com.application.onlineTestSeries.PYQPTest.Test.SingleDataHolder.ServerTestGetter;
import com.application.onlineTestSeries.PYQPTest.Test.TestResult.model.GetTestResultResponse;
import com.application.onlineTestSeries.PYQPTest.Test.TestResult.model.resultData;
import com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test.PreviousTestActivity;
import com.application.onlineTestSeries.PracticeTest.Models.QuestionUpdateResponse;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestResponse;
import com.application.onlineTestSeries.PracticeTest.Test.SingleDataHolder.ServerDataGetter;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.login.Models.UserData;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

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


public class resultTestActivity extends AppCompatActivity {

    Realm realmDB;
    LinearLayout rootContent;
    private static List<practiceTestData> ConvertedQuestionData;
    TextView tvTotalQus,tvTotalQus1,btnFinish,tvUnAttemptedQus,tvCorrectAns,tvWrongAns,tvTotalScore,tvTestTimeTaken,tvViewRank,view_answer,resetTest;
    String userID,testID;
    LinearLayout layoutMain;
    PieChart chart;
    ArrayList<Entry> entries ;
    ArrayList<String> PieEntryLabels;
    PieDataSet pieDataSet;
    PieData pieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyqp_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Result of "+getIntent().getStringExtra("testName"));

        ConvertedQuestionData = new ArrayList<>();
        ServerTestGetter.getInstance().clear();
        entries = new ArrayList<>();
        PieEntryLabels = new ArrayList<String>();

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

        testID = getIntent().getStringExtra("testID");
        resetTest = findViewById(R.id.resetTest);
        tvTotalScore = findViewById(R.id.tvTotalScore);
        tvTotalQus =findViewById(R.id.tvTotalQus);
        tvTotalQus1 =findViewById(R.id.tvTotalQus1);
        chart = findViewById(R.id.chart1);
        tvUnAttemptedQus =findViewById(R.id.attemptedQus);
        tvCorrectAns =findViewById(R.id.CorrectAns);
        tvWrongAns =findViewById(R.id.wrongAns);
        btnFinish =findViewById(R.id.btnFinish);
        rootContent = findViewById(R.id.root_content);
        tvTestTimeTaken =findViewById(R.id.tvTestTimeTaken);
        view_answer =findViewById(R.id.view_answer);
        tvViewRank = findViewById(R.id.tvViewRank);
        layoutMain = findViewById(R.id.layoutMain);
        getResultFromServer();
        new getQuestionList().execute();

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(resultTestActivity.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        view_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(resultTestActivity.this, PreviousTestActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        resetTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTest(testID);
            }
        });

    }
    public void resetTest(String testID) {
        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
        Call<QuestionUpdateResponse> userCall = service.resetTest(userID, testID);
        userCall.enqueue(new Callback<QuestionUpdateResponse>() {
            @Override
            public void onResponse(Call<QuestionUpdateResponse> call, Response<QuestionUpdateResponse> response) {
                if (response != null && response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        //Log.d("userTest", response.body().getMessage());
                        showSuccess(response.body().getMessage());
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                finish();
                            }
                        }, 500);
                        ///getTestFromServer(mView);
                    } else {
                        Log.d("userTest", response.body().getMessage());
                    }
                } else {
                    //Toast.makeText( , "data not found", Toast.LENGTH_SHORT).show();
                    Log.d("userQuestion", "data not found");
                }
            }

            @Override
            public void onFailure(Call<QuestionUpdateResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }
    private void getResultFromServer() {
        if (!checkInternetState.Companion.getInstance(this).isOnline()){
            showWarning("No Internet Connection");
        }else{
            //Toast.makeText(resultTestActivity.this, "Hello", Toast.LENGTH_SHORT).show();

            final ProgressDialog progressDialog = new ProgressDialog(resultTestActivity.this);
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
                                //Log.d("Hello", "onResponse:"+item.getTIMETAKEN());
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

                            //String s = String.format("%.2f", score);
                            /*entries.add(new BarEntry(totalQus, 0));
                            PieEntryLabels.add("Total question");*/

                            entries.add(new BarEntry(correctAns, 0));
                            PieEntryLabels.add("Correct Answer");

                            entries.add(new BarEntry(IncorrectAns, 1));
                            PieEntryLabels.add("Wrong Answer");

                            tvTestTimeTaken.setText(getDurationString(seconds));

                            int unattempted =  totalQus - (correctAns + IncorrectAns);
                            float getMArksWithNegative = correctAns - (IncorrectAns*negativeMark);

                            entries.add(new BarEntry(unattempted, 2));
                            PieEntryLabels.add("Unattempted");

                            final int[] MY_COLORS = {Color.rgb(124,252,0), Color.rgb(255,0,0), Color.rgb(255,192,0)};
                            ArrayList<Integer> colors = new ArrayList<Integer>();

                            for(int c: MY_COLORS) colors.add(c);

                            pieDataSet = new PieDataSet(entries, "");
                            pieData = new PieData(PieEntryLabels, pieDataSet);
                            pieDataSet.setColors(colors);
                            chart.setData(pieData);
                            chart.setCenterTextSize(12);
                            chart.setCenterText("Remarks");
                            //pieChart.setUsePercentValues(true);
                            pieDataSet.setValueTextSize(8);
                            // pieDataSet.setValueTextColor(Integer.parseInt("#ffffff"));
                            chart.animateY(3000);

                            tvTotalQus1.setText("Total Questions: "+totalQus);
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
                                if (ansNumber == Integer.parseInt(question.getQUESTIONCORRECTOPTION())){
                                    ansVal = 2; //Correct
                                }else{
                                    ansVal = 3; //Incorrect
                                }
                            }
                            //String qUESTIONID, String qUESTIONTITLE, String qUESTIONOPTION1, String qUESTIONOPTION2, String qUESTIONOPTION3, String qUESTIONOPTION4, String qUESTIONOPTION5, String qUESTIONCORRECTOPTION, String qUESTIONANSDESC, String qUESTIONSORTID, String qUESTIONDELSTATUS, String cATEGORYIDFK, String qUESTIONDATE
                            ConvertedQuestionData.add(new practiceTestData(question.getQUESTIONID(),question.getQUESTIONTITLE(),question.getQUESTIONOPTION1(),question.getQUESTIONOPTION2(),question.getQUESTIONOPTION3(),question.getQUESTIONOPTION4(),question.getQUESTIONOPTION5(),question.getQUESTIONCORRECTOPTION(),question.getQUESTIONANSDESC(),question.getQUESTIONSORTID(),question.getQUESTIONDELSTATUS(),question.getCATEGORYIDFK(),question.getQUESTIONDATE(),count,ansNumber,ansGiv,ans,testID, ansVal));
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

}
