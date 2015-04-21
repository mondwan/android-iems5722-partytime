package com.iems5722.partytime;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * An adapter handles how to fetch data from GameClient and render them as a row in ListView
 */
public class PlayerItemAdapter extends ArrayAdapter<GamePlayer> {
    public PlayerItemAdapter(Context context, int resource, List<GamePlayer> players) {
        super(context, resource, players);
    }

    /**
     * Override default getView method so that it can publish our contents into our custom view
     *
     * @param pos         int. Position of the item inside the adapter's data.
     * @param convertView View. The old view to reuse if possible
     * @param parent      ViewGroup. The parent that this view will eventually be attached to
     * @return View
     */
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        // Checkout whether the old view is available or not
        if (convertView == null) {
            Context c = this.getContext();
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
            );
            convertView = inflater.inflate(R.layout.player_row_view, parent, false);
        }

        // Get the GameClient reference
        GamePlayer player = this.getItem(pos);

        // Get View Reference
        TextView playerIP = (TextView) convertView.findViewById(
                R.id.playerIP
        );

        TextView playerName = (TextView) convertView.findViewById(
                R.id.playerName
        );

        ImageView icon = (ImageView) convertView.findViewById(
                R.id.icon
        );

        // Get the GameController reference
        GameController gameController = GameController.getInstance();

        // Get icon image resource
        int imageResourceID = gameController.getPlayerIconResource(player.getUsername());

        // Get local player position
        int localPlayerPosition = gameController.getLocalPlayerPosition();

        // Fill in data
        playerIP.setText(player.getIp());
        playerName.setText(player.getUsername());
        icon.setImageResource(imageResourceID);

        // Highlight local player row
        if (localPlayerPosition == pos) {
            Resources color = convertView.getResources();
            convertView.setBackgroundColor(
                    color.getColor(
                            R.color.LocalPlayerHighlight
                    )
            );
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }
}
