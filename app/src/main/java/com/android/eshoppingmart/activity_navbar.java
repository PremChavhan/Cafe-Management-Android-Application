package com.android.eshoppingmart;

//import android.annotation.SuppressLint;
//import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class activity_navbar extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //public String Storage_Path = "All_Image_Uploads/";
    //public String Database_Path = "All_Image_Uploads_Database";
    //public  int Image_Request_Code = 7;
   // public Uri FilePathUri;
    DatabaseReference mDatabase ;

    //private static final String TAG = "activity_navbar";
    StorageReference mStorageRef;
    //private static final int IMAGE_PICK_CODE= 1000;
    int imageids[] ={R.drawable.imgs1,R.drawable.imgs2,R.drawable.imgs3};
    ImageSwitcher imageSwitcher;
    int currentIndex=-1;
    int messageCount=imageids.length;
    //onItemClickListener mListener;


    private FirebaseRecyclerPagingAdapter<Post, PostViewHolder> mAdapter;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        mDatabase.keepSynced(true);

        RecyclerView mRecyclerView;

        final SwipeRefreshLayout mSwipeRefreshLayout;
        //ImageView SelectImage = (ImageView)findViewById(R.id.imgView);
        imageSwitcher=findViewById(R.id.imgswitcher);

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {

                // Create a new ImageView set it's properties
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);

        // set the animation type to imageSwitcher
        imageSwitcher.setInAnimation(in);
        imageSwitcher.setOutAnimation(out);

        android.net.ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        android.net.NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            Toast.makeText(activity_navbar.this, "INTERNET is Available", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity_navbar.this, "INTERNET is  Not Available", Toast.LENGTH_LONG).show();
            }

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        //Initialize RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);

        //Initialize Database


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        DatabasePagingOptions<Post> options = new DatabasePagingOptions.Builder<Post>()
                .setLifecycleOwner(this)
                .setQuery(mDatabase, config, Post.class)
                .build();



        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                //Called each time when 1000 milliseconds (1 second) (the period parameter)

                currentIndex++;
                // If index reaches maximum reset it
                if(currentIndex==messageCount)
                    currentIndex=0;
                runOnUiThread(new Runnable() {

                    public void run() {
                        imageSwitcher.setImageResource(imageids[currentIndex]);

                    }
                });
            }

        },0,5000);





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAdapter = new FirebaseRecyclerPagingAdapter<Post, PostViewHolder>(options) {
            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return (new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false)));

                //return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder,
                                            int position,
                                            @NonNull Post post) {
                //holder.setItem(post);
                final DatabaseReference mRef=getRef(position);

                //final String PostRef=mRef.getKey();
                View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity_navbar.this);
                        // set title
                        alertDialogBuilder.setTitle("Delete ?");
                        // set dialog message
                        alertDialogBuilder.setMessage("Are You Want To Delete").setCancelable(false)
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(activity_navbar.this,"Post Deleted Successfully...",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                        // start dialoage Box
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        //show it
                        alertDialog.show();

                        Log.e("Activity_navbar","LongClicked");
                        return false;
                    }


                };
                holder.setItem(post, onLongClickListener);


                }



            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        // Do your loading animation
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        // Stop Animation
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        retry();
                        break;
                }
            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                mSwipeRefreshLayout.setRefreshing(false);
                databaseError.toException().printStackTrace();
                finish();

            }
        };


        //Set Adapter to RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        //Set listener to SwipeRefreshLayout for refresh action
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refresh();
            }
        });

        //Show Dialog to add Items in Database
        findViewById(R.id.fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(activity_navbar.this,activity_second.class);
                activity_navbar.this.startActivity(intent);
                //AddPostDialog addPostDialog=new AddPostDialog();
                //addPostDialog.show(activity_navbar.this);
            }
        });

    }

    @Override
    public void onClick(View v) {



    }


    /*public interface onItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener)
    {
        mListener=listener;
    }*/

    //Start Listening Adapter
    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    //Stop Listening Adapter
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_navbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();

            startActivity(new Intent(this, login_activity.class));
            finish();
            Toast toast=Toast.makeText(getApplicationContext(),"SignOut Succesful",Toast.LENGTH_SHORT);
            toast.show();
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }else if (id == R.id.nav_forum) {
            Intent intent=new Intent(activity_navbar.this,Login.class);
            activity_navbar.this.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
