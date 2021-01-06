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

public class ListsActivity extends AppCompatActivity {

    private static final int ACTIVITY_LIST_CREATE=0;
    private static final int ACTIVITY_LIST_EDIT=1;

    private static final int INSERT_LIST = Menu.FIRST;
    private static final int DELETE_LIST = Menu.FIRST + 1;
    private static final int EDIT_LIST = Menu.FIRST + 2;
    private static final int EMAIL_ID = Menu.FIRST + 3;

    private NotesDbAdapter mDbHelper;
    private ListView mList;
    private TextView mName;
    private TextView mWeight;
    private TextView mPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        setTitle(R.string.title_activity_lists);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        //mDbHelper.createList("nombre1");
        //mDbHelper.createList("nombre2");
        mList = (ListView)findViewById(R.id.listas);
        mName = (TextView)findViewById(R.id.first_row_l_name);
        mWeight = (TextView)findViewById(R.id.first_row_l_weight);
        mPrice = (TextView)findViewById(R.id.first_row_l_price);
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
        Cursor notesCursor = mDbHelper.fetchAllLists(orderBy);
        // Create an array to specify the fields we want to
        // display in the list ( only TITLE )
        String[] from = new
                String[]{"nombre", "peso", "precio"};
        // and an array of the fields we want to bind
        // those fields to (in this case just text1 )
        int[] to = new int[]{R.id.list_name, R.id.list_weight, R.id.list_price};
        // Now create an array adapter and set it to
        // display using our row
        SimpleCursorAdapter products =
                new SimpleCursorAdapter(this,R.layout.lists_row, notesCursor, from, to);
        mList.setAdapter(products);

    }

    /** Called when the options menu is first created.
     * @param menu options menu created*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_LIST, Menu.NONE, R.string.menu_insert_list);
        return result;
    }
    /** Called when the options menu is selected.
     * @param item item selected on options menu*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_LIST:
                createList();
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
        menu.add(Menu.NONE, DELETE_LIST, Menu.NONE, R.string.menu_delete_list);
        menu.add(Menu.NONE, EDIT_LIST, Menu.NONE, R.string.menu_edit_list);
        menu.add(Menu.NONE, EMAIL_ID, Menu.NONE, R.string.menu_email);
    }

    /** Called when the context menu is selected.
     * @param item item selected on context menu
     * @return true if item is known, false otherwise*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_LIST:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteList(info.id);
                fillData(null);
                return true;
            case EDIT_LIST:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editList(info.position, info.id);
                return true;
            case EMAIL_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Cursor lCursor = mDbHelper.fetchList(info.id);
                startManagingCursor(lCursor);
                Cursor cCursor = mDbHelper.fetchAllListContent(info.id);
                startManagingCursor(cCursor);
                if (lCursor != null) {
                    cCursor.moveToFirst();
                    //System.out.println("productos seleccionados " + cCursor.getCount());
                }
                String name = lCursor.getString(
                        lCursor.getColumnIndexOrThrow("nombre"));
                String weight = lCursor.getString(
                        lCursor.getColumnIndexOrThrow("peso"));
                String price = lCursor.getString(
                        lCursor.getColumnIndexOrThrow("precio"));
                String content = "";
                do{
                    String pName = cCursor.getString(
                            cCursor.getColumnIndexOrThrow("nombre"));
                    String quantity = cCursor.getString(
                            cCursor.getColumnIndexOrThrow("cantidad"));
                            content = content +"Nombre: " + pName + "\t" +
                                    "Cantidad: " + quantity + "\n";
                }while(cCursor.moveToNext());
                String body = "Peso: " + weight + "\n" + "Precio: " + price + "\n\n" +
                        "Contenido: \n" + content;
                SendAbstraction s = new SendAbstractionImpl(this, "MAIL");
                s.send(name, body);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createList() {
        Intent i = new Intent(this, ListEdit.class);
        startActivityForResult(i, ACTIVITY_LIST_CREATE);
    }


    protected void editList(int position, long id) {
        //Cursor c = mNotesCursor;
        //c.moveToPosition(position);
        Intent i = new Intent(this, ListEdit.class);
        i.putExtra("_id", id);

        /*i.putExtra(NotesDbAdapter.KEY_TITLE, c.getString(
                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
        i.putExtra(NotesDbAdapter.KEY_BODY, c.getString(
                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));*/
        startActivityForResult(i, ACTIVITY_LIST_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData(null);
    }

}
