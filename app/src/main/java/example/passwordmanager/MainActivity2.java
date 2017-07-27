package example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity2
        extends     AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    SearchView.OnQueryTextListener,
                    View.OnClickListener
{

    private RecyclerView recyclerView;
    private ArrayList<ListEntry> result;
    private RecyclerAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private TextView emptyText;

    private FloatingActionMenu fam;
    private com.github.clans.fab.FloatingActionButton fab1, fab2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Passwords");
        setSupportActionBar(toolbar);

        Log.d("myapp", "this number: " + R.drawable.ic_fd_40px);
        Log.d("myapp", "and this: " + R.drawable.ic_ak_40px);


        // TODO method to add entries to recycler view
        

        /*
         * TODO implement method which saves all entries to a data file upon logging out
         * TODO implement method to populate recycler view with all saved entries from data file upon logging in
         * TODO implement method to add new entries to the recycler view
         * TODO implement onclick method for each cardview to new activity which shows all the information for that entry.
         *
         */

        /*
         * Set up RecyclerView
         */

        setUpRecyclerView();



        /*
         * get context (tab pressed in the side navigation menu),
         * that will determine which folder we are saving the generated password in.
         */
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        fam = (FloatingActionMenu) findViewById(R.id.fam);
        fab1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab2);

        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null)
            drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null)
        {
            navigationView.setNavigationItemSelectedListener(this);

            /* set profile information in header */
            View headerView = navigationView.getHeaderView(0);
            ImageView nav_prof_img = (ImageView) headerView.findViewById(R.id.nav_hdr_prof_img);
            TextView nav_prof_name = (TextView) headerView.findViewById(R.id.nav_hdr_prof_name);
            TextView nav_prof_email = (TextView) headerView.findViewById(R.id.nav_hdr_prof_email);

            Glide.with(this).load(getIntent().getStringExtra("prof_img")).into(nav_prof_img);
            nav_prof_name.setText(getIntent().getStringExtra("prof_name"));
            nav_prof_email.setText(getIntent().getStringExtra("prof_email"));
        }
    }


    private void setUpRecyclerView()
    {
        emptyText = (TextView) findViewById(R.id.text_no_data);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("entries");

        result = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        adapter = new RecyclerAdapter(result, this);
        recyclerView.setAdapter(adapter);

        updateList();

        checkIfEmpty();
    }

    private void removeListEntry(int position)
    {
        reference.child(result.get(position).getKey()).removeValue();
    }

    private void changeListEntry(int position) {
        ListEntry le = result.get(position);

        Map<String, Object> leValues = le.toMap();
        Map<String, Object> new_le = new HashMap<>();

        new_le.put(le.getKey(), leValues);

        reference.updateChildren(new_le);
    }


    private void updateList()
    {
        reference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                result.add(dataSnapshot.getValue(ListEntry.class));
                adapter.notifyDataSetChanged();
                checkIfEmpty();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                ListEntry entry = dataSnapshot.getValue(ListEntry.class);

                int index = getItemIndex(entry);

                result.set(index, entry);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                ListEntry entry = dataSnapshot.getValue(ListEntry.class);

                int index = getItemIndex(entry);
                result.remove(index);
                adapter.notifyItemRemoved(index);

                checkIfEmpty();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private int getItemIndex(ListEntry entry)
    {
        int index = -1;

        for (int i=0; i<result.size(); i++)
        {
            if (result.get(i).getKey().equals(entry.getKey()))
            {
                index = i;
                break;
            }
        }
        return index;
    }

    private void checkIfEmpty() {
        if (result.size()==0)
        {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }


    // TODO change icon and colour of each sub fam fabs
    // TODO implement actions of pressing each sub fam fabs
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.fab1:
                // add secure login
                startActivity(new Intent(MainActivity2.this, AddNewCredentialActivity.class));
                break;

            case R.id.fab2:
                // add secure note
                startActivity(new Intent(MainActivity2.this, AddNewSecureNoteActivity.class));
                //startActivity(new Intent(MainActivity2.this, ShowSecureNoteActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
//            moveTaskToBack(true); // exits app rather than returning to fingerprint authentication screen
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);

        // invoke onQueryTextChange() listener method when using searchview
        MenuItem mItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) MenuItemCompat.getActionView(mItem);
        sv.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            case R.id.action_logout:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.nav_secure_notes:
                // start activity
                break;
            case R.id.nav_logins:
                // start activity
                break;
            case R.id.nav_pass_gen:
                openPassGenDialog();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void openPassGenDialog()
    {
        DialogFragment df = PassGenDialog.newInstance();
        df.show(getSupportFragmentManager(), "PassGen Fragment");
    }

    private void logout()
    {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        newText = newText.toLowerCase();
        ArrayList<ListEntry> newList = new ArrayList<>();

        for (ListEntry e : result)
        {
            String name = e.getName().toLowerCase();
            if (name.contains(newText))
                newList.add(e);
        }

        adapter.setFilter(newList);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case 0:
                // edit name and description dialog?
                break;

            case 1:
                //Log.d("test", "delete triggered " + item.getGroupId());
                removeListEntry(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }

}
