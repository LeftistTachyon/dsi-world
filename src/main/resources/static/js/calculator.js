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
var value = "0", ans = null, clear = true,
        lastOperation = null, mode = "Deg";

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
    if(!clear) {
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
    ans = value = value * value + "";
    clear = true;
    updateDisplay();
}

function cube() {
    ans = value = value * value * value + "";
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
    ans = value = Math.pow(10, value) + "";
    clear = true;
    updateDisplay();
}

function log() {
    ans = value = Math.log10(value) + "";
    clear = true;
    updateDisplay();
}

function ln() {
    ans = value = Math.log(value) + "";
    clear = true;
    updateDisplay();
}

function sqrt() {
    ans = value = Math.sqrt(value) + "";
    clear = true;
    updateDisplay();
}

function cbrt() {
    ans = value = Math.cbrt(value) + "";
    clear = true;
    updateDisplay();
}

function pow2() {
    ans = value = Math.pow(2, value) + "";
    clear = true;
    updateDisplay();
}

function powE() {
    ans = value = Math.exp(value) + "";
    clear = true;
    updateDisplay();
}

function inv() {
    ans = value = 1/value + "";
    clear = true;
    updateDisplay();
}

function abs() {
    ans = value = Math.abs(value) + "";
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
        ans = value = a + "";
    } else {
        ans = value = gamma(++value) + "";
    }

    clear = true;
    updateDisplay();
}

function floor() {
    ans = value = Math.floor(value) + "";
    clear = true;
    updateDisplay();
}

function ceiling() {
    ans = value = Math.ceil(value) + "";
    clear = true;
    updateDisplay();
}

function decimal() {
    ans = value = value - Math.floor(value) + "";
    clear = true;
    updateDisplay();
}

// angle manipulation
function toDegrees(ang, from) {
    if(from === undefined)
        from = mode;
    switch(from) {
        case "Deg":
            return ang;
        case "Rad":
            return ang*180/Math.PI;
        case "Grad":
            return ang * 10 / 9;
    }
}

function toRadians(ang, from) {
    if(from === undefined)
        from = mode;
    switch(from) {
        case "Deg":
            return ang/180*Math.PI;
        case "Rad":
            return ang;
        case "Grad":
            return ang/200*Math.PI;
    }
}

document.getElementById('mode-button').addEventListener('click', function() {
    switch(mode) {
        case "Deg":
            mode = "Rad";
            break;
        case "Rad":
            mode = "Grad";
            break;
        case "Grad":
            mode = "Deg";
            break;
    }
    this.innerHTML = mode;
}, false);

// TRIG
function sin() {
    ans = value = Math.sin(toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function cos() {
    ans = value = Math.cos(toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function tan() {
    ans = value = Math.tan(toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function csc() {
    ans = value = 1/Math.sin(toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function sec() {
    ans = value = 1/Math.cos(toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function cot() {
    ans = value = 1/Math.tan(toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function asin() {
    ans = value = Math.asin(toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function acos() {
    ans = value = Math.acos(toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function atan() {
    ans = value = Math.atan(toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function acsc() {
    ans = value = Math.asin(1/toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function asec() {
    ans = value = Math.acos(1/toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function acot() {
    ans = value = Math.atan(1/toRadians(value)) + "";
    clear = true;
    updateDisplay();
}

function toDeg() {
    value = toDegrees(value, 'Rad');
    clear = true;
    updateDisplay();
}

function toRad() {
    value = toRadians(value, 'Deg');
    clear = true;
    updateDisplay();
}

function sinh() {
    ans = value = Math.sinh(value) + "";
    clear = true;
    updateDisplay();
}

function cosh() {
    ans = value = Math.cosh(value) + "";
    clear = true;
    updateDisplay();
}

function tanh() {
    ans = value = Math.tanh(value) + "";
    clear = true;
    updateDisplay();
}

function csch() {
    ans = value = 1/Math.sinh(value) + "";
    clear = true;
    updateDisplay();
}

function sech() {
    ans = value = 1/Math.cosh(value) + "";
    clear = true;
    updateDisplay();
}

function coth() {
    ans = value = 1/Math.tanh(value) + "";
    clear = true;
    updateDisplay();
}

function asinh() {
    ans = value = Math.asinh(value) + "";
    clear = true;
    updateDisplay();
}

function acosh() {
    ans = value = Math.acosh(value) + "";
    clear = true;
    updateDisplay();
}

function atanh() {
    ans = value = Math.atanh(value) + "";
    clear = true;
    updateDisplay();
}

function acsch() {
    ans = value = Math.asinh(1/value) + "";
    clear = true;
    updateDisplay();
}

function asech() {
    ans = value = Math.acosh(1/value) + "";
    clear = true;
    updateDisplay();
}

function acoth() {
    ans = value = Math.atanh(1/value) + "";
    clear = true;
    updateDisplay();
}

// variable setup /w memory
var memory = {}, selected = 'A';

function memClear() {
    delete memory[selected];
    document.getElementById('mr').disabled = true;
    document.getElementById('mc').disabled = true;
}

function memRead() {
    if(typeof memory[selected] !== 'undefined') {
        value = memory[selected];
        updateDisplay();
    }
}

function memAdd() {
    if(typeof memory[selected] === 'undefined') {
        document.getElementById('mr').disabled = false;
        document.getElementById('mc').disabled = false;

        memory[selected] = value;
    } else {
        memory[selected] = Number(memory[selected]) + Number(value) + "";
    }
}

function memSubtract() {
    if(typeof memory[selected] === 'undefined') {
        document.getElementById('mr').disabled = false;
        document.getElementById('mc').disabled = false;

        memory[selected] = -value + "";
    } else {
        memory[selected] = Number(memory[selected]) - Number(value) + "";
    }
}

function memStore() {
    if(typeof memory[selected] === 'undefined') {
        document.getElementById('mr').disabled = false;
        document.getElementById('mc').disabled = false;
    }

    memory[selected] = value;
}

var varButtons = document.getElementsByClassName('var-button');
for(var i = 0; i < varButtons.length; i++) {
    varButtons[i].addEventListener('click', function(e) {
        for(var j = 0; j < varButtons.length; j++) {
            var button = varButtons[j];
            if(typeof memory[button.innerHTML] === 'undefined') {
                button.style.color = '#000';
            } else {
                button.style.color = '#00F';
            }
        }

        selected = this.innerHTML;
        this.style.color = '#F0F';

        var disabled = typeof memory[selected] === 'undefined';
        document.getElementById('mr').disabled = disabled;
        document.getElementById('mc').disabled = disabled;
    }, false);
}

function clearMems() {
    try {
        if(confirm("Are you sure you want to delete all saved variables?")) {
            memory = {};

            for(var i = 0; i < varButtons.length; i++) {
                if(varButtons[i].innerHTML !== selected) {
                    varButtons[i].style.color = '#000';
                }
            }

            document.getElementById('mr').disabled = true;
            document.getElementById('mc').disabled = true;
        }
    } catch(e) {alert(e);}
}
