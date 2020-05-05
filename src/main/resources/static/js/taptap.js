var box = document.getElementById("box"), check = document.getElementById("check"),
        canvas = document.getElementById("canvas"), cnt = 0, pb = -1;
function move() {
    check.checked = false;

    var width = box.offsetWidth - check.offsetWidth,
            height = box.offsetHeight - check.offsetHeight,
            top = Math.round(Math.random() * height + box.offsetTop),
            left = Math.round(Math.random() * width);
    // console.log("dims: (" + width + ", " + height + ")");
    // console.log("pos : (" + left + ", " + top + ")");
    check.style.left = left + "px";
    check.style.top = top + "px";
}

var animate = window.requestAnimationFrame ||
    window.webkitRequestAnimationFrame ||
    window.mozRequestAnimationFrame ||
    function(callback) { window.setTimeout("callback()", 166) },
        context = canvas.getContext('2d');

context.pixelSize = 3;

function draw() {
    context.fillStyle = "#fff";
    context.fillRect(0, 0, canvas.width, canvas.height);

    context.fillStyle = "#000";
    context.fillNumber(cnt, 5, 20);

    if (pb != -1) {
        context.fillStyle = "#00f";
        context.fillPixelText("PB: " + pb, 5, canvas.height - 5);
    }

    cnt++;

    animate(draw);
}

check.addEventListener('input', function() {
    if(pb == -1 || cnt < pb) {
        pb = cnt;
    }
    cnt = 0;

    window.setTimeout("move()", 100);
}, false);

move();

animate(draw);