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

/**
 * Servlet implementation class ReportsCreateServlet
 */
@WebServlet("/reports/create")
public class ReportsCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsCreateServlet() {
        super();

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = (String)request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();

            Report r = new Report();

            r.setEmployee((Employee)request.getSession().getAttribute("login_employee")); //従業員情報のオブジェクトがlogin_employee

            //日報の更新日時の処理
            Date report_date = new Date(System.currentTimeMillis());
            String rd_str = request.getParameter("report_date"); //String型でデータを受け取っている。
            if(rd_str != null && rd_str.equals("")) {//日付欄をわざと未入力にした場合、当日の日付を入れるようにしている。
                report_date = Date.valueOf(request.getParameter("report_date")); //Stringで受け取った日付を Date 型へ変換する処理を行っている。
            }
            r.setReport_date(report_date);

            r.setTitle(request.getParameter("title"));
            r.setContent(request.getParameter("content"));

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            r.setCreated_at(currentTime);
            r.setUpdated_at(currentTime);

            List<String> errors = ReportValidator.validate(r);
            if(errors.size() > 0) {
                em.close();

                //エラーがあれば、新規登録画面(new画面)に戻す。
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("report", r);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/new.jsp");
                rd.forward(request, response);
        } else {
            //エラーがなかった場合
            em.getTransaction().begin();
            em.persist(r);
            em.getTransaction().commit();
            em.close();
            request.getSession().setAttribute("flush", "登録が完了しました。");

            response.sendRedirect(request.getContextPath() + "/reports/index");

            }
       }
   }
}
