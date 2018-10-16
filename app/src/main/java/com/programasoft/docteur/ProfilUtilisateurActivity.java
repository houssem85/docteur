package com.programasoft.docteur;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilUtilisateurActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView nom_profil;
    private CircleImageView image_profil;
    private FirebaseAuth auth;
    private DatabaseReference reference_utilisateur;
    private StorageReference storageReference_img_utilisateur;
    private final int GALERY_INTENT=1;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_utilisateur);
        inialisation_components();
        inialisation_firebase();
        reference_utilisateur.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            utilisateur u=dataSnapshot.getValue(utilisateur.class);
            nom_profil.setText(u.getNom());
            if(u.getImage().equals("default")==false) {
                Picasso.with(ProfilUtilisateurActivity.this).load(u.getImage()).placeholder(R.drawable.image_profil).fit().centerCrop().into(image_profil);
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void inialisation_components()
    {toolbar=(Toolbar)this.findViewById(R.id.toolbar);
     this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Modifier profil");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nom_profil=(TextView)this.findViewById(R.id.nom_profil);
        image_profil=(CircleImageView)this.findViewById(R.id.image_profil);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Chargement...");
        progressDialog.setCancelable(false);
    }

    private void inialisation_firebase()
    { auth=FirebaseAuth.getInstance();
      reference_utilisateur= FirebaseDatabase.getInstance().getReference().child("utilisateurs").child(auth.getCurrentUser().getUid());
      storageReference_img_utilisateur= FirebaseStorage.getInstance().getReference().child("profil_images").child(auth.getCurrentUser().getUid());
    }

    public void changer_nom(View view) {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        View v= LayoutInflater.from(this).inflate(R.layout.activity_changer_nom,null);
        final CircularProgressButton btn_confirmer=(CircularProgressButton)v.findViewById(R.id.btn_confirmer);
        final TextInputEditText edit_nom_profil=(TextInputEditText)v.findViewById(R.id.nom_profil);
        edit_nom_profil.setText(nom_profil.getText());
        dialog.setCancelable(false);
        dialog.setView(v);
        final AlertDialog alertDialog=dialog.show();
        btn_confirmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nom_utilisateur=edit_nom_profil.getText().toString();
                if(nom_utilisateur.isEmpty()==false)
                {btn_confirmer.startAnimation();
                 reference_utilisateur.child("nom").setValue(nom_utilisateur).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful())
                      {
                         alertDialog.cancel();
                      }else
                      {Toast.makeText(ProfilUtilisateurActivity.this,"Il ya une ereur .veuillez réessayer",Toast.LENGTH_SHORT).show();

                      }
                     }
                 });

                }else
                {

                }
            }
        });



    }

    public void changer_image(View view) {
        Intent i=new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        this.startActivityForResult(i,GALERY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALERY_INTENT &&  resultCode==RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog.show();
                storageReference_img_utilisateur.putFile(result.getUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        reference_utilisateur.child("image").setValue(taskSnapshot.getDownloadUrl().toString());
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public void deconnecter(View view) {
        auth.signOut();
        Intent i=new Intent(this,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(i);
    }
}
