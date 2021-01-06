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
import android.widget.TextView;

import es.unizar.eina.send.SendAbstraction;
import es.unizar.eina.send.SendAbstractionImpl;

public class ProductDetails extends AppCompatActivity{

    private TextView mNameText;
    private TextView mDescriptionText;
    private TextView mWeightText;
    private TextView mPriceText;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle(R.string.title_activity_products_edit);
        mDbHelper = new NotesDbAdapter( this);
        mDbHelper.open();

        mNameText = (TextView) findViewById(R.id.read_p_name);
        mDescriptionText = (TextView) findViewById(R.id.read_p_description);
        mWeightText = (TextView) findViewById(R.id.read_p_weight);
        mPriceText = (TextView) findViewById(R.id.read_p_price);

        mRowId = (savedInstanceState == null)?null:
                (Long)savedInstanceState.getSerializable("_id");
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null)?
                    extras.getLong("_id"):null;
        }
        System.out.println("en on create "+mRowId);

    }

    private void populateFields () {
        if ( mRowId != null ) {
            Cursor note = mDbHelper.fetchProduct(mRowId);
            startManagingCursor(note);
            if (mNameText == null){
                System.out.println("nulo");
            }
            if (mWeightText == null){
                System.out.println("nulo2");
            }
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
