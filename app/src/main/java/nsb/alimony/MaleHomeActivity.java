package nsb.alimony;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MaleHomeActivity extends AppCompatActivity {

    Button fawryPay;
    TextView status, balance, br_name, br_number, br_address, br_phone, general_message, pay_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_male_home);

        status = findViewById(R.id.file_status);
        balance = findViewById(R.id.balance);
        br_name = findViewById(R.id.br_name);
        br_address = findViewById(R.id.br_address);
        br_number = findViewById(R.id.br_number);
        br_phone = findViewById(R.id.br_phone);
        general_message = findViewById(R.id.generalMessage);
        pay_message = findViewById(R.id.paymentMessage);
        fawryPay = findViewById(R.id.fawry);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String nid = user.getDisplayName();
            int number = Integer.parseInt(nid.substring(12, 13));
            String collection = "";
            if (number % 2 == 0) {
                collection = "Females";
            } else {
                collection = "Users";
            }

            DocumentReference reference = FirebaseFirestore.getInstance().collection(collection).document(Objects.requireNonNull(nid));

            reference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null){
                    Object name = task.getResult().get("name");
                    if (name != null) {
                        Objects.requireNonNull(getSupportActionBar()).setTitle(String.valueOf(name));
                    }
                }

            });


            Spinner spinner = findViewById(R.id.filesNumbers);

            ArrayList<String> data = new ArrayList<>();
            data.add("Choose your file");

            FirebaseFirestore.getInstance().collection(collection).document(nid).collection("files_numbers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                        data.add(task.getResult().getDocuments().get(i).getId());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MaleHomeActivity.this, android.R.layout.simple_spinner_item, data);
                    spinner.setAdapter(adapter);
                }
            });


            String finalCollection = collection;
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != 0) {
                        String fileNumber = data.get(i);
                        FirebaseFirestore.getInstance().collection(finalCollection).document(nid).collection("files_numbers").document(fileNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult() != null) {
                                    String status1 = (String) task.getResult().get("status");
                                    Long balance1 = (Long) task.getResult().get("balance");
                                    String address1 = (String) task.getResult().get("br_address");
                                    String name1 = (String) task.getResult().get("br_name");
                                    Long number1 = (Long) task.getResult().get("br_number");
                                    String phone1 = (String) task.getResult().get("br_phone");
                                    String g_message1 = (String) task.getResult().get("general_message");
                                    String p_message1 = (String) task.getResult().get("payment_message");

                                    status.setText(String.valueOf(status1));
                                    balance.setText(String.valueOf(balance1));
                                    br_number.setText(String.valueOf(number1));
                                    br_name.setText(String.valueOf(name1));
                                    br_phone.setText(String.valueOf(phone1));
                                    br_address.setText(String.valueOf(address1));
                                    general_message.setText(String.valueOf(g_message1));
                                    pay_message.setText(String.valueOf(p_message1));
                                }
                            }
                        });
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }


        fawryPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MaleHomeActivity.this, PayActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.verify_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MaleHomeActivity.this, LoginActivity.class);
            startActivity(intent);
            MaleHomeActivity.this.finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

}