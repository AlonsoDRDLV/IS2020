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

public class ProductsActivity extends AppCompatActivity{

    private static final int ACTIVITY_PRODUCT_CREATE=0;
    private static final int ACTIVITY_PRODUCT_EDIT=1;
    private static final int ACTIVITY_PRODUCT_DETAILS=2;

    private static final int INSERT_PRODUCT = Menu.FIRST;
    private static final int DELETE_PRODUCT = Menu.FIRST + 1;
    private static final int EDIT_PRODUCT = Menu.FIRST + 2;

    private NotesDbAdapter mDbHelper;
    private ListView mList;
    private TextView mName;
    private TextView mWeight;
    private TextView mPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        setTitle(R.string.title_activity_products);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mList = (ListView)findViewById(R.id.productos);
        mName = (TextView)findViewById(R.id.first_row_p_name);
        mWeight = (TextView)findViewById(R.id.first_row_p_weight);
        mPrice = (TextView)findViewById(R.id.first_row_p_price);
        fillData(null);

        registerForContextMenu(mList);
        mName.setOnClickListener(new View.OnClickListener() {
            public void onClick ( View view ) {
                fillData("nombre");
            }

        });

        mWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick ( View view ) {
                fillData("peso");
            }

        });

        mPrice.setOnClickListener(new View.OnClickListener() {
            public void onClick ( View view ) {
                fillData("precio");
            }

        });

    }

    private void fillData(String orderBy) {
        // Get all of the notes from the database and
        // create the item list
        Cursor notesCursor = mDbHelper.fetchAllProducts(orderBy);
        // Create an array to specify the fields we want to
        // display in the list ( only TITLE )
        String[] from = new
                String[]{"nombre", "peso", "precio"};
        // and an array of the fields we want to bind
        // those fields to (in this case just text1 )
        int[] to = new int[]{R.id.product_name, R.id.product_weight, R.id.product_price};
        // Now create an array adapter and set it to
        // display using our row
        SimpleCursorAdapter products =
                new SimpleCursorAdapter(this,R.layout.products_row, notesCursor, from, to);
        mList.setAdapter(products);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
                System.out.println("antes de mandar el intent "+arg3);
                goToDetails(arg3);
            }
        });

    }

    /** Called when the options menu is first created.
     * @param menu options menu created*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_PRODUCT, Menu.NONE, R.string.menu_insert_product);
        return result;
    }
    /** Called when the options menu is selected.
     * @param item item selected on options menu*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_PRODUCT:
                createProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Called when the context menu is created.
     * @param menu context menu created
     * @param v view of context menu created
     * @param menuInfo menu info of context menu created*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_PRODUCT, Menu.NONE, R.string.menu_delete_product);
        menu.add(Menu.NONE, EDIT_PRODUCT, Menu.NONE, R.string.menu_edit_product);
    }

    /** Called when the context menu is selected.
     * @param item item selected on context menu
     * @return true if item is known, false otherwise*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_PRODUCT:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteProduct(info.id);
                fillData(null);
                return true;
            case EDIT_PRODUCT:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editProduct(info.position, info.id);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createProduct() {
        Intent i = new Intent(this, ProductEdit.class);
        startActivityForResult(i, ACTIVITY_PRODUCT_CREATE);
    }


    protected void editProduct(int position, long id) {
        //Cursor c = mNotesCursor;
        //c.moveToPosition(position);
        Intent i = new Intent(this, ProductEdit.class);
        i.putExtra("_id", id);
        /*i.putExtra(NotesDbAdapter.KEY_TITLE, c.getString(
                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
        i.putExtra(NotesDbAdapter.KEY_BODY, c.getString(
                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));*/
        startActivityForResult(i, ACTIVITY_PRODUCT_EDIT);
    }

    private void goToDetails(long id){
        Intent i = new Intent(this, ProductDetails.class);
        i.putExtra("_id", id);
        startActivityForResult(i, ACTIVITY_PRODUCT_DETAILS);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData(null);
    }
}
