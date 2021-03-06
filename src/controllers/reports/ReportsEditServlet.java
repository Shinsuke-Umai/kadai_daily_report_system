package controllers.reports;

import java.io.IOException;

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

/**
 * Servlet implementation class ReportsEditServlet
 */
@WebServlet("/reports/edit")
public class ReportsEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsEditServlet() {
        super();

    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        Report r = em.find(Report.class, Integer.parseInt(request.getParameter("id")));//どのレポートか

        em.close();

        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");//ログインしている従業員のオブジェクト

        if(r != null && login_employee.getId() == r.getEmployee().getId()) { //ログインした本人以外は見れないように、セッションにあるログイン情報とデータベスから受け取ったidが一致しているかを確認
            request.setAttribute("report", r);//レポートをjspへ
            request.setAttribute("_token", request.getSession().getId());//正しいセッションかどうかを確認する値をjspへ
            request.getSession().setAttribute("report_id", r.getId());//正しいセッション正しいレポートかどうかの値をセッションへ
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/edit.jsp");
        rd.forward(request, response);
    }

}
