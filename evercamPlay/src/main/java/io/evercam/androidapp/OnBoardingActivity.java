package io.evercam.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import io.evercam.androidapp.onboarding.IntroTextureView;
import io.evercam.androidapp.utils.Constants;

public class OnBoardingActivity extends ParentAppCompatActivity {
    private final String TAG = "OnBoardingActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        IntroTextureView textureView = (IntroTextureView) findViewById(R.id.intro_texture_view);

        Button signUpButton = (Button) findViewById(R.id.btn_welcome_signup);
        Button loginButton = (Button) findViewById(R.id.btn_welcome_login);

        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(OnBoardingActivity.this, SignUpActivity.class);
                startActivityForResult(signup, Constants.REQUEST_CODE_SIGN_UP);
            }
        });

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(OnBoardingActivity.this, LoginActivity.class);
                startActivityForResult(login, Constants.REQUEST_CODE_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_SIGN_IN || requestCode == Constants
                .REQUEST_CODE_SIGN_UP) {
            if (resultCode == Constants.RESULT_TRUE) {
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }


}