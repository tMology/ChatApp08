package edu.uncc.hw08;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements MyChatsFragment.MyChatsListener, CreateChatFragment.CreateChatListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("isOnline", true);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, new MyChatsFragment())
                .commit();
    }



    @Override
    public void logout() {
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .update("isOnline", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                        startActivity(intent);
                        FirebaseAuth.getInstance().signOut();
                        finish();
                    }
                });
    }

    @Override
    public void goToCreateNewChat() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateChatFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToChat(Chat chat) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, ChatFragment.newInstance(chat))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goBackToMyChats() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new MyChatsFragment())
                .commit();
    }

    //@Override
    public void closeChatFragment() {
        getSupportFragmentManager().popBackStack();
    }
}