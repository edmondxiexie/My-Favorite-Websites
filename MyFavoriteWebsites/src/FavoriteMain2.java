/**
 * 08-672 Assignment 3.
 * @author Jiayi Xie
 * @id jiayix 
 * 11/25/2016
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.RollbackException;

import dao.FavoriteDAO2;
import dao.UserDAO2;
import databean.FavoriteBean2;
import databean.User;
import formbean.FavoriteForm;
import formbean.LoginForm;
import formbean.RegisterForm;

/**
 * Servlet implementation class Hw3
 */
public class FavoriteMain2 extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
        private FavoriteDAO2 favoriteDAO;
        private UserDAO2 userDAO;
        
        public void init() throws ServletException {
            String jdbcDriverName = getInitParameter("jdbcDriver");
            String jdbcURL = getInitParameter("jdbcURL");

            try {
                ConnectionPool cp = new ConnectionPool(jdbcDriverName, jdbcURL);
                
                userDAO = new UserDAO2(cp, "fav_generic_user");
                favoriteDAO = new FavoriteDAO2(cp, "fav_generic_favorite");
            } catch (DAOException e) {
                throw new ServletException(e);
            }
        }
        
        public void doGet(HttpServletRequest request, HttpServletResponse response) 
                throws ServletException, IOException {
            HttpSession session = request.getSession();
            if (session.getAttribute("user") == null) {
                login(request, response);
            } else {
                addFavorite(request, response, (User)session.getAttribute("user"));
            }
        }
        
        public void doPost(HttpServletRequest request, HttpServletResponse response) 
                throws ServletException, IOException {
            doGet(request, response);
        }

        private void login(HttpServletRequest request, HttpServletResponse response) 
                throws ServletException, IOException {
            List<String> errors = new ArrayList<String>();
            
            LoginForm loginForm = new LoginForm(request);
            
            RegisterForm registerForm = new RegisterForm(request);
            
            if (!loginForm.isPresent()) {
                outputLoginPage(response, loginForm, null);
                return;
            }
            
            try {
                User user;
                   
                // Jump to register page. 
                if (loginForm.getButton().equals("Register")) {
                    outputRegisterPage(response, loginForm, registerForm, errors);
                    return;
                } 
                
                // Login page
                else if(loginForm.getButton().equals("Login")){
                    errors.addAll(loginForm.getValidationErrors());
                    if (errors.size() != 0) {
                        outputLoginPage(response, loginForm, errors);
                        return;
                    }
                       
                    // Find user in the database
                    user = userDAO.read(loginForm.getEmailAddress());
                    if (user == null) {
                        errors.add("No such user");
                        outputLoginPage(response, loginForm, errors);
                        return;
                    }

                    if (!loginForm.getPassword().equals(user.getPassword())) {
                        errors.add("Incorrect password");
                        outputLoginPage(response, loginForm, errors);
                        return;
                    }
                } 
                
                // Register page 
                else {                    
                    user = userDAO.read(registerForm.getEmailAddress());
                    if (user != null) {
                        errors.add("Exsiting user");
                        outputRegisterPage(response, loginForm, registerForm, errors);
                        return;
                    }
                    
                    errors.addAll(registerForm.getValidationErrors());
                    if (errors.size() != 0) {
                        outputRegisterPage(response, loginForm, registerForm, errors);
                        return;
                    }
                                        
                    user = new User();
                    user.setEmailAddress(registerForm.getEmailAddress());
                    user.setPassword(registerForm.getPassword());
                    user.setFirstName(registerForm.getFirstName());
                    user.setLastName(registerForm.getLastName());
                    userDAO.create(user);
                    user = userDAO.read(user.getEmailAddress());
                }
                
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                addFavorite(request, response, user);
            } catch (RollbackException e) {
                errors.add(e.getMessage());
                outputLoginPage(response, loginForm, errors);
            }
        }

        private void addFavorite(HttpServletRequest request, HttpServletResponse response, User user) 
                throws ServletException, IOException {
            List<String> errors = new ArrayList<String>();
            
            FavoriteForm favoriteForm = new FavoriteForm(request);

            if (!favoriteForm.isPresent() && favoriteForm.getFavoriteId() == null) {
                outputList(response, favoriteForm, null, user);
                return;
            }
            
            errors.addAll(favoriteForm.getValidationErrors());
            if (errors.size() != 0) {
                outputList(response, favoriteForm, errors, user);
                return;
            }

            try {
                FavoriteBean2 favoriteBean;
                if (favoriteForm.isPresent() && favoriteForm.getButton().equals("Add Favorite")) {
                    favoriteBean = new FavoriteBean2();
                    favoriteBean.setUserId(user.getUserId());
                    favoriteBean.setUrl(favoriteForm.getUrl());
                    favoriteBean.setComments(favoriteForm.getComments());
                    favoriteBean.setClicks(0);
                    favoriteDAO.create(favoriteBean);
                    outputList(response, favoriteForm, errors, user);
                } else if(favoriteForm.isPresent() && favoriteForm.getButton().equals("Logout")) {
                    HttpSession session = request.getSession();
                    session.removeAttribute("user");
                    login(request, response);
                } else {
//                    favoriteDAO.count(Integer.parseInt(favoriteForm.getFavoriteId()));
                    FavoriteBean2 favorite = favoriteDAO.read(Integer.parseInt(favoriteForm.getFavoriteId()));
                    favorite.setClicks(favorite.getClicks() + 1);
                    favoriteDAO.update(favorite);
                    outputList(response, favoriteForm, errors, user);
                }

            } catch (RollbackException e) {
                errors.add(e.getMessage());
                outputList(response, favoriteForm, errors, user);
            }
        }
        
        // Methods that generate & output HTML
        
        private final String servletName = this.getClass().getSimpleName();
        
        private void generateHead(PrintWriter out) {
            out.println("  <head>");
            out.println("    <meta charset=\"utf-8\"/>");
            out.println("    <title>" + servletName + "</title>");
            out.println("  </head>");
        }
        
        private void outputLoginPage(HttpServletResponse response, LoginForm form, List<String> errors) 
                throws IOException {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
        
            out.println("<!DOCTYPE html>");
            out.println("<html>");
        
            generateHead(out);
        
            out.println("<body>");
            out.println("<h2>" + servletName + " Login</h2>"); 
    
            if (errors != null && errors.size() > 0) {
                for (String error : errors) {
                    out.println("<p style=\"font-size: large; color: red\">");
                    out.println(error);
                    out.println("</p>");
                }
            }
        
            // Generate an HTML <form> to get data from the user
            out.println("<form method=\"POST\">");
            out.println("    <table>");
            
            // First row
            out.println("        <tr>");
            out.println(
                    "            <td style=\"font-size: large\">E-mail Address:</td>");
            out.println("            <td>");
            out.println("                <input type=\"text\" name=\"emailAddress\"");
            if (form != null && form.getEmailAddress() != null) {
                out.println("                    value=\"" + form.getEmailAddress() + "\"");
            }
            out.println("              autofocus />");
            out.println("            <td>");
            out.println("        </tr>");
            
            // Second row
            out.println("        <tr>");
            out.println("            <td style=\"font-size: large\">Password:</td>");
            out.println("            <td><input type=\"password\" name=\"password\" /></td>");
            out.println("        </tr>");
            
            // Third row
            out.println("        <tr>");
            out.println("            <td colspan=\"2\" align=\"center\">");
            out.println("                <input type=\"submit\" name=\"button\" value=\"Login\" />");
            out.println("                <input type=\"submit\" name=\"button\" value=\"Register\" />");
            out.println("            </td>");
            out.println("        </tr>");
            out.println("    </table>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
        
        private void outputRegisterPage(HttpServletResponse response, LoginForm loginForm, 
                                        RegisterForm registerForm, List<String> errors) 
                                                throws IOException {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
        
            out.println("<html>");
        
            generateHead(out);
        
            out.println("<body>");
            out.println("<h2>Register a new user</h2>");
            
            if (errors != null && errors.size() > 0) {
                for (String error : errors) {
                    out.println("<p style=\"font-size: large; color: red\">");
                    out.println(error);
                    out.println("</p>");
                }
            }
        
            // Generate an HTML <form> to get data from the user
            out.println("<form method=\"POST\">");
            out.println("    <table>");
            
            // Set E-mail Address
            out.println("        <tr>");
            out.println("            <td style=\"font-size: x-large\">E-mail Address:</td>");
            out.println("            <td>");
            out.println("                <input type=\"text\" name=\"emailAddress\"");
            if (registerForm != null && registerForm.getEmailAddress() != null) {
                out.println("                    value=\"" + registerForm.getEmailAddress() + "\"");
            }
            out.println("                />");
            out.println("            <td>");
            out.println("        </tr>");
            
            // Set First Name            
            out.println("        <tr>");
            out.println("            <td style=\"font-size: x-large\">First Name:</td>");
            out.println("            <td>");
            out.println("                <input type=\"text\" name=\"firstName\"");
            if (registerForm != null && registerForm.getFirstName() != null) {
                out.println("                    value=\"" + registerForm.getFirstName() + "\"");
            }
            out.println("                />");
            out.println("            <td>");
            out.println("        </tr>");
            
            // Set Last Name   
            out.println("        <tr>");
            out.println("            <td style=\"font-size: x-large\">Last Name:</td>");
            out.println("            <td>");
            out.println("                <input type=\"text\" name=\"lastName\"");
            if (registerForm != null && registerForm.getLastName() != null) {
                out.println("                    value=\"" + registerForm.getLastName() + "\"");
            }
            out.println("                />");
            out.println("            <td>");
            out.println("        </tr>");
            
            // Set Password
            out.println("        <tr>");
            out.println("            <td style=\"font-size: x-large\">Password:</td>");
            out.println("            <td><input type=\"password\" name=\"password\" /></td>");
            out.println("        </tr>");
            out.println("        <tr>");
            out.println("            <td colspan=\"2\" align=\"center\">");
            out.println("                <input type=\"submit\" name=\"button\" value=\"Submit\" />");
            out.println("            </td>");
            out.println("        </tr>");
            
            out.println("    </table>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
      
        private void outputList(HttpServletResponse response, FavoriteForm form, List<String> messages, User user) 
                throws IOException {
            FavoriteBean2[] favorites;
            try {
                favorites = favoriteDAO.getUserFavorites(user.getUserId());
            } catch (RollbackException e) {
                messages.add(e.getMessage());
                favorites = new FavoriteBean2[0];
            }
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html>");

            generateHead(out);

            out.println("<body>");
            out.println("<h2>Favorites for " + user.getFirstName() + " " + user.getLastName());
            out.println("</h2>");
            out.println("<h2>E-mail: " + user.getEmailAddress());
            out.println("</h2>");

            // Generate an HTML <form> to get data from the user
            out.println("<form method=\"POST\">");
            out.println("    <table>");
            out.println("        <tr>");
            out.println("            <td style=\"font-size: large\">URL:</td>");
            out.println("            <td colspan=\"2\"><input type=\"text\" size=\"40\" name=\"url\"/></td>");
            out.println("        </tr>");
            out.println("        <tr>");
            out.println("            <td style=\"font-size: large\">Comment:</td>");
            out.println("            <td colspan=\"2\"><input type=\"text\" size=\"40\" name=\"comments\"/></td>");
            out.println("        </tr>");
            out.println("        <tr>");
            out.println("            <td/>");
            out.println("            <td colspan=\"2\"><input type=\"submit\" name=\"favoritebutton\" value=\"Add Favorite\"/>");
            out.println("                              <input type=\"submit\" name=\"favoritebutton\" value=\"Logout\"/>");
            out.println("            </td>");
            out.println("        </tr>");
            out.println("    </table>");
            out.println("</form>");

            if(messages != null) {
                for (String message : messages) {
                    out.println("<p style=\"font-size: large; color: red\">");
                    out.println(message);
                    out.println("</p>");
                }
            }
     
            out.println("<p style=\"font-size: large\">Total Favorites : "+favorites.length+"</p>");
            out.println("<table>");
            if (favorites != null) {
                for(int i = 0; i < favorites.length; i++) {
                    out.println("<table>");
                    out.println("  <tr>");
                    out.println("    <td><a href=\"jiayix?favoriteId=" + favorites[i].getFavoriteId() + "\">" + favorites[i].getUrl() + "</a></td>");
                    out.println("  </tr>");
                    out.println("  <tr>");
                    out.println("    <td>" + favorites[i].getComments() + "</td>");
                    out.println("  </tr>");
                    out.println("  <tr>");
                    out.println("    <td>" + favorites[i].getClicks() + " Clicks</td>");
                    out.println("  </tr>");
                    out.println("</table>");
                    out.println("</br>");
                }
            }

            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
        }
}
