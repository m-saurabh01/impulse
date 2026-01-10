<aside class="sidebar d-flex flex-column p-3">
    <nav class="nav nav-pills flex-column gap-1">

        <a class="nav-link d-flex align-items-center"
           href="${pageContext.request.contextPath}/mail/inbox">
            <img src="${pageContext.request.contextPath}/assets/icons/inbox.svg" class="icon"/>
            Inbox
        </a>

        <a class="nav-link d-flex align-items-center"
           href="${pageContext.request.contextPath}/mail/sent">
            <img src="${pageContext.request.contextPath}/assets/icons/send.svg" class="icon"/>
            Sent
        </a>

        <a class="nav-link d-flex align-items-center"
           href="${pageContext.request.contextPath}/mail/drafts">
            <img src="${pageContext.request.contextPath}/assets/icons/mail.svg" class="icon"/>
            Drafts
        </a>

        <a class="nav-link d-flex align-items-center"
           href="${pageContext.request.contextPath}/mail/trash">
            <img src="${pageContext.request.contextPath}/assets/icons/trash.svg" class="icon"/>
            Trash
        </a>

    </nav>
</aside>
