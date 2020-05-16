package controllers.toppage;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Report;
import utils.DBUtil;


@WebServlet("/index.html")
public class TopPageIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    public TopPageIndexServlet() {
        super();

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        //セッションスコープからログインする社員を取得する
        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");

        //開くページ数を取得
        int page;
        try{
            page = Integer.parseInt(request.getParameter("page"));
        }catch(Exception e){
            page = 1;
        }
        //最大件数と開始位置を指定してメッセージを取得
        List<Report> reports = em.createNamedQuery("getMyAllReports",Report.class)
                                 .setParameter("employee", login_employee)
                                 .setFirstResult(15 * (page - 1))
                                 .setMaxResults(15)
                                 .getResultList();

        //全件数を取得
        long reports_count = (long)em.createNamedQuery("getMyReportsCount",Long.class)
                                     .setParameter("employee", login_employee)
                                     .getSingleResult();
        em.close();

        request.setAttribute("reports", reports);
        request.setAttribute("reports_count", reports_count);//全件数
        request.setAttribute("page", page);//ページ数


        if(request.getSession().getAttribute("flush") != null){
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }


        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/topPage/index.jsp");
        rd.forward(request, response);

    }

}
