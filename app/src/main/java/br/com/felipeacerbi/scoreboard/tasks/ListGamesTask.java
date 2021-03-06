package br.com.felipeacerbi.scoreboard.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ListView;

import br.com.felipeacerbi.scoreboard.activities.MainScoreActivity;
import br.com.felipeacerbi.scoreboard.adapters.GamesListAdapter;
import br.com.felipeacerbi.scoreboard.adapters.PlayersListAdapter;
import br.com.felipeacerbi.scoreboard.db.GameDAO;
import br.com.felipeacerbi.scoreboard.db.PlayerDAO;

/**
 * Created by felipe.acerbi on 11/07/2014.
 */
public class ListGamesTask extends AsyncTask<Void, Void, GamesListAdapter> {

    private MainScoreActivity msb;
    private ListView list;
    private ProgressDialog pdia;

    public ListGamesTask(MainScoreActivity msb, ListView list) {
        this.msb = msb;
        this.list = list;

        msb.getApp().register(this);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pdia = new ProgressDialog(msb);
        pdia.setMessage("Loading Games...");
        pdia.show();
    }

    @Override
    protected GamesListAdapter doInBackground(Void... voids) {

        GameDAO gdao = new GameDAO(msb);

        GamesListAdapter gla = new GamesListAdapter(msb, gdao.listGames());

        gdao.close();

        return gla;
    }

    @Override
    protected void onPostExecute(GamesListAdapter gamesListAdapter) {

        pdia.dismiss();

        list.setAdapter(gamesListAdapter);

        msb.getApp().unregister(this);
    }
}
