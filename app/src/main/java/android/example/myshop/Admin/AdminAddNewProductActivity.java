package android.example.myshop.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.example.myshop.R;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName,Description,Price,Pname,saveCurrentDate,saveCurrentTime;
    private Button addNewProductBtn;
    private EditText inputProductName;
    private EditText inputProductDescription;
    private EditText inputProductPrice;
    private ImageView inputProductImage;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference productRef;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryName = getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        addNewProductBtn = findViewById(R.id.add_new_product);
        inputProductImage = findViewById(R.id.select_product_image);
        inputProductName = findViewById(R.id.product_name);
        inputProductDescription = findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        // OPENING GALLERY
        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

    }

    // SELECTING IMAGE
    private void OpenGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    //GETTING AND DISPLAYING IMAGE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            ImageUri = data.getData();
            inputProductImage.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData(){
        Description = inputProductDescription.getText().toString();
        Price = inputProductPrice.getText().toString();
        Pname = inputProductName.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this, "Product Image Required!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Please enter price", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Pname)){
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
        }
        else{
            StoreProductInformation();
        }
    }

    // GETTING DATE AND TIME FOR GENERATING KEY
    private void StoreProductInformation(){

        loadingBar.setTitle("Add new product");
        loadingBar.setMessage("Please wait, adding new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;
        
        //LINK OF PRODUCT NAME 
        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment()+productRandomKey+".jpg");
        
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        // STORING IN FIREBASE
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product image uploaded successfully!", Toast.LENGTH_SHORT).show();
                // DISPLAYING IMAGE TO USER , GETTING URL
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        // GETTING THE URI NOT THE LINK, WE DO NOT IF UPLOADED OR NOT
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            // GET THE LINK
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "getting product image url successfully!", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    // SAVING IN DATABASE
    private void SaveProductInfoToDatabase(){
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price",Price);
        productMap.put("pname",Pname);

        productRef.child(productRandomKey).updateChildren(productMap)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent i = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                    startActivity(i);

                    loadingBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully!", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
