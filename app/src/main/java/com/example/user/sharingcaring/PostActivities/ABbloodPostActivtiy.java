package com.example.user.sharingcaring.PostActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.sharingcaring.BloodCategories.ABpositive;
import com.example.user.sharingcaring.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ABbloodPostActivtiy extends AppCompatActivity {

    EditText bloodAbout,bloodGroup,bloodNeeded,bloodLocation,bloodPhnNumber;
    Button postButton;

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef,postRef;
    private StorageReference storageReference;
    private String saveCurrentDate, saveCurrentTime,postRandomName,downloadUrl,currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abblood_post_activtiy);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        storageReference=FirebaseStorage.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("ABPositiveBloodPosts");

        loadingBar=new ProgressDialog(this);


        bloodAbout=findViewById(R.id.ab_blood_about);
        bloodGroup=findViewById(R.id.ab_blood_group);
        bloodNeeded=findViewById(R.id.ab_blood_needed);
        bloodLocation=findViewById(R.id.ab_blood_location);
        postButton=findViewById(R.id.ab_blood_post_button);
        bloodPhnNumber=findViewById(R.id.ab_blood_phoneNumber);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePost();
            }
        });

    }

    private void validatePost() {

        String about=bloodAbout.getText().toString();
        String blood_group=bloodGroup.getText().toString();
        String blood_need=bloodNeeded.getText().toString();
        String blood_location=bloodLocation.getText().toString();
        String blood_phnNumber=bloodPhnNumber.getText().toString();

        if (TextUtils.isEmpty(about)){
            Toast.makeText(ABbloodPostActivtiy.this,"Please, fill up the blank segment",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(blood_group)){
            Toast.makeText(ABbloodPostActivtiy.this,"Please, fill up the blank segment",Toast.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(blood_need)){
            Toast.makeText(ABbloodPostActivtiy.this,"Please, fill up the blank segment",Toast.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(blood_location)){
            Toast.makeText(ABbloodPostActivtiy.this,"Please, fill up the blank segment",Toast.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(blood_phnNumber)){
            Toast.makeText(ABbloodPostActivtiy.this,"Please, fill up the blank segment",Toast.LENGTH_LONG).show();
        }else {
            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please wait, while we are updating your new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            savingPostIntoDatabase();
        }
    }

    private void savingPostIntoDatabase() {
        Calendar calFordate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate=currentDate.format(calFordate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        saveCurrentTime=currentTime.format(calFordate.getTime());

        postRandomName=saveCurrentDate+saveCurrentTime;

        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String about_blood=bloodAbout.getText().toString();
                    String location_blood=bloodLocation.getText().toString();
                    String needed_blood=bloodNeeded.getText().toString();
                    String group_blood=bloodGroup.getText().toString();
                    String phnNumber_blood=bloodPhnNumber.getText().toString();

                    String userName=  dataSnapshot.child("User Name").getValue().toString();
                    String userProfile=  dataSnapshot.child("profile image").getValue().toString();


                    HashMap postMap=new HashMap();
                    postMap.put("uid",currentUserId);
                    postMap.put("date", saveCurrentDate);
                    postMap.put("time", saveCurrentTime);
                    postMap.put("aboutBlood", about_blood);
                    postMap.put("location", location_blood);
                    postMap.put("bloodGroup", group_blood);
                    //postMap.put("profileimage", userProfile);
                    postMap.put("whenNeed", needed_blood);
                    postMap.put("profileimage", userProfile);
                    postMap.put("fullname", userName);
                    postMap.put("phoneNumber",phnNumber_blood);


                    postRef.child(currentUserId + postRandomName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                //sendUserToMain();
                                sendUserToABPosBlood();
                                Toast.makeText(ABbloodPostActivtiy.this, "New Post is updated successfully.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }else {
                                Toast.makeText(ABbloodPostActivtiy.this, "Error Occurred while updating your post.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToABPosBlood() {

        Intent intent=new Intent(ABbloodPostActivtiy.this,ABpositive.class);
        startActivity(intent);
    }
}
