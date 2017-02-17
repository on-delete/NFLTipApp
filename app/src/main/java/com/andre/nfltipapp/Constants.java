package com.andre.nfltipapp;

import com.andre.nfltipapp.model.TeamInfo;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String BASE_URL = "http://rocciberge.de:3000/";

    public static final String TAG = "nfltipapp";

    public static final String SUCCESS = "success";
    public static final String FAILURE = TAG + "failure";
    public static final String USERNAME_FREE = "username unused";
    public static final String LOGIN_SUCCESSFULL = "login successfull";
    public static final String GET_DATA_SUCCESSFULL = "data successfull";

    public static final String UUID = "uuid";
    public static final String NAME = "name";
    public static final String DATA = "data";
    public static final String PREDICTIONLIST = "predictionlist";
    public static final String PREDICTIONSPLUSLIST = "predictionspluslist";
    public static final String GAME = "game";
    public static final String TEAMNAME = "teamname";
    public static final String STATE = "state";
    public static final String AFC_STANDINGS = "afcstandings";
    public static final String NFC_STANDINGS = "nfcstandings";

    public static final String[] TAB_NAME_LIST = {"Ranking", "Prognosen", "Statistik", "Tabelle"};

    public static final Map<String, String> WEEK_TYPE_MAP = new HashMap<String, String>() {{
        put("REG", "Regular Season");
        put("WC", "Wildcard");
        put("DIV", "Divisional Playoffs");
        put("CON", "Conference Championships");
        put("SB", "Super Bowl");
    }};

    public static final Map<String, TeamInfo> TEAM_INFO_MAP = new HashMap<String, TeamInfo>() {{
        put("NE", new TeamInfo("New England", "Patriots", R.drawable.ne, "#022856"));
        put("MIA", new TeamInfo("Miami", "Dolphins", R.drawable.mia, "#028992"));
        put("BUF", new TeamInfo("Buffalo", "Bills", R.drawable.buf, "#024F8A"));
        put("NYJ", new TeamInfo("New York", "Jets", R.drawable.nyj, "#263D35"));
        put("PIT", new TeamInfo("Pittsburgh", "Steelers", R.drawable.pit, "#2A2A2A"));
        put("BAL", new TeamInfo("Baltimore", "Ravens", R.drawable.bal, "#2D2F8D"));
        put("CIN", new TeamInfo("Cincinnati", "Bengals", R.drawable.cin, "#DB4822"));
        put("CLE", new TeamInfo("Cleveland", "Browns", R.drawable.cle, "#FE3D02"));
        put("HOU", new TeamInfo("Houston", "Texans", R.drawable.hou, "#C4253F"));
        put("TEN", new TeamInfo("Tennessee", "Titans", R.drawable.ten, "#C21F26"));
        put("IND", new TeamInfo("Indianapolis", "Colts", R.drawable.ind, "#D7D7D7"));
        put("JAX", new TeamInfo("Jacksonville", "Jaguars", R.drawable.jax, "#8E6C29"));
        put("OAK", new TeamInfo("Oakland", "Raiders", R.drawable.oak, "#1E1E1E"));
        put("KC", new TeamInfo("Kansas City", "Chiefs", R.drawable.kc, "#CF1933"));
        put("DEN", new TeamInfo("Denver", "Broncos", R.drawable.den, "#022858"));
        put("SD", new TeamInfo("San Diego", "Chargers", R.drawable.sd, "#022651"));
        put("DAL", new TeamInfo("Dallas", "Cowboys", R.drawable.dal, "#A5ACB0"));
        put("NYG", new TeamInfo("New York", "Giants", R.drawable.nyg, "#023B79"));
        put("WAS", new TeamInfo("Washington", "Redskins", R.drawable.was, "#5A1911"));
        put("PHI", new TeamInfo("Philadelphia", "Eagles", R.drawable.phi, "#02464E"));
        put("GB", new TeamInfo("Green Bay", "Packers", R.drawable.gb, "#E9B10F"));
        put("DET", new TeamInfo("Detroit", "Lions", R.drawable.det, "#888E92"));
        put("MIN", new TeamInfo("Minnesota", "Vikings", R.drawable.min, "#4A257B"));
        put("CHI", new TeamInfo("Chicago", "Bears", R.drawable.chi, "#02133C"));
        put("ATL", new TeamInfo("Atlanta", "Falcons", R.drawable.atl, "#B22239"));
        put("TB", new TeamInfo("Tampa Bay", "Buccaneers", R.drawable.tb, "#E60F0F"));
        put("NO", new TeamInfo("New Orleans", "Saints", R.drawable.no, "#1B1B1B"));
        put("CAR", new TeamInfo("Carolina", "Panthers", R.drawable.car, "#1E1E1E"));
        put("SEA", new TeamInfo("Seattle", "Seahawks", R.drawable.sea, "#022959"));
        put("ARI", new TeamInfo("Arizona", "Cardinals", R.drawable.ari, "#A10736"));
        put("LA", new TeamInfo("Los Angeles", "Rams", R.drawable.la, "#022754"));
        put("SF", new TeamInfo("San Francisco", "49ers", R.drawable.sf, "#B79C6D"));
    }};

    public enum UPDATE_STATES {
        HOME_TEAM_SELECTED,
        AWAY_TEAM_SELECTED,
        UNPREDICTED
    }

    public enum PREDICTIONS_PLUS_STATES {
        SUPERBOWL,
        AFC_WINNER,
        NFC_WINNER,
        BEST_OFFENSE,
        BEST_DEFENSE
    }
}
