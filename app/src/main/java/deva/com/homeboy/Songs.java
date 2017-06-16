package deva.com.homeboy;

import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
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
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by DARSHAN on 13-03-2017.
 */

public class Songs extends AppCompatActivity {
    protected static final int RESULT_SPEECH = 1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    EditText et;
    TextToSpeech tts;
    int result;
    TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        //new playSongAsync().execute("sorr");
        et = (EditText) findViewById(R.id.editText2);
        tv = (TextView) findViewById(R.id.textView);


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

    public void voiceTotext(View v) {

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


    public void postCommand(View v) {
        String command = et.getText().toString();
        recogonizeCommand(command);
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
            int c=0;
            for(int i=0;i<parts.length;i++)
            {
                if(parts[i].equals("turn") || parts[i].equals("off") ||parts[i].equals("light")  )
                {
                    c++;
                }
            }
            if(c==3)
            {
                new cmdRecog().execute("stop the light");

            }
            else {
                new cmdRecog().execute(command);
            }
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
                URL url = new URL("http://192.168.1.104/HomeBoy/php/song.php?keyword=" + key);
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
            Toast.makeText(Songs.this, s + "", Toast.LENGTH_LONG).show();
            MediaPlayer mediaPlayer;
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource("http://192.168.1.104/HomeBoy/Node/song/" + s);
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
                URL url = new URL("http://192.168.1.104/HomeBoy/php/find_command.php?cmd=" + key);
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
            Toast.makeText(Songs.this, s + "", Toast.LENGTH_LONG).show();

        }
    }











}
