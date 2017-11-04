package com.shuishou.malatang.db;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.shuishou.malatang.bean.Desk;
import com.shuishou.malatang.ui.MainActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/9.
 */

public class DBOperator {
//    private final MainActivity mainActivity;
    private static LiteOrm liteOrm;

    public DBOperator(MainActivity mainActivity){
//        this.mainActivity = mainActivity;
        if (liteOrm == null){
            liteOrm = LiteOrm.newCascadeInstance(mainActivity, "digitalmenu.db");
        }
        liteOrm.setDebugged(true);
    }

    public void saveObjectByCascade(Object o){
        liteOrm.cascade().save(o);
    }

    public void saveObjectsByCascade(ArrayList objects){
        liteOrm.cascade().save(objects);
    }

    public void updateObject(Object o){
        liteOrm.update(o);
    }

    public Object queryObjectById(int id, Class c){
        return liteOrm.queryById(id, c);
    }
    public void deleteAllData(Class c){
        liteOrm.deleteAll(c);
    }
    public void deleteObject(Object o){
        liteOrm.delete(o);
    }


    public void clearDesk(){
        liteOrm.deleteAll(Desk.class);
    }

    public ArrayList<Desk> queryDesks(){
        return liteOrm.query(new QueryBuilder<Desk>(Desk.class).appendOrderAscBy("sequence"));
    }

    public LiteOrm getLiteOrm(){
        return liteOrm;
    }
}
