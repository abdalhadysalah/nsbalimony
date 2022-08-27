package nsb.alimony;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VerifyActivity extends AppCompatActivity {

    LinearLayout messageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        messageLayout = findViewById(R.id.layoutMessage);
        Button okButton = findViewById(R.id.buttonOk);
        TextView messageTextView = findViewById(R.id.textViewMessage);

        Button startUsingApp = findViewById(R.id.button_start_using_nsba);
        startUsingApp.setOnClickListener(view -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String email = user.getEmail();
            String nid = user.getDisplayName();
            int number = Integer.parseInt(nid.substring(12, 13));
            String collection = "";
            if (number % 2 == 0) {
                collection = "Females";
            } else {
                collection = "Users";
            }
            String finalCollection = collection;
            FirebaseFirestore.getInstance().collection(collection).document(nid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String password = (String) task.getResult().get("password");

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("password", password);
                                    FirebaseFirestore.getInstance().collection(finalCollection).document(nid).update(data);
                                    Intent intent = new Intent();
                                    if (user != null && user.isEmailVerified()) {
                                        if (finalCollection.equals("Females")) {
                                            intent.setClass(VerifyActivity.this, FemaleHomeActivity.class);
                                        } else if (finalCollection.equals("Users")) {
                                            intent.setClass(VerifyActivity.this, MaleHomeActivity.class);
                                        }
                                    } else {
                                        messageLayout.setVisibility(View.VISIBLE);
                                        messageTextView.setText(R.string.confirm_email);
                                        return;
                                    }

                                    startActivity(intent);
                                    VerifyActivity.this.finishAffinity();

                                } else {
                                    messageLayout.setVisibility(View.VISIBLE);
                                    messageTextView.setText(task1.getException().getMessage());
                                }
                            });




                }
            });

        });

        okButton.setOnClickListener(view -> messageLayout.setVisibility(View.GONE));

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
            Intent intent = new Intent(VerifyActivity.this, LoginActivity.class);
            startActivity(intent);
            VerifyActivity.this.finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

}