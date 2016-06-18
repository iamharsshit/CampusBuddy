package in.co.mdg.campusBuddy;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.co.mdg.campusBuddy.contacts.RecyclerViewFastScroller;
import in.co.mdg.campusBuddy.contacts.data_models.Contact;
import in.co.mdg.campusBuddy.contacts.data_models.Department;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by root on 13/6/15.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ContactViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    private Realm realm = Realm.getDefaultInstance();
    private RealmResults<Department> depts = realm.where(Department.class).findAll().sort("name");
    private RealmResults<Contact> contacts = realm.where(Contact.class).isNotNull("profilePic").findAll().sort("name");
    private RealmList<Contact> deptContacts;
    private static MyRecyclerAdapter instance;
    private int type = 1;
    private static ClickListener clicklistener;
    public interface ClickListener {
        void itemClicked(int type,String name);
    }
    public MyRecyclerAdapter() {
    }

    public static synchronized MyRecyclerAdapter getInstance() {
        if (instance == null) {
            instance = new MyRecyclerAdapter();
        }
        return instance;
    }
    public void setListData(int option,String deptName) {
        if(option==3)
        {
            deptContacts = realm.where(Department.class).equalTo("name",deptName).findFirst().getContacts();
        }
        type = option;
        notifyDataSetChanged();
    }
    public void setClicklistener (ClickListener clickListener)
    {
       clicklistener = clickListener;
    }

    void closeRealm() {
        realm.close();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        switch (type)
        {
            case 1:
                holder.bind(type,depts.get(position));
                break;
            case 2:
                holder.bind(type,contacts.get(position));
                break;
            case 3:
                holder.bind(type,deptContacts.get(position));
        }
    }

    @Override
    public int getItemCount() {
        switch(type)
        {
            case 1:
                return depts.size();
            case 2:
                return contacts.size();
            case 3:
                return deptContacts.size();
        }
        return 0;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        switch(type)
        {
            case 1:
                return depts.get(pos).getName().substring(0,1);
            case 2:
                return contacts.get(pos).getName().substring(0,1);
            case 3:
                return deptContacts.get(pos).getName().substring(0,1);
        }
        return "";
    }
    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView profilePic;

        ContactViewHolder(View itemView)
        {
            super(itemView);
            name =(TextView)itemView.findViewById(R.id.name);
            profilePic=(ImageView) itemView.findViewById(R.id.profile_pic);
        }

        void bind(final int type,final Object item) {

            switch(type){
                case 1:
                    Department dept = (Department) item;
                    name.setText(dept.getName());
                    Picasso.with(profilePic.getContext())
                            .load("http://www.iitr.ac.in/departments/" + dept.getPhoto() + "/assets/images/top1.jpg")
                            .noFade()
                            .error(R.drawable.iit_roorkee)
                            .into(profilePic);
                    break;
                case 2:case 3:
                    Contact contact = (Contact) item;
                    name.setText(contact.getName());
                    String picAddress = contact.getProfilePic();
                    if(picAddress == null)
                    {
                        picAddress ="default.jpg";
                    }
                    if (picAddress.equals("") || picAddress.equals("default.jpg")) {
                        profilePic.setImageDrawable(
                                ContextCompat.getDrawable(
                                        profilePic.getContext()
                                        , R.drawable.com_facebook_profile_picture_blank_portrait));
                    } else {
                        Picasso.with(profilePic.getContext())
                                .load("http://people.iitr.ernet.in/facultyphoto/" + picAddress)
                                .noFade()
                                .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                                .into(profilePic);
                    }


                    break;
            }
//            itemView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
//                        profilePic.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).start();
//                    } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
//                        profilePic.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
//                    }
//                    return true;
//                }
//            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicklistener.itemClicked(type,name.getText().toString());
                }
            });
            }
    }
}
