package com.example.cockym.phonebook.ui.contact_list;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cockym.phonebook.bean.Contact;
import com.example.cockym.phonebook.database.DBHelper;
import com.example.cockym.phonebook.R;
import com.example.cockym.phonebook.ui.universal.UniversalActivity;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ContactListActivity extends AppCompatActivity {

    private ContactListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Список Контанктов");
        Menu menu = toolbar.getMenu();
        menu.clear();
        MenuItem item = menu.add(Menu.NONE, 1, Menu.NONE, "Поиск");
        item.setIcon(R.drawable.ic_search_white_24dp);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW |
                MenuItem.SHOW_AS_ACTION_ALWAYS);

        SearchView searchView = new SearchView(this);
        searchView.setIconified(false);
        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

// SearchView

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.clearFocus();

        adapter = new ContactListAdapter(this);
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Contact item = adapter.getItem(i);
                StringBuilder sb = new StringBuilder();
                sb.append("Имя:  ").append(item.name).append("\n");
                sb.append("Номер:  ").append(item.number).append("\n");
                sb.append("Email:  ").append(item.email).append("\n");

                AlertDialog.Builder d = new AlertDialog.Builder(ContactListActivity.this);
                d.setTitle("Контакт");
                d.setMessage(sb.toString());
                d.setNegativeButton("Назад", null);
                d.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UniversalActivity.open(ContactListActivity.this, item.id);
                    }
                });
                d.create().show();

            }
        });

        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Contact item = adapter.getItem(i);
                AlertDialog.Builder b = new AlertDialog.Builder(ContactListActivity.this);
                List<CharSequence> list = new ArrayList<>();
                list.add("Удалить");
                list.add("Изменить");
                final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(ContactListActivity.this,
                        android.R.layout.select_dialog_item, list);
                b.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            AlertDialog.Builder d = new AlertDialog.Builder(ContactListActivity.this);
                            d.setTitle("ОПАСНО !");
                            d.setMessage("Вы хотите удалить этот контакт ?");
                            d.setNegativeButton("Нет", null);
                            d.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteInDb(item);
                                }
                            });
                            d.create().show();
                        } else if (i == 1) {
                            UniversalActivity.open(ContactListActivity.this, item.id);
                        }
                    }
                });
                b.create().show();

                return true;
            }
        });


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                UniversalActivity.open(ContactListActivity.this,-1);
            }

        });
    }

    public void deleteInDb(Contact item) {
        DBHelper helper = new DBHelper(ContactListActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM mytable WHERE id = ?", new Object[]{item.id});
        db.close();
        reloadList();
    }

    @Override
    protected void onStart() {
        super.onStart();

        reloadList();
    }

    public void reloadList() {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        final List<Contact> r = new ArrayList<>();
        try {
            Cursor c = db.query("mytable", null, null, null, null, null, null);
            if (c.moveToFirst()) {
                int id = c.getColumnIndex("id");
                int nameId = c.getColumnIndex("name");
                int numberId = c.getColumnIndex("number");
                int emailId = c.getColumnIndex("user_email");
                do {
                    r.add(new Contact(c.getInt(id),
                            c.getString(nameId),
                            c.getString(emailId),
                            c.getString(numberId),"")
                    );

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.setItems(r);
    }
}
