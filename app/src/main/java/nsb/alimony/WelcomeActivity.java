package nsb.alimony;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPreferences preferences = getSharedPreferences("language", MODE_PRIVATE);
        String lang = preferences.getString("lang", "ar");

        Locale locale = new Locale(lang);

        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        ImageView welcomeImageView = findViewById(R.id.imageViewWelcome);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome_anim);
        welcomeImageView.startAnimation(animation);

        new Handler().postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent = new Intent();
            if (user != null) {
                String nid = user.getDisplayName();
                if (user.isEmailVerified() && nid != null) {
                    int number = Integer.parseInt(nid.substring(12, 13));
                    if (number % 2 == 0) {
                        intent.setClass(WelcomeActivity.this, FemaleHomeActivity.class);
                    } else {
                        intent.setClass(WelcomeActivity.this, MaleHomeActivity.class);
                    }

                } else {
                    intent.setClass(WelcomeActivity.this, VerifyActivity.class);
                }
            } else {
                intent.setClass(WelcomeActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            WelcomeActivity.this.finishAffinity();
        }, 5000);

    }

}