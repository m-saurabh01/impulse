<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:mailLayout pageTitle="Sent">

<c:choose>
    <c:when test="${empty page.content}">
        <div class="empty-state d-flex flex-column
                    align-items-center justify-content-center
                    h-100 text-center animate-fade-up">
            <img src="${pageContext.request.contextPath}/assets/icons/send.svg"
                 class="empty-icon mb-3"/>
            <h5>No sent mail</h5>
            <p class="text-muted">Emails you send will appear here.</p>
        </div>
    </c:when>

    <c:otherwise>
        <div class="mail-list">
            <c:forEach items="${page.content}" var="email">
                <div class="mail-row">
                    <strong>To:</strong>
                    ${email.subject}
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

</layout:mailLayout>
