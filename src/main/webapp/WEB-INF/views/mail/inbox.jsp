<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:mailLayout pageTitle="Inbox">

<c:choose>

    
    <c:when test="${empty page.content}">
        <div class="empty-state d-flex flex-column
            align-items-center justify-content-center
            h-100 text-center animate-fade-up">

    <img src="${pageContext.request.contextPath}/assets/icons/inbox.svg"
         class="empty-icon mb-3"/>

    <h5>Your inbox is empty</h5>
    <p class="text-muted">New emails will appear here.</p>

    <a href="${pageContext.request.contextPath}/mail/compose"
       class="btn btn-primary mt-2">
        Compose your first email
    </a>
</div>

    </c:when>

    
    <c:otherwise>
        <div class="mail-list">
            <c:forEach items="${page.content}" var="r">
                <div class="mail-row"
                     onclick="loadPreview(${r.email.id})">
                    <strong>${r.email.sender.email}</strong>
                    <div class="subject">${r.email.subject}</div>
                </div>
            </c:forEach>
        </div>
        
        <div id="previewPane"
         class="preview-pane flex-grow-2 p-3">
       
    </div>
    </c:otherwise>

</c:choose>

</layout:mailLayout>
