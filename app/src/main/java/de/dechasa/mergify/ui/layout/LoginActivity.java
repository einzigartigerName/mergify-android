package de.dechasa.mergify.ui.layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import de.dechasa.mergify.R;
import de.dechasa.mergify.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "your-client-id";
    private static final String REDIRECT_URI = "your-redirect-uri";

    private static final String[] scopes = new String[]{
            "user-read-private",
            "user-read-email",
            "playlist-read-private",
            "playlist-read-collaborative",
            "playlist-modify-public",
            "playlist-modify-private"
    };

    private ActivityLoginBinding binding;
    private SharedPreferences storage;
    private static String TOKEN_KEY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TOKEN_KEY = getString(R.string.storage_token);
        storage = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
    }

    /**
     * Login Button pressed
     */
    public void onClickLogin(final View view) {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(scopes);
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    String token = response.getAccessToken();
                    saveToken(token);
                    startApplication();
                    break;
                // Auth flow returned an error
                case ERROR:
                    binding.txtLoginFeedback.setText(response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    /**
     * Write the Token to SharedPreferences
     * @param token spotify token to write
     */
    private void saveToken(String token) {
        SharedPreferences.Editor editor = storage.edit();

        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    /**
     * Start the Main Activity
     */
    private void startApplication() {
        Intent main = new Intent(this, MainActivity.class);

        startActivity(main);
    }
}
