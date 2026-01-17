/**
 * Pulse Mail - JavaScript Functions
 * Outlook-style email client functionality
 */

// Track currently selected email
var selectedEmailId = null;
var currentSource = 'inbox';

// WebSocket connection
var stompClient = null;
var wsConnected = false;

/**
 * Initialize WebSocket connection for real-time notifications
 */
function initWebSocket() {
    if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
        console.warn('WebSocket libraries not loaded');
        return;
    }

    var socket = new SockJS(contextPath + '/ws');
    stompClient = Stomp.over(socket);
    
    // Disable debug logging in production
    stompClient.debug = function() {};

    stompClient.connect({}, function(frame) {
        wsConnected = true;
        console.log('WebSocket connected');

        // Subscribe to user-specific inbox notifications
        stompClient.subscribe('/user/queue/inbox', function(message) {
            var notification = JSON.parse(message.body);
            handleNewEmailNotification(notification);
        });

        // Subscribe to read receipt notifications
        stompClient.subscribe('/user/queue/read-receipts', function(message) {
            var notification = JSON.parse(message.body);
            handleReadReceiptNotification(notification);
        });

    }, function(error) {
        console.error('WebSocket connection error:', error);
        wsConnected = false;
        // Retry connection after 5 seconds
        setTimeout(initWebSocket, 5000);
    });
}

/**
 * Handle incoming new email notification
 */
function handleNewEmailNotification(notification) {
    // Show toast notification
    showEmailToast(notification);
    
    // Update inbox badge count
    updateInboxBadge(1);
    
    // If currently on inbox page, prepend the new email to list
    if (window.location.pathname.indexOf('/inbox') > -1) {
        prependNewEmailToList(notification);
    }
}

/**
 * Handle read receipt notification
 */
function handleReadReceiptNotification(notification) {
    showReadReceiptToast(notification);
}

/**
 * Show toast notification for read receipt
 */
function showReadReceiptToast(notification) {
    var toast = document.createElement('div');
    toast.className = 'email-toast read-receipt-toast animate-slide-in';
    toast.innerHTML = 
        '<div class="toast-icon" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%);">' +
            '<i class="bi bi-check2-all" style="font-size: 20px;"></i>' +
        '</div>' +
        '<div class="toast-content">' +
            '<div class="toast-title">Read Receipt</div>' +
            '<div class="toast-subject">' + escapeHtml(notification.readerEmail) + ' read your email</div>' +
            '<div class="toast-preview">"' + escapeHtml(notification.subject) + '" - ' + escapeHtml(notification.readAt) + '</div>' +
        '</div>' +
        '<button class="toast-close" onclick="this.parentElement.remove()">&times;</button>';

    document.body.appendChild(toast);

    // Auto-remove after 6 seconds
    setTimeout(function() {
        if (toast.parentElement) {
            toast.classList.add('animate-slide-out');
            setTimeout(function() { toast.remove(); }, 300);
        }
    }, 6000);
}

/**
 * Show toast notification for new email
 */
function showEmailToast(notification) {
    // Remove any existing toasts
    var existingToast = document.querySelector('.email-toast');
    if (existingToast) existingToast.remove();

    var toast = document.createElement('div');
    toast.className = 'email-toast animate-slide-in';
    toast.innerHTML = 
        '<div class="toast-icon">' +
            '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">' +
                '<path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path>' +
                '<polyline points="22,6 12,13 2,6"></polyline>' +
            '</svg>' +
        '</div>' +
        '<div class="toast-content">' +
            '<div class="toast-title">New email from ' + escapeHtml(notification.senderEmail) + '</div>' +
            '<div class="toast-subject">' + escapeHtml(notification.subject) + '</div>' +
            '<div class="toast-preview">' + escapeHtml(notification.preview) + '</div>' +
        '</div>' +
        '<button class="toast-close" onclick="this.parentElement.remove()">&times;</button>';

    // Click to view email
    toast.addEventListener('click', function(e) {
        if (!e.target.classList.contains('toast-close')) {
            window.location.href = contextPath + '/mail/inbox';
        }
    });

    document.body.appendChild(toast);

    // Auto-remove after 8 seconds
    setTimeout(function() {
        if (toast.parentElement) {
            toast.classList.add('animate-slide-out');
            setTimeout(function() { toast.remove(); }, 300);
        }
    }, 8000);
}

/**
 * Update inbox badge count
 */
function updateInboxBadge(increment) {
    var badge = document.getElementById('inboxUnreadBadge');
    if (badge) {
        var current = parseInt(badge.textContent) || 0;
        var newCount = current + increment;
        badge.textContent = newCount;
        badge.style.display = newCount > 0 ? '' : 'none';
    }
}

/**
 * Prepend new email to inbox list (without full refresh)
 */
function prependNewEmailToList(notification) {
    var mailList = document.querySelector('.mail-list');
    if (!mailList) return;

    // Create new mail row
    var initials = notification.senderEmail.substring(0, 2).toUpperCase();
    var colorIndex = (mailList.querySelectorAll('.mail-row').length % 6) + 1;

    // Format date like inbox.jsp: "Jan 14"
    var now = new Date();
    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    var dateStr = months[now.getMonth()] + ' ' + now.getDate();
    
    var newRow = document.createElement('div');
    newRow.className = 'mail-row unread animate-fade-in';
    newRow.setAttribute('data-email-id', notification.emailId);
    
    newRow.innerHTML = 
        '<label class="mail-checkbox" onclick="event.stopPropagation()">' +
            '<input type="checkbox" class="email-checkbox" data-email-id="' + notification.emailId + '" onchange="updateBulkSelection()">' +
            '<span class="checkmark"></span>' +
        '</label>' +
        '<div class="mail-row-content" onclick="selectEmail(this.parentElement, ' + notification.emailId + ', \'inbox\')">' +
            '<div class="mail-avatar color-' + colorIndex + '">' + initials + '</div>' +
            '<div class="mail-content">' +
                '<div class="mail-sender">' + escapeHtml(notification.senderEmail) + '</div>' +
                '<div class="mail-subject">' + escapeHtml(notification.subject || '(No subject)') + '</div>' +
            '</div>' +
            '<div class="mail-meta">' +
                '<span class="mail-time">' + escapeHtml(dateStr) + '</span>' +
                '<span class="unread-dot"></span>' +
                '<button class="mail-actions-btn" onclick="event.stopPropagation(); toggleMailMenu(this, ' + notification.emailId + ', \'inbox\', false)" title="More actions">' +
                    '<i class="bi bi-three-dots-vertical"></i>' +
                '</button>' +
            '</div>' +
        '</div>';

    // Find the first mail-row to insert before it (skip bulk toolbar, search banner, etc.)
    var firstMailRow = mailList.querySelector('.mail-row');
    if (firstMailRow) {
        mailList.insertBefore(newRow, firstMailRow);
    } else {
        mailList.appendChild(newRow);
    }
}

/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    if (!text) return '';
    var div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize WebSocket on page load
document.addEventListener('DOMContentLoaded', function() {
    initWebSocket();
});

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
        }).then(function() {
            // Update sidebar unread count
            if (typeof window.updateInboxUnreadCount === 'function') {
                window.updateInboxUnreadCount();
            }
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
 * Reply to email - redirects to compose page with pre-filled data
 * @param {number} emailId - The ID of the email to reply to
 * @param {boolean} replyAll - If true, includes all recipients (CC)
 */
function replyToEmail(emailId, replyAll) {
    var url = contextPath + "/mail/compose?replyTo=" + emailId;
    if (replyAll) {
        url += "&replyAll=true";
    }
    window.location.href = url;
}

/**
 * Toggle conversation thread visibility in preview
 */
function toggleThread() {
    var messages = document.getElementById('threadMessages');
    var chevron = document.querySelector('.thread-chevron');
    if (messages) {
        if (messages.style.display === 'none') {
            messages.style.display = 'block';
            if (chevron) chevron.style.transform = 'rotate(180deg)';
        } else {
            messages.style.display = 'none';
            if (chevron) chevron.style.transform = 'rotate(0deg)';
        }
    }
}

/**
 * Remove email from current label (used in label view)
 */
function removeFromCurrentLabel(emailId) {
    if (!window.currentLabelId) {
        console.error('Current label ID not set');
        return;
    }
    
    if (!confirm('Remove this email from the current label?')) return;
    
    var headers = { "X-Requested-With": "XMLHttpRequest" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/labels/email/remove?emailId=" + emailId + "&labelId=" + window.currentLabelId, {
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
        console.error('Remove from label failed:', err);
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

// ============================================
// BULK SELECTION OPERATIONS
// ============================================

/**
 * Update bulk selection state and show/hide toolbar
 */
function updateBulkSelection() {
    var checkboxes = document.querySelectorAll('.email-checkbox');
    var checked = document.querySelectorAll('.email-checkbox:checked');
    
    // Support both bulkToolbar (inbox/sent) and bulkActions (drafts/trash)
    var toolbar = document.getElementById('bulkToolbar') || document.getElementById('bulkActions');
    var countSpan = document.getElementById('selectedCount');
    var selectAllCheckbox = document.getElementById('selectAllCheckbox') || document.getElementById('selectAll');
    
    // Update count
    if (countSpan) {
        countSpan.textContent = checked.length + ' selected';
    }
    
    // Show/hide toolbar/actions
    if (toolbar) {
        if (checked.length > 0) {
            toolbar.style.display = 'flex';
        } else {
            toolbar.style.display = 'none';
        }
    }
    
    // Update select all checkbox state
    if (selectAllCheckbox) {
        if (checked.length === 0) {
            selectAllCheckbox.checked = false;
            selectAllCheckbox.indeterminate = false;
        } else if (checked.length === checkboxes.length) {
            selectAllCheckbox.checked = true;
            selectAllCheckbox.indeterminate = false;
        } else {
            selectAllCheckbox.checked = false;
            selectAllCheckbox.indeterminate = true;
        }
    }
    
    // Update row styling
    checkboxes.forEach(function(cb) {
        var row = cb.closest('.mail-row');
        if (row) {
            if (cb.checked) {
                row.classList.add('bulk-selected');
            } else {
                row.classList.remove('bulk-selected');
            }
        }
    });
}

/**
 * Toggle select all checkboxes
 */
function toggleSelectAll() {
    var selectAll = document.getElementById('selectAllCheckbox') || document.getElementById('selectAll');
    var checkboxes = document.querySelectorAll('.email-checkbox');
    
    if (!selectAll) return;
    
    checkboxes.forEach(function(cb) {
        cb.checked = selectAll.checked;
    });
    
    updateBulkSelection();
}

/**
 * Get array of selected email IDs
 */
function getSelectedEmailIds() {
    var checked = document.querySelectorAll('.email-checkbox:checked');
    var ids = [];
    checked.forEach(function(cb) {
        ids.push(cb.getAttribute('data-email-id'));
    });
    return ids;
}

/**
 * Bulk delete (move to trash)
 */
function bulkDelete() {
    var ids = getSelectedEmailIds();
    if (ids.length === 0) return;
    
    if (!confirm('Move ' + ids.length + ' email(s) to trash?')) return;
    
    var headers = { "X-Requested-With": "XMLHttpRequest", "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/bulkMoveToTrash?emailIds=" + ids.join('&emailIds='), {
        method: "POST",
        headers: headers
    })
    .then(function(response) {
        if (response.ok) {
            // Remove rows with animation
            ids.forEach(function(id) {
                var row = document.querySelector('.mail-row[data-email-id="' + id + '"]');
                if (row) {
                    row.style.animation = 'fadeOut 0.2s ease';
                    setTimeout(function() { row.remove(); }, 200);
                }
            });
            // Clear preview
            var previewPane = document.getElementById("previewPane");
            if (previewPane) {
                previewPane.innerHTML = getEmptyPreviewHtml();
            }
            // Hide toolbar
            document.getElementById('bulkToolbar').style.display = 'none';
        }
    })
    .catch(function(err) {
        console.error('Bulk delete failed:', err);
    });
}

/**
 * Bulk mark as read
 */
function bulkMarkRead() {
    var ids = getSelectedEmailIds();
    if (ids.length === 0) return;
    
    var headers = { "X-Requested-With": "XMLHttpRequest", "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/bulkMarkRead?emailIds=" + ids.join('&emailIds='), {
        method: "POST",
        headers: headers
    })
    .then(function(response) {
        if (response.ok) {
            // Update row styling
            ids.forEach(function(id) {
                var row = document.querySelector('.mail-row[data-email-id="' + id + '"]');
                if (row) {
                    row.classList.remove('unread');
                    var dot = row.querySelector('.unread-dot');
                    if (dot) dot.style.display = 'none';
                }
            });
            // Update sidebar unread count
            if (typeof window.updateInboxUnreadCount === 'function') {
                window.updateInboxUnreadCount();
            }
            // Uncheck all and hide toolbar
            document.querySelectorAll('.email-checkbox').forEach(function(cb) {
                cb.checked = false;
            });
            updateBulkSelection();
        }
    })
    .catch(function(err) {
        console.error('Bulk mark read failed:', err);
    });
}

/**
 * Bulk restore from trash
 */
function bulkRestore() {
    var ids = getSelectedEmailIds();
    if (ids.length === 0) return;
    
    var headers = { "X-Requested-With": "XMLHttpRequest", "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/bulkRestore?emailIds=" + ids.join('&emailIds='), {
        method: "POST",
        headers: headers
    })
    .then(function(response) {
        if (response.ok) {
            ids.forEach(function(id) {
                var row = document.querySelector('.mail-row[data-email-id="' + id + '"]');
                if (row) {
                    row.style.animation = 'fadeOut 0.2s ease';
                    setTimeout(function() { row.remove(); }, 200);
                }
            });
            var previewPane = document.getElementById("previewPane");
            if (previewPane) {
                previewPane.innerHTML = getEmptyPreviewHtml();
            }
            document.getElementById('bulkToolbar').style.display = 'none';
        }
    })
    .catch(function(err) {
        console.error('Bulk restore failed:', err);
    });
}

/**
 * Bulk permanent delete
 */
function bulkPermanentDelete() {
    var ids = getSelectedEmailIds();
    if (ids.length === 0) return;
    
    if (!confirm('Permanently delete ' + ids.length + ' email(s)? This cannot be undone.')) return;
    
    var headers = { "X-Requested-With": "XMLHttpRequest", "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/bulkPermanentDelete?emailIds=" + ids.join('&emailIds='), {
        method: "POST",
        headers: headers
    })
    .then(function(response) {
        if (response.ok) {
            ids.forEach(function(id) {
                var row = document.querySelector('.mail-row[data-email-id="' + id + '"]');
                if (row) {
                    row.style.animation = 'fadeOut 0.2s ease';
                    setTimeout(function() { row.remove(); }, 200);
                }
            });
            var previewPane = document.getElementById("previewPane");
            if (previewPane) {
                previewPane.innerHTML = getEmptyPreviewHtml();
            }
            document.getElementById('bulkToolbar').style.display = 'none';
        }
    })
    .catch(function(err) {
        console.error('Bulk permanent delete failed:', err);
    });
}

/**
 * Bulk delete drafts
 */
function bulkDeleteDrafts() {
    var ids = getSelectedEmailIds();
    if (ids.length === 0) return;
    
    if (!confirm('Delete ' + ids.length + ' draft(s)? This cannot be undone.')) return;
    
    var headers = { "X-Requested-With": "XMLHttpRequest", "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/bulkDeleteDrafts?emailIds=" + ids.join('&emailIds='), {
        method: "POST",
        headers: headers
    })
    .then(function(response) {
        if (response.ok) {
            ids.forEach(function(id) {
                var row = document.querySelector('.mail-row[data-email-id="' + id + '"]');
                if (row) {
                    row.style.animation = 'fadeOut 0.2s ease';
                    setTimeout(function() { row.remove(); }, 200);
                }
            });
            updateBulkSelection();
        }
    })
    .catch(function(err) {
        console.error('Bulk delete drafts failed:', err);
    });
}

// ============================================
// STARRED EMAILS
// ============================================

/**
 * Toggle star status for an email
 */
function toggleStar(emailId, element) {
    var headers = { "X-Requested-With": "XMLHttpRequest", "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/toggleStar?emailId=" + emailId, {
        method: "POST",
        headers: headers
    })
    .then(function(response) { return response.json(); })
    .then(function(isStarred) {
        var icon = element.querySelector('i');
        if (isStarred) {
            element.classList.add('starred');
            element.title = 'Remove from starred';
            icon.className = 'bi bi-star-fill';
        } else {
            element.classList.remove('starred');
            element.title = 'Add to starred';
            icon.className = 'bi bi-star';
            // If on starred page, remove the row
            if (window.location.pathname.includes('/starred')) {
                var row = element.closest('.mail-row');
                if (row) {
                    row.style.animation = 'fadeOut 0.2s ease';
                    setTimeout(function() { row.remove(); }, 200);
                }
            }
        }
    })
    .catch(function(err) {
        console.error('Toggle star failed:', err);
    });
}

/**
 * Toggle star from preview pane
 */
function toggleStarPreview(emailId, element) {
    var headers = { "X-Requested-With": "XMLHttpRequest", "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/toggleStar?emailId=" + emailId, {
        method: "POST",
        headers: headers
    })
    .then(function(response) { return response.json(); })
    .then(function(isStarred) {
        var icon = element.querySelector('i');
        var label = element.querySelector('span');
        if (isStarred) {
            element.classList.add('starred');
            element.title = 'Remove from starred';
            icon.className = 'bi bi-star-fill';
            if (label) label.textContent = 'Starred';
        } else {
            element.classList.remove('starred');
            element.title = 'Add to starred';
            icon.className = 'bi bi-star';
            if (label) label.textContent = 'Star';
        }
        // Also update the star in the mail list
        var mailRow = document.querySelector('.mail-row[data-email-id="' + emailId + '"]');
        if (mailRow) {
            var listStar = mailRow.querySelector('.mail-star');
            if (listStar) {
                var listIcon = listStar.querySelector('i');
                if (isStarred) {
                    listStar.classList.add('starred');
                    listStar.title = 'Remove from starred';
                    listIcon.className = 'bi bi-star-fill';
                } else {
                    listStar.classList.remove('starred');
                    listStar.title = 'Add to starred';
                    listIcon.className = 'bi bi-star';
                }
            }
        }
    })
    .catch(function(err) {
        console.error('Toggle star failed:', err);
    });
}

/**
 * Forward email
 */
function forwardEmail(emailId) {
    window.location.href = contextPath + '/mail/compose?forward=' + emailId;
}

/**
 * Cache for user labels
 */
var userLabelsCache = null;

/**
 * Toggle 3-dot mail actions menu
 */
function toggleMailMenu(btn, emailId, source, isStarred) {
    // Close any existing menu
    closeMailMenu();
    
    // Mark button as active
    btn.classList.add('active');
    
    // Get button position
    var rect = btn.getBoundingClientRect();
    
    // Create menu
    var menu = document.createElement('div');
    menu.className = 'mail-actions-menu';
    menu.id = 'mailActionsMenu';
    menu.setAttribute('data-email-id', emailId);
    
    // Position menu
    menu.style.top = (rect.bottom + 4) + 'px';
    menu.style.right = (window.innerWidth - rect.right) + 'px';
    
    // Build menu items based on source
    var menuHtml = '';
    
    if (source === 'drafts') {
        // Drafts only has edit and delete
        menuHtml += '<div class="mail-actions-menu-item" onclick="closeMailMenu(); window.location.href=\'' + contextPath + '/mail/compose?id=' + emailId + '\'">' +
                    '<i class="bi bi-pencil"></i> Edit Draft</div>';
        menuHtml += '<div class="mail-actions-menu-divider"></div>';
        menuHtml += '<div class="mail-actions-menu-item danger" onclick="closeMailMenu(); deleteDraft(' + emailId + ')">' +
                    '<i class="bi bi-trash"></i> Delete</div>';
        menu.innerHTML = menuHtml;
        document.body.appendChild(menu);
        setTimeout(function() {
            document.addEventListener('click', closeMailMenuOnClickOutside);
        }, 10);
        return;
    }
    
    if (source === 'inbox' || source === 'sent' || source === 'starred' || source === 'label') {
        menuHtml += '<div class="mail-actions-menu-item" onclick="closeMailMenu(); replyToEmail(' + emailId + ', false)">' +
                    '<i class="bi bi-reply"></i> Reply</div>';
        menuHtml += '<div class="mail-actions-menu-item" onclick="closeMailMenu(); replyToEmail(' + emailId + ', true)">' +
                    '<i class="bi bi-reply-all"></i> Reply All</div>';
        menuHtml += '<div class="mail-actions-menu-item" onclick="closeMailMenu(); forwardEmail(' + emailId + ')">' +
                    '<i class="bi bi-forward"></i> Forward</div>';
        menuHtml += '<div class="mail-actions-menu-divider"></div>';
    }
    
    // Star toggle (not for drafts/trash)
    if (source !== 'trash') {
        var starIcon = isStarred ? 'bi-star-fill' : 'bi-star';
        var starText = isStarred ? 'Remove Star' : 'Add Star';
        menuHtml += '<div class="mail-actions-menu-item" onclick="closeMailMenu(); toggleStarFromMenu(' + emailId + ')">' +
                    '<i class="bi ' + starIcon + '" style="color: #ffc107;"></i> ' + starText + '</div>';
        
        // Labels submenu
        menuHtml += '<div class="mail-actions-submenu">' +
                    '<div class="mail-actions-menu-item"><i class="bi bi-tag"></i> Add Label</div>' +
                    '<div class="mail-actions-submenu-content" id="labelsSubmenu">Loading...</div></div>';
        
        // Snooze submenu (only for inbox)
        if (source === 'inbox' || source === 'snoozed') {
            menuHtml += '<div class="mail-actions-submenu snooze-submenu">' +
                        '<div class="mail-actions-menu-item"><i class="bi bi-clock" style="color: #6c5ce7;"></i> Snooze</div>' +
                        '<div class="mail-actions-submenu-content snooze-options">' +
                        '<div class="mail-actions-menu-item" onclick="closeMailMenu(); snoozeEmail(' + emailId + ', \'later_today\')">' +
                        '<i class="bi bi-sun"></i> Later today</div>' +
                        '<div class="mail-actions-menu-item" onclick="closeMailMenu(); snoozeEmail(' + emailId + ', \'tomorrow\')">' +
                        '<i class="bi bi-calendar-check"></i> Tomorrow</div>' +
                        '<div class="mail-actions-menu-item" onclick="closeMailMenu(); snoozeEmail(' + emailId + ', \'next_week\')">' +
                        '<i class="bi bi-calendar-week"></i> Next week</div>' +
                        '<div class="mail-actions-menu-divider"></div>' +
                        '<div class="mail-actions-menu-item" onclick="closeMailMenu(); snoozeEmail(' + emailId + ', \'1_hour\')">' +
                        '<i class="bi bi-clock-history"></i> 1 hour</div>' +
                        '<div class="mail-actions-menu-item" onclick="closeMailMenu(); snoozeEmail(' + emailId + ', \'4_hours\')">' +
                        '<i class="bi bi-clock-history"></i> 4 hours</div>' +
                        '<div class="mail-actions-menu-divider"></div>' +
                        '<div class="mail-actions-menu-item" onclick="closeMailMenu(); showCustomSnooze(' + emailId + ')">' +
                        '<i class="bi bi-calendar3"></i> Pick date & time</div>' +
                        '</div></div>';
        }
        
        menuHtml += '<div class="mail-actions-menu-divider"></div>';
    }
    
    // Delete / Remove from label
    if (source === 'trash') {
        menuHtml += '<div class="mail-actions-menu-item danger" onclick="closeMailMenu(); permanentDelete(' + emailId + ')">' +
                    '<i class="bi bi-trash"></i> Delete Forever</div>';
        menuHtml += '<div class="mail-actions-menu-item" onclick="closeMailMenu(); restoreFromTrash(' + emailId + ')">' +
                    '<i class="bi bi-arrow-counterclockwise"></i> Restore</div>';
    } else if (source === 'label') {
        // For label view, show "Remove from Label" instead of delete
        menuHtml += '<div class="mail-actions-menu-item" onclick="closeMailMenu(); removeFromCurrentLabel(' + emailId + ')">' +
                    '<i class="bi bi-tag-fill" style="color: #dc3545;"></i> Remove from Label</div>';
        menuHtml += '<div class="mail-actions-menu-divider"></div>';
        menuHtml += '<div class="mail-actions-menu-item danger" onclick="closeMailMenu(); moveToTrash(' + emailId + ')">' +
                    '<i class="bi bi-trash"></i> Delete</div>';
    } else {
        menuHtml += '<div class="mail-actions-menu-item danger" onclick="closeMailMenu(); moveToTrash(' + emailId + ')">' +
                    '<i class="bi bi-trash"></i> Delete</div>';
    }
    
    menu.innerHTML = menuHtml;
    document.body.appendChild(menu);
    
    // Load labels for submenu (if not trash)
    if (source !== 'trash') {
        loadLabelsForMenu(emailId);
    }
    
    // Close menu when clicking outside
    setTimeout(function() {
        document.addEventListener('click', closeMailMenuOnClickOutside);
    }, 10);
}

/**
 * Close the mail actions menu
 */
function closeMailMenu() {
    var menu = document.getElementById('mailActionsMenu');
    if (menu) {
        menu.remove();
    }
    document.querySelectorAll('.mail-actions-btn.active').forEach(function(btn) {
        btn.classList.remove('active');
    });
    document.removeEventListener('click', closeMailMenuOnClickOutside);
}

function closeMailMenuOnClickOutside(e) {
    var menu = document.getElementById('mailActionsMenu');
    if (menu && !menu.contains(e.target) && !e.target.closest('.mail-actions-btn')) {
        closeMailMenu();
    }
}

/**
 * Toggle star from menu
 */
function toggleStarFromMenu(emailId) {
    var headers = { "X-Requested-With": "XMLHttpRequest", "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/toggleStar?emailId=" + emailId, {
        method: "POST",
        headers: headers
    })
    .then(function(response) { return response.json(); })
    .then(function(isStarred) {
        // Show feedback
        showToast(isStarred ? 'Added to Starred' : 'Removed from Starred');
    })
    .catch(function(err) {
        console.error('Toggle star failed:', err);
    });
}

/**
 * Snooze an email
 */
function snoozeEmail(emailId, preset) {
    var headers = { "Content-Type": "application/json" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/snooze", {
        method: "POST",
        headers: headers,
        body: JSON.stringify({
            emailId: emailId,
            preset: preset
        })
    })
    .then(function(response) { return response.json(); })
    .then(function(data) {
        if (data.success) {
            showToast('Snoozed until ' + data.snoozedUntil, 'success');
            // Remove from inbox list with animation
            var row = document.querySelector('[data-email-id="' + emailId + '"]');
            if (row) {
                row.style.transition = 'all 0.3s ease';
                row.style.transform = 'translateX(100%)';
                row.style.opacity = '0';
                setTimeout(function() { row.remove(); }, 300);
            }
            // Update snooze badge
            updateSnoozeBadge();
        } else {
            showToast(data.error || 'Failed to snooze', 'error');
        }
    })
    .catch(function(err) {
        console.error('Snooze failed:', err);
        showToast('Failed to snooze email', 'error');
    });
}

/**
 * Show custom snooze date/time picker
 */
function showCustomSnooze(emailId) {
    // Remove any existing modal
    var existing = document.getElementById('snoozeModal');
    if (existing) existing.remove();
    
    // Get tomorrow's date as minimum
    var tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    var minDate = tomorrow.toISOString().slice(0, 16);
    
    var modal = document.createElement('div');
    modal.id = 'snoozeModal';
    modal.className = 'modal-overlay';
    modal.innerHTML = 
        '<div class="snooze-modal">' +
        '<div class="snooze-modal-header">' +
        '<i class="bi bi-clock" style="color: #6c5ce7;"></i>' +
        '<span>Snooze until</span>' +
        '<button class="close-btn" onclick="closeSnoozeModal()"><i class="bi bi-x"></i></button>' +
        '</div>' +
        '<div class="snooze-modal-body">' +
        '<input type="datetime-local" id="snoozeDateTime" min="' + minDate + '" class="form-control">' +
        '</div>' +
        '<div class="snooze-modal-footer">' +
        '<button class="btn btn-secondary" onclick="closeSnoozeModal()">Cancel</button>' +
        '<button class="btn btn-primary snooze-btn" onclick="confirmCustomSnooze(' + emailId + ')">Snooze</button>' +
        '</div>' +
        '</div>';
    
    document.body.appendChild(modal);
    
    // Focus the date picker
    setTimeout(function() {
        document.getElementById('snoozeDateTime').focus();
    }, 100);
}

function closeSnoozeModal() {
    var modal = document.getElementById('snoozeModal');
    if (modal) modal.remove();
}

function confirmCustomSnooze(emailId) {
    var dateTime = document.getElementById('snoozeDateTime').value;
    if (!dateTime) {
        showToast('Please select a date and time', 'error');
        return;
    }
    
    var headers = { "Content-Type": "application/json" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + "/mail/snooze", {
        method: "POST",
        headers: headers,
        body: JSON.stringify({
            emailId: emailId,
            preset: 'custom',
            customTime: dateTime
        })
    })
    .then(function(response) { return response.json(); })
    .then(function(data) {
        closeSnoozeModal();
        if (data.success) {
            showToast('Snoozed until ' + data.snoozedUntil, 'success');
            // Remove from inbox list with animation
            var row = document.querySelector('[data-email-id="' + emailId + '"]');
            if (row) {
                row.style.transition = 'all 0.3s ease';
                row.style.transform = 'translateX(100%)';
                row.style.opacity = '0';
                setTimeout(function() { row.remove(); }, 300);
            }
            updateSnoozeBadge();
        } else {
            showToast(data.error || 'Failed to snooze', 'error');
        }
    })
    .catch(function(err) {
        closeSnoozeModal();
        console.error('Custom snooze failed:', err);
        showToast('Failed to snooze email', 'error');
    });
}

/**
 * Update snooze badge count in sidebar
 */
function updateSnoozeBadge() {
    fetch(contextPath + '/mail/snooze/count')
        .then(function(res) { return res.json(); })
        .then(function(count) {
            var badge = document.getElementById('snoozedBadge');
            if (badge) {
                if (count > 0) {
                    badge.textContent = count;
                    badge.style.display = '';
                } else {
                    badge.style.display = 'none';
                }
            }
        })
        .catch(function(err) {
            console.error('Failed to update snooze badge:', err);
        });
}

/**
 * Load labels for the submenu
 */
function loadLabelsForMenu(emailId) {
    var submenu = document.getElementById('labelsSubmenu');
    if (!submenu) return;
    
    // Check cache
    if (userLabelsCache) {
        renderLabelsSubmenu(submenu, emailId, userLabelsCache);
        return;
    }
    
    fetch(contextPath + '/mail/labels/list')
        .then(function(res) { return res.json(); })
        .then(function(labels) {
            userLabelsCache = labels;
            renderLabelsSubmenu(submenu, emailId, labels);
        })
        .catch(function(err) {
            submenu.innerHTML = '<div class="no-labels-msg">Failed to load labels</div>';
        });
}

function renderLabelsSubmenu(submenu, emailId, labels) {
    if (labels.length === 0) {
        submenu.innerHTML = '<div class="no-labels-msg">No labels yet</div>' +
                           '<div class="label-menu-item" onclick="closeMailMenu(); window.location.href=\'' + contextPath + '/mail/labels/manage\'">' +
                           '<i class="bi bi-plus" style="color: #6c5ce7;"></i> Create Label</div>';
        return;
    }
    
    var html = '';
    labels.forEach(function(label) {
        html += '<div class="label-menu-item" onclick="addLabelToEmail(' + emailId + ', ' + label.id + ')">' +
                '<span class="label-menu-color" style="background: ' + label.color + ';"></span>' +
                '<span class="label-menu-name">' + label.name + '</span></div>';
    });
    html += '<div class="mail-actions-menu-divider"></div>';
    html += '<div class="label-menu-item" onclick="closeMailMenu(); window.location.href=\'' + contextPath + '/mail/labels/manage\'">' +
            '<i class="bi bi-gear" style="color: var(--text-muted);"></i> Manage Labels</div>';
    submenu.innerHTML = html;
}

/**
 * Add label to email
 */
function addLabelToEmail(emailId, labelId) {
    closeMailMenu();
    
    var headers = { "X-Requested-With": "XMLHttpRequest", "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + '/mail/labels/email/add?emailId=' + emailId + '&labelId=' + labelId, {
        method: 'POST',
        headers: headers
    })
    .then(function(res) {
        if (res.ok) {
            showToast('Label added');
        } else {
            showToast('Failed to add label', 'error');
        }
    })
    .catch(function(err) {
        showToast('Failed to add label', 'error');
    });
}

/**
 * Delete draft
 */
function deleteDraft(emailId) {
    var headers = { "X-Requested-With": "XMLHttpRequest", "Content-Type": "application/x-www-form-urlencoded" };
    if (typeof csrfToken !== 'undefined' && typeof csrfHeader !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch(contextPath + '/mail/deleteDraft?id=' + emailId, {
        method: 'POST',
        headers: headers
    })
    .then(function(res) {
        if (res.ok) {
            showToast('Draft deleted');
            var row = document.querySelector('.mail-row[data-email-id="' + emailId + '"]');
            if (row) {
                row.style.opacity = '0';
                row.style.transform = 'translateX(-20px)';
                setTimeout(function() { row.remove(); }, 300);
            }
        } else {
            showToast('Failed to delete', 'error');
        }
    })
    .catch(function(err) {
        showToast('Failed to delete', 'error');
    });
}

/**
 * Show toast notification
 */
function showToast(message, type) {
    // Remove existing toast
    var existing = document.querySelector('.mail-toast');
    if (existing) existing.remove();
    
    var toast = document.createElement('div');
    toast.className = 'mail-toast' + (type === 'error' ? ' toast-error' : '');
    toast.innerHTML = '<i class="bi ' + (type === 'error' ? 'bi-x-circle' : 'bi-check-circle') + '"></i> ' + message;
    document.body.appendChild(toast);
    
    // Animate in
    setTimeout(function() { toast.classList.add('show'); }, 10);
    
    // Remove after 3 seconds
    setTimeout(function() {
        toast.classList.remove('show');
        setTimeout(function() { toast.remove(); }, 300);
    }, 3000);
}


// =====================================================
// EASTER EGGS - Fun hidden features
// =====================================================

/**
 * Console ASCII Art - Shows when developer opens console
 */
(function() {
// Expose secrets function (hidden - no console output on load)
    window.impulse = {
        secrets: function() {
            console.log('%c Easter Eggs:', 'color: #6c5ce7; font-weight: bold; font-size: 14px;');
            console.log('%c  1. Konami Code: Try a famous cheat code...', 'color: #333;');
            console.log('%c  2. Click the logo rapidly...', 'color: #333;');
            console.log('%c  3. Check the feedback form...', 'color: #333;');
            console.log('%c  4. Type something colorful...', 'color: #333;');
            return 'Secrets unlocked!';
        },
        version: '1.0.0',
        author: 'Saurabh Mishra'
    };
})();

/**
 * Konami Code Easter Egg - ↑↑↓↓←→←→BA triggers confetti
 */
(function() {
    var konamiCode = ['ArrowUp', 'ArrowUp', 'ArrowDown', 'ArrowDown', 
                      'ArrowLeft', 'ArrowRight', 'ArrowLeft', 'ArrowRight', 
                      'KeyB', 'KeyA'];
    var konamiIndex = 0;
    
    document.addEventListener('keydown', function(e) {
        var key = e.code;
        if (key === konamiCode[konamiIndex]) {
            konamiIndex++;
            if (konamiIndex === konamiCode.length) {
                konamiIndex = 0;
                triggerConfetti();
            }
        } else {
            konamiIndex = 0;
        }
    });
    
    function triggerConfetti() {
        var colors = ['#6c5ce7', '#00b894', '#fdcb6e', '#e74c3c', '#fd79a8', '#74b9ff'];
        var confettiCount = 150;
        
        // Create confetti container
        var container = document.createElement('div');
        container.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: 99999; overflow: hidden;';
        document.body.appendChild(container);
        
        // Create confetti pieces
        for (var i = 0; i < confettiCount; i++) {
            var confetti = document.createElement('div');
            confetti.style.cssText = 
                'position: absolute;' +
                'width: ' + (Math.random() * 10 + 5) + 'px;' +
                'height: ' + (Math.random() * 10 + 5) + 'px;' +
                'background: ' + colors[Math.floor(Math.random() * colors.length)] + ';' +
                'left: ' + Math.random() * 100 + '%;' +
                'top: -20px;' +
                'opacity: ' + (Math.random() * 0.5 + 0.5) + ';' +
                'border-radius: ' + (Math.random() > 0.5 ? '50%' : '2px') + ';' +
                'transform: rotate(' + Math.random() * 360 + 'deg);';
            container.appendChild(confetti);
            
            // Animate each piece
            animateConfetti(confetti);
        }
        
        // Show message
        var msg = document.createElement('div');
        msg.style.cssText = 'position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 30px 50px; border-radius: 16px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); z-index: 100000; text-align: center; animation: bounceIn 0.5s ease;';
        msg.innerHTML = '<h2 style="margin: 0 0 10px; color: #6c5ce7;"><i class="bi bi-trophy-fill"></i> Achievement Unlocked!</h2><p style="margin: 0; color: #666;">You found the Konami Code!</p>';
        document.body.appendChild(msg);
        
        // Cleanup after 4 seconds
        setTimeout(function() {
            container.remove();
            msg.remove();
        }, 4000);
        
        function animateConfetti(el) {
            var duration = Math.random() * 3000 + 2000;
            var startTime = Date.now();
            var startY = -20;
            var endY = window.innerHeight + 20;
            var swayAmount = Math.random() * 100 - 50;
            var rotation = Math.random() * 720;
            
            function update() {
                var elapsed = Date.now() - startTime;
                var progress = elapsed / duration;
                
                if (progress < 1) {
                    var y = startY + (endY - startY) * progress;
                    var sway = Math.sin(progress * Math.PI * 4) * swayAmount;
                    var rotate = progress * rotation;
                    el.style.transform = 'translateX(' + sway + 'px) rotate(' + rotate + 'deg)';
                    el.style.top = y + 'px';
                    requestAnimationFrame(update);
                }
            }
            requestAnimationFrame(update);
        }
    }
})();

/**
 * Logo Rapid Click Easter Egg - Click 7 times for Matrix effect
 */
(function() {
    var clickCount = 0;
    var clickTimer = null;
    
    document.addEventListener('click', function(e) {
        var logo = e.target.closest('.brand') || e.target.closest('.brand-logo');
        if (!logo) return;
        
        clickCount++;
        
        if (clickTimer) clearTimeout(clickTimer);
        clickTimer = setTimeout(function() { clickCount = 0; }, 800);
        
        if (clickCount >= 7) {
            clickCount = 0;
            triggerMatrixRain();
        }
    });
    
    function triggerMatrixRain() {
        var canvas = document.createElement('canvas');
        canvas.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: 99999; pointer-events: none;';
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        document.body.appendChild(canvas);
        
        var ctx = canvas.getContext('2d');
        var chars = 'IMPULSE01アイウエオカキクケコサシスセソタチツテトナニヌネノ';
        var fontSize = 14;
        var columns = canvas.width / fontSize;
        var drops = [];
        
        for (var i = 0; i < columns; i++) {
            drops[i] = Math.random() * -100;
        }
        
        function draw() {
            ctx.fillStyle = 'rgba(0, 0, 0, 0.05)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            
            ctx.fillStyle = '#00b894';
            ctx.font = fontSize + 'px monospace';
            
            for (var i = 0; i < drops.length; i++) {
                var char = chars[Math.floor(Math.random() * chars.length)];
                ctx.fillText(char, i * fontSize, drops[i] * fontSize);
                
                if (drops[i] * fontSize > canvas.height && Math.random() > 0.975) {
                    drops[i] = 0;
                }
                drops[i]++;
            }
        }
        
        var matrixInterval = setInterval(draw, 35);
        
        // Stop after 5 seconds
        setTimeout(function() {
            clearInterval(matrixInterval);
            canvas.style.transition = 'opacity 0.5s';
            canvas.style.opacity = '0';
            setTimeout(function() { canvas.remove(); }, 500);
        }, 5000);
    }
})();

/**
 * Rainbow Mode - Type "rainbow" anywhere
 */
(function() {
    var buffer = '';
    var rainbowActive = false;
    
    document.addEventListener('keypress', function(e) {
        if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') return;
        
        buffer += e.key.toLowerCase();
        if (buffer.length > 10) buffer = buffer.slice(-10);
        
        if (buffer.includes('rainbow') && !rainbowActive) {
            buffer = '';
            rainbowActive = true;
            activateRainbowMode();
        }
    });
    
    function activateRainbowMode() {
        var style = document.createElement('style');
        style.id = 'rainbow-mode';
        style.textContent = 
            '@keyframes rainbow { ' +
            '  0% { filter: hue-rotate(0deg); } ' +
            '  100% { filter: hue-rotate(360deg); } ' +
            '} ' +
            '.rainbow-mode * { animation: rainbow 3s linear infinite !important; }';
        document.head.appendChild(style);
        
        document.body.classList.add('rainbow-mode');
        
        showToast('Rainbow Mode Activated!');
        
        // Disable after 10 seconds
        setTimeout(function() {
            document.body.classList.remove('rainbow-mode');
            var styleEl = document.getElementById('rainbow-mode');
            if (styleEl) styleEl.remove();
            rainbowActive = false;
            showToast('Rainbow Mode Deactivated');
        }, 10000);
    }
})();


