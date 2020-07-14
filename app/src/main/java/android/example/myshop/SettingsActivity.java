package android.example.myshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.example.myshop.Model.Users;
import android.example.myshop.Prevalent.Prevalent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameText, userPhoneText,addressText;
    private TextView profileChangeBtn, closeTextBtn, saveTextBtn;
    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePicturesRef;
    private StorageTask uploadTask;
    private String checker = "";
    private Button secuityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePicturesRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        profileImageView = findViewById(R.id.settings_profile_image);
        fullNameText = findViewById(R.id.settings_full_name);
        userPhoneText = findViewById(R.id.settings_phone_number);
        addressText = findViewById(R.id.settings_address);
        profileChangeBtn = findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings_btn);
        saveTextBtn = findViewById(R.id.update_account_settings);
        secuityBtn = findViewById(R.id.security_questions);

        userInfoDisplay(profileImageView,fullNameText,userPhoneText,addressText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked")){
                    userInfoSaved();
                }
                else{
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";

                // OPEN GALLERY
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

        secuityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this,ResetPasswordActivity.class);
                i.putExtra("settings","check");
                startActivity(i);
            }
        });
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("name",fullNameText.getText().toString());
        userMap.put("address",addressText.getText().toString());
        userMap.put("phoneOrder",userPhoneText.getText().toString());

        ref.child(Prevalent.onlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile info updated!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else{
            Toast.makeText(this, "Error, try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved(){
        if(TextUtils.isEmpty(fullNameText.getText().toString())){
            Toast.makeText(this, "Name is mandatory...", Toast.LENGTH_SHORT).show();
            fullNameText.setFocusableInTouchMode(true);
            fullNameText.requestFocus();
        }
        else if(TextUtils.isEmpty(addressText.getText().toString())){
            Toast.makeText(this, "Address is mandatory...", Toast.LENGTH_SHORT).show();
            addressText.setFocusableInTouchMode(true);
            addressText.requestFocus();
        }

        else if(TextUtils.isEmpty(userPhoneText.getText().toString())){
            Toast.makeText(this, "Phone is mandatory...", Toast.LENGTH_SHORT).show();
            userPhoneText.setFocusableInTouchMode(true);
            userPhoneText.requestFocus();
        }
        else if(checker.equals("clicked")){
            uploadImage();
        }
    }

    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating profile");
        progressDialog.setMessage("Please wait, updating account information.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if(imageUri != null){
            final StorageReference fileRef = storageProfilePicturesRef
                    .child(Prevalent.onlineUser.getPhone() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw  task.getException();

                    }
                    return fileRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();

                        userMap.put("name",fullNameText.getText().toString());
                        userMap.put("address",addressText.getText().toString());
                        userMap.put("phoneOrder",userPhoneText.getText().toString());
                        userMap.put("image",myUrl);

                        ref.child(Prevalent.onlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile info updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }

    // DISPLAYING INFORMATION
    public void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameText, final EditText userPhoneText, final EditText addressText){
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.onlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("image").exists()){
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameText.setText(name);
                        userPhoneText.setText(phone);
                        addressText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
