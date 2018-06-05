package com.application.onlineTestSeries.PracticeTest.TestResult;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.application.onlineTestSeries.HelperClasses.ScreenshotType;
import com.application.onlineTestSeries.HelperClasses.ScreenshotUtils;
import com.application.onlineTestSeries.Home.MainActivity;
import com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test.PreviousTestActivity;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;
import com.application.onlineTestSeries.PracticeTest.Test.SingleDataHolder.ServerDataGetter;

public class resultActivity extends AppCompatActivity {

    private static List<practiceTestData> ConvertedQuestionData;
    TextView tvTotalQus,tvNotAttemptedQus,tvCorrectAns,tvWrongAns,btnFinish,tvTotalScore,view_answer,tvTotalQus1;

    LinearLayout rootContent;
    PieChart chart;
    ArrayList<Entry> entries ;
    ArrayList<String> PieEntryLabels;
    PieDataSet pieDataSet;
    PieData pieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Practice Test Result");
        ConvertedQuestionData = new ArrayList<>();
        ConvertedQuestionData = ServerDataGetter.getInstance().getConvertedQuestionData();
        entries = new ArrayList<>();
        PieEntryLabels = new ArrayList<String>();
        //Typeface customFont1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");

        tvTotalQus =findViewById(R.id.tvTotalQus);
        tvTotalQus1 =findViewById(R.id.tvTotalQus1);
        chart = findViewById(R.id.chart1);

        tvNotAttemptedQus =findViewById(R.id.notAttemptedQus);
        tvCorrectAns =findViewById(R.id.CorrectAns);
        tvWrongAns =findViewById(R.id.wrongAns);
        btnFinish =findViewById(R.id.btnFinish);
        //btnFinish.setTypeface(customFont1);
        rootContent = findViewById(R.id.root_content);
        tvTotalScore = findViewById(R.id.tvTotalScore);
        view_answer = findViewById(R.id.view_answer);

        int totalAnsCorrect=0,totalQuestionWrong=0,totalQuestionAttempted=0;
        for (practiceTestData item: ConvertedQuestionData ){
            if (item.isAttempted()){
                totalQuestionAttempted +=1;
                if (Integer.parseInt(item.getQUESTIONCORRECTOPTION())==item.getAnswerProvided()){
                    totalAnsCorrect +=1;
                }else{
                    totalQuestionWrong +=1;
                }
            }
        }
        int unattempted = ConvertedQuestionData.size() - (totalAnsCorrect+ totalQuestionWrong);

        tvCorrectAns.setText(""+totalAnsCorrect);
        tvWrongAns.setText(""+totalQuestionWrong);
        tvCorrectAns.setText(""+totalAnsCorrect);
        tvNotAttemptedQus.setText(""+unattempted);
        tvTotalQus.setText("Total Questions: "+ConvertedQuestionData.size());
        tvTotalScore.setText(totalAnsCorrect+"/"+ConvertedQuestionData.size());
        tvTotalQus1.setText("Total Questions: "+ConvertedQuestionData.size());
        entries.add(new BarEntry(totalAnsCorrect, 0));
        PieEntryLabels.add("Correct Answer");

        entries.add(new BarEntry(totalQuestionWrong, 1));
        PieEntryLabels.add("Wrong Answer");

       // tvTestTimeTaken.setText(getDurationString(seconds));

        //int unattempted =  ConvertedQuestionData.size() - (totalAnsCorrect + totalQuestionWrong);
        //float getMArksWithNegative = correctAns - (IncorrectAns*negativeMark);

        entries.add(new BarEntry(unattempted, 2));
        PieEntryLabels.add("Unattempted");

        final int[] MY_COLORS = {Color.rgb(124,252,0), Color.rgb(255,0,0), Color.rgb(255,192,0)};
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c: MY_COLORS) colors.add(c);

        pieDataSet = new PieDataSet(entries, "");
        pieData = new PieData(PieEntryLabels, pieDataSet);
        //pieDataSet.set
        pieDataSet.setColors(colors);
        chart.setData(pieData);
        chart.setCenterTextSize(12);
        chart.setCenterText("Remarks");
        //pieChart.setUsePercentValues(true);
        pieDataSet.setValueTextSize(8);
        // pieDataSet.setValueTextColor(Integer.parseInt("#ffffff"));
        chart.animateY(3000);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(resultActivity.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        view_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(resultActivity.this, PreviousTestActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(resultActivity.this, MainActivity.class);
        startActivity(intent);
        
        super.onBackPressed();
    }
}
