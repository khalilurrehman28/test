package com.application.onlineTestSeries.landmarkJudgement.FilterActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.application.onlineTestSeries.R;

import java.util.Calendar;

public class judgementFilter extends AppCompatActivity {

    EditText judgementDate,judgementCase;
    Button searchJudgement;
    int  DDay,DMonth,DYear;
    String newDateSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judgement_filter);


        judgementDate = findViewById(R.id.judgeDate);
        judgementCase = findViewById(R.id.judgeCaseNo);
        searchJudgement = findViewById(R.id.judgeSearch);

        judgementDate.setInputType(InputType.TYPE_NULL);
        judgementDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        String mon = (monthOfYear < 10) ? "0" + monthOfYear : "" + monthOfYear;
                        String day = (dayOfMonth < 10) ? "0" + dayOfMonth : "" + dayOfMonth;
                        //String getDate = year+"/"+mon+"/"+day;
                        String getDate = day + "/" + mon + "/" + year;
                        newDateSearch = year + "-" + mon + "-" + day+" 00:00:00";

                        DDay = dayOfMonth;
                        DMonth = monthOfYear;
                        DYear = year;
                        judgementDate.setText(getDate);
                        judgementDate.setError(null);
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int Date = calendar.get(Calendar.DAY_OF_MONTH);
                int Month = calendar.get(Calendar.MONTH);
                int Year = calendar.get(Calendar.YEAR);
                //Time date = new Time();
                DatePickerDialog d = new DatePickerDialog(judgementFilter.this, dpd, Year, Month, Date);
                d.getDatePicker().setMaxDate(System.currentTimeMillis());
                d.show();
            }});
        searchJudgement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData(judgementDate)){
                    Intent intent = new Intent();
                    intent.putExtra("date", newDateSearch);
                    String userText;
                    if (judgementCase.getText().toString().equals("")){
                        userText = "";
                    }else{
                        userText = judgementCase.getText().toString();
                    }
                    intent.putExtra("caseno", userText);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private boolean validateData(EditText judgementDate) {
        if (judgementDate.getText().toString().equals("")){
            judgementDate.setError("Please choose date");
            return false;
        }

        return true;
    }
}
