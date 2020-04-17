CanvasRenderingContext2D.prototype.pixelChars = {
    "0" :   [[1, 1, 1],
             [1, 0, 1],
             [1, 0, 1],
             [1, 0, 1],
             [1, 1, 1]],
    "1" :   [[0, 1, 0],
             [1, 1, 0],
             [0, 1, 0],
             [0, 1, 0],
             [1, 1, 1]],
    "2" :   [[1, 1, 1],
             [0, 0, 1],
             [1, 1, 1],
             [1, 0, 0],
             [1, 1, 1]],
    "3" :   [[1, 1, 1],
             [0, 0, 1],
             [1, 1, 1],
             [0, 0, 1],
             [1, 1, 1]],
    "4" :   [[1, 0, 1],
             [1, 0, 1],
             [1, 1, 1],
             [0, 0, 1],
             [0, 0, 1]],
    "5" :   [[1, 1, 1],
             [1, 0, 0],
             [1, 1, 0],
             [0, 0, 1],
             [1, 1, 0]],
    "6" :   [[1, 1, 1],
             [1, 0, 0],
             [1, 1, 1],
             [1, 0, 1],
             [1, 1, 1]],
    "7" :   [[1, 1, 1],
             [0, 0, 1],
             [0, 0, 1],
             [0, 0, 1],
             [0, 0, 1]],
    "8" :   [[1, 1, 1],
             [1, 0, 1],
             [1, 1, 1],
             [1, 0, 1],
             [1, 1, 1]],
    "9" :   [[1, 1, 1],
             [1, 0, 1],
             [1, 1, 1],
             [0, 0, 1],
             [1, 1, 1]],
    "-" :   [[0, 0, 0],
             [0, 0, 0],
             [1, 1, 1],
             [0, 0, 0],
             [0, 0, 0]],
    "." :   [[0, 0, 0],
             [0, 0, 0],
             [0, 0, 0],
             [0, 0, 0],
             [0, 1, 0]]
};

CanvasRenderingContext2D.prototype.fillNumber = function(num, x, y) {
    var str = "" + Math.round(num);
    this.fillPixelText(str, x, y);
};

CanvasRenderingContext2D.prototype.fillPixelText = function(str, x, y) {
    var baseX = x;
    for(var i = 0; i < str.length; i++) {
        var mat = ctx.pixelChars[str.charAt(i)];
        if(typeof mat === 'undefined') {
            console.log("Couldn't find " + str.charAt(i) + ", continuing");
            continue;
        }

        for(var j = 0, y_ = y - 2 * mat.length; j < mat.length; j++, y_ += 2) {
            var arr = mat[j];
            for(var k = 0, x_ = baseX; k < arr.length; k++, x_ += 2) {
                if(arr[k]) {
                    ctx.fillRect(x_, y_, 2, 2);
                }
            }
        }

        baseX += 2 * (mat[0].length + 1);
    }
}