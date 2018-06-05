package com.application.onlineTestSeries.termsAndConditions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.PYQPTest.Test.MainTest;
import com.application.onlineTestSeries.PYQPTest.Test.Models.PYQP_Response;
import com.application.onlineTestSeries.PYQPTest.Test.Models.PYQP_Test_Data;
import com.application.onlineTestSeries.PYQPTest.Test.SingleDataHolder.ServerTestGetter;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.SectionActivity.WebHandler.MyTagHandler;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.*;

public class termsConditionActivity extends AppCompatActivity {

    TextView btnCancel,btnStartTest,description;
    CheckBox checkbox_terms;
    String testID,testName,testDuration;
    boolean status;
    private static List<PYQP_Test_Data> ConvertedQuestionData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_condition);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        testID = getIntent().getStringExtra("testID");
        testName =getIntent().getStringExtra("testName");
        testDuration = getIntent().getStringExtra("testDuration");
        setTitle("Terms and condition");
        ConvertedQuestionData = new ArrayList<>();
        btnCancel=findViewById(R.id.btnCancel);
        btnStartTest=findViewById(R.id.btnStartTest);
        checkbox_terms=findViewById(R.id.checkbox_terms);
        description = findViewById(R.id.description);
//        description.setText(Html.fromHtml(getIntent().getStringExtra("description"), new GlideImageGetter(this, description), null));
        Spanned origanlHtmlTextWithTags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            origanlHtmlTextWithTags = Html.fromHtml(getIntent().getStringExtra("description"), Html.FROM_HTML_MODE_COMPACT, null, new MyTagHandler());
        } else {
            origanlHtmlTextWithTags = Html.fromHtml(getIntent().getStringExtra("description"), null, new MyTagHandler());
        }

        //SpannableString WordSpan = new SpannableString(origanlHtmlTextWithTags);
        description.setText(origanlHtmlTextWithTags);


        btnStartTest.setTextColor(Color.parseColor("#A63ABFB0"));

        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerTestGetter.getInstance().clear();
                if (checkbox_terms.isChecked()){
                    ServerTestGetter.getInstance().clear();
                    //new getQuestionList().execute();


                    final ProgressDialog progress;
                    progress = new ProgressDialog(termsConditionActivity.this);
                    progress.setTitle("Fetching Test");
                    progress.setMessage("Please wait...");
                    progress.setCancelable(false);
                    progress.show();
                    ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
                    Call<PYQP_Response> userCall = service.get_test_pyqp(testID);
                    userCall.enqueue(new Callback<PYQP_Response>() {
                        @Override
                        public void onResponse(Call<PYQP_Response> call, Response<PYQP_Response> response) {
                            if (response != null && response.isSuccessful()) {
                                int count = 1;
                                for (PYQP_Test_Data question: response.body().getData()) {
                                    String ans;
                                    boolean ansGiv;
                                    int ansNumber;
                                    ans = answerNotViewed;
                                    ansGiv = false;
                                    ansNumber = 0;
                                    ServerTestGetter.getInstance().addDataToList(new PYQP_Test_Data(question.getQUESTIONID(),question.getQUESTIONTITLE(),question.getQUESTIONOPTION1(),question.getQUESTIONOPTION2(),question.getQUESTIONOPTION3(),question.getQUESTIONOPTION4(),question.getQUESTIONOPTION5(),question.getQUESTIONCORRECTOPTION(),question.getQUESTIONANSDESC(),question.getQUESTIONSORTID(),question.getQUESTIONDELSTATUS(),question.getCATEGORYIDFK(),question.getQUESTIONDATE(),count,ansNumber,ansGiv,ans,testID));
                                    count++;
                                }

                                Runnable progressRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        if (progress.isShowing()){
                                            progress.dismiss();
                                        }

                                        Intent intent = new Intent(termsConditionActivity.this, MainTest.class);
                                        intent.putExtra("testID",testID);
                                        intent.putExtra("testName",testName);
                                        intent.putExtra("testDuration",testDuration);
                                        startActivity(intent);
                                        finish();
                                    }
                                };

                                Handler pdCanceller = new Handler();
                                pdCanceller.postDelayed(progressRunnable, 2000);


                            } else {
                                Log.d("userQuestion","data not found");
                            }
                        }

                        @Override
                        public void onFailure(Call<PYQP_Response> call, Throwable t) {
                            Log.d("onFailure", t.toString());
                        }
                    });

                }else if (!checkbox_terms.isChecked()){
                    //Toast.makeText(termsConditionActivity.this, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
                    showMessageWarning("Please accept terms and conditions");
                }
            }
        });

        checkbox_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked){
                    //if (status){
                        btnStartTest.setTextColor(Color.parseColor("#128c7e"));
                    /*}else{
                        showMessageWarning("Please wait...");
                        //Toast.makeText(termsConditionActivity.this, "please wait", Toast.LENGTH_SHORT).show();
                    }*/
                }else {
                    btnStartTest.setTextColor(Color.parseColor("#A63ABFB0"));
                }
            }
        });

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                status = true;
                btnStartTest.setTextColor(Color.parseColor("#128c7e"));
                btnStartTest.setEnabled(true);
            }
        }, 12000);*/
    }

    public class getQuestionList extends AsyncTask<Void,Void,Void> {
        ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(termsConditionActivity.this);
            progress.setTitle("Fetching Test");
            progress.setMessage("Please wait...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<PYQP_Response> userCall = service.get_test_pyqp(testID);
            userCall.enqueue(new Callback<PYQP_Response>() {
                @Override
                public void onResponse(Call<PYQP_Response> call, Response<PYQP_Response> response) {
                    if (response != null && response.isSuccessful()) {
                        int count = 1;
                        for (PYQP_Test_Data question: response.body().getData()) {
                            String ans;
                            boolean ansGiv;
                            int ansNumber;
                            ans = answerNotViewed;
                            ansGiv = false;
                            ansNumber = 0;
                            ServerTestGetter.getInstance().addDataToList(new PYQP_Test_Data(question.getQUESTIONID(),question.getQUESTIONTITLE(),question.getQUESTIONOPTION1(),question.getQUESTIONOPTION2(),question.getQUESTIONOPTION3(),question.getQUESTIONOPTION4(),question.getQUESTIONOPTION5(),question.getQUESTIONCORRECTOPTION(),question.getQUESTIONANSDESC(),question.getQUESTIONSORTID(),question.getQUESTIONDELSTATUS(),question.getCATEGORYIDFK(),question.getQUESTIONDATE(),count,ansNumber,ansGiv,ans,testID));
                            count++;
                        }
                    } else {
                        Log.d("userQuestion","data not found");
                    }
                }

                @Override
                public void onFailure(Call<PYQP_Response> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progress.isShowing()){
                progress.dismiss();
            }
            Intent intent = new Intent(termsConditionActivity.this, MainTest.class);
            intent.putExtra("testID",testID);
            intent.putExtra("testName",testName);
            intent.putExtra("testDuration",testDuration);
            startActivity(intent);
            finish();
            //progress.dismiss();
        }
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
    private void showMessageWarning(String message) {
        Toasty.warning(termsConditionActivity.this, message, Toast.LENGTH_LONG, true).show();
    }
}