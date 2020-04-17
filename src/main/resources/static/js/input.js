window.onload = function() {
    window.canvas = document.getElementById('bottom-canvas');
    window.ctx = canvas.getContext('2d');

    function clearScreen() {
        ctx.fillStyle = '#fff';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
    }

    var previousY = 0, previousDY = 0, scrollCnt = 0, key = 0;

    document.addEventListener('scroll', function() {
        var topY = window.scrollY || document.body.scrollTop || document.documentElement.scrollTop;

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
                key = mag;
            }

            ctx.fillRect(10, 79 + topY, 80, 2);

            ctx.fillNumber(key, 10, 100 + topY);

            previousDY = currentDY;
        } catch(e) {
            alert(e);
        }
    }, false);
};