package dev.adrielgro.ride;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Context context = this;

    Button buttonLogin;
    EditText editTextEmail, editTextPassword;
    TextView textViewRegister;

    FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Remove notification bar
        setContentView(R.layout.activity_main);

        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        mProgress = new ProgressDialog(this);
        buttonLogin.setOnClickListener(this);
        //textViewRegister.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    Log.i("FIREBASE", "Sesion iniciada: " + user.getEmail());

                    Intent intent = new Intent(context, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.i("FIREBASE", "Sesion cerrada!");
                }
            }
        };
    }

    /*private void register(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.i("FIREBASE", "Usuario creado correctamente.");
                } else {
                    Log.e("FIREBASE", task.getException().getMessage().toString());
                }
            }
        });
    }*/

    private void login(String email, String password) {
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Log.e("FIREBASE", "Correo o contraseña vacios.");
        } else {
            mProgress.setMessage("Iniciando sesión, espera un momento...");
            mProgress.setCancelable(false);
            mProgress.show();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //mProgress.dismiss();
                    if(task.isSuccessful()) {
                        Log.i("FIREBASE", "Usuario logueado correctamente.");
                    } else {
                        Log.e("FIREBASE", task.getException().getMessage().toString());
                        Toast.makeText(context, "Usuario y/o contraseña incorrecta, intentalo de nuevo.", Toast.LENGTH_SHORT);
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        switch (v.getId()) {
            case R.id.buttonLogin:
                login(email, password);
                break;
            case R.id.textViewRegister:
                Intent intent = new Intent(context, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}
