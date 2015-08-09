package project.wardenclyffe.Hub;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

/**
 * Created by Andre on 11/06/15.
 */

public class RecycleView_Adapter extends RecyclerView.Adapter<RecycleView_Adapter.View_Holder> {
    private LayoutInflater layoutInflater;
    private Context context;

    List<RecycleView_Element> data = Collections.emptyList();

    public RecycleView_Adapter(Context context, List<RecycleView_Element> data) {
        layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.recycle_view_row, viewGroup, false);
        View_Holder holder = new View_Holder(view);

        return holder;
    }

    //Populate our view
    @Override
    public void onBindViewHolder(final View_Holder viewHolder, int i) {
        final RecycleView_Element current = data.get(i);
        viewHolder.title.setText(current.Device_Name);
        viewHolder.image.setImageResource(current.Device_Type);

    }

    @Override
    public int getItemCount() {

        return data.size();
    }


    class View_Holder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        public View_Holder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.RecycleView_Text);
            image = (ImageView) itemView.findViewById(R.id.RecycleView_Image);

        }
    }
}
