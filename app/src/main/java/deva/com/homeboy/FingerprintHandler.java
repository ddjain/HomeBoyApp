package deva.com.homeboy;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by DARSHAN on 02-06-2017.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
class FingerprintHandler extends FingerprintManager.AuthenticationCallback {


    private Context context;


    // Constructor
    public FingerprintHandler(Context mContext) {
        context = mContext;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update("Fingerprint Authentication error\n" + errString, false);
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update("Fingerprint Authentication help\n" + helpString, false);
    }


    @Override
    public void onAuthenticationFailed() {
        this.update("Fingerprint Authentication failed.", false);
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Fingerprint Authentication succeeded.", true);
    }


    public void update(String e, Boolean success){
        if(success){
            new checkPws().execute(PinBox.imei);

        }
    }



    private class checkPws extends AsyncTask<String, Void, String> {
        String err = "";
        String type;
        String res = "";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];
            try {
                URL url = new URL(PinBox.ip + "/HomeBoy/php/checkpws.php?pws=" + token);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.flush();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));

                bufferedWriter.flush();

                bufferedWriter.flush();
                bufferedWriter.close();
                os.flush();
                os.close();
                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String result = "", line;
                while ((line = bufferedReader.readLine()) != null) {
                    res = res + line;
                }
                bufferedReader.close();
                inputStream.close();
                return res;
            } catch (Exception e1) {
                e1.printStackTrace();
                err = err + e1;
            }
            return " " + err;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("1")) {
                Toast.makeText(context, "ThumbPrint is valid...", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(context, Test.class);
                context.startActivity(i);
            } else {
                Toast.makeText(context, "ThumbPrint is invalid...", Toast.LENGTH_SHORT).show();
            }

        }
    }







}