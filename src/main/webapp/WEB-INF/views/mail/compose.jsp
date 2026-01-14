<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:mailLayout pageTitle="Compose">

<script src="${pageContext.request.contextPath}/assets/tinymce/tinymce.min.js"></script>

<script>
tinymce.init({
    selector: '#bodyHtml',
    height: 400,
    menubar: false,
    plugins: 'lists link image table code paste',
    toolbar: 'undo redo | bold italic underline | forecolor backcolor | alignleft aligncenter alignright | bullist numlist | table link image | code',
    branding: false,
    promotion: false,
    skin: 'oxide',
    content_css: 'default'
});
</script>

<div class="compose-container">
    <form method="post" action="${pageContext.request.contextPath}/mail/send" 
          enctype="multipart/form-data" class="compose-form">
        
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        
        <c:if test="${not empty draft}">
            <input type="hidden" name="draftId" value="${draft.id}"/>
        </c:if>
        
        <div class="compose-field">
            <label class="compose-label">To</label>
            <input type="text" class="compose-input" name="to" 
                   placeholder="Recipients" value="${draft.toRecipients}">
        </div>
        
        <div class="compose-field">
            <label class="compose-label">Cc</label>
            <input type="text" class="compose-input" name="cc" 
                   placeholder="Carbon copy" value="${draft.ccRecipients}">
        </div>
        
        <div class="compose-field">
            <label class="compose-label">Bcc</label>
            <input type="text" class="compose-input" name="bcc" 
                   placeholder="Blind carbon copy" value="${draft.bccRecipients}">
        </div>
        
        <div class="compose-field">
            <label class="compose-label">Subject</label>
            <input type="text" class="compose-input subject-input" name="subject" 
                   placeholder="Subject" value="${draft.subject}">
        </div>
        
        <div class="compose-body">
            <textarea id="bodyHtml" name="bodyHtml">${draft.bodyHtml}</textarea>
        </div>
        
        <div class="compose-attachments">
            <label class="attach-label">
                <svg class="attach-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="m21.44 11.05-9.19 9.19a6 6 0 0 1-8.49-8.49l8.57-8.57A4 4 0 1 1 18 8.84l-8.59 8.57a2 2 0 0 1-2.83-2.83l8.49-8.48"></path>
                </svg>
                <span>Attach files</span>
                <input type="file" name="attachments" multiple style="display: none;">
            </label>
            <div id="fileList" class="file-list"></div>
        </div>
        
        <div class="compose-actions">
            <button type="submit" class="btn btn-primary btn-send">
                <svg class="send-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="m22 2-7 20-4-9-9-4Z"></path>
                    <path d="M22 2 11 13"></path>
                </svg>
                Send
            </button>
            <button type="submit" name="draft" value="true" class="btn btn-outline-secondary">
                Save Draft
            </button>
            <c:choose>
                <c:when test="${not empty draft}">
                    <button type="button" class="btn btn-outline-danger" onclick="discardDraft(${draft.id})">
                        Discard
                    </button>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/mail/inbox" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </form>
</div>

<c:if test="${not empty draft}">
<form id="discardForm" method="post" action="${pageContext.request.contextPath}/mail/discard" style="display:none;">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="hidden" name="id" value="${draft.id}"/>
</form>
</c:if>

<script>
function discardDraft(draftId) {
    if (confirm('Are you sure you want to discard this draft?')) {
        document.getElementById('discardForm').submit();
    }
}

document.querySelector('input[name="attachments"]').addEventListener('change', function(e) {
    var fileList = document.getElementById('fileList');
    fileList.innerHTML = '';
    for (var i = 0; i < this.files.length; i++) {
        var file = this.files[i];
        var div = document.createElement('div');
        div.textContent = file.name + ' (' + Math.round(file.size / 1024) + ' KB)';
        fileList.appendChild(div);
    }
});
</script>

</layout:mailLayout>
