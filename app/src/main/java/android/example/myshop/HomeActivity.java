package android.example.myshop;

import android.content.Intent;
import android.example.myshop.Admin.AdminUpdateProductActivity;
import android.example.myshop.Model.Products;
import android.example.myshop.Prevalent.Prevalent;
import android.example.myshop.ViewHolder.ProductViewHolder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference ProductRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private String typeUser= "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");

        // from admin activity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            typeUser = getIntent().getExtras().get("Admin").toString();
        }

        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typeUser.equals("Admin")){
                    Intent i = new Intent(HomeActivity.this,CartActivity.class);
                    startActivity(i);
                }
                }
            });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileView = headerView.findViewById(R.id.user_profile_image);

        if(!typeUser.equals("Admin")){
            userNameTextView.setText(Prevalent.onlineUser.getName());
            Picasso.get().load(Prevalent.onlineUser.getImage()).placeholder(R.drawable.profile).into(profileView);
        }

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    //RETRIEVE DATA

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductRef,Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                        productViewHolder.txtproductName.setText(products.getPname());
                        productViewHolder.txtproductDescription.setText(products.getDescription());
                        productViewHolder.txtproductPrice.setText("Price = " + products.getPrice()+"$");
                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(typeUser.equals("Admin")){
                                    Intent i = new Intent(HomeActivity.this, AdminUpdateProductActivity.class);
                                    i.putExtra("pid",products.getPid());
                                    startActivity(i);
                                }
                                else{
                                    Intent i = new Intent(HomeActivity.this,ProductDetailActivity.class);
                                    i.putExtra("pid",products.getPid());
                                    startActivity(i);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout,parent,false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            if(!typeUser.equals("Admin")){
                Intent i = new Intent(HomeActivity.this,CartActivity.class);
                startActivity(i);
            }
        }
        else if (id == R.id.nav_search) {
            if(!typeUser.equals("Admin")){
                Intent i = new Intent(HomeActivity.this,SearchProductsActivity.class);
                startActivity(i);
            }
        }
        else if (id == R.id.nav_categories) {

        }
        else if (id == R.id.nav_settings) {
            if(!typeUser.equals("Admin")){
                Intent i = new Intent(HomeActivity.this,SettingsActivity.class);
                startActivity(i);
            }
        }
        else if (id == R.id.nav_logout) {
            if(!typeUser.equals("Admin")){
                Paper.book().destroy();
                Intent  i = new Intent(HomeActivity.this,MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
