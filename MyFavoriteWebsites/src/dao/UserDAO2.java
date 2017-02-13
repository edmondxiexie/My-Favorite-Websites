package dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import databean.User;

public class UserDAO2 extends GenericDAO<User> {

    public UserDAO2(ConnectionPool connectionPool, String tableName) throws DAOException {
        super(User.class, tableName, connectionPool);
    }

}
