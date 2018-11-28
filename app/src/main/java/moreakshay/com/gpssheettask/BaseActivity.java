package moreakshay.com.gpssheettask;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
/**
 * Created by SID on 2017-04-19.
 */

public class BaseActivity extends AppCompatActivity {

    AlertDialog dialog;

    private String phoneNuber;
    private final int CALL_REQUEST_PERMISSION = 999;
    private final int WRITE_STORAGE_PERMISSION = 990;
    private final int LOCATION_PERMISSION = 995;
    private final int SMS_PERMISSION = 992;
    private final int READ_PERMISSION = 993;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
    }

    public void adjustFontScale(Configuration configuration)
    {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    public boolean isInternetConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public int getDeviceHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        return height;
    }

    public int getDeviceWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager)
                this.getSystemService(this.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void openNoInternetDialog(Context context) {

    }


    public void startActivityWithAnimation(Intent intent, int enterAnimation, int exitAnimation) {
        startActivity(intent);
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog pd = new ProgressDialog(context, R.style.AppTheme);
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.setCanceledOnTouchOutside(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        return pd;
    }

/*
    public void showLoader() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loader, null);
        alertBuilder.setView(view);
        dialog = alertBuilder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        dialog.setCancelable(false);
        dialog.show();
    }
*/

    public void dismissLoader() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }



    public void hideKeyboard(View v) {
        // v is the View which has the focus
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void showKeyboard(View v) {
        // v is the View which has the focus
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(v, 0);
        }
    }

    public void checkLocationStatus() {
        /*LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_delete_confirmation);
            TextViewPlus message = (TextViewPlus) dialog.findViewById(R.id.message);
            ButtonPlus positive = (ButtonPlus) dialog.findViewById(R.id.delete_positive);
            ButtonPlus negative = (ButtonPlus) dialog.findViewById(R.id.delete_negative);
            message.setText(getString(R.string.gps_network_not_enabled));
            positive.setText(getString(R.string.open_location_settings));
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    dialog.dismiss();
                }
            });
            negative.setText(getString(R.string.Cancel));
            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
            dialog.show();
        }*/

    }

    public void permission(String phoneNumber) {
        this.phoneNuber = phoneNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST_PERMISSION);
        }
    }

    PermissionInjector permissionInjector;

    public void getWritePermission(PermissionInjector injector) {
        this.permissionInjector = injector;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION);
            }
        } else {
            injector.permissionGranted();
        }

    }

    public void getLocationPermission(PermissionInjector injector) {
        this.permissionInjector = injector;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            }
        } else {
            injector.permissionGranted();
        }

    }

    public void getMessagePermission(PermissionInjector injector) {
        this.permissionInjector = injector;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION);
            }
        } else {
            injector.permissionGranted();
        }
    }

    public void getReadMessagePermission(PermissionInjector injector) {
        this.permissionInjector = injector;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS}, READ_PERMISSION);
            }
        } else {
            injector.permissionGranted();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_REQUEST_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    String uri = "tel:" + phoneNuber.trim();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // ((BaseActivity) getActivity()).showSnackbar(layoutCreate, "Cannot call without permission");
                }
                break;
            case WRITE_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionInjector.permissionGranted();
                } else {
                    permissionInjector.permissionDenied();
                }
                break;
            case LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionInjector.permissionGranted();
                } else {
                    permissionInjector.permissionDenied();
                }
                break;
            case SMS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionInjector.permissionGranted();
                } else {
                    permissionInjector.permissionDenied();
                }
                break;
            case READ_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionInjector.permissionGranted();
                } else {
                    permissionInjector.permissionDenied();
                }
        }
    }

    public boolean isValidText(String regex, EditText editText, String errorMessage) {
        if (editText.getText().toString().matches(regex) && !editText.getText().toString().isEmpty()) {
            editText.setError(null);
            return true;
        } else {
            editText.setError(errorMessage);
            editText.requestFocus();
            return false;
        }
    }

    public boolean isValidSelection(Spinner spinner, String errorMessage) {
        if (spinner.getSelectedItemPosition() > 0) {
            return true;
        } else {
            TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setError(errorMessage);
            spinner.requestFocus();
            return false;
        }
    }



}
