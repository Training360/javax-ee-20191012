let ws = null;

window.onload = function() {
    let url = "ws://localhost:8080/empapp/socket/warning";
    ws = new WebSocket(url);

    ws.onmessage = function(event) {
        let message = event.data;
        let div = document.querySelector("#reply-div");
        let p = document.createElement("p");
        p.innerHTML = message;
        div.appendChild(p);
    }

    button = document.querySelector("#message-button");
    button.onclick = function() {
        let input = document.querySelector("#message-input");
        let content = input.value;
        console.log(content);
        ws.send(content);
        return false;
    };

}