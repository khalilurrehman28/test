package com.application.onlineTestSeries.SectionActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.onlineTestSeries.R;
import com.mancj.slideup.SlideUp;

import java.text.Normalizer;

import com.application.onlineTestSeries.Chapters.Models.ChapterData;
import com.application.onlineTestSeries.NotesDescription.notesDescription;
import com.application.onlineTestSeries.Notes.Models.AddToNotes;
import com.application.onlineTestSeries.SectionActivity.Models.HighlightedText;
import com.application.onlineTestSeries.SectionActivity.WebHandler.MyTagHandler;

import com.application.onlineTestSeries.Utils.checkInternetState;
import com.application.onlineTestSeries.bookmarks.model.AddToBookmarks;
import com.application.onlineTestSeries.menuBareActs.menuBareActs;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class sectionPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int AddToNotesActivtiCallback = 101;
    Realm realmDB;
    Boolean isNightModeEnabled = false;
    LinearLayout btnSearch,mainLinearLayout, addNotes, highlighttext, searchDic, shareText, changeTextSize, bookmark;
    TextView sectionDataView, descriptionText;
    ImageView changeTheme;
    String chapterID;
    String chapterName, courseName;
    SeekBar seekBar;
    EditText searchText;
    String serverOriginaltext;
    SpannableString changedTestHolder;


    Animation slideUpAnimation, slideDownAnimation;
    private SlideUp slideUp;
    private View dim;
    private View sliderView;
    private Boolean isSeekOpen = false;
    private Boolean isSearchBarOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_preview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//to hide keyboad

        courseName = getIntent().getStringExtra("courseName");
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
        descriptionText = findViewById(R.id.descriptionText);
        sectionDataView = findViewById(R.id.sectionDataView);
        changeTheme = findViewById(R.id.changeTheme);
        btnSearch= findViewById(R.id.btnSearch);
        mainLinearLayout = findViewById(R.id.mainLinearLayout);
        addNotes = findViewById(R.id.addNotes);
        highlighttext = findViewById(R.id.highlighttext);
        searchDic = findViewById(R.id.searchDic);
        shareText = findViewById(R.id.shareText);
        changeTextSize = findViewById(R.id.changeTextSize);
        searchText = findViewById(R.id.searchText);
        bookmark = findViewById(R.id.bookmark);
        changeTheme.setOnClickListener(this);
        addNotes.setOnClickListener(this);
        highlighttext.setOnClickListener(this);
        searchDic.setOnClickListener(this);
        shareText.setOnClickListener(this);
        changeTextSize.setOnClickListener(this);
        bookmark.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.startAnimation(slideDownAnimation);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                seekBar.setVisibility(View.GONE);
                            }
                        }, 1200);
                        //seekBar.setVisibility(View.GONE);
                        isSeekOpen = false;
                    }
                }, 2000);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               /* p=progress;
                sectionDataView.setTextSize(p);*/
                sectionDataView.setTextSize((float) (progress) + 15);

            }
        });

        changeTheme();
        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        //showSuccess(getIntent().getStringExtra("chapterID"));
        RealmResults<ChapterData> results = realmDB.where(ChapterData.class).equalTo("cHAPTERID", getIntent().getStringExtra("chapterID")).findAll();
        for (ChapterData item : results) {
            serverOriginaltext = item.getCHAPTERSECTIONDESC();
            updateUi(item.getCHAPTERID(), item.getCHAPTERSECTIONDESC());
            chapterID = item.getCHAPTERID();
            chapterName = item.getCHAPTERTITLE();

        }
        if (!chapterName.equals("")) {
            setTitle(chapterName);
        } else {
            setTitle("");
        }
        searchText.addTextChangedListener(mTextEditorWatcher);
        RealmResults<AddToNotes> notesResult = realmDB.where(AddToNotes.class).equalTo("type",0).equalTo("chapterID", getIntent().getStringExtra("chapterID")).findAll();
        for (AddToNotes item : notesResult) {
            UpdateClickUi(item.getStartIndex(), item.getEndIndex());
        }

        /*searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Toast.makeText(sectionPreviewActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                    //return true;
                }
                return false;
            }
        });*/

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_section_preview, menu);
        return true;
    }

    private void showSuccess(String message) {
        Toasty.success(getApplicationContext(), message, Toast.LENGTH_LONG, true).show();
    }

    private void updateUi(String chapterid, String chaptersectiondesc) {
        RealmResults<HighlightedText> results = realmDB.where(HighlightedText.class).equalTo("chapterID", chapterid).equalTo("type",0).findAll();
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
                sectionDataView.setText(WordSpan);
                changedTestHolder = WordSpan;
            }
        } else {
            Spanned origanlHtmlTextWithTags;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                origanlHtmlTextWithTags = Html.fromHtml(chaptersectiondesc, Html.FROM_HTML_MODE_COMPACT, null, new MyTagHandler());
            } else {
                origanlHtmlTextWithTags = Html.fromHtml(chaptersectiondesc, null, new MyTagHandler());
            }
            sectionDataView.setText(origanlHtmlTextWithTags);
            changedTestHolder = SpannableString.valueOf(sectionDataView.getText());
        }
    }

    public void changeTheme() {
        //.showSuccess("i am clicked "+isNightModeEnabled);
        if (isNightModeEnabled) {
            changeTheme.setImageResource(R.drawable.ic_change_background_1);
            mainLinearLayout.setBackgroundColor(getResources().getColor(R.color.colorDark));
            sectionDataView.setTextColor(getResources().getColor(R.color.colorWhite));
            isNightModeEnabled = false;
        } else {
            changeTheme.setImageResource(R.drawable.ic_change_background);
            mainLinearLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            sectionDataView.setTextColor(getResources().getColor(R.color.colorBlack));
            isNightModeEnabled = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                viewSearchBar();
                break;
            case R.id.changeTheme:
                changeTheme();
                break;
            case R.id.addNotes:
                addToNotes();
                break;
            case R.id.highlighttext:
                highLightText();
                break;
            case R.id.changeTextSize:
                updateTextSize();
                break;
            case R.id.shareText:
                shareText();
                break;
            case R.id.bookmark:
                addToBookmark();
                break;
            case R.id.searchDic:
                searchDicTionary();
                break;
        }
    }

    private void viewSearchBar() {

        if (isSearchBarOpen) {
            searchText.startAnimation(slideDownAnimation);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    searchText.setVisibility(View.GONE);


                }
            }, 1000);
            //seekBar.setVisibility(View.GONE);
            isSearchBarOpen = false;

        }else{
                searchText.startAnimation(slideUpAnimation);
                searchText.setVisibility(View.VISIBLE);
                isSearchBarOpen = true;
        }
    }


    private void searchDicTionary() {
        if (!checkInternetState.Companion.getInstance(sectionPreviewActivity.this).isOnline()) {
            showWarning("No Internet Connection");
        } else {
            int min = 0;
            int max = sectionDataView.getText().length();
            int selectionStart = 0;
            int selectionEnd = 0;
            if (sectionDataView.isFocused()) {
                selectionStart = sectionDataView.getSelectionStart();
                selectionEnd = sectionDataView.getSelectionEnd();
                min = Math.max(0, Math.min(selectionStart, selectionEnd));
                max = Math.max(0, Math.max(selectionStart, selectionEnd));
            }
            CharSequence selectedWord = sectionDataView.getText().subSequence(min, max);
            if (selectedWord.toString().trim().length() < 2) {
                showWarning("Please select some words");
            } else {
                Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                search.putExtra(SearchManager.QUERY, selectedWord.toString());
                startActivity(search);
            }
        }
    }

    private void shareText() {
        if (!checkInternetState.Companion.getInstance(sectionPreviewActivity.this).isOnline()) {
            showWarning("No Internet Connection");
        } else {
            int min = 0;
            int max = sectionDataView.getText().length();
            int selectionStart = 0;
            int selectionEnd = 0;
            if (sectionDataView.isFocused()) {
                selectionStart = sectionDataView.getSelectionStart();
                selectionEnd = sectionDataView.getSelectionEnd();
                min = Math.max(0, Math.min(selectionStart, selectionEnd));
                max = Math.max(0, Math.max(selectionStart, selectionEnd));
            }
            CharSequence selectedWord = sectionDataView.getText().subSequence(min, max);
            if (selectedWord.toString().trim().length() < 2) {
                showWarning("Please select some words");
            } else {

               /* Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                intent.putExtra(Intent.EXTRA_SUBJECT, courseName + " (" + chapterName + ")");
                intent.putExtra(Intent.EXTRA_TEXT, selectedWord.toString());
                startActivity(Intent.createChooser(intent, "Send Email"));*/
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, courseName + " (" + chapterName + ")");
                sendIntent.putExtra(Intent.EXTRA_TEXT, selectedWord.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }

        }
    }


    private void addToBookmark() {
        int key = 1;
        RealmResults<AddToBookmarks> results = realmDB.where(AddToBookmarks.class).findAll();
        if (results.size() > 0) {
            key = results.size() + 1;
        }
        RealmResults<AddToBookmarks> resultBookmark = realmDB.where(AddToBookmarks.class).equalTo("chapterID", chapterID).equalTo("type", 0).findAll();
        if (resultBookmark.size() > 0) {
            showError("Already bookmarked");
        }else{
            realmDB.beginTransaction();
            AddToBookmarks data = realmDB.createObject(AddToBookmarks.class, key);
            data.setChapterID(chapterID);
            data.setChapterName(chapterName);
            data.setCourseName(courseName);
            data.setType(0);
            realmDB.commitTransaction();

            RealmResults<AddToBookmarks> resultsdata = realmDB.where(AddToBookmarks.class).equalTo("chapterID", chapterID).equalTo("type", 0).findAll();
            //SELECT * FROM Tablename where colname = value
            if (resultsdata.size() > 0) {
                showSuccess("Chapter Bookmarked Successfully");
            }
        }
    }

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

    private void highLightText() {
        int min = 0;
        int max = sectionDataView.getText().length();
        int selectionStart = 0;
        int selectionEnd = 0;
        if (sectionDataView.isFocused()) {
            selectionStart = sectionDataView.getSelectionStart();
            selectionEnd = sectionDataView.getSelectionEnd();
            min = Math.max(0, Math.min(selectionStart, selectionEnd));
            max = Math.max(0, Math.max(selectionStart, selectionEnd));
        }

        CharSequence selectedWord = sectionDataView.getText().subSequence(min, max);
        if (selectedWord.toString().trim().length() < 2) {
            showWarning("Please select some words");
        } else {
            SpannableString WordSpan = new SpannableString(sectionDataView.getText());
            WordSpan.setSpan(new ForegroundColorSpan(Color.RED), min, max, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sectionDataView.setText(WordSpan);
            changedTestHolder = WordSpan;
            int key = 1;
            RealmResults<HighlightedText> results = realmDB.where(HighlightedText.class).findAll();
            if (results.size() > 0) {
                key = results.size() + 1;
            }

            realmDB.beginTransaction();
            HighlightedText data = realmDB.createObject(HighlightedText.class, key);
            data.setChapterID(chapterID);
            data.setStartIndex(min);
            data.setEndIndex(max);
            data.setType(0);
            realmDB.commitTransaction();
        }
    }

    private void addToNotes() {
        //showSuccess("Notes");
        int min = 0;
        int max = sectionDataView.getText().length();
        int selectionStart = 0;
        int selectionEnd = 0;
        if (sectionDataView.isFocused()) {
            selectionStart = sectionDataView.getSelectionStart();
            selectionEnd = sectionDataView.getSelectionEnd();
            min = Math.max(0, Math.min(selectionStart, selectionEnd));
            max = Math.max(0, Math.max(selectionStart, selectionEnd));
        }
        CharSequence selectedWord = sectionDataView.getText().subSequence(min, max);
        if (selectedWord.toString().trim().length() < 2) {
            showWarning("Please select some words");
        } else if (selectedWord.toString().trim().length() > 120) {
            showWarning("Please select less words");
        } else {
            Intent i = new Intent(sectionPreviewActivity.this, notesDescription.class);
            i.putExtra("chapterID", chapterID);
            i.putExtra("min", min);
            i.putExtra("max", max);
            i.putExtra("word", selectedWord.toString());
            i.putExtra("chapterName", chapterName);
            i.putExtra("courseName", courseName);
            i.putExtra("type", 0);
            startActivityForResult(i, AddToNotesActivtiCallback);
        }
    }

    private void showError(String s) {
        Toasty.error(getApplicationContext(), s, Toast.LENGTH_LONG, true).show();
    }

    private void showWarning(String s) {
        Toasty.warning(getApplicationContext(), s, Toast.LENGTH_LONG, true).show();
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
        SpannableString ss = new SpannableString(sectionDataView.getText());
        final int colorForThisClickableSpan = Color.BLUE;
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                RealmResults<AddToNotes> notesResult = realmDB.where(AddToNotes.class).equalTo("chapterID", chapterID).equalTo("startIndex", min).equalTo("endIndex", max).findAll();
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

        sectionDataView.setMovementMethod(LinkMovementMethod.getInstance());
        //sectionDataView.setHighlightColor(Color.BLUE);
        sectionDataView.setText(ss);
        changedTestHolder = ss;
    }

    @Override
    public void onBackPressed() {
        if (slideUp.isVisible()) {
            slideUp.animateOut();
        } else {
            super.onBackPressed();
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

        if (id == R.id.action_bare_acts) {

            startActivity(new Intent(sectionPreviewActivity.this,menuBareActs.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        public void afterTextChanged(Editable s) {
            sectionDataView.setText(highlight(searchText.getText().toString(),changedTestHolder));
        }

    };

    public static CharSequence highlight(String search, SpannableString originalText) {
        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        int start = normalizedText.indexOf(search);
        if (start <= 0) {
            return originalText;
        } else {
            Spannable highlighted = new SpannableString(originalText);
            while (start > 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(), originalText.length());
                highlighted.setSpan(new BackgroundColorSpan(Color.GREEN), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = normalizedText.indexOf(search, spanEnd);
            }
            return highlighted;
        }
    }
}
