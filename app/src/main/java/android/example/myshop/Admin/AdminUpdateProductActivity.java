package android.example.myshop.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.myshop.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminUpdateProductActivity extends AppCompatActivity {

    private Button updateBtn,deleteBtn;
    private EditText name,price,description;
    private ImageView imageView;
    private String productsId = "";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_product);

        productsId = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productsId);

        updateBtn = findViewById(R.id.apply_update_btn);
        name = findViewById(R.id.product_name_update);
        price = findViewById(R.id.product_price_update);
        description = findViewById(R.id.product_description_update);
        imageView = findViewById(R.id.product_image_update);
        deleteBtn = findViewById(R.id.delete_product_btn);

        displayProductDetail();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });
    }

    private void deleteProduct() {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent i = new Intent(AdminUpdateProductActivity.this, AdminCategoryActivity.class);
                startActivity(i);
                finish();
                Toast.makeText(AdminUpdateProductActivity.this, "Product Deleted Successfully !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyChanges() {
        String nname = name.getText().toString();
        String nprice = price.getText().toString();
        String ndescription = description.getText().toString();
        if(nname.equals("")){
            Toast.makeText(this, "Enter valid name", Toast.LENGTH_SHORT).show();
        }
        else if(nprice.equals("")){
            Toast.makeText(this, "Enter valid price", Toast.LENGTH_SHORT).show();
        }
        else if(ndescription.equals("")){
            Toast.makeText(this, "Enter valid description", Toast.LENGTH_SHORT).show();
        }
        else{
            final HashMap<String, Object> cartMap = new HashMap<>();

            cartMap.put("pid",productsId);
            cartMap.put("pname",nname);
            cartMap.put("price",nprice);
            cartMap.put("description",ndescription);

            productsRef.updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        Toast.makeText(AdminUpdateProductActivity.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AdminUpdateProductActivity.this,AdminCategoryActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        }
    }

    private void displayProductDetail() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String pname = snapshot.child("pname").getValue().toString();
                    String pprice = snapshot.child("price").getValue().toString();
                    String pdescription = snapshot.child("description").getValue().toString();
                    String pimage = snapshot.child("image").getValue().toString();

                    name.setText(pname);
                    price.setText(pprice);
                    description.setText(pdescription);
                    Picasso.get().load(pimage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
