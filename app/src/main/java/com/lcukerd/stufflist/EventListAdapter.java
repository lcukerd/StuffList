package com.lcukerd.stufflist;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lcukerd.stufflist.activity.showList;
import com.lcukerd.stufflist.database.DBinteract;
import com.lcukerd.stufflist.database.eventDBcontract;

import java.util.ArrayList;

import static android.graphics.Color.parseColor;

/**
 * Created by Programmer on 15-11-2017.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder>
{
    private static final String tag = EventListAdapter.class.getSimpleName();

    private ArrayList<String> eventList;
    private LayoutInflater inflater;
    private DBinteract interact;
    private Context mContext;
    private String order, colorbar[] = {"#35e544", "#3e35e5", "#e59035", "#e53584"};

    public EventListAdapter(String order, Context context)
    {
        inflater = LayoutInflater.from(context);
        interact = new DBinteract(context);
        this.order = order;
        updateDataSet(order);
        mContext = context;
    }

    @Override
    public long getItemId(int position)
    {
        return super.getItemId(getItemCount() - position - 1);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = inflater.inflate(R.layout.start_element, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position)
    {
        final String name = eventList.get(position);

        holder.eventName.setText(name);
        holder.color.setBackgroundColor(parseColor(colorbar[position % 4]));
        holder.parentLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, showList.class);
                intent.putExtra("Event_Name", name);
                mContext.startActivity(intent);
            }
        });
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                DBinteract interact = new DBinteract(mContext);
                SQLiteDatabase db = new eventDBcontract(mContext).getWritableDatabase();
                Cursor cursor = interact.readinEvent(name, eventDBcontract.ListofItem.columndatetime + " ASC");
                while (cursor.moveToNext())
                {
                    String imageloc = cursor.getString(cursor.getColumnIndex(
                            eventDBcontract.ListofItem.columnFileloc));
                    if (imageloc != null)
                    {
                        if (imageloc.charAt(0) != 'a')
                        {
                            ContentResolver imagefile = mContext.getContentResolver();
                            Log.d("file deletion", String.valueOf(imagefile
                                    .delete(Uri.parse(imageloc), null, null)) +
                                    " " + imageloc);
                        }
                    }
                    Log.d("delete operaiton", String.valueOf(db.delete(eventDBcontract.ListofItem.tableName,
                            eventDBcontract.ListofItem.columnID + " = " +
                                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnID)),
                            null)));
                }
                updateDataSet(order);
                return true;
            }
        });
    }

    public void updateDataSet(String order)
    {
        ArrayList<String> tempList = new ArrayList<>();
        if (eventList != null)
            tempList.addAll(eventList);
        eventList = interact.readfromDB(updateorder(order));
        if (eventList != null)
        {
            int length = eventList.size() > tempList.size() ? eventList.size() : tempList.size();
            if (!this.order.equals(order))
                notifyDataSetChanged();
            else
            {
                int index = 0;
                try
                {
                    for (int i = 0; i < length; i++)
                    {
                        if (!tempList.get(i).equals(eventList.get(i)))
                        {
                            index = i;
                            break;
                        }
                    }
                    Log.d(tag, "Index is " + String.valueOf(index));
                    if (tempList.size() > eventList.size())
                        notifyItemRemoved(index);
                    else if (eventList.size() > tempList.size())
                        notifyItemInserted(index);
                } catch (IndexOutOfBoundsException e)
                {
                    Log.e(tag, "Index out of bound");
                    if (tempList.size() > eventList.size())
                        notifyItemRemoved(length - 1);
                }
            }
        }
    }

    private String updateorder(String order)
    {
        if (order.equals("date_asc_event"))
            order = eventDBcontract.ListofItem.columndatetime + " ASC";
        else if (order.equals("date_desc_event"))
            order = eventDBcontract.ListofItem.columndatetime + " DESC";
        else if (order.equals("name_asc_event"))
            order = eventDBcontract.ListofItem.columnEvent + " ASC";
        else if (order.equals("name_desc_event"))
            order = eventDBcontract.ListofItem.columnEvent + " DESC";
        return order;
    }

    @Override
    public int getItemCount()
    {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder
    {
        ImageView color;
        TextView eventName;
        LinearLayout parentLayout;

        public EventViewHolder(View itemView)
        {
            super(itemView);
            color = (ImageView) itemView.findViewById(R.id.color_start);
            eventName = (TextView) itemView.findViewById(R.id.event_start);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.linearRecycleStart);
            getItemCount();
        }
    }
}

