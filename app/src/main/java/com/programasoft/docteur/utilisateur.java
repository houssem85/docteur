package com.programasoft.docteur;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

public class utilisateur {
    private String id;
    private String nom;
    private String image;

    public utilisateur() {
    }

    public utilisateur( String nom, String image) {

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

    public void ajoute(String Email, String mot_de_pasee, final Context context,final CircularProgressButton button)
    {


        final FirebaseAuth auth=FirebaseAuth.getInstance();
        final DatabaseReference database_utilisateurs=FirebaseDatabase.getInstance().getReference().child("utilisateurs");
        button.startAnimation();
        auth.createUserWithEmailAndPassword(Email,mot_de_pasee).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {   database_utilisateurs.child(auth.getCurrentUser().getUid()).setValue(utilisateur.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                      public void onComplete(@NonNull Task<Void> task) {

                         if(task.isSuccessful())
                        {   button.stopAnimation();
                            Intent i=new Intent(context,MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(i);


                        }else
                            {Toast.makeText(context,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();
                             button.revertAnimation();
                             button.setBackgroundResource(R.drawable.rectanglebutton);
                            }
                       }
                     });


                }else
                {  button.revertAnimation();
                   button.setBackgroundResource(R.drawable.rectanglebutton);
                    Toast.makeText(context,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    public void ajoute(String id, final Context context, final CircularProgressButton button, final AlertDialog dialog)
    {DatabaseReference reference_utilisateurs=FirebaseDatabase.getInstance().getReference().child("utilisateurs");
     button.startAnimation();
     reference_utilisateurs.child(id).setValue(this).addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {
             if(task.isSuccessful())
             {dialog.cancel();

             }else
             {button.revertAnimation();
              button.setBackgroundResource(R.drawable.rectanglebutton);
              Toast.makeText(context,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();
             }

         }
     });

    }
}
