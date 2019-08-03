package com.sweenutt.hobbyistaa.Activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sweenutt.hobbyistaa.Adapters.ImageUploadInfo;
import com.sweenutt.hobbyistaa.Fragments.HomeFragment;
import com.sweenutt.hobbyistaa.R;

public class PostArtikelActivity extends AppCompatActivity {
    EditText judulpost, artikelpost;
    ImageView gambarpost;
    Button postBtn;

    //folder path ke firebase
    String StoragePath = "all_image_artikel/";
    //root nama database untuk firebase database
    String DatabasePath = "Postingan";
    // membuat URI
    Uri FilePathUri;
    // membuat StorageReference dan Database Reference
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    //proses dialog
    ProgressDialog mProgressDialog;
    // image request code untuk memilih gambar
    int IMAGE_REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_artikel);

        judulpost = findViewById(R.id.judulpost);
        artikelpost = findViewById(R.id.artikelpost);
        gambarpost = findViewById(R.id.gambarpost);
        postBtn = findViewById(R.id.btnPost);

        //klik untuk memilih gambar
        gambarpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //membuat intent
                Intent intent = new Intent();
                //setting tipe intent sebagai gambar untuk memilih gambar di hp
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Pilih Gambar"), IMAGE_REQUEST_CODE);
            }
        });

        //tombol upload ke firebase

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // memanggil method upload data
                uploadDataToFirebase();

            }
        });

        // assign FirebaseStorage instance to storage reference object
        mStorageReference = FirebaseStorage.getInstance().getReference();
        // assign FirebaseDatabase instance with root database name
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(DatabasePath);

        //progress dialog

        mProgressDialog = new ProgressDialog(PostArtikelActivity.this);

    }

    private void uploadDataToFirebase() {
        // cek filepathuri kosong atau tidak
        if(FilePathUri != null) {
            // setting progress bar tilte
            mProgressDialog.setTitle("Gambar sedang di unggah...");
            // show progress dialog
            mProgressDialog.show();
            // membuat kedua storage reference
            StorageReference storageReference2nd = mStorageReference.child(StoragePath + System.currentTimeMillis()+"."+getFileExtension(FilePathUri));
            // menambah addOnsuccess ke storagereference
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // get title
                            String JudulPosting = judulpost.getText().toString().trim();
                            // get artikel
                            String ArtikelPosting = artikelpost.getText().toString().trim();
                            //hide progress dialog
                            mProgressDialog.dismiss();
                            //show toast
                            Toast.makeText(PostArtikelActivity.this, "Gambar telah di unggah...", Toast.LENGTH_SHORT).show();
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(JudulPosting, ArtikelPosting, taskSnapshot.getMetadata().toString(), JudulPosting.toLowerCase());
                            // mendapat image upload id
                            String imageUploadId = mDatabaseReference.push().getKey();
                            //adding image upload id
                            mDatabaseReference.child(imageUploadId).setValue(imageUploadInfo);

                            Intent intent = new Intent(PostArtikelActivity.this, MainActivity.class);
                            startActivity(intent);


                        }
                    })
                    //ketika ada sesuatu yang salah sebagai jaringan yang error
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //hide progress dialog
                            mProgressDialog.dismiss();
                            //show error toast
                            Toast.makeText(PostArtikelActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.setTitle("Gambar sedang di unggah...");
                        }
                    });
        }
        else {
            Toast.makeText(this, "Mohon pilih gambar atau tambah nama gambar.", Toast.LENGTH_SHORT).show();
        }
    }

    //method untuk mendapat gambar pilihan file dari file path uri
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // returning the file extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){
            FilePathUri = data.getData();

            try {
                // mendapat gambar ke bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), FilePathUri);
                // setting bitmap ke image view
                gambarpost.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
