package com.example.tomipc.foodzye;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.tomipc.foodzye.model.Food;
import com.example.tomipc.foodzye.model.Menu;
import com.example.tomipc.foodzye.model.Review;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URL;


public class Database {

    HttpURLConnection connection;
    private static String URL = "http://10.0.3.2/";

    private Context context;

    private ArrayList<Food> arrayOfFood = new ArrayList<>();
    private ArrayList<Menu> arrayOfMenu = new ArrayList<>();
    private ArrayList<Review> arrayOfReview = new ArrayList<>();
    private ArrayList<Review> arrayOfSingleReview = new ArrayList<>();

    public Database(){
    }

    public Database(Context c){
        this.context = c;
    }

    /**
     * data is Hash map that is going to be inserted into database
     * route is the name of the route, only last part, http://10.0.3.2/route
     * @param data
     * @param route
     */
    public void insert (HashMap data, String route) {

        //System.out.println(URL+route+" "+data);

        PostResponseAsyncTask task = new PostResponseAsyncTask(context, data, new AsyncResponse() {
            @Override
            public void processFinish(String result) {

                if (result.equals("success")) {
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context, "Error. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        task.execute(URL + route);
    }


    public ArrayList<Review> readUserReview (String route, String menu_idIn, String user_idIn) {
        try {
            String foodJSON;
            foodJSON = new Read().execute(URL+route+"/"+menu_idIn+"/"+user_idIn).get();
            JSONArray obj = new JSONArray(foodJSON);

            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);

                String comment = jObject.getString("comment");
                double rate = jObject.getDouble("rate");

                Review review = new Review(comment, rate );

                arrayOfSingleReview.add(review);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return arrayOfSingleReview;
    }

    public ArrayList<Review> readReview (String route, String menu_idIn) {
        try {
            String foodJSON;
            foodJSON = new Read().execute(URL+route+"/"+menu_idIn).get();
            JSONArray obj = new JSONArray(foodJSON);

            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);

                String comment = jObject.getString("comment");
                double rate = jObject.getDouble("rate");
                String name = jObject.getString("name");
                String picture = jObject.getString("user_picture");

                Review review = new Review(comment, rate, name, picture );

                arrayOfReview.add(review);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return arrayOfReview;
    }

    public ArrayList<Food> readFood (String route) {
                try {
                    String foodJSON;
                    foodJSON = new Read().execute(URL+route+"/").get();
                    JSONArray obj = new JSONArray(foodJSON);

                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject jObject = obj.getJSONObject(i);

                        int id = jObject.getInt("id");
                        String name = jObject.getString("name");

                        Food food = new Food(id, name);

                        arrayOfFood.add(food);
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }

        return arrayOfFood;
    }

    //TODO: Znam da mogu imat univerzalnu read funkciju samo moram imat poziv konstruktora koji prima univerzalni parametar, mozak mi ne radi trenutno glupo rijesenje.
    public ArrayList<Menu> readMenu (String route) {
        try {
            String foodJSON;
            foodJSON = new Read().execute(URL+route+"/").get();
            JSONArray obj = new JSONArray(foodJSON);

            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);

                int id = jObject.getInt("id");
                String name = jObject.getString("name");
                String description = jObject.getString("description");
                String currency = jObject.getString("currency");
                String image = jObject.getString("food_image");
                double price = jObject.getDouble("price");
                double rate = jObject.getDouble("rate_total");

                Menu food = new Menu(id, name, description,  currency,  image,  rate,  price);
                arrayOfMenu.add(food);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return arrayOfMenu;
    }



    private class Read extends AsyncTask<String , String, String> {

        @Override
        protected String doInBackground(String... urls) {
            //posalji zahtjev
            try {
                URL url = null;
                String response = null;
                //System.out.println("Teest: "+urls[0]);
                url = new URL(urls[0]);

                //create the connection
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.setRequestMethod("GET");

                String line = "";
                //create your inputstream
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                //read in the data from input stream
                BufferedReader reader = new BufferedReader(in);
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                //get the string version of the response data
                response = sb.toString();
                //close input streams
                in.close();

                reader.close();
                return response;
            }
            catch (Exception e) {
                Log.e("HTTP GET:", e.toString());
            }

            return "Error. Please try again later.";
        }
    }
}
