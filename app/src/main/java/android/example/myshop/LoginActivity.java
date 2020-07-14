package android.example.myshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.example.myshop.Admin.AdminCategoryActivity;
import android.example.myshop.Model.Users;
import android.example.myshop.Prevalent.Prevalent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    EditText inputPhone;
    EditText inputPassword;
    Button loginBtn;
    ProgressDialog loadingBar;
    String parentDBName = "Users";
    CheckBox checkBoxRmemeberMe;
    TextView adminLink;
    TextView userLink;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputPhone = findViewById(R.id.login_phone_input);
        inputPassword = findViewById(R.id.login_password_input);
        loginBtn = findViewById(R.id.first_login_btn);
        loadingBar = new ProgressDialog(this);
        checkBoxRmemeberMe = findViewById(R.id.remember_me_chk);
        adminLink = findViewById(R.id.admin_panel_link);
        userLink = findViewById(R.id.not_admin_panel_link);
        forgotPassword = findViewById(R.id.forgot_password_link);

        Paper.init(this);

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                userLink.setVisibility(View.VISIBLE);
                parentDBName = "Admins";
            }
        });

        userLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                userLink.setVisibility(View.INVISIBLE);
                parentDBName = "Users";
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                i.putExtra("login","check");
                startActivity(i);
            }
        });
    }

    private void loginUser(){
        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, checking credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            userAccessGranted(phone,password);
        }
    }
    //GRANT ACCESS
    private void userAccessGranted(final String phone,final String password){
        //REMEMBER ME
        if(checkBoxRmemeberMe.isChecked()){
            Paper.book().write(Prevalent.userPhone,phone);
            Paper.book().write(Prevalent.userPasswordKey,password);
        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDBName).child(phone).exists()){
                    //retrieving user data
                    Users userdata = snapshot.child(parentDBName).child(phone).getValue(Users.class);
                    if(userdata.getPhone().equals(phone)){
                        if(userdata.getPassword().equals(password)){
                            if(parentDBName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Welcome Admin, logged in successfully!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent i =new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else if(parentDBName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent i =new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.onlineUser = userdata;
                                startActivity(i);
                                finish();
                            }
                        }
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Password/Phone is incorrect", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }else{
                    Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Please create a new account!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
