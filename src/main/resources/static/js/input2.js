try {
    window.console = window.console || {log: function(s){}};

    var canvas, ctx,
            presses = {"UP": 0, "DOWN": 0, "LEFT": 0, "RIGHT": 0, "A": 0};
    function drawFrame() {
        ctx.fillStyle = "#fff";
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        ctx.fillStyle = presses["UP"] ? "#000" : "#aaa";
        ctx.fillPixelText("UP", 25, 40);
        if(presses["UP"] > 0) presses["UP"]--;

        ctx.fillStyle = presses["DOWN"] ? "#000" : "#aaa";
        ctx.fillPixelText("DOWN", 25, 58);
        if(presses["DOWN"] > 0) presses["DOWN"]--;

        ctx.fillStyle = presses["LEFT"] ? "#000" : "#aaa";
        ctx.fillPixelText("LEFT", 25, 76);
        if(presses["LEFT"] > 0) presses["LEFT"]--;

        ctx.fillStyle = presses["RIGHT"] ? "#000" : "#aaa";
        ctx.fillPixelText("RIGHT", 25, 94);
        if(presses["RIGHT"] > 0) presses["RIGHT"]--;

        ctx.fillStyle = presses["A"] ? "#000" : "#aaa";
        ctx.fillPixelText("A", 25, 112);
        if(presses["A"] > 0) presses["A"]--;

        window.setTimeout("drawFrame();", 1);
    }

    var short = 3, long = 16;
} catch(e) {
    alert("Instantiation threw an exception:\n" + e);
}

window.onload = function() {
    try {
        function resetScroll() {
            document.documentElement.scrollTop = 20;
            document.documentElement.scrollLeft = 20;
        }

        resetScroll();

        canvas = document.getElementById('draw');
        ctx = canvas.getContext('2d');

        ctx.pixelSize = 3;

        drawFrame();

        document.addEventListener('keydown', function(evt) {
            try {
                var keyCode = evt.which || evt.keyCode;
                console.log(keyCode + " pressed");
                switch(keyCode) {
                    case 38: // [^]
                        presses["UP"] = long;
                        break;
                    case 40: // [DOWN]
                        presses["DOWN"] = long;
                        break;
                    case 37: // [<-]
                        presses["LEFT"] = long;
                        break;
                    case 39: // [->]
                        presses["RIGHT"] = long;
                        break;
                    case 65: // [A]
                    case 32: // [Space]
                    case 12: // [NumPad 5]
                        presses["A"] = long;
                        break;
                    case undefined: // DS A button (?)
                        presses["A"] = short;
                        break;
                }
            } catch(e) {
                alert("keydown threw an exception:\n" + e);
            }
        }, false);

        document.addEventListener('scroll', function(evt) {
            try {
                var dy = document.documentElement.scrollTop - 20,
                        dx = document.documentElement.scrollLeft - 20;

                console.log("velocity = <" + dx + ", " + dy + ">");

                if(dy > 0) { // down
                    presses["DOWN"] = short;
                } else if(dy < 0) { // up
                    presses["UP"] = short;
                }

                if(dx > 0) { // left (?)
                    presses["LEFT"] = short;
                } else if(dx < 0) { // right (?)
                    presses["RIGHT"] = short;
                }

                resetScroll();
            } catch(e) {
                alert("scroll threw an exception:\n" + e);
            }
        }, false);
    } catch(e) {
        alert("Onload threw an exception:\n" + e);
    }
};