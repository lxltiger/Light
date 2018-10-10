package com.kimascend.light.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.home.entity.Group;
import com.kimascend.light.mesh.Mesh;
import com.kimascend.light.scene.Scene;
import com.kimascend.light.user.Profile;

/**
 * 如果修改了entity的字段 一定要升级版本号
 * 提高Migration，增加表的语法在SmartLightDataBase_impl中实现 可以复制过来
 * 如果自已实体类被标记了entity 一定要在此处注册登记 否则编译不过
 *  主键需要标注为NotNull 否则编译不过
 *
 *  如果在Dao中使用了某个字段， 如果识别不了，需要加上ColumnInfo注解
 */
@Database(entities = {Lamp.class, Profile.class,Mesh.class, Scene.class,Group.class}, version = 7, exportSchema = false)
public abstract class SmartLightDataBase extends RoomDatabase {

    private static final String DATABASE_NAME = "SmartLight.db";
    private static SmartLightDataBase sDataBase;

    public abstract LampDao lamp();
    public abstract UserDao user();
    public abstract GroupDao group();
    public abstract SceneDao scene();

    public synchronized static SmartLightDataBase INSTANCE(Context context) {
        if (sDataBase == null) {
            sDataBase = Room.databaseBuilder(context.getApplicationContext(), SmartLightDataBase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_3_4,MIGRATION_4_5,MIGRATION_5_6,MIGRATION_6_7)
//                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sDataBase;
    }

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE mesh ADD COLUMN userId text");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            加了一个表scene
            database.execSQL("CREATE TABLE IF NOT EXISTS `scene` (`id` TEXT NOT NULL, `creater` TEXT, `icon` TEXT, `meshId` TEXT, `meshName` TEXT, `name` TEXT, `sceneId` INTEGER NOT NULL, PRIMARY KEY(`id`))");

        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            加了一个表group
            database.execSQL("CREATE TABLE IF NOT EXISTS `group` (`groupId` INTEGER NOT NULL, `id` TEXT NOT NULL, `name` TEXT, `icon` TEXT, `deviceIds` TEXT, PRIMARY KEY(`id`))");
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE scene ADD COLUMN deviceIds text");
        }
    };
}
