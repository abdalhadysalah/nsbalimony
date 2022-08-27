package nsb.alimony;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        EditText nidEditText = findViewById(R.id.editTextNId);
        Button addButton = findViewById(R.id.buttonAdd);

        addButton.setOnClickListener(view -> {
            String nid = nidEditText.getText().toString().trim();

            if (nid.isEmpty()) {
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nid.length() != 14) {
                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                return;
            }

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

                    if (task.getResult().exists()) {

                        Toast.makeText(AdminActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    } else  {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", "");
                        data.put("name", "");
                        data.put("phone", "");
                        data.put("password", "");

                        FirebaseFirestore.getInstance().collection(finalCollection).document(nid).set(data);

                        Toast.makeText(AdminActivity.this, "Done", Toast.LENGTH_SHORT).show();

                        nidEditText.setText("");
                    }

                }
            });




        });


    }
}