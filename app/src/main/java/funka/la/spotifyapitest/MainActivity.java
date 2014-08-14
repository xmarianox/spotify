package funka.la.spotifyapitest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;

import java.net.URI;

public class MainActivity extends Activity implements PlayerNotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "c5248de726c44a5d9dd9011ae79fc26d";

    private static final String REDIRECT_URI = "funkaprotocol://callback";

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpotifyAuthentication.openAuthWindow(CLIENT_ID, "token", REDIRECT_URI,
                new String[]{"user-read-private", "user-library-read", "playlist-read-private", "streaming"}, null, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null) {

            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Spotify spotify = new Spotify(response.getAccessToken());

            Log.d("MainActivity", "Spotify - RESPONSE: " + spotify.toString());

            mPlayer = spotify.getPlayer(this, "Funka.la", this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {
                    mPlayer.addConnectionStateCallback(MainActivity.this);
                    mPlayer.addPlayerNotificationCallback(MainActivity.this);
                    mPlayer.play("spotify:track:3FzKPS0oVknVlCW3PhxIHl");
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.d("MainActivity", "No se pudo inisializar el player: " + throwable.getMessage());
                }
            });
        }


    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "Usuario loggeado");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "Usuario Loggedout");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Ocurrio un Error!!!");
    }

    @Override
    public void onNewCredentials(String s) {
        Log.d("MainActivity", "Credenciales de usuario recibidas...");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Recibiendo mensaje de conexion" + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType) {
        Log.d("MainActivity", "Playback event received" + eventType.name());
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}
