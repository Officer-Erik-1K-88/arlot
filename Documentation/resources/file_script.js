window.onload = function() {
    var height = document.body.scrollHeight;
    if (height < 56) {
        height += 56;
    }
    height += 20;
    const title = document.getElementsByTagName("title")[0];
    window.parent.postMessage({
        height: height,
        meta: {
            title: title.innerHTML
        }
    }, "*");
};