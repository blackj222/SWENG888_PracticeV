package edu.psu.sweng888.practicev.fragments;

import androidx.fragment.app.Fragment;

// Base class to override onBackPressed()
public abstract class BaseFragment extends Fragment {
    public boolean onBackPressed() {
        return false; // override in subclasses if needed
    }
}


