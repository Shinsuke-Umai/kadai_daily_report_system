<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
        <c:if test = "${hadError}">
            <div>
                社員番号かパスワードが間違っています。
            </div>
        </c:if>
        <c:if test = "${flush != null}">
            <div id = "flush_success">
                    <c:out value="${flush}"></c:out>
            </div>
        </c:if>
        <h2>ログイン</h2>
        <form method="POST" action="<c:url value='/login' />">
            <label for="code">社員番号</label><br />
            <input type="text" name="code" value="${code}" /> <%--社員番号は自動で入っている --%>
            <br /><br />

            <label for="password">パスワード</label><br />
            <input type="password" name="password" />
            <br /><br />

            <input type="hidden" name="_token" value="${_token}" /> <%--パスワードは毎回入力するため、valueで初期値を設定していない --%>
            <button type="submit">ログイン</button>
        </form>
    </c:param>
</c:import>