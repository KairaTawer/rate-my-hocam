package kz.sdu.kairatawer.ratemyhocam.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;
import kz.sdu.kairatawer.ratemyhocam.models.Users;

public class TeacherProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView_ratings)
    RecyclerView mRatingList;

    Teacher teacher;

    DatabaseReference teacherRef, ratingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        ButterKnife.bind(this);

        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void init() {

        teacherRef = FirebaseDatabase.getInstance().getReference("Teacher");
        ratingRef = FirebaseDatabase.getInstance().getReference("Rating");

        teacherRef.orderByChild("teacherId").equalTo(getIntent().getStringExtra("teacherId"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    teacher = ds.getValue(Teacher.class);
                    getSupportActionBar().setTitle(teacher.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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
