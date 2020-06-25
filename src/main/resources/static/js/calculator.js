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
var value = "0", ans = null, clear = true;

function updateDisplay() {
    display.innerHTML = value;
    if(value === "0") {
        clear = true;
        clearButton.innerHTML = "C";
    } else if(!ans) {
        clearButton.innerHTML = "C";
    } else {
        clearButton.innerHTML = "CE";
    }
}

function typeCharacter(c) {
    try {
        if(c === '.' || c === 'E') {
            if(!value.includes(c)) {
                value += c;
                updateDisplay();
            }
        } else {
            if(clear) {
                value = c;
                clear = false;
            } else {
                value += c;
            }

            updateDisplay();
        }
    } catch(e) {
        alert('An unexpected exception occurred:\n'+e)
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
    if(value.includes('E')) {
        if(value.includes('E-')) {
            value = value.replace('E-', 'E');
        } else {
            value = value.replace('E', 'E-');
        }
    } else {
        value = -value + "";
    }
    updateDisplay();
}

function square() {
    value = value * value + "";
    clear = true;
    updateDisplay();
}

function cube() {
    value = value * value * value + "";
    clear = true;
    updateDisplay();
}

clearButton.addEventListener('click', function(e) {
    if(this.innerHTML === 'C') {
        ans = null;
    }

    value = "0";
    updateDisplay();
}, false);

function insertE() {
    value = Math.E + "";
    clear = true;
    updateDisplay();
}

function insertPi() {
    value = Math.PI + "";
    clear = true;
    updateDisplay();
}

function insertRand() {
    value = Math.random() + "";
    clear = true;
    updateDisplay();
}

function pow10() {
    value = Math.pow(10, value) + "";
    clear = true;
    updateDisplay();
}

function log() {
    value = Math.log10(value) + "";
    clear = true;
    updateDisplay();
}

function ln() {
    value = Math.log(value) + "";
    clear = true;
    updateDisplay();
}

function sqrt() {
    value = Math.sqrt(value) + "";
    clear = true;
    updateDisplay();
}

function cbrt() {
    value = Math.cbrt(value) + "";
    clear = true;
    updateDisplay();
}

function pow2() {
    value = Math.pow(2, value) + "";
    clear = true;
    updateDisplay();
}

function powE() {
    value = Math.exp(value) + "";
    clear = true;
    updateDisplay();
}

function inv() {
    value = 1/value + "";
    clear = true;
    updateDisplay();
}

function abs() {
    value = Math.abs(value) + "";
    clear = true;
    updateDisplay();
}

// via the Lanczos approximation, a lá apelsinapa on StackOverflow
function gamma(n) {
    //some magic constants
    var g = 7, // g represents the precision desired, p is the values of p[i] to plug into Lanczos' formula
        p = [0.99999999999980993, 676.5203681218851, -1259.1392167224028, 771.32342877765313, -176.61502916214059, 12.507343278686905, -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7];
    if (n < 0.5) {
        return Math.PI / Math.sin(n * Math.PI) / gamma(1 - n);
    } else {
        n--;
        var x = p[0];
        for (var i = 1; i < g + 2; i++) {
            x += p[i] / (n + i);
        }
        var t = n + g + 0.5;
        return Math.sqrt(2 * Math.PI) * Math.pow(t, (n + 0.5)) * Math.exp(-t) * x;
    }
}

function fact() {
    if(value > 0 && value % 1 == 0) {
        var a = 1;
        for(var i = 2; i <= value; i++) {
            a *= i;
        }
        value = a + "";
    } else {
        value = gamma(++value) + "";
    }

    clear = true;
    updateDisplay();
}