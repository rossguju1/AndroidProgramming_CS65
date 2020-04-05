package edu.dartmouth.cs.dartquiz_ross;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    public static final String EXTRA_USER_CHEATED = "cheated";
    private static final String KEY_CHEATED = "cheat_state";
    private Button mShowAnswerButton;
    private TextView mShowAnswerText;

    private boolean mCheated = false;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_CHEATED, mCheated);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        if (savedInstanceState != null){
            mCheated = savedInstanceState.getBoolean(KEY_CHEATED);

        }

        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mShowAnswerText = findViewById(R.id.show_answer_text);

        if (mCheated){
            mShowAnswerText.setText(Boolean.toString(getIntent().getBooleanExtra(QuizActivity.EXTRA_ANSWER, false)));
            answerShown();

        }

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowAnswerText.setText(Boolean.toString(getIntent().getBooleanExtra(QuizActivity.EXTRA_ANSWER, false)));
                answerShown();
                mCheated = true;
            }
        });

    }

    private void answerShown() {
        Intent intent = new Intent();
        
        intent.putExtra(EXTRA_USER_CHEATED, true);
        //if clicked set to ok if not set to cancel
        setResult(RESULT_OK, intent);

    }
}
