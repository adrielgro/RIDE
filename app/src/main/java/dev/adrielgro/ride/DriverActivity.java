package dev.adrielgro.ride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DriverActivity extends AppCompatActivity {

    private Button buttonDriverOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        buttonDriverOn = (Button) findViewById(R.id.buttonDriverOn);

        buttonDriverOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
