package com.lucasjwilber.mapchat;

// thanks to:
// https://inducesmile.com/android-programming/how-to-create-custom-infowindow-with-google-map-marker-in-android/

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CommentWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public CommentWindowAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public View getInfoWindow(Marker marker) {
//        return null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view =  inflater.inflate(R.layout.comment_info_window, null);
        TextView commentTV = view.findViewById(R.id.commentWindowTV);

        StringBuilder commentHTML = new StringBuilder();
        commentHTML.append("<h5>" + marker.getTitle() + "</h5><br>");
        commentHTML.append("<p>" + marker.getSnippet() + "</p>");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            commentTV.setText(Html.fromHtml(commentHTML.toString(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            commentTV.setText(Html.fromHtml(commentHTML.toString()));
        }
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

}
