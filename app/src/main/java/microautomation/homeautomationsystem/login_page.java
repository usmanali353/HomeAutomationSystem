package microautomation.homeautomationsystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import fr.ganfra.materialspinner.MaterialSpinner;

public class login_page extends AppCompatActivity {
TextInputEditText email,password;
Button btn;
FirebaseAuth auth;
FirebaseAuth add_user_auth;
ProgressDialog progressDialog;
SharedPreferences prefs;
TextView login_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        add_user_auth=FirebaseAuth.getInstance();
        prefs=PreferenceManager.getDefaultSharedPreferences(this);
        progressDialog=new ProgressDialog(login_page.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait....");
        email=findViewById(R.id.email_txt);
        password=findViewById(R.id.password_txt);
        btn=findViewById(R.id.btn);
        login_id=findViewById(R.id.login_id);
        login_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_user_to_firebase_auth();
            }
        });
      auth=FirebaseAuth.getInstance();
      if (prefs.getString("user_id","").equals("waCpFdWRxoZgCsb6k0WpObLMDS52")){
          Intent i = new Intent(login_page.this, Admin_page.class);
          startActivity(i);
          finish();
      }else if(!prefs.getString("user_id","").equals("")){
          Intent i = new Intent(login_page.this, Home.class);
          startActivity(i);
          finish();
      }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()){
                    email.setError("Email is Required");
                }else if(password.getText().toString().isEmpty()){
                    password.setError("Password is Required");
                }else if(password.getText().toString().length()<6){
                    password.setError("Password is too Short");
                }else{
                        progressDialog.show();
                        auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    Toast.makeText(login_page.this,"Login Sucessfully",Toast.LENGTH_LONG).show();
                                    FirebaseUser user=auth.getCurrentUser();
                                    prefs.edit().putString("email",user.getEmail()).apply();
                                    prefs.edit().putString("user_id",user.getUid()).apply();
                                    if(prefs.getString("user_id","").equals("waCpFdWRxoZgCsb6k0WpObLMDS52")){
                                        Intent i = new Intent(login_page.this, Admin_page.class);
                                        startActivity(i);
                                        finish();
                                    }else {
                                        Intent i = new Intent(login_page.this, Home.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Toast.makeText(login_page.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
        });

    }
    private void add_user_to_firebase_auth(){
        View add_dependent_view=LayoutInflater.from(login_page.this).inflate(R.layout.add_user_layout,null);
        final TextInputEditText email=add_dependent_view.findViewById(R.id.email_txt);
        final TextInputEditText password=add_dependent_view.findViewById(R.id.password_txt);
        MaterialSpinner select_user_type=add_dependent_view.findViewById(R.id.select_user_type);
        select_user_type.setVisibility(View.GONE);
        final AlertDialog add_dependent =new AlertDialog.Builder(login_page.this)
                .setTitle("Sign Up")
                .setMessage("Provide Information to Sign Up")
                .setCancelable(false)
                .setView(add_dependent_view)
                .setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        add_dependent.show();
        add_dependent.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()){
                    email.setError("Email is Required");
                }else if(password.getText().toString().isEmpty()){
                    password.setError("Password is required");
                }else if(password.getText().toString().length()<6){
                    password.setError("Password too short");
                }else{
                    add_user_auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Sign_up();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(login_page.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
            }
    private void Sign_up(){
        users u=new users("Customer");
        FirebaseDatabase.getInstance().getReference("Users").child(add_user_auth.getCurrentUser().getEmail().substring(0,add_user_auth.getCurrentUser().getEmail().indexOf("."))).child("User_Role").push().setValue(u).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(login_page.this,"Registration Sucess",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(login_page.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    }

