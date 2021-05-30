package com.NobodyKnows.chatlayoutview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.NobodyKnows.chatlayoutview.Interfaces.ProgressClickListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class ProgressButton extends RelativeLayout {
    private LayoutInflater layoutInflater;
    private RelativeLayout root;
    private TextView label;
    private ImageView icon;
    private Boolean mode = false;
    private ImageView close;
    private CircularProgressBar progressBar;
    private ProgressClickListener progressClickListener;
    public ProgressButton(Context context) {
        super(context);
        init(null,0);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    private void init(AttributeSet attrs,int defStyleAttr) {
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = (RelativeLayout) layoutInflater.inflate(R.layout.progressbutton,this,true);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.ProgressButton);
        mode = typedArray.getBoolean(R.styleable.ProgressButton_icon_mode,false);
        label = root.findViewById(R.id.textlabel);
        RelativeLayout button = root.findViewById(R.id.button);
        if(mode) {
            button.setBackgroundResource(0);
            label.setVisibility(GONE);
        }

    }

    public void initalize() {
        RelativeLayout button = root.findViewById(R.id.button);
        label = root.findViewById(R.id.textlabel);
        icon = root.findViewById(R.id.icon);
        progressBar = root.findViewById(R.id.progressbar);
        close = root.findViewById(R.id.closeicon);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mode) {
                    if(label.getVisibility() == GONE) {
                        label.setVisibility(VISIBLE);
                        icon.setVisibility(VISIBLE);
                        progressBar.setVisibility(GONE);
                        close.setVisibility(GONE);
                        progressClickListener.onCancel();
                    } else {
                        label.setVisibility(GONE);
                        icon.setVisibility(GONE);
                        progressBar.setVisibility(VISIBLE);
                        close.setVisibility(VISIBLE);
                        progressClickListener.onStart();
                    }
                } else {
                    if(icon.getVisibility() == GONE) {
                        icon.setVisibility(VISIBLE);
                        progressBar.setVisibility(GONE);
                        close.setVisibility(GONE);
                        progressClickListener.onCancel();
                    } else {
                        icon.setVisibility(GONE);
                        progressBar.setVisibility(VISIBLE);
                        close.setVisibility(VISIBLE);
                        progressClickListener.onStart();
                    }
                }
            }
        });
    }

    public void resetProgressButton() {
        if(!mode) {
            label.setVisibility(VISIBLE);
            icon.setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);
            close.setVisibility(GONE);
            progressClickListener.onCancel();
        } else {
            icon.setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);
            close.setVisibility(GONE);
            progressClickListener.onCancel();
        }
    }

    public ProgressClickListener getProgressClickListener() {
        return progressClickListener;
    }

    public void setProgressClickListener(ProgressClickListener progressClickListener) {
        this.progressClickListener = progressClickListener;
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public void setUploadType() {
        this.icon.setImageResource(R.drawable.upload);
        this.label.setText("Upload");
    }

    public void setDownloadType() {
        this.icon.setImageResource(R.drawable.download);
        this.label.setText("Download");
    }

    public CircularProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgress(float value) {
        label.setVisibility(GONE);
        icon.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        close.setVisibility(VISIBLE);
        progressBar.setProgress(value);
    }

    public float getProgress() {
        return progressBar.getProgress();
    }

    public Boolean getIndeterminateMode() {
        return this.progressBar.getIndeterminateMode();
    }

    public void setIndeterminateMode(Boolean indeterminateMode) {
        this.progressBar.setIndeterminateMode(indeterminateMode);
    }
}
