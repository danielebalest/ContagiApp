package com.example.contagiapp.gruppi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.contagiapp.GruppoAdapter;
import com.example.contagiapp.R;
import com.example.contagiapp.data.amici.AddFriendsActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    public GroupFragment() {
    }

    private Utente utente;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton crea_gruppo;
    private Button visualizza_gruppo;
    TextInputEditText editText;

    ArrayList<Gruppo> listaGruppi = new ArrayList<Gruppo>();
    ArrayList<String> listaId = new ArrayList<String>();

    ListView listView;
    RecyclerView rvGruppi;
    TextView tvTuoiGruppi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_group, container, false);
        tvTuoiGruppi = view.findViewById(R.id.tvTuoiGruppi);
        rvGruppi = view.findViewById(R.id.rvGruppi);
        loadGruppi();


        crea_gruppo = view.findViewById(R.id.FAB_groups);
        crea_gruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreaGruppoActivity.class);
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

    private void loadGruppi() {

        Gson gson = new Gson();
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailAdmin;
        //TODO capire il funzionamento
        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);
            mailAdmin = utente.getMail();
            Log.d("mail", mailAdmin);
        } else {
            SharedPreferences prefs1 = getActivity().getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailAdmin = prefs1.getString("mail", "no");
            Log.d("mail", mailAdmin);
        }


        db.collection("Gruppo").whereEqualTo("admin", mailAdmin).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);
                    String id = documentSnapshot.getId();
                    listaId.add(id);
                    listaGruppi.add(gruppo);
                    Log.d("Lista Gruppi", String.valueOf(listaGruppi));
                }
                if(listaGruppi.isEmpty()){
                    tvTuoiGruppi.setText("Non hai ancora creato un gruppo. Crea subito uno");
                }
                GruppoAdapter adapter = new GruppoAdapter(listaGruppi);
                rvGruppi.setAdapter(adapter);
                rvGruppi.setLayoutManager(new LinearLayoutManager(getActivity()));


            }
        });
    }

    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private RecyclerTouchListener.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final RecyclerTouchListener.ClickListener clickListener) {
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
