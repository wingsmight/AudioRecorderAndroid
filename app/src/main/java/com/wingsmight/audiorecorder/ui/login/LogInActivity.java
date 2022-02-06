package com.wingsmight.audiorecorder.ui.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wingsmight.audiorecorder.CloudDatabase;
import com.wingsmight.audiorecorder.CloudStoragePlan;
import com.wingsmight.audiorecorder.MainActivity;
import com.wingsmight.audiorecorder.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;


public class LogInActivity extends AppCompatActivity {
    EditText Email, Password;
    Button LogInButton, RegisterButton;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    FirebaseUser mUser;
    String email, password;
    ProgressDialog dialog;
    public static final String userEmail="";

    public static final String TAG="LOGIN";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        LogInButton = (Button) findViewById(R.id.buttonLogin);
        RegisterButton = (Button) findViewById(R.id.buttonRegister);
        Email = (EditText) findViewById(R.id.editEmail);
        Password = (EditText) findViewById(R.id.editPassword);
        dialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mUser != null) {
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else
                {
                    Log.d(TAG,"AuthStateChanged:Logout");
                }

            }
        };
       // LogInButton.setOnClickListener((View.OnClickListener) this);
        //RegisterButton.setOnClickListener((View.OnClickListener) this);
        //Adding click listener to log in button.
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling EditText is empty or no method.
                userSign();


            }
        });

        // Adding click listener to register button.
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //removeAuthSateListner is used  in onStart function just for checking purposes,it helps in logging you out.
        mAuth.removeAuthStateListener(mAuthListner);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListner != null) {
            mAuth.removeAuthStateListener(mAuthListner);
        }

    }

    @Override
    public void onBackPressed() {
        LogInActivity.super.finish();
    }



    private void userSign() {
        email = Email.getText().toString().trim();
        password = Password.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LogInActivity.this, "Введен некорректная почта", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(LogInActivity.this, "Введен некорректный пароль", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.setMessage("Производится вход, пожалуйста, подождите...");
        dialog.setIndeterminate(true);
        dialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    dialog.dismiss();

                    Toast.makeText(LogInActivity.this, "Вход не удался, попытайтесь снова", Toast.LENGTH_SHORT).show();

                } else {
                    dialog.dismiss();

                    CloudDatabase.loadUser(email, new Consumer<Map<String, Object>>() {
                        @Override
                        public void accept(Map<String, Object> userData) {
                            User user = new User(
                                    (String)userData.get("name"),
                                    (String)userData.get("surname"),
                                    (String)userData.get("email"),
                                    new Date(),
                                    new Date().getTime(),
                                    ((Long)userData.get("storageSize")).intValue()
                            );
                            saveUserToPreferences(user);

                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            intent.putExtra(userEmail,email);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
    private void saveUserToPreferences(User user) {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("userFullName", user.Surname + " " + user.Name);
        editor.putString("birthDate", DateFormat.getDateInstance(DateFormat.SHORT).format(user.getBirthDate()));
        editor.putString("email", user.getEmail());

        editor.commit();
    }
}

