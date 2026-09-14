package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(
        name = "LogoutServlet",
        urlPatterns = {"/logout"},
        initParams = {
                @WebInitParam(name = "redirectAfterLogout", value = "/login")
        }
)
public class LogoutServlet extends HttpServlet {

    private String redirectAfterLogout;

    @Override
    public void init() throws ServletException {
        this.redirectAfterLogout = getInitParameter("redirectAfterLogout");
        if (this.redirectAfterLogout == null) {
            this.redirectAfterLogout = "/login";
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + redirectAfterLogout + "?msg=logged_out");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
