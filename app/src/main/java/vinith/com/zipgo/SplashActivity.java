package vinith.com.zipgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liveongo on 22/9/16.
 */
public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private LoginButton mFbLoginButton;
    public static CallbackManager mCallBackManager;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mSharedPreferenceEditor;
    private int RC_SIGN_IN = 99;
    GoogleApiClient mGoogleApiClient;


    public static final String MyPREFERENCES = "MyPrefs";
    public static final String FACEBOOKPROFILENAME = "facebook";
    public static final String GOOGLEPROFILENAME = "googlesignin";
    public static final String LOGINTYPE = "loginType";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_splash);


        mSharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mSharedPreferenceEditor = mSharedPreferences.edit();

        /**
         * Auto login  once sign in
         */
        if (mSharedPreferences.getString(GOOGLEPROFILENAME, null) != null || mSharedPreferences.getString(FACEBOOKPROFILENAME, null) != null) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        // Customize sign-in button. The sign-in button can be displayed in
//        // multiple sizes and color schemes. It can also be contextually
//        // rendered based on the requested scopes. For example. a red button may
//        // be displayed when Google+ scopes are requested, but a white button
//        // may be displayed when only basic profile is requested. Try adding the
//        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
//        // difference.
//        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        signInButton.setScopes(gso.getScopeArray());

        mFbLoginButton = (LoginButton) findViewById(R.id.login_button);
        mFbLoginButton.setReadPermissions("email");


        mCallBackManager = CallbackManager.Factory.create();

        /**
         * Facebook Login
         */
        // Callback registration
        mFbLoginButton.registerCallback(mCallBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                JSONObject JSONobj = graphResponse.getJSONObject();

                                try {
                                    String name = JSONobj.getString("name");
                                    mSharedPreferenceEditor.putString(FACEBOOKPROFILENAME, name);
                                    mSharedPreferenceEditor.putInt(LOGINTYPE, 2);
                                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                    mSharedPreferenceEditor.commit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, first_name, last_name,age_range,gender,email,birthday,hometown,location,photos,work,education");
                request.setParameters(parameters);
                request.executeAsync();
            }


            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        /**
         * Google Sign In
         */
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();


            }
        });


    }


    private void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
                        }
                    });
        }


    }


    /**
     * Google Sign In
     */
    private void signIn() {

        signOut();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    /**
     * Google Sign In Successfully and saving profile name and type in SharedPreference
     * for Auto Login
     *
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("LOGGER", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            mSharedPreferenceEditor.putString(GOOGLEPROFILENAME, acct.getDisplayName());
            mSharedPreferenceEditor.putInt(LOGINTYPE, 1);
            mSharedPreferenceEditor.commit();
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallBackManager.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
