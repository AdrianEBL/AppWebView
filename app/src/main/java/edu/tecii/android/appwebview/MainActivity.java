package edu.tecii.android.appwebview;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toolbar myToolbar;
    WebView webView;
    Button back, forward;
    EditText edtxt;
    ProgressBar pBar;

    static ArrayList<History> visitas = new ArrayList();
    String enlacePag = "https://www.google.com.mx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");

        webView = (WebView) findViewById(R.id.webView);
        edtxt = (EditText) findViewById(R.id.editText);
        back = (Button) findViewById(R.id.button);
        forward = (Button) findViewById(R.id.button2);
        pBar = (ProgressBar) findViewById(R.id.progressBar);

        try {
            enlacePag = getIntent().getExtras().getString("enlace");
            if (enlacePag == "" || enlacePag == null) {
                enlacePag = "https://www.google.com.mx";
            }
        } catch (Exception e) { }
        cargarHistorial();

        WebSettings configuracion = webView.getSettings();
        configuracion.setAppCacheEnabled(true);
        configuracion.setJavaScriptEnabled(true);
        configuracion.setAllowContentAccess(true);
        configuracion.setBuiltInZoomControls(true);
        configuracion.setSupportZoom(true);
        configuracion.setDisplayZoomControls(true);

        webView.loadUrl(enlacePag);
        edtxt.setText(webView.getUrl().toString());
        onPages();

        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading (WebView view, WebResourceRequest resource){
                return shouldOverrideUrlLoading(view, resource);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                edtxt.setText(url.toString());
                pBar.setVisibility(View.VISIBLE);
                onPages();
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                edtxt.setText(url.toString());
                pBar.setVisibility(View.GONE);
                onPages();
                History historial = new History(webView.getTitle(), webView.getUrl().toString());
                visitas.add(historial);
                guardarHistorial();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onForwardPressed();
            }
        });

        edtxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    String dirURL = edtxt.getText().toString();
                    boolean resultado = dirURL.startsWith("https://");
                    if (resultado) {
                        webView.loadUrl(dirURL);
                    } else {
                        webView.loadUrl("https://"+dirURL);
                    }
                    return true;
                }
                return false;
            }
        });

        edtxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                edtxt.setText(webView.getUrl().toString());
            }
        });

        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                pBar.setProgress(0);
                pBar.setVisibility(View.VISIBLE);
                MainActivity.this.setProgress(progress * 1000);

                pBar.incrementProgressBy(progress);

                if(progress == 100) {
                    pBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemUpdate:
                webView.loadUrl(webView.getUrl().toString());
                return true;

            case R.id.itemHome:
                webView.loadUrl("https://www.google.com.mx");
                return true;

            case R.id.itemHistory:
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                return true;

            case R.id.itemLink:
                ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newUri(getContentResolver(),"link", Uri.parse(webView.getUrl().toString()));
                clipBoard.setPrimaryClip(clip);
                Toast.makeText(this, "Vinculo Copiado al portapales", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.itemOut:
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse(webView.getUrl().toString()));
                startActivity(intent1);
                return true;

            case R.id.itemAbout:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Toast.makeText(this, "No existen paginas que visitar", Toast.LENGTH_SHORT).show();
        }
    }

    public void onForwardPressed() {
        if (webView.canGoForward()) {
            webView.goForward();
        }
    }

    public void onPages() {
        if (webView.canGoBack()) {
            back.setEnabled(true);
        } else {
            back.setEnabled(false);
        }
        if (webView.canGoForward()) {
            forward.setEnabled(true);
        } else {
            forward.setEnabled(false);
        }
    }


    public void guardarHistorial() {
        try {
            File file = new File(getApplication().getFilesDir(), "historial.txt");
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file, false));
            stream.writeObject(visitas);
            stream.close();
        } catch (Exception e) {
        }
    }

    public void cargarHistorial() {
        try {
            File file = new File(getApplicationContext().getFilesDir(), "historial.txt");
            if (file.exists()){
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
                visitas = (ArrayList<History>)stream.readObject();
                stream.close();
            }
        } catch (Exception e) {

        }
    }
}
