package in.co.mdg.campusBuddy.contacts;


import android.content.Intent;


import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import in.co.mdg.campusBuddy.R;
import in.co.mdg.campusBuddy.contacts.data_models.Contact;
import in.co.mdg.campusBuddy.contacts.data_models.Department;
import io.realm.Realm;

public class ShowContact extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private int maxScrollSize;
    private ImageView profilePic;
    private String std_code_res_off = "01332 28 "; // std code for roorkee
    private String std_code_bsnl = "01332 "; //std code for roorkee
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.8f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.2f;
    private static final int ALPHA_ANIMATIONS_DURATION = 300;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private Toolbar toolbar;
    private NestedScrollView nestedScrollView;
    private AppBarLayout mAppBarLayout;
    private TextView name_text;
    private TextView dept_text;
    private TextView desg_text;
    private ImageView profileBackdrop;
    private String name;
    private String dept;

    private Contact contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);
        initializeVariables();
        setData();
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(this);
        toolbar.getBackground().setAlpha(0);

    }

    private void initializeVariables() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        name_text = (TextView) findViewById(R.id.name);
        dept_text = (TextView) findViewById(R.id.dept);
        desg_text = (TextView) findViewById(R.id.desg);
        mTitle = (TextView) findViewById(R.id.action_bar_title);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        mTitleContainer = (LinearLayout) findViewById(R.id.linearlayout_title);
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        profileBackdrop  = (ImageView) findViewById(R.id.profile_backdrop);

        maxScrollSize = mAppBarLayout.getTotalScrollRange();
        name = getIntent().getStringExtra("name");
        dept = getIntent().getStringExtra("dept");
        Realm realm = Realm.getDefaultInstance();
        contact = realm
                .where(Department.class)
                .equalTo("name", dept)
                .findFirst()
                .getContacts()
                .where()
                .equalTo("name", name)
                .findFirst();
    }

    private void setData()
    {
        name_text.setText(name);
        dept_text.setText(dept);
        mTitle.setText(name);

        Picasso.with(this).load("http://timesofindia.indiatimes.com/photo/48010944.cms").into(profileBackdrop, new Callback() {
            @Override
            public void onSuccess() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    getWindow().setStatusBarColor(Color.TRANSPARENT);
//                }
            }
            @Override
            public void onError() {}
        });

        if(contact.getDesignation() != null)
            desg_text.setText(contact.getDesignation());
        else
            desg_text.setVisibility(View.GONE);

        if (contact.getProfilePic() != null) {
            if (!contact.getProfilePic().equals("") && !contact.getProfilePic().equals("default.jpg")) {
                Picasso.with(this)
                        .load("http://people.iitr.ernet.in/facultyphoto/" + contact.getProfilePic())
                        .noFade()
                        .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .into(profilePic);
            }
        }
        if (dept.equals("Polymer & Paper Pulp")) {
            std_code_res_off = "0132 271 ";
            std_code_bsnl = "0132 ";
        }

        LinearLayout contactOffice = (LinearLayout) findViewById(R.id.contact_office);
        if (contact.getIitr_o() != null) {
            final TextView iitr_o = (TextView) findViewById(R.id.iitr_o);
            iitr_o.setText(std_code_res_off + contact.getIitr_o());
            contactOffice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + iitr_o.getText().toString().replace(" ", "")));
                    startActivity(intent);
                }
            });
        } else {
            findViewById(R.id.divider2).setVisibility(View.GONE);
            contactOffice.setVisibility(View.GONE);
        }

        LinearLayout contactResidence = (LinearLayout) findViewById(R.id.contact_residence);
        if (contact.getIitr_r() != null) {
            final TextView iitr_r = (TextView) findViewById(R.id.iitr_r);
            iitr_r.setText(std_code_res_off + contact.getIitr_r());
            contactResidence.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + iitr_r.getText().toString().replace(" ", "")));
                    startActivity(intent);
                }
            });
        } else {
            findViewById(R.id.divider3).setVisibility(View.GONE);
            contactResidence.setVisibility(View.GONE);
        }

        LinearLayout contactBsnl = (LinearLayout) findViewById(R.id.contact_bsnl);
        if (contact.getPhoneBSNL() != null) {
            final TextView phoneBsnl = (TextView) findViewById(R.id.phone_bsnl);
            if (contact.getPhoneBSNL().startsWith("9") || contact.getPhoneBSNL().startsWith("8")) {
                final TextView bsnlText = (TextView) findViewById(R.id.bsnl_text);
                bsnlText.setText("Mobile");
                phoneBsnl.setText(contact.getPhoneBSNL());
            } else {
                phoneBsnl.setText(std_code_bsnl + contact.getPhoneBSNL());
            }
            contactBsnl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneBsnl.getText().toString().replace(" ", "")));
                    startActivity(intent);
                }
            });
        } else {
            findViewById(R.id.divider4).setVisibility(View.GONE);
            contactBsnl.setVisibility(View.GONE);
        }

        LinearLayout contactEmail = (LinearLayout) findViewById(R.id.contact_email);
        if (contact.getEmail() != null) {
            final TextView email = (TextView) findViewById(R.id.email);
            email.setText(contact.getEmail() + "@iitr.ac.in");
            contactEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("plain/text");
                    intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email.getText().toString()});
                    startActivity(intent);
                }
            });
        } else {
            findViewById(R.id.divider5).setVisibility(View.GONE);
            contactEmail.setVisibility(View.GONE);
        }

        LinearLayout contactWebsite = (LinearLayout) findViewById(R.id.contact_website);
        if (contact.getWebsite() != null) {
            final TextView website = (TextView) findViewById(R.id.website_link);
            website.setText(contact.getWebsite());
            contactWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website.getText().toString()));
                    startActivity(browserIntent);
                }
            });

        } else {
            findViewById(R.id.divider6).setVisibility(View.GONE);
            contactWebsite.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        if(maxScrollSize == 0)
            maxScrollSize = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScrollSize;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {

        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            float modifiedPercent = getModifiedPercent(percentage,PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR);
            toolbar.getBackground().setAlpha((int)(255*modifiedPercent));
            nestedScrollView.setPadding(0,(int)(toolbar.getMeasuredHeight()*modifiedPercent),0,0);
            if(!mIsTheTitleVisible) {
//                mAppBarLayout.setFitsSystemWindows(false);
//                profilePic.setFitsSystemWindows(false);
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.brand_primary_dark));
//                }
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                toolbar.getBackground().setAlpha(0);
//                mAppBarLayout.setFitsSystemWindows(true);
//                profilePic.setFitsSystemWindows(true);
                nestedScrollView.setPadding(0,0,0,0);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    getWindow().setStatusBarColor(Color.TRANSPARENT);
//                }
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    private static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private float getModifiedPercent(float percentage,float leastValue)
    {
        return (percentage - leastValue)/(1f-leastValue);
    }

}