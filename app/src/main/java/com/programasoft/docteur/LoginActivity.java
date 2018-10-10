package com.programasoft.docteur;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
private EditText txt_mail;
private EditText txt_mot_de_passe;
private TextView txt_recuper_mot_de_passe;
private CircularProgressButton btn_login;
private FirebaseAuth auth;
private ProgressDialog dialog;
private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inialisation_component();
        initialisation_firebase();
        txt_recuper_mot_de_passe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final String email=txt_mail.getText().toString();
               if(email.isEmpty()==false)
               {   dialog.show();
                   auth.sendPasswordResetEmail(email)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   dialog.dismiss();
                                   if (task.isSuccessful()) {
                                       Toast.makeText(LoginActivity.this,"Veuillez vérifier votre boîte de réception",Toast.LENGTH_SHORT).show();
                                   }else
                                   {
                                       Toast.makeText(LoginActivity.this,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
               }else{Toast.makeText(LoginActivity.this,"Veuillez entrer l'adresse e-mail avec laquelle vous vous êtes enregistré(e)",Toast.LENGTH_SHORT).show();}
            }
        });
    }
    public void inialisation_component()
    {txt_mail=(EditText)this.findViewById(R.id.txt_email);
     txt_mot_de_passe=(EditText)this.findViewById(R.id.txt_mot_de_passe);
     txt_recuper_mot_de_passe=(TextView)this.findViewById(R.id.txt_recuper_mot_de_passe);
     btn_login=(CircularProgressButton)this.findViewById(R.id.btn_login);
     dialog=new ProgressDialog(this);
     dialog.setMessage("Chargement...");
     dialog.setCancelable(false);
    }
    public void initialisation_firebase()
    {auth=FirebaseAuth.getInstance();
     auth.setLanguageCode("fr");
    }
    public void initialisation_facebook()
    {
        callbackManager = CallbackManager.Factory.create();
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("hhh", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("hhh", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=auth.getCurrentUser();
        if(user!=null)
        { Intent i=new Intent(this,MainActivity.class);
          i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(i);

        }
    }

    public void ajoute_utilisateur(View view) {
        Intent i=new Intent(this,InscriptionActivity.class);
        this.startActivity(i);
    }

    public void login_facebook(View view) {
        initialisation_facebook();

    }

    public void login_google(View view) {
    }

    public void login_email(View view) {
       String Email=txt_mail.getText().toString();
       String mot_de_passe=txt_mot_de_passe.getText().toString();
       if(Email.isEmpty()==false || mot_de_passe.isEmpty()==false)
       {    btn_login.startAnimation();
             auth.signInWithEmailAndPassword(Email,mot_de_passe).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                     if(task.isSuccessful())
                     {   btn_login.stopAnimation();
                         Intent i=new Intent(LoginActivity.this,MainActivity.class);
                         i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         LoginActivity.this.startActivity(i);

                     }else
                     {   Toast.makeText(LoginActivity.this,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();
                         btn_login.revertAnimation();
                         btn_login.setBackgroundResource(R.drawable.rectanglebutton);
                     }
                  }
              });

       }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = auth.getCurrentUser();
                            Intent i=new Intent(LoginActivity.this,MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            LoginActivity.this.startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
