package edu.dartmouth.cs.dartquiz_ross;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.dartmouth.cs.dartquiz_ross.models.Question;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "lifecycle";
    private static final String KEY_CURRENT_INDEX = "Index";
    public static final String EXTRA_ANSWER = "answer";

    private static final int REQUEST_CHEATED = 01;
    //private static final String KEY_CURRENT_CHEAT = "Cheated";
    private static final String KEY_CHEAT_BANK = "CheatBank";

    private Button mFalseButton;
    private TextView mQuestionText;
    private Button mTrueButton;
    private Button mCheatButton;
    private Button mNextButton;
    private Button mPreviousButton;
    private int mCurrentIndex = 0;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_soccer, true, false),
            new Question(R.string.question_ivy, false, false),
            new Question(R.string.question_hope, true, false),
            new Question(R.string.question_joke, true, false),
    };
    //private boolean mUserCheated = false;

    private int[] mUserCheatBank = new int[] {0, 0, 0, 0};

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_INDEX, mCurrentIndex);
        outState.putIntArray(KEY_CHEAT_BANK, mUserCheatBank);
        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");

        if (savedInstanceState != null) {
            mUserCheatBank = savedInstanceState.getIntArray(KEY_CHEAT_BANK);
            for (int i = 0; i < mQuestionBank.length; i++) {
                if (mUserCheatBank[i] == 0) {
                    mQuestionBank[i].setmCheatQuestion(false);

                } else {
                    mQuestionBank[i].setmCheatQuestion(true);
                }
            }
            mCurrentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX);
        }



        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);

        mQuestionText = findViewById(R.id.question_text);
        mQuestionText.setText(mQuestionBank[mCurrentIndex].getmQuestionResID());

        mNextButton = findViewById(R.id.next_button);
        mPreviousButton = findViewById(R.id.previous_button);

        mCheatButton = findViewById(R.id.cheat_button);

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mQuestionText.setText(mQuestionBank[mCurrentIndex].getmQuestionResID());
            }

        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex == 0) {
                    mCurrentIndex = mQuestionBank.length - 1;
                } else {
                    mCurrentIndex = mCurrentIndex - 1;
                }
                mQuestionText.setText(mQuestionBank[mCurrentIndex].getmQuestionResID());
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
                intent.putExtra(EXTRA_ANSWER, mQuestionBank[mCurrentIndex].getmAnswerIsTrue());
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CHEATED);

            }

        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHEATED && resultCode == RESULT_OK && data != null){
            mQuestionBank[mCurrentIndex].setmCheatQuestion(data.getBooleanExtra(CheatActivity.EXTRA_USER_CHEATED, false));
            mUserCheatBank[mCurrentIndex] = 1;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart()");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void checkAnswer(boolean answer){

        if(mQuestionBank[mCurrentIndex].getmCheatQuestion()){
            Toast.makeText(QuizActivity.this, "Cheating Is Wrong", Toast.LENGTH_SHORT).show();

        } else{


        if (mQuestionBank[mCurrentIndex].getmAnswerIsTrue() == answer){
            Toast.makeText(QuizActivity.this, "Correct", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(QuizActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
            }
        }


    }


}

