package vinith.com.zipgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private String TAG = MainActivity.class.getSimpleName();
    Spinner mFromSpinner, mToSpinner;
    RelativeLayout mContentLayout;
    ProgressBar mProgressBar;
    List<Stops> mStopList;
    List<Routes> mRoutesList;


    int mRefreshFromPosition;
    int mToRefreshPosition;

    List<String> mListOfStops;

    boolean mRefreshFlag = false;

    String mFromSpinnerValue = null, mToSpinnerValue = null;

    /**
     * stroring spinner Positions;
     */
    int mFromSpinnerPosition = -1, mToSpinnerPosition = -1;

    ImageView mSwipeRefresh;
    ListView mRoutesListView;

    RoutesListAdapter mRoutesListAdapter;

    /**
     * having List of Routes
     */
    List<Routes> listOfRoutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentLayout = (RelativeLayout) findViewById(R.id.main_layout);

        mFromSpinner = (Spinner) findViewById(R.id.spinner1);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarMain);

        mRoutesListView = (ListView) findViewById(R.id.listView);

        mSwipeRefresh = (ImageView) findViewById(R.id.iv_swipe_refresh);


        mContentLayout.setVisibility(View.GONE);

        mToSpinner = (Spinner) findViewById(R.id.spinner2);

        mStopList = new ArrayList<>();

        mRoutesList = new ArrayList<>();

        listOfRoutes = new ArrayList<>();


        mRoutesListAdapter = new RoutesListAdapter(getApplicationContext(), listOfRoutes);


//         From Spinner click listener
        mFromSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) MainActivity.this);


        // To Spinner click listener
        mToSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) MainActivity.this);

        /**
         * get Stops for Drop Down List
         */
        getStops();


        /**
         * Swipe Refresh for refreshing list and Drop Down list
         */
        mSwipeRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOfRoutes.size() > 0) {
                    Collections.reverse(listOfRoutes);
                    mRoutesListAdapter.notifyDataSetChanged();


                    if (!mRefreshFlag) {
                        mFromSpinner.setOnItemSelectedListener(null);
                        mToSpinner.setOnItemSelectedListener(null);
                        mFromSpinner.setSelection(mToSpinnerPosition, false);
                        mToSpinner.setSelection(mFromSpinnerPosition, false);
                        mRefreshFlag = true;
                        mFromSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) MainActivity.this);
                        mToSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) MainActivity.this);


                    } else {

                        mFromSpinner.setOnItemSelectedListener(null);
                        mToSpinner.setOnItemSelectedListener(null);
                        mFromSpinner.setSelection(mFromSpinnerPosition, false);
                        mToSpinner.setSelection(mToSpinnerPosition, false);
                        mRefreshFlag = false;

                        mFromSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) MainActivity.this);
                        mToSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) MainActivity.this);


                    }

                }

            }
        });


    }


    /**
     * Method to make json array request for getting Stops
     */
    private void getStops() {

        String listOfStops = "http://www.mocky.io/v2/57dd85c71100000302a2def5";

        JsonArrayRequest stopsRequest = new JsonArrayRequest(listOfStops,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        /**
                         * Exception Handling for Json Parsing
                         */
                        try {

                            // Parsing json array response
                            // loop through each json object
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject person = (JSONObject) response
                                        .get(i);

                                int id = person.getInt("id");
                                String name = person.getString("name");
                                String description = person.getString("description");

                                mStopList.add(new Stops(id, name, description));
                            }


                            /**
                             * get Routes for finding routes
                             */
                            getRoutes();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stopsRequest);
    }


    /**
     * Method to make json array request for getting Routes
     */
    private void getRoutes() {

        String listOfStops = "http://www.mocky.io/v2/57dd88221100005402a2def7";

        JsonArrayRequest routesRequest = new JsonArrayRequest(listOfStops,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        /**
                         * Exception Handling for Json Parsing
                         */
                        try {
                            // Parsing json array response
                            // loop through each json object
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject person = (JSONObject) response.get(i);

                                int id = person.getInt("id");
                                String name = person.getString("name");
                                String description = person.getString("description");
                                JSONArray stop_sequence = person.getJSONArray("stops_sequence");


                                mRoutesList.add(new Routes(id, name, description, stop_sequence));


                            }


                            setStopSpinner(mStopList);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(routesRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Menu item click for log out
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.MyPREFERENCES, Context.MODE_PRIVATE);


                if (sharedPreferences.getInt(SplashActivity.LOGINTYPE, 0) == 1) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SplashActivity.GOOGLEPROFILENAME, null);
                    editor.putInt(SplashActivity.LOGINTYPE, 0);
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                    intent.putExtra("logout", 1);
                    startActivity(intent);
                    MainActivity.this.finish();
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SplashActivity.FACEBOOKPROFILENAME, null);
                    editor.putInt(SplashActivity.LOGINTYPE, 0);
                    editor.commit();
                    logout();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Facebook Log out Function
     */
    private void logout() {
        LoginManager.getInstance().logOut();

        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * set Spinners Adapter
     *
     * @param stopsList
     */
    public void setStopSpinner(List<Stops> stopsList) {
        mListOfStops = new ArrayList<>();

        for (int i = 0; i < stopsList.size(); i++) {
            Stops stops = stopsList.get(i);
            mListOfStops.add(stops.getName());
        }
        List<String> listStops2 = new ArrayList<>();
        listStops2.addAll(mListOfStops);
        listStops2.add("From");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, listStops2) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) view.findViewById(R.id.text_item)).setText("");
                    ((TextView) view.findViewById(R.id.text_item)).setHint(getItem(getCount()));

                }
                return view;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };


        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);


        // attaching data adapter to spinner
        mFromSpinner.setAdapter(dataAdapter);
        mFromSpinner.setSelection(dataAdapter.getCount());


        List<String> listStops1 = new ArrayList<>();
        listStops1.addAll(mListOfStops);
        listStops1.add("To");

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, listStops1) {


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) view.findViewById(R.id.text_item)).setText("");
                    ((TextView) view.findViewById(R.id.text_item)).setText(getItem(getCount()));

                }
                return view;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };
        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);


        // attaching data adapter to spinner
        mToSpinner.setAdapter(dataAdapter1);
        mToSpinner.setSelection(dataAdapter1.getCount());


        mContentLayout.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.GONE);

    }


    /**
     * Adding list of routes having these stops
     *
     * @param fromPostion
     * @param toPostion
     */
    private void setValue(int fromPostion, int toPostion) {
        for (int i = 0; i < mRoutesList.size(); i++) {
            try {
                JSONArray jsonArray = mRoutesList.get(i).getStops_sequence();
                List<Integer> listOfIds = new ArrayList<>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    listOfIds.add((Integer) jsonArray.get(j));
                }

                int fromId = mStopList.get(fromPostion).getId();
                int toId = mStopList.get(toPostion).getId();


                /**
                 *
                 * Adding list of routes having these stops
                 */
                if (listOfIds.contains(fromId) && listOfIds.contains(toId)) {


                    listOfRoutes.add(mRoutesList.get(i));

                }
            } catch (JSONException e) {

            }


        }

        mRoutesListView.setAdapter(mRoutesListAdapter);


    }


    /**
     * Item Selected event for Spinners
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.spinner1) {
            // On selecting a spinner item
            mFromSpinnerValue = parent.getItemAtPosition(position).toString();
            mFromSpinnerPosition = position;

            /**
             * If its not match list is clear
             */
            if (listOfRoutes.size() > 0) {
                listOfRoutes.clear();
                mRoutesListAdapter.notifyDataSetChanged();
            }
            if (mFromSpinnerPosition == mToSpinnerPosition && mFromSpinnerPosition != mListOfStops.size()) {
                listOfRoutes.clear();
                mRoutesListAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "Please select correct destination", Toast.LENGTH_SHORT).show();

            } else {

                if (mFromSpinner != mToSpinner && mFromSpinnerPosition != mListOfStops.size() && mToSpinnerPosition != mListOfStops.size()) {
                    setValue(mFromSpinnerPosition, mToSpinnerPosition);


                }
            }

        } else if (spinner.getId() == R.id.spinner2) {
            // On selecting a spinner item
            mToSpinnerValue = parent.getItemAtPosition(position).toString();
            mToSpinnerPosition = position;

            if (listOfRoutes.size() > 0) {
                listOfRoutes.clear();
                mRoutesListAdapter.notifyDataSetChanged();
            }

            if (mFromSpinnerPosition == mToSpinnerPosition && mFromSpinnerPosition != mListOfStops.size()) {
                listOfRoutes.clear();
                mRoutesListAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "Please select correct destination", Toast.LENGTH_SHORT).show();

            } else {
                if (mFromSpinner != mToSpinner && mFromSpinnerPosition != mListOfStops.size() && mToSpinnerPosition != mListOfStops.size()) {
                    setValue(mFromSpinnerPosition, mToSpinnerPosition);

                }
            }


        }


    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        mFromSpinnerPosition = -1;
        mToSpinnerPosition = -1;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
