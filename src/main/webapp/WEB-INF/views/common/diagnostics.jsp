<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:mailLayout pageTitle="System Diagnostics">

<div class="diagnostics-container">
    <div class="diagnostics-header">
        <div class="diagnostics-title">
            <i class="bi bi-controller"></i>
            <h1>You found it!</h1>
        </div>
        <p class="diagnostics-subtitle">Take a break, you deserve it. Pick a game below.</p>
    </div>

    <div class="games-grid" id="gamesGrid">
        <!-- Snake Game -->
        <div class="game-card" onclick="openGame('snake')">
            <div class="game-icon" style="background: linear-gradient(135deg, #00b894, #55efc4);">
                <i class="bi bi-arrow-right-circle"></i>
            </div>
            <h3>Snake</h3>
            <p>Classic snake game. Eat, grow, don't hit walls!</p>
            <div class="game-controls"><i class="bi bi-keyboard"></i> Arrow Keys</div>
        </div>

        <!-- 2048 Game -->
        <div class="game-card" onclick="openGame('puzzle2048')">
            <div class="game-icon" style="background: linear-gradient(135deg, #f39c12, #f1c40f);">
                <i class="bi bi-grid-3x3"></i>
            </div>
            <h3>2048</h3>
            <p>Merge tiles to reach 2048!</p>
            <div class="game-controls"><i class="bi bi-keyboard"></i> Arrow Keys</div>
        </div>

        <!-- Memory Game -->
        <div class="game-card" onclick="openGame('memory')">
            <div class="game-icon" style="background: linear-gradient(135deg, #6c5ce7, #a29bfe);">
                <i class="bi bi-grid-fill"></i>
            </div>
            <h3>Memory</h3>
            <p>Match pairs of cards. Test your memory!</p>
            <div class="game-controls"><i class="bi bi-mouse"></i> Click Cards</div>
        </div>

        <!-- Breakout Game -->
        <div class="game-card" onclick="openGame('breakout')">
            <div class="game-icon" style="background: linear-gradient(135deg, #e74c3c, #ff7675);">
                <i class="bi bi-bricks"></i>
            </div>
            <h3>Breakout</h3>
            <p>Break all the bricks with the ball!</p>
            <div class="game-controls"><i class="bi bi-keyboard"></i> Arrows / <i class="bi bi-mouse"></i> Mouse</div>
        </div>
    </div>

    <!-- Game Container -->
    <div class="game-container" id="gameContainer" style="display: none;">
        <div class="game-header">
            <button class="back-btn" onclick="closeGame()">
                <i class="bi bi-arrow-left"></i> Back to Games
            </button>
            <div class="game-score" id="gameScore"></div>
        </div>
        <div class="game-canvas-wrapper" id="gameCanvasWrapper">
            <!-- Games will be rendered here -->
        </div>
    </div>

    <div class="diagnostics-footer">
        <p><i class="bi bi-shield-check"></i> This is our little secret!</p>
        <a href="${pageContext.request.contextPath}/about" class="back-link">
            <i class="bi bi-arrow-left"></i> Back to About
        </a>
    </div>
</div>

<style>
.diagnostics-container {
    width: 100%;
    padding: 40px 24px;
    box-sizing: border-box;
}

.diagnostics-header {
    text-align: center;
    margin-bottom: 40px;
}

.diagnostics-title {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    margin-bottom: 12px;
}

.diagnostics-title i {
    font-size: 48px;
    color: #6c5ce7;
}

.diagnostics-title h1 {
    font-size: 36px;
    font-weight: 700;
    color: #1a1a2e;
    margin: 0;
}

.diagnostics-subtitle {
    font-size: 16px;
    color: #6c757d;
    margin: 0;
}

.games-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 24px;
    margin-bottom: 40px;
    max-width: 1000px;
    margin-left: auto;
    margin-right: auto;
}

.game-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 2px solid #f0f0f0;
}

.game-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 40px rgba(108, 92, 231, 0.2);
    border-color: #6c5ce7;
}

.game-icon {
    width: 64px;
    height: 64px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 16px;
    color: white;
    font-size: 28px;
}

.game-card h3 {
    font-size: 18px;
    font-weight: 600;
    color: #1a1a2e;
    margin: 0 0 8px;
}

.game-card p {
    font-size: 13px;
    color: #6c757d;
    margin: 0 0 12px;
}

.game-controls {
    font-size: 11px;
    color: #adb5bd;
    background: #f8f9fa;
    padding: 6px 12px;
    border-radius: 20px;
    display: inline-flex;
    align-items: center;
    gap: 6px;
}

.game-container {
    background: white;
    border-radius: 16px;
    padding: 24px;
    margin: 0 auto 40px;
    box-shadow: 0 4px 20px rgba(0,0,0,0.1);
    max-width: 600px;
}

.game-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.back-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    background: #f0f0f0;
    border: none;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
}

.back-btn:hover {
    background: #e0e0e0;
}

.game-score {
    font-size: 18px;
    font-weight: 600;
    color: #6c5ce7;
}

.game-canvas-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 400px;
    background: #1a1a2e;
    border-radius: 12px;
    overflow: hidden;
}

.game-canvas-wrapper canvas {
    display: block;
}

.diagnostics-footer {
    text-align: center;
    color: #6c757d;
}

.diagnostics-footer p {
    margin: 0 0 16px;
}

.back-link {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #6c5ce7;
    text-decoration: none;
    font-weight: 500;
}

.back-link:hover {
    text-decoration: underline;
}

/* Memory Game Styles */
.memory-grid {
    display: grid;
    grid-template-columns: repeat(4, 80px);
    gap: 10px;
    padding: 20px;
}

.memory-card {
    width: 80px;
    height: 80px;
    background: #6c5ce7;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 24px;
    color: white;
    transition: all 0.3s;
    user-select: none;
}

.memory-card.flipped, .memory-card.matched {
    background: white;
    color: #1a1a2e;
}

.memory-card.matched {
    opacity: 0.6;
    cursor: default;
}

/* 2048 Styles */
.puzzle-grid {
    display: grid;
    grid-template-columns: repeat(4, 80px);
    gap: 8px;
    padding: 20px;
    background: #bbada0;
    border-radius: 8px;
}

.puzzle-cell {
    width: 80px;
    height: 80px;
    background: rgba(238, 228, 218, 0.35);
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    font-weight: bold;
    color: #776e65;
}

.puzzle-cell[data-value="2"] { background: #eee4da; }
.puzzle-cell[data-value="4"] { background: #ede0c8; }
.puzzle-cell[data-value="8"] { background: #f2b179; color: #f9f6f2; }
.puzzle-cell[data-value="16"] { background: #f59563; color: #f9f6f2; }
.puzzle-cell[data-value="32"] { background: #f67c5f; color: #f9f6f2; }
.puzzle-cell[data-value="64"] { background: #f65e3b; color: #f9f6f2; }
.puzzle-cell[data-value="128"] { background: #edcf72; color: #f9f6f2; font-size: 20px; }
.puzzle-cell[data-value="256"] { background: #edcc61; color: #f9f6f2; font-size: 20px; }
.puzzle-cell[data-value="512"] { background: #edc850; color: #f9f6f2; font-size: 20px; }
.puzzle-cell[data-value="1024"] { background: #edc53f; color: #f9f6f2; font-size: 16px; }
.puzzle-cell[data-value="2048"] { background: #edc22e; color: #f9f6f2; font-size: 16px; }
</style>

<script>
var currentGame = null;
var gameInterval = null;

function openGame(game) {
    document.getElementById('gamesGrid').style.display = 'none';
    document.getElementById('gameContainer').style.display = 'block';
    currentGame = game;
    
    var wrapper = document.getElementById('gameCanvasWrapper');
    wrapper.innerHTML = '';
    
    if (game === 'snake') {
        initSnake(wrapper);
    } else if (game === 'puzzle2048') {
        init2048(wrapper);
    } else if (game === 'memory') {
        initMemory(wrapper);
    } else if (game === 'breakout') {
        initBreakout(wrapper);
    }
}

function closeGame() {
    if (gameInterval) {
        clearInterval(gameInterval);
        gameInterval = null;
    }
    document.getElementById('gamesGrid').style.display = 'grid';
    document.getElementById('gameContainer').style.display = 'none';
    document.getElementById('gameScore').textContent = '';
    currentGame = null;
    document.onkeydown = null;
    document.onkeyup = null;
}

// =====================
// SNAKE GAME
// =====================
function initSnake(wrapper) {
    var canvas = document.createElement('canvas');
    canvas.width = 400;
    canvas.height = 400;
    wrapper.appendChild(canvas);
    
    var ctx = canvas.getContext('2d');
    var gridSize = 20;
    var snake = [{x: 10, y: 10}];
    var food = {x: 15, y: 15};
    var dx = 1, dy = 0;
    var score = 0;
    
    function draw() {
        ctx.fillStyle = '#1a1a2e';
        ctx.fillRect(0, 0, 400, 400);
        
        // Draw food
        ctx.fillStyle = '#ff7675';
        ctx.beginPath();
        ctx.arc(food.x * gridSize + gridSize/2, food.y * gridSize + gridSize/2, gridSize/2 - 2, 0, Math.PI * 2);
        ctx.fill();
        
        // Draw snake
        snake.forEach(function(segment, i) {
            ctx.fillStyle = i === 0 ? '#6c5ce7' : '#a29bfe';
            ctx.fillRect(segment.x * gridSize + 1, segment.y * gridSize + 1, gridSize - 2, gridSize - 2);
        });
        
        document.getElementById('gameScore').textContent = 'Score: ' + score;
    }
    
    function update() {
        var head = {x: snake[0].x + dx, y: snake[0].y + dy};
        
        // Wall collision
        if (head.x < 0 || head.x >= 20 || head.y < 0 || head.y >= 20) {
            clearInterval(gameInterval);
            alert('Game Over! Score: ' + score);
            closeGame();
            return;
        }
        
        // Self collision
        for (var i = 0; i < snake.length; i++) {
            if (snake[i].x === head.x && snake[i].y === head.y) {
                clearInterval(gameInterval);
                alert('Game Over! Score: ' + score);
                closeGame();
                return;
            }
        }
        
        snake.unshift(head);
        
        if (head.x === food.x && head.y === food.y) {
            score += 10;
            food = {x: Math.floor(Math.random() * 20), y: Math.floor(Math.random() * 20)};
        } else {
            snake.pop();
        }
        
        draw();
    }
    
    document.onkeydown = function(e) {
        if (currentGame !== 'snake') return;
        if (e.key === 'ArrowUp' && dy !== 1) { dx = 0; dy = -1; e.preventDefault(); }
        else if (e.key === 'ArrowDown' && dy !== -1) { dx = 0; dy = 1; e.preventDefault(); }
        else if (e.key === 'ArrowLeft' && dx !== 1) { dx = -1; dy = 0; e.preventDefault(); }
        else if (e.key === 'ArrowRight' && dx !== -1) { dx = 1; dy = 0; e.preventDefault(); }
    };
    
    draw();
    gameInterval = setInterval(update, 150);
}

// =====================
// 2048 GAME
// =====================
function init2048(wrapper) {
    var grid = [];
    var score = 0;
    
    var container = document.createElement('div');
    container.style.cssText = 'display: flex; flex-direction: column; align-items: center; padding: 20px;';
    
    var gridEl = document.createElement('div');
    gridEl.className = 'puzzle-grid';
    container.appendChild(gridEl);
    
    wrapper.appendChild(container);
    
    // Initialize grid
    for (var i = 0; i < 16; i++) {
        grid[i] = 0;
    }
    
    addNewTile();
    addNewTile();
    renderGrid();
    
    function addNewTile() {
        var empty = [];
        for (var i = 0; i < 16; i++) {
            if (grid[i] === 0) empty.push(i);
        }
        if (empty.length > 0) {
            var idx = empty[Math.floor(Math.random() * empty.length)];
            grid[idx] = Math.random() < 0.9 ? 2 : 4;
        }
    }
    
    function renderGrid() {
        gridEl.innerHTML = '';
        for (var i = 0; i < 16; i++) {
            var cell = document.createElement('div');
            cell.className = 'puzzle-cell';
            if (grid[i] > 0) {
                cell.textContent = grid[i];
                cell.setAttribute('data-value', grid[i]);
            }
            gridEl.appendChild(cell);
        }
        document.getElementById('gameScore').textContent = 'Score: ' + score;
    }
    
    function slide(row) {
        var arr = row.filter(function(x) { return x !== 0; });
        for (var i = 0; i < arr.length - 1; i++) {
            if (arr[i] === arr[i + 1]) {
                arr[i] *= 2;
                score += arr[i];
                arr.splice(i + 1, 1);
            }
        }
        while (arr.length < 4) arr.push(0);
        return arr;
    }
    
    function move(dir) {
        var moved = false;
        var newGrid = grid.slice();
        
        if (dir === 'left' || dir === 'right') {
            for (var r = 0; r < 4; r++) {
                var row = [grid[r*4], grid[r*4+1], grid[r*4+2], grid[r*4+3]];
                if (dir === 'right') row.reverse();
                row = slide(row);
                if (dir === 'right') row.reverse();
                for (var c = 0; c < 4; c++) {
                    if (newGrid[r*4+c] !== row[c]) moved = true;
                    newGrid[r*4+c] = row[c];
                }
            }
        } else {
            for (var c = 0; c < 4; c++) {
                var col = [grid[c], grid[c+4], grid[c+8], grid[c+12]];
                if (dir === 'down') col.reverse();
                col = slide(col);
                if (dir === 'down') col.reverse();
                for (var r = 0; r < 4; r++) {
                    if (newGrid[r*4+c] !== col[r]) moved = true;
                    newGrid[r*4+c] = col[r];
                }
            }
        }
        
        if (moved) {
            grid = newGrid;
            addNewTile();
            renderGrid();
        }
    }
    
    document.onkeydown = function(e) {
        if (currentGame !== 'puzzle2048') return;
        if (e.key === 'ArrowUp') { e.preventDefault(); move('up'); }
        else if (e.key === 'ArrowDown') { e.preventDefault(); move('down'); }
        else if (e.key === 'ArrowLeft') { e.preventDefault(); move('left'); }
        else if (e.key === 'ArrowRight') { e.preventDefault(); move('right'); }
    };
}

// =====================
// MEMORY GAME
// =====================
function initMemory(wrapper) {
    // Using Bootstrap icons instead of emojis
    var icons = [
        'bi-star-fill', 'bi-heart-fill', 'bi-lightning-fill', 'bi-moon-fill',
        'bi-sun-fill', 'bi-cloud-fill', 'bi-gem', 'bi-trophy-fill'
    ];
    var cards = icons.concat(icons);
    var flipped = [];
    var matched = 0;
    var moves = 0;
    
    // Shuffle
    for (var i = cards.length - 1; i > 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = cards[i];
        cards[i] = cards[j];
        cards[j] = temp;
    }
    
    var container = document.createElement('div');
    container.style.cssText = 'display: flex; flex-direction: column; align-items: center; padding: 20px;';
    
    var gridEl = document.createElement('div');
    gridEl.className = 'memory-grid';
    
    cards.forEach(function(icon, i) {
        var card = document.createElement('div');
        card.className = 'memory-card';
        card.dataset.icon = icon;
        card.dataset.index = i;
        card.innerHTML = '<i class="bi bi-question-lg"></i>';
        card.onclick = function() { flipCard(card); };
        gridEl.appendChild(card);
    });
    
    container.appendChild(gridEl);
    wrapper.appendChild(container);
    
    document.getElementById('gameScore').textContent = 'Moves: 0';
    
    function flipCard(card) {
        if (flipped.length >= 2) return;
        if (card.classList.contains('flipped') || card.classList.contains('matched')) return;
        
        card.classList.add('flipped');
        card.innerHTML = '<i class="bi ' + card.dataset.icon + '"></i>';
        flipped.push(card);
        
        if (flipped.length === 2) {
            moves++;
            document.getElementById('gameScore').textContent = 'Moves: ' + moves;
            
            if (flipped[0].dataset.icon === flipped[1].dataset.icon) {
                flipped[0].classList.add('matched');
                flipped[1].classList.add('matched');
                matched += 2;
                flipped = [];
                
                if (matched === 16) {
                    setTimeout(function() {
                        alert('You Won in ' + moves + ' moves!');
                        closeGame();
                    }, 500);
                }
            } else {
                setTimeout(function() {
                    flipped[0].classList.remove('flipped');
                    flipped[0].innerHTML = '<i class="bi bi-question-lg"></i>';
                    flipped[1].classList.remove('flipped');
                    flipped[1].innerHTML = '<i class="bi bi-question-lg"></i>';
                    flipped = [];
                }, 1000);
            }
        }
    }
}

// =====================
// BREAKOUT GAME
// =====================
function initBreakout(wrapper) {
    var canvas = document.createElement('canvas');
    canvas.width = 480;
    canvas.height = 320;
    wrapper.appendChild(canvas);
    
    var ctx = canvas.getContext('2d');
    
    var ballRadius = 8;
    var x = canvas.width / 2;
    var y = canvas.height - 30;
    var dx = 3;
    var dy = -3;
    
    var paddleHeight = 10;
    var paddleWidth = 75;
    var paddleX = (canvas.width - paddleWidth) / 2;
    
    var brickRowCount = 4;
    var brickColumnCount = 8;
    var brickWidth = 50;
    var brickHeight = 15;
    var brickPadding = 5;
    var brickOffsetTop = 30;
    var brickOffsetLeft = 20;
    
    var score = 0;
    var bricks = [];
    
    var colors = ['#ff7675', '#fdcb6e', '#00b894', '#6c5ce7'];
    
    for (var c = 0; c < brickColumnCount; c++) {
        bricks[c] = [];
        for (var r = 0; r < brickRowCount; r++) {
            bricks[c][r] = { x: 0, y: 0, status: 1 };
        }
    }
    
    var rightPressed = false;
    var leftPressed = false;
    
    document.onkeydown = function(e) {
        if (currentGame !== 'breakout') return;
        if (e.key === 'Right' || e.key === 'ArrowRight') { rightPressed = true; e.preventDefault(); }
        else if (e.key === 'Left' || e.key === 'ArrowLeft') { leftPressed = true; e.preventDefault(); }
    };
    
    document.onkeyup = function(e) {
        if (currentGame !== 'breakout') return;
        if (e.key === 'Right' || e.key === 'ArrowRight') rightPressed = false;
        else if (e.key === 'Left' || e.key === 'ArrowLeft') leftPressed = false;
    };
    
    canvas.onmousemove = function(e) {
        var relativeX = e.clientX - canvas.getBoundingClientRect().left;
        if (relativeX > paddleWidth/2 && relativeX < canvas.width - paddleWidth/2) {
            paddleX = relativeX - paddleWidth / 2;
        }
    };
    
    function drawBricks() {
        for (var c = 0; c < brickColumnCount; c++) {
            for (var r = 0; r < brickRowCount; r++) {
                if (bricks[c][r].status === 1) {
                    var brickX = c * (brickWidth + brickPadding) + brickOffsetLeft;
                    var brickY = r * (brickHeight + brickPadding) + brickOffsetTop;
                    bricks[c][r].x = brickX;
                    bricks[c][r].y = brickY;
                    ctx.beginPath();
                    ctx.roundRect(brickX, brickY, brickWidth, brickHeight, 3);
                    ctx.fillStyle = colors[r];
                    ctx.fill();
                    ctx.closePath();
                }
            }
        }
    }
    
    function drawBall() {
        ctx.beginPath();
        ctx.arc(x, y, ballRadius, 0, Math.PI * 2);
        ctx.fillStyle = '#fff';
        ctx.fill();
        ctx.closePath();
    }
    
    function drawPaddle() {
        ctx.beginPath();
        ctx.roundRect(paddleX, canvas.height - paddleHeight - 5, paddleWidth, paddleHeight, 5);
        ctx.fillStyle = '#6c5ce7';
        ctx.fill();
        ctx.closePath();
    }
    
    function collisionDetection() {
        for (var c = 0; c < brickColumnCount; c++) {
            for (var r = 0; r < brickRowCount; r++) {
                var b = bricks[c][r];
                if (b.status === 1) {
                    if (x > b.x && x < b.x + brickWidth && y > b.y && y < b.y + brickHeight) {
                        dy = -dy;
                        b.status = 0;
                        score += 10;
                        if (score === brickRowCount * brickColumnCount * 10) {
                            clearInterval(gameInterval);
                            alert('You Win! Score: ' + score);
                            closeGame();
                        }
                    }
                }
            }
        }
    }
    
    function draw() {
        ctx.fillStyle = '#1a1a2e';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        
        drawBricks();
        drawBall();
        drawPaddle();
        collisionDetection();
        
        document.getElementById('gameScore').textContent = 'Score: ' + score;
        
        // Ball collision with walls
        if (x + dx > canvas.width - ballRadius || x + dx < ballRadius) dx = -dx;
        if (y + dy < ballRadius) dy = -dy;
        else if (y + dy > canvas.height - ballRadius - paddleHeight - 5) {
            if (x > paddleX && x < paddleX + paddleWidth) {
                dy = -dy;
            } else if (y + dy > canvas.height - ballRadius) {
                clearInterval(gameInterval);
                alert('Game Over! Score: ' + score);
                closeGame();
                return;
            }
        }
        
        if (rightPressed && paddleX < canvas.width - paddleWidth) paddleX += 5;
        else if (leftPressed && paddleX > 0) paddleX -= 5;
        
        x += dx;
        y += dy;
    }
    
    gameInterval = setInterval(draw, 16);
}
</script>

</layout:mailLayout>
