package com.primarynet.fellow4u;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends Activity {
    private static final String TAG = "Fellow4U";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        grantPermission();

		/*
		안드로이드 Activity가 바로 실행 될 때 Network으로 웹서버 등에 접속 시 android.os.NetworkOnMainThreadException이 발생합니다.
		이러한 경우 Thread를 이용하여 Network에 접속해야합니다.
		 */

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
                finish();
            }
        }, 1000);
    }


    private boolean grantPermission() {
        if (Build.VERSION.SDK_INT > 22) {

            final List<String> permissionsList = new ArrayList<String>();       // Needed Permission
            final List<String> permissionsRequest = new ArrayList<String>();    // Not Granted List

            // Don't need GPS permission on fellow4u
            //permissionsList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            //permissionsList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsList.add(android.Manifest.permission.READ_PHONE_STATE);
            permissionsList.add(android.Manifest.permission.INTERNET);
            permissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionsList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionsList.add(android.Manifest.permission.CAMERA);

            for (int i = 0; i < permissionsList.size(); i++) {
                if (ContextCompat.checkSelfPermission(this, permissionsList.get(i))!= PackageManager.PERMISSION_GRANTED) {
                    permissionsRequest.add(permissionsList.get(i));
                }
            }

            if (permissionsRequest.size() > 0) {
                // go ahead and request permissions
                String[] sArrays = permissionsRequest.toArray( new String[permissionsRequest.size()] );
                ActivityCompat.requestPermissions(this, sArrays, 1);
            } else {
                // no permission need to be asked so all good...we have them all.
            }

            while (true) {
                int nRequestTotal = permissionsRequest.size();
                int nCount = 0;
                for (int i = 0; i < nRequestTotal; i++) {
                    if (ContextCompat.checkSelfPermission(this, permissionsRequest.get(i))== PackageManager.PERMISSION_GRANTED) {
                        nCount++;
                    }
                }

                if(nCount == nRequestTotal)
                {
                    Log.i("intro", "Got permissions, exiting block loop");

                    break;
                }

                Log.i("intro", "Sleeping, waiting for permissions");
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }

        return true;
    }

}
