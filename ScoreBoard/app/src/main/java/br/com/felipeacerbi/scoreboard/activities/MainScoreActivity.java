package br.com.felipeacerbi.scoreboard.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import br.com.felipeacerbi.scoreboard.fragments.CurrentMatchFragment;
import br.com.felipeacerbi.scoreboard.models.Game;
import br.com.felipeacerbi.scoreboard.fragments.NavigationDrawerFragment;
import br.com.felipeacerbi.scoreboard.fragments.PlayersFragment;
import br.com.felipeacerbi.scoreboard.R;


public class MainScoreActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private Game game;

    private NavigationDrawerFragment navDrawerFragment;
    private CharSequence title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        navDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        navDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navDrawerFragment.isDrawerOpen()) {
            if(title.equals(getString(R.string.title_section1))) {
                getMenuInflater().inflate(R.menu.main_score, menu);
                restoreActionBar();
            } else if(title.equals(getString(R.string.title_section3))) {
                getMenuInflater().inflate(R.menu.players, menu);
                restoreActionBar();
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return false;
            case R.id.action_add_player:
                return false;
        }

        return false;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, CurrentMatchFragment.newInstance(position + 1))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlayersFragment.newInstance(position + 1))
                        .commit();
                break;
        }


    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                title = getString(R.string.title_section1);
                break;
            case 2:
                title = getString(R.string.title_section2);
                break;
            case 3:
                title = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
    }
}
