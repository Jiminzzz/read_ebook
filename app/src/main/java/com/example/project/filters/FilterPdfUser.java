package com.example.project.filters;

import android.widget.Filter;

import com.example.project.adapters.AdapterPdfUser;
import com.example.project.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfUser extends Filter {

    ArrayList<ModelPdf>filterList;

    AdapterPdfUser adapterPdfUser;

    public FilterPdfUser(ArrayList<ModelPdf> filterList, AdapterPdfUser adapterPdfUser) {
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {

        FilterResults results = new FilterResults();

        if(charSequence!=null||charSequence.length()>0){
            charSequence =charSequence.toString().toUpperCase();
            ArrayList<ModelPdf>filterModels = new ArrayList<>();

            for(int i=0; i <filterList.size();i++){
                if(filterList.get(i).getTitle().toLowerCase().contains( charSequence )){
                    filterModels.add( filterList.get( i ));
                }
            }
            results.count = filterModels.size();
            results.values =filterModels;

        }
        else{
            results.count = filterList.size();
            results.values = filterList;

        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {
        //apply filter changes
    adapterPdfUser.pdfArrayList =(ArrayList<ModelPdf>)results.values;

    adapterPdfUser.notifyDataSetChanged();
    }
}
