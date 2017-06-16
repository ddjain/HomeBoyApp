package deva.com.homeboy;

import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DARSHAN on 24-03-2017.
 */

public class Test extends AppCompatActivity implements TextToSpeech.OnInitListener{
    static String ip;

    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    int c = 0;
    TextView tv;
    Switch s1, s2, s3, s4;
    FloatingActionButton fab;
    String nodeIp = "http://192.168.1.108";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        initializeComponents();
        callAsynchronousTask();
        tts = new TextToSpeech(this, this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                voiceTotext();
            }
        });
    }

    public void txtCmd(View v) {
        EditText et11 = (EditText) findViewById(R.id.editText3);
        String cmd = et11.getText().toString();
        recogonizeCommand(cmd);
    }

    public void voiceTotext() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void recogonizeCommand(String command) {
        String[] parts = command.split(" ");
        String key = "";
        if (parts[0].equals("play")) {
            key = parts[1];
            for (int i = 2; i < parts.length; i++) {
                key = key + " " + parts[i];
            }
            Toast.makeText(this, "" + key, Toast.LENGTH_SHORT).show();
            new playSongAsync().execute(key);
        } else if (parts[0].equals("connect") || parts[0].equals("pair") && parts[1].equals("me")) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Bluetooth is already Enable", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Turning on the bluetooth", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.enable();
                Toast.makeText(this, "bluetooth is on", Toast.LENGTH_SHORT).show();
            }
        } else {
            int c = 0;
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("turn") || parts[i].equals("off") || parts[i].equals("light")) {
                    c++;
                }
            }
            if (c == 3) {
                new cmdRecog().execute("stop the light");

            } else {
                new cmdRecog().execute(command);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    recogonizeCommand("" + result.get(0));
                    Toast.makeText(this, "" + result.get(0), Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

    private void initializeComponents() {
        tv = (TextView) findViewById(R.id.textView6);
        s1 = (Switch) findViewById(R.id.switch1);
        s2 = (Switch) findViewById(R.id.switch2);
        s3 = (Switch) findViewById(R.id.switch3);
        s4 = (Switch) findViewById(R.id.switch4);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        ip = PinBox.ip;
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new getStatus().execute();
                            //tv.setText(""+c);
                            //c++;
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 50000 ms

    }

    private void parseJSONARRAY(String s) {
        try {
            String ss1, ss2, ss3, ss4;
            JSONObject jb;
            JSONArray ja = new JSONArray(s);
            jb = ja.getJSONObject(0);
            ss1 = jb.getString("status");
            jb = ja.getJSONObject(1);
            ss2 = jb.getString("status");
            jb = ja.getJSONObject(2);
            ss3 = jb.getString("status");
            jb = ja.getJSONObject(3);
            ss4 = jb.getString("status");
            changeSwitch(ss1, ss2, ss3, ss4);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeSwitch(String ss1, String ss2, String ss3, String ss4) {
        if (ss1.equals("1")) {
            s1.setChecked(true);
        } else {
            s1.setChecked(false);
        }

        if (ss2.equals("1")) {
            s2.setChecked(true);
        } else {
            s2.setChecked(false);
        }

        if (ss3.equals("1")) {
            s3.setChecked(true);
        } else {
            s3.setChecked(false);


        }


        if (ss4.equals("1")) {
            s4.setChecked(true);
        } else {
            s4.setChecked(false);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {

            } else {

            }

        } else {
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
                URL url = new URL(ip + "/HomeBoy/php/song.php?keyword=" + key);
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
            s.trim();
            Toast.makeText(Test.this, s + "", Toast.LENGTH_LONG).show();
            MediaPlayer mediaPlayer;
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(ip + "/HomeBoy/Node/song/" + s);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private class cmdRecog extends AsyncTask<String, Void, String> {
        String err = "";
        String type;
        String res = "";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            String key = strings[0];
            key.trim();
            try {
                URL url = new URL(ip + "/HomeBoy/php/find_command.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST" +
                        "");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.flush();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
                String data = URLEncoder.encode("cmd", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");
                bufferedWriter.flush();
                bufferedWriter.write(data);
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
            s.trim();
            Toast.makeText(Test.this, s + "", Toast.LENGTH_LONG).show();

            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);

        }
    }

    private class getStatus extends AsyncTask<String, Void, String> {
        String err = "";

        String res = "";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(ip + "/HomeBoy/php/getStatusJ.php");
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
            parseJSONARRAY(s);
        }


    }

    private class chngStatus extends AsyncTask<String, Void, String> {
        String err = "";

        String res = "";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            String s = strings[0];
            try {
                URL url = new URL(nodeIp + s);
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

            Toast.makeText(Test.this, s + "", Toast.LENGTH_SHORT).show();
        }


    }


}
