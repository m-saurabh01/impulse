/**
 * Pulse Mail - JavaScript Functions
 * Outlook-style email client functionality
 */

// Track currently selected email
var selectedEmailId = null;
var currentSource = 'inbox';

/**
 * Select an email and load its preview
 */
function selectEmail(element, emailId, source) {
    // Remove selection from all rows
    var allRows = document.querySelectorAll('.mail-row');
    for (var i = 0; i < allRows.length; i++) {
        allRows[i].classList.remove('selected');
    }
    
    // Add selection to clicked row
    element.classList.add('selected');
    selectedEmailId = emailId;
    currentSource = source || 'inbox';
    
    // Load preview
    loadPreview(emailId, currentSource);
    
    // Mark as read (remove unread class) and persist to backend
    if (element.classList.contains('unread')) {
        element.classList.remove('unread');
        // Hide the unread dot
        var dot = element.querySelector('.unread-dot');
        if (dot) dot.style.display = 'none';
        // Persist read status to backend (with CSRF token)
        var headers = { "X-Requested-With": "XMLHttpRequest" };
        if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
            headers[csrfHeader] = csrfToken;
        }
        fetch(contextPath + "/mail/markAsRead?id=" + emailId, {
            method: "POST",
            headers: headers
        }).catch(function(err) {
            console.error('Failed to mark as read:', err);
        });
    }
}

/**
 * Load email preview via AJAX
 */
function loadPreview(emailId, source) {
    var previewPane = document.getElementById("previewPane");
    if (!previewPane) return;
    
    source = source || 'inbox';
    
    // Show loading state
    previewPane.innerHTML = '<div class="preview-empty"><div class="spinner"></div><p>Loading...</p></div>';
    
    fetch(contextPath + "/mail/preview?id=" + emailId + "&source=" + source, {
        headers: { 
            "X-Requested-With": "XMLHttpRequest"
        }
    })
    .then(function(response) {
        if (!response.ok) throw new Error("Failed to load preview");
        return response.text();
    })
    .then(function(html) {
        previewPane.innerHTML = '<div class="animate-fade-in">' + html + '</div>';
    })
    .catch(function(err) {
        console.error(err);
        previewPane.innerHTML = 
            '<div class="preview-empty">' +
            '<i class="bi bi-exclamation-circle"></i>' +
            '<h5>Unable to load email</h5>' +
            '<p>Please try again later</p>' +
            '</div>';
    });
}

/**
 * Delete email
 */
function deleteEmail(emailId) {
    if (!confirm('Move this email to trash?')) return;
    
    fetch(contextPath + "/mail/delete/" + emailId, {
        method: "POST",
        headers: {
            "X-Requested-With": "XMLHttpRequest",
            "Content-Type": "application/json"
        }
    })
    .then(function(response) {
        if (response.ok) {
            // Remove from list
            var row = document.querySelector('[data-email-id="' + emailId + '"]');
            if (row) {
                row.style.animation = 'fadeOut 0.2s ease';
                setTimeout(function() {
                    row.remove();
                }, 200);
            }
            // Clear preview
            var previewPane = document.getElementById("previewPane");
            if (previewPane) {
                previewPane.innerHTML = getEmptyPreviewHtml();
            }
        }
    })
    .catch(function(err) {
        console.error('Delete failed:', err);
    });
}

/**
 * Move email to trash
 */
function moveToTrash(emailId) {
    if (!confirm('Move this email to trash?')) return;
    
    var headers = { "X-Requested-With": "XMLHttpRequest" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/moveToTrash?emailId=" + emailId, {
        method: "POST",
        headers: headers
    })
    .then(function(response) {
        if (response.ok) {
            // Remove from list
            var row = document.querySelector('[data-email-id="' + emailId + '"]');
            if (row) {
                row.style.animation = 'fadeOut 0.2s ease';
                setTimeout(function() {
                    row.remove();
                }, 200);
            }
            // Clear preview
            var previewPane = document.getElementById("previewPane");
            if (previewPane) {
                previewPane.innerHTML = getEmptyPreviewHtml();
            }
        }
    })
    .catch(function(err) {
        console.error('Move to trash failed:', err);
    });
}

/**
 * Restore email from trash
 */
function restoreFromTrash(emailId) {
    var headers = { "X-Requested-With": "XMLHttpRequest" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/restoreFromTrash?emailId=" + emailId, {
        method: "POST",
        headers: headers
    })
    .then(function(response) {
        if (response.ok) {
            // Remove from trash list
            var row = document.querySelector('[data-email-id="' + emailId + '"]');
            if (row) {
                row.style.animation = 'fadeOut 0.2s ease';
                setTimeout(function() {
                    row.remove();
                    updateTrashToolbar();
                }, 200);
            }
            // Clear preview
            var previewPane = document.getElementById("previewPane");
            if (previewPane) {
                previewPane.innerHTML = getEmptyPreviewHtml();
            }
        }
    })
    .catch(function(err) {
        console.error('Restore from trash failed:', err);
    });
}

/**
 * Permanently delete an email
 */
function permanentDelete(emailId) {
    if (!confirm('Delete this email permanently? This cannot be undone.')) return;
    
    var headers = { "X-Requested-With": "XMLHttpRequest" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/permanentDelete?emailId=" + emailId, {
        method: "POST",
        headers: headers
    })
    .then(function(response) {
        if (response.ok) {
            var row = document.querySelector('[data-email-id="' + emailId + '"]');
            if (row) {
                row.style.animation = 'fadeOut 0.2s ease';
                setTimeout(function() {
                    row.remove();
                    updateTrashToolbar();
                }, 200);
            }
            var previewPane = document.getElementById("previewPane");
            if (previewPane) {
                previewPane.innerHTML = getEmptyPreviewHtml();
            }
        }
    })
    .catch(function(err) {
        console.error('Permanent delete failed:', err);
    });
}

/**
 * Empty all trash
 */
function emptyTrash() {
    if (!confirm('Permanently delete all items in trash? This cannot be undone.')) return;
    
    var headers = { "X-Requested-With": "XMLHttpRequest" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/emptyTrash", {
        method: "POST",
        headers: headers
    })
    .then(function(response) {
        if (response.ok) {
            // Reload the page to show empty state
            window.location.reload();
        }
    })
    .catch(function(err) {
        console.error('Empty trash failed:', err);
    });
}

/**
 * Update trash toolbar after item removal
 */
function updateTrashToolbar() {
    var remaining = document.querySelectorAll('.mail-row').length;
    var toolbar = document.querySelector('.trash-toolbar');
    if (toolbar) {
        if (remaining === 0) {
            // Show empty state
            window.location.reload();
        } else {
            var info = toolbar.querySelector('.trash-info span');
            if (info) {
                info.textContent = remaining + ' item(s) in trash';
            }
        }
    }
}

/**
 * Get empty preview HTML
 */
function getEmptyPreviewHtml() {
    return '<div class="preview-empty">' +
        '<i class="bi bi-envelope-open"></i>' +
        '<h5>Select an item to read</h5>' +
        '<p>Nothing is selected</p>' +
        '</div>';
}

/**
 * Search functionality
 */
function initSearch() {
    var searchInput = document.getElementById('searchInput');
    if (!searchInput) return;
    
    var searchTimeout = null;
    
    searchInput.addEventListener('input', function() {
        clearTimeout(searchTimeout);
        var query = this.value.toLowerCase();
        
        searchTimeout = setTimeout(function() {
            var mailRows = document.querySelectorAll('.mail-row');
            for (var i = 0; i < mailRows.length; i++) {
                var row = mailRows[i];
                var text = row.textContent.toLowerCase();
                if (query === '' || text.indexOf(query) > -1) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            }
        }, 200);
    });
}

/**
 * Keyboard navigation
 */
function initKeyboardNav() {
    document.addEventListener('keydown', function(e) {
        var mailRows = document.querySelectorAll('.mail-row');
        if (mailRows.length === 0) return;
        
        var currentIndex = -1;
        for (var i = 0; i < mailRows.length; i++) {
            if (mailRows[i].classList.contains('selected')) {
                currentIndex = i;
                break;
            }
        }
        
        // Arrow Down
        if (e.keyCode === 40) {
            e.preventDefault();
            if (currentIndex < mailRows.length - 1) {
                var nextRow = mailRows[currentIndex + 1];
                var emailId = nextRow.getAttribute('data-email-id');
                selectEmail(nextRow, parseInt(emailId));
            }
        }
        
        // Arrow Up
        if (e.keyCode === 38) {
            e.preventDefault();
            if (currentIndex > 0) {
                var prevRow = mailRows[currentIndex - 1];
                var emailId = prevRow.getAttribute('data-email-id');
                selectEmail(prevRow, parseInt(emailId));
            }
        }
        
        // Delete key
        if (e.keyCode === 46 && selectedEmailId) {
            deleteEmail(selectedEmailId);
        }
    });
}

/**
 * Initialize on DOM ready
 */
document.addEventListener('DOMContentLoaded', function() {
    initSearch();
    initKeyboardNav();
});
