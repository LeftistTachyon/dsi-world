$(document).ready(function() {
var canvas = document.getElementById("bottom-canvas");
var ctx = canvas.getContext("2d");

ctx.font = "15px monospace";

function clearScreen() {
    ctx.fillStyle = "#fff";
    ctx.fillRect(0, 0, canvas.width, canvas.height);
}

var previousY = 0, previousDY = 0, scrollCnt = 0, keyStr = "";

document.body.addEventListener("scroll", function() {
    clearScreen();

    ctx.fillStyle = "#000";
    ctx.fillText(window.scrollY, 10, 30);

    var currentDY = window.scrollY - previousY;
    ctx.fillText(currentDY, 10, 50);

    ctx.fillText(++scrollCnt, 10, 70);

    previousY = window.scrollY;

    var mag = Math.abs(currentDY); // magnitude of currentDY
    console.log(mag);
    if(mag > Math.abs(previousDY)) {
        if(currentDY > 0) {
            keyStr = mag + " down";
        } else {
            keyStr = mag + " up";
        }
    }

    ctx.fillText(keyStr, 10, 100);

    previousDY = currentDY;
}, false);
});
