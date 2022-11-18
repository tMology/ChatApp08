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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.uncc.hw08.databinding.ChatListItemBinding;
import edu.uncc.hw08.databinding.FragmentMyChatsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyChatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyChatsFragment newInstance(String param1, String param2) {
        MyChatsFragment fragment = new MyChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentMyChatsBinding binding;

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
        });
        //There are 2 problems with this code first, I do not know why my binding cannot call recyclerView here and 2 (line 147)
        //binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
         chatAdapter = new ChatAdapter();





        getActivity().setTitle("My Chats");
    }

    ArrayList<Chat> mChat = new ArrayList<>();
    ChatAdapter chatAdapter;

    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{


        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ChatListItemBinding binding = ChatListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ChatViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            Chat chat = mChat.get(position);
            holder.setupUI(chat);
        }

        @Override
        public int getItemCount() {
            return mChat.size();
        }

        class ChatViewHolder extends RecyclerView.ViewHolder{
            ChatListItemBinding mBinding;
            Chat mChat;

            public ChatViewHolder( ChatListItemBinding binding){
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Chat chat){
                mChat = chat;

                mBinding.textViewMsgText.setText(mChat.getMessage());

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

                //After get createdAt i need to add .toDate, however I am not able to add this.
                mBinding.textViewMsgOn.setText(sdf.format(mChat.getCreatedAt()));

            }

        }

    }









    MyChatsListener mListener;
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        mListener = (MyChatsListener) context;
    }

    interface MyChatsListener{
        void logout();
        void goToCreateNewChat();
    }
}