package com.application.onlineTestSeries.PracticeTest.practicTestGrabber;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.BuyTest.Buy_test;
import com.application.onlineTestSeries.Home.MainActivity;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.PracticeTest.Models.QuestionUpdateResponse;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestResponse;
import com.application.onlineTestSeries.PracticeTest.PracticeTestFragment;
import com.application.onlineTestSeries.PracticeTest.Test.SingleDataHolder.ServerDataGetter;
import com.application.onlineTestSeries.PracticeTest.Test.TestActivity;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.SectionActivity.WebHandler.MyTagHandler;
import com.application.onlineTestSeries.Utils.checkInternetState;
import com.application.onlineTestSeries.login.Models.UserData;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.answerGiven;
import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.answerNotViewed;
import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.answerSkip;


public class practiceTestGrabber extends AppCompatActivity {

    private static List<practiceTestData> ConvertedQuestionData;
    private PracticeTestFragment.OnFragmentInteractionListener mListener;
    private int serverQuestionID;
    Realm realmDB;
    int UserID,testCount;
    TextView btnCancel, btnStartTest, btnReset, userInstruction, tryFreeTest;
    CheckBox checkbox_terms;
    int TestID, lastID, position, subscriptionFlag;
    String testID, maxquestion, testName, testPrice;
    Context ctx;
    boolean usersStat;
    LinearLayout tryFreeTestLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_test_grabber);

        tryFreeTest = findViewById(R.id.tryFreeTest);
        tryFreeTestLayout = findViewById(R.id.tryFreeTestLayout);
        testID = getIntent().getStringExtra("testID");
        ctx = this;
        testCount = getIntent().getIntExtra("testQuestion",0);
        //instructionData = getIntent().getStringExtra("instructionData");
        maxquestion = getIntent().getStringExtra("maxquestion");
        userInstruction = findViewById(R.id.userInstruction);
        //userInstruction.setText(instructionData);
        subscriptionFlag = getIntent().getIntExtra("subscription", 0);
        //userInstruction.setText(Html.fromHtml(instructionData, new GlideImageGetter(this, userInstruction), null));
        usersStat = getIntent().getBooleanExtra("purschased_Status", false);

        testName = getIntent().getStringExtra("testName");
        testPrice = getIntent().getStringExtra("testPrice");
        Spanned origanlHtmlTextWithTags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            origanlHtmlTextWithTags = Html.fromHtml(getIntent().getStringExtra("instructionData"), Html.FROM_HTML_MODE_COMPACT, null, new MyTagHandler());
        } else {
            origanlHtmlTextWithTags = Html.fromHtml(getIntent().getStringExtra("instructionData"), null, new MyTagHandler());
        }
        userInstruction.setText(origanlHtmlTextWithTags);

        //Toast.makeText(ctx, ""+TestID, Toast.LENGTH_SHORT).show();
        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Terms and condition");

        RealmResults<UserData> results = realmDB.where(UserData.class).findAll();
        for (UserData item : results) {
            //txtName.setText(item.getUsername());
            UserID = Integer.parseInt(item.getUserId());
        }

        ConvertedQuestionData = new ArrayList<>();
        btnCancel = findViewById(R.id.btnCancel);
        btnStartTest = findViewById(R.id.btnStartTest);

        Log.d("practiceTest", "onCreate: " + usersStat + "----" + subscriptionFlag);


        if (usersStat || subscriptionFlag == 1) {
            btnStartTest.setText("Continue");
            tryFreeTestLayout.setVisibility(View.GONE);
        } else {
            btnStartTest.setText("Try");
        }
        btnReset = findViewById(R.id.btnReset);
        checkbox_terms = findViewById(R.id.checkbox_terms);

        btnReset.setTextColor(Color.parseColor("#A63ABFB0"));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
            }
        });

        btnStartTest.setTextColor(Color.parseColor("#A63ABFB0"));

        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearing the local
                //store data of previous tests
                ServerDataGetter.getInstance().clearPreviousRecords();
                if (checkbox_terms.isChecked()) {
                    // Toast.makeText(v.getContext(), "enable", Toast.LENGTH_SHORT).show();
                    if (!checkInternetState.Companion.getInstance(ctx).isOnline()) {
                        showWarning("No Internet Connection");
                    } else {
                        final ProgressDialog progress = new ProgressDialog(ctx);
                        progress.setTitle("Loading practice test");
                        progress.setMessage("Please wait...");
                        progress.setCancelable(false);
                        progress.show();
                        //new getQuestionList().execute();

                        ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
                        Call<practiceTestResponse> userCall = service.get_pratice_question(UserID, testID, subscriptionFlag, maxquestion, usersStat);
                        userCall.enqueue(new Callback<practiceTestResponse>() {
                            @Override
                            public void onResponse(Call<practiceTestResponse> call, Response<practiceTestResponse> response) {
                                if (response != null && response.isSuccessful()) {
                                    lastID = response.body().getMaxID();
                                    TestID = response.body().getTestID();
                                    int count = 1;
                                    for (practiceTestData question : response.body().getData()) {
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

                                        if (lastID == Integer.parseInt(question.getQUESTIONID())) {
                                            position = count;
                                        }
                                        //ConvertedQuestionData.add(new practiceTestData(question.getQUESTIONID(), question.getQUESTIONTITLE(), question.getQUESTIONOPTION1(), question.getQUESTIONOPTION2(), question.getQUESTIONOPTION3(), question.getQUESTIONOPTION4(), question.getQUESTIONOPTION5(), question.getQUESTIONCORRECTOPTION(), question.getQUESTIONANSDESC(), question.getQUESTIONSORTID(), question.getQUESTIONDELSTATUS(), question.getCATEGORYIDFK(), question.getQUESTIONDATE(), count, ansNumber, ansGiv, ans, question.getTestID(), ansVal));
                                        ServerDataGetter.getInstance().getQuestionData(new practiceTestData(question.getQUESTIONID(), question.getQUESTIONTITLE(), question.getQUESTIONOPTION1(), question.getQUESTIONOPTION2(), question.getQUESTIONOPTION3(), question.getQUESTIONOPTION4(), question.getQUESTIONOPTION5(), question.getQUESTIONCORRECTOPTION(), question.getQUESTIONANSDESC(), question.getQUESTIONSORTID(), question.getQUESTIONDELSTATUS(), question.getCATEGORYIDFK(), question.getQUESTIONDATE(), count, ansNumber, ansGiv, ans, question.getTestID(), ansVal));
                                        count++;
                                    }


                                    serverQuestionID = response.body().getMaxID();
                                    //ServerDataGetter.getInstance().setConvertedQuestionData(ConvertedQuestionData);

                                    if (progress.isShowing()){
                                        progress.dismiss();
                                    }

                                    Runnable progressRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                     //       progress.cancel();
                                            Intent i = new Intent(ctx, TestActivity.class);
                                            i.putExtra("questionId", serverQuestionID);
                                            i.putExtra("TestID", TestID);
                                            i.putExtra("position", position);
                                            i.putExtra("testCount",testCount);
                                            startActivity(i);
                                        }
                                    };
                                    Handler pdCanceller = new Handler();
                                    pdCanceller.postDelayed(progressRunnable, 1000);


                                } else {
                                    //Toast.makeText( , "data not found", Toast.LENGTH_SHORT).show();
                                    Log.d("userQuestion", "data not found");
                                }
                            }

                            @Override
                            public void onFailure(Call<practiceTestResponse> call, Throwable t) {
                                Log.d("onFailure", t.toString());
                            }
                        });
                    }
                } else if (!checkbox_terms.isChecked()) {
                    showWarning("Please accept terms and conditions");

                }
            }
        });

        tryFreeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ctx, testPrice, Toast.LENGTH_SHORT).show();
                if (!testPrice.equals("") && !testPrice.equals("0")) {
                    Intent i = new Intent(practiceTestGrabber.this, Buy_test.class);
                    i.putExtra("testName", testName);
                    i.putExtra("testPrice", testPrice);
                    i.putExtra("testID", testID);
                    i.putExtra("testType", 0);
                    startActivity(i);
                } else {
                    Toast.makeText(ctx, "Test not available for purchase", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_terms.isChecked()) {
                    if (!checkInternetState.Companion.getInstance(ctx).isOnline()) {
                        showWarning("No Internet Connection");

                    } else {
                        clearTest();
                    }
                } else if (!checkbox_terms.isChecked()) {
                    showWarning("Please accept terms and conditions");

                }
            }
        });

        checkbox_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnStartTest.setTextColor(Color.parseColor("#128c7e"));
                    btnReset.setTextColor(Color.parseColor("#128c7e"));
                } else {
                    btnStartTest.setTextColor(Color.parseColor("#A63ABFB0"));
                    btnReset.setTextColor(Color.parseColor("#A63ABFB0"));
                }
            }
        });
    }

    public class getQuestionList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<practiceTestResponse> userCall = service.get_pratice_question(UserID, testID, subscriptionFlag, maxquestion, usersStat);
            userCall.enqueue(new Callback<practiceTestResponse>() {
                @Override
                public void onResponse(Call<practiceTestResponse> call, Response<practiceTestResponse> response) {
                    if (response != null && response.isSuccessful()) {
                        lastID = response.body().getMaxID();
                        TestID = response.body().getTestID();
                        int count = 1;
                        for (practiceTestData question : response.body().getData()) {
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

                            if (lastID == Integer.parseInt(question.getQUESTIONID())) {
                                position = count;
                            }
                            ConvertedQuestionData.add(new practiceTestData(question.getQUESTIONID(), question.getQUESTIONTITLE(), question.getQUESTIONOPTION1(), question.getQUESTIONOPTION2(), question.getQUESTIONOPTION3(), question.getQUESTIONOPTION4(), question.getQUESTIONOPTION5(), question.getQUESTIONCORRECTOPTION(), question.getQUESTIONANSDESC(), question.getQUESTIONSORTID(), question.getQUESTIONDELSTATUS(), question.getCATEGORYIDFK(), question.getQUESTIONDATE(), count, ansNumber, ansGiv, ans, question.getTestID(), ansVal));
                            count++;
                        }

                        serverQuestionID = response.body().getMaxID();
                        ServerDataGetter.getInstance().setConvertedQuestionData(ConvertedQuestionData);

                    } else {
                        //Toast.makeText( , "data not found", Toast.LENGTH_SHORT).show();
                        Log.d("userQuestion", "data not found");
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

    private void clearTest() {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Quiz")
                .setMessage("Are you sure you want to reset all test")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progress = new ProgressDialog(ctx);
                        progress.setTitle("Clear Test");
                        progress.setMessage("Please wait...");
                        progress.setCancelable(false);
                        progress.show();

                        Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                progress.cancel();
                                new clearAnswerToDatabase().execute();
                            }
                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 1500);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    public class clearAnswerToDatabase extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... voids) {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<QuestionUpdateResponse> userCall = service.empty_records_usertest(UserID);
            userCall.enqueue(new Callback<QuestionUpdateResponse>() {
                @Override
                public void onResponse(Call<QuestionUpdateResponse> call, Response<QuestionUpdateResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getStatus()) {
                                showMessageSuccess(response.body().getMessage());
                                position = 0;
                                //if record clear from database then clear from realm and api re reun
                                ServerDataGetter.getInstance().clearPreviousRecords();


                                if (!checkInternetState.Companion.getInstance(ctx).isOnline()) {
                                    showWarning("No Internet Connection");

                                } else {
                                    new getQuestionList().execute();
                                }


                                //startActivity(new Intent(ctx, MainActivity.class));
                                //getActivity().finish();
                            } else {
                                showMessageInfo("Test already reset");
                            }
                        } else {
                            Log.d("onFailure", "response null");
                        }
                    } else {
                        Log.d("onFailure", "hit failure");
                    }
                }

                @Override
                public void onFailure(Call<QuestionUpdateResponse> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void showMessageSuccess(String message) {
        Toasty.success(ctx, message, Toast.LENGTH_LONG, true).show();
    }

    private void showMessageInfo(String message) {
        Toasty.info(ctx, message, Toast.LENGTH_LONG, true).show();
    }

    private void showWarning(String s) {
        Toasty.warning(ctx, s, Toast.LENGTH_LONG, true).show();
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
}
