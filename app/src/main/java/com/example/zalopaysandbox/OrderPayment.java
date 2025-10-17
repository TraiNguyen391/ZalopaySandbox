package com.example.zalopaysandbox;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zalopaysandbox.Api.CreateOrder;
import com.example.zalopaysandbox.Constant.AppInfo;

import org.json.JSONObject;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class OrderPayment extends AppCompatActivity {

    private Button btnConfirm;
    private TextView tvQuantity;
    private TextView tvTotalAmount;

    private static final String TAG = "ZaloPay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_payment);

        btnConfirm = findViewById(R.id.btnPay);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d(TAG, "Khởi tạo ZaloPay SDK với appId=" + AppInfo.APP_ID);
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

        Intent intent = getIntent();
        tvQuantity.setText(intent.getStringExtra("quantity"));

        double total = intent.getDoubleExtra("totalAmount", 0);
        String totalString = String.format("%.0f", total);
        tvTotalAmount.setText(totalString + "đ");

        btnConfirm.setOnClickListener(view -> {
            CreateOrder orderApi = new CreateOrder();
            try {
                //Ném cái totalString dạng chuỗi như 100000 vào API tạo đơn hàng
                JSONObject data = orderApi.createOrder(totalString);
                Log.d(TAG, "Kết quả API tạo đơn hàng: " + data);

                String code = data.getString("return_code");
                if (code.equals("1")) {
                    String token = data.getString("zp_trans_token");
                    Log.d(TAG, "Tạo đơn hàng thành công, token=" + token);

                    ZaloPaySDK.getInstance().payOrder(OrderPayment.this, token, "demozpdk2553://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String transactionId, String transToken, String appTransId) {
                            Intent successIntent = new Intent(OrderPayment.this, PaymentNotification.class);
                            successIntent.putExtra("result", "Thanh toán thành công");
                            startActivity(successIntent);
                        }

                        @Override
                        public void onPaymentCanceled(String zpTransToken, String appTransId) {
                            Intent cancelIntent = new Intent(OrderPayment.this, PaymentNotification.class);
                            cancelIntent.putExtra("result", "Hủy thanh toán");
                            startActivity(cancelIntent);
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
                            Intent errorIntent = new Intent(OrderPayment.this, PaymentNotification.class);
                            errorIntent.putExtra("result", "Lỗi thanh toán");
                            startActivity(errorIntent);
                        }
                    });
                } else {
                    Log.e(TAG, "Tạo đơn hàng thất bại. return_code=" + code +
                            ", return_message=" + data.optString("return_message"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi tạo đơn hàng hoặc thanh toán", e);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

}
