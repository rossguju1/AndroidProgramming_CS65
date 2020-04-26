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
    public void testNameValuePersistedBetweenLaunches(){
        // implement your test based on the function header
    }

    /**
     * Test to make sure that value of email is persisted across activity restarts.
     *
     * Launches the main activity, sets a email value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the email value match what we
     * set it to.
     */
    @Test
    public void testEmailValuePersistedBetweenLaunches() {
        // implement your test based on the function header
    }

    /**
     * Test to make sure that value of phone is persisted across activity restarts.
     *
     * Launches the main activity, sets a phone value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the phone value match what we
     * set it to.
     */
    @Test
    public void testPhoneValuePersistedBetweenLaunches() {
        // implement your test based on the function header
    }

    /**
     * Test to make sure that value of gender is persisted across activity restarts.
     *
     * Launches the main activity, sets a gender value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the gender value match what we
     * set it to.
     */
    @Test
    public void testGenderValuePersistedBetweenLaunches() {
        // implement your test based on the function header
    }

    /**
     * Test to make sure that value of class is persisted across activity restarts.
     *
     * Launches the main activity, sets a class value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the class value match what we
     * set it to.
     */
    @Test
    public void testClassValuePersistedBetweenLaunches() {
        // implement your test based on the function header
    }

    /**
     * Test to make sure that value of major is persisted across activity restarts.
     *
     * Launches the main activity, sets a major value, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the major value match what we
     * set it to.
     */
    @Test
    public void testMajorValuePersistedBetweenLaunches() {
        // implement your test based on the function header
    }

    /**
     * Test to make sure that image is persisted across activity restarts.
     *
     * Launches the main activity, sets an image, clicks the save button, closes the activity,
     * then relaunches that activity. Checks to make sure that the image matches what we
     * set it to.
     */
    @Test
    public void testImagePersistedBetweenLaunches() {
        // implement your test based on the function header
    }


}
