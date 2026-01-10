function loadPreview(emailId) {
    fetch(`${contextPath}/mail/preview?id=` + emailId, {
        headers: { "X-Requested-With": "XMLHttpRequest" }
    })
    .then(r => {
        if (!r.ok) throw new Error("Failed to load preview");
        return r.text();
    })
    .then(html => {
        document.getElementById("previewPane").innerHTML = html;
    })
    .catch(err => {
        console.error(err);
        document.getElementById("previewPane").innerHTML =
            "<div class='text-danger'>Unable to load email</div>";
    });
}
