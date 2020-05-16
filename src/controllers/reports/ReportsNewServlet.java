package controllers.reports;

import java.io.IOException;
import java.sql.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;

@WebServlet("/reports/new")
public class ReportsNewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


    public ReportsNewServlet() {
        super();

    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       //CSRF対策
	    request.setAttribute("_token", request.getSession().getId());

	    //今日の日付を入力欄に表示させる機能
	    Report r = new Report();
	    r.setReport_date(new Date(System.currentTimeMillis()));
	    request.setAttribute("report", r);

	    RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/new.jsp");
	    rd.forward(request, response);

	}

}
