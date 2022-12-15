package com.example.quizapp4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.quizapp4.Model.DbQuery;
import com.example.quizapp4.Model.QuestionModel;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ScoreActivity extends AppCompatActivity {

    private TextView scoreTV, timeTV, totalQTV, correctQTV, wrongQTV, unattemptedQTV;
    Button lenderB, reAttemptB, viewAnsB;
    private long timeTaken;
    private int finalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        init();
        loadData();

        viewAnsB.setOnClickListener(view -> {
            Intent intent = new Intent(ScoreActivity.this, AnswerActivity.class);
            startActivity(intent);
        });

        reAttemptB.setOnClickListener(view -> {
            reAttempt();
        });

        saveResult();
    }

    private void init() {
        scoreTV = findViewById(R.id.score);
        timeTV = findViewById(R.id.time);
        totalQTV = findViewById(R.id.totalQ);
        wrongQTV = findViewById(R.id.wrongQ);
        correctQTV = findViewById(R.id.correctQ);
        unattemptedQTV = findViewById(R.id.un_attemptedQ);
        lenderB = findViewById(R.id.lenderB);
        reAttemptB = findViewById(R.id.reattemptB);
        viewAnsB = findViewById(R.id.view_answerB);
    }

    private void loadData() {
        int correctQ = 0, wrongQ = 0, unattemptQ = 0;

        for (int i = 0; i < DbQuery.g_quesList.size(); i++) {
            if (DbQuery.g_quesList.get(i).getSelectedAns() == -1) {
                unattemptQ++;
            } else {
                if (DbQuery.g_quesList.get(i).getSelectedAns() == DbQuery.g_quesList.get(i).getCorrectAnswer()) {
                    correctQ++;
                }
                else {
                    wrongQ++;
                }
            }
        }

        correctQTV.setText(String.valueOf(correctQ));
        wrongQTV.setText(String.valueOf(wrongQ));
        unattemptedQTV.setText(String.valueOf(unattemptQ));

        totalQTV.setText(String.valueOf(DbQuery.g_quesList.size()));

        finalScore = (correctQ * 100) / DbQuery.g_quesList.size();
        scoreTV.setText(String.valueOf(finalScore));

        timeTaken = getIntent().getLongExtra("TIME_TAKEN", 0);
        String time = String.format("%02d:%02d min", TimeUnit.MILLISECONDS.toMinutes(timeTaken), TimeUnit.MILLISECONDS.toSeconds(timeTaken) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTaken)));

        timeTV.setText(time);
    }

    private void reAttempt() {
        for (int i = 0; i < DbQuery.g_quesList.size(); i++) {
            DbQuery.g_quesList.get(i).setSelectedAns(-1);
            DbQuery.g_quesList.get(i).setStatus(DbQuery.NOT_VISITED);
        }

        Intent intent = new Intent(ScoreActivity.this, StartTestActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveResult() {
        DbQuery.saveResult(finalScore, new MyCompleteListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void setBookMarks() {
        for (int i = 0; i < DbQuery.g_quesList.size(); i++) {
            QuestionModel question = DbQuery.g_quesList.get(i);
            if (question.isBookmarked()) {
                if (!DbQuery.g_bmIdList.contains(question.getqID())) {
                    DbQuery.g_bmIdList.add(question.getqID());
                    DbQuery.myProfile.setBookmarksCount(DbQuery.g_bmIdList.size());
                }
            } else {
                if (DbQuery.g_bmIdList.contains(question.getqID())) {
                    DbQuery.g_bmIdList.remove(question.getqID());
                    DbQuery.myProfile.setBookmarksCount(DbQuery.g_bmIdList.size());
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ScoreActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}