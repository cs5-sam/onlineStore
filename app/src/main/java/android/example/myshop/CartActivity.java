package android.example.myshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.example.myshop.Model.Cart;
import android.example.myshop.Prevalent.Prevalent;
import android.example.myshop.ViewHolder.CartViewHolder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Button nextBtn;
    private Button totalBtn;

    private TextView totalAmt;
    private TextView message1;

    private int totalAmount = 0;
    private int oneProductPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextBtn = findViewById(R.id.next_btn);
        totalBtn = findViewById(R.id.total_btn);
        totalAmt = findViewById(R.id.total_amount);
        message1 = findViewById(R.id.msg1);

        totalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalAmt.setText("Total Amount : " + totalAmount);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this,FinalOrderActivity.class);
                i.putExtra("Total Amount",String.valueOf(totalAmount));
                startActivity(i);
                finish();
            }
        });
    }

    // LOADING CONTENT OF CART
    @Override
    protected void onStart() {
        super.onStart();
        //CheckOrderStat();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.onlineUser.getPhone())
                                .child("Products"), Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {
                        cartViewHolder.txtProductName.setText(cart.getPname());
                        cartViewHolder.txtProductQuantity.setText("Quantity : "+cart.getQuantity());
                        cartViewHolder.txtProductPrice.setText("Price : " +cart.getPrice()+"$");

                        //CALCULATING AMOUNT EACH AND TOTAL
                        oneProductPrice = ((Integer.parseInt(cart.getPrice()))) * Integer.parseInt(cart.getQuantity());
                        totalAmount = totalAmount + oneProductPrice;
                        // DELETE/EDIT ITEM FROM CART
                        cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                    "Edit",
                                    "Delete"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart options:");

                                // CLICK LISTENER FOR BUTTONS, GENERATE OPTIONS WHEN ITEM CLICKED

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            Intent i = new Intent(CartActivity.this,ProductDetailActivity.class);
                                            i.putExtra("pid",cart.getPid());
                                            startActivity(i);
                                        }
                                        else if(which == 1){
                                            cartListRef.child("User View").child(Prevalent.onlineUser.getPhone())
                                                    .child("Products").child(cart.getPid())
                                                    .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this, "Item deleted!", Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent(CartActivity.this,HomeActivity.class);
                                                        startActivity(i);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }


                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    /*private void CheckOrderStat(){

        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.onlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String state = snapshot.child("state").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    if(state.equals("shipped")){
                        totalAmt.setText("\n Order is shipped");
                        recyclerView.setVisibility(View.GONE);
                        message1.setVisibility(View.VISIBLE);
                        message1.setText("order shipped");
                        nextBtn.setVisibility(View.GONE);
                        totalBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "Wait for first order", Toast.LENGTH_SHORT).show();
                    }
                    else if(state.equals("not shipped")){
                        totalAmt.setText("\n Not yet shipped");
                        recyclerView.setVisibility(View.GONE);
                        String msg2 = "Not yet shipped";
                        message1.setVisibility(View.VISIBLE);
                        message1.setText(msg2);
                        nextBtn.setVisibility(View.GONE);
                        totalBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "Wait for first order", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}
