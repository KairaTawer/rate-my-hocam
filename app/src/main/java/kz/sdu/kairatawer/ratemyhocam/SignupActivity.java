package kz.sdu.kairatawer.ratemyhocam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class SignupActivity extends AppCompatActivity {

    private ShimmerTextView mShimmerTextView;
    private EditText mEmail, mPassword, mConfirmPassword;
    private Button mLogin, mSignup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Shimmer mShimmer;

    String email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        mShimmerTextView = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        mEmail = (EditText) findViewById(R.id.et_email);
        mPassword = (EditText) findViewById(R.id.et_password);
        mConfirmPassword = (EditText) findViewById(R.id.et_retype_password);
        mLogin = (Button) findViewById(R.id.btn_login);
        mSignup = (Button) findViewById(R.id.btn_signup);

        mShimmer = new Shimmer();
        mShimmer.start(mShimmerTextView);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(signupIntent);
            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignup();
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
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        confirmPassword = mConfirmPassword.getText().toString();

        if(!password.equals(confirmPassword)) {
            //Toast.makeText(SignupActivity.this,"",Toast.LENGTH_LONG).show();
            mConfirmPassword.setError("Passwords must be the same");
            return false;
        } else if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(SignupActivity.this,"Fields are empty",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

}
