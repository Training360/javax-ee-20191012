window.onload = function() {
    let evtSource = new EventSource("api/warning/stream");
    evtSource.addEventListener("warning",
        function(event) {
             let div = document.querySelector("#reply-div");
             let p = document.createElement("p");
             p.innerHTML = event.data;
             div.appendChild(p);
        });
}