package com.example.project.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.MyApplication;
import com.example.project.activities.PdfDetailActivity;
import com.example.project.activities.PdfEditActivity;
import com.example.project.databinding.RowPdfAdminBinding;
import com.example.project.filters.FilterPdfAdmin;
import com.example.project.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;


public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    private Context context;

    public ArrayList<ModelPdf>pdfArrayList,filerList;

    private RowPdfAdminBinding binding;

    private FilterPdfAdmin filter;

    private  static final String TAG = "PDF_ADAPTER_TAG";

    //progress
    private ProgressDialog progressDialog;

    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filerList = pdfArrayList;

        //init progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfAdminBinding.inflate( LayoutInflater.from( context ) ,parent,false);

        return new HolderPdfAdmin( binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        // get data set data handle click etc.

        // get data
        ModelPdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        long timestamp = model.getTimestamp();

        // convert timestamp  to dd/mm/yyyy
        String formattedDate = MyApplication.formatTimestamp( timestamp );

        //set data
        holder.titleTv.setText( title );
        holder.descriptionTv.setText( description );
        holder.dateTv.setText( formattedDate );

        // load further detail like category,pdf form url, pdf size in seprate function
        MyApplication.loadCategory(
                ""+categoryId,
                holder.categoryTv
                );
        MyApplication.loadPdfFromUrlSinglePage(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.progressBar,
                null
        );
      MyApplication.loadPdfsize(
              ""+pdfUrl,
              ""+title,
              holder.sizeTv
      );

        //handle click, show dialog with options 1) Edit, 2) Delete
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moreOptionsDialog(model, holder);
            }
        });
        //handle book/pdf click, open pdf details page,pass pdf/book id to get details of it
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class );
                intent.putExtra( "bookId",pdfId );
                context.startActivity(intent);
            }
        } );
    }

    private void moreOptionsDialog(ModelPdf model, HolderPdfAdmin holder) {

        String bookId = model.getId();
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();

        //options to show in dialog
        String[] options = {"Edit", "Delete"};

        //Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                       //handle  dialog option click
                       if (which == 0){
                           //Edit clicked, Open PdfEditActivity to edit the book info
                           Intent intent = new Intent(context, PdfEditActivity.class);
                           intent.putExtra("bookId", bookId);
                           context.startActivity(intent);

                       }
                       else if (which == 1){
                           //Delete clicked
                           MyApplication.deleteBook( context,""+bookId,""+bookUrl,""+bookTitle );
                           //deleteBook(model,holder);
                       }
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterPdfAdmin( filerList,this );
        }
        return filter;
    }

    //view holder class for row_pdf_admin.xml
    class HolderPdfAdmin extends RecyclerView.ViewHolder{

        //ui view of row_pdf_admin.xml
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,categoryTv,sizeTv,dateTv;
        ImageView moreBtn;


        public HolderPdfAdmin(@NonNull View itemView) {
            super( itemView );

            pdfView = binding.pdfView;
            progressBar = binding.ProgressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            moreBtn =binding.moreBtn;

        }
    }
}
