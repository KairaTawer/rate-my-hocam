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
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.fragments.ExploreFragment;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;

public class ViewAllActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView mRecyclerViewAll;

    FirebaseRecyclerAdapter adapter;

    String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        mToolbar = findViewById(R.id.toolbar_actionbar);
        mRecyclerViewAll = findViewById(R.id.recyclerView_all);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Engineering Faculty");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerViewAll.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("/Teacher");

        Query query = mDatabase.orderByChild("rating");

        FirebaseRecyclerOptions<Teacher> options =
                new FirebaseRecyclerOptions.Builder<Teacher>()
                        .setQuery(query, Teacher.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Teacher, TeacherViewHolder>(options) {
            @NonNull
            @Override
            public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.teacher_list_item, parent, false);

                return new TeacherViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull TeacherViewHolder holder, int position, @NonNull Teacher model) {
                holder.setName(model.getName());
                holder.setRating(model.getRating() + "");
                holder.setPosition(model.getPosition());
                teacherId = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ViewAllActivity.this, TeacherProfileActivity.class);
                        intent.putExtra("teacherId", teacherId);
                        startActivity(intent);
                    }
                });

            }
        };

        adapter.notifyDataSetChanged();
        mRecyclerViewAll.setAdapter(adapter);
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public Button mRateButton;
        public TeacherViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mRateButton = mView.findViewById(R.id.button_rate_teacher);
        }

        public void setRating(String rating) {
            TextView mRating = mView.findViewById(R.id.textView_teacher_rating);

            mRating.setText(rating);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        adapter.startListening();
        super.onStart();
    }

    @Override
    public void onStop() {
        adapter.stopListening();
        super.onStop();
    }
}
