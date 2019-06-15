package com.trainex.uis.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.diaglog.AlertLoginDialog;
import com.trainex.diaglog.CustomProgressDialog;
import com.trainex.fragment.inmain.AboutUsFragment;
import com.trainex.fragment.inmain.ContactUsFragment;
import com.trainex.fragment.inmain.DetailTrainerFragment;
import com.trainex.fragment.inmain.FavoritesFragment;
import com.trainex.fragment.inmain.MyBookingFragment;
import com.trainex.fragment.inmain.ProfileFragment;
import com.trainex.fragment.inmain.GroupClassEventsFragment;
import com.trainex.fragment.inmain.HomeFragment;
import com.trainex.fragment.inmain.ListCoachFragment;
import com.trainex.fragment.inmain.MenuFragment;
import com.trainex.fragment.inmain.RegisterTrainerFragment;
import com.trainex.rest.RestClient;
import com.trainex.uis.login_signup.LoginActivity;
import com.trainex.uis.login_signup.StartActivity;

public class MainActivity extends AppCompatActivity {
    private FrameLayout containerMain;

    private static HomeFragment homeFragment;
    private static MenuFragment menuFragment;
    private static ListCoachFragment listTrain1on1, listTrainOnline, listEMS, listMassage;
    private static DetailTrainerFragment detailTrainerFragment;
    private static GroupClassEventsFragment groupClassEventsFragment;
    private static FavoritesFragment favoritesFragment;
    private static AppCompatActivity mainActivity;
    private static ProfileFragment profileFragment;
    private static ContactUsFragment contactUsFragment;
    private static AboutUsFragment aboutUsFragment;
    private static RegisterTrainerFragment registerTrainerFragment;
    private static MyBookingFragment myBookingFragment;
    private static BottomNavigationView bottomNav;
    private static boolean isInProfile = false;
    private static int STATESTACK;
    private static final int STACK = 1;
    private static final int NOT_STACK = 0;
    private static final int IN_HOME = 2;
    private Bundle saveState = new Bundle();
    public static boolean isInDetailFavorite = false;
    public static int PERMISSION_CALL = 99;
    public static int PERMISSION_READ_STORAGE = 98;

    public static DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout content;
    private static boolean isOpeningDrawer;
    private String token;
    private static ActionBarDrawerToggle drawerToggleShowFragment, actionBarDrawerToggle, drawerToggleStartActivty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (saveState != null) {
            STATESTACK = saveState.getInt("STATESTACK");
        }
        init();
        bind();
        SharedPreferences prefs = getSharedPreferences("MY_SHARE_PREFERENCE", MODE_PRIVATE);
        token = prefs.getString("token", "");
        Log.e("token", token);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveState.putInt("STATESTACK", STATESTACK);
    }

    private void init() {
        containerMain = findViewById(R.id.containerMain);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nvView);
        content = (LinearLayout) findViewById(R.id.content);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottomNav);
    }

    private void bind() {
        isOpeningDrawer = false;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isOpeningDrawer = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                isOpeningDrawer = false;
            }
        };
        MenuFragment.SelectMenu selectMenu = new MenuFragment.SelectMenu() {
            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }

            @Override
            public void setOnMenuSelection(int i) {
                if (i == 0) {
                    showFragmentOfMenu(homeFragment, true, R.id.item_bottom_home);
                } else if (i == 1) {
                    if (!token.equalsIgnoreCase("")) {
                        showFragmentOfMenu(myBookingFragment, true, -1);
                    } else {
                        AlertLoginDialog alertLoginDialog = new AlertLoginDialog(MainActivity.this, "Error!", "You must login to see your bookings");
                        alertLoginDialog.show();
                    }
                }  else if (i == 2) {
                    if (!token.equalsIgnoreCase("")) {
                        startActivityFromMenu(NotificationActivity.class);
                    } else {
                        AlertLoginDialog alertLoginDialog = new AlertLoginDialog(MainActivity.this, "Error!", "You must login to see your notifications");
                        alertLoginDialog.show();
                    }

                } else if (i == 3) {
                    if (!token.equalsIgnoreCase("")) {
                        showFragmentOfMenu(favoritesFragment, false, R.id.item_bottom_favorites);
                    } else {
                        AlertLoginDialog alertLoginDialog = new AlertLoginDialog(MainActivity.this, "Error!", "You must login to see your favorite trainers");
                        alertLoginDialog.show();
                    }

                } else if (i == 4) {

                    showFragmentOfMenu(aboutUsFragment, true, -1);
                } else if (i == 5) {
                    startActivityFromMenu(TermActivity.class);
                } else if (i == 6) {
                    if (!token.equalsIgnoreCase("")) {
                        showFragmentOfMenu(profileFragment, true, R.id.item_bottom_profile);
                    } else {
                        AlertLoginDialog alertLoginDialog = new AlertLoginDialog(MainActivity.this, "Error!", "You must login to see your profiles");
                        alertLoginDialog.show();
                    }

                } else if (i == 7) {
                    if (!token.equalsIgnoreCase("")) {
                        showFragmentOfMenu(contactUsFragment, true, -1);
                    } else {
                        AlertLoginDialog alertLoginDialog = new AlertLoginDialog(MainActivity.this, "Error!", "You must login to send contact us");
                        alertLoginDialog.show();
                    }

                } else if (i == 8) {
                    startActivityFromMenu(ShareAppActivity.class);
                }else if(i==9){
                    if (!token.equalsIgnoreCase("")) {
                        AlertLoginDialog dialog = new AlertLoginDialog(MainActivity.this,"Warning","Do you want to logout","Logout","Cancel");
                        dialog.show();
                    } else {
                        AlertLoginDialog alertLoginDialog = new AlertLoginDialog(MainActivity.this, "Error!", "You must login");
                        alertLoginDialog.show();
                    }

                }
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        mainActivity = this;
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        contactUsFragment = new ContactUsFragment();
        aboutUsFragment = new AboutUsFragment();
        registerTrainerFragment = new RegisterTrainerFragment();
        myBookingFragment = new MyBookingFragment();

        menuFragment = new MenuFragment();
        Bundle bundleMenu = new Bundle();
        bundleMenu.putParcelable("menuSelect", selectMenu);
        menuFragment.setArguments(bundleMenu);


        groupClassEventsFragment = new GroupClassEventsFragment();
        favoritesFragment = new FavoritesFragment();
        replaceFragment(homeFragment, 0, R.anim.anim_out_to_left, R.anim.anim_in_from_left, R.anim.anim_out_to_left, false);

        FragmentManager manager = mainActivity.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.layoutNavigation, menuFragment);
        ft.commit();

        //Bottom nav
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.item_bottom_home) {
                    replaceFragment(homeFragment, R.anim.anim_in_from_left, R.anim.anim_out_to_right, R.anim.anim_in_from_left, R.anim.anim_out_to_right, false);
                    menuItem.setIcon(getResources().getDrawable(R.drawable.toggle_icon_home));
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    return true;
                } else if (menuItem.getItemId() == R.id.item_bottom_favorites) {
                    if (!token.equalsIgnoreCase("")) {
                        replaceFragment(favoritesFragment, R.anim.anim_in_from_left, R.anim.anim_out_to_right, 0, 0, false);
                        menuItem.setIcon(getResources().getDrawable(R.drawable.toggle_icon_heart));
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        return true;
                    } else {
                        AlertLoginDialog alertLoginDialog = new AlertLoginDialog(MainActivity.this, "Error!", "You must login to see your favorites trainers");
                        alertLoginDialog.show();
                        return true;
                    }

                } else {
                    if (!token.equalsIgnoreCase("")) {
                        replaceFragment(profileFragment, R.anim.anim_in_from_right, R.anim.anim_out_to_left, R.anim.anim_in_from_left, R.anim.anim_out_to_right, false);
                        menuItem.setIcon(getResources().getDrawable(R.drawable.toggle_icon_profile));
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        return true;
                    } else {
                        AlertLoginDialog alertLoginDialog = new AlertLoginDialog(MainActivity.this, "Error!", "You must login to see your profile");
                        alertLoginDialog.show();
                        return true;
                    }

                }
            }
        });
        bottomNav.setSelectedItemId(bottomNav.getMenu().getItem(0).getItemId());
    }

    private static void startActivityFromMenu(final Class<?> cls) {

        mainActivity.startActivity(new Intent(mainActivity, cls));
        drawerLayout.removeDrawerListener(drawerToggleStartActivty);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        drawerLayout.removeDrawerListener(actionBarDrawerToggle);
        drawerLayout.addDrawerListener(drawerToggleStartActivty);
        isOpeningDrawer = false;
        drawerLayout.closeDrawer(Gravity.START);
    }

    public static void showMenu() {
        drawerLayout.openDrawer(Gravity.START);
    }

    public static void closeMenu() {
        drawerLayout.closeDrawer(Gravity.START);
    }

    public static void showHomeFromLeft() {
        bottomNav.setSelectedItemId(bottomNav.getMenu().getItem(0).getItemId());
    }

    public static void showListTrainer(Fragment fragment, String title, int idCategory) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("idCategory", idCategory);
        fragment.setArguments(bundle);
        replaceFragment(fragment, R.anim.anim_in_from_right, R.anim.anim_out_to_left, R.anim.anim_in_from_left, R.anim.anim_out_to_right, true);
    }


    public static void showDetailTrainer(int idTrainer, int idCategory) {
        detailTrainerFragment = new DetailTrainerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("idTrainer", idTrainer);
        bundle.putInt("idCategory", idCategory);
        detailTrainerFragment.setArguments(bundle);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        replaceFragment(detailTrainerFragment, R.anim.anim_in_from_right, R.anim.anim_out_to_left, R.anim.anim_in_from_left, R.anim.anim_out_to_right, true);
    }

    public static void showGroupClassEvents() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        replaceFragment(groupClassEventsFragment, R.anim.anim_in_from_right, R.anim.anim_out_to_left, R.anim.anim_in_from_left, R.anim.anim_out_to_right, false);
    }

    public static void showFragmentOfMenu(final Fragment fragment, boolean canOpenDrawer, final int idItemMenu) {
        drawerToggleShowFragment = new ActionBarDrawerToggle(mainActivity, drawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                isOpeningDrawer = false;

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isOpeningDrawer = true;
            }
        };

        if (idItemMenu == -1) {
            bottomNav.getMenu().getItem(0).setChecked(true);

            replaceFragment(fragment, R.anim.anim_in_from_right, R.anim.anim_out_to_left, R.anim.anim_in_from_left, R.anim.anim_out_to_right, false);
        } else {
            bottomNav.setSelectedItemId(idItemMenu);
        }
        drawerLayout.removeDrawerListener(drawerToggleShowFragment);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        drawerLayout.removeDrawerListener(actionBarDrawerToggle);
        drawerLayout.addDrawerListener(drawerToggleShowFragment);

        drawerLayout.closeDrawer(Gravity.START);


        if (canOpenDrawer) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        if (fragment == profileFragment) {
            isInProfile = true;
        } else {
            isInProfile = false;
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (isOpeningDrawer) {
            drawerLayout.removeDrawerListener(drawerToggleShowFragment);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            drawerLayout.closeDrawer(Gravity.START);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else if (isInDetailFavorite) {
            replaceFragment(favoritesFragment, R.anim.anim_in_from_left, R.anim.anim_out_to_right, 0, 0, false);
            isInDetailFavorite = false;
        } else {
            if (STATESTACK == IN_HOME) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                Log.e("here", "In home");
                if (doubleBackToExitPressedOnce) {
                    Intent intent = new Intent(this, StartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Exit me", true);
                    startActivity(intent);
                    finish();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                if (STATESTACK == NOT_STACK) {
                    showHomeFromLeft();
                } else {
                    if (homeFragment.isAdded() && homeFragment.isVisible()) {
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        Log.e("here", "In home");
                        if (doubleBackToExitPressedOnce) {
                            Intent intent = new Intent(this, StartActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("Exit me", true);
                            startActivity(intent);
                            finish();
                            return;
                        }

                        this.doubleBackToExitPressedOnce = true;
                        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                doubleBackToExitPressedOnce = false;
                            }
                        }, 2000);
                    } else {
                        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
                            super.onBackPressed();
                            Log.e("here", "Stack false123");
                        } else {
                            showHomeFromLeft();
                            Log.e("here", "Stack false");
                        }
                    }

                }
            }
        }

    }

    private static void replaceFragment(Fragment fragment, int animIn, int animOut, int animBackIn, int animBackOut, boolean isBackStack) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;
        isInDetailFavorite = false;
        FragmentManager manager = mainActivity.getSupportFragmentManager();
        boolean fragmentPopped;

        if (fragment == profileFragment) {
            isInProfile = true;
        } else {
            isInProfile = false;
        }
        if (isBackStack) {
            fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
            if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.containerMain, fragment, fragmentTag);
                ft.addToBackStack(backStateName);
                ft.commit();
            } else {
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.containerMain, fragment, fragmentTag);
                ft.commit();
            }
        } else {
            for (int i = 0; i < manager.getBackStackEntryCount(); ++i) {
                manager.popBackStack();
            }
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.containerMain, fragment, fragmentTag);
            ft.commit();
        }
        if (fragment == homeFragment) {
            STATESTACK = IN_HOME;
        } else {
            if (isBackStack) {
                STATESTACK = STACK;
            } else {
                STATESTACK = NOT_STACK;
            }
        }

        Log.e("STATE STACK", STATESTACK + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 || requestCode == 2 || requestCode == 3) {
            registerTrainerFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == DetailTrainerFragment.REQUEST_REVIEWS) {
            if (resultCode == RESULT_OK) {
                detailTrainerFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == 4) {
            profileFragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestPerMissionReadStorage(String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
        } else {
            if (requestCode == PERMISSION_READ_STORAGE) {
                Toast.makeText(this, "Turn on read storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            //Internet
            if (requestCode == PERMISSION_CALL) {
                Toast.makeText(this, "Try again to call", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PERMISSION_READ_STORAGE) {
                Toast.makeText(this, "Turn on read storage", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }


    }
}
