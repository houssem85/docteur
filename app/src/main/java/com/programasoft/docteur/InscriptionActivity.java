package com.programasoft.docteur;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class InscriptionActivity extends AppCompatActivity {
    private EditText txt_nom;
    private EditText txt_mot_de_passe;
    private EditText txt_confirm_mot_de_passe;
    private EditText txt_email;
    private CircularProgressButton btn_inscription;
    private RadioButton docteur;
    private RadioButton Patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        initilisation_component();
    }
    private void initilisation_component()
    {txt_nom=(EditText)this.findViewById(R.id.txt_nom);
     txt_mot_de_passe=(EditText)this.findViewById(R.id.txt_mot_de_passe);
     txt_confirm_mot_de_passe=(EditText)this.findViewById(R.id.txt_confirm_mot_de_passe);
     txt_email=(EditText)this.findViewById(R.id.txt_email);
     docteur=(RadioButton)this.findViewById(R.id.docteur);
     Patient=(RadioButton)this.findViewById(R.id.patient);
     btn_inscription=(CircularProgressButton)this.findViewById(R.id.btn_insription);
    }

    public void inscription(View view) {
       String nom=txt_nom.getText().toString();
       String mot_de_passe=txt_mot_de_passe.getText().toString();
       String confirm_mot_de_passe=txt_confirm_mot_de_passe.getText().toString();
       String email=txt_email.getText().toString();

       if(docteur.isChecked()==true || Patient.isChecked()==true)
       {   if(email.isEmpty()==false && nom.isEmpty()==false && mot_de_passe.isEmpty()==false && confirm_mot_de_passe.isEmpty()==false)
           {    if(mot_de_passe.equals(confirm_mot_de_passe))
                 {   if(mot_de_passe.length()>=8) {

                        if(docteur.isChecked()==true)
                        { docteur d=new docteur(nom,"default");
                          d.ajoute(email,mot_de_passe,this,btn_inscription);


                        }else if(Patient.isChecked()==true)
                        {utilisateur u=new utilisateur(nom,"default");
                         u.ajoute(email,mot_de_passe,this,btn_inscription);

                        }

                     }else{Toast.makeText(this,"Utiliser au moin 8 caractere",Toast.LENGTH_SHORT).show();}
                 }else{Toast.makeText(this,"les mots de passe ne correspondent pas. veuillez réessayer",Toast.LENGTH_SHORT).show();}
           }else
           {Toast.makeText(this,"Tous les champs sont obligatoires",Toast.LENGTH_SHORT).show();
           }

       }else
       {Toast.makeText(this,"Choisir le type d 'utilisateur",Toast.LENGTH_SHORT).show();
       }
    }

    public void connectez_vous_ici(View view) {
        Intent i=new Intent(this,LoginActivity.class);
        this.startActivity(i);
    }
}
