window.onload = function() {
    try {
        const extend = document.getElementsByClassName("classExtends")[0];
        const implement = document.getElementsByClassName("classImplements")[0];
        if (extend.innerHTML != "") {
            extend.innerHTML = "<b>Extends</b>: "+extend.innerHTML;
        }

        if (implement.innerHTML != "") {
            implement.innerHTML = (extend.innerHTML != ""?"<b>&</b> ":"")+"<b>Implements</b>: "+implement.innerHTML;
            const content = implement.innerHTML;
            if (content.split(",").length>1) {
                var finalCom = content.lastIndexOf(",");
                implement.innerHTML = content.slice(0, finalCom) + ", <b>&</b> " + content.slice(finalCom+1);
            }
        }
    } catch(Error) {}

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