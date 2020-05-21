package edu.dartmouth.cs.myorganizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class AddfileFragmentFragment extends DialogFragment {

    // Dialog error
    public static final int DIALOG_ID_ERROR = -1;

    // Key for dialog camera option
    public static final int LOAD_PHOTO_ITEM = 1;


    // Key for dialog option to take a picture
    public static final int TAKE_PHOTO_PHOTO_ITEM= 0;

    public static final int OTHER_FILE_ITEM = 2;



    private static final String DIALOG_ID_KEY = "dialog_id";



    // Creates a new instance of the dialog
    public static AddfileFragmentFragment newInstance(int dialog_id) {
        AddfileFragmentFragment frag = new  AddfileFragmentFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_KEY, dialog_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

        final Activity parent = getActivity();

        // This sets up the dialog for choosing either to take a new picture
        // or selecting one from gallery
        switch (dialog_id) {
            case LOAD_PHOTO_ITEM:
                AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                builder.setTitle(R.string.picture_picker_fragment);
                DialogInterface.OnClickListener dialoglistener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        ((MainActivity) parent).onPhotoPickerItemSelected(item);
                    }
                };
                final CharSequence[] items = {"Take Picture", "Add Photo", "Add other File"};

                builder.setItems(items, dialoglistener);
                return builder.create();

            default:
                return null;
        }
    }
}