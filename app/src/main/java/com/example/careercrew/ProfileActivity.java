package com.example.careercrew;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MAX_IMAGE_SIZE = 1024 * 1024; // 1MB

    private ImageView profilePic;
    private EditText name, age, gender, email, phone, about, education, DOB;

    private TextView nameText, ageText, genderText, emailText, phoneText, aboutText, educationText, DOBText;

    private Uri profilePicUri;

    private DatabaseReference userDatabaseReference;
    private DatabaseReference profileDatabaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = findViewById(R.id.profilePic);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        about = findViewById(R.id.about);
        education = findViewById(R.id.education);
        DOB = findViewById(R.id.DOB);

        nameText = findViewById(R.id.nameText);
        ageText = findViewById(R.id.ageText);
        genderText = findViewById(R.id.genderText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        aboutText = findViewById(R.id.aboutText);
        educationText = findViewById(R.id.educationText);
        DOBText = findViewById(R.id.DOBText);

        // Initialize Firebase references
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        profileDatabaseReference = FirebaseDatabase.getInstance().getReference("Profile");
        storageReference = FirebaseStorage.getInstance().getReference("profilePics");

        checkAndLoadProfileData(); // Check and load profile data
    }

    public void selectProfilePic(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profilePicUri = data.getData();
            profilePic.setImageURI(profilePicUri);
        }
    }

    public void submitProfile(View view) {
        String nameText = name.getText().toString().trim();
        String ageText = age.getText().toString().trim();
        String genderText = gender.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        String aboutText = about.getText().toString().trim();
        String educationText = education.getText().toString().trim();
        String DOBtext = DOB.getText().toString().trim();

        if (phoneText.isEmpty() || aboutText.isEmpty() || educationText.isEmpty()) {
            Toast.makeText(this, "Please fill in all mandatory fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (profilePicUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profilePicUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                if (data.length > MAX_IMAGE_SIZE) {
                    Toast.makeText(this, "Profile picture size should not exceed 1MB", Toast.LENGTH_SHORT).show();
                    return;
                }

                StorageReference profilePicRef = storageReference.child(System.currentTimeMillis() + ".jpg");
                UploadTask uploadTask = profilePicRef.putBytes(data);
                uploadTask.addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String profilePicUrl = uri.toString();
                    saveProfile(nameText, ageText, genderText, emailText, phoneText, aboutText, educationText, DOBtext, profilePicUrl);
                })).addOnFailureListener(e -> Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show());

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
            }
        } else {
            saveProfile(nameText, ageText, genderText, emailText, phoneText, aboutText, educationText, DOBtext, null);
        }
    }

    private void saveProfile(String name, String age, String gender, String email, String phone, String about, String education, String DOB, String profilePicUrl) {
        String userEmail = getUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User email is null, cannot save profile", Toast.LENGTH_SHORT).show();
            return;
        }

        Profile profile = new Profile(name, age, gender, email, phone, about, education, DOB, profilePicUrl);
        String sanitizedEmail = userEmail.replace(".", ",");

        profileDatabaseReference.child(sanitizedEmail).setValue(profile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                // Call the method to switch EditText to TextView
                switchEditTextToTextView();
            } else {
                Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchEditTextToTextView() {
        // Log entry into the method
        Log.d("ProfileActivity", "Entered switchEditTextToTextView");

        try {
            // Set the text for TextViews from EditTexts
            nameText.setText(name.getText().toString().trim());
            ageText.setText(age.getText().toString().trim());
            genderText.setText(gender.getText().toString().trim());
            emailText.setText(email.getText().toString().trim());
            phoneText.setText(phone.getText().toString().trim());
            aboutText.setText(about.getText().toString().trim());
            educationText.setText(education.getText().toString().trim());
            DOBText.setText(DOB.getText().toString().trim());

            // Set visibility of EditTexts to GONE
            name.setVisibility(View.GONE);
            age.setVisibility(View.GONE);
            gender.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            about.setVisibility(View.GONE);
            education.setVisibility(View.GONE);
            DOB.setVisibility(View.GONE);

            // Set visibility of TextViews to VISIBLE
            nameText.setVisibility(View.VISIBLE);
            ageText.setVisibility(View.VISIBLE);
            genderText.setVisibility(View.VISIBLE);
            emailText.setVisibility(View.VISIBLE);
            phoneText.setVisibility(View.VISIBLE);
            aboutText.setVisibility(View.VISIBLE);
            educationText.setVisibility(View.VISIBLE);
            DOBText.setVisibility(View.VISIBLE);

            // Log success
            Log.d("ProfileActivity", "Successfully switched EditTexts to TextViews");
        } catch (Exception e) {
            // Log any exceptions
            Log.e("ProfileActivity", "Error switching EditTexts to TextViews", e);
        }
    }

    private void checkAndLoadProfileData() {
        String userEmail = getUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User email is null, cannot load profile", Toast.LENGTH_SHORT).show();
            return;
        }

        String sanitizedEmail = userEmail.replace(".", ",");

        profileDatabaseReference.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);
                if (profile != null) {
                    nameText.setText(profile.name);
                    ageText.setText(profile.age);
                    genderText.setText(profile.gender);
                    emailText.setText(profile.email);
                    phoneText.setText(profile.phone);
                    aboutText.setText(profile.about);
                    educationText.setText(profile.education);
                    DOBText.setText(profile.DOB);

                    // Set visibility of EditTexts to GONE
                    name.setVisibility(View.GONE);
                    age.setVisibility(View.GONE);
                    gender.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);
                    phone.setVisibility(View.GONE);
                    about.setVisibility(View.GONE);
                    education.setVisibility(View.GONE);
                    DOB.setVisibility(View.GONE);

                    // Set visibility of TextViews to VISIBLE
                    nameText.setVisibility(View.VISIBLE);
                    ageText.setVisibility(View.VISIBLE);
                    genderText.setVisibility(View.VISIBLE);
                    emailText.setVisibility(View.VISIBLE);
                    phoneText.setVisibility(View.VISIBLE);
                    aboutText.setVisibility(View.VISIBLE);
                    educationText.setVisibility(View.VISIBLE);
                    DOBText.setVisibility(View.VISIBLE);

                    // Disable editing of these fields if you want to
                    nameText.setEnabled(false);
                    ageText.setEnabled(false);
                    genderText.setEnabled(false);
                } else {
                    loadData(); // Load user data from users node if profile doesn't exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        String userEmail = getUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User email is null, cannot load profile", Toast.LENGTH_SHORT).show();
            return;
        }

        userDatabaseReference.child(userEmail.replace(".", ",")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);
                if (profile != null) {
                    name.setText(profile.name);
                    age.setText(profile.age);
                    gender.setText(profile.gender);
                    email.setText(profile.email);
                    phone.setText(profile.phone);
                    about.setText(profile.about);
                    education.setText(profile.education);
                    DOB.setText(profile.DOB);

                    // Disable editing of these fields if you want to
                    name.setEnabled(false);
                    age.setEnabled(false);
                    gender.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getEmail() : null;
    }

    private static class Profile {
        public String name, age, gender, email, phone, about, education, DOB, profilePicUrl;

        public Profile() {
        }

        public Profile(String name, String age, String gender, String email, String phone, String about, String education, String DOB, String profilePicUrl) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.email = email;
            this.phone = phone;
            this.about = about;
            this.education = education;
            this.DOB = DOB;
            this.profilePicUrl = profilePicUrl;
        }
    }
}
