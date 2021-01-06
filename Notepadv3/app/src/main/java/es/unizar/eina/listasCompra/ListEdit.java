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

public class ListEdit extends AppCompatActivity {

    private static final int ACTIVITY_ADD_TO_LIST=0;
    private static final int DELETE_P_FROM_LIST = Menu.FIRST;

    private EditText mNameText;
    private ListView mList;
    private Button confirmButton;
    private Button addButton;
    private AppCompatActivity a;

    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists_edit);
        setTitle(R.string.title_activity_lists_edit);
        mDbHelper = new NotesDbAdapter( this);
        mDbHelper.open();

        a = this;

        mNameText = (EditText) findViewById(R.id.insert_l_name);
        mList = (ListView) findViewById(R.id.productos_2);
        addButton = (Button) findViewById(R.id.add_product);
        confirmButton = (Button) findViewById(R.id.confirm_list);

        mRowId = (savedInstanceState == null)?null:
                (Long)savedInstanceState.getSerializable("_id");

        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null)?
                    extras.getLong("_id"):null;
            if (mRowId == null) {

                long id = mDbHelper.createList("");
                if (id > 0){
                    mRowId = id;
                }
            }
        }


        fillData(null);
        registerForContextMenu(mList);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick ( View view ) {
                setResult ( RESULT_OK );
                finish ();
            }

        });

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick ( View view ) {
                Intent i = new Intent(a, AddToList.class);
                i.putExtra("_id", mRowId);
                startActivityForResult(i, ACTIVITY_ADD_TO_LIST);
            }

        });
    }

    private void fillData(String orderBy) {
       Cursor notesCursor = mDbHelper.fetchAllListContent(mRowId);
       //notesCursor.moveToNext();
        //
        String[] from = new
                String[]{"nombre", "peso", "precio", "cantidad"};

        int[] to = new int[]{R.id.product_name_in_l, R.id.product_weight_in_l,
                R.id.product_price_in_l,  R.id.product_quantity_in_l};

        SimpleCursorAdapter products =
                new SimpleCursorAdapter(this,R.layout.products_in_l_row, notesCursor, from, to);
        mList.setAdapter(products);

    }

    private void populateFields () {
        if ( mRowId != null ) {
            Cursor note = mDbHelper.fetchList(mRowId);
            startManagingCursor(note);
            mNameText.setText(String.valueOf(note.getString(
                    note.getColumnIndexOrThrow("nombre"))));
        }
    }

    /** Called when the context menu is created.
     * @param menu context menu created
     * @param v view of context menu created
     * @param menuInfo menu info of context menu created*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_P_FROM_LIST, Menu.NONE, R.string.menu_delete_product);
    }

    /** Called when the context menu is selected.
     * @param item item selected on context menu
     * @return true if item is known, false otherwise*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_P_FROM_LIST:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteFromList(a, mRowId, info.id);
                fillData(null);
                return true;
        }
        return super.onContextItemSelected(item);
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
        fillData(null);
    }
    private void saveState () {
        String name = mNameText.getText().toString();
        if (mRowId == null){

            long id = mDbHelper.createList(name);
            if (id > 0){
                mRowId = id;
            }
        } else{

            Cursor lCursor = mDbHelper.fetchList(mRowId);
            startManagingCursor(lCursor);

            float currentWeight = lCursor.getFloat(
                    lCursor.getColumnIndexOrThrow("peso"));
            float currentPrice = lCursor.getFloat(
                    lCursor.getColumnIndexOrThrow("precio"));



            mDbHelper.updateList(mRowId, name, currentWeight, currentPrice);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData(null);
        populateFields();

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
