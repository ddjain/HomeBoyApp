package deva.com.homeboy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by DARSHAN on 28-04-2017.
 */

public class Tt extends AppCompatActivity {
    EditText et1, et2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tt);
        et1 = (EditText) findViewById(R.id.editText4);
        et2 = (EditText) findViewById(R.id.editText7);

    }

    public void plus(View v)
    {
        int a=Integer.valueOf(et1.getText().toString());
        int b=Integer.valueOf(et2.getText().toString());
        int c=a+b;
        Toast.makeText(this, "Addlitoin is"+c, Toast.LENGTH_SHORT).show();

    }
    public void min(View v)
    {

    }

}
