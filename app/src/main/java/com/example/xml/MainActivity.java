package com.example.xml;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Files.FileChooser;
import HTTPAsync.HTTPAsyncLoopj;
import xmltools.ParseXML;

public class MainActivity extends AppCompatActivity {
    private TextView txtFileXML;
    private EditText txtFileXMLSalida;
    private Button buscarXML;
    private CheckBox sobrescribir;
    private File sdDir;
    List<String> listaFicheros = new ArrayList<>();
    static  StringBuilder informe = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!checkAndRequestPermissions())
            return;

        sobrescribir = findViewById(R.id.checkBoxSobrescribir);
        txtFileXML = findViewById(R.id.fileXML);
        txtFileXMLSalida = findViewById(R.id.fileXMLSalida);
        buscarXML = findViewById(R.id.BuscarXML);
        buscarXML.setFocusableInTouchMode(true);
        buscarXML.requestFocus();

        //Crear la carpeta de la app para organizar archivos
        File sdCard = Environment.getExternalStorageDirectory();
        String nameApp = (String)this.getString(R.string.app_name);
        sdDir = new File(sdCard.getAbsolutePath() + "/" + nameApp);
        sdDir.mkdir();

        buscarXML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarXML.setVisibility(View.GONE);
                txtFileXML.setText("Buscando ficheros 130.xml ...");
                listaFicheros.clear();
                informe.setLength(0);
                buscarXMLs(sdDir);
                informe.append("Encontrados: "+listaFicheros.size()+" ficheros 130.xml\n");
                transformarXMLs();
            }
        });
        buscarXML.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                buscarXML();
                return false;
            }
        });
    }
    private void transformarXMLs(){
        if (listaFicheros.size()>0) {
            String fileName = listaFicheros.get(0);
            listaFicheros.remove(0);
            txtFileXML.setText(listaFicheros.size()+" "+fileName);
            informe.append(fileName+"\n");
            getNodeValue(fileName, "WMS_Capabilities/Capability/Layer/Layer/MetadataURL/OnlineResource", "xlink:href");
        }else{

            txtFileXML.setText("TRABAJO TERMINADO !!!");
            txtFileXMLSalida.setText(informe.toString());
            //Escribir informe en un fichero
            try {
                File file =  new File(sdDir,"informe.txt");
                OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(file, false));
                fout.write(informe.toString());
                fout.flush();
                fout.close();
            }catch (Exception e){}
        }
    }

    private void buscarXMLs(File f) {
        if (f == null) return;
        File[] dirs = f.listFiles();
        try {
            for (File ff : dirs) {
                if (ff.getName().compareToIgnoreCase("130.xml")==0)
                    listaFicheros.add(ff.getAbsolutePath());

                if (ff.isDirectory())
                    buscarXMLs(ff);
            }
        }catch (Exception e) { }
    }
    private static final int REQUEST_PATH = 1;
    private void buscarXML(){
        Intent intent1 = new Intent(this, FileChooser.class);
        Bundle bundle = new Bundle();
        bundle.putString("SDDIR",sdDir.toString());
        intent1.putExtras(bundle);
        startActivityForResult(intent1,REQUEST_PATH);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PATH:
                switch (resultCode) {
                    case RESULT_OK:
                        String curPath = data.getStringExtra("GetPath");
                        String curFileName = data.getStringExtra("GetFileName");
                        String fileName = curPath + "/" + curFileName;
                        getNodeValue(fileName,"WMS_Capabilities/Capability/Layer/Layer/MetadataURL/OnlineResource","xlink:href");
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "No has selecionado NADA", Toast.LENGTH_LONG);
                        break;
                }
                break;

        }
    }
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private  boolean checkAndRequestPermissions() {
        int permissionLeer = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLeer != PackageManager.PERMISSION_GRANTED)listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        //Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if ( perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        finish();
                        startActivity(getIntent());
                    } else {
                        //Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if ( ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        {
                            showDialogOK("WRITE_STORAGE Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //proceed with logic by disabling the related features or quit the app.
                            finish();
                        }
                    }
                }
            }
        }
    }
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ParseXML p;
    private void getNodeValue(String fileName,String path,String nodeName){
        p = new ParseXML(fileName);
        //path "WMS_Capabilities/Capability/Layer/Layer/MetadataURL/OnlineResource"
        //nodeName "xlink:href"
        List<Node> mis_nodos = p.getNodos(path,p.documento);
        if (mis_nodos.size()>0){
            Node aux = mis_nodos.get(0);
            if (aux.hasAttributes()) {
                NamedNodeMap nodoMap = aux.getAttributes();
                for (int i = 0; i < nodoMap.getLength(); i++) {
                    String nombreAtributo = nodoMap.item(i).getNodeName();
                    String valorAtributo = nodoMap.item(i).getNodeValue();
                    if (nombreAtributo.compareTo(nodeName) == 0 ){
                        String href = valorAtributo;//.replace("amp;","");
                        getFileFromURL(href);
                        return;
                    }
                }
            }
        }
        //Si no hay un nodo de tipo path y nodeName que se le pasan saltamos al siguiente de la lista
        informe.append("    No hay attr de enlace: "+nodeName+"\n");
        p.recorreDOM("");
        p.writeXML(sobrescribir.isChecked());
        transformarXMLs();
    }

    private void getFileFromURL(String url){
        final HTTPAsyncLoopj com = new HTTPAsyncLoopj();
        //url = "http://www.mapama.es/ide/metadatos/srv/spa/csw?SERVICE=CSW&VERSION=2.0.2&REQUEST=GetRecordById&outputSchema=http://www.isotc211.org/2005/gmd&ElementSetName=full&ID=2602aafe-591d-4fa6-afb0-459d8f085209";
        com.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onStart(){
                Toast.makeText(getApplicationContext(), "Conectando con el servidor...", Toast.LENGTH_LONG);
            }
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                String XML = new String(bytes);
                XML = XML.trim();
                p.recorreDOM(XML);
                p.writeXML(sobrescribir.isChecked());
                informe.append("    OK\n");
                transformarXMLs();
            }
            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Error en la conexión", Toast.LENGTH_LONG);
                informe.append("    Error en la conexión\n");
                transformarXMLs();
            }
        });
    }

}
