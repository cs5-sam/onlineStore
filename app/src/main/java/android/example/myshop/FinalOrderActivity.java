package android.example.myshop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
    private Button confirmBtn;
    private String totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_order);

        nameEditText = findViewById(R.id.shipment_name);
        phoneEditText = findViewById(R.id.shipment_phone_number);
        addressEditText = findViewById(R.id.shipment_address);
        cityEditText = findViewById(R.id.shipment_city);
        confirmBtn = findViewById(R.id.confirm_order_btn);

        totalAmount = getIntent().getStringExtra("Total Amount");
        Toast.makeText(this, "Total amount : "+totalAmount, Toast.LENGTH_SHORT).show();
    }
}
