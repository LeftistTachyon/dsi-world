try {
    Array.prototype.slice.call(document.getElementsByClassName("expandable"))
            .forEach(function(item, idx){
        alert("starting " + idx);

        item.addEventListener("click", function() {
            // alert(this + " clicked")

            try {
                // console.log("clicked " + idx);
                var content;
                if(this.getAttribute("data-content")) {
                    content = document.getElementById(item.getAttribute("data-content"));
                } else {
                    content = this.nextElementSibling;
                }
                // alert(content + " " + content.style.display);
                if (content.style.display == "none") {
                    this.setAttribute("data-sign", "-");
                    content.style.display = "block";
                } else {
                    this.setAttribute("data-sign", "+");
                    content.style.display = "none";
                }
            } catch(e) {
                alert(e);
            }

            return false;
        }, false);

        if(!item.getAttribute("data-sign"))
            item.setAttribute("data-sign", "+");

        alert("done " + idx);
    });
} catch(err) {
    alert(err);
}