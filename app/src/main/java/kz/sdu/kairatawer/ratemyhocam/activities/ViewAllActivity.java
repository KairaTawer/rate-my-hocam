package kz.sdu.kairatawer.ratemyhocam.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.fragments.ExploreFragment;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;

public class ViewAllActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_actionbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView_all)
    RecyclerView mRecyclerViewAll;
    @BindView(R.id.spinner_filter)
    Spinner mFilterSpinner;

    FirebaseRecyclerAdapter alphabetAdapter, ratingAdapter;

    String teacherId;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Engineering Faculty");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        mRecyclerViewAll.setLayoutManager(layoutManager);

        alphabetAdapter = getAdapter("name");

        mRecyclerViewAll.setAdapter(alphabetAdapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFilterSpinner.setAdapter(adapter);

        mFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Rating")) {
                    if(ratingAdapter == null) {
                        ratingAdapter = getAdapter("rating");
                        ratingAdapter.startListening();
                    }

                    reverseRecycler(true);
                    mRecyclerViewAll.setAdapter(ratingAdapter);
                } else {
                    reverseRecycler(false);
                    mRecyclerViewAll.setAdapter(alphabetAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void reverseRecycler(boolean toReverse) {

        layoutManager.setReverseLayout(toReverse);
        layoutManager.setStackFromEnd(toReverse);
        mRecyclerViewAll.setLayoutManager(layoutManager);

    }

    private FirebaseRecyclerAdapter getAdapter(String filterOption) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("/Teacher");

        Query query = mDatabase.orderByChild(filterOption);

        FirebaseRecyclerOptions<Teacher> options =
                new FirebaseRecyclerOptions.Builder<Teacher>()
                        .setQuery(query, Teacher.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Teacher, TeacherViewHolder>(options) {
            @NonNull
            @Override
            public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.teacher_list_item, parent, false);

                return new TeacherViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final TeacherViewHolder holder, int position, @NonNull final Teacher model) {
                holder.setName(model.getName());
                holder.setRating(model.getRating());
                holder.setPosition(model.getPosition());
                teacherId = getRef(position).getKey();
                model.setId(teacherId);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ViewAllActivity.this, TeacherProfileActivity.class);
                        intent.putExtra("teacherId", model.getId());
                        startActivity(intent);
                    }
                });
                holder.mRateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent rateIntent = new Intent(ViewAllActivity.this, RateTeacherActivity.class);
                        rateIntent.putExtra("teacherId", model.getId());
                        startActivity(rateIntent);
                    }
                });

            }
        };

        return adapter;
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public Button mRateButton;
        public TeacherViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mRateButton = mView.findViewById(R.id.button_rate_teacher);
        }

        public void setRating(float rating) {
            TextView mRating = mView.findViewById(R.id.textView_teacher_rating);

            if(rating != 0.0) mRating.setText(String.format("%.1f", rating));
        }

        public void setName(String name) {
            TextView mRating = mView.findViewById(R.id.textView_teacher_name);

            mRating.setText(name);
        }

        public void setPosition(String position) {
            TextView mPosition = mView.findViewById(R.id.textView_teacher_position);

            mPosition.setText(position);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            case R.id.action_filter:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        alphabetAdapter.startListening();
        if(ratingAdapter != null) ratingAdapter.startListening();
        super.onStart();
    }

    @Override
    public void onStop() {
        alphabetAdapter.stopListening();
        if(ratingAdapter != null) ratingAdapter.stopListening();
        super.onStop();
    }
}
