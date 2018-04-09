package kz.sdu.kairatawer.ratemyhocam.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.databinding.ActivitySignupBinding;
import kz.sdu.kairatawer.ratemyhocam.models.UserUtil;
import kz.sdu.kairatawer.ratemyhocam.ui.AuthDialog;

public class SignupActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ActivitySignupBinding binding;

    String email, password;
    int facultyId = 999999,graduateYear = 999999;

    AuthDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
                //Intent logInIntent = new Intent(SignupActivity.this,LoginActivity.class);
                //startActivity(logInIntent);
                onBackPressed();
            }
        });

        binding.spinnerGraduateYear.setItems(2000,2001,2002,2003,2004,2005,2006,2007,2008,2009,2010,2011,2012,2013,2014,2015,2016,2017,2018,2019,2020,2021,2022,2023,2024);
        binding.spinnerGraduateYear.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                graduateYear = (int) item;
            }
        });

        binding.spinnerFaculty.setItems("Engineering and Natural Sciences","Law and Social Science","Education and Humanity","Business School");
        binding.spinnerFaculty.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                facultyId = (int) id;
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

                                mDatabase.child("Users")
                                        .child(user.getUid()).setValue(new UserUtil(graduateYear,facultyId,false));

                                Intent mainIntent = new Intent(SignupActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                            } else {
                                mDialog = new AuthDialog();
                                mDialog.showDialog(SignupActivity.this,task.getException().getMessage());
                            }
                        }
                    });
        }

    }

    private Boolean startValidate() {
        email = binding.etEmail.getText().toString();
        password = binding.etPassword.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || facultyId == 999999 || graduateYear == 999999) {
            Toast.makeText(SignupActivity.this,"All fields are required.",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
