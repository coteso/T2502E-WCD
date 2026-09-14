package Filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
/**
 * FILTER - chan cac request vao /products khi chua dang nhap.
 * Day la mot thanh phan phu tro cua tang Controller trong kien truc Servlet,
 * giup tap trung logic "bao ve trang" thay vi lap lai o tung Servlet.
 *
 * ==========================================================================
 * VONG DOI FILTER - song song va CHAY TRUOC vong doi servlet
 * ==========================================================================
 *   (1) INSTANTIATE : container tao filter bang constructor khong doi so.
 *   (2) init(FilterConfig) : chay DUNG 1 LAN luc deploy, truoc moi request.
 *   (3) doFilter(...)      : chay cho MOI request khop urlPatterns.
 *                            chain.doFilter() = di TIEP toi filter ke tiep
 *                            hoac toi servlet dich. KHONG goi chain.doFilter()
 *                            nghia la chan dung request tai day.
 *                            Code truoc chain.doFilter() = xu ly REQUEST;
 *                            code sau chain.doFilter()  = xu ly RESPONSE.
 *   (4) destroy()          : chay DUNG 1 LAN khi undeploy / tat server.
 *
 * Thu tu tong the:  Filter.init -> Servlet.init -> Filter.doFilter
 *                   -> Servlet.service -> Servlet.destroy -> Filter.destroy
 * ==========================================================================
 */
@WebFilter(
        filterName = "AuthFilter",
        urlPatterns = {"/products"},
        initParams = {
                @WebInitParam(name = "loginPath", value = "/login"),
                @WebInitParam(name = "sessionKey", value = "loggedUser")
        }
)
public class AuthFilter implements Filter {

    /** Doc tu <init-param> trong init(FilterConfig). */
    private String loginPath;
    private String sessionKey;

    /** Giu lai FilterConfig de dung ve sau (vd: getServletContext()). */
    private FilterConfig filterConfig;

    // ---------------------------------------------------------------------
    // (1) INSTANTIATE
    // ---------------------------------------------------------------------

    public AuthFilter() {
        System.out.println("[LIFECYCLE] AuthFilter (1) CONSTRUCTOR - instance vua duoc tao");
    }

    // ---------------------------------------------------------------------
    // (2) INIT - chay 1 LAN luc deploy
    // ---------------------------------------------------------------------

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.filterConfig = config;
        this.loginPath = config.getInitParameter("loginPath");
        this.sessionKey = config.getInitParameter("sessionKey");
        config.getServletContext().log("[LIFECYCLE] AuthFilter (2) init() - filter = "
                + config.getFilterName() + " | loginPath = " + loginPath
                + " | sessionKey = " + sessionKey);
    }

    // ---------------------------------------------------------------------
    // (3) DO FILTER - chay cho MOI request khop /products
    // ---------------------------------------------------------------------

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute(sessionKey) != null);

        filterConfig.getServletContext().log("[LIFECYCLE] AuthFilter (3) doFilter() TRUOC - "
                + req.getMethod() + " " + req.getRequestURI() + " | loggedIn = " + loggedIn);

        if (loggedIn) {
            // Di tiep trong chuoi: filter ke tiep -> ProductServlet.service()
            chain.doFilter(request, response);

            // Code sau chain.doFilter() chay khi servlet da xu ly xong (giai doan RESPONSE)
            filterConfig.getServletContext().log("[LIFECYCLE] AuthFilter (3) doFilter() SAU - "
                    + "servlet da xu ly xong, status = " + resp.getStatus());
        } else {
            // KHONG goi chain.doFilter() -> chan request, khong toi duoc servlet
            filterConfig.getServletContext().log("[LIFECYCLE] AuthFilter (3) CHAN request "
                    + "-> redirect ve " + loginPath);
            resp.sendRedirect(req.getContextPath() + loginPath);
        }
    }

    // ---------------------------------------------------------------------
    // (4) DESTROY - chay 1 LAN khi undeploy / tat server
    // ---------------------------------------------------------------------

    @Override
    public void destroy() {
        if (filterConfig != null) {
            filterConfig.getServletContext().log("[LIFECYCLE] AuthFilter (4) destroy() - giai phong filter");
        }
        this.filterConfig = null;
    }
}
