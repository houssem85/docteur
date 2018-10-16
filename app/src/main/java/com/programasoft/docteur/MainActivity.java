package com.programasoft.docteur;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
private Toolbar toolbar;
private ViewPager viewPager;
private TabLayout tabLayout;
private SectionPagerAdapter adapter;
private CircleImageView image_profil;
private TextView nom_profil;
private FirebaseAuth auth;
private DatabaseReference docteurs_ref;
private DatabaseReference utilisateurs_ref;
private String nature;
private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise_firebase();
        inialisation_components();
        setCustumeToolbare();
        verifier_existance_utilisateur();



    }

    private void inialisation_components()
    {toolbar=(Toolbar)this.findViewById(R.id.toolbar);
     this.setSupportActionBar(toolbar);

     viewPager=(ViewPager)this.findViewById(R.id.viewpager);
     tabLayout=(TabLayout)this.findViewById(R.id.main_tab);
     tabLayout.setupWithViewPager(viewPager);
     adapter=new SectionPagerAdapter(this.getSupportFragmentManager());
     viewPager.setAdapter(adapter);
     dialog=new ProgressDialog(this);
     dialog.setMessage("Chargement...");
     dialog.setCancelable(false);
     dialog.show();
    }
    private void setCustumeToolbare()
    {
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.custum_toolbare,null);
        image_profil=(CircleImageView)v.findViewById(R.id.image_profil);
        image_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nature.equals("docteur"))
                {

                }else
                {Intent i=new Intent(MainActivity.this,ProfilUtilisateurActivity.class);
                 MainActivity.this.startActivity(i);
                }
            }
        });
        nom_profil=(TextView)v.findViewById(R.id.nom_profil);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.addView(v);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu,menu);
        return true;

    }
    private void initialise_firebase()
    {
        auth=FirebaseAuth.getInstance();
        utilisateurs_ref= FirebaseDatabase.getInstance().getReference().child("utilisateurs");
        docteurs_ref= FirebaseDatabase.getInstance().getReference().child("docteurs");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deconnecter:
            auth.signOut();
            Intent i=new Intent(this,LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(i);
        }
        return true;
    }

    public boolean verifier_existance_utilisateur()
    {utilisateurs_ref.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.hasChild(auth.getCurrentUser().getUid())==false)
            {docteurs_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(auth.getCurrentUser().getUid())==false)
                    {//------------ask user for nature ------------------
                        dialog.dismiss();
                        final AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                        View v= LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_nature_utilisateur,null);
                        final CircularProgressButton btn_confirmer=(CircularProgressButton)v.findViewById(R.id.btn_confirmer);
                        final RadioButton docteur=(RadioButton)v.findViewById(R.id.docteur);
                        final RadioButton patient=(RadioButton)v.findViewById(R.id.patient);
                        patient.setChecked(true);
                        dialog.setCancelable(false);
                        dialog.setView(v);
                        final AlertDialog alertDialog=dialog.show();
                        btn_confirmer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String pathImage = auth.getCurrentUser().getPhotoUrl().toString();
                                // find the Facebook profile and get the user's id
                                for(UserInfo profile : auth.getCurrentUser().getProviderData()) {
                                    // check if the provider id matches "facebook.com"
                                    if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                                        pathImage = "https://graph.facebook.com/"+profile.getUid()+"/picture?height=500";
                                    }
                                }

                                 // construct the URL to the profile picture, with a custom height
                                // alternatively, use '?type=small|medium|large' instead of ?height=


                                if(docteur.isChecked())
                                { docteur d=new docteur(auth.getCurrentUser().getDisplayName(),pathImage);
                                  d.ajoute(auth.getCurrentUser().getUid(),MainActivity.this,btn_confirmer,alertDialog);
                                  nature="docteur";
                                  set_image_name_profil();

                                }else if(patient.isChecked())
                                {nature="utilisateur";
                                 utilisateur u=new utilisateur(auth.getCurrentUser().getDisplayName(),pathImage);
                                 u.ajoute(auth.getCurrentUser().getUid(),MainActivity.this,btn_confirmer,alertDialog);
                                 set_image_name_profil();
                                }
                            }
                        });





                    }else
                    {nature="docteur";
                    set_image_name_profil();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();
                }
            });

            }else
            {nature="utilisateur";
             set_image_name_profil();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(MainActivity.this,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();
        }
    });
      return true;
    }
    private void  set_image_name_profil()
    { if(nature.equals("docteur")==true)
      {   docteurs_ref.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
          docteur d=dataSnapshot.getValue(docteur.class);
          nom_profil.setText(d.getNom());
          if(d.getImage().equals("default")==false)
              {Picasso.with(MainActivity.this).load(d.getImage()).placeholder(R.drawable.image_profil).fit().centerCrop().into(image_profil);}
           dialog.dismiss();
          }



          @Override
          public void onCancelled(DatabaseError databaseError) {
              dialog.dismiss();
          }
      });

      }else
      {   utilisateurs_ref.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              utilisateur u=dataSnapshot.getValue(utilisateur.class);
              nom_profil.setText(u.getNom());
              if(u.getImage().equals("default")==false)
              {Picasso.with(MainActivity.this).load(u.getImage()).placeholder(R.drawable.image_profil).fit().centerCrop().into(image_profil);}
              dialog.dismiss();
          }



          @Override
          public void onCancelled(DatabaseError databaseError) {
              dialog.dismiss();
          }
      });

      }

    }

}
