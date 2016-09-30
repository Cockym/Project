package com.example.cockym.phonebook.ui.universal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.cockym.phonebook.R;
import com.example.cockym.phonebook.bean.Contact;
import com.example.cockym.phonebook.database.DBHelper;
import com.example.cockym.phonebook.ui.Util;

public class UniversalActivity extends Activity {

    public static void open(Activity activity, int id) {
        Intent intent = new Intent(activity, UniversalActivity.class);
      //  activity.startActivity(new Intent(activity, UniversalActivity.class));
        intent.putExtra("arg", id);
        activity.startActivity(intent);
    }

    public int getId() {
        return getIntent().getExtras().getInt("arg");
    }

    EditText etName, etNumber, etEmail;
    DBHelper dbHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Новый Контакт");
        toolbar.setTitle("Редактировать");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Menu menu = toolbar.getMenu();
        menu.close();
        final MenuItem item = menu.add(Menu.NONE, 0, Menu.NONE, "save");
        item.setIcon(R.drawable.ic_check_white_24dp);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (item.getItemId()) {
                    case 0:
                        saveContact();
                        break;
                    default:
                        throw new RuntimeException();
                }
                return true;
            }
        });

        LinearLayout ll = (LinearLayout) findViewById(R.id.llfon);
        ll.removeAllViews();

        LinearLayout lil = (LinearLayout) findViewById(R.id.llfon1);
        lil.removeAllViews();

        dbHelper = new DBHelper(this);
        Contact contact = loadContact();

        etName = (EditText) findViewById(R.id.editText);

        etName.setText(contact.name);

        String[] numbers = contact.number.split(" ");
        View lastView = null;
        for (String n: numbers) {
            lastView = numberView(n, lastView);
        }

        if (numbers.length == 0) {
            numberView("", null);
        }

        String[] emails = contact.email.split(" ");
        View lastView1 = null;
        for (String e : emails) {
            lastView1 = emailView(e, lastView1);
        }

        if (emails.length == 0) {
            emailView("", null);
        }

        numberView("", null);
        emailView("", null);

        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String number = etNumber.getText().toString();
                if (!TextUtils.isEmpty(number) && !Util.isNumberValid(number)) {
                    Toast.makeText(getApplicationContext(), "Введите номер !", Toast.LENGTH_SHORT).show();return;
                }if (!TextUtils.isEmpty(email) && !Util.isEmailValid(email)) {Toast.makeText(getApplicationContext(), "Введите email !", Toast.LENGTH_SHORT).show();return;}
                saveContact();}
        });

        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContact();
            }
        });
        
    }

    private void saveContact() {
        ContentValues cv = new ContentValues();
        int id = getId();
        String name = etName.getText().toString();
        String numbers = getNumbers();
        String email = getEmails();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        cv.put("id ", String.valueOf(id));
        cv.put("name ", name);
        cv.put("number ", numbers);
        cv.put("user_email ", email);
        db.insert("mytable", null, cv);
        db.replace("mytable", null, cv);

        dbHelper.close();

        Toast.makeText(UniversalActivity.this, "Сохранён", Toast.LENGTH_SHORT).show();
        finish();
    }

    public Contact loadContact() {
        int id = getId();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name, number, user_email FROM mytable WHERE id = ?",
                new String[]{String.valueOf(id)});

        if (c.moveToFirst()) {
            int nameId = c.getColumnIndex("name");
            int numbersId = c.getColumnIndex("number");
            int emailId = c.getColumnIndex("user_email");

            return new Contact(id,
                    c.getString(nameId),
                    c.getString(emailId),
                    c.getString(numbersId), "");

        }

        return new Contact(-1, "", "", "", "");
    }

    private String getNumbers() {
        try {
            StringBuilder sb = new StringBuilder();
            LinearLayout ll = (LinearLayout) findViewById(R.id.llfon);
            for (int i = 0; i < ll.getChildCount(); i++) {
                LinearLayout row = (LinearLayout) ll.getChildAt(i);
                EditText phone = (EditText) row.findViewById(R.id.editText2);
                sb.append(phone.getText().toString()).append(" ");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getEmails() {
        try {
            StringBuilder sb1 = new StringBuilder();
            LinearLayout lil = (LinearLayout) findViewById(R.id.llfon1);
            for (int i = 0; i < lil.getChildCount(); i++) {
                LinearLayout rov = (LinearLayout) lil.getChildAt(i);
                EditText email = (EditText) rov.findViewById(R.id.editText3);
                sb1.append(email.getText().toString()).append(" ");
            }
            return sb1.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public View numberView(String number, View view) {
        final LinearLayout ll = (LinearLayout) findViewById(R.id.llfon);
        //TODO addFonVIEW
        if (view != null) {
            ImageButton b = (ImageButton) view;
            b.setImageResource(R.drawable.ic_close_white_24dp);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll.removeView((View) view.getParent());
                }
            });
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View inflate = inflater.inflate(R.layout.contact_number_new, null);
        View btn = inflate.findViewById(R.id.btn_new);
        inflate.findViewById(R.id.btn_new).setTag(ll.getChildCount());
        btn.setTag(ll.getChildCount());
        inflate.findViewById(R.id.btn_new);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberView("", view);
            }
        });
        ll.addView(inflate);
        return btn;
    }

    public View emailView(String email, View view) {
        final LinearLayout ll = (LinearLayout) findViewById(R.id.llfon1);
        //TODO addFonView1
        if (view != null) {
            ImageButton b1 = (ImageButton) view;
            b1.setImageResource(R.drawable.ic_close_white_24dp);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    ll.removeView((View) view1.getParent());
                }
            });
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View inflate = inflater.inflate(R.layout.contact_email_new, null);
        View btn = inflate.findViewById(R.id.btn_new);

        inflate.findViewById(R.id.btn_new);
        inflate.findViewById(R.id.btn_new)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailView("", view);
            }
        });
        ll.addView(inflate);
        return btn;
    }
}