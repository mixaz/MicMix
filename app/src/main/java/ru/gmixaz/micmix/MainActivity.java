package ru.gmixaz.micmix;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private TextView text;
    private EditText edt2;
    private EditText edt3;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    execCommand("mount -o rw,remount,rw /system");
                    Toast.makeText(MainActivity.this,"command executed OK",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    text.setText(e.getMessage());
                }
            }
        });

        Button btnReboot = (Button) findViewById(R.id.reboot);
        btnReboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    execCommand("reboot");
                    Toast.makeText(MainActivity.this,"command executed OK",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    text.setText(e.getMessage());
                }
            }
        });

        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String ss = execCommand("mount|grep system");
                    if(ss.contains("rw")) {
                        ss = edt2.getText().toString();
                        pref.edit()
                                .putString("edt2", ss)
                                .putString("curr", ss)
                                .commit();
                        copyFile("mixer_paths.xml", "%DEC1_VOL%", ss, "/data/data/" + getPackageName() + "/");
                        execCommand("cp /data/data/" + getPackageName() + "/mixer_paths.xml /system/etc/");
                        text.setText("Current value: " + ss);
                    }
                    else {
                        text.setText("Mount system RW first!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    text.setText(e.getMessage());
                }
            }
        });

        Button btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String ss = execCommand("mount|grep system");
                    if(ss.contains("rw")) {
                        ss = edt3.getText().toString();
                        pref.edit()
                                .putString("edt3", ss)
                                .putString("curr", ss)
                                .commit();
                        copyFile("mixer_paths.xml", "%DEC1_VOL%", ss, "/data/data/" + getPackageName() + "/");
                        execCommand("cp /data/data/" + getPackageName() + "/mixer_paths.xml /system/etc/");
                        text.setText("Current value: "+ss);
                    }
                    else {
                        text.setText("Mount system RW first!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    text.setText(e.getMessage());
                }
            }
        });

        edt2 = (EditText) findViewById(R.id.edit2);
        edt2.setText(pref.getString("edt2","84"));
        edt3 = (EditText) findViewById(R.id.edit3);
        edt3.setText(pref.getString("edt3","90"));

        text = (TextView) findViewById(R.id.text);
        text.setText("Current value: "+pref.getString("curr",""));
    }

    /*
     * https://stackoverflow.com/a/6953432/1028256
     */
    private String execCommand(String cmd) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "system/bin/sh"});
        DataOutputStream stdout = new DataOutputStream(p.getOutputStream());

        stdout.writeBytes(cmd);
        stdout.writeByte('\n');
        stdout.flush();
        stdout.close();

        BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
        char[] buffer = new char[1024];
        int read;
        StringBuffer out = new StringBuffer();

        while((read = stdin.read(buffer)) > 0) {
            out.append(buffer, 0, read);
        }
        stdin.close();
        p.waitFor();
        return out.toString();
    }

     private void copyFile(String filename, String find, String replace, String toPath) throws IOException {
        AssetManager assetManager = this.getAssets();

        InputStream in = null;
        OutputStream out = null;

            in = assetManager.open(filename);
            String newFileName = toPath + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            String ss = "";
            while ((read = in.read(buffer)) != -1) {
                ss += new String(buffer,0,read);
            }
            ss = ss.replace(find,replace);
            out.write(ss.getBytes());
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
