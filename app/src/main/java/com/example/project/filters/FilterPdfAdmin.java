package com.example.project.filters;

import android.widget.Filter;

import com.example.project.adapters.AdapterCategory;
import com.example.project.adapters.AdapterPdfAdmin;
import com.example.project.models.ModelCategory;
import com.example.project.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter{
    ArrayList<ModelPdf>filterList;
    AdapterPdfAdmin adapterPdfAdmin;

    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterList = filterList;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint != null && constraint.length()>0){
            constraint = constraint.toString().toLowerCase();
            ArrayList<ModelPdf>filteredModels = new ArrayList<>();

            for (int i=0;i<filterList.size();i++){
                if (filterList.get(i).getTitle().toUpperCase().contains( constraint )){
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterPdfAdmin.pdfArrayList =(ArrayList<ModelPdf>)results.values;

        adapterPdfAdmin.notifyDataSetChanged();
    }
}
