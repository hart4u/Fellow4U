package com.primarynet.fellow4u;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.os.Environment;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.io.IOException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    LocationManager locationManager;

    private static final String TAG = "Fellow4U";
    private Context m_context;

    private ImageButton m_btnQuit;
    private ImageButton m_btnShowVersion;
    private ImageButton m_btnDriverMode;
    private ImageButton m_btnUpgrade;

    private WebView m_WebView;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final String TYPE_IMAGE = "image/*";
    private final static int FILECHOOSER_RESULTCODE = 1;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_FROM_JS = 2;

    // 뒤로가기 버튼 컨트롤
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private ProgressDialog progressDialog;

    private PhoneStateListener  m_phoneListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            if(state == TelephonyManager.CALL_STATE_RINGING) {
                Log.d(TAG, "PhoneStateListener : CALL_STATE_RINGING");
//				Toast.makeText(getApplicationContext(), "On Ringing.... " + incomingNumber, Toast.LENGTH_LONG).show();
            }
        }
    };

    class MyWebClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url)	{
            if( url.startsWith("http:") || url.startsWith("https:") ) {
                return false;
            }
            // Otherwise allow the OS to handle it
            else if (url.startsWith("tel:")) {
                Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(tel);
                return true;
            }
            return true;
        }
    }

    // Create an image file
    private File createImageFile() throws IOException {
        Log.d(TAG, "createImageFile");

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    public boolean onKeyDown(int keyCode,KeyEvent event){
        // Back Key press when has web history they Go back (history)
        if((keyCode== KeyEvent.KEYCODE_BACK)&&m_WebView.canGoBack()){
            m_WebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {

            // 서비스 종료시킨다.
            Toast.makeText(this, "App Terminated", Toast.LENGTH_SHORT).show();
            super.onBackPressed();

        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "one more click for quit", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);	// Remove Title Bar
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Fiz for Portrait

        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity::onCreate()");

        m_context = this;//. getApplicationContext();

        // WebView
        m_WebView = (WebView)findViewById(R.id.webview_main);
        m_WebView.setWebViewClient(new MyWebClient());

        m_WebView.setWebChromeClient(new WebChromeClient() {

            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Notification")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();

                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {
                Log.d(TAG, "WebChromeClient >> onJsConfirm");

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirmation")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .create()
                        .show();

                return true;
            }
            // For Android Version < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                Log.d(TAG, "openFileChooser : " + "WebViewActivity OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU), n=1");
                //System.out.println("WebViewActivity OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU), n=1");
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(TYPE_IMAGE);
                startActivityForResult(intent, INPUT_FILE_REQUEST_CODE);
            }

            // For 3.0 <= Android Version < 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                Log.d(TAG, "openFileChooser : " + "WebViewActivity 3<A<4.1, OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU,aT), n=2");
                //System.out.println("WebViewActivity 3<A<4.1, OS Version : " + Build.VERSION.SDK_INT + "\t openFC(VCU,aT), n=2");
                openFileChooser(uploadMsg, acceptType, "");
            }

            // For 4.1 <= Android Version < 5.0
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                Log.d(TAG, "openFileChooser : " + "openFileChooser : "+acceptType+"/"+capture);
                Log.d(getClass().getName(), "openFileChooser : "+acceptType+"/"+capture);
                mUploadMessage = uploadFile;
                imageChooser();
            }

            // For Android Version 5.0+
            // Ref: https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                Log.d(TAG, "openFileChooser : " + "WebViewActivity A>5, OS Version : " + Build.VERSION.SDK_INT + "\t onSFC(WV,VCUB,FCP), n=3");
                System.out.println("WebViewActivity A>5, OS Version : " + Build.VERSION.SDK_INT + "\t onSFC(WV,VCUB,FCP), n=3");
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                imageChooser();
                return true;
            }

            private void imageChooser() {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(getClass().getName(), "Unable to create Image File", ex);
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:"+photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType(TYPE_IMAGE);

                Intent[] intentArray;
                if(takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            }
        });

        WebSettings set = m_WebView.getSettings();
        set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(false);
        set.setAllowContentAccess(true);
        set.setAllowFileAccess(true);

//		m_WebView.getSettings().setPluginsEnabled(true);
//		m_WebView.getSettings().setPluginState(PluginState. ON);

        // Block for Font Size ( > 3.0 )
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) set.setTextZoom(100);

        //m_WebView.loadUrl("http://www.ontrip4u.com/?token="+SharedPrefManager.getInstance(this).getDeviceToken());
        m_WebView.loadUrl("http://www.primarynet.co.kr");

    }

}

