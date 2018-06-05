package com.application.onlineTestSeries.PYQPTest.Test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.HelperClasses.RecyclerTouchListener;
import com.application.onlineTestSeries.PYQPTest.Test.Adapter.QuestionTestListAdapter;
import com.application.onlineTestSeries.PYQPTest.Test.Adapter.TestViewPagerAdapter;
import com.application.onlineTestSeries.PYQPTest.Test.Models.PYQP_Response;
import com.application.onlineTestSeries.PYQPTest.Test.Models.PYQP_Test_Data;
import com.application.onlineTestSeries.PYQPTest.Test.SingleDataHolder.ServerTestGetter;
import com.application.onlineTestSeries.PYQPTest.Test.TestResult.resultTestActivity;
import com.application.onlineTestSeries.PracticeTest.Constants.testconstants;
import com.application.onlineTestSeries.PracticeTest.Models.QuestionUpdateResponse;
import com.application.onlineTestSeries.PracticeTest.Models.SendingDataToDB;
import com.application.onlineTestSeries.PracticeTest.Test.Adapter.CustomViewPager;
import com.application.onlineTestSeries.PracticeTest.Test.Helper.GlideImageGetter;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.login.Models.UserData;
import com.mancj.slideup.SlideUp;

import java.util.ArrayList;
import java.util.List;

import com.application.onlineTestSeries.Network.ApiClient;
import com.application.onlineTestSeries.Network.ApiService;

import es.dmoral.toasty.Toasty;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.*;


public class MainTest extends AppCompatActivity {

    private CustomViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private static String Progress = testconstants.notStarted;

    ImageView showAnswer,btnShowGrid;
    private SlideUp slideUp,slideUpAnswer;
    private View dim;
    private View slideView,slideViewAnswer;
    QuestionTestListAdapter Questionadapter;
    RecyclerView recyclerView;
    private CardView card;
    TextView answerText,timerCount,currentQuestionNumberTV,totalAttemptedTV,finish;
    Realm realmDb;
    int userID;
    CountDownTimer cdt;
    String testID,TAG;
    private static List<PYQP_Test_Data> ConvertedQuestionData;
    int testDuration;
    long testDurationMillisec;
    long second = 0;
    RelativeLayout previoustestlayout;
    LinearLayout next, previous;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        TAG = this.getClass().getName();
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra("testName"));*/
        previoustestlayout = findViewById(R.id.previoustestlayout);
        previoustestlayout.setVisibility(View.GONE);
        timerCount = findViewById(R.id.timerCount);
        currentQuestionNumberTV = findViewById(R.id.currentQuestionNumberTV);
        totalAttemptedTV = findViewById(R.id.totalAttemptedTV);
        testID = getIntent().getStringExtra("testID");
        testDuration = Integer.parseInt(getIntent().getStringExtra("testDuration").trim());

        testDurationMillisec = testDuration * 60 * 1000;//min * sec* millisec

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

        ConvertedQuestionData = new ArrayList<>();
        ConvertedQuestionData = ServerTestGetter.getInstance().getConvertedQuestionData();

        Questionadapter = new QuestionTestListAdapter(this,ConvertedQuestionData);

        //RecyclerView For the Question
        recyclerView = findViewById(R.id.QuestionListRecycler);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 6);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(Questionadapter);

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
                    }else{
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
                pd.setTestID(Integer.parseInt(ConvertedQuestionData.get(position).getTestID()));

                new saveAnswerToDatabase(pd).execute();
                //new saveResultToDb().execute();
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        mPager = findViewById(R.id.pager);
        mPager.setPagingEnabled(false);
        mPagerAdapter = new TestViewPagerAdapter(getSupportFragmentManager(),ConvertedQuestionData);
        mPager.setAdapter(mPagerAdapter);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        finish = findViewById(R.id.finish);
        showAnswer = findViewById(R.id.view_answer);
        btnShowGrid = findViewById(R.id.control);

        slideView = findViewById(R.id.slideView);
        slideViewAnswer = findViewById(R.id.slideViewAnswer);
        dim = findViewById(R.id.dim);

        slideUp = new SlideUp(slideView);
        slideUp.hideImmediately();
        answerText = findViewById(R.id.answerText);

        slideUpAnswer = new SlideUp(slideViewAnswer);
        slideUpAnswer.hideImmediately();

        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = mPager.getCurrentItem();
                answerText.setText(Html.fromHtml(ConvertedQuestionData.get(val).getQUESTIONANSDESC(), new GlideImageGetter(getApplicationContext(), answerText), null));

                if (slideUpAnswer.isVisible()){
                    slideUpAnswer.animateOut();
                }else {
                    slideUpAnswer.animateIn();
                }

            }
        });

        cdt = new CountDownTimer(testDurationMillisec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;

                long elapsedHours = millisUntilFinished / hoursInMilli;
                millisUntilFinished = millisUntilFinished % hoursInMilli;

                long elapsedMinutes = millisUntilFinished / minutesInMilli;
                millisUntilFinished = millisUntilFinished % minutesInMilli;

                long elapsedSeconds = millisUntilFinished / secondsInMilli;

                second+=1;
                Log.d("second",""+second);


                String yy = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes,elapsedSeconds);
                timerCount.setText(yy);
            }

            @Override
            public void onFinish() {
                timerCount.setText("00:00:00");
                saveAnsweronFinish();
            }
        }.start();

        previous.setVisibility(View.INVISIBLE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


                SendingDataToDB pd = new SendingDataToDB();
                pd.setAns(ConvertedQuestionData.get(val).getAnswerProvided());
                pd.setUserID(userID);
                pd.setQuestionID(Integer.parseInt(ConvertedQuestionData.get(val).getQUESTIONID()));
                pd.setTestID(Integer.parseInt(ConvertedQuestionData.get(val).getTestID()));

                new saveAnswerToDatabase(pd).execute();
                //new saveResultToDb().execute();
                /*if (val>0){
                    previous.setVisibility(View.VISIBLE);
                }

                if (ConvertedQuestionData.size()>0){
                    if (val==(ConvertedQuestionData.size()-1)){
                        next.setVisibility(View.INVISIBLE);
                    }
                }*/

                mPager.setCurrentItem(val+1);
                mPagerAdapter.notifyDataSetChanged();
                updateTestNumber();
                Log.d(TAG, "onClick: next->"+ val);
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
                }else{
                    if (ConvertedQuestionData.get(val).isMarked()){
                        ConvertedQuestionData.get(val).setProcessStart(questionIsMarked);
                    }else {
                        ConvertedQuestionData.get(val).setProcessStart(answerSkip);
                    }
                }

                SendingDataToDB pd = new SendingDataToDB();
                pd.setAns(ConvertedQuestionData.get(val).getAnswerProvided());
                pd.setUserID(userID);
                pd.setQuestionID(Integer.parseInt(ConvertedQuestionData.get(val).getQUESTIONID()));
                pd.setTestID(Integer.parseInt(ConvertedQuestionData.get(val).getTestID()));

                new saveAnswerToDatabase(pd).execute();
                //new saveResultToDb().execute();

               /* if ((val-1)==0||val==0){
                    previous.setVisibility(View.INVISIBLE);
                }

                if (ConvertedQuestionData.size()>0){
                    //if (val==(ConvertedQuestionData.size()-1)){
                    next.setVisibility(View.VISIBLE);
                    //}
                }*/

                Log.d(TAG, "onClick: prev->"+ val);
                mPager.setCurrentItem(val-1);
                mPagerAdapter.notifyDataSetChanged();
                updateTestNumber();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(MainTest.this);
                builder.setTitle("Quiz")
                        .setMessage("Are you sure you want to finish test?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                saveAnsweronFinish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                //Toast.makeText(MainTest.this, ""+mPagerAdapter.getCount(), Toast.LENGTH_SHORT).show();
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
                Log.d(TAG, "onPageSelected: "+position);
                updateTestNumber();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnShowGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Questionadapter.notifyDataSetChanged();
                if (slideUp.isVisible()){
                    slideUp.animateOut();
                }else{
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

        updateTestNumber();
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
        /*mPager.setCurrentItem(val-1);
        mPagerAdapter.notifyDataSetChanged();    */
        updateTestNumber();
        new saveAnswerToDatabase(pd).execute();
        cdt.cancel();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(MainTest.this,resultTestActivity.class);
                intent.putExtra("testID",testID);
                intent.putExtra("testName",getIntent().getStringExtra("testName"));
                startActivity(intent);
                finish();
            }
        }, 900);
    }


    public void updateTestNumber(){
        String attempted = null;
        int totalQuestionAttempted=0;
        for (PYQP_Test_Data item: ConvertedQuestionData ){
            if (item.isAttempted()){
                totalQuestionAttempted +=1;
            }
        }

        attempted = String.valueOf(totalQuestionAttempted)+"/"+ConvertedQuestionData.size();
        totalAttemptedTV.setText(attempted);

        String currentNumber;
        int val1 = mPager.getCurrentItem()+1;
        currentNumber = String.valueOf(val1)+"/"+ConvertedQuestionData.size();
        currentQuestionNumberTV.setText(currentNumber);
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
            builder = new AlertDialog.Builder(MainTest.this);
            builder.setTitle("Quiz")
                    .setMessage("Are you sure you want to exit test?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            cdt.cancel();
                            finish();
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

    }

    public class saveAnswerToDatabase extends AsyncTask<String,Void,Void> {

        SendingDataToDB sdtb;
        List<PYQP_Test_Data> temp = new ArrayList<>();
        public saveAnswerToDatabase(SendingDataToDB pd) {
            sdtb = pd;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            temp = ServerTestGetter.getInstance().getConvertedQuestionData();

        }

        @Override
        protected Void doInBackground(String... voids) {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<QuestionUpdateResponse> userCall = service.getAddQuestion(sdtb.getUserID(),String.valueOf(sdtb.getAns()),String.valueOf(sdtb.getQuestionID()),Integer.parseInt(testID));
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

            int totalAnsCorrect=0,totalQuestionWrong=0,totalQuestionAttempted=0;
            for (PYQP_Test_Data item: ConvertedQuestionData ){
                if (item.isAttempted()){
                    totalQuestionAttempted +=1;
                    //Log.d("options",item.getQUESTIONCORRECTOPTION()+"---"+item.getAnswerProvided());
                    if (Integer.parseInt(item.getQUESTIONCORRECTOPTION())==item.getAnswerProvided()){
                        totalAnsCorrect +=1;
                    }else{
                        totalQuestionWrong +=1;
                    }
                }
            }

            Call<PYQP_Response> userCall1 = service.add_result_pyqp(String.valueOf(userID),testID,String.valueOf(totalAnsCorrect),String.valueOf(temp.size()),String.valueOf(totalQuestionWrong),String.valueOf(totalQuestionAttempted),second);
            userCall1.enqueue(new Callback<PYQP_Response>() {
                @Override
                public void onResponse(Call<PYQP_Response> call, Response<PYQP_Response> response) {
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
                public void onFailure(Call<PYQP_Response> call, Throwable t) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        //findMenuItems.inflate(R.menu.test_menu, menu);
        //MenuItem menugraph = menu.findItem(R.id.resetTable);
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

        /*if (id == R.id.resetTable){
            //takeScreenshot(ScreenshotType.FULL);

            clearTest();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public class saveResultToDb extends AsyncTask<String,Void,Void> {

        List<PYQP_Test_Data> temp = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            temp = ServerTestGetter.getInstance().getConvertedQuestionData();
        }

        @Override
        protected Void doInBackground(String... voids) {
            int totalAnsCorrect=0,totalQuestionWrong=0,totalQuestionAttempted=0;
            for (PYQP_Test_Data item: ConvertedQuestionData ){
                if (item.isAttempted()){
                    totalQuestionAttempted +=1;
                    Log.d("options",item.getQUESTIONCORRECTOPTION()+"---"+item.getAnswerProvided());
                    if (Integer.parseInt(item.getQUESTIONCORRECTOPTION())==item.getAnswerProvided()){
                        totalAnsCorrect +=1;
                    }else{
                        totalQuestionWrong +=1;
                    }
                }
            }

            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<PYQP_Response> userCall = service.add_result_pyqp(String.valueOf(userID),testID,String.valueOf(totalAnsCorrect),String.valueOf(temp.size()),String.valueOf(totalQuestionWrong),String.valueOf(totalQuestionAttempted), second);
            userCall.enqueue(new Callback<PYQP_Response>() {
                @Override
                public void onResponse(Call<PYQP_Response> call, Response<PYQP_Response> response) {
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
                public void onFailure(Call<PYQP_Response> call, Throwable t) {
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
        Toasty.success(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
    }

    private void showMessageInfo(String message) {
        Toasty.info(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
    }
}