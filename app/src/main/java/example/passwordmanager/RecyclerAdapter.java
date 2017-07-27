package example.passwordmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hassanchowdhury on 04/02/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>
{
    private ArrayList<ListEntry> list;
    private Context ctx;

    public RecyclerAdapter(ArrayList<ListEntry> list, Context ctx)
    {
        this.list = list;
        this.ctx = ctx;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);
        return new RecyclerViewHolder(view, ctx, list);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position)
    {
        ListEntry e = list.get(position);
        holder.logo.setImageResource(e.getImage_id());
        holder.name.setText(e.getName());
        holder.description.setText(e.getDescription());

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
        {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
            {
                menu.add(holder.getAdapterPosition(), 0, 0, "Edit");
                menu.add(holder.getAdapterPosition(), 1, 0, "Delete");
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilter(ArrayList<ListEntry> newList)
    {
        list = new ArrayList<>();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView logo;
        private TextView name, description;
        private ArrayList<ListEntry> list;
        private Context ctx;

        public RecyclerViewHolder(View itemView, Context ctx, ArrayList<ListEntry> list)
        {
            super(itemView);
            this.list = list;
            this.ctx = ctx;

            /*
             * whenever a user clicks on the cardview it will
             * invoke the onClick() method below
             */
            itemView.setOnClickListener(this);

            logo = (ImageView) itemView.findViewById(R.id.generic_category_logo);
            name = (TextView) itemView.findViewById(R.id.entry_name);
            description = (TextView) itemView.findViewById(R.id.entry_description);
        }

        @Override
        public void onClick(View v)
        {
            int position = getAdapterPosition();
            ListEntry e = this.list.get(position);
            Intent i = new Intent(this.ctx, EntryInfoActivity.class);
            i.putExtra("entry", e);

            this.ctx.startActivity(i);
        }
    }
}
