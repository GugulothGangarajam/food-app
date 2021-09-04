package com.example.yumm;

import android.content.Intent;
import android.os.Bundle;

import com.example.yumm.Interface.ItemClickListener;
import com.example.yumm.Model.Category;
import com.example.yumm.ViewHolder.MenuViewHolder;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference category;

    private RecyclerView recycler_menu;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ChildEventListener childEventListener;
    private static final int SIGNED_IN = 1;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        firebaseAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(MainActivity.this,Cart.class);
                startActivity(cartIntent);
            }
        });

        recycler_menu = (RecyclerView) findViewById(R.id.home_list);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        imageView = (ImageView) findViewById(R.id.startDisplay);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    onSignedIn(user.getDisplayName());
                }
                else {
                    onSignedOut();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.PhoneBuilder().build(),
                                            new AuthUI.IdpConfig.AnonymousBuilder().build()
                                    ))
                                    .setTheme(R.style.LoginTheme)
                                    .setLogo(R.drawable.logobg)

                            .build(),SIGNED_IN
                    );
                }
            }
        };

        imageView.setVisibility(View.VISIBLE);
        loadMenu();
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, final Category category, int i) {
                menuViewHolder.textMenuName.setText(category.getName());
                Picasso.with(getBaseContext()).load(category.getImage())
                        .into(menuViewHolder.menuImage);
                final Category clickMenuItem = category;
                menuViewHolder.setOnItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(MainActivity.this,clickMenuItem.getName(),Toast.LENGTH_SHORT).show();
                        Intent foodIntent = new Intent(MainActivity.this,FoodList.class);
                        foodIntent.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodIntent);
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
        imageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            AuthUI.getInstance().signOut(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
        detachDatabaseListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGNED_IN) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,"Welcome!",Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this,"Sign In Failed!",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void onSignedIn(String displayName) {
        attachDatabaseListener();
    }

    private void onSignedOut() {
        detachDatabaseListener();
    }

    private void attachDatabaseListener() {
        if(childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            category.addChildEventListener(childEventListener);
        }
    }

    private void detachDatabaseListener() {
        if(childEventListener != null) {
            category.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }



}