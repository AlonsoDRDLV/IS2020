package es.unizar.eina.listasCompra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import es.unizar.eina.send.SendAbstraction;
import es.unizar.eina.send.SendAbstractionImpl;

public class ProductEdit extends AppCompatActivity {

    private EditText mNameText;
    private EditText mDescriptionText;
    private EditText mWeightText;
    private EditText mPriceText;
    private Button confirmButton;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_edit);
        setTitle(R.string.title_activity_products_edit);
        mDbHelper = new NotesDbAdapter( this);
        mDbHelper.open();

        mNameText = (EditText) findViewById(R.id.insert_p_name);
        mDescriptionText = (EditText) findViewById(R.id.insert_p_description);
        mWeightText = (EditText) findViewById(R.id.insert_p_weight);
        mPriceText = (EditText) findViewById(R.id.insert_p_price);

        confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null)?null:
                (Long)savedInstanceState.getSerializable("_id");
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null)?
                    extras.getLong("_id"):null;
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick ( View view ) {
                setResult ( RESULT_OK );
                finish ();
            }

        });
    }

    private void populateFields () {
        if ( mRowId != null ) {
            Cursor note = mDbHelper.fetchProduct(mRowId);
            startManagingCursor(note);
            mNameText.setText(String.valueOf(note.getString(
                    note.getColumnIndexOrThrow("nombre"))));
            mDescriptionText.setText(String.valueOf(note.getString(
                    note.getColumnIndexOrThrow("descripcion"))));
            mWeightText.setText(note.getString(
                    note.getColumnIndexOrThrow("peso")));
            mPriceText.setText(note.getString(
                    note.getColumnIndexOrThrow("precio")));
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable("_id", mRowId);
    }
    @Override
    protected void onPause(){
        super.onPause();
        saveState();
    }
    @Override
    protected void onResume(){
        super.onResume();
        populateFields();
    }
    private void saveState () {
        String name = mNameText.getText().toString();
        String description = mDescriptionText.getText().toString();
        String weight = mWeightText.getText().toString();
        String price = mPriceText.getText().toString();
        float weightf = Float.parseFloat(weight);
        float pricef =  Float.parseFloat(price);
        if (mRowId == null){
            long id = mDbHelper.createProduct(name, description, weightf, pricef);
            if (id > 0){
                mRowId = id;
            }
        } else{
            mDbHelper.updateProduct(mRowId, description, name, weightf, pricef);
        }
    }


    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
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
    }*/
}
