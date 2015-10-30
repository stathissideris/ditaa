package org.ditaa.web;

import org.apache.commons.io.FileUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class RestartServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doit(resp); }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doit(resp); }

    // also set by ImageServlet, to start the timer when ImageServlet is first invoked
    // (because otherwise, this servlet may only be invoked when there's already a problem,
    // and we don't want to make people wait 5 minutes after that -- they'll just give up and leave.
    static final long[] lastReset = { System.currentTimeMillis() };
//    private static final long MIN_RESET_INTERVAL = 10 * 1000; // 5 minutes in millis
    private static final long MIN_RESET_INTERVAL = 5 * 60 * 1000; // 5 minutes in millis

    private static class RestartResult {
        boolean successful;
        String message;
        private RestartResult(boolean success, String msg) { successful = success; message = msg; }
    }

    private void doit(HttpServletResponse resp) throws IOException {
        boolean reset;
        long diff;
        synchronized (lastReset) {
            diff = System.currentTimeMillis() - lastReset[0];
            reset = diff > MIN_RESET_INTERVAL;
            if (reset) lastReset[0] = System.currentTimeMillis();
        }
        resp.setContentType("text/html");
        resp.getWriter().println("<html><head><title>" + Config.getTitle(getServletContext()) + "</title></head><body>");
        if (reset) {
            RestartResult result = restart(getServletContext());
            resp.getWriter().println("<h2>" + (result.successful ? "Success" : "Failed") + "</h2>");
            resp.getWriter().println("<p>" + result.message + "</p>");
        } else {
            resp.getWriter().println("<h2>The service was not restarted.</h2>");
            resp.getWriter().println();
            resp.getWriter().println("<p>The service was initialized " + (diff > 120000 ? ((diff / 60000) + " minutes and ") : "") + ((diff % 60000) / 1000) + " seconds ago.</p>");
            resp.getWriter().println("<p>The minimum reset interval is " + (MIN_RESET_INTERVAL / 60000) + " minutes.</p>");
        }
        resp.getWriter().println("</body></html>");
    }

    public static RestartResult restart(ServletContext context) throws IOException {
        String name = Config.getRestartFilename(context);
        if (Compare.isBlank(name)) return new RestartResult(false, "No reset file configured.");
        else {
            File file = new File(Config.getRestartFilename(context));
            try {
                FileUtils.touch(file);
                return new RestartResult(true, "The service will reset " + Config.getRestartTimePeriod(context) + ".");
            } catch(IOException e) {
                e.printStackTrace();
                return new RestartResult(false, "Unable to reset the service: " + e.getMessage());
            }
        }
    }

    private static final long serialVersionUID = 1;
}
