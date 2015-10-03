package com.phongnt.phong.chattingtostranger.ui;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.phongnt.phong.chattingtostranger.R;
import com.phongnt.phong.chattingtostranger.data.DatabaseHandler;
import com.phongnt.phong.chattingtostranger.data.User;
import com.phongnt.phong.chattingtostranger.services.ChatService;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_action_map,
            R.drawable.ic_action_group,
            R.drawable.ic_action_reply
    };
    ViewPagerAdapter adapter;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        databaseHandler = new DatabaseHandler(this);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        //setupTabIcons();
        ImageButton add_friend = (ImageButton)findViewById(R.id.findFriend);
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(TabActivity.this, NewDialog.class);
                startActivity(in);
            }
        });



        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        TextView username = (TextView)navigationView.findViewById(R.id.username);
        User user = databaseHandler.getUser();
        username.setText(user.getUser());




        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.friend:
                        Toast.makeText(getApplicationContext(), "Friend", Toast.LENGTH_SHORT).show();
                        /*ContentFragment fragment = new ContentFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame,fragment);
                        fragmentTransaction.commit();*/
                        Intent ins = new Intent(TabActivity.this,NewDialog.class);
                        startActivity(ins);
                        finish();
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.logout:
                        //Toast.makeText(getApplicationContext(),"Stared Selected",Toast.LENGTH_SHORT).show();
                        ChatService.getInstance().logout();
                        databaseHandler.resetLogin();
                        Intent in = new Intent(TabActivity.this,Login.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();
                        return true;
                    case R.id.faq:
                        Toast.makeText(getApplicationContext(),"FAQ",Toast.LENGTH_SHORT).show();
                        return true;

                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,
                R.string.app_name){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();



    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[2]);
        //tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(final ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new MapFragment(), "MAP");
        //adapter.addFragment(new GroupFragment(), "GROUP");
        adapter.addFragment(new InBoxFragment(), "INBOX");
        viewPager.setAdapter(adapter);




    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
