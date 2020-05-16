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

import models.Employee;
import models.Report;
import models.validators.ReportValidator;
import utils.DBUtil;


@WebServlet("/reports/create")
public class ReportsCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    public ReportsCreateServlet() {
        super();

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //CSRF対策
        String _token = (String)request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())) {

            EntityManager em = DBUtil.createEntityManager();

            Report r = new Report();

            r.setEmployee((Employee)request.getSession().getAttribute("login_employee"));


            Date report_date = new Date(System.currentTimeMillis());
            String rd_str = request.getParameter("report_date");
            if(rd_str != null && !rd_str.equals("")) {  //日付欄をわざと未入力にした場合、当日の日付が入力されるようにしてます
                report_date = Date.valueOf(request.getParameter("report_date")); //Stringで受け取った日付をDate型へ変換する処理を行う
            }
            r.setReport_date(report_date);

            //フォームで入力した内容をプロパティに上書きします
            r.setTitle(request.getParameter("title"));
            r.setContent(request.getParameter("content"));

            //現在日時のもつ日付型オブジェクトを取得できます。
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            r.setCreated_at(currentTime);
            r.setUpdated_at(currentTime);


            //バリデーションを入力してエラーがあれば新規登録フォームに戻る
            List<String> errors = ReportValidator.validate(r);
            if(errors.size() > 0) {
                em.close();

                //フォームに初期値を設定さらにエラーメッセージを送る
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("report", r);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/new.jsp");
                rd.forward(request, response);
            } else {
                //データベースに保存
                em.getTransaction().begin();
                em.persist(r);
                em.getTransaction().commit();
                em.close();
                request.getSession().setAttribute("flush", "登録が完了しました。");

                //indexページにリダイレクト
                response.sendRedirect(request.getContextPath() + "/reports/index");
            }
        }
    }

}