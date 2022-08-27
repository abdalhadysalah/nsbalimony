package nsb.alimony;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout messageLayout, loadingLayout;
    EditText emailEdiText, passwordEditText;
    Button loginButton, okButton;
    TextView messageTextView, createAccountButton, forgotPasswordButton, localeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        messageLayout = findViewById(R.id.layoutMessage);
        loadingLayout = findViewById(R.id.layoutLoading);
        emailEdiText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        okButton = findViewById(R.id.buttonOk);
        messageTextView = findViewById(R.id.textViewMessage);
        createAccountButton = findViewById(R.id.textViewCreateAccount);
        forgotPasswordButton = findViewById(R.id.textViewForgotPassword);
        localeTextView = findViewById(R.id.locale);

        SharedPreferences preferences = getSharedPreferences("language", MODE_PRIVATE);
        String lang = preferences.getString("lang", "ar");

        if (lang.equals("ar")) {
            localeTextView.setText("English");
        } else {
            localeTextView.setText("العربية");
        }


        localeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                Locale locale;
                if (lang.equals("en")) {
                    editor.putString("lang", "ar");
                    locale = new Locale("ar");
                } else {
                    editor.putString("lang", "en");
                    locale = new Locale("en");
                }
                editor.apply();

                Locale.setDefault(locale);
                Resources resources = getResources();
                Configuration config = resources.getConfiguration();
                config.setLocale(locale);
                resources.updateConfiguration(config, resources.getDisplayMetrics());
                LoginActivity.this.finishAffinity();
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
            }
        });

        loginButton.setOnClickListener(view -> {
            loginStartConfig();

            String email = emailEdiText.getText().toString().trim();

            if (email.isEmpty()) {
                showMessage(getString(R.string.empty_email));
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showMessage(getString(R.string.badly_formatted_email));
                return;
            }

            String password = passwordEditText.getText().toString().trim();

            if (password.isEmpty()) {
                showMessage(getString(R.string.empty_password));
                return;
            }

            if (password.length() < 6) {
                showMessage(getString(R.string.badly_formatted_password));
                return;
            }

            if (email.equals("nsbalimony@gmail.com") && password.equals("1234567")) {
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                startActivity(intent);
                return;
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Map<String, Object> data = new HashMap<>();
                        data.put("password", password);
                        String nid = user.getDisplayName();
                        int number = Integer.parseInt(nid.substring(12, 13));
                        String collection = "";
                        if (number % 2 == 0) {
                            collection = "Females";
                        } else {
                            collection = "Users";
                        }
                        FirebaseFirestore.getInstance().collection(collection).document(nid).update(data);
                        Intent intent = new Intent();
                        if (user != null && user.isEmailVerified()) {
                            intent.setClass(LoginActivity.this, MaleHomeActivity.class);
                        } else {
                            intent.setClass(LoginActivity.this, VerifyActivity.class);
                        }

                        startActivity(intent);
                        LoginActivity.this.finishAffinity();
                    } else {
                        showMessage(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });

        });

        okButton.setOnClickListener(view -> messageLayout.setVisibility(View.GONE));

        createAccountButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPasswordButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

    }

    private void loginStartConfig () {
        loadingLayout.setVisibility(View.VISIBLE);
        emailEdiText.setEnabled(false);
        passwordEditText.setEnabled(false);
        loginButton.setEnabled(false);
        createAccountButton.setEnabled(false);
        forgotPasswordButton.setEnabled(false);
    }

    private void loginStopConfig () {
        loadingLayout.setVisibility(View.GONE);
        emailEdiText.setEnabled(true);
        passwordEditText.setEnabled(true);
        loginButton.setEnabled(true);
        createAccountButton.setEnabled(true);
        forgotPasswordButton.setEnabled(true);
    }

    private void showMessage (String message) {
        loginStopConfig();
        messageLayout.setVisibility(View.VISIBLE);
        messageTextView.setText(message);
    }

}