<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:mailLayout pageTitle="Drafts">

<c:choose>
    <c:when test="${empty page.content}">
        <div class="empty-state d-flex flex-column
                    align-items-center justify-content-center
                    h-100 text-center animate-fade-up">
            <img src="${pageContext.request.contextPath}/assets/icons/mail.svg"
                 class="empty-icon mb-3"/>
            <h5>No drafts</h5>
            <p class="text-muted">Draft emails will appear here.</p>
        </div>
    </c:when>

    <c:otherwise>
        <div class="mail-list">
            <c:forEach items="${page.content}" var="email">
                <div class="mail-row">
                    <strong>${email.subject}</strong>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

</layout:mailLayout>
