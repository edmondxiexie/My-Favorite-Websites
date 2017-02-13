package dao;

import javax.servlet.ServletException;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;
import org.genericdao.MatchArg;
import org.genericdao.RollbackException;
import org.genericdao.Transaction;

import databean.FavoriteBean2;

public class FavoriteDAO2 extends GenericDAO<FavoriteBean2> {

    public FavoriteDAO2(ConnectionPool connectionPool, String tableName)
            throws DAOException {
        super(FavoriteBean2.class, tableName, connectionPool);
    }

    public FavoriteBean2[] getUserFavorites(int userId) throws RollbackException {
        
        try {
            FavoriteBean2[] favorites = match(MatchArg.equals("userId", userId));
            return favorites;
        } catch (RollbackException e) {
            e.printStackTrace();
            throw new RollbackException(e);
        }
    }
    
}
