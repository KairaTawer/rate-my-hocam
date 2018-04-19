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
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.databinding.ActivitySignupBinding;
import kz.sdu.kairatawer.ratemyhocam.models.Users;
import kz.sdu.kairatawer.ratemyhocam.ui.AuthDialog;

public class SignupActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ActivitySignupBinding binding;

    String email, password;
    int facultyId = 999999,graduateYear = 999999;
    private static final String[] FACULTY_LIST = {
            "Инженерия и естественные науки",
            "Юрисприденция и социальные науки",
            "Педагогика и гуманитарные науки",
            "Бизнес-школа"
    };

    AuthDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);

        init();

        fillSpinners();
    }

    private void fillSpinners() {

        binding.spinnerGraduateYear.setItems(2000,2001,2002,2003,2004,2005,2006,2007,2008,2009,2010,2011,2012,2013,2014,2015,2016,2017,2018,2019,2020,2021,2022,2023,2024);
        binding.spinnerGraduateYear.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                graduateYear = (int) item;
            }
        });

        binding.spinnerFaculty.setItems(FACULTY_LIST);
        binding.spinnerFaculty.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                facultyId = (int) id;
            }
        });
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        binding.setSignUpClicker(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignup();
            }
        });

        binding.setLogInClicker(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                                        .child(user.getUid()).setValue(new Users(graduateYear,facultyId,false));

                                Intent mainIntent = new Intent(SignupActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                            } else {
                                mDialog = new AuthDialog();
                                String message = "Проблемы с интернетом.";

                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    message = "Слишком слабый пароль (минимум 6 символов).";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    message = "Неверный формат почты.";
                                } catch(Exception e) {
                                    message = "Почта уже зарегистрирована.";
                                }
                                mDialog.showDialog(SignupActivity.this, message);
                            }
                        }
                    });
        }

    }

    private Boolean startValidate() {
        email = binding.etEmail.getText().toString();
        password = binding.etPassword.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || facultyId == 999999 || graduateYear == 999999) {
            Toast.makeText(SignupActivity.this,"Все поля обязательны.",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
