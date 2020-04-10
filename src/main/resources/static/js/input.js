window.onload = function() {
    alert(Math);

    var canvas = document.getElementById('bottomcanvas');
    var ctx = canvas.getContext('2d');

    function clearScreen() {
        ctx.fillStyle = '#fff';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
    }

    var previousY = 0, previousDY = 0, scrollCnt = 0, keyStr = '';

    document.addEventListener('scroll', function() {
        var topY = window.scrollY || document.body.scrollTop;

        try {
            clearScreen();

            ctx.fillStyle = '#000';
            ctx.fillText(topY, 10, 30 + topY);

            var currentDY = topY - previousY;
            ctx.fillText(currentDY, 10, 50 + topY);

            ctx.fillText(++scrollCnt, 10, 70 + topY);

            previousY = topY;

            var mag = Math.abs(currentDY); // magnitude of currentDY
            // console.log(mag);
            if(mag > Math.abs(previousDY)) {
                if(currentDY > 0) {
                    keyStr = mag + ' down';
                } else {
                    keyStr = mag + ' up';
                }
            }

            ctx.fillText(keyStr, 10, 100 + topY);

            previousDY = currentDY;
        } catch(e) {
            alert(e);
        }
    }, false);
};