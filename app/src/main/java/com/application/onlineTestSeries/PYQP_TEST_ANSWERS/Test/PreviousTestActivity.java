package com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test.Helper.GlideImageGetter;
import com.application.onlineTestSeries.R;
import com.mancj.slideup.SlideUp;

import java.util.ArrayList;
import java.util.List;

import com.application.onlineTestSeries.HelperClasses.GridSpacingItemDecoration;
import com.application.onlineTestSeries.HelperClasses.RecyclerTouchListener;
import com.application.onlineTestSeries.Home.MainActivity;
import com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test.Adapter.QuestionListAdapter;
import com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test.Adapter.ViewPagerAdapter;
import com.application.onlineTestSeries.PracticeTest.Constants.testconstants;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;
import com.application.onlineTestSeries.PracticeTest.Test.Adapter.CustomViewPager;
import com.application.onlineTestSeries.PracticeTest.Test.SingleDataHolder.ServerDataGetter;
import com.application.onlineTestSeries.login.Models.UserData;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class PreviousTestActivity extends AppCompatActivity {

    private CustomViewPager mPager;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private static List<practiceTestData> ConvertedQuestionData;

    private static String Progress = testconstants.notStarted;

    ImageView next, previous, btnShowGrid;
    private SlideUp slideUp,slideUpAnswer;
    private View dim;
    private View slideView,slideViewAnswer;
    QuestionListAdapter Questionadapter;
    RecyclerView recyclerView;
    private CardView card;
    ProgressDialog progress;
    TextView answerText,currentQuestionNumberTV,totalAttemptedTV,checkAnswer,optionText,finish;
    Realm realmDb;
    int userID,TestID;
    RelativeLayout currenttestlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_test);
        final String TAG = getLocalClassName();
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Attempted Answers");*/
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        currenttestlayout = findViewById(R.id.currenttestlayout);
        currenttestlayout.setVisibility(View.GONE);
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

        currentQuestionNumberTV = findViewById(R.id.currentQuestionNumberTV);
        totalAttemptedTV = findViewById(R.id.totalAttemptedTV);
        checkAnswer = findViewById(R.id.checkAnswer);
        finish = findViewById(R.id.finish);
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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                card = view.findViewById(R.id.QuestionCard);
                mPager.setCurrentItem(position);
                slideUp.animateOut();
                Log.d(TAG, "onClick: "+ConvertedQuestionData.get(position).getIsCorrectAnswer());
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        mPager = findViewById(R.id.pager);
        mPager.setPagingEnabled(false);
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),ConvertedQuestionData);
        mPager.setAdapter(mPagerAdapter);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        btnShowGrid = findViewById(R.id.control);

        slideView = findViewById(R.id.slideView);
        slideViewAnswer = findViewById(R.id.slideViewAnswer);
        dim = findViewById(R.id.dim);

        slideUp = new SlideUp(slideView);
        slideUp.hideImmediately();
        answerText = findViewById(R.id.answerText);
        optionText = findViewById(R.id.optionText);
        slideUpAnswer = new SlideUp(slideViewAnswer);
        slideUpAnswer.hideImmediately();

        checkAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = mPager.getCurrentItem();

                String correctOption = "Correct Answer: "+ConvertedQuestionData.get(val).getQUESTIONCORRECTOPTION();
                optionText.setText(correctOption);
                if (!ConvertedQuestionData.get(val).getQUESTIONANSDESC().equals("")){

                    answerText.setText(Html.fromHtml(ConvertedQuestionData.get(val).getQUESTIONANSDESC(), new GlideImageGetter(getApplicationContext(), answerText), null));
                }else{
                    answerText.setText("Explanation is not provided for the question");
                }

                slideUpAnswer.animateIn();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val = mPager.getCurrentItem();
                mPager.setCurrentItem(val+1);
                mPagerAdapter.notifyDataSetChanged();

                updateTestNumber();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val = mPager.getCurrentItem();
                mPager.setCurrentItem(val-1);
                mPagerAdapter.notifyDataSetChanged();
                updateTestNumber();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(PreviousTestActivity.this,MainActivity.class);

                startActivity(intent);
                finish();
            }
        });

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
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
                slideUp.animateIn();
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
            finish();
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
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void updateTestNumber(){
        String attempted = null;
        int totalQuestionAttempted=0;
        for (practiceTestData item: ConvertedQuestionData ){
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

    private void showMessageSuccess(String message) {
        Toasty.success(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
    }

    private void showMessageInfo(String message) {
        Toasty.info(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
    }
}
