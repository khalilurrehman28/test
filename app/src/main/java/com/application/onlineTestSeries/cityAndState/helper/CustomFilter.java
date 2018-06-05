package com.application.onlineTestSeries.cityAndState.helper;

import android.widget.Filter;

import com.application.onlineTestSeries.cityAndState.Adapter.getStateAdapter;
import com.application.onlineTestSeries.cityAndState.Model.StateData;

import java.util.ArrayList;

/**
 * Created by Hp on 3/17/2016.
 */
public class CustomFilter extends Filter {

    getStateAdapter adapter;
    ArrayList<StateData> filterList;


    public CustomFilter(ArrayList<StateData> filterList, getStateAdapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<StateData> filteredPlayers=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(filterList.get(i).getSTATENAME().toUpperCase().contains(constraint))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterList.get(i));
                }
            }

            results.count=filteredPlayers.size();
            results.values=filteredPlayers;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;

        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

       adapter.cityList= (ArrayList<StateData>) results.values;

        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
