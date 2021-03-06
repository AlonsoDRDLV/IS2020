package es.unizar.eina.notepadv3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import es.unizar.eina.send.SendAbstraction;
import es.unizar.eina.send.SendAbstractionImpl;


public class Notepadv3 extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    private static final int EMAIL_ID = Menu.FIRST + 3;
    private static final int SMS_ID = Menu.FIRST + 4;

    private NotesDbAdapter mDbHelper;
    //private Cursor mNotesCursor;
    private ListView mList;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepadv3);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mList = (ListView)findViewById(R.id.list);
        fillData();

        registerForContextMenu(mList);

    }

    private void fillData() {
        // Get all of the notes from the database and
        // create the item list
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        // Create an array to specify the fields we want to
        // display in the list ( only TITLE )
        String[] from = new
                String[]{ NotesDbAdapter.KEY_TITLE};
        // and an array of the fields we want to bind
        // those fields to (in this case just text1 )
        int[] to = new int[]{R.id.text1};
        // Now create an array adapter and set it to
        // display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this,R.layout.notes_row, notesCursor, from, to);
        mList.setAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_edit);
        menu.add(Menu.NONE, EMAIL_ID, Menu.NONE, R.string.menu_email);
        menu.add(Menu.NONE, SMS_ID, Menu.NONE, R.string.menu_sms);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
            case EDIT_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editNote(info.position, info.id);
                return true;
            case EMAIL_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Cursor note = mDbHelper.fetchNote(info.id);
                startManagingCursor(note);
                String title = note.getString(
                        note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
                String body = note.getString(
                        note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
                System.out.println(title);
                System.out.println(body);
                SendAbstraction s = new SendAbstractionImpl(this, "MAIL");
                s.send(title, body);
                return true;
            case SMS_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                note = mDbHelper.fetchNote(info.id);
                startManagingCursor(note);
                title = note.getString(
                        note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
                body = note.getString(
                        note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
                s = new SendAbstractionImpl(this, "SMS");
                s.send(title, body);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    protected void editNote(int position, long id) {
        //Cursor c = mNotesCursor;
        //c.moveToPosition(position);
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        /*i.putExtra(NotesDbAdapter.KEY_TITLE, c.getString(
                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
        i.putExtra(NotesDbAdapter.KEY_BODY, c.getString(
                c.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));*/
        startActivityForResult(i, ACTIVITY_EDIT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
