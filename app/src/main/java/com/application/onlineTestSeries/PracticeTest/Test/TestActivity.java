package com.application.onlineTestSeries.PracticeTest.Test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.R;
import com.mancj.slideup.SlideUp;

import java.util.ArrayList;
import java.util.List;

import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.HelperClasses.RecyclerTouchListener;
import com.application.onlineTestSeries.Home.MainActivity;
import com.application.onlineTestSeries.Home.Models.subscriptionUserData;
import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;
import com.application.onlineTestSeries.PracticeTest.Constants.testconstants;
import com.application.onlineTestSeries.PracticeTest.Models.QuestionUpdateResponse;
import com.application.onlineTestSeries.PracticeTest.Models.SendingDataToDB;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;
import com.application.onlineTestSeries.PracticeTest.Test.Adapter.CustomViewPager;
import com.application.onlineTestSeries.PracticeTest.Test.Adapter.QuestionListAdapter;
import com.application.onlineTestSeries.PracticeTest.Test.Adapter.ViewPagerAdapter;
import com.application.onlineTestSeries.PracticeTest.Test.SingleDataHolder.ServerDataGetter;
import com.application.onlineTestSeries.PracticeTest.TestResult.resultActivity;
import com.application.onlineTestSeries.Subscription_package.subscriptionSubscribe.SubscriptionSubscribe;
import com.application.onlineTestSeries.login.Models.UserData;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.answerGiven;
import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.answerSkip;
import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.questionIsMarked;

public class TestActivity extends AppCompatActivity {

    private CustomViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ViewPagerAdapter mPagerAdapter;

    //List<QuestionModal> question;

    private static List<practiceTestData> ConvertedQuestionData;

    private static String Progress = testconstants.notStarted;

    ImageView showGrid;
    private SlideUp slideUp,slideUpAnswer;
    private View dim;
    private View slideView,slideViewAnswer;
    QuestionListAdapter Questionadapter;
    RecyclerView recyclerView;
    private CardView card;
    ProgressDialog progress;
    TextView answerText,totalAttemptedTV,currentQuestionNumberTV,showAnswer,finish,OptionText;
    Realm realmDb;
    int userID,TestID,testCount;
    RelativeLayout previoustestlayout;
    LinearLayout next, previous;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Practice Test");*/
        testCount = getIntent().getIntExtra("testCount",0);
        previoustestlayout = findViewById(R.id.previoustestlayout);
        previoustestlayout.setVisibility(View.GONE);
        currentQuestionNumberTV = findViewById(R.id.currentQuestionNumberTV);
        totalAttemptedTV = findViewById(R.id.totalAttemptedTV);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        TestID = getIntent().getIntExtra("TestID",0);
        try {
            realmDb = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDb = Realm.getInstance(config);
        }

        RealmResults<UserData> results = realmDb.where(UserData.class).findAll();
        for (UserData item: results){
            userID = Integer.parseInt(item.getUserId());
        }

        showAlertOrCheckSubscription();

        ConvertedQuestionData = new ArrayList<>();
        ConvertedQuestionData = ServerDataGetter.getInstance().getConvertedQuestionData();

        Questionadapter = new QuestionListAdapter(this,ConvertedQuestionData);

        //RecyclerView For the Question
        recyclerView = findViewById(R.id.QuestionListRecycler);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 6);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(Questionadapter);
        mPager = findViewById(R.id.pager);
        mPager.setPagingEnabled(false);
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),ConvertedQuestionData);
        mPager.setAdapter(mPagerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                card = (CardView) view.findViewById(R.id.QuestionCard);
                mPager.setCurrentItem(position);
                slideUp.animateOut();
                Questionadapter.notifyDataSetChanged();
                //ConvertedQuestionData.get(position).setProcessStart(constants.Submittd);
                if (!ConvertedQuestionData.get(position).getProcessStart().equals(questionIsMarked)){
                    if(ConvertedQuestionData.get(position).isAttempted()){
                        ConvertedQuestionData.get(position).setProcessStart(answerGiven);

                        if (ConvertedQuestionData.get(position).getAnswerProvided()==Integer.parseInt(ConvertedQuestionData.get(position).getQUESTIONCORRECTOPTION())){
                            ConvertedQuestionData.get(position).setIsCorrectAnswer(2);
                        }else{
                            ConvertedQuestionData.get(position).setIsCorrectAnswer(3);
                        }

                    }else{
                        ConvertedQuestionData.get(position).setIsCorrectAnswer(1);
                        ConvertedQuestionData.get(position).setProcessStart(answerSkip);
                    }
                }else{
                    ConvertedQuestionData.get(position).setProcessStart(questionIsMarked);
                }
                updateTestNumber();

                SendingDataToDB pd = new SendingDataToDB();
                pd.setAns(ConvertedQuestionData.get(position).getAnswerProvided());
                pd.setUserID(userID);
                pd.setQuestionID(Integer.parseInt(ConvertedQuestionData.get(position).getQUESTIONID()));

                new saveAnswerToDatabase(pd).execute();
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));


        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        previous.setVisibility(View.INVISIBLE);
        finish = findViewById(R.id.finish);
        showAnswer = findViewById(R.id.view_answer);
        showGrid = findViewById(R.id.control);

        slideView = findViewById(R.id.slideView);
        slideViewAnswer = findViewById(R.id.slideViewAnswer);
        dim = findViewById(R.id.dim);

        slideUp = new SlideUp(slideView);
        slideUp.hideImmediately();
        answerText = findViewById(R.id.answerText);
        OptionText = findViewById(R.id.optionText);
        slideUpAnswer = new SlideUp(slideViewAnswer);
        slideUpAnswer.hideImmediately();

        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Fragment active=mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem());
                if(active!=null){
                    ((questionFragment)active).showCorrectAnswer();
                }*/
                int val = mPager.getCurrentItem();
                String option="";
                switch (Integer.parseInt(ConvertedQuestionData.get(val).getQUESTIONCORRECTOPTION())){
                    case 1:
                        option = "A";
                        break;
                    case 2:
                        option = "B";
                        break;
                    case 3:
                        option = "C";
                        break;
                    case 4:
                        option = "D";
                        break;
                    case 5:
                        option = "E";
                        break;
                }


                String correctOption = "Correct Answer: "+option;
                OptionText.setText(correctOption);

                if (!ConvertedQuestionData.get(val).getQUESTIONANSDESC().equals("")){
                    answerText.setText(Html.fromHtml(ConvertedQuestionData.get(val).getQUESTIONANSDESC(), new com.application.onlineTestSeries.PracticeTest.Test.Helper.GlideImageGetter(getApplicationContext(), answerText), null));
                }else{
                    answerText.setText("Explanation is not provided for the question");
                }

                //answerText.setText(Html.fromHtml(ConvertedQuestionData.get(val).getQUESTIONANSDESC(), new GlideImageGetter(getApplicationContext(), answerText), null));
                if (slideUpAnswer.isVisible()){
                    slideUpAnswer.animateOut();
                }else {

                    slideUpAnswer.animateIn();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val = mPager.getCurrentItem();
                if (ConvertedQuestionData.get(val).getProcessStart().equals(answerGiven)){
                    if (ConvertedQuestionData.get(val).isMarked()){
                        ConvertedQuestionData.get(val).setProcessStart(questionIsMarked);
                    }

                    if (ConvertedQuestionData.get(val).getAnswerProvided()==Integer.parseInt(ConvertedQuestionData.get(val).getQUESTIONCORRECTOPTION())){
                        ConvertedQuestionData.get(val).setIsCorrectAnswer(2);
                    }else{
                        ConvertedQuestionData.get(val).setIsCorrectAnswer(3);
                    }

                }else{
                    ConvertedQuestionData.get(val).setIsCorrectAnswer(1);
                    if (ConvertedQuestionData.get(val).isMarked()){
                        ConvertedQuestionData.get(val).setProcessStart(questionIsMarked);
                    }else {
                        ConvertedQuestionData.get(val).setProcessStart(answerSkip);
                    }
                }

                mPager.setCurrentItem(val+1);
                mPagerAdapter.notifyDataSetChanged();
                updateTestNumber();


                SendingDataToDB pd = new SendingDataToDB();
                pd.setAns(ConvertedQuestionData.get(val).getAnswerProvided());
                pd.setUserID(userID);
                pd.setQuestionID(Integer.parseInt(ConvertedQuestionData.get(val).getQUESTIONID()));
                new saveAnswerToDatabase(pd).execute();
                //Toasty.info(getApplicationContext(),mPager.getCurrentItem()+"--"+ConvertedQuestionData.size(),Toast.LENGTH_LONG,true).show();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val = mPager.getCurrentItem();
                if (ConvertedQuestionData.get(val).getProcessStart().equals(answerGiven)){
                    if (ConvertedQuestionData.get(val).isMarked()){
                        ConvertedQuestionData.get(val).setProcessStart(questionIsMarked);
                    }

                    if (ConvertedQuestionData.get(val).getAnswerProvided()==Integer.parseInt(ConvertedQuestionData.get(val).getQUESTIONCORRECTOPTION())){
                        ConvertedQuestionData.get(val).setIsCorrectAnswer(2);
                    }else{
                        ConvertedQuestionData.get(val).setIsCorrectAnswer(3);
                    }

                }else{
                    ConvertedQuestionData.get(val).setIsCorrectAnswer(1);

                    if (ConvertedQuestionData.get(val).isMarked()){
                        ConvertedQuestionData.get(val).setProcessStart(questionIsMarked);
                    }else {
                        ConvertedQuestionData.get(val).setProcessStart(answerSkip);
                    }
                }
                updateTestNumber();

                mPager.setCurrentItem(val-1);
                mPagerAdapter.notifyDataSetChanged();
                SendingDataToDB pd = new SendingDataToDB();
                pd.setAns(ConvertedQuestionData.get(val).getAnswerProvided());
                pd.setUserID(userID);
                pd.setQuestionID(Integer.parseInt(ConvertedQuestionData.get(val).getQUESTIONID()));

                new saveAnswerToDatabase(pd).execute();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(TestActivity.this);
                builder.setTitle("Quiz")
                    .setMessage("Are you sure you want to finish test?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            previous.setOnClickListener(null);
                            next.setOnClickListener(null);
                            saveAnsweronFinish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            }
        });

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                if (ConvertedQuestionData.get(position).getProcessStart().equals(answerGiven)){
                    if (ConvertedQuestionData.get(position).isMarked()){
                        ConvertedQuestionData.get(position).setProcessStart(questionIsMarked);
                    }
                }else{
                    if (ConvertedQuestionData.get(position).isMarked()){
                        ConvertedQuestionData.get(position).setProcessStart(questionIsMarked);
                    }else {
                        ConvertedQuestionData.get(position).setProcessStart(answerSkip);
                    }
                }
                if (position==0){
                    previous.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.VISIBLE);
                }else{
                    previous.setVisibility(View.VISIBLE);
                }

                if (ConvertedQuestionData.size()>0){
                    next.setVisibility(View.VISIBLE);
                    if (position==(ConvertedQuestionData.size()-1)){
                        next.setVisibility(View.INVISIBLE);
                    }
                }
                //Log.d(TAG, "onPageSelected: "+position);
                updateTestNumber();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        showGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Questionadapter.notifyDataSetChanged();
                if (slideUp.isVisible()){
                    slideUp.animateOut();
                }else {
                    slideUp.animateIn();
                }
            }
        });

        slideUp.setSlideListener(new SlideUp.SlideListener() {
            @Override
            public void onSlideDown(float v) {
                dim.setAlpha(1 - (v / 100));
            }

            @Override
            public void onVisibilityChanged(int i) {
                if (i == View.GONE) {
                   // fab.show();
                }
            }
        });
        Questionadapter.notifyDataSetChanged();
        mPagerAdapter.notifyDataSetChanged();

        mPager.setCurrentItem(getIntent().getIntExtra("position",0));
        updateTestNumber();

    }

    private void showAlertOrCheckSubscription() {
        RealmResults<subscriptionUserData> results = realmDb.where(subscriptionUserData.class).findAll();
        String status ="",isActive ="",date = "";
        for (subscriptionUserData item: results) {
            status = item.getSUBSCRIPTIONSTATUS();
            isActive = item.getSUBSCRIBERSUBSCRIPTIONSTATUS();
            date = item.getSUBSCRIBERSUBSCRIPTIONDATE();
        }

        if (!isActive.equals("1") || !status.equals("Active")){
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(TestActivity.this);
            builder.setCancelable(false);
            builder.setTitle("Information")
                    .setMessage(R.string.subscription_message)
                    .setPositiveButton("Not now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Subscribe now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(TestActivity.this, SubscriptionSubscribe.class));

                        }
                    })
                    .show();
        }
    }

    public void updateTestNumber(){
        String attempted = null;
        int totalQuestionAttempted=0;
        for (practiceTestData item: ConvertedQuestionData ){
            if (item.isAttempted()){
                totalQuestionAttempted +=1;
            }
        }

        attempted = String.valueOf(totalQuestionAttempted)+"/"+testCount;
        totalAttemptedTV.setText(attempted);

        String currentNumber;
        int val1 = mPager.getCurrentItem()+1;
        currentNumber = String.valueOf(val1)+"/"+testCount;
        currentQuestionNumberTV.setText(currentNumber);
    }

    private void saveAnsweronFinish() {
        int val = mPager.getCurrentItem();
        if (ConvertedQuestionData.get(val).getProcessStart().equals(answerGiven)){
            if (ConvertedQuestionData.get(val).isMarked()){
                ConvertedQuestionData.get(val).setProcessStart(questionIsMarked);
            }
        }else{
            if (ConvertedQuestionData.get(val).isMarked()){
                ConvertedQuestionData.get(val).setProcessStart(questionIsMarked);
            }else {
                ConvertedQuestionData.get(val).setProcessStart(answerSkip);
            }
        }

        previous.setOnClickListener(null);
        next.setOnClickListener(null);

        SendingDataToDB pd = new SendingDataToDB();
        pd.setAns(ConvertedQuestionData.get(val).getAnswerProvided());
        pd.setUserID(userID);
        pd.setQuestionID(Integer.parseInt(ConvertedQuestionData.get(val).getQUESTIONID()));
        pd.setTestID(Integer.parseInt(ConvertedQuestionData.get(val).getTestID()));

        updateTestNumber();
        new saveAnswerToDatabase(pd).execute();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(TestActivity.this,resultActivity.class);
                startActivity(intent);
                finish();
            }
        }, 900);
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

    @Override
    public void onBackPressed() {
        if (slideUp.isVisible()){
            slideUp.animateOut();
        }else if(slideUpAnswer.isVisible()){
            slideUpAnswer.animateOut();
        }else{
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(TestActivity.this);
            builder.setTitle("Quiz")
            .setMessage("Are you sure you want to exit test?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(TestActivity.this,MainActivity.class));
                        finish();
                    }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
            }
            })
            .show();
        }

    }

    public class saveAnswerToDatabase extends AsyncTask<String,Void,Void> {

        SendingDataToDB sdtb;

        public saveAnswerToDatabase(SendingDataToDB pd) {
            sdtb = pd;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... voids) {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<QuestionUpdateResponse> userCall = service.getAddQuestion(sdtb.getUserID(),String.valueOf(sdtb.getAns()),String.valueOf(sdtb.getQuestionID()),TestID);
            userCall.enqueue(new Callback<QuestionUpdateResponse>() {
                @Override
                public void onResponse(Call<QuestionUpdateResponse> call, Response<QuestionUpdateResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body()!=null){
                            //Log.d("onFailure", response.body().getMessage());
                        }else{
                            Log.d("onFailure", "response null");
                        }
                    }else{
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.test_menu, menu);
        //MenuItem menugraph = menu.findItem(R.id.resetTable);
        return super.onCreateOptionsMenu(menu);
    }*/

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

       /* if (id == R.id.resetTable){
            //takeScreenshot(ScreenshotType.FULL);

            //clearTest();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /*private void clearTest() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(TestActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(TestActivity.this);
        }
        builder.setTitle("Quiz")
        .setMessage("Are you sure you want to reset test")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new clearAnswerToDatabase().execute();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show();

    }

    public class clearAnswerToDatabase extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... voids) {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<QuestionUpdateResponse> userCall = service.empty_records_usertest(userID);
            userCall.enqueue(new Callback<QuestionUpdateResponse>() {
                @Override
                public void onResponse(Call<QuestionUpdateResponse> call, Response<QuestionUpdateResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body()!=null){
                            if (response.body().getStatus()){
                                showMessageSuccess(response.body().getMessage());
                                startActivity(new Intent(TestActivity.this, MainActivity.class));
                                finish();
                            }else{
                               showMessageInfo(response.body().getMessage());
                            }
                        }else{
                            Log.d("onFailure", "response null");
                        }
                    }else{
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
    }*/

    private void showMessageSuccess(String message) {
        Toasty.success(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
    }

    private void showMessageInfo(String message) {
        Toasty.info(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
    }
}
