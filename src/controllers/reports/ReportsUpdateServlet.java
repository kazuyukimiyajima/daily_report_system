package controllers.reports;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import models.validators.ReportValidator;
import utils.DBUtil;


@WebServlet("/reports/update")
public class ReportsUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;



    public ReportsUpdateServlet() {
        super();

    }


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    //CSRF対策！！
        String _token = (String)request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())){

            EntityManager em = DBUtil.createEntityManager();

            //セッションスコープからレポートのIDを取得して、該当のIDレポート１件のみをデータベースから取得
            Report r = em.find(Report.class, (Integer)(request.getSession().getAttribute("report_id")));

            //フォームで入力した内容を各プロパティーに上書きします
            r.setReport_date(Date.valueOf(request.getParameter("report_date")));
            r.setTitle(request.getParameter("title"));
            r.setContent(request.getParameter("content"));
            r.setUpdated_at(new Timestamp(System.currentTimeMillis()));

            //バリデーションを実行してエラーがあれば編集画面のフォームに戻る
            List<String> errors = ReportValidator.validate(r);
            if(errors.size() > 0){
                em.close();

                //フォームに初期値を設定、さらにメッセージを送る
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("report", r);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/edit.jsp");
                rd.forward(request, response);


            }else{
                //データベースの更新
                em.getTransaction().begin();
                em.getTransaction().commit();
                em.close();
                request.getSession().setAttribute("flush", "更新が完了しました。");
                //セッションスコープ上の不要になったデータの削除
                request.getSession().removeAttribute("report_id");
                //indexページへリダイレクト
                response.sendRedirect(request.getContextPath() + "/reports/index");

            }

        }
	}

}
