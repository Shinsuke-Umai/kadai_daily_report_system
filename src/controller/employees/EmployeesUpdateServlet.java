package controller.employees;

import java.io.IOException;
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
import models.validators.EmployeeValidator;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class EmployeesUpdateServlet
 */
@WebServlet("/employees/update")
public class EmployeesUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesUpdateServlet() {
        super();

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //セッションのgetId()である_tokenと、リクエストスコープの_tokenが正しく同じものかどうかを、変数を用いてチェック。
        //newサーブレットでリクエストコープに格納した_tokenをgetParameterで取り出して、比較している。
        //そしてそれがnullでなく、セッションで作られたgetId()と同じか比較している。
        //同じであれば、データベースを繋ぐ。
        String _token = (String)request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();

            //editサーブレットからセッションに格納し、そのセッションから繋がっているからemployee_idを使う
            Employee e = em.find(Employee.class, (Integer)(request.getSession().getAttribute("employee_id")));

            // 現在の値と異なる社員番号が入力されていたら
            // 重複チェックを行う指定をする
            Boolean codeDuplicateCheckFlag = true;
            if(e.getCode().equals(request.getParameter("code"))) { //createサーブレットで作ったコードと(今のコード)と同じかどうか
                codeDuplicateCheckFlag = false;  //更新は名前は違っていなければならないから、初期値trueの反対になる
            } else {
                e.setCode(request.getParameter("code")); //新しくセットしたいコード
            }

         // パスワード欄に入力があったら
            // パスワードの入力値チェックを行う指定をする
            Boolean passwordCheckFlag = true;
            String password = request.getParameter("password");
            if(password == null || password.equals("")) { //パスワードが空白ならエラーなし
                passwordCheckFlag = false;
            } else { //それ以外は（何か書いてあれば)、新しくセットする
                e.setPassword(
                        EncryptUtil.getPasswordEncrypt(
                                password,
                                (String)this.getServletContext().getAttribute("pepper")
                                )
                        );
            }

            //eのオブジェクトに実際に追加する項目(更新)
            e.setName(request.getParameter("name"));
            e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));
            e.setUpdated_at(new Timestamp(System.currentTimeMillis()));
            e.setDelete_flag(0);

            //EmployeeValidateクラスのバリデートメソッドを利用している
            List<String> errors = EmployeeValidator.validate(e, codeDuplicateCheckFlag, passwordCheckFlag);
            if(errors.size() > 0) { //エラーがあれば
                em.close();
                //エラーを表示し、
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("employee", e);
                request.setAttribute("errors", errors);
                //edit画面に戻る
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/edit.jsp");
                rd.forward(request, response);
            } else { //エラーがなければ更新
                em.getTransaction().begin();
                em.getTransaction().commit();
                em.close();
                request.getSession().setAttribute("flush", "更新が完了しました。");

                request.getSession().removeAttribute("employee_id");

                response.sendRedirect(request.getContextPath() + "/employees/index");
            }
        }
    }
}
