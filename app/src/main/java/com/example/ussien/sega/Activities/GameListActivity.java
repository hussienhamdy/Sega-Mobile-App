package com.example.ussien.sega.Activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.ussien.sega.Adapters.GameListAdapter;
import com.example.ussien.sega.Model.ApiClient;
import com.example.ussien.sega.Model.ApiInterface;
import com.example.ussien.sega.Model.Game;
import com.example.ussien.sega.Model.Student;
import com.example.ussien.sega.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.ManifestValidator;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameListActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {

    private static final int notificationID = 15;
    boolean loading = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        if(loadStudent()==null)
        {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_game_list);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        //handle background notifications
        if(getIntent().getExtras()!=null)
        {
            if(getIntent().getExtras().getString("gameID")!=null)
            {
                int gameID = Integer.valueOf(getIntent().getExtras().getString("gameID"));
                Intent intent = new Intent(GameListActivity.this,GameActivity.class);
                intent.putExtra("gameID",gameID);
                startActivity(intent);
            }
        }

        //handle foreground notifications
        PusherAndroid pusher = new PusherAndroid("67900da86aad75ba2530");
        PushNotificationRegistration nativePusher = pusher.nativePusher();
        try {
            nativePusher.registerFCM(this);
        } catch (ManifestValidator.InvalidManifestException e) {
            e.printStackTrace();
        }
        nativePusher.subscribe("news");
        nativePusher.setFCMListener(new FCMPushNotificationReceivedListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage)
            {
                String title = remoteMessage.getNotification().getTitle();
                String content = remoteMessage.getNotification().getBody();
                String gameID = remoteMessage.getData().get("gameID");
                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notification = new NotificationCompat.Builder(GameListActivity.this,"");
                notification.setAutoCancel(true);
                notification.setSmallIcon(R.drawable.ic_notifications_active_black_24dp);
                notification.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                    R.drawable.logo2));
                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle(title);
                notification.setContentText(content);
                notification.setSound(sound);
                Intent intent = new Intent(GameListActivity.this,GameActivity.class);
                intent.putExtra("gameID",Integer.valueOf(gameID));
                PendingIntent pendingIntent = PendingIntent.getActivity(GameListActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationID, notification.build());
            }
        });


        final GridView gridView = findViewById(R.id.games_grid);

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!loading)
                    loadGames(gridView);
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                // to solve the issue of scrolling with swipeRefreshLayout
                if (gridView.getChildAt(0) != null)
                    swipeRefreshLayout.setEnabled(gridView.getFirstVisiblePosition() == 0 && gridView.getChildAt(0).getTop() == 0);
            }
        });

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.bringToFront();
            }
        };
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_black_24dp,getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        Student student = loadStudent();
        TextView headerMail= header.findViewById(R.id.header_mail);
        headerMail.setText(student.getEmail());
        TextView headerNickName= header.findViewById(R.id.header_nickName);
        SimpleDraweeView image = header.findViewById(R.id.imageView);
        Uri uri = Uri.parse(student.getProfilePicture());
        image.setImageURI(uri);
        headerNickName.setText(student.getNickName());
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameListActivity.this,ProfileActivity.class);
                startActivity(intent);
                drawer.closeDrawer(Gravity.START);
            }
        });
        loadGames(gridView);
    }

    private void loadGames(final GridView gridView)
    {
        loading = true;
        TextView textView = findViewById(R.id.no_connection_txt);
        textView.setVisibility(View.GONE);
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefresh);

        int userID = loadStudent().getStudentID();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<Game>> call = apiInterface.loadGames(userID);
        call.enqueue(new Callback<ArrayList<Game>>() {
            @Override
            public void onResponse(Call<ArrayList<Game>> call, Response<ArrayList<Game>> response) {
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                loading = false;
                ArrayList<Game> games = response.body();
                final GameListAdapter gameListAdapter = new GameListAdapter(GameListActivity.this,games);
                gridView.setAdapter(gameListAdapter);
                View loading = findViewById(R.id.loading_layout);
                loading.setVisibility(View.GONE);
                if(games.size()==0)
                {
                    View empty = findViewById(R.id.empty_layout);
                    empty.setVisibility(View.VISIBLE);
                }
                else
                {
                    View empty = findViewById(R.id.empty_layout);
                    empty.setVisibility(View.GONE);
                }
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(GameListActivity.this,GameActivity.class);
                        int gameID = ((Game)gameListAdapter.getItem(i)).getGameID();
                        intent.putExtra("gameID",gameID);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onFailure(Call<ArrayList<Game>> call, Throwable t) {
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                loading = false;
                TextView textView = findViewById(R.id.no_connection_txt);
                textView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out)
        {
                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                sharedPref.edit().clear().commit();
                Intent intent = new Intent(GameListActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
         if (id == R.id.search_game) {
            Intent intent = new Intent(GameListActivity.this,GameRegistrationActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Student loadStudent()
    {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if(sharedPref.getAll().size()==0)
            return null;
        Gson gson = new Gson();
        String json = sharedPref.getString("student", "");
        return gson.fromJson(json, Student.class);
    }
}
