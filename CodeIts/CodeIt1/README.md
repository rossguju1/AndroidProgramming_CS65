# CodeIt 1  DartQuiz App


This app is the DartQuiz App

The given DartQuiz had the 3 edge case bugs and also the following bugs


# Edge Cases

## Edge1: Rotating the CheatActivity View

I fixed this by adding a variable ``boolean mCheated`` inconjunction with ``onSaveInstanceState()`` method to save if the user clicked on *Show Answer* button.  Also a member variable was added to the model to hold if the user cheated on a given question.

## Edge 2: Rotating the QuizActivity View
## and
## Edge 3: Clicking the Next Button until the answer comes around again 
I solved both Edge cases 2 and 3 by creating an array  ``int[] mUserCheatBank`` which contains 1 (True) or 0 (False). This array is used to maintain the state of each question.


# Extra Bugs found in DartQuiz app (the given apk)

I found and resolved the following bugs.

## Bug 1
When clicking the “Cheat!” Button and rotating the screen while in CheatActivity, it automatically displays the answer.

## Bug 2
If you cheat and then move to another question and then rotate it forgets if the user cheated or not.

