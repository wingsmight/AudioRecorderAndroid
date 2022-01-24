package com.wingsmight.audiorecorder.ui.login;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wingsmight.audiorecorder.CloudDatabase;
import com.wingsmight.audiorecorder.CloudStoragePlan;
import com.wingsmight.audiorecorder.MainActivity;
import com.wingsmight.audiorecorder.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{
    EditText name,surname,email,password,confirmedPassword;
    TextView birthdate;
    Button mRegisterbtn;
    TextView mLoginPageBack;
    FirebaseAuth mAuth;
    DatabaseReference mdatabase;
    String Name,Surname,Email,Password,ConfirmedPassword;
    Date Birthdate = new Date(2001, 1, 1);
    ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        email = (EditText)findViewById(R.id.editEmail);
        name = (EditText)findViewById(R.id.editName);
        surname = (EditText)findViewById(R.id.editSurname);
        birthdate = (TextView) findViewById(R.id.valueBirthdate);
        password = (EditText)findViewById(R.id.editPassword);
        confirmedPassword = (EditText)findViewById(R.id.editConfirmPassword);
        mRegisterbtn = (Button)findViewById(R.id.buttonRegister);
        mLoginPageBack = (TextView)findViewById(R.id.buttonLogin);

        // for authentication using FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        mRegisterbtn.setOnClickListener(this);
        mLoginPageBack.setOnClickListener(this);
        mDialog = new ProgressDialog(this);
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.wingsmight.audiorecorder.ui.login.DatePicker mDatePickerDialogFragment = new com.wingsmight.audiorecorder.ui.login.DatePicker();
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            // Start home activity
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));

            finish();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.SHORT).format(mCalendar.getTime());
        birthdate.setText(selectedDate);
        Birthdate = new Date(year, month, dayOfMonth);
    }

    @Override
    public void onClick(View v) {
        if (v==mRegisterbtn){
            UserRegister();
        }else if (v== mLoginPageBack){
        startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
        }
    }

    private void UserRegister() {
        Name = name.getText().toString().trim();
        Surname = surname.getText().toString().trim();
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();
        ConfirmedPassword = confirmedPassword.getText().toString().trim();

        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(SignUpActivity.this, "Введите имя", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Surname)) {
            Toast.makeText(SignUpActivity.this, "Введите фамилию", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isValidEmail(Email)) {
            Toast.makeText(SignUpActivity.this, "Введите корректную почту", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Password)) {
            Toast.makeText(SignUpActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(ConfirmedPassword)) {
            Toast.makeText(SignUpActivity.this, "Подтвердите пароль", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Password.equals(ConfirmedPassword)) {
            Toast.makeText(SignUpActivity.this, "Введенные пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }  else if (Password.length() < 6) {
            Toast.makeText(SignUpActivity.this,"Пароль слишком короткий",Toast.LENGTH_SHORT).show();
            return;
        }
        mDialog.setMessage("Создание пользователя, пожалуйста, подождите...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    OnAuth(task.getResult().getUser());
                    mDialog.dismiss();
                }else{
                    Exception exception = task.getException();
                    String localizedMessage = exception.getLocalizedMessage();

                    Toast.makeText(SignUpActivity.this,localizedMessage,Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
    }

    private void OnAuth(FirebaseUser user) {
        createAnewUser(user.getUid());

        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
    }

    private void createAnewUser(String uid) {
        User user = BuildNewuser();
        mdatabase.child(uid).setValue(user);
    }


    private User BuildNewuser(){
        User user = new User(
                getUserName(),
                getUserSurname(),
                getUserEmail(),
                getBirthdate(),
                new Date().getTime(),
                CloudStoragePlan.getSize(CloudStoragePlan.Plan.free200MB)
        );

        saveUserToPreferences(user);

        CloudDatabase.addUser(user);

        return user;
    }

    private void saveUserToPreferences(User user) {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("userFullName", user.Surname + " " + user.Name);
        editor.putString("birthDate", DateFormat.getDateInstance(DateFormat.SHORT).format(user.getBirthDate()));
        editor.putString("email", user.getEmail());

        editor.commit();
    }

    public String getUserName() {
        return Name;
    }

    public String getUserSurname() {
        return Surname;
    }

    public String getUserEmail() {
        return Email;
    }

    public Date getBirthdate() {
        return Birthdate;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
