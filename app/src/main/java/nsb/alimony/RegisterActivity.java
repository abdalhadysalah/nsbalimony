package nsb.alimony;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private LinearLayout messageLayout, loadingLayout;
    private EditText nameEditText, nidEdiText, phoneEditText, emailEditText, passwordEditText;
    private Button registerButton;
    private TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        messageLayout = findViewById(R.id.layoutMessage);
        loadingLayout = findViewById(R.id.layoutLoading);
        nameEditText = findViewById(R.id.editTextName);
        nidEdiText = findViewById(R.id.editTextNId);
        phoneEditText = findViewById(R.id.editTextPhoneNumber);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.buttonRegister);
        Button okButton = findViewById(R.id.buttonOk);
        messageTextView = findViewById(R.id.textViewMessage);

        registerButton.setOnClickListener(view -> {
            registerStartConfig();

            String name = nameEditText.getText().toString().trim();
            String nid = nidEdiText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (name.isEmpty() || nid.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showMessage("One or more fields are empty.");
                return;
            }

            if (nid.length() != 14) {
                showMessage(getString(R.string.inv_id));
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showMessage(getString(R.string.badly_formatted_email));
                return;
            }

            if (password.length() < 6) {
                showMessage(getString(R.string.badly_formatted_password));
                return;
            }

            int number = Integer.parseInt(nid.substring(12, 13));
            String collection = "";
            if (number % 2 == 0) {
                collection = "Females";
            } else {
                collection = "Users";
            }

            String nationalId = FirebaseFirestore.getInstance().collection(collection).document(nid).getId();


            String finalCollection = collection;
            FirebaseFirestore.getInstance().collection(collection).document(nid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            user.updateProfile(new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(nationalId)
                                                    .build());
                                        }

                                        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("id", id);
                                        data.put("name", name);
                                        data.put("phone", phone);
                                        data.put("password", password);
                                        FirebaseFirestore.getInstance().collection(finalCollection).document(nid).update(data);
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(task2 -> {
                                            Intent intent = new Intent(RegisterActivity.this, VerifyActivity.class);
                                            intent.putExtra("nid", nid);
                                            startActivity(intent);
                                            RegisterActivity.this.finishAffinity();
                                        });
                                    } else {
                                        showMessage(Objects.requireNonNull(task1.getException()).getMessage());
                                    }
                                });
                    } else {
                        showMessage("Please Call Customer Service.");
                    }
                 }
            });

        });

        okButton.setOnClickListener(view -> messageLayout.setVisibility(View.GONE));

    }

    private void registerStartConfig () {
        loadingLayout.setVisibility(View.VISIBLE);
        nameEditText.setEnabled(false);
        nidEdiText.setEnabled(false);
        phoneEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        registerButton.setEnabled(false);
    }

    private void registerStopConfig () {
        loadingLayout.setVisibility(View.GONE);
        nameEditText.setEnabled(true);
        nidEdiText.setEnabled(true);
        phoneEditText.setEnabled(true);
        emailEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        registerButton.setEnabled(true);
    }

    private void showMessage (String message) {
        registerStopConfig();
        messageLayout.setVisibility(View.VISIBLE);
        messageTextView.setText(message);
    }

}