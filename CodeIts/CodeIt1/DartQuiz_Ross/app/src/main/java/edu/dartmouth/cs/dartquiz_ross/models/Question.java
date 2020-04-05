package edu.dartmouth.cs.dartquiz_ross.models;

public class Question {

    private int mQuestionResID;
    private boolean mAnswerIsTrue;
    private boolean mCheatQuestion;

    public Question(int mQuestionResID, boolean mAnswerIsTrue, boolean mCheatQuestion) {
        this.mQuestionResID = mQuestionResID;
        this.mAnswerIsTrue = mAnswerIsTrue;
    }

    public int getmQuestionResID() {
        return mQuestionResID;
    }

    public void setmQuestionResID(int mQuestionResID) {
        this.mQuestionResID = mQuestionResID;
    }

    public boolean getmAnswerIsTrue() {
        return mAnswerIsTrue;
    }

    public void setmAnswerIsTrue(boolean mAnswerIsTrue) {
        this.mAnswerIsTrue = mAnswerIsTrue;
    }

    public boolean getmCheatQuestion() {
        return mCheatQuestion;
    }

    public void setmCheatQuestion(boolean mCheatQuestion) {
        this.mCheatQuestion = mCheatQuestion;
    }
}
