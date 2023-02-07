package au.example.ecbratesfromuri;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    String uri = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView contentView = findViewById(R.id.content);
        Button btnFetch = findViewById(R.id.downloadBtn);

        btnFetch.setOnClickListener(v -> {
            contentView.setText("Загрузка...");
            new Thread(() -> {
                try{
                    content = getContent(uri);
                    contentView.post(() -> contentView.setText(content));
                }
                catch (IOException ex){
                    contentView.post(() -> {
                        contentView.setText(getString(R.string.error_label).concat(Objects.requireNonNull(ex.getMessage())));
                        Toast.makeText(getApplicationContext(), getString(R.string.error_label), Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });
    }

    private String getContent(String path) throws IOException {
        BufferedReader reader=null;
        InputStream stream = null;
        HttpsURLConnection connection = null;
        try {
            URL url=new URL(path);
            connection =(HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();
            stream = connection.getInputStream();
            reader= new BufferedReader(new InputStreamReader(stream));
            StringBuilder buf=new StringBuilder();
            String line;
            while ((line=reader.readLine()) != null) {
                buf.append(line).append("\n");
            }
            return(buf.toString());
        }
        finally {
            if (reader != null) {
                reader.close();
            }
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}