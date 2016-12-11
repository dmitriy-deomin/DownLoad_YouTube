package dmitriy.deomin.download_youtube;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static dmitriy.deomin.download_youtube.Main.*;

public class History extends Activity implements AdapterView.OnItemLongClickListener {

    String [] title_url = {};

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        listView = (ListView)findViewById(R.id.listView_history);

        //title_url
        title_url = new String[getStringArrayPref(Main.context, "a_title_h").size()];
        for (int i = 0; i != getStringArrayPref(Main.context, "a_title_h").size(); i++) {
            title_url[i] = getStringArrayPref(Main.context, "a_title_h").get(i);
        }


        //устанавливаем массив в ListView
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, title_url));
        listView.setTextFilterEnabled(true);

        //Обрабатываем щелчки на элементах ListView:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                CharSequence k = ((TextView) v).getText();
                String n = k.toString();
                String arr[] = n.split("\n");
              //  putText(arr[1]);
               // Toast.makeText(getApplicationContext(),"Скопировали в буфер "+arr[1].toString(),Toast.LENGTH_SHORT).show();
                Main.url.setText(arr[1].toString());
                Main.Download(Main.url);
                pizdec();
            }
        });

        listView.setOnItemLongClickListener(this);


    }

    private void pizdec(){
        this.finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final String selectedItem = parent.getItemAtPosition(position).toString(); //получаем строку

        //подкрашиваем
        Spannable text = new SpannableString("Удалить "+selectedItem + "? ");
        text.setSpan(new ForegroundColorSpan(Color.RED),0,text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Удаление");
      //  b.setIcon(R.drawable.delete);
        b.setMessage(text);
        b.setCancelable(true);
        b.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //нечего не делаем
            }
        });
        b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //удаляем

                ArrayList<String> masiv_title_url;
                masiv_title_url = new ArrayList<>(getStringArrayPref(getApplicationContext(), "a_title_h")); // чтение title



                if (masiv_title_url.remove(selectedItem))//пробуем удалить
                {
                    // сохранение номеров
                    if (setStringArrayPref(getApplicationContext(), "a_title_h", masiv_title_url))
                    {
                        //если сохранилось обновляем список
                        //title_url
                        title_url = new String[getStringArrayPref(Main.context, "a_title_h").size()];
                        for (int i = 0; i != getStringArrayPref(Main.context, "a_title_h").size(); i++) {
                            title_url[i] = getStringArrayPref(Main.context, "a_title_h").get(i);
                        }
                        //устанавливаем массив в ListView
                        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, title_url));
                        Toast.makeText(getApplicationContext(), "Удалено" + selectedItem, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog alert = b.create();
        alert.show();

        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ArrayList<String> pustoy_massiv;
        pustoy_massiv = new ArrayList<>();


        if(id==R.id.action_del_all){ // удалить все
            setStringArrayPref(getApplicationContext(), "a_title_h", pustoy_massiv);
        }

        //если сохранилось обновляем список
        //title_url
        title_url = new String[getStringArrayPref(Main.context, "a_title_h").size()];
        for (int i = 0; i != getStringArrayPref(Main.context, "a_title_h").size(); i++) {
            title_url[i] = getStringArrayPref(Main.context, "a_title_h").get(i);
        }
        //устанавливаем массив в ListView
        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, title_url));
        Toast.makeText(getApplicationContext(), "Удалено" , Toast.LENGTH_SHORT).show();


        return super.onOptionsItemSelected(item);
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void putText(String text){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES. HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = ClipData.newPlainText(text, text);
            clipboard.setPrimaryClip(clip);
        }
    }


}
