package com.andre.nfltipapp;

import com.andre.nfltipapp.model.TeamInfo;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String BASE_URL = "https://rocciberge.de:3000/";

    public static final String TAG = "nfltipapp";

    public static final String SUCCESS = "success";
    public static final String FAILURE = TAG + "failure";
    public static final String USERID = "userId";
    public static final String DATA = "data";
    public static final String PREDICTIONS = "PREDICTIONS";
    public static final String PREDICTIONS_BEFORE_SEASON = "predictionsbeforeseason";
    public static final String GAME = "game";
    public static final String TEAMNAME = "teamname";
    public static final String PREDICTION_TYPE_STRING = "predictiontype";
    public static final String AFC_STANDINGS = "afcstandings";
    public static final String NFC_STANDINGS = "nfcstandings";
    public static final String PREDICTION_BEFORE_SEASON = "Zusatztipps";
    public static final String WEEK = "Woche ";
    public static final String SHARED_PREF_FILENAME = "nfltipapp-userprefs";

    public static final String DEFAULT_BACKGROUND_COLOR = "#E6E6E6";
    public static final String DEFAULT_TEAM_BACKGROUND_COLOR = "#BFBFBF";
    public static final String TABLE_SELECTED_TEXT_COLOR = "#FAFAFA";
    public static final String TABLE_DEFAULT_TEXT_COLOR = "#151515";

    public static final String[] TAB_NAME_LIST = {"Ranking", "Prognosen", "Statistik", "Tabelle"};

    public static final Map<String, String> WEEK_TYPE_MAP = new HashMap<String, String>() {{
        put("REG", "Regular Season");
        put("WC", "Wildcard");
        put("DIV", "Divisional Playoffs");
        put("CON", "Conference Championships");
        put("SB", "Super Bowl");
    }};

    public static final Map<String, TeamInfo> TEAM_INFO_MAP = new HashMap<String, TeamInfo>() {{
        put("NE", new TeamInfo("New England", "Patriots", DIVISION.AFC, R.drawable.ic_ne, "#022856"));
        put("MIA", new TeamInfo("Miami", "Dolphins", DIVISION.AFC, R.drawable.ic_mia, "#028992"));
        put("BUF", new TeamInfo("Buffalo", "Bills", DIVISION.AFC, R.drawable.ic_buf, "#024F8A"));
        put("NYJ", new TeamInfo("New York", "Jets", DIVISION.AFC, R.drawable.ic_nyj, "#263D35"));
        put("PIT", new TeamInfo("Pittsburgh", "Steelers", DIVISION.AFC, R.drawable.ic_pit, "#2A2A2A"));
        put("BAL", new TeamInfo("Baltimore", "Ravens", DIVISION.AFC, R.drawable.ic_bal, "#2D2F8D"));
        put("CIN", new TeamInfo("Cincinnati", "Bengals", DIVISION.AFC, R.drawable.ic_cin, "#DB4822"));
        put("CLE", new TeamInfo("Cleveland", "Browns", DIVISION.AFC, R.drawable.ic_cle, "#FE3D02"));
        put("HOU", new TeamInfo("Houston", "Texans", DIVISION.AFC, R.drawable.ic_hou, "#C4253F"));
        put("TEN", new TeamInfo("Tennessee", "Titans", DIVISION.AFC, R.drawable.ic_ten, "#C21F26"));
        put("IND", new TeamInfo("Indianapolis", "Colts", DIVISION.AFC, R.drawable.ic_ind, "#D7D7D7"));
        put("JAX", new TeamInfo("Jacksonville", "Jaguars", DIVISION.AFC, R.drawable.ic_jax, "#8E6C29"));
        put("OAK", new TeamInfo("Oakland", "Raiders", DIVISION.AFC, R.drawable.ic_oak, "#1E1E1E"));
        put("KC", new TeamInfo("Kansas City", "Chiefs", DIVISION.AFC, R.drawable.ic_kc, "#CF1933"));
        put("DEN", new TeamInfo("Denver", "Broncos", DIVISION.AFC, R.drawable.ic_den, "#022858"));
        put("SD", new TeamInfo("San Diego", "Chargers", DIVISION.AFC, R.drawable.ic_sd, "#022651"));
        put("DAL", new TeamInfo("Dallas", "Cowboys", DIVISION.NFC, R.drawable.ic_dal, "#A5ACB0"));
        put("NYG", new TeamInfo("New York", "Giants", DIVISION.NFC, R.drawable.ic_nyg, "#023B79"));
        put("WAS", new TeamInfo("Washington", "Redskins", DIVISION.NFC, R.drawable.ic_was, "#5A1911"));
        put("PHI", new TeamInfo("Philadelphia", "Eagles", DIVISION.NFC, R.drawable.ic_phi, "#02464E"));
        put("GB", new TeamInfo("Green Bay", "Packers", DIVISION.NFC, R.drawable.ic_gb, "#E9B10F"));
        put("DET", new TeamInfo("Detroit", "Lions", DIVISION.NFC, R.drawable.ic_det, "#888E92"));
        put("MIN", new TeamInfo("Minnesota", "Vikings", DIVISION.NFC, R.drawable.ic_min, "#4A257B"));
        put("CHI", new TeamInfo("Chicago", "Bears", DIVISION.NFC, R.drawable.ic_chi, "#02133C"));
        put("ATL", new TeamInfo("Atlanta", "Falcons", DIVISION.NFC, R.drawable.ic_atl, "#B22239"));
        put("TB", new TeamInfo("Tampa Bay", "Buccaneers", DIVISION.NFC, R.drawable.ic_tb, "#E60F0F"));
        put("NO", new TeamInfo("New Orleans", "Saints", DIVISION.NFC, R.drawable.ic_no, "#1B1B1B"));
        put("CAR", new TeamInfo("Carolina", "Panthers", DIVISION.NFC, R.drawable.ic_car, "#1E1E1E"));
        put("SEA", new TeamInfo("Seattle", "Seahawks", DIVISION.NFC, R.drawable.ic_sea, "#022959"));
        put("ARI", new TeamInfo("Arizona", "Cardinals", DIVISION.NFC, R.drawable.ic_ari, "#A10736"));
        put("LA", new TeamInfo("Los Angeles", "Rams", DIVISION.NFC, R.drawable.ic_la, "#022754"));
        put("SF", new TeamInfo("San Francisco", "49ers", DIVISION.NFC, R.drawable.ic_sf, "#B79C6D"));
    }};

    public enum UPDATE_TYPE {
        HOME_TEAM_SELECTED,
        AWAY_TEAM_SELECTED,
        UNPREDICTED
    }

    public enum PREDICTION_TYPE {
        SUPERBOWL,
        AFC_WINNER,
        NFC_WINNER,
        BEST_OFFENSE,
        BEST_DEFENSE
    }

    public enum DIVISION {
        AFC,
        NFC
    }
}
