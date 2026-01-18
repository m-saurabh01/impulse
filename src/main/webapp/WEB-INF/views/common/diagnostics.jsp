<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:mailLayout pageTitle="System Diagnostics">

<div class="diagnostics-container">
    <div class="diagnostics-top-nav">
        <a href="${pageContext.request.contextPath}/about" class="back-link">
            <i class="bi bi-arrow-left"></i> Back to About
        </a>
    </div>
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

        <!-- Flappy Bird Game -->
        <div class="game-card" onclick="openGame('flappy')">
            <div class="game-icon" style="background: linear-gradient(135deg, #00cec9, #81ecec);">
                <i class="bi bi-twitter"></i>
            </div>
            <h3>Flappy Bird</h3>
            <p>Tap to fly through the pipes!</p>
            <div class="game-controls"><i class="bi bi-keyboard"></i> Space / <i class="bi bi-mouse"></i> Click</div>
        </div>

        <!-- Tetris Game -->
        <div class="game-card" onclick="openGame('tetris')">
            <div class="game-icon" style="background: linear-gradient(135deg, #fd79a8, #e84393);">
                <i class="bi bi-boxes"></i>
            </div>
            <h3>Tetris</h3>
            <p>Stack blocks, clear lines, don't fill up!</p>
            <div class="game-controls"><i class="bi bi-keyboard"></i> Arrow Keys + Space</div>
        </div>

        <!-- Whack-a-Mole Game -->
        <div class="game-card" onclick="openGame('whackamole')">
            <div class="game-icon" style="background: linear-gradient(135deg, #a55eea, #8854d0);">
                <i class="bi bi-hammer"></i>
            </div>
            <h3>Whack-a-Mole</h3>
            <p>Whack the moles before they hide!</p>
            <div class="game-controls"><i class="bi bi-mouse"></i> Click to Whack</div>
        </div>

        <!-- Typing Speed Game -->
        <div class="game-card" onclick="openGame('typing')">
            <div class="game-icon" style="background: linear-gradient(135deg, #0984e3, #74b9ff);">
                <i class="bi bi-keyboard-fill"></i>
            </div>
            <h3>Type Racer</h3>
            <p>Test your typing speed and accuracy!</p>
            <div class="game-controls"><i class="bi bi-keyboard"></i> Type the Words</div>
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
    </div>
</div>

<style>
.diagnostics-container {
    width: 100%;
    padding: 40px 24px;
    box-sizing: border-box;
}

.diagnostics-top-nav {
    margin-bottom: 24px;
}

.diagnostics-top-nav .back-link {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #6c5ce7;
    text-decoration: none;
    font-weight: 500;
    padding: 10px 16px;
    background: rgba(108, 92, 231, 0.1);
    border-radius: 8px;
    transition: all 0.2s;
}

.diagnostics-top-nav .back-link:hover {
    background: rgba(108, 92, 231, 0.2);
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
    padding-bottom: 16px;
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

/* Whack-a-Mole Styles */
.whack-container {
    padding: 20px;
    text-align: center;
    width: 400px;
    height: 400px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.whack-timer {
    font-size: 24px;
    font-weight: bold;
    color: white;
    margin-bottom: 15px;
}

.whack-grid {
    display: grid;
    grid-template-columns: repeat(3, 110px);
    gap: 12px;
    justify-content: center;
}

.whack-hole {
    width: 110px;
    height: 110px;
    background: linear-gradient(145deg, #5d4e37, #3d3225);
    border-radius: 50%;
    position: relative;
    cursor: pointer;
    overflow: hidden;
    box-shadow: inset 0 10px 20px rgba(0,0,0,0.5);
}

.whack-hole .mole {
    position: absolute;
    bottom: -70px;
    left: 50%;
    transform: translateX(-50%);
    width: 70px;
    height: 70px;
    background: linear-gradient(145deg, #8b7355, #6b5344);
    border-radius: 50% 50% 40% 40%;
    transition: bottom 0.15s ease-out;
    display: flex;
    align-items: center;
    justify-content: center;
}

.whack-hole .mole .mole-face,
.whack-hole .mole .mole-hit {
    font-size: 32px;
    color: #2d3436;
}

.whack-hole .mole .mole-face {
    display: block;
}

.whack-hole .mole .mole-hit {
    display: none;
}

.whack-hole.active .mole {
    bottom: 25px;
}

.whack-hole.whacked .mole .mole-face {
    display: none;
}

.whack-hole.whacked .mole .mole-hit {
    display: block;
    color: #e74c3c;
    animation: hitShake 0.3s ease;
}

@keyframes hitShake {
    0%, 100% { transform: rotate(0deg); }
    25% { transform: rotate(-15deg); }
    75% { transform: rotate(15deg); }
}

/* Typing Game Styles */
.typing-container {
    padding: 20px;
    width: 100%;
    max-width: 600px;
    min-height: 400px;
    display: flex;
    flex-direction: column;
    margin: 0 auto;
}

.typing-stats {
    display: flex;
    justify-content: center;
    gap: 30px;
    margin-bottom: 20px;
    flex-shrink: 0;
}

.typing-stats .stat {
    background: rgba(108, 92, 231, 0.2);
    padding: 12px 24px;
    border-radius: 12px;
    color: white;
    font-size: 18px;
}

.typing-stats .stat span {
    font-weight: bold;
    font-size: 24px;
    color: #a29bfe;
}

.typing-display {
    background: rgba(255,255,255,0.1);
    padding: 20px;
    border-radius: 12px;
    margin-bottom: 20px;
    min-height: 120px;
    max-height: 180px;
    overflow-y: auto;
    font-size: 20px;
    line-height: 2;
    color: rgba(255,255,255,0.6);
    text-align: left;
    flex-shrink: 0;
}

.typing-display .word {
    padding: 4px 2px;
    border-radius: 4px;
}

.typing-display .word.current {
    background: rgba(108, 92, 231, 0.5);
    color: white;
}

.typing-display .word.typed {
    color: #00b894;
}

.typing-input-wrapper {
    flex-shrink: 0;
    margin-top: auto;
}

.typing-input {
    width: 100%;
    padding: 16px 20px;
    font-size: 20px;
    border: 2px solid #6c5ce7;
    border-radius: 12px;
    background: rgba(255,255,255,0.95);
    color: #333;
    outline: none;
    box-sizing: border-box;
}

.typing-input::placeholder {
    color: #999;
}

.typing-input:focus {
    border-color: #a29bfe;
    box-shadow: 0 0 0 4px rgba(108, 92, 231, 0.3);
}

.typing-hint {
    margin-top: 12px;
    color: rgba(255,255,255,0.5);
    font-size: 14px;
    text-align: center;
}
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
    } else if (game === 'flappy') {
        initFlappy(wrapper);
    } else if (game === 'tetris') {
        initTetris(wrapper);
    } else if (game === 'whackamole') {
        initWhackaMole(wrapper);
    } else if (game === 'typing') {
        initTyping(wrapper);
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
    canvas.width = 400;
    canvas.height = 400;
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

// =====================
// FLAPPY BIRD GAME
// =====================
function initFlappy(wrapper) {
    var canvas = document.createElement('canvas');
    canvas.width = 400;
    canvas.height = 400;
    wrapper.appendChild(canvas);
    
    var ctx = canvas.getContext('2d');
    var bird = { x: 80, y: 250, velocity: 0, gravity: 0.5, jump: -8, size: 20 };
    var pipes = [];
    var pipeWidth = 60;
    var pipeGap = 140;
    var score = 0;
    var gameOver = false;
    var frameCount = 0;
    
    function createPipe() {
        var minHeight = 50;
        var maxHeight = canvas.height - pipeGap - minHeight;
        var height = Math.floor(Math.random() * (maxHeight - minHeight + 1)) + minHeight;
        pipes.push({
            x: canvas.width,
            topHeight: height,
            bottomY: height + pipeGap,
            passed: false
        });
    }
    
    function flap() {
        if (gameOver) {
            // Restart
            bird.y = 250;
            bird.velocity = 0;
            pipes = [];
            score = 0;
            gameOver = false;
            frameCount = 0;
        } else {
            bird.velocity = bird.jump;
        }
    }
    
    document.onkeydown = function(e) {
        if (e.code === 'Space') {
            e.preventDefault();
            flap();
        }
    };
    
    canvas.onclick = flap;
    
    function draw() {
        // Background gradient
        var gradient = ctx.createLinearGradient(0, 0, 0, canvas.height);
        gradient.addColorStop(0, '#87CEEB');
        gradient.addColorStop(1, '#98FB98');
        ctx.fillStyle = gradient;
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        
        if (!gameOver) {
            // Update bird
            bird.velocity += bird.gravity;
            bird.y += bird.velocity;
            
            // Create pipes
            frameCount++;
            if (frameCount % 90 === 0) {
                createPipe();
            }
            
            // Update pipes
            for (var i = pipes.length - 1; i >= 0; i--) {
                pipes[i].x -= 3;
                
                // Score
                if (!pipes[i].passed && pipes[i].x + pipeWidth < bird.x) {
                    pipes[i].passed = true;
                    score++;
                }
                
                // Remove off-screen pipes
                if (pipes[i].x + pipeWidth < 0) {
                    pipes.splice(i, 1);
                }
            }
            
            // Check collisions
            if (bird.y + bird.size > canvas.height || bird.y - bird.size < 0) {
                gameOver = true;
            }
            
            for (var i = 0; i < pipes.length; i++) {
                var p = pipes[i];
                if (bird.x + bird.size > p.x && bird.x - bird.size < p.x + pipeWidth) {
                    if (bird.y - bird.size < p.topHeight || bird.y + bird.size > p.bottomY) {
                        gameOver = true;
                    }
                }
            }
        }
        
        // Draw pipes
        ctx.fillStyle = '#2ecc71';
        for (var i = 0; i < pipes.length; i++) {
            var p = pipes[i];
            // Top pipe
            ctx.fillRect(p.x, 0, pipeWidth, p.topHeight);
            ctx.fillStyle = '#27ae60';
            ctx.fillRect(p.x - 5, p.topHeight - 30, pipeWidth + 10, 30);
            ctx.fillStyle = '#2ecc71';
            // Bottom pipe
            ctx.fillRect(p.x, p.bottomY, pipeWidth, canvas.height - p.bottomY);
            ctx.fillStyle = '#27ae60';
            ctx.fillRect(p.x - 5, p.bottomY, pipeWidth + 10, 30);
            ctx.fillStyle = '#2ecc71';
        }
        
        // Draw bird
        ctx.fillStyle = '#f1c40f';
        ctx.beginPath();
        ctx.arc(bird.x, bird.y, bird.size, 0, Math.PI * 2);
        ctx.fill();
        // Eye
        ctx.fillStyle = 'white';
        ctx.beginPath();
        ctx.arc(bird.x + 8, bird.y - 5, 6, 0, Math.PI * 2);
        ctx.fill();
        ctx.fillStyle = 'black';
        ctx.beginPath();
        ctx.arc(bird.x + 10, bird.y - 5, 3, 0, Math.PI * 2);
        ctx.fill();
        // Beak
        ctx.fillStyle = '#e67e22';
        ctx.beginPath();
        ctx.moveTo(bird.x + bird.size, bird.y);
        ctx.lineTo(bird.x + bird.size + 12, bird.y + 3);
        ctx.lineTo(bird.x + bird.size, bird.y + 8);
        ctx.fill();
        
        // Score
        document.getElementById('gameScore').textContent = 'Score: ' + score;
        
        // Game over
        if (gameOver) {
            ctx.fillStyle = 'rgba(0,0,0,0.5)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = 'white';
            ctx.font = 'bold 36px Arial';
            ctx.textAlign = 'center';
            ctx.fillText('Game Over!', canvas.width/2, canvas.height/2 - 20);
            ctx.font = '20px Arial';
            ctx.fillText('Score: ' + score, canvas.width/2, canvas.height/2 + 20);
            ctx.fillText('Click or Space to restart', canvas.width/2, canvas.height/2 + 50);
        }
    }
    
    gameInterval = setInterval(draw, 20);
}

// =====================
// TETRIS GAME
// =====================
function initTetris(wrapper) {
    var canvas = document.createElement('canvas');
    canvas.width = 400;
    canvas.height = 400;
    wrapper.appendChild(canvas);
    
    var ctx = canvas.getContext('2d');
    var blockSize = 20;
    var cols = 10;
    var rows = 20;
    var offsetX = (canvas.width - cols * blockSize) / 2; // Center horizontally
    var board = [];
    var score = 0;
    var gameOver = false;
    
    // Initialize board
    for (var y = 0; y < rows; y++) {
        board[y] = [];
        for (var x = 0; x < cols; x++) {
            board[y][x] = 0;
        }
    }
    
    var shapes = [
        [[1,1,1,1]], // I
        [[1,1],[1,1]], // O
        [[0,1,0],[1,1,1]], // T
        [[1,0,0],[1,1,1]], // L
        [[0,0,1],[1,1,1]], // J
        [[0,1,1],[1,1,0]], // S
        [[1,1,0],[0,1,1]]  // Z
    ];
    
    var colors = ['#00d2d3', '#feca57', '#a55eea', '#ff9f43', '#54a0ff', '#5f27cd', '#ee5253'];
    
    var currentPiece = null;
    var currentX = 0;
    var currentY = 0;
    var currentColor = '';
    
    function newPiece() {
        var idx = Math.floor(Math.random() * shapes.length);
        currentPiece = shapes[idx].map(function(row) { return row.slice(); });
        currentColor = colors[idx];
        currentX = Math.floor(cols / 2) - Math.floor(currentPiece[0].length / 2);
        currentY = 0;
        
        if (!canMove(0, 0)) {
            gameOver = true;
        }
    }
    
    function canMove(dx, dy, piece) {
        piece = piece || currentPiece;
        for (var y = 0; y < piece.length; y++) {
            for (var x = 0; x < piece[y].length; x++) {
                if (piece[y][x]) {
                    var newX = currentX + x + dx;
                    var newY = currentY + y + dy;
                    if (newX < 0 || newX >= cols || newY >= rows) return false;
                    if (newY >= 0 && board[newY][newX]) return false;
                }
            }
        }
        return true;
    }
    
    function rotate() {
        var rotated = [];
        for (var x = 0; x < currentPiece[0].length; x++) {
            rotated[x] = [];
            for (var y = currentPiece.length - 1; y >= 0; y--) {
                rotated[x].push(currentPiece[y][x]);
            }
        }
        if (canMove(0, 0, rotated)) {
            currentPiece = rotated;
        }
    }
    
    function lockPiece() {
        for (var y = 0; y < currentPiece.length; y++) {
            for (var x = 0; x < currentPiece[y].length; x++) {
                if (currentPiece[y][x] && currentY + y >= 0) {
                    board[currentY + y][currentX + x] = currentColor;
                }
            }
        }
        clearLines();
        newPiece();
    }
    
    function clearLines() {
        var linesCleared = 0;
        for (var y = rows - 1; y >= 0; y--) {
            var full = true;
            for (var x = 0; x < cols; x++) {
                if (!board[y][x]) { full = false; break; }
            }
            if (full) {
                board.splice(y, 1);
                board.unshift(new Array(cols).fill(0));
                linesCleared++;
                y++; // Check same row again
            }
        }
        score += linesCleared * 100;
    }
    
    document.onkeydown = function(e) {
        if (gameOver) return;
        if (e.code === 'ArrowLeft' && canMove(-1, 0)) { currentX--; }
        else if (e.code === 'ArrowRight' && canMove(1, 0)) { currentX++; }
        else if (e.code === 'ArrowDown' && canMove(0, 1)) { currentY++; }
        else if (e.code === 'ArrowUp' || e.code === 'Space') { rotate(); }
        e.preventDefault();
    };
    
    function draw() {
        ctx.fillStyle = '#1a1a2e';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        
        // Draw board
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                if (board[y][x]) {
                    ctx.fillStyle = board[y][x];
                    ctx.fillRect(offsetX + x * blockSize, y * blockSize, blockSize - 1, blockSize - 1);
                } else {
                    ctx.fillStyle = '#2d2d44';
                    ctx.fillRect(offsetX + x * blockSize, y * blockSize, blockSize - 1, blockSize - 1);
                }
            }
        }
        
        // Draw current piece
        if (currentPiece && !gameOver) {
            ctx.fillStyle = currentColor;
            for (var y = 0; y < currentPiece.length; y++) {
                for (var x = 0; x < currentPiece[y].length; x++) {
                    if (currentPiece[y][x]) {
                        ctx.fillRect(offsetX + (currentX + x) * blockSize, (currentY + y) * blockSize, blockSize - 1, blockSize - 1);
                    }
                }
            }
        }
        
        document.getElementById('gameScore').textContent = 'Score: ' + score;
        
        if (gameOver) {
            ctx.fillStyle = 'rgba(0,0,0,0.7)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = 'white';
            ctx.font = 'bold 28px Arial';
            ctx.textAlign = 'center';
            ctx.fillText('Game Over!', canvas.width/2, canvas.height/2);
            ctx.font = '16px Arial';
            ctx.fillText('Score: ' + score, canvas.width/2, canvas.height/2 + 30);
        }
    }
    
    function gameLoop() {
        if (!gameOver && canMove(0, 1)) {
            currentY++;
        } else if (!gameOver) {
            lockPiece();
        }
        draw();
    }
    
    newPiece();
    draw();
    gameInterval = setInterval(gameLoop, 500);
}

// =====================
// WHACK-A-MOLE GAME
// =====================
function initWhackaMole(wrapper) {
    var container = document.createElement('div');
    container.className = 'whack-container';
    container.innerHTML = '<div class="whack-timer">Time: <span id="whackTimer">30</span>s</div><div class="whack-grid" id="whackGrid"></div>';
    wrapper.appendChild(container);
    
    var grid = document.getElementById('whackGrid');
    var score = 0;
    var timeLeft = 30;
    var activeMole = null;
    var moleTimeout = null;
    
    // Create holes
    for (var i = 0; i < 9; i++) {
        var hole = document.createElement('div');
        hole.className = 'whack-hole';
        hole.dataset.index = i;
        hole.innerHTML = '<div class="mole"><i class="bi bi-emoji-smile-fill mole-face"></i><i class="bi bi-emoji-dizzy-fill mole-hit"></i></div>';
        hole.onclick = function() {
            if (this.classList.contains('active')) {
                score += 10;
                this.classList.remove('active');
                this.classList.add('whacked');
                setTimeout(function(el) { el.classList.remove('whacked'); }, 300, this);
                document.getElementById('gameScore').textContent = 'Score: ' + score;
                showNextMole();
            }
        };
        grid.appendChild(hole);
    }
    
    var holes = grid.querySelectorAll('.whack-hole');
    
    function showNextMole() {
        if (moleTimeout) clearTimeout(moleTimeout);
        
        // Hide current mole
        holes.forEach(function(h) { h.classList.remove('active'); });
        
        if (timeLeft <= 0) return;
        
        // Show random mole
        var randomIndex = Math.floor(Math.random() * 9);
        holes[randomIndex].classList.add('active');
        
        // Auto-hide after random time
        moleTimeout = setTimeout(function() {
            holes[randomIndex].classList.remove('active');
            showNextMole();
        }, 600 + Math.random() * 800);
    }
    
    function updateTimer() {
        timeLeft--;
        document.getElementById('whackTimer').textContent = timeLeft;
        if (timeLeft <= 0) {
            clearTimeout(moleTimeout);
            holes.forEach(function(h) { h.classList.remove('active'); });
            alert('Time\'s up! Final Score: ' + score);
            closeGame();
        }
    }
    
    document.getElementById('gameScore').textContent = 'Score: 0';
    showNextMole();
    gameInterval = setInterval(updateTimer, 1000);
}

// =====================
// TYPING SPEED GAME
// =====================
function initTyping(wrapper) {
    var container = document.createElement('div');
    container.className = 'typing-container';
    container.innerHTML = 
        '<div class="typing-stats">' +
            '<div class="stat"><span id="typingWpm">0</span> WPM</div>' +
            '<div class="stat"><span id="typingAccuracy">100</span>% Accuracy</div>' +
            '<div class="stat"><span id="typingTimer">60</span>s</div>' +
        '</div>' +
        '<div class="typing-display" id="typingDisplay"></div>' +
        '<div class="typing-input-wrapper">' +
            '<input type="text" class="typing-input" id="typingInput" placeholder="Start typing here..." autocomplete="off" />' +
            '<div class="typing-hint">Type the highlighted word and press Space to continue</div>' +
        '</div>';
    wrapper.appendChild(container);
    
    var words = ['the','be','to','of','and','a','in','that','have','i','it','for','not','on','with','he','as','you','do','at','this','but','his','by','from','they','we','say','her','she','or','an','will','my','one','all','would','there','their','what','so','up','out','if','about','who','get','which','go','me','when','make','can','like','time','no','just','him','know','take','people','into','year','your','good','some','could','them','see','other','than','then','now','look','only','come','its','over','think','also','back','after','use','two','how','our','work','first','well','way','even','new','want','because','any','these','give','day','most','us','code','java','spring','email','server','data','user','app','web','test','build','run','class','method'];
    
    var currentWords = [];
    var currentIndex = 0;
    var correctChars = 0;
    var totalChars = 0;
    var startTime = null;
    var timeLeft = 60;
    var gameStarted = false;
    
    function generateWords() {
        currentWords = [];
        for (var i = 0; i < 50; i++) {
            currentWords.push(words[Math.floor(Math.random() * words.length)]);
        }
        renderWords();
    }
    
    function renderWords() {
        var display = document.getElementById('typingDisplay');
        display.innerHTML = currentWords.map(function(word, idx) {
            var cls = idx < currentIndex ? 'typed' : (idx === currentIndex ? 'current' : '');
            return '<span class="word ' + cls + '">' + word + '</span>';
        }).join(' ');
    }
    
    var input = document.getElementById('typingInput');
    input.focus();
    
    input.oninput = function() {
        if (!gameStarted) {
            gameStarted = true;
            startTime = Date.now();
            gameInterval = setInterval(updateTypingTimer, 1000);
        }
    };
    
    input.onkeydown = function(e) {
        if (e.code === 'Space') {
            e.preventDefault();
            var typed = input.value.trim();
            var expected = currentWords[currentIndex];
            
            totalChars += expected.length;
            if (typed === expected) {
                correctChars += expected.length;
            }
            
            currentIndex++;
            input.value = '';
            renderWords();
            updateStats();
            
            if (currentIndex >= currentWords.length) {
                generateWords();
                currentIndex = 0;
            }
        }
    };
    
    function updateStats() {
        var elapsed = (Date.now() - startTime) / 1000 / 60; // minutes
        var wpm = elapsed > 0 ? Math.round((correctChars / 5) / elapsed) : 0;
        var accuracy = totalChars > 0 ? Math.round((correctChars / totalChars) * 100) : 100;
        
        document.getElementById('typingWpm').textContent = wpm;
        document.getElementById('typingAccuracy').textContent = accuracy;
        document.getElementById('gameScore').textContent = wpm + ' WPM';
    }
    
    function updateTypingTimer() {
        timeLeft--;
        document.getElementById('typingTimer').textContent = timeLeft;
        if (timeLeft <= 0) {
            clearInterval(gameInterval);
            input.disabled = true;
            var elapsed = 60 / 60;
            var wpm = Math.round((correctChars / 5) / elapsed);
            var accuracy = totalChars > 0 ? Math.round((correctChars / totalChars) * 100) : 100;
            alert('Time\'s up!\nWPM: ' + wpm + '\nAccuracy: ' + accuracy + '%');
            closeGame();
        }
    }
    
    generateWords();
}
</script>

</layout:mailLayout>
