package dmitriy.deomin.download_youtube;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.net.URLEncoder.encode;




public class Main extends Activity implements AdapterView.OnItemLongClickListener{

    public static EditText url;
    static Context context;
    static ListView listView;
    static TextView textView;

    TextView progres;

    static String gud_url; // нужен для сохранения в историю url

    Integer pos;
    String save_file_url;


    //**************************************
    static final String TITLE = "title";
    static final String QUALITY = "quality";
    static final String TYPE = "tipe";
    static final String SIZE ="size";

    static String [] title = {};
   public static String [] url_d= {};
    static String [] quality={};
    static String [] type = {};
    static String [] size = {};
    public static String [] syfix = {};



   static public String url_ava;
//****************************************

    public static ProgressBar load_link_progres;
    public static Button to_link;

    static SharedPreferences mSettings; // сохранялка
    final String APP_PREFERENCES = "mysettings"; // файл сохранялки


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = getApplicationContext();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        url_ava  = "http://www.donook.com/upload/nook/thumb50/awesomevideos.jpg?1363182971";


        progres = (TextView)findViewById(R.id.textView_progres);
        url = (EditText) findViewById(R.id.editText_url);
        listView =(ListView) findViewById(R.id.listView);
        listView.setVisibility(View.GONE); // скроем пустой список
        textView= (TextView)findViewById(R.id.textView_hide_list); // показывает инфу пока список ссылок пустой

        ///****
        load_link_progres = (ProgressBar)findViewById(R.id.load_link_progres);
        load_link_progres.setVisibility(View.GONE);
        to_link = (Button)findViewById(R.id.button_download);
        //********

        listView.setOnItemLongClickListener(this);


        //Обрабатываем щелчки на элементах ListView:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
                v.startAnimation(anim);

                if (isNetworkConnected()) {
                    pos = position; //


                    String arr_prefix[] = syfix[pos].toString().split("/");
                    String prefix = arr_prefix[1];
                    if (prefix.contains("html")) {
                        prefix = "mp4";
                    }

                    String name_save_file = title[pos].toString();

                    name_save_file = name_save_file.replaceAll("[^A-Za-zА-Яа-я0-9 ]", ""); //удаляем всю хрень

                    name_save_file = name_save_file.trim(); // удаляем пробелы

                    save_file_url = name_save_file + "." + prefix;

                    try {
                        encode(save_file_url, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url_d[pos]));
                    request.setTitle(title[pos].toString());
                    request.setDescription(url_d[pos]);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, save_file_url);

                    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);

                    Toast.makeText(context, "Загрузка началась,файл будет сохранен в папку видео", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context, R.string.nied_internet_conekt, Toast.LENGTH_SHORT).show();
                }

            }
        });




        //получаем адрес из другова приложения*********************************
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
            //и получим сразу все ссылки
            Download(findViewById(R.id.button_download));
        }
        //*************************************************************************

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        view.startAnimation(anim);

        pos = position;

        putText(url_d[pos].toString());

        //подкрашиваем
        Spannable text = new SpannableString("Скопировали ссылку в буфер.\n\n\nОткрыть ссылку  в системе " + "? ");
        text.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ((Button)content.findViewById(R.id.button_open_sis)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(url_d[pos].toString());
                Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(it);
            }
        });
        ((Button)content.findViewById(R.id.button_open_daw)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String arr_prefix[] = syfix[position].toString().split("/");
                String prefix = arr_prefix[1];
                if (prefix.contains("html")) {
                    prefix = "mp4";
                }

                String name_save_file = title[position].toString();

                name_save_file = name_save_file.replaceAll("[^A-Za-zА-Яа-я0-9 ]", ""); //удаляем всю хрень

                name_save_file = name_save_file.trim(); // удаляем пробелы

                name_save_file = name_save_file + "." + prefix;

                try {
                    encode(name_save_file, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                Log.d("TTT",url_d[position].toString()+"     "+name_save_file);




             dow(url_d[position].toString(),name_save_file);
                alertDialog.cancel();
            }

        });
        ((Button)content.findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             alertDialog.cancel();
            }
        });


        return true;
    }
    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            url.setText(sharedText);
            if(isNetworkConnected()){
                Download(this.findViewById(R.id.button_download));// если перешли из другова приложения сразу получаем ссылки
            }else{
                Toast.makeText(context, R.string.nied_internet_conekt,Toast.LENGTH_LONG).show();
            }

        }
    }




    public void dow(String s, String file){

        //создадим папки если нет
        File sddir = new File(Environment.getExternalStorageDirectory().toString()+"/Movies/");
        if (!sddir.exists()) {
            sddir.mkdirs();
        }


        Uri downloadUri = Uri.parse(s);
        final Uri destinationUri = Uri.parse(Environment.getExternalStorageDirectory().toString()+"/Movies/"+file);


        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .addCustomHeader("Auth-Token", "YourTokenApiKey")
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadContext("mkm")//Optional
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int id) {
                        Toast.makeText(context,"Загрузка завершена в"+destinationUri.toString(),Toast.LENGTH_SHORT).show();
                        progres.setText("");
                    }

                    @Override
                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                        Toast.makeText(context,"Ошибка загрузки",Toast.LENGTH_SHORT).show();
                        progres.setText("");
                    }

                    @Override
                    public void onProgress(int id, long totalBytes, long downlaodedBytes, int progress){
                        progres.setText("Загрузка"+String.valueOf(progress)+"%");
                    }
                });

        ThinDownloadManager downloadManager;
        downloadManager = new ThinDownloadManager();
        downloadManager.add(downloadRequest);
        Toast.makeText(context,"Загрузка началась",Toast.LENGTH_SHORT).show();
    }



    @Override
    public boolean  onKeyUp(int keyCode, KeyEvent event) {
        if(event.getAction() == android.view.KeyEvent.ACTION_UP){
            switch(keyCode) {
                case android.view.KeyEvent.KEYCODE_MENU:
                    //TODO
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
                    ((Button)findViewById(R.id.button_menu)).startAnimation(anim);
                    showPopupMenu(this.findViewById(R.id.button_menu));
                    return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 777) {
            if (resultCode == RESULT_OK) {
                // String thiefname = data.getStringExtra("exit");
                pizdec();
            }else {

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void showPopupMenu(View v) {

        // если андроид меньше 3 то запустим активность с кнопками
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            Intent i  = new Intent(this, Popmenu.class);
            startActivityForResult(i,777);
        }else {
            PopupMenu popupMenu = new PopupMenu(this, v);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                popupMenu.inflate(R.menu.popupmenu); // Для Android 4.0
            }

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB) {
                // для версии Android 3.0 нужно использовать длинный вариант
                popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu());
            }
            popupMenu
                    .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.action_abaut:
                                    Intent i = new Intent(Main.this, Abaut_main.class);
                                    startActivity(i);
                                    return true;
                                case R.id.action_exit:
                                    pizdec();
                                    return true;
                                case R.id.action_history:
                                    Intent h = new Intent(Main.this, History.class);
                                    startActivity(h);
                                    return  true;

                                default:
                                    return false;
                            }
                        }
                    });

            popupMenu.show();
        }
    }

    public void pizdec() {
        this.finish();
    }

    public void clik_logo_menu(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.mytrans);
        ((Button)findViewById(R.id.button_logo)).startAnimation(anim);
        Intent h = new Intent(Main.this, History.class);
        startActivity(h);
    }

    public void Paste(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button)findViewById(R.id.button_paste)).startAnimation(anim);
        if(getText()!=null)
        url.setText(getText());

    }

    public void Delete(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button)findViewById(R.id.button_delet)).startAnimation(anim);
        url.setText("");
    }

    public void Cut(View view){
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button)findViewById(R.id.button_cut)).startAnimation(anim);
        if(url.getText().length()>1) {
            putText(url.getText().toString());
            url.setText("");
        }
    }

    public static   void Download(View view) {

        String url_new = url.getText().toString(); // полный адрес

        if(url_new.length()>3){

            url_new = url_new.replace("\\","/"); //заменим не те сплеши

            if(url_new.contains("/")){ //если адрес полный разделим и достанем id
                if(isNetworkConnected()){
                    to_link.setVisibility(View.GONE);
                    load_link_progres.setVisibility(View.VISIBLE);
                    String ur[] = url_new.split("/");
                    url_new = ur[ur.length-1].toString();//нужен только id
                    TakeLinkDowload takeLinkDowload = new TakeLinkDowload();
                    url_ava = "http://i1.ytimg.com/vi/"+url_new+"/1.jpg";
                    takeLinkDowload.execute("http://i9027296.bget.ru/getvideo.php?id="+url_new);
                }else {
                    Toast.makeText(context,R.string.nied_internet_conekt,Toast.LENGTH_SHORT).show();
                }
            }else { // если только id
                if(isNetworkConnected()){
                    to_link.setVisibility(View.GONE);
                    load_link_progres.setVisibility(View.VISIBLE);
                    TakeLinkDowload takeLinkDowload = new TakeLinkDowload();
                    url_ava = "http://i1.ytimg.com/vi/"+url_new+"/1.jpg";
                    takeLinkDowload.execute("http://i9027296.bget.ru/getvideo.php?id="+url_new);
                }else {
                    Toast.makeText(context,R.string.nied_internet_conekt,Toast.LENGTH_SHORT).show();
                }
            }

        }else {
            Toast.makeText(context,"Введите url видео",Toast.LENGTH_SHORT).show();
        }


    }

    static void load_listviw(){

        //title
        title = new String[getStringArrayPref(context, "a_title").size()];
        for (int i = 0; i != getStringArrayPref(context, "a_title").size(); i++) {
            title[i] = getStringArrayPref(context, "a_title").get(i);
        }

        //url
        url_d = new String[getStringArrayPref(context, "a_url_d").size()];
        for (int i = 0; i != getStringArrayPref(context, "a_url_d").size(); i++) {
            url_d[i] = getStringArrayPref(context, "a_url_d").get(i);
        }

        //quality
        quality = new String[getStringArrayPref(context, "a_quality").size()];
        for (int i = 0; i != getStringArrayPref(context, "a_quality").size(); i++) {
            quality[i] = getStringArrayPref(context, "a_quality").get(i);
        }

        //type
        type= new String[getStringArrayPref(context, "a_type").size()];
        for (int i = 0; i != getStringArrayPref(context, "a_type").size(); i++) {
            type[i] = getStringArrayPref(context, "a_type").get(i);
        }

        //size
        size= new String[getStringArrayPref(context, "a_size").size()];
        for (int i = 0; i != getStringArrayPref(context, "a_size").size(); i++) {
            size[i] = getStringArrayPref(context, "a_size").get(i);
        }

        //syfix
        syfix= new String[getStringArrayPref(context, "a_syfix").size()];
        for (int i = 0; i != getStringArrayPref(context, "a_syfix").size(); i++) {
            syfix[i] = getStringArrayPref(context, "a_syfix").get(i);
        }


        ArrayList<Map<String,Object>> data = new ArrayList<Map<String,Object>>(title.length);

        Map<String,Object> m ;


        String AVA = "ava";

        for(int i = 0;i< title.length;i++){
            m= new HashMap<String,Object>();
            m.put(TITLE,(title.length>i)?title[i]:"v");
            m.put(QUALITY,(quality.length>i)?quality[i]:"-");
            m.put(TYPE,(type.length>i)?type[i]:"-");
            m.put(SIZE,(size.length>i)?size[i]:"-");
            data.add(m);
        }



        String[] from = {
                TITLE,
                QUALITY,
                TYPE,
                SIZE,
        };
        int[] to = {
                R.id.textView_title,
                R.id.textView_quality,
                R.id.textView_type,
                R.id.textView_size,

        };

        Adapter_Youtube adapter_youtube = new Adapter_Youtube(context,data, R.layout.delegat_yotube,from,to);
        textView.setVisibility(View.GONE); // скрываем инфу
        listView.setVisibility(View.VISIBLE); // показываем список
        listView.setAdapter(adapter_youtube);

    }


    static void clear_listviw(){
//устанавливаем пустой массив в ListView
        String mas[] = new String[0];
        listView.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mas));
    }

    public void clik_menu(View v) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button)findViewById(R.id.button_menu)).startAnimation(anim);
        showPopupMenu(v);
    }

    public void Open_yuotube(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button)findViewById(R.id.button_open_youtube)).startAnimation(anim);

        ComponentName cm = new ComponentName(
                "com.google.android.youtube",
                "com.google.android.apps.youtube.app.WatchWhileActivity");

        Intent intent = new Intent(); intent.setComponent(cm);
        startActivity(intent);



    }


    static class TakeLinkDowload extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gud_url =url.getText().toString();
        }
        @Override
        protected String doInBackground(String... params) {

            Document doc = null;
            try {
                doc = Jsoup.connect(params[0].toString())
                        .timeout(20000) //20 секунд будем ждать пока загрузится
                        .get();

            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList a_title = new ArrayList(); // title
            ArrayList a_url_d = new ArrayList(); // ссылка
            ArrayList a_title_h = new ArrayList(); // title_url
            ArrayList a_quality = new ArrayList(); // качество
            ArrayList a_type = new ArrayList(); // кодек
            ArrayList a_size = new ArrayList(); // кодек
            ArrayList a_syfix = new ArrayList(); // кодек

            if(doc!=null) {
                if (doc.select(".link") != null) {
                    Elements elements = doc.select(".link");
                    if (elements != null) {
                        for (int i = 0; i != elements.size(); i++) {
                            a_title.add(elements.get(i).select("title").text());
                            a_url_d.add(elements.get(i).select("url").text());
                            a_quality.add(elements.get(i).select("quality").text());
                            a_type.add(elements.get(i).select("type").text());
                            a_size.add(elements.get(i).select("size").text());
                            a_syfix.add(elements.get(i).select("syfix").text());
                        }

//история*******************************************************************
                        a_title_h = getStringArrayPref(context, "a_title_h");
                        a_title_h.add(a_title.get(0).toString() + "\n" + gud_url);
                        setStringArrayPref(context, "a_title_h", a_title_h);
//****************************************************************************
                        setStringArrayPref(context, "a_title", a_title);
                        setStringArrayPref(context, "a_url_d", a_url_d);
                        setStringArrayPref(context, "a_quality", a_quality);
                        setStringArrayPref(context, "a_type", a_type);
                        setStringArrayPref(context, "a_size", a_size);
                        setStringArrayPref(context, "a_syfix", a_syfix);

                        return a_title.get(0).toString();
                    }
                }
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            load_link_progres.setVisibility(View.GONE);
            to_link.setVisibility(View.VISIBLE);

            if(s.length()>2) { // если чето есть то покажем
                load_listviw(); // заполняем список ссылками
            }else{
                clear_listviw(); // очищаем список
                Toast.makeText(context,"Нечего не найдено",Toast.LENGTH_LONG).show();
            }

        }
    }

//запись
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
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
    //чтение
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    public String getText(){
        String text = null;
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES. HONEYCOMB ) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard.getText()!=null) {
                text = clipboard.getText().toString();
            }
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard.getText()!=null) {
                text = clipboard.getText().toString();
            }
        }
        return text;
    }
//*****************************************************************************************************
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void save_value(String Key,String Value){ //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public String save_read(String key_save){  // чтение настройки
        if(mSettings.contains(key_save)) {
            return (mSettings.getString(key_save, ""));
        }
        return "";
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

//***********************************************************************************
    public static boolean setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        return editor.commit();
    }
    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
//*********************************************************************
}
