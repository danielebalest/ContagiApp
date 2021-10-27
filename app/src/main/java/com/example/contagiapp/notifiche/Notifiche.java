package com.example.contagiapp.notifiche;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.contagiapp.MainActivity;
import com.example.contagiapp.R;
import com.example.contagiapp.eventi.Evento;
import com.example.contagiapp.eventi.ProfiloEventoFragment;
import com.example.contagiapp.gruppi.Gruppo;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//classe che crea e mostra notifiche nella barra delle notifiche android
public class Notifiche {
    private static final String TAG = "Notifiche.java";
    private MainActivity mainActivity;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Bitmap bitMap;
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static final String GROUP_KEY_WORK = "NOTIFY";

    public Notifiche(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        invitiAmici();
    }

    private void invitiAmici() {

        db.collection("Utenti")
                .document(getMailUtenteLoggato())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        Utente utente = documentSnapshot.toObject(Utente.class);
                        final ArrayList<String> invitiAmici = utente.getRichiesteRicevute();
                        final ArrayList<Notification> notifiche = new ArrayList<>();

                        boolean controllo = false;
                        for(int i = 0; i < invitiAmici.size(); i++) {
                            if(i == invitiAmici.size()-1) controllo = true;
                            final boolean finalControllo = controllo;

                            db.collection("Utenti")
                                    .document(invitiAmici.get(i))
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(@NonNull DocumentSnapshot documentSnapshot1) {
                                            Utente ut = documentSnapshot1.toObject(Utente.class);

                                            //TODO non riesco a caricare l'immagine dell'utente
                                            //caricaImgDaStorage("imgUtenti", ut.getMail());
                                            Notification newMessageNotification1 =
                                                    new NotificationCompat.Builder(mainActivity, "CHANNEL_ID")
                                                            .setSmallIcon(R.drawable.ic_friends)
                                                            .setSubText("Richiesta di amicizia")
                                                            .setLargeIcon(bitMap)
                                                            /*.setStyle(new NotificationCompat.BigPictureStyle()
                                                                    .bigPicture(bitMap)
                                                                    .bigLargeIcon(null))*/
                                                            .setContentText("Città: "+ut.getCitta()+"\nData di nascita: "+ut.getDataNascita())
                                                            .setContentTitle(ut.getCognome()+" "+ut.getNome())
                                                            .setGroup(GROUP_KEY_WORK)
                                                            .build();

                                            notifiche.add(newMessageNotification1);

                                            if(finalControllo) richiesteGruppi(notifiche);
                                        }
                                    });
                        }
                    }
                });
    }

    private void richiesteGruppi(final ArrayList<Notification> notifiche) {

        db.collection("Utenti")
                .document(getMailUtenteLoggato())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Utente utente = documentSnapshot.toObject(Utente.class);
                final ArrayList<String> richiesteGruppi = utente.getInvitiRicevuti();

                boolean controllo = false;
                for(int i = 0; i < richiesteGruppi.size(); i++) {
                    if(i == richiesteGruppi.size()-1) controllo = true;
                    final boolean finalControllo = controllo;

                    db.collection("Gruppo")
                            .document(richiesteGruppi.get(i))
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull DocumentSnapshot documentSnapshot1) {
                            final Gruppo grup = documentSnapshot1.toObject(Gruppo.class);

                            db.collection("Utenti")
                                    .document(grup.getAdmin())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                    Utente admin = documentSnapshot2.toObject(Utente.class);

                                    Notification newMessageNotification1 =
                                            new NotificationCompat.Builder(mainActivity, "CHANNEL_ID")
                                                    .setSmallIcon(R.drawable.ic_group_black_24dp)
                                                    .setSubText("Unisciti al gruppo")
                                                    .setLargeIcon(bitMap)
                                                    /*.setStyle(new NotificationCompat.BigPictureStyle()
                                                            .bigPicture(bitMap)
                                                            .bigLargeIcon(null))*/
                                                    .setContentTitle("Amministratore gruppo: "+admin.getCognome()+" "+admin.getNome())
                                                    .setContentText("Nome gruppo: "+grup.getNomeGruppo()+" Partecipanti: "+grup.getNroPartecipanti())
                                                    .setGroup(GROUP_KEY_WORK)
                                                    .build();

                                    notifiche.add(newMessageNotification1);

                                    if(finalControllo) eventi(notifiche);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void eventi(final ArrayList<Notification> notifiche) {

        db.collection("Eventi")
                .whereArrayContains("partecipanti",getMailUtenteLoggato())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for(DocumentSnapshot documentSnapshot : task.getResult()) {
                        Evento evento = documentSnapshot.toObject(Evento.class);

                        try {
                            Date dataEvento = new SimpleDateFormat("dd/MM/yyyy").parse(evento.getData());
                            Date dataAttuale = new Date(System.currentTimeMillis());

                            //86400000 millisecondi = 1 giorno
                            if((dataAttuale.getTime() - dataEvento.getTime()) <= 86400000) {

                                Intent notifyIntent = new Intent(mainActivity, Smistamento.class);
                                notifyIntent.putExtra("id", evento.getIdEvento());

                                // Set the Activity to start in a new, empty task
                                //notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                // Create the PendingIntent
                                PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                                        mainActivity, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
                                );

                                Notification newMessageNotification1 =
                                        new NotificationCompat.Builder(mainActivity, "CHANNEL_ID")
                                                .setSmallIcon(R.drawable.ic_event_black_24dp)
                                                .setSubText("Ti ricordiamo l'evento "+evento.getNome())
                                                .setLargeIcon(bitMap)
                                                .setContentIntent(notifyPendingIntent)
                                                /*.setStyle(new NotificationCompat.BigPictureStyle()
                                                        .bigPicture(bitMap)
                                                        .bigLargeIcon(null))*/
                                                .setContentText("Città: "+evento.getCitta())
                                                .setContentTitle("Data: "+evento.getData()+" "+evento.getOrario())
                                                .setGroup(GROUP_KEY_WORK)
                                                .build();

                                notifiche.add(newMessageNotification1);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    statoXTampone(notifiche);
                }
            }
        });
    }

    private void statoXTampone(final ArrayList<Notification> notifiche) {

        db.collection("Utenti")
                .document(getMailUtenteLoggato())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String stato = documentSnapshot.toObject(Utente.class).getStato();

                if(stato.equals("arancione") || stato.equals("giallo")) {

                    Notification newMessageNotification1 =
                            new NotificationCompat.Builder(mainActivity, "CHANNEL_ID")
                                    .setSmallIcon(R.drawable.ic_account_circle_black_24dp)
                                    .setSubText("Stato")
                                    .setContentTitle("Ti ricordo che il tuo stato è "+stato+"\n")
                                    .setContentText("Fai un tampone al più presto")
                                    .setLargeIcon(bitMap)
                                    /*.setStyle(new NotificationCompat.BigPictureStyle()
                                            .bigPicture(bitMap)
                                            .bigLargeIcon(null))*/
                                    .setGroup(GROUP_KEY_WORK)
                                    .build();

                    notifiche.add(newMessageNotification1);

                    Notification summaryNotification =
                            new NotificationCompat.Builder(mainActivity, "CHANNEL_ID")
                                    .setContentTitle("Notifiche")
                                    //set content text to support devices running API level < 24
                                    .setContentText("Notifiche")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    //build summary info into InboxStyle template
                                    .setStyle(new NotificationCompat.InboxStyle()
                                            .setSummaryText("Notifiche"))
                                    //specify which group this notification belongs to
                                    .setGroup(GROUP_KEY_WORK)
                                    //set this notification as the summary for the group
                                    .setGroupSummary(true)
                                    .build();

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mainActivity);
                    for(int j = 0; j < notifiche.size(); j++) {
                        notificationManager.notify(j, notifiche.get(j));
                    }
                    notificationManager.notify(10, summaryNotification);
                }
            }
        });
    }

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = mainActivity.getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;

        if(!json.equals("no")) {
            Utente utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMail();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = mainActivity.getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
    }

    private void caricaImgDaStorage(String directory, String idImmagine) {
        storageRef.child(directory + "/" + idImmagine).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageView imageView = mainActivity.findViewById(R.id.imgUser);
                String sUrl = uri.toString(); //otteniamo il token del'immagine
                Log.d("sUrl", sUrl);
                Picasso.get().load(sUrl).into(imageView);

                Drawable d = imageView.getDrawable();
                bitMap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailure Exception", String.valueOf(e));
            }
        });
    }
}
