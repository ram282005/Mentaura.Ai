package com.example.careercrew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, ageTextView, genderTextView, emailTextView, phonetext, biotext, edutext, dobtext;
    private EditText phoneEditText, bioEditText, educationEditText, dobEditText;
    private ImageView editImageView, editdp;
    private CircleImageView dp;
    private Button submitButton;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference, newdata;
    private String userEmail, name2, age2, gender2, phone2, bio2, edu2, dob2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        nameTextView = findViewById(R.id.name);
        ageTextView = findViewById(R.id.age);
        genderTextView = findViewById(R.id.gender);
        emailTextView = findViewById(R.id.email);
        phonetext = findViewById(R.id.phonenumber);
        biotext = findViewById(R.id.bioinfo);
        edutext = findViewById(R.id.edu);
        dobtext = findViewById(R.id.dateofbirth);

        phoneEditText = findViewById(R.id.phone);
        bioEditText = findViewById(R.id.about);
        educationEditText = findViewById(R.id.education);
        dobEditText = findViewById(R.id.DOB);

        dp = findViewById(R.id.profilePic);
        editdp = findViewById(R.id.editimage);
        editImageView = findViewById(R.id.edit);
        submitButton = findViewById(R.id.submitBtn);

        // Disable editing initially
        disableEditing();

        storageReference = FirebaseStorage.getInstance().getReference("Profile");
        FirebaseApp.initializeApp(this);

        editdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Set onClickListener for edit button
        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditing();
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            if (userEmail != null) {
                String sanitizedEmail = userEmail.replace(".", ",");
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(sanitizedEmail);
                newdata = FirebaseDatabase.getInstance().getReference("Profile").child(sanitizedEmail);

                fetchUserData();

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imageUri != null) {
                            uploadImageAndSubmitData(userEmail);
                        } else {
                            submitUserData(userEmail, null);
                        }
                    }
                });
            }
        } else {
            Toast.makeText(this, "User not found :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserData() {
        newdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name2 = dataSnapshot.child("name").getValue(String.class);
                    age2 = dataSnapshot.child("age").getValue(String.class);
                    gender2 = dataSnapshot.child("gender").getValue(String.class);
                    phone2 = dataSnapshot.child("phone").getValue(String.class);
                    bio2 = dataSnapshot.child("bio").getValue(String.class);
                    edu2 = dataSnapshot.child("education").getValue(String.class);
                    dob2 = dataSnapshot.child("dob").getValue(String.class);

                    nameTextView.setText(name2);
                    ageTextView.setText(age2);
                    genderTextView.setText(gender2);
                    emailTextView.setText(userEmail);
                    phonetext.setText(phone2);
                    biotext.setText(bio2);
                    edutext.setText(edu2);
                    dobtext.setText(dob2);

                    String profileImageUrl = dataSnapshot.child("image").getValue(String.class);
                    if (profileImageUrl != null) {
                        loadProfileImage(profileImageUrl);
                    }

                } else {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                name2 = snapshot.child("name").getValue(String.class);
                                age2 = snapshot.child("age").getValue(String.class);
                                gender2 = snapshot.child("gender").getValue(String.class);

                                nameTextView.setText(name2);
                                ageTextView.setText(age2);
                                genderTextView.setText(gender2);
                                emailTextView.setText(userEmail);

                            } else {
                                Toast.makeText(ProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ProfileActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableEditing() {
        phoneEditText.setVisibility(View.VISIBLE);
        bioEditText.setVisibility(View.VISIBLE);
        educationEditText.setVisibility(View.VISIBLE);
        dobEditText.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);

        phoneEditText.setEnabled(true);
        bioEditText.setEnabled(true);
        educationEditText.setEnabled(true);
        dobEditText.setEnabled(true);

        phonetext.setVisibility(View.GONE);
        biotext.setVisibility(View.GONE);
        edutext.setVisibility(View.GONE);
        dobtext.setVisibility(View.GONE);
    }

    private void disableEditing() {
        phoneEditText.setVisibility(View.GONE);
        bioEditText.setVisibility(View.GONE);
        educationEditText.setVisibility(View.GONE);
        dobEditText.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);

        phonetext.setVisibility(View.VISIBLE);
        biotext.setVisibility(View.VISIBLE);
        edutext.setVisibility(View.VISIBLE);
        dobtext.setVisibility(View.VISIBLE);
    }

    private void submitUserData(String userEmail, String imageUrl) {
        String phone = phoneEditText.getText().toString().trim();
        String bio = bioEditText.getText().toString().trim();
        String education = educationEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();

        if (!phone.isEmpty() && !bio.isEmpty() && !education.isEmpty() && !dob.isEmpty()) {

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("name", name2);
            additionalData.put("age", age2);
            additionalData.put("gender", gender2);
            additionalData.put("email", userEmail);
            additionalData.put("phone", phone);
            additionalData.put("bio", bio);
            additionalData.put("education", education);
            additionalData.put("dob", dob);
            if (imageUrl != null) {
                additionalData.put("image", imageUrl);
            }

            newdata.updateChildren(additionalData);

            Toast.makeText(ProfileActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();

            // Disable editing again
            disableEditing();
        } else {
            Toast.makeText(ProfileActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileImage(String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.baseline_account_circle_24) // optional placeholder image
                .error(R.drawable.baseline_account_circle_24) // optional error image
                .into(dp);
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadImageAndSubmitData(final String userEmail) {
        if (imageUri != null) {
            String sanitizedEmail = userEmail.replace(".", ",");
            final StorageReference fileReference = storageReference.child(sanitizedEmail).child("profile.jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    submitUserData(userEmail, imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                dp.setImageBitmap(bitmap);

                // Upload image to Firebase Storage
                uploadImageAndSubmitData(userEmail);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}