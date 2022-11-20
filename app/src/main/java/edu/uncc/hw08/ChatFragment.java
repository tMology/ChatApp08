package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import edu.uncc.hw08.databinding.ChatListItemBinding;
import edu.uncc.hw08.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_CHAT = "ARG_PARAM_CHAT";

    private Chat mChat;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(Chat chat) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CHAT, chat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChat = (Chat) getArguments().getSerializable(ARG_PARAM_CHAT);
        }
    }

    FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    //Continue from YOUTUBE 1:05:02 11/18/2022

    ListenerRegistration messagesRegistration;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.closeChatFragment();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgText = binding.editTextMessage.getText().toString();
                if(msgText.isEmpty()){
                    Toast.makeText(getActivity(), "Enter a Message", Toast.LENGTH_SHORT).show();
                } else {

                    HashMap<String, Object> messageData = new HashMap<>();
                    messageData.put("message", msgText);
                    messageData.put("createdAt" , FieldValue.serverTimestamp());
                    messageData.put("ownerId" , mAuth.getCurrentUser().getUid());
                    messageData.put("ownerName" , mAuth.getCurrentUser().getDisplayName());

                    DocumentReference msgDocRef = FirebaseFirestore.getInstance().collection("chats")
                            .document(mChat.getChatId()).collection("messages").document();

                    messageData.put("msgId", msgDocRef.getId());

                    msgDocRef.set(messageData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                binding.editTextMessage.setText("");

                                FirebaseFirestore.getInstance().collection("chats")
                                        .document(mChat.getChatId()).update("lastMsg", messageData);


                            }else {
                                Toast.makeText(getActivity(), "Error sending Message", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        binding.buttonDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore.getInstance().collection("chats")
                        .document(mChat.getChatId()).collection("messages").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                ArrayList<DocumentReference> msgDocRefs = new ArrayList<>();

                                for (QueryDocumentSnapshot doc: task.getResult()) {
                                    msgDocRefs.add(doc.getReference());
                                }
                                deleteFirstMsg(msgDocRefs);
                            }
                        });
            }
        });

        messagesRegistration = FirebaseFirestore.getInstance().collection("chats")
                .document(mChat.getChatId()).collection("messages").orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                          mMessages.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Message message = doc.toObject(Message.class);
                            mMessages.add(message);
                        }
                        messagesAdapter.notifyDataSetChanged();

                        if(mMessages.size() == 0){
                            //Delete chat and leave
                            FirebaseFirestore.getInstance().collection("chats")
                                    .document(mChat.getChatId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mListener.closeChatFragment();
                                            }else{

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void onPause(){
        super.onPause();
        if(messagesRegistration != null){
            messagesRegistration.remove();
        }
    }

    void deleteFirstMsg(ArrayList<DocumentReference> msgDocRefs){
        if(msgDocRefs.size() > 0){
            DocumentReference docRef = msgDocRefs.remove(0);
            docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    deleteFirstMsg(msgDocRefs);
                }
            });
        }
    }


    ArrayList<Message> mMessages = new ArrayList<>();
    MessagesAdapter messagesAdapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
            ChatListItemBinding binding = ChatListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new MessageViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position){
            holder.setupUI(mMessages.get(position));
        }

        @Override
        public int getItemCount() { return mMessages.size(); }

        class MessageViewHolder extends RecyclerView.ViewHolder{
            ChatListItemBinding mBinding;
            Message mMessage;
            public MessageViewHolder(ChatListItemBinding binding){
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Message message){
                mMessage = message;

                if(mMessage.getOwnerId().equals(mAuth.getCurrentUser().getUid())){
                    mBinding.imageViewDelete.setVisibility(View.VISIBLE);
                    mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Confirm if message is last message
                            if(mMessage.getMsgId().equals(mChat.getLastMsg().getMsgId())){
                                FirebaseFirestore.getInstance()
                                        .collection("chats").document(mChat.getChatId())
                                        .collection("messages").document(message.getMsgId()).delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                FirebaseFirestore.getInstance()
                                                        .collection("chats").document(mChat.getChatId())
                                                        .collection("messages")
                                                        .orderBy("createdAt", Query.Direction.DESCENDING)
                                                        .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isSuccessful() && task.getResult() !=null){

                                                                    for (QueryDocumentSnapshot doc: task.getResult()) {

                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("chats").document(mChat.getChatId()).update("lastMsg", doc.getData());
                                                                    }
                                                                }else{

                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }else{
                                FirebaseFirestore.getInstance()
                                        .collection("chats").document(mChat.getChatId())
                                        .collection("messages").document(message.getMsgId()).delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        }
                    });

                    mBinding.textViewMsgBy.setText("Me");

                }else{
                    mBinding.imageViewDelete.setVisibility(View.INVISIBLE);
                    mBinding.textViewMsgBy.setText(mMessage.getOwnerName());
                }


                mBinding.textViewMsgText.setText(mMessage.getMsgTxt());
                if(mBinding/*.getCreatedAt()*/ != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    mBinding.textViewMsgOn.setText(sdf.format(mMessage.getCreatedAt())); //Cant Write .toDate after getCreatedAt!!!!!! Youtube 1:10:27
                }else {
                    mBinding.textViewMsgOn.setText("N/A");

                }

            }

        }

    }


    ChatListener mListener;
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        mListener = (ChatListener) context;
    }

    interface ChatListener{
        void closeChatFragment();
    }

}