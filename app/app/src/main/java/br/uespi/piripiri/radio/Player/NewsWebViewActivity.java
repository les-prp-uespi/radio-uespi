package br.uespi.piripiri.radio.Player;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gustavorw.app.R;

public class NewsWebViewActivity extends AppCompatActivity {
    private WebView newsWeb;
    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        newsWeb = findViewById(R.id.newsWebView);

        progress = findViewById(R.id.progress_web);
        progress.setMax(100);
        Intent intent = getIntent();
        newsWeb.getSettings().setJavaScriptEnabled(true);
        //String link = intent.getStringExtra("link");
        String id = intent.getStringExtra("id");

        String url = "https://portaldaibiapaba.com.br/prp/noticia.php?id="+id;
        //Log.v("link",url);
        // eliminar a verificação de ssl
        newsWeb.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        newsWeb.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progress.setProgress(newProgress);
                if(newProgress == 100){
                    progress.setVisibility(View.GONE);
                }
            }

        });
        newsWeb.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_noticia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rate) {
            Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.share_news) {
            share();
            Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //funcao menu
    public void share() {
        Intent intent = getIntent();
        String link = intent.getStringExtra("link");
        String titulo = intent.getStringExtra("titulo");
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, titulo+"\n\n"+"Ler mais em: "+link);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.via)));
    }

}
