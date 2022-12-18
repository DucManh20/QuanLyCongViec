$(document).ready(function () {
    if(localStorage.getItem("nav-item") == null){
        $("#nav-item-0").addClass('active')
    }

    $(".nav-link-custom-1").on("click", function () {
        localStorage.setItem("nav-item", $(this).attr('id'))
    });
    const loadedNav = localStorage.getItem("nav-item");
    if (loadedNav) {
        selectTab(loadedNav);
    }
})

function selectTab(tabId) {
    $('.nav-link-custom-1').removeClass('active')
    $("#" + tabId).addClass('active')
}
