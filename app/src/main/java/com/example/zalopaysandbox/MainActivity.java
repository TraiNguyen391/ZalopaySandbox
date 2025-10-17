package com.example.zalopaysandbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnConfirm;
    EditText tvQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnConfirm = findViewById(R.id.btnConfirm);
        tvQuantity = findViewById(R.id.tvQuantity);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                if (tvQuantity.getText().toString().isEmpty() || tvQuantity.getText().toString().equals("0")){
                    Toast.makeText(MainActivity.this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
                    return;
                }

                String quantity = tvQuantity.getText().toString();
                double totalAmount = Double.parseDouble(quantity) * (double) 100000;

                Intent intent = new Intent(MainActivity.this, OrderPayment.class);
                intent.putExtra("totalAmount", totalAmount);
                intent.putExtra("quantity", quantity);
                startActivity(intent);
            }
        });

    }
}