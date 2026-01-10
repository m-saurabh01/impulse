<h5>${email.subject}</h5>

<div class="text-muted mb-2">
    From: ${email.sender.email}
</div>

<hr/>

<div class="mail-body">
    ${email.bodyHtml}
</div>

<c:if test="${not empty attachments}">
    <hr/>
    <h6>Attachments</h6>
    <c:forEach items="${attachments}" var="a">
        <div>
            <a href="${pageContext.request.contextPath}/attachments/${a.id}">
                ${a.originalFilename}
            </a>
        </div>
    </c:forEach>
</c:if>
