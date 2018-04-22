package kz.sdu.kairatawer.ratemyhocam.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.models.SavedTeacher;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;

public class SavedTeachersActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView_teachers)
    RecyclerView mSavedTeachers;

    LinearLayoutManager layoutManager;

    FirebaseRecyclerAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mDatabase, teacherRef;

    String teacherId;
    Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_teachers);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(SavedTeachersActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Saved Teachers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        mSavedTeachers.setLayoutManager(layoutManager);

        adapter = getAdapter("name");

        mSavedTeachers.setAdapter(adapter);
    }

    private FirebaseRecyclerAdapter getAdapter(String filterOption) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("/SavedTeacher");
        teacherRef = FirebaseDatabase.getInstance().getReference().child("/Teacher");

        Query query = mDatabase.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<SavedTeacher> options =
                new FirebaseRecyclerOptions.Builder<SavedTeacher>()
                        .setQuery(query, SavedTeacher.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<SavedTeacher, ViewAllActivity.TeacherViewHolder>(options) {
            @NonNull
            @Override
            public ViewAllActivity.TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.teacher_list_item, parent, false);

                return new ViewAllActivity.TeacherViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final ViewAllActivity.TeacherViewHolder holder, final int position, @NonNull final SavedTeacher model) {
                teacherRef.orderByKey().equalTo(model.getTeacherId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                                    teacher = ds.getValue(Teacher.class);
                                    holder.setName(teacher.getName());
                                    holder.setRating(teacher.getRating());
                                    teacher.setId(ds.getKey());
                                    holder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(SavedTeachersActivity.this, TeacherProfileActivity.class);
                                            intent.putExtra("teacherId", ds.getKey());
                                            startActivity(intent);
                                        }
                                    });
                                    holder.mRateButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent rateIntent = new Intent(SavedTeachersActivity.this, RateTeacherActivity.class);
                                            rateIntent.putExtra("teacherId", ds.getKey());
                                            startActivity(rateIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };

        return adapter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        adapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        adapter.stopListening();
        super.onStop();
    }
}
