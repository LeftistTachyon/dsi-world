try {
    var contents = document.getElementsByClassName("expand-content");

    Array.prototype.slice.call(document.getElementsByClassName("expandable"))
            .forEach(function(item, idx){
        item.addEventListener("click", function() {
            // alert(this + " clicked")

            try {
                // console.log("clicked " + idx);
                var content = contents[idx];
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
    });
} catch(err) {
    alert(err);
}