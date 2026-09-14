package controller;

import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.UserService;

import java.io.IOException;
import java.util.Optional;

@WebServlet(
        name = "LoginServlet",
        urlPatterns = {"/login"},
        initParams = {
                @WebInitParam(name = "sessionTimeoutMinutes", value = "30")
        }
)
public class LoginServlet extends HttpServlet {

    private UserService userService;
    private int sessionTimeoutMinutes;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
        String timeoutParam = getInitParameter("sessionTimeoutMinutes");
        this.sessionTimeoutMinutes = timeoutParam != null ? Integer.parseInt(timeoutParam) : 30;

        // Khởi tạo tài khoản admin mặc định (admin / admin123) nếu chưa có
        try {
            userService.initDefaultAdminAccount();
        } catch (Exception e) {
            log("Lỗi khởi tạo tài khoản mặc định: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Optional<User> userOpt = userService.login(username, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedUser", user);
            session.setMaxInactiveInterval(sessionTimeoutMinutes * 60);
            resp.sendRedirect(req.getContextPath() + "/products");
        } else {
            req.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
