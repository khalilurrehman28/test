package com.application.onlineTestSeries.NotesDescription;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.application.onlineTestSeries.Notes.Models.AddToNotes;
import com.application.onlineTestSeries.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class notesDescription extends AppCompatActivity {
    Realm realmDB;
    TextView selectedWord;
    EditText descriptionOfWord;
    Button saveToDb,btnCancel;
    int min,max,type;
    String chapterID,word,chapterName,courseName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_description);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Enter your note");
        selectedWord = findViewById(R.id.selectedWord
        );
        descriptionOfWord = findViewById(R.id.descriptionOfWord);
        btnCancel =findViewById(R.id.btnCancel);
        saveToDb = findViewById(R.id.saveToDb);

        min = getIntent().getIntExtra("min",0);
        max = getIntent().getIntExtra("max",0);
        chapterID = getIntent().getStringExtra("chapterID");
        word = getIntent().getStringExtra("word");
        chapterName = getIntent().getStringExtra("chapterName");
        courseName = getIntent().getStringExtra("courseName");
        type = getIntent().getIntExtra("type",0);
        selectedWord.setText(word);

        Log.d("subseuence",""+min+"----"+max);

        try {
            realmDB = Realm.getDefaultInstance();
        } catch (Exception e) {
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            realmDB = Realm.getInstance(config);
        }

        saveToDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descriptionOfWord.getText().toString().trim().equals("")){
                    descriptionOfWord.setError("Please add some note");
                }else {
                    int key = 1;
                    RealmResults<AddToNotes> results = realmDB.where(AddToNotes.class).findAll();
                    if (results.size()>0){
                        key = results.size() + 1;
                    }

                    realmDB.beginTransaction();
                    AddToNotes data = realmDB.createObject(AddToNotes.class,key);
                    data.setChapterID(chapterID);
                    data.setStartIndex(min);
                    data.setEndIndex(max);
                    data.setDescription(descriptionOfWord.getText().toString());
                    data.setChapterName(chapterName);
                    data.setWord(word);
                    data.setCourseName(courseName);
                    data.setType(type);
                    realmDB.commitTransaction();

                    Intent intent = new Intent();
                    intent.putExtra("min", min);
                    intent.putExtra("max", max);

                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
}
