package br.com.felipeacerbi.scoreboard.adapters;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.felipeacerbi.scoreboard.R;
import br.com.felipeacerbi.scoreboard.activities.AddPlayerActivity;
import br.com.felipeacerbi.scoreboard.activities.NewGameActivity;
import br.com.felipeacerbi.scoreboard.db.PlayerDAO;
import br.com.felipeacerbi.scoreboard.fragments.CurrentMatchFragment;
import br.com.felipeacerbi.scoreboard.models.Player;
import br.com.felipeacerbi.scoreboard.tasks.SelectPlayersTask;

/**
 * Created by felipe.acerbi on 08/07/2014.
 */
public class NewGamePlayersAdapter extends BaseAdapter {

    private Activity activity;
    private List<Player> players;
    private ViewHolder temp;
    private boolean isNew;

    public NewGamePlayersAdapter(Activity activity, List<Player> players, boolean isNew) {
        this.players = players;
        this.activity = activity;
        this.isNew = isNew;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int id) {
        return players.get(id);
    }

    @Override
    public long getItemId(int pos) {
        return players.indexOf(pos);
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {

        ViewHolder vh;
        Player player;

        if(view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.activity_new_game_player, viewGroup, false);
            vh = new ViewHolder();
        } else {
            vh = (ViewHolder) view.getTag();
        }

        player = players.get(pos);

        vh.playerTitle = (TextView) view.findViewById(R.id.select_players_text);
        vh.playerType = (Spinner) view.findViewById(R.id.add_player_type);
        vh.playerName = (TextView) view.findViewById(R.id.add_player_name);
        vh.nameShadow = (ImageView) view.findViewById(R.id.name_shadow);
        vh.nameShadowRight = (ImageView) view.findViewById(R.id.name_shadow_right);
        vh.viewPosition = pos;
        vh.playerTitle.setText("Player " + (vh.viewPosition + 1));
        vh.player = player;

        view.setTag(vh);
        vh.playerType.setTag(vh);
        vh.playerName.setTag(vh);
        vh.playerName.setSelected(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.player_types, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        vh.playerType.setAdapter(adapter);

        vh.playerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                ViewHolder vh = (ViewHolder) adapterView.getTag();
                switch (pos) {
                    case 0:
                        vh.nameShadow.setVisibility(View.INVISIBLE);
                        vh.nameShadowRight.setVisibility(View.INVISIBLE);

                        vh.playerName.setText(vh.playerTitle.getText());
                        addNewPlayer(vh);
                        vh.playerName.setBackgroundColor(activity.getResources().getColor(R.color.default_color));
                        vh.playerName.setOnClickListener(null);

                        isNew = true;
                        break;
                    case 1:
                        vh.nameShadow.setVisibility(View.INVISIBLE);
                        vh.nameShadowRight.setVisibility(View.INVISIBLE);

                        setTemp(vh);

                        Intent newPlayer = new Intent(activity, AddPlayerActivity.class);
                        activity.startActivityForResult(newPlayer, NewGameActivity.NEW_PLAYER);

                        vh.playerName.setBackgroundColor(activity.getResources().getColor(R.color.default_color));
                        vh.playerName.setOnClickListener(null);

                        isNew = true;
                        break;
                    case 2:
                        vh.nameShadow.setVisibility(View.VISIBLE);
                        vh.nameShadowRight.setVisibility(View.VISIBLE);

                        if(!isNew) {
                            vh.playerName.setText(vh.player.getName());
                        } else {
                            pickPlayer(vh);
                        }
                        vh.playerName.setBackgroundResource(R.drawable.player_button_style);
                        vh.playerName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pickPlayer((ViewHolder) view.getTag());
                            }
                        });
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //ViewHolder vh = (ViewHolder) adapterView.getTag();
                //vh.playerType.setSelection(0);
                //addNewPlayer(vh);
            }
        });


        if(!isNew) {
            vh.playerType.setSelection(2);
        }

        return view;
    }

    public void pickPlayer(ViewHolder vh) {
        setTemp(vh);
        new SelectPlayersTask(((NewGameActivity) activity), NewGamePlayersAdapter.this).execute();
    }

    public void getPlayerNameDialog(final ViewHolder vh, final PlayersSelectListAdapter adapter) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View convertView = inflater.inflate(R.layout.players_select_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Select Player");

        final ListView lv = (ListView) convertView.findViewById(R.id.players_select_listview);
        lv.setEmptyView(convertView.findViewById(R.id.empty_select_text));
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                vh.player = adapter.getPlayers().get(pos);
                players.set(vh.viewPosition, vh.player);
                view.setSelected(true);
            }
        });

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                vh.playerName.setText(vh.player.getName());
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                vh.playerType.setSelection(0);
                addNewPlayer(vh);
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                vh.playerType.setSelection(0);
                addNewPlayer(vh);
            }
        });
        alertDialog.show();

    }

    public void setNewPlayer(Player player) {
        ViewHolder vh = getTemp();
        vh.player = player;
        players.set(vh.viewPosition, vh.player);
        vh.playerName.setText(vh.player.getName());
    }

    public boolean isNew() {
        return isNew;
    }

    public ViewHolder getTemp() {
        return temp;
    }

    public void setTemp(ViewHolder temp) {
        this.temp = temp;
    }

    public void showMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public void addNewPlayer(ViewHolder vh) {

        Player player = new Player();
        player.setName(vh.playerTitle.getText().toString());
        vh.player = player;
        players.set(vh.viewPosition, vh.player);

    }

    private class ViewHolder {
        TextView playerTitle;
        Spinner playerType;
        TextView playerName;
        ImageView nameShadow;
        ImageView nameShadowRight;
        int viewPosition;
        Player player;
    }
}
