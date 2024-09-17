package com.example.careercrew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private EditText editTextAge, editTextCareerFocus;
    private TextView hobby;
    private Spinner spinnerGender, spinnerEdu, spinnerBoard, spinnerSubject, spinnerDegree, spinnerBranch;
    private Button buttonRegister;
    private ImageView back, home, boardText, boardImg, subjectText, subjectImg, degreeText, degreeImg, branchText, branchImg;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private String nextActivityClass;  // To hold the next activity class
    private String selectedGender, education, board, subject, selectDegree, branch;  // To hold the selected gender
    private HashMap<String, String[]> degreeSpecializationMap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        back = findViewById(R.id.register_back);
        home = findViewById(R.id.register_home);

        editTextAge = findViewById(R.id.register_age);
        hobby = findViewById(R.id.hobbies);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerEdu = findViewById(R.id.spinnerEducation);
        spinnerBoard = findViewById(R.id.spinnerboard);
        spinnerSubject = findViewById(R.id.spinnersubjcet);
        spinnerDegree = findViewById(R.id.spinnerdegree);
        spinnerBranch = findViewById(R.id.spinnerbranch);

        boardText = findViewById(R.id.board_text);
        subjectText = findViewById(R.id.subject_text);
        degreeText = findViewById(R.id.degree_text);
        branchText = findViewById(R.id.branch_text);
        boardImg = findViewById(R.id.board_img);
        subjectImg = findViewById(R.id.subject_img);
        degreeImg = findViewById(R.id.degree_img);
        branchImg = findViewById(R.id.branch_img);

        editTextCareerFocus = findViewById(R.id.register_hobbies);
        buttonRegister = findViewById(R.id.register_register);

        spinnerBoard.setVisibility(View.GONE);
        spinnerSubject.setVisibility(View.GONE);
        spinnerDegree.setVisibility(View.GONE);
        spinnerBranch.setVisibility(View.GONE);
        boardText.setVisibility(View.GONE);
        subjectText.setVisibility(View.GONE);
        degreeText.setVisibility(View.GONE);
        branchText.setVisibility(View.GONE);
        boardImg.setVisibility(View.GONE);
        subjectImg.setVisibility(View.GONE);
        degreeImg.setVisibility(View.GONE);
        branchImg.setVisibility(View.GONE);

        // Get the next activity class from the intent extras
        nextActivityClass = getIntent().getStringExtra("nextActivityClass");

        degreeSpecializationMap = new HashMap<>();
        degreeSpecializationMap.put("Select your Degree", new String[]{"Select your Degree"});
        degreeSpecializationMap.put("Bachelor of Arts (BA)", new String[]{"Select your Branch", "Art History", "Creative Writing", "Dance", "English", "Gender Studies", "History", "Journalism", "Linguistics", "Literature", "Music", "Philosophy", "Political Science", "Religious Studies", "Sociology", "Theatre Arts"});
        degreeSpecializationMap.put("Bachelor of Science (BSc)", new String[]{"Select your Branch", "Astronomy", "Biology", "Biochemistry", "Bioinformatics", "Chemistry", "Computer Science", "Data Science", "Environmental Science", "Forensic Science", "Geology", "Marine Biology", "Mathematics", "Neuroscience", "Physics", "Physics with Astrophysics"});
        degreeSpecializationMap.put("Bachelor of Business Administration (BBA)", new String[]{"Select your Branch", "Business Analytics", "Entrepreneurship", "Finance", "General Business", "Human Resources", "Investment Banking", "International Business", "Marketing", "Operations Management", "Project Management", "Real Estate", "Retail Management", "Supply Chain Management"});
        degreeSpecializationMap.put("Bachelor of Engineering (BEng)", new String[]{"Select your Branch", "Aerospace Engineering", "Biomedical Engineering", "Chemical Engineering", "Civil Engineering", "Computer Engineering", "Electrical Engineering", "Environmental Engineering", "Industrial Engineering", "Mechanical Engineering", "Materials Science", "Naval Architecture", "Petroleum Engineering", "Software Engineering", "Structural Engineering", "Thermal Engineering"});
        degreeSpecializationMap.put("Bachelor of Fine Arts (BFA)", new String[]{"Select your Branch", "Art Education", "Digital Media", "Fine Arts", "Graphic Design", "Painting", "Photography", "Printmaking", "Sculpture", "Textile Design", "Visual Communication", "3D Modeling"});
        degreeSpecializationMap.put("Bachelor of Laws (LLB)", new String[]{"Select your Branch", "Commercial Law", "Criminal Law", "Environmental Law", "Family Law", "Human Rights Law", "International Law", "Legal Studies", "Public International Law", "Taxation Law"});
        degreeSpecializationMap.put("Bachelor of Medicine, Bachelor of Surgery (MBBS)", new String[]{"Select your Branch", "Clinical Medicine", "General Medicine", "Medical Imaging", "Obstetrics and Gynecology", "Pediatrics", "Preventive Medicine", "Psychiatry", "Surgery"});
        degreeSpecializationMap.put("Bachelor of Social Work (BSW)", new String[]{"Select your Branch", "Child Welfare", "Clinical Social Work", "Community Development", "Gerontology"});
        degreeSpecializationMap.put("Bachelor of Architecture (BArch)", new String[]{"Select your Branch", "Architecture", "Historic Preservation", "Interior Design", "Landscape Architecture", "Urban Design"});
        degreeSpecializationMap.put("Bachelor of Music (BM)", new String[]{"Select your Branch", "Music Composition", "Music Education", "Music Performance", "Music Theory", "Sound Engineering"});
        degreeSpecializationMap.put("Bachelor of Hospitality Management (BHM)", new String[]{"Select your Branch", "Event Planning", "Hotel Management", "Restaurant Management", "Tourism Management"});
        degreeSpecializationMap.put("Bachelor of Physical Education (BPE)", new String[]{"Select your Branch", "Athletic Coaching", "Fitness Training", "Recreation Administration", "Sports Management"});
        degreeSpecializationMap.put("Bachelor of Design (BDes)", new String[]{"Select your Branch", "Fashion Design", "Graphic Design", "Interior Design", "Product Design"});
        degreeSpecializationMap.put("Bachelor of Technology (BTech)", new String[]{"Select your Branch", "Aeronautical Engineering", "Artificial Intelligence", "Automobile Engineering", "Biotechnology", "Chemical Engineering", "Civil Engineering", "Computer Science Engineering", "Electrical Engineering", "Electronics and Communication Engineering", "Engineering Science", "Environmental Engineering", "Information Technology", "Mechanical Engineering", "Metallurgical Engineering", "Mining Engineering", "Petroleum Engineering", "Production Engineering"});
        degreeSpecializationMap.put("Bachelor of Environmental Science (BEnvSc)", new String[]{"Select your Branch", "Conservation Biology", "Environmental Chemistry", "Environmental Policy", "Sustainable Development"});
        degreeSpecializationMap.put("Bachelor of Information Technology (BIT)", new String[]{"Select your Branch", "Cybersecurity", "Database Management", "Network Administration", "Software Development"});
        degreeSpecializationMap.put("Bachelor of Science in Nursing (BSN)", new String[]{"Select your Branch", "Clinical Nursing", "Geriatric Nursing", "Pediatric Nursing", "Psychiatric Nursing"});
        degreeSpecializationMap.put("Bachelor of Science in Agriculture (BSA)", new String[]{"Select your Branch", "Agricultural Economics", "Animal Science", "Crop Science", "Soil Science"});
        degreeSpecializationMap.put("Bachelor of Public Administration (BPA)", new String[]{"Select your Branch", "Nonprofit Management", "Public Finance", "Public Policy", "Urban and Regional Planning"});
        degreeSpecializationMap.put("Bachelor of Science in Tourism (BST)", new String[]{"Select your Branch", "Cultural Heritage Management", "Sustainable Tourism", "Tour Guide Services", "Travel Management"});

        // Set up the Spinner for gender
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, R.layout.spinner_item2);
        genderAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerGender.setAdapter(genderAdapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {  // First item selected
                    selectedGender = null;
                } else {
                    selectedGender = (String) parent.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGender = null;
            }
        });

        // Spinner for Education
        ArrayAdapter<CharSequence> educationAdapter = ArrayAdapter.createFromResource(this,
                R.array.edu_options, R.layout.spinner_item2);
        educationAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerEdu.setAdapter(educationAdapter);
        spinnerEdu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    education = null;
                } else {
                    education = (String) parent.getItemAtPosition(position);
                    handleEducationSelection(education);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                education = null;
            }
        });

        // Spinner for Board
        ArrayAdapter<CharSequence> boardAdapter = ArrayAdapter.createFromResource(this,
                R.array.board_options, R.layout.spinner_item2);
        boardAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerBoard.setAdapter(boardAdapter);
        spinnerBoard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    board = null;
                } else {
                    board = (String) parent.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                board = null;
            }
        });

        // Spinner for Subject
        ArrayAdapter<CharSequence> subjectAdapter = ArrayAdapter.createFromResource(this,
                R.array.subject_options, R.layout.spinner_item2);
        subjectAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerSubject.setAdapter(subjectAdapter);
        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    subject = null;
                } else {
                    subject = (String) parent.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                subject = null;
            }
        });

        String[] degreeKeys = new String[]{"Select your Degree", "Bachelor of Architecture (BArch)", "Bachelor of Arts (BA)", "Bachelor of Business Administration (BBA)", "Bachelor of Design (BDes)", "Bachelor of Engineering (BEng)", "Bachelor of Environmental Science (BEnvSc)", "Bachelor of Fine Arts (BFA)", "Bachelor of Hospitality Management (BHM)", "Bachelor of Information Technology (BIT)", "Bachelor of Laws (LLB)", "Bachelor of Medicine, Bachelor of Surgery (MBBS)", "Bachelor of Music (BM)", "Bachelor of Physical Education (BPE)", "Bachelor of Public Administration (BPA)", "Bachelor of Science (BSc)", "Bachelor of Science in Agriculture (BSA)", "Bachelor of Science in Nursing (BSN)", "Bachelor of Science in Tourism (BST)", "Bachelor of Social Work (BSW)", "Bachelor of Technology (BTech)"};

        // Spinner for Degree and branch
        ArrayAdapter<String> degreeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item2, degreeKeys);
        degreeAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerDegree.setAdapter(degreeAdapter);
        spinnerDegree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectDegree = null;
                } else {
                    selectDegree = spinnerDegree.getSelectedItem().toString();
                    updateSpecializationSpinner(selectDegree);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {  // Set OnClickListener for ImageView
            @Override
            public void onClick(View v) {
                navigateToHomePage();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_home = new Intent(Register.this, HomePage.class);
                startActivity(intent_home);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    registerUserDetails();
                }
            }
        });

    }

    private void handleEducationSelection(String education) {
        if ("School or Intermediate".equals(education)) {
            spinnerBoard.setVisibility(View.VISIBLE);
            spinnerSubject.setVisibility(View.VISIBLE);
            spinnerDegree.setVisibility(View.GONE);
            spinnerBranch.setVisibility(View.GONE);

            boardText.setVisibility(View.VISIBLE);
            subjectText.setVisibility(View.VISIBLE);
            degreeText.setVisibility(View.GONE);
            branchText.setVisibility(View.GONE);

            boardImg.setVisibility(View.VISIBLE);
            subjectImg.setVisibility(View.VISIBLE);
            degreeImg.setVisibility(View.GONE);
            branchImg.setVisibility(View.GONE);

            hobby.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT));
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) hobby.getLayoutParams();
            params.topToBottom = spinnerSubject.getId();
            params.setMargins(0, 32, 0, 0);
            hobby.setLayoutParams(params);

        } else if ("University_UG".equals(education)) {
            spinnerBoard.setVisibility(View.GONE);
            spinnerSubject.setVisibility(View.GONE);
            spinnerDegree.setVisibility(View.VISIBLE);
            spinnerBranch.setVisibility(View.VISIBLE);

            boardText.setVisibility(View.GONE);
            subjectText.setVisibility(View.GONE);
            degreeText.setVisibility(View.VISIBLE);
            branchText.setVisibility(View.VISIBLE);

            boardImg.setVisibility(View.GONE);
            subjectImg.setVisibility(View.GONE);
            degreeImg.setVisibility(View.VISIBLE);
            branchImg.setVisibility(View.VISIBLE);

            hobby.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT));
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) hobby.getLayoutParams();
            params.topToBottom = spinnerBranch.getId();
            params.setMargins(0, 32, 0, 0);
            hobby.setLayoutParams(params);

        } else {
            spinnerBoard.setVisibility(View.GONE);
            spinnerSubject.setVisibility(View.GONE);
            spinnerDegree.setVisibility(View.GONE);
            spinnerBranch.setVisibility(View.GONE);

            boardText.setVisibility(View.GONE);
            subjectText.setVisibility(View.GONE);
            degreeText.setVisibility(View.GONE);
            branchText.setVisibility(View.GONE);

            boardImg.setVisibility(View.GONE);
            subjectImg.setVisibility(View.GONE);
            degreeImg.setVisibility(View.GONE);
            branchImg.setVisibility(View.GONE);

            hobby.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT));
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) hobby.getLayoutParams();
            params.topToBottom = spinnerEdu.getId();
            params.setMargins(0, 32, 0, 0);
            hobby.setLayoutParams(params);
        }
    }

    private void updateSpecializationSpinner(String selectedDegree) {
        String[] specializations = degreeSpecializationMap.get(selectedDegree);
        if (specializations != null) {
            ArrayAdapter<String> specializationAdapter = new ArrayAdapter<>(this, R.layout.spinner_item2, specializations);
            specializationAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinnerBranch.setAdapter(specializationAdapter);
            spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        branch = null;
                    } else {
                        branch = (String) parent.getItemAtPosition(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    branch = null;
                }
            });
        }
    }

    private boolean validateFields() {
        String age = editTextAge.getText().toString().trim();
        if (age.isEmpty() || selectedGender == null || education == null) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ("School or Intermediate".equals(education)) {
            if (board == null || subject == null) {
                Toast.makeText(this, "Board and Subject are required", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if ("University_UG".equals(education)) {
            if (selectDegree == null || branch == null) {
                Toast.makeText(this, "Degree and Branch are required", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void registerUserDetails() {
        final String age = editTextAge.getText().toString().trim();
        final String careerFocus = editTextCareerFocus.getText().toString().trim();

        if (age.isEmpty() || selectedGender == null || selectedGender.isEmpty() || education == null || education.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String emailKey = email.replace(".", ",");

                // Save user details to Firebase
                dbRef.child("users").child(emailKey).child("age").setValue(age);
                dbRef.child("users").child(emailKey).child("gender").setValue(selectedGender);
                dbRef.child("users").child(emailKey).child("education").setValue(education);
                if ("School or Intermediate".equals(education)) {
                    dbRef.child("users").child(emailKey).child("board").setValue(board);
                    dbRef.child("users").child(emailKey).child("subject").setValue(subject);
                } else if ("University_UG".equals(education)) {
                    dbRef.child("users").child(emailKey).child("degree").setValue(selectDegree);
                    dbRef.child("users").child(emailKey).child("branch").setValue(branch);
                }
                dbRef.child("users").child(emailKey).child("hobbies").setValue(careerFocus);

                // Registration successful, check for last choice
                checkLastChoice(emailKey);
            }
        }
    }

    private void checkLastChoice(String emailKey) {
        DatabaseReference lastChoiceRef = dbRef.child("users").child(emailKey).child("lastChoice");
        lastChoiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String lastChoice = dataSnapshot.getValue(String.class);
                    Log.d("Register", "Navigating to LastChoiceHandler with lastChoice: " + lastChoice);
                    navigateToActivity(LastChoiceHandler.class, lastChoice);
                } else {
                    Log.d("Register", "No last choice found, navigating to HomePage");
                    navigateToActivity(HomePage.class, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Register", "Database error: " + databaseError.getMessage());
                navigateToActivity(HomePage.class, null);
            }
        });
    }

    private void navigateToActivity(Class<?> targetActivity, String lastChoice) {
        Log.d("Register", "Navigating to activity: " + targetActivity.getSimpleName());
        Intent intent = new Intent(Register.this, targetActivity);
        if (lastChoice != null) {
            intent.putExtra("lastChoice", lastChoice);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
        startActivity(intent);
        finish();
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(Register.this, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}