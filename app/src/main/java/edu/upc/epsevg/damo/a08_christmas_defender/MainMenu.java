package edu.upc.epsevg.damo.a08_christmas_defender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends Activity {

    Context THIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THIS = this;
        super.onCreate(savedInstanceState);

        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_menu);
    }

    public void onClick(View view) {
        Intent intent = new Intent(THIS, MainGame.class);
        startActivity(intent);
    }

}