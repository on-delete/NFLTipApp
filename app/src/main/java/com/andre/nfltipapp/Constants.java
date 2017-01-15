package com.andre.nfltipapp;

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

    public static final Map<String, String> TEAM_MAP = new HashMap<String, String>(){{
        put("NE", "New England Patriots");
        put("MIA", "Miami Dolphins");
        put("BUF", "Buffalo Bills");
        put("NYJ", "New York Jets");
        put("PIT", "Pittsburgh Steelers");
        put("BAL", "Baltimore Ravens");
        put("CIN", "Cincinnati Bengals");
        put("CLE", "Cleveland Browns");
        put("HOU", "Houston Texans");
        put("TEN", "Tennessee Titans");
        put("IND", "Indianapolis Colts");
        put("JAX", "Jacksonville Jaguars");
        put("OAK", "Oakland Raiders");
        put("KC", "Kansas City Chiefs");
        put("DEN", "Denver Broncos");
        put("SD", "San Diego Chargers");
        put("DAL", "Dallas Cowboys");
        put("NYG", "New York Giants");
        put("WAS", "Washington Redskins");
        put("PHI", "Philadelphia Eagles");
        put("GB", "Green Bay Packers");
        put("DET", "Detroit Lions");
        put("MIN", "Minnesota Vikings");
        put("CHI", "Chicago Bears");
        put("ATL", "Atlanta Falcons");
        put("TB", "Tampa Bay Buccaneers");
        put("NO", "New Orleans Saints");
        put("CAR", "Carolina Panthers");
        put("SEA", "Seattle Seahawks");
        put("ARI", "Arizona Cardinals");
        put("LA", "Los Angeles Rams");
        put("SF", "San Francisco 49ers");
    }};

    public static final Map<String, String> WEEK_TYPE_MAP = new HashMap<String, String>() {{
        put("PRE", "Preseason");
        put("REG", "Regular Season");
        put("WC", "Wildcard");
        put("DIV", "Divisional Playoffs");
    }};
}
