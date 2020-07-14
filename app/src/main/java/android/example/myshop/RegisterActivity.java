package android.example.myshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Button createAccount;
    EditText inputName;
    EditText inputPhone;
    EditText inputPassword;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccount = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_name_input);
        inputPhone = findViewById(R.id.register_phone_input);
        inputPassword = findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccountMethod();
            }
        });
    }

    private void CreateAccountMethod(){
        String name = inputName.getText().toString();
        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, checking credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            // checking if phone exist or not and create account
            ValidatePhone(name,phone,password);
        }
    }
    private void ValidatePhone(final String name, final String phone, final String password){
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child("Users").child(phone).exists()){
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);
                    rootRef.child("Users").child(phone).updateChildren(userdataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Congratulations, account created!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(i);
                            }
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Network error, Please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(RegisterActivity.this, "This"+phone+" already exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
