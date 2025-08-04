// MainActivity.java - Hosts the main UI with navigation drawer, handles fragment switching and Firebase authentication

package edu.psu.sweng888.practicev;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.libraries.places.api.Places;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Objects;

import edu.psu.sweng888.practicev.fragments.AccountFragment;
import edu.psu.sweng888.practicev.fragments.ItemsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth mAuth; // Firebase authentication

    // Navigation constants
    private static final int MENU_ITEMS = 1;
    private static final int MENU_ACCOUNT = 2;
    private static final int MENU_SETTINGS = 3;
    private static final int MENU_LOGOUT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Default fragment display
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ItemsFragment())
                .commit();
        }

        // Handle back press to navigate fragment stack
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, new ItemsFragment())
                        .commit();
                }
            }
        });

        // Initialize Google Places if not already initialized
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key));
        }

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize drawer components
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize Firebase auth and user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Populate header with user info if available
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.textViewUserName);
        TextView navUserEmail = headerView.findViewById(R.id.textViewUserEmail);

        if (user != null) {
            navUserEmail.setText(user.getEmail());
            String navUserNameString = "Welcome, " + user.getDisplayName() + "!";
            navUserName.setText(navUserNameString);
        }

        // Set default selected item and fragment
        if (savedInstanceState == null) {
            loadFragment(MENU_ITEMS);
            navigationView.setCheckedItem(R.id.nav_items);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int menuKey = getMenuKey(item.getItemId());
        handleMenuAction(menuKey);
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    // Translate menu item ID into logical constant
    private int getMenuKey(int menuId) {
        if (menuId == R.id.nav_items) return MENU_ITEMS;
        if (menuId == R.id.nav_account) return MENU_ACCOUNT;
        if (menuId == R.id.nav_logout) return MENU_LOGOUT;
        return -1;
    }

    // Respond to selected menu option
    private void handleMenuAction(int menuKey) {
        Fragment selectedFragment = null;

        if (menuKey == MENU_ITEMS) {
            selectedFragment = new ItemsFragment();
        } else if (menuKey == MENU_ACCOUNT) {
            selectedFragment = new AccountFragment();
        } else if (menuKey == MENU_LOGOUT) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        if (selectedFragment != null) {
            loadFragment(menuKey);
        }
    }

    // Replaces fragment container with selected fragment
    private void loadFragment(int menuKey) {
        Fragment fragment = null;
        if (menuKey == MENU_ITEMS) {
            fragment = new ItemsFragment();
        } else if (menuKey == MENU_ACCOUNT) {
            fragment = new AccountFragment();
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        }
    }

    // Ensure locale is applied from preferences before loading resources
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        String langCode = prefs.getString("app_lang", Locale.getDefault().getLanguage());

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    // Handle toolbar menu actions (drawer toggle or back)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_drawer_toggle) {
            drawerLayout.openDrawer(GravityCompat.END);
            return true;
        } else if (itemId == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
