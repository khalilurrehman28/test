package com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test.QuestionFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.application.onlineTestSeries.PYQP_TEST_ANSWERS.Test.Helper.GlideImageGetter;
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestData;
import com.application.onlineTestSeries.PracticeTest.Test.SingleDataHolder.ServerDataGetter;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.login.Models.UserData;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.ARG_PAGE;
import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.answerGiven;
import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.answerSkip;
import static com.application.onlineTestSeries.PracticeTest.Constants.testconstants.questionIsMarked;

public class questionPYQPFragment extends Fragment {
    private static List<practiceTestData> ConvertedQuestionData;
    /**
     * The argument key for the page number this fragment represents.
     */
    TextView question,questionlabel;
    RadioButton option1, option2, option3, option4,option5;
    RadioGroup radioGroup;
    ImageView markQuestion,erase;
    Realm realmDb;
    int userID;
    private int mPageNumber;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static questionPYQPFragment create(int pageNumber) {
        questionPYQPFragment fragment = new questionPYQPFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pyqptestfragment, container, false);
        ConvertedQuestionData = new ArrayList<>(ServerDataGetter.getInstance().getConvertedQuestionData());
        mPageNumber = getArguments() != null ? getArguments().getInt(ARG_PAGE) : 1;
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
        questionlabel = v.findViewById(R.id.questionlabel);
        question = v.findViewById(R.id.question);
        option1 = v.findViewById(R.id.option1);
        option2 = v.findViewById(R.id.option2);
        option3 = v.findViewById(R.id.option3);
        option4 = v.findViewById(R.id.option4);
        option5 = v.findViewById(R.id.option5);

        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);
        option5.setEnabled(false);
        erase = v.findViewById(R.id.erase);

        markQuestion = v.findViewById(R.id.markQuestion);

        if (ConvertedQuestionData.get(mPageNumber).isMarked()) {
            markQuestion.setImageResource(R.drawable.ic_bookmark_filled);
        } else {
            markQuestion.setImageResource(R.drawable.ic_bookmark_nav);
        }

        markQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConvertedQuestionData.get(mPageNumber).isMarked()) {
                    markQuestion.setImageResource(R.drawable.ic_bookmark_nav);
                    ConvertedQuestionData.get(mPageNumber).setMarked(false);
                    if (ConvertedQuestionData.get(mPageNumber).getProcessStart().equals(questionIsMarked)) {
                        if(ConvertedQuestionData.get(mPageNumber).isAttempted()){
                            ConvertedQuestionData.get(mPageNumber).setProcessStart(answerGiven);
                        }else{
                            ConvertedQuestionData.get(mPageNumber).setProcessStart(answerSkip);
                        }
                    } else {
                        if(ConvertedQuestionData.get(mPageNumber).isAttempted()){
                            ConvertedQuestionData.get(mPageNumber).setProcessStart(answerGiven);
                        }else{
                            ConvertedQuestionData.get(mPageNumber).setProcessStart(answerSkip);
                        }
                    }
                }else {
                    markQuestion.setImageResource(R.drawable.ic_bookmark_filled);
                    ConvertedQuestionData.get(mPageNumber).setMarked(true);
                    ConvertedQuestionData.get(mPageNumber).setProcessStart(questionIsMarked);
                }
            }
        });

        question.setText(Html.fromHtml(ConvertedQuestionData.get(mPageNumber).getQUESTIONTITLE(), new GlideImageGetter(getContext(), question), null));
        option1.setText("A. "+Html.fromHtml(ConvertedQuestionData.get(mPageNumber).getQUESTIONOPTION1(), new GlideImageGetter(getContext(), option1), null));
        option2.setText("B. "+Html.fromHtml(ConvertedQuestionData.get(mPageNumber).getQUESTIONOPTION2(), new GlideImageGetter(getContext(), option2), null));
        option3.setText("C. "+Html.fromHtml(ConvertedQuestionData.get(mPageNumber).getQUESTIONOPTION3(), new GlideImageGetter(getContext(), option3), null));
        option4.setText("D. "+Html.fromHtml(ConvertedQuestionData.get(mPageNumber).getQUESTIONOPTION4(), new GlideImageGetter(getContext(), option4), null));
        if (!ConvertedQuestionData.get(mPageNumber).getQUESTIONOPTION5().equals("")) {
            option5.setText("E. "+Html.fromHtml(ConvertedQuestionData.get(mPageNumber).getQUESTIONOPTION5(), new GlideImageGetter(getContext(), option4), null));
        }else{
            option5.setVisibility(View.INVISIBLE);
        }
        questionlabel.setText("Q"+ConvertedQuestionData.get(mPageNumber).getCounter()+": ");

        switch (ConvertedQuestionData.get(mPageNumber).getAnswerProvided()){
            case 1:
                if (ConvertedQuestionData.get(mPageNumber).getAnswerProvided()==Integer.parseInt(ConvertedQuestionData.get(mPageNumber).getQUESTIONCORRECTOPTION())){
                    option1.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_border));
                }else{
                    option1.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_wrong_border));
                }
                option1.setChecked(true);
                option1.setSelected(true);
                break;
            case 2:
                if (ConvertedQuestionData.get(mPageNumber).getAnswerProvided()==Integer.parseInt(ConvertedQuestionData.get(mPageNumber).getQUESTIONCORRECTOPTION())){
                    option2.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_border));
                }else{
                    option2.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_wrong_border));
                }
                option2.setChecked(true);
                option2.setSelected(true);
                break;
            case 3:
                if (ConvertedQuestionData.get(mPageNumber).getAnswerProvided()==Integer.parseInt(ConvertedQuestionData.get(mPageNumber).getQUESTIONCORRECTOPTION())){
                    option3.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_border));
                }else{
                    option3.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_wrong_border));
                }
                option3.setChecked(true);
                option3.setSelected(true);
                break;
            case 4:
                if (ConvertedQuestionData.get(mPageNumber).getAnswerProvided()==Integer.parseInt(ConvertedQuestionData.get(mPageNumber).getQUESTIONCORRECTOPTION())){
                    option4.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_border));
                }else{
                    option4.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_wrong_border));
                }
                option4.setChecked(true);
                option4.setSelected(true);
                break;
            case 5:
                if (ConvertedQuestionData.get(mPageNumber).getAnswerProvided()==Integer.parseInt(ConvertedQuestionData.get(mPageNumber).getQUESTIONCORRECTOPTION())){
                    option5.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_border));
                }else{
                    option5.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_wrong_border));
                }
                option5.setChecked(true);
                option5.setSelected(true);
                break;
            case 0:
                break;
        }

        switch (Integer.parseInt(ConvertedQuestionData.get(mPageNumber).getQUESTIONCORRECTOPTION())){
            case 1:
                option1.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_border));
                option1.setChecked(true);
                option1.setSelected(true);
                break;
            case 2:
                option2.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_border));
                option2.setChecked(true);
                option2.setSelected(true);
                break;
            case 3:
                option3.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_border));
                option3.setChecked(true);
                option3.setSelected(true);
                break;
            case 4:
                option4.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_border));
                option4.setChecked(true);
                option4.setSelected(true);
                break;
            case 5:
                option5.setBackground(ContextCompat.getDrawable(v.getContext(),R.drawable.dash_border));
                option5.setChecked(true);
                option5.setSelected(true);
                break;
            case 0:
                break;
        }


        /*radioGroup = v.findViewById(R.id.radioGroup);
        radioGroup.setEnabled(false);*/

        return v;
    }


}
