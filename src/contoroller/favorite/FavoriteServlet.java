package contoroller.favorite;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
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
 * Servlet implementation class FavoriteServlet
 */
@WebServlet("/favorite")
public class FavoriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FavoriteServlet() {
        super();

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //どのレポートに誰がいるのか
        String _token = request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();

            Favorite f = new Favorite();//いいねの変数

            //ログインユーザーの確認
            HttpSession session = ((HttpServletRequest)request).getSession();

            //ログインしている従業員・初期値はログインしている人
            Employee e = (Employee)session.getAttribute("login_employee");
            f.setEmployee(e); //mysqlに登録する側なので、set。getは違う。

            //ログインしているレポート・初期値はログインしている人
            Report r = (Report)session.getAttribute("login_employee");
            f.setReport(r);

            em.getTransaction().begin();
            em.persist(f);
            em.getTransaction().commit();


         //DBからdelete
            List<Favorite> deletefavorite = em.createNamedQuery("deleteFavorites", Favorite.class)
                                        .getResultList();

            em.close();

         //リクエストスコープに格納
            request.setAttribute("favorite", f);

         //showサーブレットへのリダイレクト
            response.sendRedirect(request.getContextPath() + "/reports/show");

        }


    }



}
