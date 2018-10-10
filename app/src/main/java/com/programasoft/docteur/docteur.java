package com.programasoft.docteur;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * Created by ASUS on 07/10/2018.
 */

public class docteur {
    private String id;
    private String nom;
    private String image;

    public docteur() {
    }

    public docteur(String nom, String image) {
        this.nom = nom;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void ajoute(String email, String motde_passe, final Context context, final CircularProgressButton button)
    {

        FirebaseAuth auth=FirebaseAuth.getInstance();
        final DatabaseReference databaseReference_docteurs = FirebaseDatabase.getInstance().getReference().child("docteurs");
        button.startAnimation();
        auth.createUserWithEmailAndPassword(email,motde_passe).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if(task.isSuccessful())
              {      databaseReference_docteurs.push().setValue(docteur.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                      public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful())
                         {   Intent i=new Intent(context,MainActivity.class);
                             i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                             context.startActivity(i);
                             button.stopAnimation();

                          }else
                         {   button.revertAnimation();
                             button.setBackgroundResource(R.drawable.rectanglebutton);
                             Toast.makeText(context,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();
                         }
                        }
                      });

              }else
              {   button.revertAnimation();
                  button.setBackgroundResource(R.drawable.rectanglebutton);
                  Toast.makeText(context,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();
              }
            }
        });

    }
}
