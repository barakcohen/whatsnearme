package one.tribe.whatsnearme.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import one.tribe.whatsnearme.R;

/**
 *
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "whatsnearme.db";

    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "Creating database tables");

            TableUtils.createTable(connectionSource, KnownDevice.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "App database cannot be created", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "Updating database tables");

            TableUtils.dropTable(connectionSource, KnownDevice.class, true);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "App database cannot be updated", e);
            throw new RuntimeException(e);
        }
    }

    public <T> RuntimeExceptionDao<T, Integer> getDAO(Class<T> clazz) {
        return getRuntimeExceptionDao(clazz);
    }
}
