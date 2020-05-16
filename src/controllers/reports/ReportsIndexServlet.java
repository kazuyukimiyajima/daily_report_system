package controllers.reports;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import utils.DBUtil;

@WebServlet("/reports/index")
public class ReportsIndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


    public ReportsIndexServlet() {
        super();

    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        //開くページ数の取得
        int page;
	    try{
	        page = Integer.parseInt(request.getParameter("page"));
	    }catch(Exception e){
	        page = 1;
	    }
	    //最大件数と開始位置を指定してレポートを取得
	    List<Report>reports = em.createNamedQuery("getAllReports",Report.class)
	                            .setFirstResult(15 * (page - 1))
	                            .setMaxResults(15)
	                            .getResultList();

	    //全件数を取得
	    long reports_count = (long)em.createNamedQuery("getReportsCount",Long.class)
	                                  .getSingleResult();

	    em.close();

	    request.setAttribute("reports", reports);
	    request.setAttribute("reports_count", reports_count);
	    request.setAttribute("page", page);

	       //フラッシュメッセージがセッションスコープにセットされていたら
        //リクエストスコープに保存する（セッションスコープから削除）
	    if(request.getSession().getAttribute("flush") != null){
	        request.setAttribute("flush", request.getSession().getAttribute("flush"));
	        request.getSession().removeAttribute("flush");
	    }

	    RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/index.jsp");
	    rd.forward(request, response);
	}

}
