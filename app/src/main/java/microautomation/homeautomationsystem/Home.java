package microautomation.homeautomationsystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ArrayAdapter;

import android.widget.ExpandableListView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;

import fr.ganfra.materialspinner.MaterialSpinner;
public class Home extends AppCompatActivity {
 ExpandableListView switches_list;
 ArrayList<Device> device_list;
 SharedPreferences prefs;
 FirebaseDatabase db;
 DatabaseReference root_ref;
 ArrayList<String> names;
 ArrayList<Dependents> dependentsArrayList;
 TextView device_not_found;
 ProgressDialog pd;
 int i=0;
 int j=0;
 FirebaseAuth dependent_auth;
 DatabaseReference devices_ref;
 ActionBarDrawerToggle actionBarDrawerToggle;
 DrawerLayout drawerlayout;
 NavigationView nv;
 ArrayList<String> user_specific_devices;
    ArrayList<String> heading_list;
    HashMap<String,ArrayList<String>> childs_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        heading_list=new ArrayList<>();
        childs_list=new HashMap<>();
        heading_list.add("Owned Devices");
        heading_list.add("Child Devices");
        nv=findViewById(R.id.nav_view);
        dependent_auth=FirebaseAuth.getInstance();
        names=new ArrayList<>();
        pd=new ProgressDialog(this);
        pd.setMessage("Please Wait...");
        pd.setCancelable(false);
        user_specific_devices=new ArrayList<>();
        dependentsArrayList=new ArrayList<>();
        prefs=PreferenceManager.getDefaultSharedPreferences(this);
        drawerlayout=findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerlayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        switches_list=findViewById(R.id.switch_list);
        device_not_found=findViewById(R.id.no_devices_found);
        device_list=new ArrayList<>();
        db=FirebaseDatabase.getInstance();
        root_ref=db.getReference("Users");
        devices_ref=db.getReference("Devices");
       check_for_new_devices();
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.add_dependent){
                      add_dependent();
                }else if(menuItem.getItemId()==R.id.add_devices){
                    add_new_device();
                    }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            prefs.edit().remove("email").apply();
            prefs.edit().remove("user_id").apply();
            startActivity(new Intent(Home.this,login_page.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
        private void check_for_new_devices(){
        root_ref.child(prefs.getString("email","").substring(0,prefs.getString("email","").indexOf("."))).child("Devices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                device_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Device d=snapshot.getValue(Device.class);
                        device_list.add(d);
                }
                if (device_list.size()>0){
                       ArrayList<String> user_specific_devices=new ArrayList<>();
                    ArrayList<String> dependent_devices=new ArrayList<>();
                    ArrayList<String> user_own_devices=new ArrayList<>();
                       for (int i=0;i<device_list.size();i++){
                           user_specific_devices.add(i,device_list.get(i).getDevice_name());
                           if(user_specific_devices.get(i).contains(prefs.getString("email","").substring(0,prefs.getString("email","").indexOf(".")))){
                               user_own_devices.add(user_specific_devices.get(i));
                           }else{
                               dependent_devices.add(user_specific_devices.get(i));
                           }
                       }
                             childs_list.put(heading_list.get(1), dependent_devices);
                             childs_list.put(heading_list.get(0), user_own_devices);
                        if(childs_list!=null) {
                            switches_list.setAdapter(new device_list_adapter(heading_list, childs_list, Home.this));
                        }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Home.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
        private void add_dependent(){
        View send_profit_view=LayoutInflater.from(Home.this).inflate(R.layout.add_dependent,null);
        final TextInputEditText profit=send_profit_view.findViewById(R.id.profit_txt);
        TextInputLayout txt=send_profit_view.findViewById(R.id.profit_textinputlayout);
        AlertDialog send_profit_dialog=new AlertDialog.Builder(Home.this)
                .setTitle("Add Dependent")
                .setMessage("Enter Email for dependent")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNeutralButton("Already Have Dependent", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setView(send_profit_view).create();
        send_profit_dialog.show();
        send_profit_dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profit.getText().toString().isEmpty()){
                    profit.setError("Enter Email");

                }else {
                    get_all_registered_users_and_verify_entered_email_exist(profit);
                }
            }
        });
        send_profit_dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_already_dependent_dialog();
            }
        });
    }
        private void get_all_registered_users_and_verify_entered_email_exist(final TextInputEditText email){
       // final ArrayList<String> all_registered_users=new ArrayList<>();
        // j=0;
        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //  all_registered_users.clear();

                //all_registered_users.add(j,s.getKey());
                //j++;
                if (dataSnapshot.hasChild(email.getText().toString().substring(0, email.getText().toString().indexOf(".")))) {
                    check_already_dependent(email);
                } else {
                    email.setError("Invalid Email");
                }
                      /* for (int i=0;i<all_registered_users.size();i++){
                           if(all_registered_users.get(i).equals(email.getText().toString().substring(0,email.getText().toString().indexOf(".")))){
                               check_already_dependent(email);
                           }
                           Log.e("all_registered_users",all_registered_users.get(i));
                       }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Home.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
        private void check_already_dependent(final TextInputEditText email){
       // final ArrayList<Dependents> dependents_list=new ArrayList<Dependents>();
        //final ArrayList<String> dependents_id_list=new ArrayList<String>();
        FirebaseDatabase.getInstance().getReference("Users").child(prefs.getString("email","").substring(0,prefs.getString("email","").indexOf("."))).child("Dependents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // dependents_list.clear();
                if(dataSnapshot.getChildrenCount()>0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.child("dependents_id").getValue(String.class).equals(email.getText().toString())) {
                            email.setError("Dependent Already Exists");
                        }
                    }
                }else{
                    add_as_dependent_of_admin_user(email);
                }
               /* if(dependents_list.size()>0) {
                    for (int i = 0; i < dependents_list.size(); i++) {
                        dependents_id_list.add(i, dependents_list.get(i).getDependents_id());
                        if (dependents_id_list.get(i).equals(email.getText().toString().substring(0, email.getText().toString().indexOf(".")))) {
                            email.setError("Dependent Already Exists");
                        }

                    }
                }*/
                if(email.getError()==null){
                    add_as_dependent_of_admin_user(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Home.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
        private void add_as_dependent_of_admin_user(TextInputEditText email) {
        Dependents d=new Dependents(email.getText().toString());
        FirebaseDatabase.getInstance().getReference("Users").child(prefs.getString("email","").substring(0,prefs.getString("email","").indexOf("."))).child("Dependents").push().setValue(d).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Home.this,"Dependent Added Sucessfully",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Home.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
        private void add_new_device(){
        if(names.size()==0) {
            FirebaseDatabase.getInstance().getReference("Devices").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user_specific_devices.clear();
                    names.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        names.add(i, snapshot.getKey());
                        i++;
                    }
                    View add_user_view=LayoutInflater.from(Home.this).inflate(R.layout.add_devices_user,null);
                    final MaterialSpinner select_device=add_user_view.findViewById(R.id.select_device);
                    for(int i=0;i<names.size();i++) {
                        if (names.get(i).contains(prefs.getString("email", "").substring(0, prefs.getString("email", "").indexOf(".")))) {
                            user_specific_devices.add(names.get(i));
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Home.this, android.R.layout.simple_list_item_1, user_specific_devices);
                            select_device.setAdapter(adapter);
                        }
                    }
                        android.app.AlertDialog add_user_dialog=new android.app.AlertDialog.Builder(Home.this)
                                .setTitle("Add Devices")
                                .setCancelable(false)
                                .setMessage("Select Device Id")
                                .setView(add_user_view)
                                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        user_specific_devices.clear();
                                    }
                                }).create();
                        add_user_dialog.show();
                        add_user_dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(select_device.getSelectedItem()!=null) {
                                    for (int i = 0; i < device_list.size(); i++) {
                                        if (device_list.get(i).device_name.equals(select_device.getSelectedItem().toString())) {
                                            select_device.setError("Device Already Exist");
                                        }
                                    }
                                    if(select_device.getError()==null){
                                        Device d=new Device(select_device.getSelectedItem().toString());
                                        FirebaseDatabase.getInstance().getReference("Users").child(prefs.getString("email","").substring(0,prefs.getString("email","").indexOf("."))).child("Devices").push().setValue(d).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Home.this,"Device Added",Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Home.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }else{
                                    select_device.setError("Please Select Device to Add");
                                }

                            }
                        });
                    }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Home.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else {
            user_specific_devices.clear();
            View add_user_view = LayoutInflater.from(Home.this).inflate(R.layout.add_devices_user, null);
            final MaterialSpinner select_device = add_user_view.findViewById(R.id.select_device);
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).contains(prefs.getString("email", "").substring(0, prefs.getString("email", "").indexOf(".")))) {
                    user_specific_devices.add(names.get(i));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Home.this, android.R.layout.simple_list_item_1, user_specific_devices);
                    select_device.setAdapter(adapter);
                }
            }
                android.app.AlertDialog add_user_dialog = new android.app.AlertDialog.Builder(Home.this)
                        .setTitle("Add Devices")
                        .setCancelable(false)
                        .setMessage("Select Device Id")
                        .setView(add_user_view)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                user_specific_devices.clear();
                            }
                        }).create();
                add_user_dialog.show();
                add_user_dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (select_device.getSelectedItem() != null) {
                            for (int i = 0; i < device_list.size(); i++) {
                                if (device_list.get(i).device_name.equals(select_device.getSelectedItem().toString())) {
                                    select_device.setError("Device Already Exist");
                                }
                            }
                            if (select_device.getError() == null) {
                                Device d = new Device(select_device.getSelectedItem().toString());
                                root_ref.child(prefs.getString("email","").substring(0,prefs.getString("email","").indexOf("."))).child("Devices").push().setValue(d).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Home.this, "Device Added", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            select_device.setError("Please Select Device to Add");
                        }
                    }
                });
            }
        }
        private void show_already_dependent_dialog(){
        ArrayList<String> user_registered_devices=new ArrayList<>();
            ArrayList<String> user_own_devices=new ArrayList<>();
            View add_dependent_view=LayoutInflater.from(Home.this).inflate(R.layout.already_have_dependents_layout,null);
            final MaterialSpinner select_dependent=add_dependent_view.findViewById(R.id.select_dependent);
            final MaterialSpinner select_device=add_dependent_view.findViewById(R.id.select_device);
             for (int i=0;i<device_list.size();i++){
                 user_registered_devices.add(i,device_list.get(i).device_name);
                 if(user_registered_devices.get(i).contains(prefs.getString("email","").substring(0,prefs.getString("email","").indexOf(".")))){
                     user_own_devices.add(user_registered_devices.get(i));
                 }
             }
            select_device.setAdapter(new ArrayAdapter<String>(Home.this,android.R.layout.simple_dropdown_item_1line,user_own_devices));
            get_dependents_of_user(select_dependent);
                final AlertDialog add_devices_for_dependents =new AlertDialog.Builder(Home.this)
                        .setTitle("Add Devices for Dependent")
                        .setMessage("Select dependents and devices")
                        .setCancelable(false)
                        .setView(add_dependent_view)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                add_devices_for_dependents.show();
                add_devices_for_dependents.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(select_dependent.getSelectedItem()==null){
                            select_dependent.setError("Select Dependent");
                        }else if(select_device.getSelectedItem()==null){
                            select_device.setError("Select Device");
                        }else {
                            check_already_existing_devices_for_dependent(select_dependent, select_device);
                        }
                    }
                });
            }
        private void get_dependents_of_user(final MaterialSpinner select_dependent){
         final ArrayList<String> dependents_id=new ArrayList<>();

           FirebaseDatabase.getInstance().getReference("Users").child(prefs.getString("email", "").substring(0, prefs.getString("email", "").indexOf("."))).child("Dependents").addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   dependentsArrayList.clear();
                   for(DataSnapshot dependents_data:dataSnapshot.getChildren()){
                        Dependents dependents=dependents_data.getValue(Dependents.class);
                        dependentsArrayList.add(dependents);
                   }
                   for(int i=0;i<dependentsArrayList.size();i++){
                       dependents_id.add(i,dependentsArrayList.get(i).dependents_id);
                   }
                   select_dependent.setAdapter(new ArrayAdapter<String>(Home.this,android.R.layout.simple_dropdown_item_1line,dependents_id));
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                   Toast.makeText(Home.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
               }
           });


        }
        private void check_already_existing_devices_for_dependent(final MaterialSpinner select_dependents, final MaterialSpinner select_devices){

           FirebaseDatabase.getInstance().getReference("Users").child(select_dependents.getSelectedItem().toString().substring(0,select_dependents.getSelectedItem().toString().indexOf("."))).child("Devices").addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   for(DataSnapshot dependents_devices:dataSnapshot.getChildren()) {
                       /*Device dependents_registered_devicess=dependents_devices.getValue(Device.class);
                       dependent_devices_list.add(dependents_registered_devicess);*/
                       if (dependents_devices.getChildrenCount() > 0) {
                           if (dependents_devices.child("device_name").getValue(String.class).equals(select_devices.getSelectedItem().toString())) {
                               select_devices.setError("Device Already Exists");
                           }
                       }else{
                           add_devices_for_dependent(select_dependents, select_devices);
                       }
                   }
                   if(select_devices.getError()==null&&select_dependents.getError()==null){
                       add_devices_for_dependent(select_dependents, select_devices);
                   }
                  /* for(int i=0;i<dependent_devices_list.size();i++){
                       devices_of_dependents.add(i,dependent_devices_list.get(i).device_name);
                       if(devices_of_dependents.get(i).contains(select_devices.getSelectedItem().toString())){

                       }
                   }
                   if(select_devices.getError()==null&&select_dependents.getError()==null){

                   }*/
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                  Toast.makeText(Home.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
               }
           });

        }
        private void add_devices_for_dependent(MaterialSpinner select_dependent,MaterialSpinner select_device){
            Device dependent_device=new Device(select_device.getSelectedItem().toString());
           FirebaseDatabase.getInstance().getReference("Users").child(select_dependent.getSelectedItem().toString().substring(0,select_dependent.getSelectedItem().toString().indexOf("."))).child("Devices").push().setValue(dependent_device).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                   Toast.makeText(Home.this,"Device is Added",Toast.LENGTH_LONG).show();
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(Home.this,e.getMessage(),Toast.LENGTH_LONG).show();
               }
           });
        }
}
