package edu.psu.sweng888.practicev;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import edu.psu.sweng888.practicev.fragments.AccountFragment;
import edu.psu.sweng888.practicev.fragments.ItemsFragment;
import edu.psu.sweng888.practicev.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase authentication
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Firebase authentication
    private FirebaseAuth mAuth;

    // Instead of switch cases with R.id, define menu keys
    private static final int MENU_ITEMS = 1;
    private static final int MENU_ACCOUNT = 2;
    private static final int MENU_SETTINGS = 3;
    private static final int MENU_LOGOUT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar as the app bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bind DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Add the hamburger menu toggle to open/close the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // sync the hamburger icon with the drawer state

        // Get current user from Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Access the header of the navigation drawer
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.textViewUserName);
        TextView navUserEmail = headerView.findViewById(R.id.textViewUserEmail);

        // If user is signed in, show their email and display name in the drawer header
        if (user != null) {
            navUserEmail.setText(user.getEmail());
            String navUserNameString = "Welcome, " + user.getDisplayName() + "!";
            navUserName.setText(navUserNameString);
        }

        // Load a default fragment when the activity first starts
        if (savedInstanceState == null) {
            loadFragment(MENU_ITEMS); // open ItemsFragment by default
            navigationView.setCheckedItem(R.id.nav_items);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Convert the clicked menu item’s ID to one of our constants
        int menuKey = getMenuKey(item.getItemId());
        // Perform the action for that menu item
        handleMenuAction(menuKey);
        // Close the drawer after selecting an item
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // This method maps R.id to the internal constants
    private int getMenuKey(int menuId) {
        if (menuId == R.id.nav_items) return MENU_ITEMS;
        if (menuId == R.id.nav_account) return MENU_ACCOUNT;
        if (menuId == R.id.nav_settings) return MENU_SETTINGS;
        if (menuId == R.id.nav_logout) return MENU_LOGOUT;
        return -1;
    }

    // Handle what happens when each menu option is selected
    private void handleMenuAction(int menuKey) {
        Fragment selectedFragment = null;

        if (menuKey == MENU_ITEMS) {
            selectedFragment = new ItemsFragment();
        } else if (menuKey == MENU_ACCOUNT) {
            selectedFragment = new AccountFragment();
        } else if (menuKey == MENU_SETTINGS) {
            selectedFragment = new SettingsFragment();
        } else if (menuKey == MENU_LOGOUT) {
            // Sign out from Firebase and go back to LoginActivity
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // close MainActivity so user can’t go back
            return;
        }

        // If a valid fragment was chosen, replace the container with that fragment
        if (selectedFragment != null) {
            loadFragment(menuKey);
        }
    }

    // Load the selected fragment into the main container
    private void loadFragment(int menuKey) {
        Fragment fragment = null;
        if (menuKey == MENU_ITEMS) {
            fragment = new ItemsFragment();
        } else if (menuKey == MENU_ACCOUNT) {
            fragment = new AccountFragment();
        } else if (menuKey == MENU_SETTINGS) {
            fragment = new SettingsFragment();
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    /**
     * This method ensures the app uses the saved locale.
     * It wraps the context with a Configuration that sets the correct language
     * before resources like strings are loaded.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        // Load saved language from SharedPreferences
        SharedPreferences prefs = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        String langCode = prefs.getString("app_lang", Locale.getDefault().getLanguage());

        // Apply the locale
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);

        // Create a new context with this configuration
        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }
}
