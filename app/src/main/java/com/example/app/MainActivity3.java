package com.example.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity3 extends AppCompatActivity {
    private static final String TAG = "MainActivity3";
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private AlertDialog helpDialog;
    private CountDownTimer countDownTimer;
    private Switch btSwitch;
    private boolean confirmationClicked = false;

    private Fragmentliberary fragmentliberary;

    BluetoothAdapter mBluetoothAdapter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean isReceiverRegistered = false; // Track receiver registration

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(mBroadcastReceiver1);
            isReceiverRegistered = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main3);

        // Initialize fragmentliberary
        fragmentliberary = new Fragmentliberary();

        // Add fragment transaction to replace container with Fragmentliberary
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentliberary).commit();
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.Close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setBackground(null);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
        }

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_arduino) {
                    showToast("You clicked on Arduino");
                    device();
                } else if (itemId == R.id.nav_help) {
                    showToast("You clicked on Help");
                    showHelpDialog();
                } else if (itemId == R.id.nav_about) {
                    showToast("You clicked on About Us");
                    about();
                } else if (itemId == R.id.nav_logout) {
                    showToast("You clicked on Logout");
                    logout();
                }else if (itemId == R.id.app_bar_switch) {
                    showToast("Bluetooth settings opened");
                    // Trouver le commutateur dans la vue principale
                    Switch btSwitch = findViewById(R.id.bt_switch);
                    // Vérifier l'état actuel du commutateur
                    boolean isSwitchChecked = btSwitch.isChecked();
                    // Basculer l'état du commutateur
                    btSwitch.setChecked(!isSwitchChecked);
                    // Si le commutateur était précédemment activé, ouvrez les paramètres Bluetooth
                    if (!isSwitchChecked) {
                        Intent intent = new Intent();
                        intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(intent);
                    }
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

        // Register the Bluetooth receiver
        registerReceiver(mBroadcastReceiver1, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        isReceiverRegistered = true;
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help");
        builder.setMessage("Do you want to send a help message to the nearest police station?\nTime remaining: 60 seconds");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmationClicked = true;
                sendHelpMessage(); // إرسال رسالة نجدة عند الضغط على زر التأكيد
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                dialog.dismiss();
            }
        });

        helpDialog = builder.create();
        helpDialog.setCancelable(false);
        helpDialog.show();

        startHelpCountdown();
    }

    @SuppressLint("MissingPermission")
    private void sendHelpMessage() {
        String nom = fragmentliberary.getNomInput();
        String bloodType = fragmentliberary.getBloodInput();
        String phoneNumber = "14"; // رقم الهاتف
        String prenom =fragmentliberary.getprenom();
        String tel = fragmentliberary.getnumerotel();

        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String message = "A person in need of help who has been located by an app detection Accident \nFamilly name: " + nom +"\n name"+prenom +"\nphone nambre:"+tel+"\nBlood type: "+  bloodType + "\nLocation:" + latitude + "," + longitude;

                            try {
                                // إنشاء Intent لإرسال رسالة SMS
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("smsto:" + phoneNumber)); // تعيين رقم الهاتف
                                intent.putExtra("sms_body", message); // تعيين نص الرسالة

                                // إضافة العلم لجعل الـ Intent غير معتمد على تطبيق معين
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                // بدء النشاط
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // عرض رسالة Toast في حال فشل الإرسال
                                Toast.makeText(MainActivity3.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity3.this, "Failed to get location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                sendHelpMessage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }private void startHelpCountdown() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (helpDialog != null && helpDialog.isShowing()) {
                    helpDialog.setMessage("Do you want to send a help message to the nearest police station?\nTime remaining: " + millisUntilFinished / 1000 + " seconds");
                }
            }

            @Override
            public void onFinish() {
                if (helpDialog != null && helpDialog.isShowing() && !confirmationClicked) {
                    sendHelpMessage(); // إرسال رسالة نجدة عند انتهاء الوقت بدون تأكيد
                    helpDialog.dismiss();
                }
            }
        }.start();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Empty methods for handling other navigation items
    private void device() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Volume up to turn flash on");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.initiateScan();
    }

    private void about() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new aboutFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void logout() {
        Intent intent = new Intent(MainActivity3.this, MainActivity.class);
        startActivity(intent);
        showToast("Logged out");
    }

    public void ok1(View view) {
        Fragmentliberary fragment = new Fragmentliberary();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(true);
    }

    public void home1(View view) {
        Fragment_home fragment = new Fragment_home();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
    }

    public void carte1(View view) {
        ShareFragment fragment = new ShareFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        bottomNavigationView.getMenu().findItem(R.id.carte).setChecked(true);
    }

    public void gps1(View view) {
        SettingsFragment fragment = new SettingsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        bottomNavigationView.getMenu().findItem(R.id.GPS).setChecked(true);
    }

    public void ok(View view) {
        TextView aInput = findViewById(R.id.a_input);
        TextView bInpute = findViewById(R.id.b_input);
        TextView cInput = findViewById(R.id.c_input);

        String aContent = aInput.getText().toString().trim();
        String bContent = bInpute.getText().toString().trim();
        String cContent = cInput.getText().toString().trim();

        if (aContent.isEmpty()) {
            return;
        }

        if (!aContent.isEmpty() && !bContent.isEmpty() && !cContent.isEmpty()) {
            String message = "de :" + aContent +
                    "/ a :" + bContent +
                    "/ camion :" + cContent +
                    "/ date et temps  :" + getDateTime();

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            aInput.setText("");
            bInpute.setText("");
            cInput.setText("");
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd  - HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                showToast(result.getContents());
            } else {
                showToast("Scan canceled");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }}

