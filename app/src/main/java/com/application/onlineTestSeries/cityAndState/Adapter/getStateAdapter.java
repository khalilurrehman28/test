package com.application.onlineTestSeries.cityAndState.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.application.onlineTestSeries.R;
import com.application.onlineTestSeries.cityAndState.Model.StateData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mandeep on 25/9/17.
 */

public class getStateAdapter extends RecyclerView.Adapter<getStateAdapter.MyViewHolder> implements Filterable {
    public List<StateData> cityList;
    private Context mContext;
    private List<StateData> filterList;
    private StateAdapterListener listener;
    public getStateAdapter(Context mContext, List<StateData> cityList, StateAdapterListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.cityList = cityList;
        this.filterList=cityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item_cities, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        StateData cityState = filterList.get(position);
        holder.mCardView.setTag(position);
        holder.stateName.setText(cityState.getSTATENAME());
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    //RETURN FILTER OBJ
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterList = cityList;
                } else {
                    List<StateData> filteredList = new ArrayList<>();
                    for (StateData row : cityList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSTATENAME().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    filterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterList = (ArrayList<StateData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface StateAdapterListener {
        void onStateSelected(StateData students);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView stateName;
        public CardView mCardView;

        public MyViewHolder(View view) {
            super(view);

            stateName = (TextView) view.findViewById(R.id.stateName);
            mCardView = (CardView) view.findViewById(R.id.card_view);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onStateSelected(filterList.get(getAdapterPosition()));
                }
            });

        }

    }
}