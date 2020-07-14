package android.example.myshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.example.myshop.Model.Users;
import android.example.myshop.Prevalent.Prevalent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button signupBtn;
    Button loginBtn;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signupBtn = findViewById(R.id.main_join_btn);
        loginBtn = findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        //REMEMBER ME
        String UserPhone = Paper.book().read(Prevalent.userPhone);
        String UserPassword = Paper.book().read(Prevalent.userPasswordKey);

        if(UserPhone != "" && UserPassword!= ""){

            if(!TextUtils.isEmpty(UserPhone) && !TextUtils.isEmpty(UserPassword)){
                AllowAccess(UserPhone,UserPassword);
                loadingBar.setTitle("Already logged in");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }

        }
    }
    private void AllowAccess(final String phone,final String password){
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(phone).exists()){
                    //retrieving user data
                    Users userdata = snapshot.child("Users").child(phone).getValue(Users.class);
                    if(userdata.getPhone().equals(phone)){

                        if(userdata.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent i =new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.onlineUser = userdata;
                            startActivity(i);
                        }
                    }

                    else{
                        Toast.makeText(MainActivity.this, "Password/Phone is incorrect", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }else{
                    Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Please create a new account!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this,RegisterActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
