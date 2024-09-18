window.onload = function() {
    var height = document.body.scrollHeight;
    if (height < 56) {
        height += 56;
    }
    window.parent.postMessage({ height: height }, "*");
};