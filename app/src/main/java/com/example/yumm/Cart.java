package com.example.yumm;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yumm.Interface.ItemDeleteListener;
import com.example.yumm.Model.Food;
import com.example.yumm.Model.Order;
import com.example.yumm.Model.Request;
import com.example.yumm.ViewHolder.CartAdapter;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity implements ItemDeleteListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference requests;

    private TextView textTotalPrice;
    private Button btnPlaceOrder;

    private List<Order> cart ;

    private CartAdapter adapter;

    private Spinner spinner;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private String phn;
    private String address;

    private String upiID = "your_upiId@bankname";
    private String upiName = "Your Name";
    private String upiNote = "Yumm Food Order";
    private static final int UPI_PAYMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        textTotalPrice = (TextView) findViewById(R.id.total);
        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);

        spinner = (Spinner) findViewById(R.id.spinAddress);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        phn = firebaseAuth.getCurrentUser().getPhoneNumber();

        loadListFood();

    }

    private void placeOrder() {
        address = spinner.getSelectedItem().toString();
        if(address.equals("Select an Address")){
            Toast.makeText(getBaseContext(),"Please Select an Address!",Toast.LENGTH_SHORT).show();
        }
        else if(cart.size() == 0) {
            Toast.makeText(getBaseContext(),"Cart is empty!",Toast.LENGTH_SHORT).show();
        }
        else if(phn == null) {
            Toast.makeText(getBaseContext(),"Please Register with mobile number",Toast.LENGTH_SHORT).show();
            AuthUI.getInstance().signOut(getBaseContext());
            finish();
        }
        else {
            payUsingUpi(String.valueOf(adapter.getPrice()));
            //Toast.makeText(this,"Order Placed,YAY!",Toast.LENGTH_SHORT).show();
        }
    }

    private void loadListFood() {
        cart = new Database(this).getCart();
        adapter = new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        setTotalPrice(adapter.getPrice());

    }

    public void setTotalPrice(int total) {

        Locale locale = new Locale("en","IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        textTotalPrice.setText(fmt.format(total));
    }


    @Override
    public void onItemDeleted(int position) {
        //int price = adapter.getPrice();
        //setTotalPrice(price);
        Order item = adapter.getItem(position);
        if(item != null) {
            int price = (adapter.getPrice())- (Integer.parseInt(item.getPrice().toString()) * Integer.parseInt(item.getQuantity().toString()));
            new Database(this).removeItem(adapter.getItem(position));
            cart = new Database(this).getCart();
            adapter = new CartAdapter(cart,this);
            recyclerView.setAdapter(adapter);
            setTotalPrice(price);
        }
    }

    void payUsingUpi(String amount) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa",upiID)
                .appendQueryParameter("pn",upiName)
                .appendQueryParameter("tn",upiNote)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("cu","INR")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        Intent chooser = Intent.createChooser(upiPayIntent,"Pay with:");

        if(chooser.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(this,"No UPI App Found",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT :
                if(resultCode == RESULT_OK ) {
                    if(data != null) {
                        String txt = data.getStringExtra("response");
                        Request request = new Request(
                                phn,
                                address,
                                String.valueOf(adapter.getPrice()),
                                cart
                        );
                        requests.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);
                        new Database(this).cleanCart();
                        spinner.setSelection(0);
                        Toast.makeText(this,"Order Placed! YAY!",Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this,"What",Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show();

                }
        }
    }


    /*
    private void pay() {
        String payeeVpa = "swapniksaiba@upi";
        String payeeName = "Saiba";
        String transactionId = "FWB4MGW3JBEMNVGWGBE";
        String transactionRefId = "Q3JTGWIFUHBHRF37IUFHBEFW3IKEHB";
        String description = "FOOD";
        String amount = String.valueOf(adapter.getPrice());

        // START PAYMENT INITIALIZATION
        mEasyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa(payeeVpa)
                .setPayeeName(payeeName)
                .setTransactionId(transactionId)
                .setTransactionRefId(transactionRefId)
                .setDescription(description)
                .setAmount(amount)
                .build();

        // Register Listener for Events
        mEasyUpiPayment.setPaymentStatusListener(this);

        switch (paymentAppChoice.getId()) {
            case R.id.app_default:
                mEasyUpiPayment.setDefaultPaymentApp(PaymentApp.NONE);
                break;
            case R.id.app_amazonpay:
                mEasyUpiPayment.setDefaultPaymentApp(PaymentApp.AMAZON_PAY);
                break;
            case R.id.app_bhim_upi:
                mEasyUpiPayment.setDefaultPaymentApp(PaymentApp.BHIM_UPI);
                break;
            case R.id.app_google_pay:
                mEasyUpiPayment.setDefaultPaymentApp(PaymentApp.GOOGLE_PAY);
                break;
            case R.id.app_phonepe:
                mEasyUpiPayment.setDefaultPaymentApp(PaymentApp.PHONE_PE);
                break;
            case R.id.app_paytm:
                mEasyUpiPayment.setDefaultPaymentApp(PaymentApp.PAYTM);
                break;
        }

        // Check if app exists or not
        if (mEasyUpiPayment.isDefaultAppExist()) {
            onAppNotFound();
            return;
        }
        // END INITIALIZATION

        // START PAYMENT
        mEasyUpiPayment.startPayment();
    }
    */
}