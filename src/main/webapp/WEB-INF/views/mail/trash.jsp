<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:mailLayout pageTitle="Trash">

<c:choose>
    <c:when test="${empty page.content}">
        <div class="empty-state d-flex flex-column
                    align-items-center justify-content-center
                    h-100 text-center animate-fade-up">
            <img src="${pageContext.request.contextPath}/assets/icons/trash.svg"
                 class="empty-icon mb-3"/>
            <h5>Trash is empty</h5>
            <p class="text-muted">Deleted emails will appear here.</p>
        </div>
    </c:when>

    <c:otherwise>
        <div class="mail-list">
            <c:forEach items="${page.content}" var="r">
                <div class="mail-row">
                    <strong>${r.email.subject}</strong>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

</layout:mailLayout>
