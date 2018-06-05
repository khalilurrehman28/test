package com.application.onlineTestSeries.PYQPTest.Test.QuestionFragment;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.PYQPTest.Test.MainTest;
import com.application.onlineTestSeries.PYQPTest.Test.Models.PYQP_Test_Data;
import com.application.onlineTestSeries.PYQPTest.Test.SingleDataHolder.ServerTestGetter;
import com.application.onlineTestSeries.PracticeTest.Models.QuestionReportResponse;
import com.application.onlineTestSeries.PracticeTest.Models.QuestionUpdateResponse;
import com.application.onlineTestSeries.PracticeTest.Models.SendingDataToDB;
import com.application.onlineTestSeries.PracticeTest.Test.Helper.GlideImageGetter;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.login.Models.UserData;

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

public class questionTestFragment extends Fragment {
    private static List<PYQP_Test_Data> ConvertedQuestionData;
    /**
     * The argument key for the page number this fragment represents.
     */
    TextView question,questionlabel;
    RadioButton option1, option2, option3, option4,option5;
    RadioGroup radioGroup;
    ImageView markQuestion;
    Realm realmDb;
    int userID;
    TextView reset,reportQuestion;
    private int mPageNumber;
    View mView;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static questionTestFragment create(int pageNumber) {
        questionTestFragment fragment = new questionTestFragment();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question, container, false);
        mView = v;
        ConvertedQuestionData = new ArrayList<>(ServerTestGetter.getInstance().getConvertedQuestionData());
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
        reset = v.findViewById(R.id.reset);
        reportQuestion = v.findViewById(R.id.reportQuestion);

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
            option5.setText(Html.fromHtml(ConvertedQuestionData.get(mPageNumber).getQUESTIONOPTION5(), new GlideImageGetter(getContext(), option4), null));
        }else{
            option5.setVisibility(View.INVISIBLE);
        }
        questionlabel.setText("Q"+ConvertedQuestionData.get(mPageNumber).getCounter()+": ");

        reportQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Hello", Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(v.getContext());
                View view = inflater.inflate(R.layout.report_question, null);
                builder.setTitle("Report Question");
                builder.setCancelable(false);
                builder.setMessage("Do you want to Report this question?");

                final EditText etComments = view.findViewById(R.id.etComments);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new reportQuestionToDatabaseFrag(etComments.getText().toString()).execute();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setView(view);
                builder.show();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Reset Question")
                        .setMessage("Are you sure you want to reset this Question?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                radioGroup.clearCheck();
                                ConvertedQuestionData.get(mPageNumber).setAnswerProvided(0);
                                ConvertedQuestionData.get(mPageNumber).setAttempted(false);
                                ConvertedQuestionData.get(mPageNumber).setProcessStart(answerSkip);

                                ((MainTest) getActivity()).updateTestNumber();

                                SendingDataToDB pd = new SendingDataToDB();
                                pd.setAns(0);
                                pd.setUserID(userID);
                                pd.setQuestionID(Integer.parseInt(ConvertedQuestionData.get(mPageNumber).getQUESTIONID()));
                                pd.setTestID(Integer.parseInt(ConvertedQuestionData.get(mPageNumber).getTestID()));
                                new saveAnswerToDatabaseFrag(pd).execute();
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

        switch (ConvertedQuestionData.get(mPageNumber).getAnswerProvided()){
            case 1:
                option1.setChecked(true);
                option1.setSelected(true);

                break;
            case 2:
                option2.setChecked(true);
                option2.setSelected(true);

                break;
            case 3:
                option3.setChecked(true);
                option3.setSelected(true);

                break;
            case 4:
                option4.setChecked(true);
                option4.setSelected(true);

                break;
            case 5:
                option5.setChecked(true);
                option5.setSelected(true);

                break;
            case 0:
                break;
        }

        radioGroup = v.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int answerProvided;
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.option1:
                        answerProvided = 1;
                        break;
                    case R.id.option2:
                        answerProvided = 2;
                        break;
                    case R.id.option3:
                        answerProvided = 3;
                        break;
                    case R.id.option4:
                        answerProvided = 4;
                        break;
                    case R.id.option5:
                        answerProvided = 5;
                        break;
                    default:
                        answerProvided = 0;
                        break;
                }
                ConvertedQuestionData.get(mPageNumber).setAnswerProvided(answerProvided);
                ConvertedQuestionData.get(mPageNumber).setAttempted(true);
                if (ConvertedQuestionData.get(mPageNumber).isMarked()) {
                    ConvertedQuestionData.get(mPageNumber).setProcessStart(questionIsMarked);
                } else {
                    ConvertedQuestionData.get(mPageNumber).setProcessStart(answerGiven);
                }

                SendingDataToDB pd = new SendingDataToDB();
                pd.setAns(answerProvided);
                pd.setUserID(userID);
                pd.setQuestionID(Integer.parseInt(ConvertedQuestionData.get(mPageNumber).getQUESTIONID()));
                pd.setTestID(Integer.parseInt(ConvertedQuestionData.get(mPageNumber).getTestID()));
                new saveAnswerToDatabaseFrag(pd).execute();

                ((MainTest) getActivity()).updateTestNumber();
            }
        });
        return v;
    }



    public class saveAnswerToDatabaseFrag extends AsyncTask<String,Void,Void> {

        SendingDataToDB sdtb;

        public saveAnswerToDatabaseFrag(SendingDataToDB pd) {
            sdtb = pd;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... voids) {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<QuestionUpdateResponse> userCall = service.getAddQuestion(sdtb.getUserID(),String.valueOf(sdtb.getAns()),String.valueOf(sdtb.getQuestionID()),sdtb.getTestID());
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

    public class reportQuestionToDatabaseFrag extends AsyncTask<String,Void,Void> {

        String userText;
        public reportQuestionToDatabaseFrag(String userText) {
            this.userText = userText;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... voids) {
            ApiService service = ApiClient.INSTANCE.getClient().create(ApiService.class);
            Call<QuestionReportResponse> userCall = service.add_remarks(userID,ConvertedQuestionData.get(mPageNumber).getQUESTIONID(),userText);
            userCall.enqueue(new Callback<QuestionReportResponse>() {
                @Override
                public void onResponse(Call<QuestionReportResponse> call, Response<QuestionReportResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body()!=null){
                            //Log.d("onFailure", response.body().getMessage());
                            Toasty.info(getContext(), response.body().getMsg(), Toast.LENGTH_LONG, true).show();
                        }else{
                            Log.d("onFailure", "response null");
                        }
                    }else{
                        Log.d("onFailure", "hit failure");
                    }
                }

                @Override
                public void onFailure(Call<QuestionReportResponse> call, Throwable t) {
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
}
