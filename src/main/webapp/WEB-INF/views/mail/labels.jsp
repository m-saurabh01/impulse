<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:mailLayout pageTitle="Labels">

<div class="labels-container animate-fade-up">
    <div class="labels-header">
        <h4><i class="bi bi-tags-fill"></i> Manage Labels</h4>
        <button class="btn btn-primary btn-sm" onclick="showCreateLabelModal()">
            <i class="bi bi-plus-lg"></i> New Label
        </button>
    </div>
    
    <div class="labels-list">
        <c:choose>
            <c:when test="${empty labels}">
                <div class="empty-labels">
                    <i class="bi bi-tags"></i>
                    <p>No labels yet. Create your first label to organize emails.</p>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach items="${labels}" var="label">
                    <div class="label-item" data-label-id="${label.id}">
                        <div class="label-color" style="background-color: ${label.color};"></div>
                        <span class="label-name">${label.name}</span>
                        <div class="label-actions">
                            <button class="btn btn-sm btn-link" onclick="editLabel(${label.id}, '${label.name}', '${label.color}')" title="Edit">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="btn btn-sm btn-link text-danger" onclick="deleteLabel(${label.id})" title="Delete">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Create/Edit Label Modal -->
<div class="modal fade" id="labelModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="labelModalTitle">Create Label</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="editLabelId">
                <div class="mb-3">
                    <label class="form-label">Label Name</label>
                    <input type="text" class="form-control" id="labelName" placeholder="Enter label name">
                </div>
                <div class="mb-3">
                    <label class="form-label">Color</label>
                    <div class="color-picker">
                        <div class="color-option" data-color="#e74c3c" style="background: #e74c3c;" onclick="selectColor(this)"></div>
                        <div class="color-option" data-color="#e67e22" style="background: #e67e22;" onclick="selectColor(this)"></div>
                        <div class="color-option" data-color="#f1c40f" style="background: #f1c40f;" onclick="selectColor(this)"></div>
                        <div class="color-option" data-color="#2ecc71" style="background: #2ecc71;" onclick="selectColor(this)"></div>
                        <div class="color-option" data-color="#1abc9c" style="background: #1abc9c;" onclick="selectColor(this)"></div>
                        <div class="color-option" data-color="#3498db" style="background: #3498db;" onclick="selectColor(this)"></div>
                        <div class="color-option" data-color="#9b59b6" style="background: #9b59b6;" onclick="selectColor(this)"></div>
                        <div class="color-option" data-color="#6c5ce7" style="background: #6c5ce7;" onclick="selectColor(this)"></div>
                        <div class="color-option" data-color="#34495e" style="background: #34495e;" onclick="selectColor(this)"></div>
                        <div class="color-option" data-color="#95a5a6" style="background: #95a5a6;" onclick="selectColor(this)"></div>
                    </div>
                    <input type="hidden" id="labelColor" value="#6c5ce7">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="saveLabel()">Save</button>
            </div>
        </div>
    </div>
</div>

<style>
.labels-container {
    max-width: 800px;
    width: 100%;
    margin: 40px auto;
    padding: 24px;
    min-height: calc(100vh - 200px);
}

.labels-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #e0e0e0;
}

.labels-header h4 {
    margin: 0;
    color: #1a1a2e;
}

.labels-header h4 i {
    color: #6c5ce7;
    margin-right: 8px;
}

.labels-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.label-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.label-color {
    width: 16px;
    height: 16px;
    border-radius: 4px;
    margin-right: 12px;
}

.label-name {
    flex: 1;
    font-weight: 500;
}

.label-actions {
    display: flex;
    gap: 4px;
}

.label-actions .btn-link {
    padding: 4px 8px;
    color: #6c757d;
}

.label-actions .btn-link:hover {
    color: #6c5ce7;
}

.empty-labels {
    text-align: center;
    padding: 40px;
    color: #6c757d;
}

.empty-labels i {
    font-size: 48px;
    margin-bottom: 16px;
    opacity: 0.5;
}

.color-picker {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
}

.color-option {
    width: 32px;
    height: 32px;
    border-radius: 6px;
    cursor: pointer;
    border: 2px solid transparent;
    transition: all 0.2s;
}

.color-option:hover {
    transform: scale(1.1);
}

.color-option.selected {
    border-color: #1a1a2e;
    box-shadow: 0 0 0 2px white, 0 0 0 4px currentColor;
}
</style>

<script>
var selectedColor = '#6c5ce7';
var labelModal;

document.addEventListener('DOMContentLoaded', function() {
    labelModal = new bootstrap.Modal(document.getElementById('labelModal'));
    // Select first color by default
    document.querySelector('.color-option[data-color="#6c5ce7"]').classList.add('selected');
});

function selectColor(el) {
    document.querySelectorAll('.color-option').forEach(function(opt) {
        opt.classList.remove('selected');
    });
    el.classList.add('selected');
    selectedColor = el.getAttribute('data-color');
    document.getElementById('labelColor').value = selectedColor;
}

function showCreateLabelModal() {
    document.getElementById('labelModalTitle').textContent = 'Create Label';
    document.getElementById('editLabelId').value = '';
    document.getElementById('labelName').value = '';
    selectColor(document.querySelector('.color-option[data-color="#6c5ce7"]'));
    labelModal.show();
}

function editLabel(id, name, color) {
    document.getElementById('labelModalTitle').textContent = 'Edit Label';
    document.getElementById('editLabelId').value = id;
    document.getElementById('labelName').value = name;
    var colorEl = document.querySelector('.color-option[data-color="' + color + '"]');
    if (colorEl) selectColor(colorEl);
    labelModal.show();
}

function saveLabel() {
    var name = document.getElementById('labelName').value.trim();
    var color = document.getElementById('labelColor').value;
    var editId = document.getElementById('editLabelId').value;
    
    if (!name) {
        alert('Please enter a label name');
        return;
    }
    
    var url = editId 
        ? contextPath + '/mail/labels/update'
        : contextPath + '/mail/labels/create';
    
    var params = 'name=' + encodeURIComponent(name) + '&color=' + encodeURIComponent(color);
    if (editId) params += '&labelId=' + editId;
    
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

function deleteLabel(id) {
    if (!confirm('Delete this label? It will be removed from all emails.')) return;
    
    var headers = { "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + '/mail/labels/delete', {
        method: 'POST',
        headers: headers,
        body: 'labelId=' + id
    })
    .then(function(response) {
        if (response.ok) location.reload();
    });
}
</script>

</layout:mailLayout>
