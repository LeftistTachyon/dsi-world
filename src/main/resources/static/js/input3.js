window.onload = function() {
    document.getElementById("trigger").onclick = function() {
        this.style.display = "none";
        var yeet = document.getElementById("text");
        window.lastevent = undefined;
        try {
            document.addEventListener('click', function(e) {
                yeet.appendChild(document.createTextNode('clicked ' + e.button + ' ' + e.pageX + ',' + e.pageY));
                yeet.appendChild(document.createElement('br'));
                window.lastevent = e;
            }, false);
            document.addEventListener('mouseover', function() {
                yeet.appendChild(document.createTextNode('mouseover'));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('mouseout', function() {
                yeet.appendChild(document.createTextNode('mouseout'));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('dblclick', function(e) {
                yeet.appendChild(document.createTextNode('dblclick ' + e.button));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('focus', function() {
                yeet.appendChild(document.createTextNode('focus'));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('input', function(e) {
                yeet.appendChild(document.createTextNode('input ' + e.data));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('keydown', function(e) {
                yeet.appendChild(document.createTextNode('keydown ' + e.code));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('keypress', function(e) {
                yeet.appendChild(document.createTextNode('keypress ' + e.code));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('keyup', function(e) {
                yeet.appendChild(document.createTextNode('keyup ' + e.code));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('mousedown', function(e) {
                window.lastevent = e;
                yeet.appendChild(document.createTextNode('mousedown ' + e.button));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('mouseleave', function() {
                yeet.appendChild(document.createTextNode('mouseleave'));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('mousemove', function(e) {
                yeet.appendChild(document.createTextNode('mousemove ' + e.button + ' ' + ((typeof window.lastevent == 'undefined') ? '?' : ((window.lastevent.screenX - e.screenX) + ',' + (window.lastevent.screenY - e.screenY)))));
                yeet.appendChild(document.createElement('br'));
                window.lastevent = e;
            }, false);
            document.addEventListener('mouseup', function(e) {
                yeet.appendChild(document.createTextNode('mouseup ' + e.button));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('scroll', function() {
                yeet.appendChild(document.createTextNode('scroll'));
                yeet.appendChild(document.createElement('br'));
            }, false);
            document.addEventListener('select', function() {
                yeet.appendChild(document.createTextNode('select'));
                yeet.appendChild(document.createElement('br'));
            }, false);
        } catch (err) {
            alert(err);
        }
    };
};