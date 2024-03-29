package com.batsoftware.contagiapp.eventi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.UserAdapter;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ProfiloEventoAdminFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "eventi";
    public Evento evento;
    RecyclerView rvPartecipantiProfiloEventoAdmin;
    private MaterialButton btnEliminaEvento;
    private ImageButton btnShare;

    TextView tvNomeEvento;


    ArrayList<String> idList = new ArrayList<String>(); //lista che conterrà gli id cioè le mail degli utenti

    public ProfiloEventoAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profilo_evento_admin, container, false);




        final Bundle bundle = getArguments();
        final String idEvento = bundle.getString("idEvento");

        tvNomeEvento = view.findViewById(R.id.tvNomeEventoAdmin);

        caricaEvento(idEvento, view);
        caricaPartecipanti(idEvento);


        Button modificaEvento = view.findViewById(R.id.btnModificaEvento);
        modificaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewEventsActivity.class);
                intent.putExtra("scelta", true);
                intent.putExtra("idEvento", idEvento);
                startActivity(intent);
            }
        });


        final ImageView img = view.findViewById(R.id.imgProfiloEventoAdmin);
        rvPartecipantiProfiloEventoAdmin = view.findViewById(R.id.rvPartecipantiProfiloEventoAdmin);

        db.collection("Eventi").document(idEvento)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String pathImg = documentSnapshot.getString("pathImg");
                //recupero l'immagine dallo storage
                Log.d("eventi/idEvento","eventi/" + pathImg);

                caricaImgDaStorage(storageRef, storageDirectory, pathImg, img );

            }
        });


        btnEliminaEvento = view.findViewById(R.id.btnEliminaEvento);
        btnEliminaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminaEvento(idEvento);

                showFragment(new EventsFragment());
            }
        });


        btnShare = view.findViewById(R.id.btnShareAdmin);
        btnShare.setBackgroundColor(Color.TRANSPARENT);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                Log.d("nomeEvento", String.valueOf(evento.getNome()));

                String body = getString(R.string.subMessage1)  + evento.getNome().toUpperCase() + getString(R.string.subMessage2) + evento.getIndirizzo()
                        + getString(R.string.separator) + evento.getCitta() + getString(R.string.separator) + evento.getProvincia() + getString(R.string.separator)
                        + evento.getRegione()
                        +  getString(R.string.subMessage3)
                        + getString(R.string.subMessage4)
                        + getString(R.string.link);

                myIntent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
            }
        });

        return view;
    }


    private void caricaImgDaStorage(StorageReference storageRef, String directory, String idImmagine, final ImageView imageView){
        storageRef.child(directory + "/" + idImmagine).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String sUrl = uri.toString(); //otteniamo il token del'immagine
                Log.d("sUrl", sUrl);
                Picasso.get().load(sUrl).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailure Exception", String.valueOf(e));
            }
        });
    }

    private void caricaEvento(String idEvento, final View view){
        db.collection("Eventi")
                .document(idEvento)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    evento = documentSnapshot.toObject(Evento.class);
                    String nome = evento.getNome();
                    String descrizione = evento.getDescrizione();
                    String data = evento.getData();
                    String orario = evento.getOrario();
                    String regione = evento.getRegione();
                    String provincia = evento.getProvincia();
                    String citta = evento.getCitta();
                    String indirizzo = evento.getIndirizzo();
                    int numMax = evento.getNumeroMaxPartecipanti();
                    int numPartecipanti = evento.getPartecipanti().size();
                    int numDisponibili = numMax - numPartecipanti;

                     tvNomeEvento = view.findViewById(R.id.tvNomeEventoAdmin);
                    TextView tvDescrEvento = view.findViewById(R.id.tvDescrEventoAdmin);
                    TextView tvDataEvento = view.findViewById(R.id.tvDataEventoAdmin);
                    TextView tvOrarioEvento = view.findViewById(R.id.tvOrarioEventoAdmin);
                    TextView tvIndirizzoEvento = view.findViewById(R.id.tvIndirizzoEventoAdmin);
                    TextView tvRegioneEvento = view.findViewById(R.id.tvRegioneEventoAdmin);
                    TextView tvProvinciaEvento = view.findViewById(R.id.tvProvinciaEventoAdmin);
                    TextView tvCittaEvento = view.findViewById(R.id.tvCittaEventoAdmin);
                    TextView numMaxPartecipanti = view.findViewById(R.id.num_partecipanti_max);
                    TextView numDispon = view.findViewById(R.id.posti_disponibili);
                    TextView numParteci = view.findViewById(R.id.num_partecipanti);

                    tvNomeEvento.setText(nome);
                    tvDescrEvento.setText(descrizione);
                    tvDataEvento.setText(data);
                    tvOrarioEvento.setText(orario);
                    tvRegioneEvento.setText(getText(R.string.region)+": " + regione);
                    tvProvinciaEvento.setText(getText(R.string.province)+": "  + provincia);
                    tvCittaEvento.setText(getText(R.string.city)+": " +citta);
                    tvIndirizzoEvento.setText(getText(R.string.event_address)+": " +indirizzo);
                    numMaxPartecipanti.setText(getText(R.string.maximum_number_of_participants)+": " +numMax);
                    numDispon.setText(getText(R.string.available_places)+": " +numDisponibili);
                    numParteci.setText(getText(R.string.number_of_participants)+": " +numPartecipanti);

                } else {
                    Toast.makeText(getContext(), "Documents does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void caricaPartecipanti(final String idEvento){
        db.collection("Eventi")
                .document(idEvento)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        Evento evento = documentSnapshot.toObject(Evento.class);

                        final ArrayList<String> listaPartecipanti = evento.getPartecipanti();
                        Log.d("listaPartecipanti", String.valueOf(listaPartecipanti));

                        //recuperare dalle mail l'oggetto utente
                        final ArrayList<Utente> listaUtenti = new ArrayList<Utente>();

                        if (!listaPartecipanti.isEmpty()) {

                            db.collection("Utenti")
                                    .whereIn("mail", listaPartecipanti)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                final ArrayList<String> idList = new ArrayList<String>();

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d("prova", document.getId() + " => " + document.getData());
                                                    Utente utente = document.toObject(Utente.class);
                                                    Log.d("utenteNome", String.valueOf(utente.getNome()));
                                                    listaUtenti.add(utente);
                                                    idList.add(utente.getMailPath());

                                                }
                                                Log.d("listaUtenti", String.valueOf(listaUtenti));
                                                //devo lavorare qui altrimenti perdo la visibilità della lista Utenti

                                                UserAdapter adapter = new UserAdapter(listaUtenti);
                                                rvPartecipantiProfiloEventoAdmin.setAdapter(adapter);
                                                rvPartecipantiProfiloEventoAdmin.setLayoutManager(new LinearLayoutManager(getActivity()));


                                                rvPartecipantiProfiloEventoAdmin.addOnItemTouchListener(new ProfiloEventoAdminFragment.RecyclerTouchListener(getActivity(), rvPartecipantiProfiloEventoAdmin, new ProfiloEventoAdminFragment.RecyclerTouchListener.ClickListener() {
                                                    @Override
                                                    public void onClick(View view, int position) {
                                                        String idUtenteSelezionato = idList.get(position);
                                                        Log.i("idList ", idUtenteSelezionato);

                                                        ProfiloPartecipanteFragment fragment = new ProfiloPartecipanteFragment();

                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("mailPartecipante", idUtenteSelezionato);
                                                        bundle.putString("idEvento", idEvento);

                                                        fragment.setArguments(bundle);

                                                        //richiamo il fragment

                                                        showFragment(fragment);
                                                    }

                                                    @Override
                                                    public void onLongClick(View view, int position) {
                                                    }

                                                }));

                                            } else {
                                                Log.d("ERROR", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }
                    }

                });

    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
        fr.replace(R.id.container,fragment);

        fr.addToBackStack(null); //serve per tornare al fragment precedente
        fr.commit();
        fr.show(new ProfiloEventoAdminFragment());
    }

    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ProfiloEventoAdminFragment.RecyclerTouchListener.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ProfiloEventoAdminFragment.RecyclerTouchListener.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        public interface ClickListener {
            void onClick(View view, int position);

            void onLongClick(View view, int position);
        }
    }

    private void eliminaEvento(String idEvento){
        db.collection("Eventi")
                .document(idEvento)
                .delete();

        Toasty.success(getContext(), getString(R.string.EventoEliminato), Toast.LENGTH_LONG).show();
        notificaEventoEliminato();



    }



    private void notificaEventoEliminato(){
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Evento cancellato")
                .setContentText("L'admin ha cancellato l' evento" )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        // notificationId is a unique int for each notification that you must define
        int notificationId = 1234;
        notificationManager.notify(notificationId, builder.build());



    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channelName";
            String description = "channel_description" ;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}