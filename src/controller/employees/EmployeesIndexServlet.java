package controller.employees;

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
import utils.DBUtil;

/**
 * Servlet implementation class EmployeesIndexServlet
 */
@WebServlet("/employees/index")
public class EmployeesIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesIndexServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        int page = 1; //1ページ目からという理由でまず、変数１を設定
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch(NumberFormatException e) { }  //処理を止めないようにtry catchで囲む

        List<Employee> employees = em.createNamedQuery("getAllEmployees", Employee.class) //複数のカラムのデータの数
                                  .setFirstResult(15 * (page - 1)) //1ページ目は1を代入すると０になり、コンピューターの１(0)となる
                                  .setMaxResults(15)
                                  .getResultList();

        long employees_count = (long)em.createNamedQuery("getEmployeeCount", Long.class)  //カラムのデータが何個かという１つの数字
                                    .getSingleResult();
        em.close();

        //リクエストコープに送る

        request.setAttribute("employees", employees);
        request.setAttribute("employees_count", employees_count);
        request.setAttribute("page", page);
        if(request.getSession().getAttribute("flush") != null) { //フラッシュメッセージがセッションスコープにセットされていたら、
            request.setAttribute("flush", request.getSession().getAttribute("flush")); //セッションスコープ内のフラッシュメッセージをリクエストコープに保存し、
            request.getSession().getAttribute("flush");//セッション内のフラッシュメッセージを取得する
        }

        //Employeeの処理後にindex.jspに画面を戻す処理
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/index.jsp");
        rd.forward(request, response);


    }

}
