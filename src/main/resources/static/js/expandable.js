try {
    var arr = document.getElementsByClassName("expandable");
    for(var idx = 0; idx < arr.length; idx++) {
//        alert("starting " + idx);

        arr[idx].addEventListener("click", function() {
            // alert(this + " clicked")

            try {
                // console.log("clicked " + idx);
                var content;
                if(this.getAttribute("data-content")) {
                    content = document.getElementById(this.getAttribute("data-content"));
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

        if(!arr[idx].getAttribute("data-sign"))
            arr[idx].setAttribute("data-sign", "+");

//        alert("done " + idx);
    }
} catch(err) {
    alert(err);
}