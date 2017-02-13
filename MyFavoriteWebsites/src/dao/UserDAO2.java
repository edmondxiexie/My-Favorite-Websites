package dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import databean.UserBean;

public class UserDAO2 extends GenericDAO<UserBean> {

    public UserDAO2(ConnectionPool connectionPool, String tableName) throws DAOException {
        super(UserBean.class, tableName, connectionPool);
    }

}
