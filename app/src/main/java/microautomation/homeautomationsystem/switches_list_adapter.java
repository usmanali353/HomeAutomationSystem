package microautomation.homeautomationsystem;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class switches_list_adapter extends BaseAdapter {
    ArrayList<switch_model> switches_list;
    ArrayList<String> keys;
    DatabaseReference rootref;
    SharedPreferences prefs;
    Context context;
    String device_name;
    public switches_list_adapter(ArrayList<switch_model> switches_list, Context context,ArrayList<String> keys,String device_name) {
        this.switches_list = switches_list;
        this.context=context;
        this.keys=keys;
        this.device_name=device_name;
        rootref=FirebaseDatabase.getInstance().getReference("Devices");
        prefs=PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int getCount() {
        return switches_list.size();
    }

    @Override
    public Object getItem(int position) {
        return switches_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        switch_list_viewholder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.switch_item_layout, parent, false);
            holder=new switch_list_viewholder();
             holder.switch_name=convertView.findViewById(R.id.switch_name);
             holder.button=convertView.findViewById(R.id.switch_toggle);
             convertView.setTag(holder);
        }else{
            holder=(switch_list_viewholder)convertView.getTag();
        }
        holder.switch_name.setText(switches_list.get(position).switch_name);
        if(switches_list.get(position).switch_state==1){
           holder.button.setChecked(true);
        }else{
            holder.button.setChecked(false);
        }
        holder.button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                   rootref.child(device_name).child(keys.get(position)).child("switch_state").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {

                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                       }
                   });
                }else if(!buttonView.isChecked()){
                    rootref.child(device_name).child(keys.get(position)).child("switch_state").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        return convertView;
    }
    class switch_list_viewholder{
        TextView switch_name;
        ToggleButton button;
    }
}
