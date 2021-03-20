package controllers.login;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //doGetはログイン画面
        request.setAttribute("_token", request.getSession().getId());
        request.setAttribute("hasError", false);//falseであればエラーの状態
        if(request.getSession().getAttribute("flush") != null) {
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
        rd.forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //doPostはログイン処理を実行
        //認証結果を格納する変数(trueだったらログインできるという状態にするため、初期値はfalse)
        Boolean check_result = false;

        //Emloyeeクラスのcodeとpassword
        String code = request.getParameter("code");
        String plain_pass = request.getParameter("password");

        Employee e = null; //初期値はnullに設定し、ログイン情報を入れる前の状態にする

        if(code != null && code.equals("") && plain_pass != null && plain_pass.equals("")) {
            EntityManager em = DBUtil.createEntityManager();
                String password = EncryptUtil.getPasswordEncrypt(
                        plain_pass,
                        (String)this.getServletContext().getAttribute("pepper")
                        );
                //社員番号とパスワードが正しいかチェックする
                try {
                    e = em.createNamedQuery("checkLoginCodeAndPassword", Employee.class)
                          .setParameter("code", code)
                          .setParameter("pass", password)
                          .getSingleResult();
                } catch(NoResultException ex){}

                em.close();

                if(e != null) {
                    check_result = true; //社員番号とパスワードが正しければ、trueに上書きする。(trueであればログインできる)
                }
            }
        if(!check_result) {
            //認証できなかったらログイン画面に戻る//jspに送る
            request.setAttribute("_token", request.getSession().getId());
            request.setAttribute("hasError", true);
            request.setAttribute("code", code);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
            rd.forward(request, response);
        } else {
            //認証できたらログイン状態にしてトップページへリダイレクト
            request.getSession().setAttribute("login_employee", e);

            request.getSession().setAttribute("flush", "ログインしました");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

}
