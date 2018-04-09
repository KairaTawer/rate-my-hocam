package kz.sdu.kairatawer.ratemyhocam.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.ui.AuthDialog;

public class LoginActivity extends AppCompatActivity {

    AuthDialog mDialog;

    @BindView(R.id.reg_progress)
    ProgressBar mProgress;
    @BindView(R.id.et_email)
    TextView mEmail;
    @BindView(R.id.et_password)
    TextView mPassword;
    @BindView(R.id.button_signIn)
    Button mSignIn;
    @BindView(R.id.button_signUp)
    Button mSignUp;
    @BindView(R.id.imageView_logo)
    ImageView mLogo;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setSupportActionBar(null);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignupActivity.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(LoginActivity.this, Pair.create((View) mLogo, "imageTransition"));
                    startActivity(signUpIntent, options.toBundle());
                    return;
                }
                startActivity(signUpIntent);
            }
        });
    }

    private void startLogin() {

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgress.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mProgress.setVisibility(View.INVISIBLE);
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                            } else {
                                mProgress.setVisibility(View.INVISIBLE);
                                mDialog = new AuthDialog();
                                mDialog.showDialog(LoginActivity.this,task.getException().getMessage());
                            }
                        }
                    });
        } else {
            Toast.makeText(LoginActivity.this, "Fields are empty.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
