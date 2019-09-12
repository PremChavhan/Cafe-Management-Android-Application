package com.android.eshoppingmart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import static java.lang.Double.parseDouble;

public class activity_second extends AppCompatActivity {


    public static final int PICK_IMAGE_REQUEST=1;
    public Uri Imageuri;
    public String imgurl;
    public DatabaseReference mDatabase;
    StorageReference mStorageRef;
    private static final int IMAGE_PICK_CODE = 1000;
    public EditText mTitleEditText;
    public EditText mBodyEditText;
    public EditText mPriceEditText ;
    Button mAddChooseButton;
    Button mAddUploadButton;
    Button mAddPostButton;
    Button mExitButton;
    public ProgressBar progressbar;
    ImageView mImageview;
    Post post;
    LinearLayout container1,container3;
    RelativeLayout container2;
    EditText ImageName;
    ProgressDialog progressDialog;
    //String TempImageName = imageUrl;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_layout);
        ImageName = findViewById(R.id.editTextImgName);
        mTitleEditText = findViewById(R.id.editTextTitle);
        mBodyEditText = findViewById(R.id.editTextBody);
        mPriceEditText = findViewById(R.id.editTextPrice);
        mAddChooseButton = findViewById(R.id.buttonchooseimage);
        mAddUploadButton = findViewById(R.id.buttonuploadimage);
        mAddPostButton = findViewById(R.id.buttonAddPost);
        mExitButton = findViewById(R.id.buttonExit);
        //progressbar=findViewById(R.id.progressBar);
        mImageview=findViewById(R.id.imgView);
        container1=findViewById(R.id.container1);
        //container2=findViewById(R.id.container2);
        container3=findViewById(R.id.container3);
        //progressbar.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(activity_second.this);

        //container1.setVisibility(View.VISIBLE);
        //container2.setVisibility(View.VISIBLE);
        //ontainer3.setVisibility(View.VISIBLE);


        mStorageRef=FirebaseStorage.getInstance().getReference("Uploads");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");



                mAddChooseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openfilechooser();


                    }


                });
                mAddUploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        UploadImageFileToFirebaseStorage();

                        // Calling method to upload selected image on Firebase storage.
                        //UploadImageFileToFirebaseStorage();


                    }
                });


                mAddPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //statechange2();
                        progressDialog.setTitle("Adding Post...");
                        progressDialog.show();
                        final String title = mTitleEditText.getText().toString();
                        final String body = mBodyEditText.getText().toString();
                        final String amount = mPriceEditText.getText().toString();
                        double price=parseDouble(amount);

                        //final String imaurl="https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80";
                        final String imgname=ImageName.getText().toString().trim();
                        post = new Post(title, body, price,imgurl,imgname);

                        mDatabase.push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                 @Override
                                                                                 public void onSuccess(Void aVoid) {
                                                                                     progressDialog.dismiss();
                                                                                     mTitleEditText.setText("");
                                                                                     mBodyEditText.setText("");
                                                                                     mPriceEditText.setText("");
                                                                                     ImageName.setText("");
                                                                                     mImageview.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
                                                                                     Intent intent = new Intent(activity_second.this,activity_navbar.class);
                                                                                     startActivity(intent);
                                                                                     finish();


                                                                                 }
                                                                             }
                        );

                    }
                });

                mExitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(activity_second.this,activity_navbar.class));
                    }
                });

                //Finally, Show the dialog


                //protected void onActivityResult(int requestCode, int resultcode) {
                //}







            }

    private void openfilechooser()
    {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!= null && data.getData() !=null)
        {
            Imageuri=data.getData();
            Picasso.get().load(Imageuri).into(mImageview);
            mImageview.setImageURI(Imageuri);
        }
    }


    private void UploadImageFileToFirebaseStorage () {
        // Setting progressDialog Title.
        progressDialog.setMessage("Uploading...");
        // Showing progressDialog.
        progressDialog.show();

        if (Imageuri != null) {






            // Checking whether FilePathUri Is empty or not.

            // Creating second StorageReference.
            String uniqueId = UUID.randomUUID().toString();
            final StorageReference storageReference2nd = mStorageRef.child ("Uploads/" + uniqueId);

            // Adding addOnSuccessListener to second StorageReference.
            UploadTask uploadTask=storageReference2nd.putFile (Imageuri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    //imgurl= storageReference2nd.getDownloadUrl().toString();
                    return storageReference2nd.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        System.out.println("Upload " + downloadUri);
                        progressDialog.dismiss();
                        Toast.makeText(activity_second.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                        if (downloadUri != null) {

                            imgurl = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            System.out.println("Upload " + imgurl);

                        }

                    } else {
                        // Handle failures
                        // ...

                        Toast.makeText(activity_second.this, "Failed", Toast.LENGTH_LONG).show();
                    }
                }
                   /* addOnSuccessListener (new OnSuccessListener<UploadTask.TaskSnapshot> ( ) {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Getting image name from EditText and store into string variable.
                    String TempImageName = ImageName.getText ( ).toString ( ).trim ( );

                    if (!TempImageName.trim ( ).isEmpty ( )) {
                        imgurl = TempImageName;
                    }
                    // Hiding the progressDialog after done uploading.


                    imgurl = storageReference2nd.getDownloadUrl().toString();
                    // Showing toast message after done uploading.
                    Toast.makeText (getApplicationContext ( ), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show ( );

                    @SuppressWarnings("VisibleForTests")

                    // Getting image upload ID.
                            String ImageUploadId = mDatabase.push ( ).getKey ( );

                    // Adding image upload id s child element into databaseReference.

                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.


                            // Showing exception erro message.
                            Toast.makeText(activity_second.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.


                        }
                    }*/;
        }); /*else {

            Toast.makeText(activity_second.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }*/
        progressDialog.dismiss();
    }



    /*private void UploadFile(){
        if (Imageuri != null) {
            final StorageReference filereference=mStorageRef.child(System.currentTimeMillis()+"."+GetFileExtension(Imageuri));
            filereference.putFile(Imageuri).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imgurl=filereference.getDownloadUrl().toString();
                                }
                            });
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressb.setProgress(0);

                                }
                            },5000);
                            Toast.makeText(activity_second.this,"Upload Successful",Toast.LENGTH_LONG).show();
                           // imgurl=Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity_second.this,e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressb.setProgress((int)progress);
                        }
                    });
        }
        else{
            Toast.makeText(this,"No File Selected",Toast.LENGTH_SHORT).show();
        }

    }*/



    /*private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }*/


}}

