package es.unizar.eina.listasCompra;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 *
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String PRODUCTS_CREATE =
            "create table productos ( _id integer primary key autoincrement, "
                    + "nombre text not null, descripcion text not null, peso real not null, "
                    + "precio real not null);";
    private static final String LISTS_CREATE =
            "create table listas ( _id integer primary key autoincrement, "
                    + "nombre text not null, "
                    + "peso real not null, precio real not null);";
    private static final String CONTENTS_CREATE =
            "create table contiene ( _id integer primary key autoincrement, "
                    + "lista text not null, "
                    + "producto integer not null, "
                    + "cantidad integer not null);";

    private static final String DATABASE_NAME = "data";
    private static final String PRODUCTOS = "productos";
    private static final String LISTAS = "listas";
    private static final String CONTIENE = "contiene";
    private static final int DATABASE_VERSION = 19;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(PRODUCTS_CREATE);
            db.execSQL(LISTS_CREATE);
            db.execSQL(CONTENTS_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contiene");
            db.execSQL("DROP TABLE IF EXISTS productos");
            db.execSQL("DROP TABLE IF EXISTS listas");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createProduct(String nombre, String descripcion, float peso, float precio) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("nombre", nombre);
        initialValues.put("descripcion", nombre);
        initialValues.put("peso", peso);
        initialValues.put("precio", precio);

        return mDb.insert(PRODUCTOS, null, initialValues);
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createList(String nombre) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("nombre", nombre);
        initialValues.put("peso", 0f);
        initialValues.put("precio", 0f);

        return mDb.insert(LISTAS, null, initialValues);
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long addToList(Activity a, long lista, long producto, int cantidad) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("lista", lista);
        initialValues.put("producto", producto);
        initialValues.put("cantidad", cantidad);

        Cursor lCursor =
                mDb.query(true, LISTAS, new String[] {"_id",
                                "nombre", "peso", "precio"}, "_id=?", new String[] {String.valueOf(lista)},
                        null, null, null, null);
        a.startManagingCursor(lCursor);
        if (lCursor != null) {
            lCursor.moveToFirst();
        }

        Cursor pCursor =
                mDb.query(true, PRODUCTOS, new String[] {"_id",
                                "nombre", "peso", "precio"}, "_id=?", new String[] {String.valueOf(producto)},
                        null, null, null, null);
        a.startManagingCursor(pCursor);
        if (pCursor != null) {
            pCursor.moveToFirst();
            //System.out.println("productos seleccionados " + pCursor.getCount());
        }

        float currentWeight = lCursor.getFloat(
                lCursor.getColumnIndexOrThrow("peso"));
        float currentPrice = lCursor.getFloat(
                lCursor.getColumnIndexOrThrow("precio"));
        float productWeight = pCursor.getInt(
                pCursor.getColumnIndexOrThrow("peso"));
        float productPrice = pCursor.getFloat(
                pCursor.getColumnIndexOrThrow("precio"));
        //System.out.println(producto + " pesa " + productWeight);
        currentWeight = currentWeight + productWeight*cantidad;
        currentPrice = currentPrice + productPrice*cantidad;

        boolean ok = updateList(lista, lCursor.getString(lCursor.getColumnIndexOrThrow("nombre")),
                currentWeight, currentPrice);
        if(ok){
            return mDb.insert(CONTIENE, null, initialValues);
        }
        else{
            return -1;
        }

    }


    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteProduct(long rowId) {

        return mDb.delete(PRODUCTOS,  "_id"+ "=" + rowId, null) > 0;
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteList(long rowId) {

        return mDb.delete(LISTAS,  "_id" + "=" + rowId, null) > 0;
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteFromList(Activity a, long listId, long productId ) {

        Cursor lCursor =
                mDb.query(true, LISTAS, new String[] {"_id",
                                "nombre", "peso", "precio"}, "_id=?", new String[] {String.valueOf(listId)},
                        null, null, null, null);
        a.startManagingCursor(lCursor);
        if (lCursor != null) {
            lCursor.moveToFirst();
        }

        Cursor pCursor =
                mDb.query(true, PRODUCTOS, new String[] {"_id",
                                "nombre", "peso", "precio"}, "_id=?", new String[] {String.valueOf(productId)},
                        null, null, null, null);
        a.startManagingCursor(pCursor);
        if (pCursor != null) {
            pCursor.moveToFirst();
            //System.out.println("productos seleccionados " + pCursor.getCount());
        }

        Cursor cCursor =
                mDb.query(true, CONTIENE, new String[] {"_id", "cantidad"}, "lista=? AND producto=?",
                        new String[] {String.valueOf(listId), String.valueOf(productId)},
                        null, null, null, null);
        a.startManagingCursor(pCursor);
        if (cCursor != null) {
            cCursor.moveToFirst();
            System.out.println("productos seleccionados " + cCursor.getCount());
        }

        float currentWeight = lCursor.getFloat(
                lCursor.getColumnIndexOrThrow("peso"));
        float currentPrice = lCursor.getFloat(
                lCursor.getColumnIndexOrThrow("precio"));
        float productWeight = pCursor.getFloat(
                pCursor.getColumnIndexOrThrow("peso"));
        float productPrice = pCursor.getFloat(
                pCursor.getColumnIndexOrThrow("precio"));
        int quantity = cCursor.getInt(
                cCursor.getColumnIndexOrThrow("cantidad"));

        currentWeight = currentWeight - productWeight*quantity;
        currentPrice = currentPrice - productPrice*quantity;

        boolean ok = updateList(listId, lCursor.getString(lCursor.getColumnIndexOrThrow("nombre")),
                currentWeight, currentPrice);
        if(ok){
            return mDb.delete(CONTIENE,"lista" + "=" + listId + " AND " + "producto =" + productId , null) > 0;
        }
        else{
            return ok;
        }


    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllProducts(String orderedBy) {

        if(orderedBy == null){
            return mDb.query(PRODUCTOS, new String[] {"_id", "nombre", "descripcion",
                    "peso", "precio"}, null, null, null, null, null);
        } else{
            return mDb.query(PRODUCTOS, new String[] {"_id", "nombre", "descripcion",
                    "peso", "precio"}, null, null, null, null, orderedBy);
        }
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllLists(String orderedBy) {

        if(orderedBy == null){
            return mDb.query(LISTAS, new String[] {"_id", "nombre",
                    "peso", "precio"}, null, null, null, null, null);
        } else{
            return mDb.query(LISTAS, new String[] {"_id", "nombre",
                    "peso", "precio"}, null, null, null, null, orderedBy);
        }
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllListContent(long lista) {

        return mDb.rawQuery("select p._id, p.nombre, p.peso, p.precio, c.cantidad " +
                "from contiene c, productos p where c.lista=? AND c.producto=p._id",
                new String[] {String.valueOf(lista)});
        /*return mDb.query(CONTIENE, new String[] {"_id", "lista", "producto",
                "cantidad"}, "lista=?", new String[] {String.valueOf(lista)},
                null, null, null);*/
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchProduct(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, PRODUCTOS, new String[] {"_id",
                                "nombre", "descripcion", "peso", "precio"}, "_id" + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchList(long rowId) throws SQLException {

        Cursor mCursor =
                mDb.query(true, LISTAS, new String[] {"_id",
                                "nombre", "peso", "precio"}, "_id" + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateProduct(long rowId, String name, String descripcion, float weight, float price) {
        ContentValues args = new ContentValues();
        args.put("nombre", name);
        args.put("descripcion", name);
        args.put("peso", weight);
        args.put("precio", price);

        return mDb.update(PRODUCTOS, args, "_id" + "=" + rowId, null) > 0;
    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateList(long rowId, String name, float weight, float price) {
        ContentValues args = new ContentValues();
        args.put("nombre", name);
        args.put("peso", weight);
        args.put("precio", price);

        return mDb.update(LISTAS, args, "_id" + "=" + rowId, null) > 0;
    }
}