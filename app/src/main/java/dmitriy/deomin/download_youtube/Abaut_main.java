package dmitriy.deomin.download_youtube;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Abaut_main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abaut);

        String v = getVersion();
        String t= getApplication().getResources().getString(R.string.Abaut_text);
        t= t.replace("++++++++++","Скачать с ютуба "+v);
        ((TextView) findViewById(R.id.textView_info)).setText(t);

    }

    public void clik_mail(View view) {
        Uri uri = Uri.parse("mailto:58627@bk.ru");
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(it);
    }
    public  void del_soft(View view) {
        Uri uri = Uri.fromParts("package", "dmitriy.deomin.download_youtube", null);
        Intent it = new Intent(Intent.ACTION_DELETE, uri);
        startActivity(it);
    }

    private String getVersion(){
        try {
            PackageManager packageManager=getPackageManager();
            PackageInfo packageInfo=packageManager.getPackageInfo(getPackageName(),0);
            return packageInfo.versionName;
        }
        catch (  PackageManager.NameNotFoundException e) {
            return "?";
        }
    }

    public void clik(View view) {
        this.finish();
    }
}
