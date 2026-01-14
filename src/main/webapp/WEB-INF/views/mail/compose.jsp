<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Compose">

<script src="${pageContext.request.contextPath}/assets/tinymce/tinymce.min.js"></script>

<%-- Store signature in a hidden element to avoid JS escaping issues --%>
<div id="userSignatureData" style="display:none;"><c:out value="${signature}" escapeXml="true"/></div>

<%-- Store reply data in hidden elements --%>
<c:if test="${not empty replyTo}">
    <div id="replyToData" style="display:none;"><c:out value="${replyTo}" escapeXml="true"/></div>
</c:if>
<c:if test="${not empty replyCc}">
    <div id="replyCcData" style="display:none;"><c:out value="${replyCc}" escapeXml="true"/></div>
</c:if>
<c:if test="${not empty replyBody}">
    <div id="replyBodyData" style="display:none;">${replyBody}</div>
</c:if>

<%-- Store forward data in hidden elements --%>
<c:if test="${not empty forwardBody}">
    <div id="forwardBodyData" style="display:none;">${forwardBody}</div>
</c:if>

<%-- Store draft recipient data in hidden elements --%>
<c:if test="${not empty draft}">
    <c:if test="${not empty draft.toRecipients}">
        <div id="draftToData" style="display:none;"><c:out value="${draft.toRecipients}" escapeXml="true"/></div>
    </c:if>
    <c:if test="${not empty draft.ccRecipients}">
        <div id="draftCcData" style="display:none;"><c:out value="${draft.ccRecipients}" escapeXml="true"/></div>
    </c:if>
    <c:if test="${not empty draft.bccRecipients}">
        <div id="draftBccData" style="display:none;"><c:out value="${draft.bccRecipients}" escapeXml="true"/></div>
    </c:if>
</c:if>

<%-- Store direct compose data (from feedback reply, etc.) --%>
<c:if test="${not empty directTo}">
    <div id="directToData" style="display:none;"><c:out value="${directTo}" escapeXml="true"/></div>
</c:if>
<c:if test="${not empty directSubject}">
    <div id="directSubjectData" style="display:none;"><c:out value="${directSubject}" escapeXml="true"/></div>
</c:if>
<c:if test="${not empty directBody}">
    <div id="directBodyData" style="display:none;">${directBody}</div>
</c:if>

<script>
document.addEventListener('DOMContentLoaded', function() {
    var signatureEl = document.getElementById('userSignatureData');
    var userSignature = signatureEl ? signatureEl.textContent.trim() : '';
    
    var replyBodyEl = document.getElementById('replyBodyData');
    var replyBodyContent = replyBodyEl ? replyBodyEl.innerHTML.trim() : '';
    
    var forwardBodyEl = document.getElementById('forwardBodyData');
    var forwardBodyContent = forwardBodyEl ? forwardBodyEl.innerHTML.trim() : '';
    
    var directBodyEl = document.getElementById('directBodyData');
    var directBodyContent = directBodyEl ? directBodyEl.innerHTML.trim() : '';
    
    if (typeof tinymce !== 'undefined') {
        tinymce.init({
            selector: '#bodyHtml',
            height: 400,
            menubar: false,
            plugins: 'lists link image table code paste',
            toolbar: 'undo redo | bold italic underline | forecolor backcolor | alignleft aligncenter alignright | bullist numlist | table link image | code',
            branding: false,
            promotion: false,
            skin: 'oxide',
            content_css: 'default',
            setup: function(editor) {
                editor.on('init', function() {
                    // Check if editing draft - don't override existing content
                    var existingContent = editor.getContent();
                    if (existingContent) return;
                    
                    var content = '';
                    
                    // Add cursor position at top
                    content += '<p><br></p>';
                    
                    // Add signature if exists
                    if (userSignature) {
                        content += '<p><br></p><p>--</p><p>' + userSignature.replace(/\n/g, '<br>') + '</p>';
                    }
                    
                    // Add quoted reply content
                    if (replyBodyContent) {
                        content += replyBodyContent;
                    }
                    
                    // Add forwarded message content
                    if (forwardBodyContent) {
                        content += forwardBodyContent;
                    }
                    
                    // Add direct body content (from feedback reply, etc.)
                    if (directBodyContent) {
                        content += directBodyContent;
                    }
                    
                    editor.setContent(content);
                    editor.selection.setCursorLocation(editor.getBody(), 0);
                });
            }
        });
    } else {
        console.error('TinyMCE not loaded');
    }
});
</script>

<div class="compose-container">
    <!-- Error/Success Messages -->
    <c:if test="${not empty error}">
        <div class="compose-alert alert-danger">
            <i class="bi bi-exclamation-circle"></i>
            ${error}
        </div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="compose-alert alert-success">
            <i class="bi bi-check-circle"></i>
            ${success}
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/mail/send" 
          enctype="multipart/form-data" class="compose-form" id="composeForm">
        
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        
        <c:if test="${not empty draft}">
            <input type="hidden" name="draftId" value="${draft.id}"/>
        </c:if>
        
        <div class="compose-field">
            <label class="compose-label">To</label>
            <div class="email-tags-container" id="toContainer">
                <div class="email-tags" id="toTags"></div>
                <input type="text" class="email-tag-input" id="toInput" 
                       placeholder="Add recipients (press Enter or comma to add)">
            </div>
            <input type="hidden" name="to" id="toHidden">
        </div>
        
        <div class="compose-field">
            <label class="compose-label">Cc</label>
            <div class="email-tags-container" id="ccContainer">
                <div class="email-tags" id="ccTags"></div>
                <input type="text" class="email-tag-input" id="ccInput" 
                       placeholder="Carbon copy">
            </div>
            <input type="hidden" name="cc" id="ccHidden">
        </div>
        
        <div class="compose-field">
            <label class="compose-label">Bcc</label>
            <div class="email-tags-container" id="bccContainer">
                <div class="email-tags" id="bccTags"></div>
                <input type="text" class="email-tag-input" id="bccInput" 
                       placeholder="Blind carbon copy">
            </div>
            <input type="hidden" name="bcc" id="bccHidden">
        </div>
        
        <div class="compose-field">
            <label class="compose-label">Subject</label>
            <input type="text" class="compose-input subject-input" name="subject" 
                   placeholder="Subject" value="${not empty replySubject ? replySubject : (not empty forwardSubject ? forwardSubject : draft.subject)}">
        </div>
        
        <%-- Thread ID for conversation threading --%>
        <c:if test="${not empty replyThreadId}">
            <input type="hidden" name="threadId" value="${replyThreadId}"/>
        </c:if>
        
        <div class="compose-body">
            <textarea id="bodyHtml" name="bodyHtml">${draft.bodyHtml}</textarea>
        </div>
        
        <div class="compose-options">
            <label class="compose-option-label">
                <input type="checkbox" name="readReceiptRequested" value="true" 
                       class="compose-option-checkbox"
                       ${draft.readReceiptRequested ? 'checked' : ''}>
                <span class="compose-option-check">
                    <i class="bi bi-check"></i>
                </span>
                <span class="compose-option-text">
                    <i class="bi bi-envelope-check"></i>
                    Request read receipt
                </span>
            </label>
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
            <button type="submit" class="btn btn-primary btn-send" onclick="prepareSubmit()">
                <svg class="send-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="m22 2-7 20-4-9-9-4Z"></path>
                    <path d="M22 2 11 13"></path>
                </svg>
                Send
            </button>
            <button type="submit" name="draft" value="true" class="btn btn-outline-secondary" onclick="prepareSubmit()">
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
// Email Tags System
var emailTagsData = {
    to: [],
    cc: [],
    bcc: []
};

var contactsCache = [];
var autocompleteDebounce = null;

function initEmailTags() {
    ['to', 'cc', 'bcc'].forEach(function(type) {
        var input = document.getElementById(type + 'Input');
        var container = document.getElementById(type + 'Container');
        
        // Create autocomplete dropdown
        var autocomplete = document.createElement('div');
        autocomplete.className = 'contacts-autocomplete';
        autocomplete.id = type + 'Autocomplete';
        container.appendChild(autocomplete);
        
        input.addEventListener('keydown', function(e) {
            var autocompleteEl = document.getElementById(type + 'Autocomplete');
            var items = autocompleteEl.querySelectorAll('.autocomplete-item');
            var activeItem = autocompleteEl.querySelector('.autocomplete-item.active');
            
            if (e.key === 'ArrowDown' && items.length > 0) {
                e.preventDefault();
                if (activeItem) {
                    activeItem.classList.remove('active');
                    var next = activeItem.nextElementSibling || items[0];
                    next.classList.add('active');
                } else {
                    items[0].classList.add('active');
                }
                return;
            }
            
            if (e.key === 'ArrowUp' && items.length > 0) {
                e.preventDefault();
                if (activeItem) {
                    activeItem.classList.remove('active');
                    var prev = activeItem.previousElementSibling || items[items.length - 1];
                    prev.classList.add('active');
                }
                return;
            }
            
            if ((e.key === 'Enter' || e.key === 'Tab') && activeItem) {
                e.preventDefault();
                activeItem.click();
                return;
            }
            
            if (e.key === 'Enter' || e.key === ',' || e.key === 'Tab') {
                e.preventDefault();
                addEmailTag(type, this.value);
                this.value = '';
                hideAutocomplete(type);
            }
            if (e.key === 'Backspace' && this.value === '' && emailTagsData[type].length > 0) {
                removeEmailTag(type, emailTagsData[type].length - 1);
            }
            if (e.key === 'Escape') {
                hideAutocomplete(type);
            }
        });
        
        input.addEventListener('input', function() {
            var query = this.value.trim();
            if (query.length >= 1) {
                searchContacts(type, query);
            } else {
                hideAutocomplete(type);
            }
        });
        
        input.addEventListener('blur', function() {
            // Delay to allow click on autocomplete item
            setTimeout(function() {
                hideAutocomplete(type);
                var inputEl = document.getElementById(type + 'Input');
                if (inputEl.value.trim()) {
                    addEmailTag(type, inputEl.value);
                    inputEl.value = '';
                }
            }, 200);
        });
        
        input.addEventListener('paste', function(e) {
            e.preventDefault();
            var pastedText = (e.clipboardData || window.clipboardData).getData('text');
            var emails = pastedText.split(/[,;\s]+/);
            emails.forEach(function(email) {
                addEmailTag(type, email);
            });
        });
        
        container.addEventListener('click', function() {
            input.focus();
        });
    });
}

function searchContacts(type, query) {
    if (autocompleteDebounce) clearTimeout(autocompleteDebounce);
    
    autocompleteDebounce = setTimeout(function() {
        fetch(contextPath + '/contacts/search?q=' + encodeURIComponent(query))
            .then(function(res) { return res.json(); })
            .then(function(contacts) {
                showAutocomplete(type, contacts);
            })
            .catch(function(err) {
                console.error('Contact search failed:', err);
            });
    }, 150);
}

function showAutocomplete(type, contacts) {
    var autocomplete = document.getElementById(type + 'Autocomplete');
    autocomplete.innerHTML = '';
    
    if (contacts.length === 0) {
        hideAutocomplete(type);
        return;
    }
    
    contacts.forEach(function(contact) {
        var initials = contact.email.substring(0, 2).toUpperCase();
        var displayName = contact.displayName || contact.email.split('@')[0];
        
        var item = document.createElement('div');
        item.className = 'autocomplete-item';
        item.innerHTML = '<div class="autocomplete-avatar">' + initials + '</div>' +
                         '<div class="autocomplete-info">' +
                         '<span class="autocomplete-name">' + displayName + '</span>' +
                         '<span class="autocomplete-email">' + contact.email + '</span>' +
                         '</div>';
        item.onclick = function() {
            addEmailTag(type, contact.email);
            document.getElementById(type + 'Input').value = '';
            hideAutocomplete(type);
        };
        autocomplete.appendChild(item);
    });
    
    autocomplete.style.display = 'block';
}

function hideAutocomplete(type) {
    var autocomplete = document.getElementById(type + 'Autocomplete');
    if (autocomplete) {
        autocomplete.style.display = 'none';
    }
}

function addEmailTag(type, email) {
    email = email.trim().replace(/,/g, '');
    if (!email) return;
    
    // Validate email format
    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showTagError(type, 'Invalid email: ' + email);
        return;
    }
    
    // Check for duplicates
    if (emailTagsData[type].indexOf(email) !== -1) {
        return;
    }
    
    emailTagsData[type].push(email);
    renderTags(type);
}

function removeEmailTag(type, index) {
    emailTagsData[type].splice(index, 1);
    renderTags(type);
}

function renderTags(type) {
    var container = document.getElementById(type + 'Tags');
    container.innerHTML = '';
    
    emailTagsData[type].forEach(function(email, index) {
        var tag = document.createElement('span');
        tag.className = 'email-tag';
        tag.innerHTML = email + '<button type="button" class="tag-remove" onclick="removeEmailTag(\'' + type + '\', ' + index + ')">&times;</button>';
        container.appendChild(tag);
    });
}

function showTagError(type, message) {
    var input = document.getElementById(type + 'Input');
    input.classList.add('input-error');
    setTimeout(function() {
        input.classList.remove('input-error');
    }, 2000);
}

function prepareSubmit() {
    // Convert arrays to comma-separated strings for form submission
    document.getElementById('toHidden').value = emailTagsData.to.join(',');
    document.getElementById('ccHidden').value = emailTagsData.cc.join(',');
    document.getElementById('bccHidden').value = emailTagsData.bcc.join(',');
}

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

// Initialize on load
document.addEventListener('DOMContentLoaded', function() {
    initEmailTags();
    
    // Helper function to load emails into tags
    function loadEmailsIntoTags(elementId, tagType) {
        var el = document.getElementById(elementId);
        if (el) {
            var emails = el.textContent.trim();
            if (emails) {
                emails.split(',').forEach(function(email) {
                    email = email.trim();
                    if (email && emailTagsData[tagType].indexOf(email) === -1) {
                        emailTagsData[tagType].push(email);
                    }
                });
                renderTags(tagType);
            }
        }
    }
    
    // Load draft recipients (if editing a draft)
    loadEmailsIntoTags('draftToData', 'to');
    loadEmailsIntoTags('draftCcData', 'cc');
    loadEmailsIntoTags('draftBccData', 'bcc');
    
    // Pre-populate reply recipients (overrides draft if both exist)
    loadEmailsIntoTags('replyToData', 'to');
    loadEmailsIntoTags('replyCcData', 'cc');
    
    // Pre-populate direct compose data (from feedback reply, etc.)
    loadEmailsIntoTags('directToData', 'to');
    
    // Set direct subject if provided
    var directSubjectEl = document.getElementById('directSubjectData');
    if (directSubjectEl && directSubjectEl.textContent.trim()) {
        var subjectInput = document.getElementById('subjectInput');
        if (subjectInput && !subjectInput.value) {
            subjectInput.value = directSubjectEl.textContent.trim();
        }
    }
    
    // Set direct body if provided (for feedback reply context)
    var directBodyEl = document.getElementById('directBodyData');
    if (directBodyEl && directBodyEl.innerHTML.trim()) {
        // Will be handled by TinyMCE init
        window.directBodyContent = directBodyEl.innerHTML.trim();
    }
});
</script>

</layout:mailLayout>
