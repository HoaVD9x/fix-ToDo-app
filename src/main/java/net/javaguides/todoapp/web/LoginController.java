package net.javaguides.todoapp.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.javaguides.todoapp.model.LoginBean;
import net.javaguides.todoapp.model.User;
import net.javaguides.todoapp.utils.HibernateUtil;
import net.javaguides.todoapp.utils.MD5Util;
import org.hibernate.Session;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @email Ramesh Fadatare
 */

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Session session;

    @Override
    public void init() {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("login/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        authenticate(request, response);
    }

    private void authenticate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        LoginBean loginBean = new LoginBean(username, password);

//        dung session de query csdl
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);

        try {
            String passEncrypt = MD5Util.encrypt(password);
            criteriaQuery.select(root).where(builder.and(builder.equal(root.get("username"), username),
                    builder.equal(root.get("password"), passEncrypt)));
            session.createQuery(criteriaQuery).getSingleResult();
            response.sendRedirect(request.getContextPath() + "/list");
//            RequestDispatcher dispatcher = request.getRequestDispatcher("todo/todo-list.jsp");
//            dispatcher.forward(request, response);
        } catch (NoResultException exception) {
            exception.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
