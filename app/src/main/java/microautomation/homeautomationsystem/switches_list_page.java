package microautomation.homeautomationsystem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class switches_list_page extends AppCompatActivity {
    FirebaseDatabase db;
    DatabaseReference root_ref;
    ArrayList<switch_model> switch_modelArrayList;
    ListView list_of_switches;
    int i=0;
    ArrayList<String> keys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switches_list_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        switch_modelArrayList=new ArrayList<>();
        keys=new ArrayList<>();
        list_of_switches=findViewById(R.id.switch_list);
        db=FirebaseDatabase.getInstance();
        root_ref=db.getReference("Devices");
        root_ref.child(getIntent().getStringExtra("device_name")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                switch_modelArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    switch_model model=snapshot.getValue(switch_model.class);
                    switch_modelArrayList.add(model);
                    keys.add(i,snapshot.getKey());
                    i++;
                }
                if(switch_modelArrayList.size()>0&&keys.size()>0) {
                    list_of_switches.setAdapter(new switches_list_adapter(switch_modelArrayList, switches_list_page.this, keys, getIntent().getStringExtra("device_name")));
                } else{
                    Toast.makeText(switches_list_page.this,"No Switches Found",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(switches_list_page.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
