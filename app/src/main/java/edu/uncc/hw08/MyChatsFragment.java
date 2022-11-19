package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.uncc.hw08.databinding.FragmentMyChatsBinding;
import edu.uncc.hw08.databinding.MyChatsListItemBinding;

public class MyChatsFragment extends Fragment {

    public MyChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentMyChatsBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });

        binding.buttonNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToCreateNewChat();
            }

//            chatsAdapter = new ChatsAdapter(getContext(), R.layout.my_chats_list_item, mChats);
//            binding.listView.setAdapter(chatsAdapter);
//            binding.listView.setOnItemClickListener(new View.OnClickListener(){
//               @Override
//                public void onItemClickListener(AdapterView<?> parent, View view, int position, long id){
//                   mListener.goToChat(mChats.get(position));
//                } Encountering major issues with this portion of the code Youtube 1:01:21
//            });


        });
        //There are 2 problems with this code first, I do not know why my binding cannot call recyclerView here and 2 (line 147)
        //binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

         getActivity().setTitle("My Chats");

        FirebaseFirestore.getInstance().collection("chats").whereArrayContains("userIds", mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mChats.clear();
                        for (QueryDocumentSnapshot doc: value){
                            Chat chat = doc.toObject(Chat.class);
                            mChats.add(chat);
                        }
                        //chatsAdapter.notifyDataSetChanged(); From this line my application is crashing from null, and I have no clue why..... Youtube 56:42
                    }
                });


    }

    MyChatsListener mListener;
    ArrayList<Chat> mChats = new ArrayList<>();
    ChatsAdapter chatsAdapter;

    class ChatsAdapter extends ArrayAdapter<Chat> {
    public ChatsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Chat> objects) {
        super(context, resource, objects);
    }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            MyChatsListItemBinding mBinding;
            if(convertView == null){
                mBinding = MyChatsListItemBinding.inflate(getLayoutInflater(), parent, false);
                convertView = mBinding.getRoot();
                convertView.setTag(mBinding);
            }else{
                mBinding = (MyChatsListItemBinding) convertView.getTag();
            }
            Chat chat = getItem(position);

            mBinding.textViewMsgBy.setText("test");

            if(chat.getUserIds() != null && chat.getUserIds().size() == 2 && chat.getUserNames() != null && chat.getUserNames().size() == 2){
                if(chat.getUserIds().get(0).equals(mAuth.getCurrentUser().getUid())){
                    mBinding.textViewMsgBy.setText(chat.getUserNames().get(1));
                }else{
                    mBinding.textViewMsgBy.setText(chat.getUserNames().get(0));
                }

            } else {
                mBinding.textViewMsgBy.setText("N/A");
            }


            if(chat.getLastMsg().getCreatedAt() != null){
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                mBinding.textViewMsgOn.setText(sdf.format(chat.getLastMsg().getCreatedAt())); //Still cant add .toDate() !!!!!!!! Youtube 54:03
            } else{
                mBinding.textViewMsgOn.setText("N/A");
            }

            mBinding.textViewMsgText.setText(chat.getLastMsg().getMsgTxt());

            return convertView;
        }

    }










    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        mListener = (MyChatsListener) context;
    }

    interface MyChatsListener{
        void logout();
        void goToCreateNewChat();
        void goToChat(Chat chat);
    }
}