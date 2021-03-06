package br.com.felipeacerbi.scoreboard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.felipeacerbi.scoreboard.models.Competitor;
import br.com.felipeacerbi.scoreboard.models.Player;
import br.com.felipeacerbi.scoreboard.models.Score;

/**
 * Created by Felipe on 05/07/2014.
 */
public class CompetitorDAO extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String TABLE_COMPETITORS = "Competitors";
    private static final String DATABASE = "ScoreBoardDB";
    private Context context;

    public CompetitorDAO(Context context) {

        super(context, DATABASE, null, VERSION);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sldb) {

       String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_COMPETITORS
                + "(gameId INTEGER, "
                + "playerId INTEGER);";

        sldb.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sldb, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABLE_COMPETITORS;

        sldb.execSQL(sql);

        onCreate(sldb);

    }

    public void insertCompetitor(Competitor competitor) {

        ContentValues cv = new ContentValues();

        cv.put("gameId", competitor.getGameId());
        cv.put("playerId", competitor.getPlayerId());

        getWritableDatabase().insert(TABLE_COMPETITORS, null, cv);
    }

    public void deleteCompetitors(long gameId) {

        String[] args = { String.valueOf(gameId) };

        getWritableDatabase().delete(TABLE_COMPETITORS, "gameId=?", args);

    }

    public List<Competitor> listCompetitors(long gameId) {

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_COMPETITORS + " WHERE gameId=" + gameId + ";", null);

        List<Competitor> competitors = new ArrayList<Competitor>();

        if (c.moveToFirst()) {

            do {

                Competitor competitor = new Competitor();

                competitor.setGameId(c.getLong(c.getColumnIndex("gameId")));
                competitor.setPlayerId(c.getInt(c.getColumnIndex("playerId")));

                competitors.add(competitor);

            } while (c.moveToNext());

        }

        return competitors;

    }

    public boolean isPlayerCompeting(Player player) {

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_COMPETITORS + " WHERE playerId=" + player.getId() + ";", null);

        if (c.moveToFirst()) {
            return true;
        }

        return false;

    }
}
