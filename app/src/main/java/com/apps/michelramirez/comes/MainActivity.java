package com.apps.michelramirez.comes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.widget.EditText;

import org.json.JSONArray;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnIngresar;
    EditText usuario, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario=(EditText)findViewById(R.id.usuario);
        password=(EditText)findViewById(R.id.password);
        btnIngresar=(Button)findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Thread tr=new Thread()
        {
            @Override
            public void run()
            {
                final String resultado=enviarDatosGET(usuario.getText().toString(),password.getText().toString());
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int r=obtDatosJSON(resultado);
                        if(r>0)
                        {
                            Intent i=new Intent(getApplicationContext(),login.class);
                            i.putExtra("cod",usuario.getText().toString());
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Usuario o password incorrectos",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        tr.start();
    }

    public String enviarDatosGET(String usuario,String password)
    {
        URL url=null;
        String linea="";
        int respuesta=0;
        StringBuilder result=null;

        try
        {
            url=new URL("http://192.168.56.1/Comes/login.php?usuario="+usuario+"&password="+password);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            respuesta=connection.getResponseCode();

            result=new StringBuilder();

            if(respuesta==HttpURLConnection.HTTP_OK)
            {
                InputStream in=new BufferedInputStream(connection.getInputStream());
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                while((linea=reader.readLine())!=null)
                {
                    result.append(linea);
                }
            }
        }
        catch(Exception e){}

        return result.toString();
    }

    public int obtDatosJSON(String response)
    {
        int res=0;
        try
        {
            JSONArray json=new JSONArray(response);
            if(json.length()>0)
                res=1;
        }catch(Exception e){}
        return res;
    }

}
