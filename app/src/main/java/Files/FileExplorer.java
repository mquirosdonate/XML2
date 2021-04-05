package Files;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.xml.R;

public class FileExplorer extends AppCompatActivity {
    private static final int REQUEST_PATH = 1;

    String curFileName;
    String curPath;
    TextView path;
    TextView fileName;
    Button buscar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);
        fileName = (TextView)findViewById(R.id.FileName);
        path = (TextView)findViewById(R.id.Path);
        buscar = (Button)findViewById(R.id.skipButton);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getfile();
            }
        });
    }
    public void getfile(){
        Intent intent1 = new Intent(this, FileChooser.class);
        startActivityForResult(intent1,REQUEST_PATH);
    }
    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See which child activity is calling us back.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PATH) {
            if (resultCode == RESULT_OK) {
                curPath = data.getStringExtra("GetPath");
                curFileName = data.getStringExtra("GetFileName");
                fileName.setText(curFileName);
                path.setText(curPath);
            }
        }
    }
}