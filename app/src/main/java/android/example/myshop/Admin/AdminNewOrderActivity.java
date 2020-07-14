package android.example.myshop.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.example.myshop.Model.AdminOrders;
import android.example.myshop.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef,AdminOrders.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder adminOrdersViewHolder, final int i, @NonNull final AdminOrders adminOrders) {

                        adminOrdersViewHolder.userName.setText("Name : "+ adminOrders.getName());
                        adminOrdersViewHolder.userPhone.setText("Phone : "+ adminOrders.getPhone());
                        adminOrdersViewHolder.userTotalAmount.setText("Amount : "+ adminOrders.getTotalAmount());
                        adminOrdersViewHolder.userDateTime.setText("Order at : "+ adminOrders.getDate() + " " + adminOrders.getTime());
                        adminOrdersViewHolder.userAddress.setText("Address : "+ adminOrders.getAddress() + ", " + adminOrders.getCity());

                        adminOrdersViewHolder.showProductsBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uID = getRef(i).getKey();

                                Intent i = new Intent(AdminNewOrderActivity.this, AdminUserProductsActivity.class);
                                i.putExtra("uid",uID);
                                startActivity(i);
                            }
                        });

                        adminOrdersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] =
                                        new CharSequence[]{
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                                builder.setTitle("Have you shipped the order ?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(i == 0){
                                            String uID = getRef(i).getKey();
                                            RemoveOrder(uID);
                                        }else{
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrdersViewHolder(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, userPhone,userTotalAmount,userDateTime,userAddress;
        public Button showProductsBtn;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userPhone = itemView.findViewById(R.id.order_phone_number);
            userTotalAmount = itemView.findViewById(R.id.order_total_amount);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userAddress = itemView.findViewById(R.id.order_address_city);
            showProductsBtn = itemView.findViewById(R.id.show_all_products_btn);
        }
    }

    private void RemoveOrder(String uID) {
        ordersRef.child(uID).removeValue();
    }
}
