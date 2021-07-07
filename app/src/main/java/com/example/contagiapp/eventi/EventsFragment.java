package com.example.contagiapp.eventi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.contagiapp.R;
import com.example.contagiapp.data.amici.AddFriendsActivity;
import com.example.contagiapp.data.amici.ProfiloUtentiActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventsFragment extends Fragment {

    public EventsFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton new_event;
    TextInputEditText editText;
    RecyclerView rvEventi;
    ArrayList<Evento> listaEventi = new ArrayList<Evento>();
    ArrayList<String> idList = new ArrayList<String>(); //lista che conterrà gli id cioè le mail degli utenti




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_events, container, false);


        rvEventi = view.findViewById(R.id.rvEventi);
        caricaEventi();

        new_event = view.findViewById(R.id.floating_action_button);
        new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewEventsActivity.class);
                startActivity(intent);
            }
        });

        editText = view.findViewById(R.id.search_field);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //da inserire metodo per la ricerca
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void caricaEventi(){
        listaEventi = new ArrayList<Evento>();
        db.collection("Eventi").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Evento evento = documentSnapshot.toObject(Evento.class);
                            listaEventi.add(evento);

                            String id = documentSnapshot.getId();
                            idList.add(id);

                        }
                        EventAdapter adapter = new EventAdapter(listaEventi);
                        rvEventi.setAdapter(adapter);
                        rvEventi.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rvEventi.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvEventi, new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {

                                String idEventoSelezionato = idList.get(position);
                                Log.i("idList: ", idEventoSelezionato);
                                Toast.makeText(getActivity().getApplicationContext(), idEventoSelezionato, Toast.LENGTH_SHORT).show();


                                ProfiloEventoFragment fragment = new ProfiloEventoFragment();

                                Bundle bundle = new Bundle();
                                bundle.putString("idEvento", idEventoSelezionato);

                                fragment.setArguments(bundle);



                                //richiamo il fragment

                                FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                                fr.replace(R.id.container,fragment);
                                fr.addToBackStack(null); //serve per tornare al fragment precedente
                                fr.commit();

                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }

                        }));
                    }
                }); //toDo onFailure
    }


    //per il click
    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private EventsFragment.RecyclerTouchListener.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final EventsFragment.RecyclerTouchListener.ClickListener clickListener) {
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
}
