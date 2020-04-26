try {
    var contents = document.getElementsByClassName("expand-content");

    Array.prototype.slice.call(document.getElementsByClassName("expandable"))
            .forEach(function(item, idx){
        item.addEventListener("click", function() {
            // console.log("clicked " + idx);
            var content = contents[idx];
            // console.log(content + " " + content.style.display);
            if (content.style.display) {
                this.setAttribute("data-sign", "-");
                content.style.display = null;
            } else {
                this.setAttribute("data-sign", "+");
                content.style.display = "none";
            }

            return false;
        });

        item.setAttribute("data-sign", "+");
    });
} catch(err) {
    alert(err);
}