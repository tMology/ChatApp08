package edu.uncc.hw08;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uncc.hw08.databinding.FragmentCreateChatBinding;
import edu.uncc.hw08.databinding.UsersRowItemBinding;

public class CreateChatFragment extends Fragment {



    public CreateChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    FragmentCreateChatBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersAdapter = new UsersAdapter(getContext(), R.layout.users_row_item, new ArrayList<User>());
        binding.listView.setAdapter(usersAdapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = usersAdapter.getItem(position);
            }
        });

        //Getting strange issues here. https://www.youtube.com/watch?v=zk2F4QG77zs go to 19:47 in this youtube link thats where I left off



        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goBackToMyChats();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create list views here
                String message = binding.editTextMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(getActivity(),"Please enter a comment.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        FirebaseFirestore.getInstance().collection("users")
                .orderBy("name", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        User user = doc.toObject(User.class);

                        if(mAuth.getCurrentUser().getUid().equals(user.getUid())){
                            users.add(user);
                        }
                        usersAdapter.notifyDataSetChanged();

                    }
                }
            }
        });

    }

    ArrayList<User> users = new ArrayList<>();
    UsersAdapter usersAdapter;

    class UsersAdapter extends ArrayAdapter<User> {

        public UsersAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
            super(context, resource, objects);
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            UsersRowItemBinding mBinding;

            if (convertView == null) {
                mBinding = UsersRowItemBinding.inflate(getLayoutInflater(), parent, false);
                convertView = mBinding.getRoot();
                convertView.setTag(mBinding);
            } else {
                mBinding = (UsersRowItemBinding) convertView.getTag();
            }

            User user = getItem(position);

            return convertView;
        }
    }

    CreateChatListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateChatListener) context;
    }

    interface CreateChatListener {
       void goBackToMyChats();
    }
}