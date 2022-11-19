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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    User selectedUser = null;

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

        usersAdapter = new UsersAdapter(getContext(), R.layout.users_row_item, mUsers);
        binding.listView.setAdapter(usersAdapter);
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedUser = usersAdapter.getItem(position);
                binding.textViewSelectedUser.setText(selectedUser.getName());
            }
        });

        //Getting strange issues here. https://www.youtube.com/watch?v=zk2F4QG77zs go to 27:00 in this youtube link thats where I left off



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
                if (message.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a comment.", Toast.LENGTH_SHORT).show();
                }else if(selectedUser == null) {
                    Toast.makeText(getActivity(), "Please select a user", Toast.LENGTH_SHORT).show();
            }else {


                    HashMap<String, Object> chatData = new HashMap<>();

                    ArrayList<String> userIds = new ArrayList<>();
                    userIds.add(mAuth.getCurrentUser().getUid());
                    userIds.add(selectedUser.getUid());

                    ArrayList<String> userNames = new ArrayList<>();
                    userNames.add(mAuth.getCurrentUser().getDisplayName());
                    userNames.add(selectedUser.getName());

                    chatData.put("userIds", userIds);
                    chatData.put( "userNames",userNames);

                    HashMap<String, Object> messageData = new HashMap<>();
                    messageData.put("message", message);
                    messageData.put("createdAt" , FieldValue.serverTimestamp());
                    messageData.put("ownerId" , mAuth.getCurrentUser().getUid());
                    messageData.put("ownerName" , mAuth.getCurrentUser().getDisplayName());


                    DocumentReference chatDocRef = FirebaseFirestore.getInstance().collection("chats").document();
                    chatData.put("chatId", chatDocRef.getId());

                    DocumentReference msgDocRef = FirebaseFirestore.getInstance().collection("chats")
                            .document(chatDocRef.getId()).collection("messages").document();
                    messageData.put("msgId", msgDocRef.getId());
                    chatData.put("lastMessage", messageData);

                    chatDocRef.set(chatData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //mListener.goBackToMyChats();


                                msgDocRef.set(messageData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mListener.goBackToMyChats();
                                        } else {
                                            Toast.makeText(getActivity(), "ErrorCreating message" ,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(getActivity(), "Error creating chat", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });
        //POINT OF VIDEO MENTIONS THAT HE CHANGES DATA FOR USER BEING ONLINE OR OFFLINE. YOUTUBE 27:00
        FirebaseFirestore.getInstance().collection("users")
                .orderBy("name", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mUsers.clear();
                        for (QueryDocumentSnapshot doc : value){
                            User user = doc.toObject(User.class);
                            if(mAuth.getCurrentUser().getUid().equals(user.getUid())){
                                mUsers.add(user);
                            }
                        }
                        usersAdapter.notifyDataSetChanged();
                    }
                });



    }

    ArrayList<User> mUsers = new ArrayList<>();
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

            mBinding.textViewName.setText(user.getName());

            //!!!!PROBLEM! CANNOT GET SET VISIBILITY TO NOT RED OUT!!! 20:09
//            if(user.isOnline()){}
//                mBinding.imageViewOnline.setVisiblity(View.VISIBLE);
//            } else {
//                mBinding.imageViewOnline.setVisiblity(View.INVISIBLE);
//            }


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