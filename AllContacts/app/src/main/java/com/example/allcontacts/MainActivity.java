package com.example.allcontacts;

import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends Activity {
    ListView list;
    LinearLayout ll;

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll = (LinearLayout) findViewById(R.id.LinearLayout1);
        list = (ListView) findViewById(R.id.listView1);

        if(checkPermission(Manifest.permission.READ_CONTACTS)) {
            Log.d("2","dddd checkPermission = true");
            showContactsAsync();
        } else {
            Log.d("3","dddd checkPermission = false");
            requestReadContactsPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("2","dddd in onRequestPermissionsResult");
        if(requestCode == PERMISSION_REQUEST_READ_CONTACTS){
            Log.d("2","dddd ok1");
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("2","dddd ok2");
                showContactsAsync();
            }
            else{
                Log.d("2","dddd not ok");
                Toast.makeText(this, "debug", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkPermission(String permission) {
        Log.d("2","dddd in checkpermission");
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadContactsPermission() {
        Log.d("2","dddd in requestReadContactsPermission");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            Log.d("2","dddd yes");
            Snackbar.make(ll, R.string.read_contacts_access_required, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View V) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    PERMISSION_REQUEST_READ_CONTACTS);
                        }
                    }).show();
        } else {
            Log.d("2","dddd No");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }
    }

    class LoadContactsAsycn extends AsyncTask<Void, Void, ArrayList<String>> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            Log.d("2","dddd in onPre");
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd = ProgressDialog.show(MainActivity.this, "Loading Contacts",
                    "Please Wait");
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            Log.d("2","dddd in doin");
            // TODO Auto-generated method stub
            ArrayList<String> contacts = new ArrayList<String>();

            Cursor c = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);

            int nameColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (c.moveToNext()) {
                String contactName = c.getString(nameColumn);
                String phNumber = c.getString(numberColumn);

                contacts.add(contactName + ":" + phNumber);
            }
            c.close();

            return contacts;
        }

        @Override
        protected void onPostExecute(ArrayList<String> contacts) {
            Log.d("2","dddd in onpost");
            // TODO Auto-generated method stub
            super.onPostExecute(contacts);

            pd.cancel();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getApplicationContext(), R.layout.text, contacts);

            list.setAdapter(adapter);
        }
    }

    private void showContactsAsync() {
        Log.d("2","dddd in showcontacts");
        LoadContactsAsycn lca = new LoadContactsAsycn();
        Log.d("2","dddd finish load contact");
        lca.execute();
    }
}