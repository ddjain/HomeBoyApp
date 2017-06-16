package deva.com.homeboy;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    MediaPlayer mediaPlayer;
    TextView tv;
    EditText et;
    String s1Status, s2Status, s3Status, s4Status;
    String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkStatus();
        tv = (TextView) findViewById(R.id.textView);
        // getCurrentStatus();
        et = (EditText) findViewById(R.id.editText);


        //Disable bluetooth


        /*Uri myUri = Uri.parse("http://192.168.1.185/HomeBoy/song/13.mp3");
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(myUri, "audio");
        startActivity(intent);
*/

        String url = "http://192.168.1.185/HomeBoy/song/a b.mp3";
        url.replaceAll(" ", "%20");
        Toast.makeText(this, url + "", Toast.LENGTH_SHORT).show();

    }

    public void postCommand(View v) {
        String command = et.getText().toString();
        String[] parts = command.split(" ");
        String keywords;
        if (isSong(parts[0])) {
            new playSongAsync().execute(findSongKeywords(parts));
        }
       else if(parts[0].equals("connect") || parts[0].equals("pair") && parts[1].equals("me") )
        {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
           if(mBluetoothAdapter.isEnabled())
           {
               Toast.makeText(this, "Bluetooth is already Enable", Toast.LENGTH_SHORT).show();
           }
            else {
               Toast.makeText(this, "Turning on the bluetooth", Toast.LENGTH_SHORT).show();
               mBluetoothAdapter.enable();
               Toast.makeText(this, "bluetooth is on", Toast.LENGTH_SHORT).show();

           }
        }
        else{

            keywords=extractKeywords(parts);

        }


    }

    private String extractKeywords(String[] parts) {
    String key="";

        for(int i=0;i<parts.length;i++)
        {
            if(parts[i].equals("turn") || parts[i].equals("on") || parts[i].equals("off") || parts[i].equals("light") || parts[i].equals("fan")  || parts[i].equals("on"))
            key=key+" "+parts[i];
        }


        return key;
    }

    public boolean isSong(String keyword) {
        if (keyword.equals("play"))
            return true;
        else
            return false;
    }


    private String findSongKeywords(String[] parts) {
        String keywords = "";
        for (int i = 1; i < parts.length; i++) {

            if (parts[i].equals("the") || parts[i].equals("song")) {
                continue;
            } else {
                keywords = keywords + " " + parts[i];
                Toast.makeText(this, " " + keywords, Toast.LENGTH_SHORT).show();
            }
        }
        return keywords;
    }


     /*   for (int i = 0; i < parts.length; i++) {
              if (parts[i].equals("turn") || parts[i].equals("on") || parts[i].equals("light") || parts[i].equals("off") || parts[i].equals("fan") || parts[i].equals("ac")) {
                keywords = keywords + " " + parts[i];
            }


        }*/


    public void playSong(View v) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource("http://songs1.djmazadownload.com/music/Singles/Ae%20Dil%20Hai%20Mushkil%20(MTV%20Unplugged)%20-%20Jubin%20Nautiyal%20[DJMaza.Life].mp3");
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getCurrentStatus() {
        new getStatus().execute("s1");
        new getStatus().execute("s2");
        new getStatus().execute("s3");
        new getStatus().execute("s4");

    }

    private void checkStatus() {


    }

    public void s1Switch(View v) {

        new SwitchAsync().execute("s1", "ON", "Darshan");

        getCurrentStatus();
    }

    public void s2Switch(View v) {

        new SwitchAsync().execute("s2", "ON", "Darshan");

        getCurrentStatus();
    }

    public void s3Switch(View v) {

        new SwitchAsync().execute("s3", "ON", "Darshan");

        getCurrentStatus();
    }

    public void s4Switch(View v) {

        new SwitchAsync().execute("s4", "OFF", "Darshan");
        getCurrentStatus();
    }

    public void s1Off(View v) {

        new SwitchAsync().execute("s1", "OFF", "Darshan");

        getCurrentStatus();
    }

    public void s2Off(View v) {

        new SwitchAsync().execute("s2", "OFF", "Darshan");
        getCurrentStatus();
    }

    public void s3Off(View v) {

        new SwitchAsync().execute("s3", "OFF", "Darshan");

        getCurrentStatus();
    }

    public void s4Off(View v) {

        new SwitchAsync().execute("s4", "OFF", "Darshan");
        getCurrentStatus();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        mp.stop();
    }


    private class SwitchAsync extends AsyncTask<String, Void, String> {
        String err = "";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            String status = strings[1];
            String username = strings[2];
            try {
                URL url = new URL("http://192.168.1.213/Project/switch.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
                String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") + "&" +
                        URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8") + "&" +
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                bufferedWriter.flush();
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String result = "", line;
                while ((line = bufferedReader.readLine()) != null) {
                    result = result + line;
                }
                inputStream.close();
                return result;
            } catch (Exception e1) {
                e1.printStackTrace();
                err = err + e1;
            }
            return " " + err;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            // tv.setText(s);
            // Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }


    private class getStatus extends AsyncTask<String, Void, String> {
        String err = "";
        ProgressDialog progressDialog;
        String var;
        String type;

        @Override
        protected void onPreExecute() {
        /*    progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();*/
        }

        @Override
        protected String doInBackground(String... strings) {
            type = strings[0];
            try {
                URL url = new URL("http://192.168.1.213/Project/getStatus.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
                String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
                bufferedWriter.flush();
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String result = "", line;
                while ((line = bufferedReader.readLine()) != null) {
                    result = result + line;
                }
                inputStream.close();
                return result;
            } catch (Exception e1) {
                e1.printStackTrace();
                err = err + e1;
            }
            return " " + err;
        }

        @Override
        protected void onPostExecute(String s) {
           /* progressDialog.dismiss();
*/
            if (type.equals("s1")) {
                s1Status = s;

            } else if (type.equals("s2")) {
                s2Status = s;

            } else if (type.equals("s3")) {
                s3Status = s;
            } else if (type.equals("s4")) {

                s4Status = s;
                tv.setText("s1: " + s1Status + " s2:  " + s2Status + " s3: " + s3Status + " S4: " + s4Status);
            }


            // Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }


    private class playSongAsync extends AsyncTask<String, Void, String> {
        String err = "";
        String type;
        String res = "";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            String key = strings[0];
            try {
                URL url = new URL("http://192.168.1.101/HomeBoy/song.php?keyword=" + key);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.flush();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
                /*String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
                */
                bufferedWriter.flush();
               /* bufferedWriter.write(data);
               */
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
            Toast.makeText(MainActivity.this, s + "", Toast.LENGTH_SHORT).show();
        }
    }



}
