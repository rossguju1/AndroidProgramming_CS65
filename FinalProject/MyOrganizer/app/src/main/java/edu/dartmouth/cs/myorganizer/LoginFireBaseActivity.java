package edu.dartmouth.cs.myorganizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import edu.dartmouth.cs.myorganizer.database.FuegoBaseEntry;
import edu.dartmouth.cs.myorganizer.database.MyPicture;
import edu.dartmouth.cs.myorganizer.database.PictureEntry;
import edu.dartmouth.cs.myorganizer.fragments.PictureGridFragment;

public class LoginFireBaseActivity extends AppCompatActivity {

    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected Button logInButton;
    protected TextView signUpTextView;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    private static final String DEBUG = "LoginFireBaseActivity";

    private SharedPreferences sharedPreferences;

    private AsyncInsert task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_fire_base);

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        signUpTextView = (TextView) findViewById(R.id.signUpText);
        emailEditText = (EditText) findViewById(R.id.emailField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        logInButton = (Button) findViewById(R.id.loginButton);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginFireBaseActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                email = email.trim();
                password = password.trim();

                if (email.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginFireBaseActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    final String finalEmail = email;
                    mFirebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginFireBaseActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(DEBUG, "EMAIL : " + finalEmail);
                                        Globals.FUEGOBASE_EMAIL = finalEmail;
                                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("email", finalEmail);
                                        editor.commit();

                                       // mFirebaseAuth = FirebaseAuth.getInstance();
                                        mFirebaseUser = mFirebaseAuth.getCurrentUser();

                                        String mUserId = mFirebaseUser.getUid();
                                        mDatabase = FirebaseDatabase.getInstance().getReference("user_" + mUserId);
                                        mDatabase.child("picture_entries").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if (snapshot.getValue() == null) {
                                                    Log.d(DEBUG, "database is empty");

                                                    Intent intent = new Intent(LoginFireBaseActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    // The child doesn't exist
                                                } else {
                                                    Log.d(DEBUG, "database is not empty");
                                                    PictureEntry pp = new PictureEntry(getApplicationContext());
                                                    pp.open();
                                                   ArrayList<MyPicture> all_pics = pp.getAllPictures();
                                                    if (all_pics.isEmpty() || all_pics.size()==0) {

                                                        AsyncInsert task = new AsyncInsert(snapshot);

                                                        task.execute();
                                                    } else{
                                                        pp.close();
                                                        Intent intent = new Intent(LoginFireBaseActivity.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    }



                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });



                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginFireBaseActivity.this);
                                        builder.setMessage(task.getException().getMessage())
                                                .setTitle(R.string.login_error_title)
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                }
            }
        });
    }

    class AsyncInsert extends AsyncTask<Void, String, Void> {

        private long id;
        private int mLabel;
        private Uri mImage;
        private String mBase64;
        private String mText;
        private String mDate;
        private int mSynced;
        private PictureEntry pp;

        private DataSnapshot snapshot;

        public AsyncInsert(DataSnapshot snapshot) {
            super();

            this.snapshot = snapshot;


            // do stuff
        }
        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(DEBUG, "AsyncInsert doInBackground()");

            pp = new PictureEntry(getApplicationContext());
            pp.open();

            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                //getting userinfo
                FuegoBaseEntry entry = postSnapshot.getValue(FuegoBaseEntry.class);
                //adding userinfo to the list

                this.id = Long.parseLong(entry.getId());
                this.mImage = Uri.parse(entry.getImageUri());
                this.mBase64 = entry.getImageBase64();
                this.mText = entry.getText();
                this.mLabel = Integer.parseInt(entry.getLabel());
                this.mDate = entry.getDate();
                this.mSynced = Integer.parseInt(entry.getSynced());

                final MyPicture db_entry = new MyPicture();
                db_entry.setmSynced(mSynced);
                db_entry.setId(id);

                db_entry.setmDate(mDate);
                db_entry.setmLabel(mLabel);
                db_entry.setmText(mText);
                File loadPhotoFile = null;
                try {
                    // Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImage);
                    byte[] decodedString = Base64.decode(mBase64, Base64.DEFAULT);
                    Bitmap mBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    loadPhotoFile = createImageFile();

                    FileOutputStream fOut = new FileOutputStream(loadPhotoFile);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (loadPhotoFile == null) {


                    Log.d(DEBUG, "laodPhotoFile == null");
                }
                db_entry.setmImage(Uri.fromFile(loadPhotoFile));



                pp.printPicture(db_entry);

                long new_id = pp.insertEntry(db_entry);
                entry.setId(String.valueOf(new_id));
                postSnapshot.getRef().setValue(entry);



            }

            return null;
        }


        @Override
        protected void onProgressUpdate(String... name) {
            if (!isCancelled()) {


            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(DEBUG, "INSERT THREAD DONE");
            task = null;
            pp.close();

            Intent intent = new Intent(LoginFireBaseActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }

    private File createImageFile() throws IOException {
        //Use a timestamp to create a unique image file name.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        String currentPhotoPath = image.getAbsolutePath();

        return image;
    }

}