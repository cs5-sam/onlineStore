package android.example.myshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.myshop.Model.Products;
import android.example.myshop.Prevalent.Prevalent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ProductDetailActivity extends AppCompatActivity {

    private Button addToCart;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice,productName,productDescription;
    private String productID = "";
    private String state= "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        addToCart = findViewById(R.id.add_to_cart_btn);
        productImage = findViewById(R.id.product_image_details);
        numberButton = findViewById(R.id.number_btn);
        productPrice = findViewById(R.id.product_name_price);
        productName = findViewById(R.id.product_name_details);
        productDescription = findViewById(R.id.product_name_description);

        productID = getIntent().getStringExtra("pid");


        getProductDetails(productID);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state.equals("order placed") || state.equals("order shipped")){
                    Toast.makeText(ProductDetailActivity.this, "Once shipped, can order more", Toast.LENGTH_SHORT).show();
                }
                else{
                    addingToCartList();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderStat();
    }

    // ADDING TO CART FIREBASE
    private void addingToCartList(){
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("time",saveCurrentTime);
        cartMap.put("date",saveCurrentDate);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");

        cartListRef.child("User View").child(Prevalent.onlineUser.getPhone()).child("Products")
                .child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            cartListRef.child("Admin View").child(Prevalent.onlineUser.getPhone()).child("Products")
                                    .child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ProductDetailActivity.this, "Added to Cart!", Toast.LENGTH_SHORT).show();

                                            Intent i = new Intent(ProductDetailActivity.this,HomeActivity.class);
                                            startActivity(i);
                                        }
                                    });
                        }
                    }
                });

    }

    // RETRIEVING DATA AND DISPLAYING
    private void getProductDetails(final String productID){
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products products = snapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText("Description"+products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckOrderStat(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.onlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String state = snapshot.child("state").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    if(state.equals("shipped")){
                        state = "Order shipped";
                    }
                    else if(state.equals("not shipped")){
                        state = "Order placed";
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
