package microautomation.homeautomationsystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import fr.ganfra.materialspinner.MaterialSpinner;

public class Admin_page extends AppCompatActivity {
Button add_user,add_devices;
FirebaseDatabase db;
DatabaseReference root_ref;
DatabaseReference user_ref;
SharedPreferences prefs;
FirebaseAuth auth;
int count=0;
ProgressDialog progressDialog;
    ArrayList<String> keylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait....");
        keylist=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        root_ref=db.getReference("Devices");
        user_ref=db.getReference("Users");
        prefs=PreferenceManager.getDefaultSharedPreferences(this);
        add_user=findViewById(R.id.add_user);
        add_devices=findViewById(R.id.add_devices);
        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View add_user_view=LayoutInflater.from(Admin_page.this).inflate(R.layout.add_user_layout,null);
                final TextInputEditText email=add_user_view.findViewById(R.id.email_txt);
                final TextInputEditText password=add_user_view.findViewById(R.id.password_txt);
                final MaterialSpinner select_user_type=add_user_view.findViewById(R.id.select_user_type);
                String[] items={"Customer","Dependent"};
                select_user_type.setAdapter(new ArrayAdapter<String>(Admin_page.this,android.R.layout.simple_dropdown_item_1line,items));
                android.app.AlertDialog add_user_dialog=new android.app.AlertDialog.Builder(Admin_page.this)
                        .setTitle("Add User")
                        .setCancelable(false)
                        .setMessage("Provide Credinals for adding user to System")
                        .setView(add_user_view)

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
                   add_user_dialog.show();
                  add_user_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          progressDialog.show();
                          if(select_user_type.getSelectedItem()==null){
                              select_user_type.setError("Select User Type");
                          }else if(email.getText().toString().isEmpty()){
                              email.setError("Email is Required");
                          }else if(password.getText().toString().isEmpty()){
                              password.setError("Password is required");
                          }else if(password.getText().toString().length()<6){
                              password.setError("Password too short");
                          }else{
                              auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                  @Override
                                  public void onComplete(@NonNull Task<AuthResult> task) {
                                      if(task.isSuccessful()){
                                          if(progressDialog.isShowing())
                                              progressDialog.dismiss();
                                          users u=new users(select_user_type.getSelectedItem().toString());
                                          user_ref.child(email.getText().toString().substring(0,email.getText().toString().indexOf("."))).child("User_Role").push().setValue(u).addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void aVoid) {
                                                  Toast.makeText(Admin_page.this,"User is Added to the System",Toast.LENGTH_LONG).show();
                                              }
                                          }).addOnFailureListener(new OnFailureListener() {
                                              @Override
                                              public void onFailure(@NonNull Exception e) {
                                                  Toast.makeText(Admin_page.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                              }
                                          });

                                      }
                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      if(progressDialog.isShowing())
                                          progressDialog.dismiss();
                                      Toast.makeText(Admin_page.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                  }
                              });
                          }
                      }
                  });
            }
        });
        add_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View add_device_view=LayoutInflater.from(Admin_page.this).inflate(R.layout.add_device_layout,null);
                final TextInputEditText email=add_device_view.findViewById(R.id.email_txt);
                final TextInputEditText device=add_device_view.findViewById(R.id.device_txt);
                final TextInputEditText switch_name=add_device_view.findViewById(R.id.switch_txt);
                android.app.AlertDialog add_device_dialog=new android.app.AlertDialog.Builder(Admin_page.this)
                 .setTitle("Add Device")
                  .setCancelable(false)
                 .setMessage("Don't Repeat Device name for same User")
                  .setView(add_device_view)
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
                    add_device_dialog.show();
                add_device_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        progressDialog.show();
                        if(switch_name.getText().toString().isEmpty()) {
                            switch_name.setError("Switch Name is Required");
                        }else if(email.getText().toString().isEmpty()){
                            email.setError("Email is Required");
                        }else if(device.getText().toString().isEmpty()){
                            device.setError("Device name is required");
                        }else{
                            switch_model model=new switch_model(switch_name.getText().toString(),0);
                            root_ref.child(email.getText().toString().substring(0,email.getText().toString().indexOf("."))+"_"+device.getText().toString()).push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                       if(progressDialog.isShowing())
                                           progressDialog.dismiss();
                                       Toast.makeText(Admin_page.this,"Device Added",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    Toast.makeText(Admin_page.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
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
            startActivity(new Intent(Admin_page.this,login_page.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
