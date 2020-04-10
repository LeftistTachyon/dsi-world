CanvasRenderingContext2D.prototype.fillNumber = function(num, x, y) {
    num |= 0;
    for(var i = 0; num > 0; i++) {
        var digit = num % 10;
        var r = digit.toString(16).padStart(2, '0');

        ctx.fillStyle = "#" + r + r + r;

        ctx.fillRect(x + i * 5, y - 10, 5, 10);

        num = (num / 10) | 0;
    }
};

window.onload = function() {
    window.canvas = document.getElementById('bottom-canvas');
    window.ctx = canvas.getContext('2d');

    function clearScreen() {
        ctx.fillStyle = '#fff';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
    }

    var previousY = 0, previousDY = 0, scrollCnt = 0, keyStr = 0;

    document.addEventListener('scroll', function() {
        alert(ctx.fillNumber);

        var topY = window.scrollY || document.body.scrollTop;

        clearScreen();

        ctx.fillStyle = '#000';
        try {
            ctx.fillNumber(topY, 10, 30 + topY);

            var currentDY = topY - previousY;
            ctx.fillNumber(currentDY, 10, 50 + topY);

            ctx.fillNumber(++scrollCnt, 10, 70 + topY);

            previousY = topY;

            var mag = Math.abs(currentDY); // magnitude of currentDY
            // console.log(mag);
            if(mag > Math.abs(previousDY)) {
                if(currentDY > 0) {
                    keyStr = mag;
                } else {
                    keyStr = mag;
                }
            }

            ctx.fillNumber(keyStr, 10, 100 + topY);

            previousDY = currentDY;
        } catch(e) {
            alert(e);
        }
    }, false);
};