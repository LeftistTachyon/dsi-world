CanvasRenderingContext2D.prototype.pixelNums = {
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
             [0, 0, 0]]
};

CanvasRenderingContext2D.prototype.fillNumber = function(num, x, y) {
    var str = "" + Math.round(num);
    for(var i = 0; i < str.length; i++) {
        var mat = ctx.pixelNums[str.charAt(i)];
        if(typeof mat === 'undefined') {
            console.log("Couldn't find " + str.charAt(i) + ", continuing");
            continue;
        }

        for(var j = 0, y_ = y - 10; j < mat.length; j++, y_ += 2) {
            var arr = mat[j];
            for(var k = 0, x_ = x + i * 8; k < arr.length; k++, x_ += 2) {
                if(arr[k]) {
                    ctx.fillRect(x_, y_, 2, 2);
                }
            }
        }
    }
};