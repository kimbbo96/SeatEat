package com.example.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.db_obj.Food;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Instances of this class are fragments representing a single
// object in our collection.
public class FoodObjectFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_food_rest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // prendi la lista di cibi dall'aargomento e popola la ListView contenuta nel fragment
        Bundle args = getArguments();
        String restID = args.getString("restID");
        List<String> serializedFoods = args.getStringArrayList("foods");
        List<Food> foods = new ArrayList<>();

        for (String serializedFood : serializedFoods) {
            try {
                JSONObject jsonFood = new JSONObject(serializedFood);
                String id = jsonFood.getString("id");
                String name = jsonFood.getString("nome");
                String shordDescr = jsonFood.getString("descrBreve");
                String longDescr = jsonFood.getString("descrLunga");
                String dish = jsonFood.getString("portata");
                double price = jsonFood.getDouble("costo");
                String image = jsonFood.getString("immagine");

                Food food = new Food(id, name, shordDescr, longDescr, dish, price, image);
                foods.add(food);
            }  catch (JSONException err){
                Log.d("Error", err.toString());
                err.printStackTrace();
            }
        }

        ListView listView = view.findViewById(R.id.list_view_food);
        System.out.println(listView);

        FoodListView customListView = new FoodListView(getActivity(), foods.toArray(new Food[0]), restID);

        listView.setAdapter(customListView);

        listView.setOnItemClickListener((adapterView, v, i, l) -> {
            System.out.println("hai clikkato "+i);

            /*Intent intent = new Intent(FoodRest.this,ResDetail.class);
            intent.putExtra("Restaurant",resList.get(i)); // passo l'oggetto ristornate
            startActivity(intent);*/
        });

    }
}
