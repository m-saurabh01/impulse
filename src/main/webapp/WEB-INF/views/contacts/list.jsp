<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<layout:mailLayout pageTitle="Contacts">

<div class="contacts-container animate-fade-up">
    <div class="contacts-header">
        <h4><i class="bi bi-people-fill"></i> Contacts</h4>
        <button class="btn btn-primary btn-sm" onclick="showContactModal()">
            <i class="bi bi-plus-lg"></i> Add Contact
        </button>
    </div>
    
    <div class="contacts-search">
        <div class="search-wrapper">
            <i class="bi bi-search"></i>
            <input type="text" id="contactSearch" placeholder="Search contacts..." onkeyup="filterContacts(this.value)">
        </div>
    </div>
    
    <div class="contacts-list" id="contactsList">
        <c:choose>
            <c:when test="${empty contacts}">
                <div class="empty-contacts">
                    <i class="bi bi-people"></i>
                    <p>No contacts yet. Add your first contact to get started.</p>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach items="${contacts}" var="contact">
                    <div class="contact-item" data-contact-id="${contact.id}" 
                         data-search="${fn:toLowerCase(contact.email)} ${fn:toLowerCase(contact.displayName)}">
                        <div class="contact-avatar color-${(contact.id % 6) + 1}">
                            ${fn:toUpperCase(fn:substring(contact.email, 0, 2))}
                        </div>
                        <div class="contact-info">
                            <div class="contact-name">
                                <c:choose>
                                    <c:when test="${not empty contact.displayName}">${contact.displayName}</c:when>
                                    <c:otherwise>${contact.email}</c:otherwise>
                                </c:choose>
                                <c:if test="${contact.favorite}">
                                    <i class="bi bi-star-fill text-warning"></i>
                                </c:if>
                            </div>
                            <div class="contact-email">${contact.email}</div>
                            <c:if test="${not empty contact.company}">
                                <div class="contact-company"><i class="bi bi-building"></i> ${contact.company}</div>
                            </c:if>
                        </div>
                        <div class="contact-actions">
                            <button class="btn btn-sm btn-link" onclick="composeToContact('${contact.email}')" title="Send email">
                                <i class="bi bi-envelope"></i>
                            </button>
                            <button class="btn btn-sm btn-link" onclick="toggleFavoriteContact(${contact.id}, this)" title="Toggle favorite">
                                <i class="bi ${contact.favorite ? 'bi-star-fill text-warning' : 'bi-star'}"></i>
                            </button>
                            <button class="btn btn-sm btn-link" onclick="editContact(${contact.id})" title="Edit">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="btn btn-sm btn-link text-danger" onclick="deleteContact(${contact.id})" title="Delete">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Contact Modal -->
<div class="modal fade" id="contactModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="contactModalTitle">Add Contact</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="editContactId">
                <div class="mb-3">
                    <label class="form-label">Email Address *</label>
                    <input type="email" class="form-control" id="contactEmail" placeholder="email@example.com" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Display Name</label>
                    <input type="text" class="form-control" id="contactName" placeholder="John Doe">
                </div>
                <div class="mb-3">
                    <label class="form-label">Phone</label>
                    <input type="tel" class="form-control" id="contactPhone" placeholder="+1 234 567 8900">
                </div>
                <div class="mb-3">
                    <label class="form-label">Company</label>
                    <input type="text" class="form-control" id="contactCompany" placeholder="Company name">
                </div>
                <div class="mb-3">
                    <label class="form-label">Notes</label>
                    <textarea class="form-control" id="contactNotes" rows="3" placeholder="Additional notes..."></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="saveContact()">Save</button>
            </div>
        </div>
    </div>
</div>

<style>
.contacts-container {
    max-width: 800px;
    width: 100%;
    margin: 40px auto;
    padding: 20px 40px;
}

.contacts-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}

.contacts-header h4 {
    margin: 0;
    color: #1a1a2e;
}

.contacts-header h4 i {
    color: #6c5ce7;
    margin-right: 8px;
}

.contacts-search {
    margin-bottom: 20px;
}

.contacts-search .search-wrapper {
    position: relative;
}

.contacts-search .search-wrapper i {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #6c757d;
}

.contacts-search input {
    width: 100%;
    padding: 10px 12px 10px 36px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    font-size: 14px;
}

.contacts-search input:focus {
    outline: none;
    border-color: #6c5ce7;
    box-shadow: 0 0 0 3px rgba(108, 92, 231, 0.1);
}

.contacts-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.contact-item {
    display: flex;
    align-items: center;
    padding: 16px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    transition: all 0.2s;
}

.contact-item:hover {
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.contact-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    color: white;
    margin-right: 16px;
    flex-shrink: 0;
}

.contact-avatar.color-1 { background: linear-gradient(135deg, #6c5ce7, #5b4cdb); }
.contact-avatar.color-2 { background: linear-gradient(135deg, #00b894, #00a085); }
.contact-avatar.color-3 { background: linear-gradient(135deg, #0984e3, #0773c5); }
.contact-avatar.color-4 { background: linear-gradient(135deg, #e17055, #d35a3b); }
.contact-avatar.color-5 { background: linear-gradient(135deg, #fdcb6e, #f0b842); }
.contact-avatar.color-6 { background: linear-gradient(135deg, #e84393, #d63384); }

.contact-info {
    flex: 1;
    min-width: 0;
}

.contact-name {
    font-weight: 600;
    color: #1a1a2e;
    display: flex;
    align-items: center;
    gap: 6px;
}

.contact-email {
    color: #6c757d;
    font-size: 13px;
}

.contact-company {
    color: #6c757d;
    font-size: 12px;
    margin-top: 4px;
}

.contact-company i {
    margin-right: 4px;
}

.contact-actions {
    display: flex;
    gap: 4px;
}

.contact-actions .btn-link {
    padding: 6px 10px;
    color: #6c757d;
}

.contact-actions .btn-link:hover {
    color: #6c5ce7;
}

.empty-contacts {
    text-align: center;
    padding: 60px 20px;
    color: #6c757d;
}

.empty-contacts i {
    font-size: 64px;
    margin-bottom: 16px;
    opacity: 0.5;
}
</style>

<script>
var contactModal;

document.addEventListener('DOMContentLoaded', function() {
    contactModal = new bootstrap.Modal(document.getElementById('contactModal'));
});

function filterContacts(query) {
    query = query.toLowerCase();
    document.querySelectorAll('.contact-item').forEach(function(item) {
        var searchText = item.getAttribute('data-search') || '';
        item.style.display = searchText.includes(query) ? 'flex' : 'none';
    });
}

function showContactModal() {
    document.getElementById('contactModalTitle').textContent = 'Add Contact';
    document.getElementById('editContactId').value = '';
    document.getElementById('contactEmail').value = '';
    document.getElementById('contactName').value = '';
    document.getElementById('contactPhone').value = '';
    document.getElementById('contactCompany').value = '';
    document.getElementById('contactNotes').value = '';
    contactModal.show();
}

function editContact(id) {
    var headers = { "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + '/contacts/' + id, { headers: headers })
    .then(function(response) { return response.json(); })
    .then(function(contact) {
        document.getElementById('contactModalTitle').textContent = 'Edit Contact';
        document.getElementById('editContactId').value = contact.id;
        document.getElementById('contactEmail').value = contact.email;
        document.getElementById('contactName').value = contact.displayName;
        document.getElementById('contactPhone').value = contact.phone;
        document.getElementById('contactCompany').value = contact.company;
        document.getElementById('contactNotes').value = contact.notes;
        contactModal.show();
    });
}

function saveContact() {
    var email = document.getElementById('contactEmail').value.trim();
    var displayName = document.getElementById('contactName').value.trim();
    var phone = document.getElementById('contactPhone').value.trim();
    var company = document.getElementById('contactCompany').value.trim();
    var notes = document.getElementById('contactNotes').value.trim();
    var editId = document.getElementById('editContactId').value;
    
    if (!email) {
        alert('Please enter an email address');
        return;
    }
    
    var url = editId 
        ? contextPath + '/contacts/update'
        : contextPath + '/contacts/create';
    
    var params = 'email=' + encodeURIComponent(email);
    if (displayName) params += '&displayName=' + encodeURIComponent(displayName);
    if (phone) params += '&phone=' + encodeURIComponent(phone);
    if (company) params += '&company=' + encodeURIComponent(company);
    if (notes) params += '&notes=' + encodeURIComponent(notes);
    if (editId) params += '&contactId=' + editId;
    
    var headers = { "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(url, {
        method: 'POST',
        headers: headers,
        body: params
    })
    .then(function(response) { return response.json(); })
    .then(function(data) {
        if (data.error) {
            alert(data.error);
        } else {
            location.reload();
        }
    });
}

function deleteContact(id) {
    if (!confirm('Delete this contact?')) return;
    
    var headers = { "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + '/contacts/delete', {
        method: 'POST',
        headers: headers,
        body: 'contactId=' + id
    })
    .then(function(response) {
        if (response.ok) location.reload();
    });
}

function toggleFavoriteContact(id, btn) {
    var headers = { "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + '/contacts/toggleFavorite', {
        method: 'POST',
        headers: headers,
        body: 'contactId=' + id
    })
    .then(function(response) { return response.json(); })
    .then(function(isFavorite) {
        var icon = btn.querySelector('i');
        if (isFavorite) {
            icon.className = 'bi bi-star-fill text-warning';
        } else {
            icon.className = 'bi bi-star';
        }
    });
}

function composeToContact(email) {
    window.location.href = contextPath + '/mail/compose?to=' + encodeURIComponent(email);
}
</script>

</layout:mailLayout>
