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

public class AddToList extends AppCompatActivity {

    private static final int ACTIVITY_ADD_TO_LIST=0;

    private EditText mQuantityText;
    private ListView mList;
    private Button confirmButton;
    private View lastSelected;
    private Long idContent;
    private Long idProductSelected;
    private Long idList;
    private AppCompatActivity a;
    private NotesDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        a = this;

        mQuantityText = (EditText) findViewById(R.id.insert_quantity);
        mList = (ListView) findViewById(R.id.productos_3);
        confirmButton = (Button) findViewById(R.id.confirm_add);

        idList = (savedInstanceState == null)?null:
                (Long)savedInstanceState.getSerializable("_id");
        if (idList == null) {
            Bundle extras = getIntent().getExtras();
            idList = (extras != null)?
                    extras.getLong("_id"):null;
        }

        idProductSelected = (savedInstanceState == null)?null:
                (Long)savedInstanceState.getSerializable("_id_product");

        fillData(null);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick ( View view ) {
                int quantity = Integer.parseInt(mQuantityText.getText().toString());
                if((quantity != 0) && (idProductSelected != null)){
                    idContent = mDbHelper.addToList(a, idList, idProductSelected, quantity);

                }
                setResult ( RESULT_OK );
                finish ();
            }

        });
    }

    private void fillData(String orderBy) {
        // Get all of the notes from the database and
        // create the item list
        Cursor notesCursor = mDbHelper.fetchAllProducts("nombre");
        // Create an array to specify the fields we want to
        // display in the list ( only TITLE )
        String[] from = new
                String[]{"nombre", "peso", "precio"};
        // and an array of the fields we want to bind
        // those fields to (in this case just text1 )
        int[] to = new int[]{R.id.product_name, R.id.product_weight,
                R.id.product_price};
        // Now create an array adapter and set it to
        // display using our row
        SimpleCursorAdapter products =
                new SimpleCursorAdapter(this,R.layout.products_row, notesCursor, from, to);
        mList.setAdapter(products);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {

                if(lastSelected != null){
                    lastSelected.setBackgroundColor(getResources().getColor(R.color.not_selected));
                }
                arg1.setBackgroundColor(getResources().getColor(R.color.selected));
                lastSelected = arg1;
                idProductSelected = arg3;
            }
        });

    }

    private void populateFields () {
        /*if(idProductSelected != null){
            if ( idList != null ) {
                Cursor note = mDbHelper.fetchAddedProduct(idList, productSelected);
                startManagingCursor(note);
                mQuantityText.setText(String.valueOf(note.getInt()
                        note.getColumnIndexOrThrow("cantidad"))));
            }
        }*/

    }

    @Override
    protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable("_id", idList);
        outState.putSerializable("_id_product", idProductSelected);
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
        /*int quantity = Integer.parseInt(mQuantityText.getText().toString());
        if (idProductSelected == null){
            long id = mDbHelper.addToList(float);
            if (id > 0){
                mRowId = id;
            }
        } else{

            mDbHelper.updateList(mRowId, name, currentWeight, currentPrice);
        }*/
    }
}
