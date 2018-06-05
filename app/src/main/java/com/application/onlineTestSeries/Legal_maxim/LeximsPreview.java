package com.application.onlineTestSeries.Legal_maxim;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.Legal_maxim.Models.LeximsData;
import com.application.onlineTestSeries.Notes.Models.AddToNotes;
import com.application.onlineTestSeries.NotesDescription.notesDescription;
import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.SectionActivity.Models.HighlightedText;
import com.application.onlineTestSeries.SectionActivity.WebHandler.MyTagHandler;
import com.application.onlineTestSeries.bookmarks.model.AddToBookmarks;
import com.mancj.slideup.SlideUp;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class LeximsPreview extends AppCompatActivity implements View.OnClickListener{

    TextView SCLJJudgement;

    SeekBar seekBar;
    Animation slideUpAnimation, slideDownAnimation;
    private SlideUp slideUp;
    private View dim;
    private View sliderView;
    SpannableString changedTestHolder;
    LinearLayout mainLinearLayout, addNotes, highlighttext, bookmark;
    private static final int AddToNotesActivtiCallback = 101;
    Realm realmDB;
    String lMID,caseNumber,caseDate;
    TextView descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexims_preview);
        /*toolbar = findViewById(R.id.toolbar);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//to hide keyboad
        //toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorWhite));

        SCLJJudgement = findViewById(R.id.SCLJJudgement);

        sliderView = findViewById(R.id.slideView);
        dim = findViewById(R.id.dim);
        //ButterKnife.bind(this);
        slideUp = new SlideUp(sliderView);
        slideUp.hideImmediately();
        slideUp.setSlideListener(new SlideUp.SlideListener() {
            @Override
            public void onSlideDown(float v) {
                dim.setAlpha(1 - (v / 100));
            }

            @Override
            public void onVisibilityChanged(int i) {
                if (i == View.GONE) {
                    //mMap.mar
                }
            }
        });
        slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        seekBar = findViewById(R.id.seekBar1);

        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        mainLinearLayout = findViewById(R.id.mainLinearLayout);
        addNotes = findViewById(R.id.addNotes);
        highlighttext = findViewById(R.id.highlighttext);
        //changeTextSize = findViewById(R.id.changeTextSize);
        bookmark = findViewById(R.id.bookmark);
        addNotes.setOnClickListener(this);
        highlighttext.setOnClickListener(this);
        //changeTextSize.setOnClickListener(this);
        bookmark.setOnClickListener(this);
        descriptionText = findViewById(R.id.descriptionText);

        lMID = getIntent().getStringExtra("LMID");

        RealmResults<LeximsData> judgementData = realmDB.where(LeximsData.class).equalTo("lMID",lMID).findAll();
        if (judgementData.size()>0){
            for (LeximsData item : judgementData){
                //UpdateClickUi(item.getStartIndex(),item.getEndIndex());
                setTitle(item.getLMTITLE());
                caseNumber = item.getLMTITLE();
                caseDate =item.getLMDESC();
                if (!item.getLMDESC().equals("")){
                    Spanned origanlHtmlTextWithTags;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        origanlHtmlTextWithTags = Html.fromHtml(item.getLMDESC(), Html.FROM_HTML_MODE_COMPACT, null, new MyTagHandler());
                    } else {
                        origanlHtmlTextWithTags = Html.fromHtml(item.getLMDESC(), null, new MyTagHandler());
                    }
                    SCLJJudgement.setText(origanlHtmlTextWithTags);
                    updateUi(item.getLMID(),item.getLMDESC());
                }else {
                    SCLJJudgement.setText("");
                }
            }
        }

        RealmResults<AddToNotes> notesResult = realmDB.where(AddToNotes.class).equalTo("type",2).equalTo("chapterID", lMID).findAll();
        for (AddToNotes item : notesResult) {
            UpdateClickUi(item.getStartIndex(), item.getEndIndex());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNotes:
                addToNotes();
                break;
            case R.id.highlighttext:
                highLightText();
                break;
           /* case R.id.changeTextSize:
                updateTextSize();
                break;*/
            case R.id.bookmark:
                addToBookmark1();
                break;
        }
    }

    private void addToBookmark1() {
        int key = 1;
        RealmResults<AddToBookmarks> results = realmDB.where(AddToBookmarks.class).findAll();
        if (results.size() > 0) {
            key = results.size() + 1;
        }
        RealmResults<AddToBookmarks> resultBookmark = realmDB.where(AddToBookmarks.class).equalTo("chapterID", lMID).equalTo("type", 2).findAll();
        if (resultBookmark.size() > 0) {
            showError("Already bookmarked");
        }else{
            realmDB.beginTransaction();
            AddToBookmarks data = realmDB.createObject(AddToBookmarks.class, key);
            data.setType(2);
            data.setChapterID(lMID);
            data.setChapterName(caseDate);
            data.setCourseName(caseNumber);
            realmDB.commitTransaction();

            RealmResults<AddToBookmarks> resultsdata = realmDB.where(AddToBookmarks.class).equalTo("chapterID", lMID).equalTo("type", 2).findAll();
            if (resultsdata.size() > 0) {
                showSuccess("Judgement Bookmarked Successfully");
            }
        }
    }

/*
    private void updateTextSize() {
        if (isSeekOpen) {
            seekBar.startAnimation(slideDownAnimation);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    seekBar.setVisibility(View.GONE);
                }
            }, 900);
            //seekBar.setVisibility(View.GONE);
            isSeekOpen = false;
        } else {
            seekBar.startAnimation(slideUpAnimation);
            seekBar.setVisibility(View.VISIBLE);
            isSeekOpen = true;
        }
    }
*/

    private void highLightText() {
        int min = 0;
        int max = SCLJJudgement.getText().length();
        int selectionStart = 0;
        int selectionEnd = 0;
        if (SCLJJudgement.isFocused()) {
            selectionStart = SCLJJudgement.getSelectionStart();
            selectionEnd = SCLJJudgement.getSelectionEnd();
            min = Math.max(0, Math.min(selectionStart, selectionEnd));
            max = Math.max(0, Math.max(selectionStart, selectionEnd));
        }

        CharSequence selectedWord = SCLJJudgement.getText().subSequence(min, max);
        if (selectedWord.toString().trim().length() < 2) {
            showWarning("Please select some words");
        } else {
            SpannableString WordSpan = new SpannableString(SCLJJudgement.getText());
            WordSpan.setSpan(new ForegroundColorSpan(Color.RED), min, max, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SCLJJudgement.setText(WordSpan);
            changedTestHolder = WordSpan;
            int key = 1;
            RealmResults<HighlightedText> results = realmDB.where(HighlightedText.class).equalTo("chapterID",lMID).equalTo("type",2).findAll();
            if (results.size() > 0) {
                key = results.size() + 1;
            }

            realmDB.beginTransaction();
            HighlightedText data = realmDB.createObject(HighlightedText.class, key);
            data.setChapterID(lMID);
            data.setStartIndex(min);
            data.setEndIndex(max);
            data.setType(2);
            realmDB.commitTransaction();
        }
    }

    private void addToNotes() {
        //showSuccess("Notes");
        int min = 0;
        int max = SCLJJudgement.getText().length();
        int selectionStart = 0;
        int selectionEnd = 0;
        if (SCLJJudgement.isFocused()) {
            selectionStart = SCLJJudgement.getSelectionStart();
            selectionEnd = SCLJJudgement.getSelectionEnd();
            min = Math.max(0, Math.min(selectionStart, selectionEnd));
            max = Math.max(0, Math.max(selectionStart, selectionEnd));
        }
        CharSequence selectedWord = SCLJJudgement.getText().subSequence(min, max);
        if (selectedWord.toString().trim().length() < 2) {
            showWarning("Please select some words");
        } else if (selectedWord.toString().trim().length() > 120) {
            showWarning("Please select less words");
        } else {
            Intent i = new Intent(LeximsPreview.this, notesDescription.class);
            i.putExtra("chapterID", lMID);
            i.putExtra("min", min);
            i.putExtra("max", max);
            i.putExtra("word", selectedWord.toString());
            i.putExtra("chapterName",caseDate);
            i.putExtra("courseName", caseNumber);
            i.putExtra("type", 2);
            startActivityForResult(i, AddToNotesActivtiCallback);
        }
    }

    private void showError(String s) {
        Toasty.error(getApplicationContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showWarning(String s) {
        Toasty.warning(getApplicationContext(), s, Toast.LENGTH_LONG, true).show();
    }
    private void showSuccess(String message) {
        Toasty.success(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AddToNotesActivtiCallback:
                if (resultCode == RESULT_OK) {
                    UpdateClickUi(data.getIntExtra("min", 0), data.getIntExtra("max", 0));
                    showSuccess("Note added successfully");
                }
                break;
        }
    }

    private void UpdateClickUi(final int min, final int max) {
        SpannableString ss = new SpannableString(SCLJJudgement.getText());
        final int colorForThisClickableSpan = Color.BLUE;
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                RealmResults<AddToNotes> notesResult = realmDB.where(AddToNotes.class).equalTo("type", 2).equalTo("chapterID", lMID).equalTo("startIndex", min).equalTo("endIndex", max).findAll();
                for (AddToNotes item : notesResult) {
                    descriptionText.setText(item.getDescription());
                }
                slideUp.animateIn();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(colorForThisClickableSpan);
            }
        };
        //WordSpan.setSpan(new ForegroundColorSpan(Color.RED),min,max, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, min, max, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        SCLJJudgement.setMovementMethod(LinkMovementMethod.getInstance());
        //SCLJJudgement.setHighlightColor(Color.BLUE);
        SCLJJudgement.setText(ss);
        //changedTestHolder = ss;
    }

    private void updateUi(String chapterid, String chaptersectiondesc) {
        RealmResults<HighlightedText> results = realmDB.where(HighlightedText.class).equalTo("chapterID", chapterid).equalTo("type",2).findAll();
        if (results.size() > 0) {
            Spanned origanlHtmlTextWithTags;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                origanlHtmlTextWithTags = Html.fromHtml(chaptersectiondesc, Html.FROM_HTML_MODE_COMPACT, null, new MyTagHandler());
            } else {
                origanlHtmlTextWithTags = Html.fromHtml(chaptersectiondesc, null, new MyTagHandler());
            }
            SpannableString WordSpan = new SpannableString(origanlHtmlTextWithTags);
            for (HighlightedText item : results) {
                int min = Math.max(0, Math.min(item.getStartIndex(), item.getEndIndex()));
                int max = Math.max(0, Math.max(item.getStartIndex(), item.getEndIndex()));

                WordSpan.setSpan(new ForegroundColorSpan(Color.RED), min, max, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SCLJJudgement.setText(WordSpan);
                // changedTestHolder = WordSpan;
            }
        } else {
            Spanned origanlHtmlTextWithTags;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                origanlHtmlTextWithTags = Html.fromHtml(chaptersectiondesc, Html.FROM_HTML_MODE_COMPACT, null, new MyTagHandler());
            } else {
                origanlHtmlTextWithTags = Html.fromHtml(chaptersectiondesc, null, new MyTagHandler());
            }
            SCLJJudgement.setText(origanlHtmlTextWithTags);
            //changedTestHolder = SpannableString.valueOf(SCLJJudgement.getText());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (slideUp.isVisible()) {
            slideUp.animateOut();
        } else {
            super.onBackPressed();
        }
    }
}
