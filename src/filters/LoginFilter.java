package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter("/*")
public class LoginFilter implements Filter {

    /**
     * Default constructor.
     */
    public LoginFilter() {

    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {

    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String context_path = ((HttpServletRequest)request).getContextPath();//"/"の含まれるページへのアクセス
        String servlet_path = ((HttpServletRequest)request).getServletPath();//サーブレットへのアクセス

        if(!servlet_path.matches("/css.*")) {  //cssフォルダ内は認証処理から除外する。そしてセッションを取得
            HttpSession session = ((HttpServletRequest)request).getSession();

        //セッションスコープに保存された従業員(ログインユーザー)情報を取得//ログインしている状態の変数e
            Employee e = (Employee)session.getAttribute("login_employee");

            if(!servlet_path.equals("/login")) {  //ログイン画面以外について
                //ログアウトしている状態であれあば
                //ログイン画面にリダイレクト
                if(e == null) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/login");
                    return;//returnは何
                }

                //従業員管理の機能(/employee)は管理者のみが観覧できるようにする
                if(servlet_path.matches("/employees.*") && e.getAdmin_flag() == 0) {
                    //employee(従業員管理)のページにアクセスした状態で、一般従業員(0)の場合は、トップページへリダイレクト
                    ((HttpServletResponse)response).sendRedirect(context_path + "/"); //"/"はトップページへリダイレクト
                    return;
                }
            } else { //ログイン画面について
                //ログインしているのにログイン画面を表示させようとした場合は
                //システムのトップページにリダイレクト(ようこそ画面)
                if(e != null) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/");
                    return;
                }

            }

        }


        //これより前に処理を書くことで、サーブレットを実行する前にフィルタの処理を実行
        chain.doFilter(request, response);
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {

    }

}
