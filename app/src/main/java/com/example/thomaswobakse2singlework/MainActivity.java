package com.example.thomaswobakse2singlework;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText inputField;
    private TextView resultTextView;
    private static final String DOMAIN_NAME = "se2-submission.aau.at";
    private static final int PORT = 20080;
    private static final int MATRIKELNUMMER_MINIMUM_LENGTH = 7;
    private static final int MATRIKELNUMMER_MAXIMUM_LENGTH = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputField= findViewById(R.id.InputMatrikNr);
        Button buttonQuer = (Button) findViewById(R.id.buttonQuersumme);
        buttonQuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String matrikelnummmer=inputField.getText().toString();
               if(matrikelnummmer.length()<=MATRIKELNUMMER_MAXIMUM_LENGTH||matrikelnummmer.length()>=MATRIKELNUMMER_MINIMUM_LENGTH){
                   int result=0;
                   for (int i=0; i<matrikelnummmer.length();i++){
                       int currentNumber = Integer.parseInt(matrikelnummmer.toString().substring(i, i + 1));
                       result+=i%2==0? currentNumber :-currentNumber;
                   }
                  resultTextView=findViewById(R.id.textView);
                   resultTextView.setText(""+result);
               }
            }
        });
        Button buttonServer= (Button) findViewById(R.id.buttonServer);
        buttonServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String matrikelnummmer=inputField.getText().toString();
                if(matrikelnummmer.length()<=MATRIKELNUMMER_MAXIMUM_LENGTH||matrikelnummmer.length()>=MATRIKELNUMMER_MINIMUM_LENGTH){
                    sendMatrikelNummer(matrikelnummmer);
                }
            }
        });
    }

    private void sendMatrikelNummer(final String matNr){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket=new Socket(DOMAIN_NAME,PORT);

                    PrintWriter writer=new PrintWriter(socket.getOutputStream(),true);
                    writer.println(matNr);

                    BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    StringBuilder response=new StringBuilder();
                    String nextLine;

                    while((nextLine=reader.readLine())!=null){
                        response.append(nextLine);
                    }
                    String result="Antwort vom Server: "+response.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultTextView=findViewById(R.id.textView);
                            resultTextView.setText(result);
                        }
                    });


                    socket.close();

                } catch (IOException e) {
                    System.out.println("ERROR");
                    resultTextView=findViewById(R.id.textView);
                    resultTextView.setText("Fehler beim Kommunizieren mit Server");
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }
}