package org.flightbooking;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import org.flightbooking.models.User;
import org.flightbooking.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;


@WebServlet(name = "helloServlet", value = "/hello")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Открыть сессию
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            // Начать транзакцию
            transaction = session.beginTransaction();

            // Создать объект User
            User user = new User();
            user.setFirstName("kirill");
            user.setLastName("petrov");
            user.setEmail("test@test.com");
            user.setPassword("password123");

            // Сохранить объект в базу данных
            session.persist(user);

            // Завершить транзакцию
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        // Закрыть SessionFactory
        HibernateUtil.shutdown();
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}