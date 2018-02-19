package kz.sdu.kairatawer.ratemyhocam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ActivitySignupBinding binding;

    String email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        binding.setSignUpClicker(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignup();
            }
        });

        binding.setLogInClicker(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logInIntent = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(logInIntent);
            }
        });

  }

    private void startSignup() {

        if(startValidate()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent mainIntent = new Intent(SignupActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                            } else {
                                Toast.makeText(SignupActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private Boolean startValidate() {
        email = binding.etEmail.getText().toString();
        password = binding.etPassword.getText().toString();
        confirmPassword = binding.etRetypePassword.getText().toString();

        if(!password.equals(confirmPassword)) {
            binding.etRetypePassword.setError("Passwords must be the same");
            return false;
        } else if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(SignupActivity.this,"Fields are empty",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
