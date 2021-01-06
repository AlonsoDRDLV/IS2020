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


public class InitActivity extends AppCompatActivity {

    //private static final int ACTIVITY_INIT=0;
    private static final int ACTIVITY_PRODUCTS=1;
    private static final int ACTIVITY_LISTS=2;

    private Button buttonToProducts;
    private Button buttonToLists;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        buttonToProducts = (Button) findViewById(R.id.toProducts);
        buttonToLists = (Button) findViewById(R.id.toLists);

    }

    public void toProducts(View view){
        Intent i = new Intent(this, ProductsActivity.class);
        startActivityForResult(i, ACTIVITY_PRODUCTS);
    }

    public void toLists(View view){
        Intent i = new Intent(this, ListsActivity.class);
        startActivityForResult(i, ACTIVITY_LISTS);
    }

    }
