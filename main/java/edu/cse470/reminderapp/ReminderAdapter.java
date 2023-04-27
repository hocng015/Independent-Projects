package edu.cse470.reminderapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> implements Filterable {

    private ArrayList<Reminder> reminderData;
    private ArrayList<Reminder> reminderDataFull;
    private View.OnClickListener myOnItemClickListener;
    private Context parentContext;
    private boolean isDeleting;

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView textReminderTitle;
        public TextView textReminderPreview;
        public TextView textReminderDate;
        public Button deleteButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            textReminderTitle = itemView.findViewById(R.id.textReminderTitle);
            textReminderPreview = itemView.findViewById(R.id.textReminderPreview);
            textReminderDate = itemView.findViewById(R.id.textReminderDate);
            deleteButton = itemView.findViewById(R.id.buttonDeleteReminder);
            itemView.setTag(this);
            itemView.setOnClickListener(myOnItemClickListener);
        }
    }
    private ReminderDataSource reminderDataSource;

    public ReminderAdapter(ArrayList<Reminder> arrayList, Context context) {
        reminderData = arrayList;
        reminderDataFull = new ArrayList<>(arrayList);
        parentContext = context;
        reminderDataSource = new ReminderDataSource(context);
    }


    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ReminderViewHolder rvh = (ReminderViewHolder) holder;
        rvh.textReminderTitle.setText(reminderData.get(position).getTitle());
        rvh.textReminderPreview.setText(reminderData.get(position).getDetails());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        String formattedDateTime = dateFormat.format(reminderData.get(position).getDateTime());
        rvh.textReminderDate.setText(formattedDateTime);
        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parentContext, ReminderActivity.class);
                intent.putExtra("reminderId", reminderData.get(position).getId());
                parentContext.startActivity(intent);
            }
        });
        if (isDeleting) {
            rvh.deleteButton.setVisibility(View.VISIBLE);
            rvh.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItem(position);
                }
            });
        } else {
            rvh.deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public Filter getFilter() {
        return reminderFilter;
    }

    private Filter reminderFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Reminder> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(reminderDataFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Reminder item : reminderDataFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            reminderData.clear();
            reminderData.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return reminderData.size();
    }

    private void deleteItem(int position) {
        Reminder reminderToDelete = reminderData.get(position);

        // Open the database, delete the reminder, and close the database.
        reminderDataSource.open();
        boolean didDelete = reminderDataSource.deleteReminder(reminderToDelete.getId());
        reminderDataSource.close();

        if (didDelete) {
            // Remove the deleted reminder from the list and notify the adapter.
            reminderData.remove(position);
            notifyDataSetChanged();
        } else {
            // Show an error message if the deletion was unsuccessful.
            Toast.makeText(parentContext, "Error deleting reminder", Toast.LENGTH_SHORT).show();
        }
    }
    public void setDelete(boolean b) {
        isDeleting = b;
    }
}

