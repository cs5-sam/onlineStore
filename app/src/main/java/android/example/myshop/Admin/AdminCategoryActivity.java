package android.example.myshop.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.myshop.HomeActivity;
import android.example.myshop.MainActivity;
import android.example.myshop.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView tShirts,sportsTShirts,femaleDresses,sweathers;
    private ImageView glasses,hatsCaps,walletBagsPurses,shoes;
    private ImageView headphonesHandFree,Laptops,watches,mobilePhones;
    private Button logoutBtn, checkOrderBtn,updateProductBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        tShirts = findViewById(R.id.t_shirts);
        sportsTShirts = findViewById(R.id.sports_t_shirts);
        femaleDresses = findViewById(R.id.female_dresses);
        sweathers = findViewById(R.id.sweather);
        glasses = findViewById(R.id.glasses);
        hatsCaps = findViewById(R.id.hats_caps);
        walletBagsPurses = findViewById(R.id.purse_bags);
        shoes = findViewById(R.id.shoes);
        headphonesHandFree = findViewById(R.id.headphones_handfree);
        Laptops = findViewById(R.id.laptop_pc);
        watches = findViewById(R.id.watches);
        mobilePhones = findViewById(R.id.mobilephones);
        logoutBtn = findViewById(R.id.admin_logout_btn);
        checkOrderBtn = findViewById(R.id.check_orders_btn);
        updateProductBtn = findViewById(R.id.update_product_btn);

        tShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category","tShirts");
                startActivity(i);
            }
        });
        sportsTShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","Sports tShirts");
                startActivity(i);
            }
        });
        femaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","Female Dresses");
                startActivity(i);
            }
        });
        sweathers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","Sweathers");
                startActivity(i);
            }
        });
        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","Glasses");
                startActivity(i);
            }
        });
        hatsCaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","Hats Caps");
                startActivity(i);
            }
        });
        walletBagsPurses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","Wallets Bags Purses");
                startActivity(i);
            }
        });
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","Shoes");
                startActivity(i);
            }
        });
        headphonesHandFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","HeadPhones HandFree");
                startActivity(i);
            }
        });
        Laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","Laptops");
                startActivity(i);
            }
        });
        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","Watches");
                startActivity(i);
            }
        });
        mobilePhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","Mobile Phones");
                startActivity(i);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });

        checkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminNewOrderActivity.class);
                startActivity(i);
            }
        });

        updateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                i.putExtra("Admin","Admin");
                startActivity(i);
            }
        });
    }
}
