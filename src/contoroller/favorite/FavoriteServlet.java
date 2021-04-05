package contoroller.favorite;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

            Favorite f = new Favorite();//いいねしたかどうかの変数

            //ログインユーザーの確認
            HttpSession session = ((HttpServletRequest)request).getSession();

            //ログインしている従業員・初期値はログインしている人
            Employee e = (Employee)session.getAttribute("login_employee");
            f.setEmployee(e); //mysqlに登録する側なので、set。getは違う。

            //どのレポートか。idで判断する。//show.jspのid=reportクラスのid
            //int id = Integer.parseInt(request.getParameter("id"));
            //idを元に１つのreportのレコード取得(reportオブジェクト)//report.javaからidを元にfindでオブジェクトを検索し、変数rとし、オブジェクト化
            Report r = em.find(Report.class, Integer.parseInt(request.getParameter("report_id")));
            //いらないr.setId(id);//show.jspのidと、report.javaのidとしてセット
            f.setReport(r);

            try {
            //DBにinsertまたはdelete
            String iine_color = request.getParameter("iine");//jspのボタン

            if("black".equals(iine_color)){ //黒が押されていれば登録
            em.getTransaction().begin();
            em.persist(f);
            em.getTransaction().commit();

            } else {//赤が押されていれば削除

         //DBからdelete
            em.getTransaction().begin();
            em.createNamedQuery("deleteFavorites")
              .setParameter("employee", e) //作った変数eをemployeeとし、Favoriteクラスのdelete文の:emloyeeと紐づける。
              .setParameter("report", r) //作った変数rをFavoriteクラスのdelete文の変数reportと紐づける。
              .executeUpdate();//executeUpdateはint型
            }
            } catch(NoResultException ex) {
            }

            em.close();
            }


         //showSerletへのリダイレクトjspではない
            response.sendRedirect(request.getContextPath() + "/reports/show?id=" + (request.getParameter("report_id")));

    }
}
