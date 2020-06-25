try {
    document.addEventListener('keydown', function(e) {
        var up = document.getElementById('up'),
                right = document.getElementById('right'),
                down = document.getElementById('down'),
                left = document.getElementById('left');
        if(e.keyCode == 38) { // up
            up.style.display = 'table-row-group';
            right.style.display = 'none';
            down.style.display = 'none';
            left.style.display = 'none';
        } else if(e.keyCode == 39) { // right
            up.style.display = 'none';
            right.style.display = 'table-row-group';
            down.style.display = 'none';
            left.style.display = 'none';
        } else if(e.keyCode == 40) { // down
            up.style.display = 'none';
            right.style.display = 'none';
            down.style.display = 'table-row-group';
            left.style.display = 'none';
        } else if(e.keyCode == 37) { // left
            up.style.display = 'none';
            right.style.display = 'none';
            down.style.display = 'none';
            left.style.display = 'table-row-group';
        }
    }, false);
} catch(e) {
    alert('Attaching listener exception:\n' + e);
}

var display = document.getElementById('display'),
        clearButton = document.getElementById('clear-button');
var value = "0", ans = undefined;

function updateDisplay() {
    display.innerHTML = value;

    if(value === "0") {
        clearButton.innerHTML = "C";
    } else {
        clearButton.innerHTML = "CE";
    }
}

function typeCharacter(c) {
    if(c === '.') {
        if(!value.includes('.')) {
            value += '.';
            updateDisplay();
        }
    } else {
        if(value == "0") {
            value = c;
        } else {
            value += c;
        }

        updateDisplay();
    }
}

function deleteCharacter() {
    if(value !== "0") {
        value = value.substring(0, value.length - 1);
        if(value === "") {
            value = "0";
        }

        updateDisplay();
    }
}

function negate() {
    value = -value + "";
    updateDisplay();
}

function square() {
    value = value * value + "";
    updateDisplay();
}

function cube() {
    value = value * value * value + "";
    updateDisplay();
}

clearButton.addEventListener('click', function(e) {
    if(this.innerHTML === 'CE') {
        value = "0";
        updateDisplay();
    } else {
        ans = undefined;
    }
}, false);

function insertE() {
    value = Math.E + "";
    updateDisplay();
}

function insertPi() {
    value = Math.PI + "";
    updateDisplay();
}

function insertRand() {
    value = Math.random() + "";
    updateDisplay();
}