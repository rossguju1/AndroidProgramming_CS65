package edu.dartmouth.cs.dartnets.testthis;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

/**
 *
 * Individual tests are defined as any method beginning with 'test'.
 *
 * ActivityInstrumentationTestCase2 allows these tests to run alongside a running
 * copy of the application under inspection. Calling getActivity() will return a
 * handle to this activity (launching it if needed).
 *
 * Instrumented test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    @Rule
    public ActivityTestRule<ProfileActivity> mActivityRule = new ActivityTestRule(ProfileActivity.class);

    /**
     * Test to make sure the image is persisted after screen rotation.
     *
     * Launches the main activity, sets a test bitmap, rotates the screen.
     * Checks to make sure that the bitmap value matches what we set it to.
     */
    @Test
    public void testImagePersistedAfterRotate() throws InterruptedException {
        ProfileActivity activity = mActivityRule.getActivity();
        // Define a test bitmap
        final Bitmap TEST_BITMAP = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.blue_pushpin);

        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TEST_BITMAP.compress(Bitmap.CompressFormat.PNG, 100, bos);
        final byte[] TEST_BITMAP_VALUE = bos.toByteArray();

        final ImageView mImageView = activity.findViewById(R.id.imageProfile);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Attempts to manipulate the UI must be performed on a UI thread.
                // Calling this outside runOnUiThread() will cause an exception.
                //
                // You could also use @UiThreadTest, but activity lifecycle methods
                // cannot be called if this annotation is used.
                //set the test bitmap to the image view
                mImageView.setImageBitmap(TEST_BITMAP);
            }
        });

        // Suspends the current thread for 1 second. This is no necessary.
        // But you can see the change on your phone.
        Thread.sleep(2000);

        // Information about a particular kind of Intent that is being monitored.
        // It is required to open your phone screen, otherwise the test will be hanging.
        Instrumentation.ActivityMonitor monitor =
                new Instrumentation.ActivityMonitor(ProfileActivity.class.getName(), null, false);
        getInstrumentation().addMonitor(monitor);
        // Rotate the screen
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getInstrumentation().waitForIdleSync();
        // Updates current activity
        ProfileActivity activity_updated = (ProfileActivity) getInstrumentation().waitForMonitor(monitor);

        // Suspends the current thread for 1 second. This is no necessary.
        // But you can see the change on your phone.
        Thread.sleep(2000);

        final ImageView mImageView2 = activity_updated.findViewById(R.id.imageProfile);
        // Get the current bitmap from image view
        Bitmap currentBitMap = ((BitmapDrawable) mImageView2.getDrawable()).getBitmap();

        // Convert bitmap to byte array
        bos = new ByteArrayOutputStream();
        currentBitMap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] currentBitmapValue = bos.toByteArray();

        // Check if these two bitmaps have the same byte values.
        // If the program executes correctly, they should be the same
        assertArrayEquals(TEST_BITMAP_VALUE, currentBitmapValue);
    }

    /**
     * Test to make sure that value of name is persisted across activity restarts.
     *
     * Launches the main activity, sets a name value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the name value match what we
     * set it to.
     */
    @Test
    public void testNameValuePersistedBetweenLaunches() throws InterruptedException {

        Thread.sleep(2000);

        ProfileActivity activity = mActivityRule.getActivity();


        final String TEST_NAME = "Ross";


        final EditText mEditName = activity.findViewById(R.id.editName);
        final Button mSaveButton = (Button) activity.findViewById(R.id.btnSave);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                mEditName.requestFocus();
                mEditName.setText(TEST_NAME);
                mSaveButton.performClick();


            }
        });


        activity.finish();

        Thread.sleep(2000);

        mActivityRule.launchActivity(null);  // Required to force creation of a new activity

        activity = mActivityRule.getActivity();

        final EditText mEditName_2 = (EditText) activity.findViewById(R.id.editName);
        String currentName = mEditName_2.getText().toString();
        assertEquals(TEST_NAME, currentName);


        Thread.sleep(2000);


    }

    /**
     * Test to make sure that value of email is persisted across activity restarts.
     *
     * Launches the main activity, sets a email value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the email value match what we
     * set it to.
     */
    @Test
    public void testEmailValuePersistedBetweenLaunches() throws InterruptedException {

        Thread.sleep(2000);

        ProfileActivity activity = mActivityRule.getActivity();


        final String TEST_EMAIL = "ross.r.guju.TH@dartmouth.edu";


        final EditText mEditEmail = activity.findViewById(R.id.editEmail);
        final Button mSaveButton = (Button) activity.findViewById(R.id.btnSave);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                mEditEmail.requestFocus();
                mEditEmail.setText(TEST_EMAIL);
                mSaveButton.performClick();

            }
        });


        activity.finish();

        Thread.sleep(2000);
        mActivityRule.launchActivity(null);  // Required to force creation of a new activity

        activity = mActivityRule.getActivity();

        final EditText mEditEmail_2 = (EditText) activity.findViewById(R.id.editEmail);
        String currentEmail = mEditEmail_2.getText().toString();
        assertEquals(TEST_EMAIL, currentEmail);


        Thread.sleep(2000);




    }

    /**
     * Test to make sure that value of phone is persisted across activity restarts.
     *
     * Launches the main activity, sets a phone value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the phone value match what we
     * set it to.
     */
    @Test
    public void testPhoneValuePersistedBetweenLaunches() throws InterruptedException {
        // implement your test based on the function header

        Thread.sleep(2000);

        ProfileActivity activity = mActivityRule.getActivity();


        final String TEST_PHONE = "7279891234";


        final EditText mEditPhone = activity.findViewById(R.id.editPhone);
        final Button mSaveButton = (Button) activity.findViewById(R.id.btnSave);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                mEditPhone.requestFocus();
                mEditPhone.setText(TEST_PHONE);
                mSaveButton.performClick();

            }
        });


        activity.finish();

        Thread.sleep(2000);

        mActivityRule.launchActivity(null);  // Required to force creation of a new activity

        activity = mActivityRule.getActivity();

        final EditText mEditPhone_2 = (EditText) activity.findViewById(R.id.editPhone);
        String currentPhone = mEditPhone_2.getText().toString();
        assertEquals(TEST_PHONE, currentPhone);

        Thread.sleep(2000);

    }

    /**
     * Test to make sure that value of gender is persisted across activity restarts.
     *
     * Launches the main activity, sets a gender value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the gender value match what we
     * set it to.
     */
    @Test
    public void testGenderValuePersistedBetweenLaunches() throws InterruptedException {
        // implement your test based on the function header

        Thread.sleep(2000);
        ProfileActivity activity = mActivityRule.getActivity();

        // 0 female
        // 1 male
        final int TEST_GENDER = 1;

        final RadioGroup mRadioGroup  = (RadioGroup) activity.findViewById(R.id.radioGender);
        final Button mSaveButton = (Button) activity.findViewById(R.id.btnSave);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRadioGroup.requestFocus();

                RadioButton radioBtn = (RadioButton) mRadioGroup.getChildAt(TEST_GENDER);
                radioBtn.setChecked(true);

                mSaveButton.performClick();

            }
        });


        activity.finish();
        Thread.sleep(2000);

        mActivityRule.launchActivity(null);  // Required to force creation of a new activity

        activity = mActivityRule.getActivity();

        RadioGroup radioGroup = activity.findViewById(R.id.radioGender);

        int current_gender  = radioGroup.indexOfChild(activity.findViewById(radioGroup
                .getCheckedRadioButtonId()));

        assertEquals(TEST_GENDER, current_gender);


        Thread.sleep(2000);

    }

    /**
     * Test to make sure that value of class is persisted across activity restarts.
     *
     * Launches the main activity, sets a class value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the class value match what we
     * set it to.
     */
    @Test
    public void testClassValuePersistedBetweenLaunches() throws InterruptedException {
        // implement your test based on the function header
        Thread.sleep(2000);

        ProfileActivity activity = mActivityRule.getActivity();


        final String TEST_CLASS = "2020";


        final EditText mEditCLass = activity.findViewById(R.id.editClass);
        final Button mSaveButton = (Button) activity.findViewById(R.id.btnSave);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                mEditCLass.requestFocus();
                mEditCLass.setText(TEST_CLASS);
                mSaveButton.performClick();

            }
        });


        activity.finish();

        Thread.sleep(2000);
        mActivityRule.launchActivity(null);  // Required to force creation of a new activity

        activity = mActivityRule.getActivity();

        final EditText mEditClass_2 = (EditText) activity.findViewById(R.id.editClass);
        String currentClass = mEditClass_2.getText().toString();
        assertEquals(TEST_CLASS, currentClass);

        Thread.sleep(2000);

    }

    /**
     * Test to make sure that value of major is persisted across activity restarts.
     *
     * Launches the main activity, sets a major value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the major value match what we
     * set it to.
     */
    @Test
    public void testMajorValuePersistedBetweenLaunches() throws InterruptedException {
        // implement your test based on the function header

        Thread.sleep(2000);

        ProfileActivity activity = mActivityRule.getActivity();


        final String TEST_MAJOR = "Computer Engineering";


        final EditText mEditMajor = activity.findViewById(R.id.editMajor);
        final Button mSaveButton = (Button) activity.findViewById(R.id.btnSave);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                mEditMajor.requestFocus();
                mEditMajor.setText(TEST_MAJOR);
                mSaveButton.performClick();

            }
        });

        Thread.sleep(2000);

        activity.finish();

        mActivityRule.launchActivity(null);  // Required to force creation of a new activity

        activity = mActivityRule.getActivity();

        final EditText mEditMajor_2 = (EditText) activity.findViewById(R.id.editMajor);
        String currentMajor = mEditMajor_2.getText().toString();
        assertEquals(TEST_MAJOR, currentMajor);


        Thread.sleep(2000);

    }

    /**
     * Test to make sure that image is persisted across activity restarts.
     *
     * Launches the main activity, sets an image, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the image matches what we
     * set it to.
     */
    @Test
    public void testImagePersistedBetweenLaunches() throws InterruptedException {

        Thread.sleep(2000);

        ProfileActivity activity = mActivityRule.getActivity();

        final Bitmap TEST_IMAGE = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.blue_pushpin);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TEST_IMAGE.compress(Bitmap.CompressFormat.PNG, 100, bos);
        final byte[] TEST_IMAGE_VALUE = bos.toByteArray();

        final ImageView mImageView = activity.findViewById(R.id.imageProfile);
        final Button mSaveButton = (Button) activity.findViewById(R.id.btnSave);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mImageView.setImageBitmap(TEST_IMAGE);
                mSaveButton.performClick();
            }
        });

        Thread.sleep(2000);


        final ImageView mImage2 = activity.findViewById(R.id.imageProfile);
        Bitmap currentBitMap = ((BitmapDrawable) mImage2.getDrawable()).getBitmap();

        bos = new ByteArrayOutputStream();
        currentBitMap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] currentImageValue = bos.toByteArray();

        assertArrayEquals(TEST_IMAGE_VALUE, currentImageValue);


        Thread.sleep(2000);



    }


}
