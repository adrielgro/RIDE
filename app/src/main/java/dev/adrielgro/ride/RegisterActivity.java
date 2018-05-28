package dev.adrielgro.ride;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        editTextName = (EditText) findViewById(R.id.editTextRegisterName);
        editTextEmail = (EditText) findViewById(R.id.editTextRegisterEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextRegisterPassword);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

         buttonRegister.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String full_name = editTextName.getText().toString();
                 String email = editTextEmail.getText().toString();
                 String password = editTextPassword.getText().toString();

                 if(!TextUtils.isEmpty(full_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                     mRegProgress.setTitle("Registrando cuenta...");
                     mRegProgress.setMessage("Espera un momento mientras se crea tu cuenta.");
                     mRegProgress.setCanceledOnTouchOutside(false);
                     mRegProgress.show();
                     register(full_name, email, password);
                 }


             }
         });
    }

    private void register(final String full_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mRegProgress.dismiss();
                if(task.isSuccessful()) {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", full_name);
                    userMap.put("status_drive", "0");

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });
                } else {
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "No se pudo iniciar sesión, verifica tu usuario/contraseña e intentalo de nuevo.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
