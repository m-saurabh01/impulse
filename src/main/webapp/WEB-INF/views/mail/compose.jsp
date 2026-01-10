<jsp:include page="../layout/header.jsp"/>

<script src="${pageContext.request.contextPath}/assets/tinymce/tinymce.min.js"></script>

<script>
tinymce.init({
  selector: '#bodyHtml',
  height: 400,
  menubar: true,
  plugins: 'lists link image table code paste',
  toolbar:
    'undo redo | bold italic underline | forecolor backcolor | ' +
    'alignleft aligncenter alignright | bullist numlist | ' +
    'table link image | code',
  branding: false,
  promotion: false
});
</script>


<form method="post" action="/mail/send" enctype="multipart/form-data"
      class="container mt-3">

    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <input class="form-control mb-2" name="to" placeholder="To">
    <input class="form-control mb-2" name="cc" placeholder="CC">
    <input class="form-control mb-2" name="bcc" placeholder="BCC">
    <input class="form-control mb-2" name="subject" placeholder="Subject">

    <textarea id="bodyHtml" name="bodyHtml"></textarea>


    <input type="file" name="attachments" multiple>

    <div class="mt-3">
        <button class="btn btn-primary">Send</button>
        <button class="btn btn-secondary" name="draft" value="true">
            Save Draft
        </button>
    </div>
</form>
