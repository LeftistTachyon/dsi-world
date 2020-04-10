try{
    $(document).ready(function() {
        alert("Document is ready");

        var canvas = document.getElementById("bottom-canvas");
        var ctx = canvas.getContext("2d");

        function clearScreen() {
            ctx.fillStyle = "#fff";
            ctx.fillRect(0, 0, canvas.width, canvas.height);
        }

        var previousY = 0;
        var previousDY = 0;
        var scrollCnt = 0;
        var keyStr = "";

        alert("Instantiation complete");

        document.addEventListener("scroll", function() {
            clearScreen();

            ctx.fillStyle = "#000";
            ctx.fillText(window.scrollY, 10, 30 + window.scrollY);

            var currentDY = window.scrollY - previousY;
            ctx.fillText(currentDY, 10, 50 + window.scrollY);

            ctx.fillText(++scrollCnt, 10, 70 + window.scrollY);

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

            ctx.fillText(keyStr, 10, 100 + window.scrollY);

            previousDY = currentDY;
        }, false);

        alert("Listener setup complete");
    });
} catch(e) {
    alert(e);
}