package com.udanano.popularmoviesretry;

import android.provider.BaseColumns;

/**
 * Created by Rawk on 9/30/2015.
 */
public final class TableData {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TableData() {}

    /* Inner class that defines the table contents */
    public static abstract class TableInfo implements BaseColumns {
        public static final String DATABASE_NAME = "popularMoviesFavorites";
        public static final String TABLE_NAME = "favoriteMovies";
        public static final String COLUMN_NAME_POSTER = "poster";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AVERAGE = "average";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        public static final String COLUMN_NAME_POPULARITY = "popularity";
        public static final String COLUMN_NAME_ID = "id";
    }
}