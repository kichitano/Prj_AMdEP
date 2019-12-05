package com.example.prj_amdep.Presentation.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prj_amdep.Model.UserModel;
import com.example.prj_amdep.Presentation.SOSActivity;
import com.example.prj_amdep.R;
import com.example.prj_amdep.Resources.AESCrypt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.media.MediaRecorder.VideoSource.CAMERA;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private UserModel userModel;
    private String newPassword = "";
    private SOSActivity sosActivity;
    private String m_Text = "";
    private TextView editUserInfo;
    private TextView userFullName;
    private TextView userPhone;
    private TextView userEmail;
    private TextView userNickname;
    private TextView userPassword;
    private ImageButton userPhoto;
    private static final String IMAGE_DIRECTORY = "/temporary";
    private int GALLERY = 1, CAMERA = 2;
    private StorageReference mStorageRef;
    private FirebaseAuth auth;
    private ProgressDialog mProgressDialog;
    private String selectedPic;
    private Uri uriPic;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReferenceProfilePic;
    private DatabaseReference mDatabase;
    private String TAG = "";
    private AESCrypt aesCrypt;
    private NavigationView navigationView;
    private View headerView;
    private ImageView userpic;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sosActivity = (SOSActivity) getActivity();
        userModel = sosActivity.userModel;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //GET COMPONENTS OF VIEW
        editUserInfo = getActivity().findViewById(R.id.btnEditUserInfo);
        userFullName = getActivity().findViewById(R.id.userFullName);
        userPhone = getActivity().findViewById(R.id.userPhone);
        userEmail = getActivity().findViewById(R.id.userEmail);
        userNickname = getActivity().findViewById(R.id.userNickname);
        userPassword = getActivity().findViewById(R.id.userPassword);
        userPhoto = getActivity().findViewById(R.id.userPhoto);

        mProgressDialog = new ProgressDialog(getActivity());
        firebaseStorage = FirebaseStorage.getInstance();
        storageReferenceProfilePic = firebaseStorage.getReference();
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userModel.getUserID());
        aesCrypt = new AESCrypt();

        //SET VALUES OF COMPONENTS
        userFullName.setText(userModel.getUserName() + " " + userModel.getUserLastname());
        userPhone.setText(userModel.getUserPhone());
        userEmail.setText(userModel.getUserEmail());
        userNickname.setText(userModel.getUserNickname());
        userPassword.setText("******");
        try {
            newPassword = aesCrypt.decryptPassword(userModel.getUserPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        getUserPic();

        //SET LISTENER TO EDIT INFO
        editUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editUserInfo.getText().equals("Edit Info")){
                    editUserInfo.setText("Save changes");
                    setFocusableClickable(true);
                }else{
                    editUserInfo.setText("Edit Info");
                    setFocusableClickable(false);
                    saveChanges();
                }
            }
        });
        userPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptForResult("Edit phone number.", "Please insert a new phone number.", new DialogInputInterface(){
                    EditText input;
                    public View onBuildDialog() {
                        input = new EditText(getContext());
                        View outView = input;
                        return outView;
                    }
                    public void onCancel() {
                        m_Text = "";
                    }
                    public void onResult(View v) {
                        m_Text = input.getText().toString();
                        if(!(m_Text.equals(""))){
                            userPhone.setText(m_Text);
                            userPhone.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                    }
                });
            }
        });
        userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptForResult("Edit email.", "Please insert a new email.", new DialogInputInterface(){
                    EditText input;
                    public View onBuildDialog() {
                        input = new EditText(getContext());
                        View outView = input;
                        return outView;
                    }
                    public void onCancel() {
                        m_Text = "";
                    }
                    public void onResult(View v) {
                        m_Text = input.getText().toString();
                        if(!(m_Text.equals(""))){
                            userEmail.setText(m_Text);
                            userEmail.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                    }
                });
            }
        });
        userNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptForResult("Edit nickname.", "Please insert a new nickname.", new DialogInputInterface(){
                    EditText input;
                    public View onBuildDialog() {
                        input = new EditText(getContext());
                        View outView = input;
                        return outView;
                    }
                    public void onCancel() {
                        m_Text = "";
                    }
                    public void onResult(View v) {
                        m_Text = input.getText().toString();
                        if(!(m_Text.equals(""))){
                            userNickname.setText(m_Text);
                            userNickname.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                    }
                });
            }
        });
        userPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptForResult("Edit password.", "Please insert a new password.", new DialogInputInterface(){
                    EditText input;
                    public View onBuildDialog() {
                        input = new EditText(getContext());
                        View outView = input;
                        return outView;
                    }
                    public void onCancel() {
                        m_Text = "";
                    }
                    public void onResult(View v) {
                        m_Text = input.getText().toString();
                        if(!(m_Text.equals(""))){
                            newPassword = m_Text;
                            userPassword.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                    }
                });
            }
        });
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        setFocusableClickable(false);
    }

    private void getUserPic() {
        mProgressDialog.setMessage("Getting user pic...");
        mProgressDialog.show();

        StorageReference picReference = storageReferenceProfilePic.child("images/users/" + userModel.getUserID() + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        picReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                userPhoto.setImageBitmap(bmp);
                mProgressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                mProgressDialog.dismiss();
            }
        });
    }

    public void setFocusableClickable(Boolean conf){
        userPhone.setClickable(conf);
        userPhone.setFocusable(conf);
        userEmail.setClickable(conf);
        userEmail.setFocusable(conf);
        userNickname.setClickable(conf);
        userNickname.setFocusable(conf);
        userPassword.setClickable(conf);
        userPassword.setFocusable(conf);
        userPhoto.setClickable(conf);
        userPhoto.setFocusable(conf);
    }


    private interface DialogInputInterface {
        View onBuildDialog();
        void onCancel();
        void onResult(View v);
    }

    private void promptForResult(String dlgTitle, String dlgMessage, final DialogInputInterface dlg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(dlgTitle);
        alert.setMessage(dlgMessage);
        final View v = dlg.onBuildDialog();
        v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (v != null) { alert.setView(v);}
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dlg.onResult(v);
                dialog.dismiss();
                return;
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dlg.onCancel();
                dialog.dismiss();
                return;
            }
        });
        alert.show();
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uriPic = null;
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    uriPic = getImageUri(getContext(), bitmap);
                    userPhoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            uriPic = getImageUri(getContext(), thumbnail);
            userPhoto.setImageBitmap(thumbnail);
        }
    }

    public void saveChanges(){
        //get the signed in user
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();
        if(uriPic != null){
            mProgressDialog.setMessage("Uploading Image...");
            mProgressDialog.show();
            StorageReference imageRef = storageReferenceProfilePic.child("images/users/" + userID + ".jpg");

            imageRef.putFile(uriPic)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.dismiss();
                            String profilePicUrl = storageReferenceProfilePic.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successful
                            //hiding the progress dialog
                            mProgressDialog.dismiss();
                            //and displaying error message
                            Toast.makeText(getActivity(), exception.getCause().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                          //displaying percentage in progress dialog
                            mProgressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }

        try {
            mProgressDialog.setMessage("Updating data...");
            mProgressDialog.show();
            mDatabase.child("userEmail").setValue(userEmail.getText());
            mDatabase.child("userPhone").setValue(userPhone.getText());
            mDatabase.child("userNickname").setValue(userNickname.getText());
            newPassword = aesCrypt.encryptPassword(newPassword);
            mDatabase.child("userPassword").setValue(newPassword);
            // Get auth credentials from the user for re-authentication
            AuthCredential credential = EmailAuthProvider.getCredential(userModel.getUserEmail(), userModel.getUserPassword()); // Current Login Credentials \\
            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "User re-authenticated.");
                    //Now change your email address \\
                    //----------------Code for Changing Email Address----------\\
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updateEmail(userEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email address updated.");
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User password updated.");
                                            mProgressDialog.dismiss();
                                            navigationView = getActivity().findViewById(R.id.nav_view);
                                            headerView = navigationView.getHeaderView(0);
                                            //SET COMPONENTS TO VARIABLES
                                            userpic = headerView.findViewById(R.id.UserPhoto);
                                            Bitmap bMap = null;
                                            try {
                                                bMap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriPic);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            userpic.setImageBitmap(bMap);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
        catch (Exception ex){
            mProgressDialog.dismiss();
            ex.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, userModel.getUserID(), null);
        return Uri.parse(path);
    }
}
