package controllers.reports;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;
import models.Favorite;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsShowServlet
 */
@WebServlet("/reports/show")
public class ReportsShowServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsShowServlet() {
        super();

    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         EntityManager em = DBUtil.createEntityManager();

         try{
            Report r = em.find(Report.class, Integer.parseInt(request.getParameter("id"))); //idを元にそのクラスを呼び出し、変数rに格納

            Favorite f = em.find(Favorite.class, Integer.parseInt(request.getParameter("id")));//いいね機能

            HttpSession session = ((HttpServletRequest)request).getSession();
            Employee e = (Employee)session.getAttribute("login_employee");

            request.setAttribute("report", r); //jspに送信するためにリクエストコープに格納
            request.setAttribute("_token", request.getSession().getId()); //SessionIdをセッションから取り出し、リクエストコープに格納
            request.setAttribute("favorite", f);

            //黒と赤、どちらのアイコンを表示するかの判定の処理//同じ第一引数を送り、jsp側でfalseなら黒、trueなら赤を表示する処理をする
            List<Favorite> favo = em.createNamedQuery("selectFavorites", Favorite.class)
                                    .setParameter("report", r)
                                    .setParameter("employee", e)
                                    .getResultList();

            em.close();
            if(favo.isEmpty()){
            request.setAttribute("favorite_exist",false); //いいねボタンが存在する
            } else {
            request.setAttribute("favorite_exist",true);  //いいねボタンが存在しない
            }

         }catch(NoResultException ex) {}

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/show.jsp");
            rd.forward(request, response);
    }

}
