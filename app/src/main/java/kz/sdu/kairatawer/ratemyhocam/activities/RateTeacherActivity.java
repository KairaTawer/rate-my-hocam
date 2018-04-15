package kz.sdu.kairatawer.ratemyhocam.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.BindView;
import butterknife.ButterKnife;
import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.models.Rating;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;

public class RateTeacherActivity extends AppCompatActivity {

    @BindView(R.id.nestedScrollView)
    NestedScrollView mContainer;
    @BindView(R.id.toolbar_actionbar)
    Toolbar mToolbar;
    @BindView(R.id.editText_courseCode)
    EditText mCourseCode;
    @BindView(R.id.ratingBar)
    RatingBar mRatingBar;
    @BindView(R.id.textView_ratingIndicator)
    TextView mRatingIndicator;
    @BindView(R.id.seekbar_difficulty)
    SeekBar mDifficulty;
    @BindView(R.id.textView_difficultyIndicator)
    TextView mDifficultyIndicator;
    @BindView(R.id.switch_attendance)
    ToggleSwitch mAttendanceSwitch;
    @BindView(R.id.switch_takeAgain)
    ToggleSwitch mTakeAgainSwitch;
    @BindView(R.id.editText_comment)
    EditText mComment;
    @BindView(R.id.textView_commentLength)
    TextView mCommentLength;
    @BindView(R.id.button_rateProfessor)
    Button mRateButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference ratingRef, teacherRef;

    Teacher teacher;
    String teacherId;
    List<String> ratingIndicators = new ArrayList<>(Arrays.asList("Awful", "Poor", "Average", "Good", "Awesome"));
    List<String> difficultyIndicators = new ArrayList<>(Arrays.asList("Effortless", "Easy", "Fair", "Tough", "Hard"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_teacher);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        ratingRef = database.getReference("Rating");
        teacherRef = database.getReference("Teacher");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(RateTeacherActivity.this, InitialActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mRatingIndicator.setText(ratingIndicators.get(Math.round(rating) - 1));
            }
        });

        mDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDifficultyIndicator.setText(difficultyIndicators.get(progress / 20));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setSupportActionBar(mToolbar);

        teacherId = getIntent().getStringExtra("teacherId");
        Query query = teacherRef.orderByKey().equalTo(teacherId);
        getSupportActionBar().setTitle("");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    teacher = ds.getValue(Teacher.class);
                    teacher.setId(ds.getKey());
                    getSupportActionBar().setTitle(teacher.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        query.addListenerForSingleValueEvent(eventListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mContainer.setSmoothScrollingEnabled(true);

        mRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitRating();
            }
        });

        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCommentLength.setText(String.valueOf(s.length()) + "/200");
                if(s.length() == 200) mCommentLength.setTextColor(Color.RED);
            }
        });

    }

    public void commitRating() {

        String courseCode = mCourseCode.getText().toString();
        float rating = mRatingBar.getRating();
        int difficulty = mDifficulty.getProgress() / 20;
        int attendance = mAttendanceSwitch.getCheckedTogglePosition();
        int takeAgain = mTakeAgainSwitch.getCheckedTogglePosition();
        String comment = mComment.getText().toString();

        Log.e("difficulty", difficulty + "");

        if(courseCode.isEmpty()) {
            scrollTo(mCourseCode);
            return;
        }
        if(comment.equals("") || TextUtils.isEmpty(comment)) {
            scrollTo(mComment);
            return;
        }

        String key =  ratingRef.push().getKey();

        Rating ratingRecord = new Rating(key, mAuth.getCurrentUser().getUid(), courseCode, comment, teacherId, rating, difficulty, attendance, takeAgain, 0);

        ratingRef.child(key).setValue(ratingRecord);//this creates the reqs key-value pair

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Thanks for rating!");
        builder.setMessage("Your rating is in processing, it may take an hour to display");
        builder.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        builder.show();

    }

    public void scrollTo(final View view) {
        mContainer.post(new Runnable() {
            @Override
            public void run() {
                mContainer.scrollTo(0, view.getBottom());
                Log.e("Empty Zone", view.getClass().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
