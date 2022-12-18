// console.log('tab.js');
// $(document).ready(function () {
//     console.log("manh");
//     if( $("#" + "orders-paid-tab").hasClass('active')){
//
//
//          console.log("Manh");
//         // $(".tab-pane fade").removeClass('active show');
//         // $("#orders-paid").addClass('active show');
//     }
//     $( ".custom-nav-link-1" ).on( "click", function() {
//         selectTab($(this).attr('id'));
//         localStorage.setItem("tab", $(this).attr('id'))
//     });
//     const loadedTab = localStorage.getItem("tab");
//     console.log(loadedTab);
//     if (loadedTab) {
//         $('#orders-all-tab').removeClass('active')
//         $("#" + loadedTab).addClass('active');
//         selectTab(loadedTab);
//     }
// // if(orders-paid-tab )
// })
//
// function selectTab(tabId) {
//     $('.custom-nav-link-1').removeClass('active')
//     $("#" + tabId).addClass('active')
//
// }
// // $( ".fade" ).on( "click", function() {
// //     selectPage($(this).attr('id'));
// //     localStorage.setItem("page", $(this).attr('id'));
// // });
//
// // const loadedPage = localStorage.getItem("page");
//
// // if (loadedPage) {
// //     $('#orders-all').removeClass('show');
// //     $('#orders-all').removeClass('active');
// //
// //     $("#" + loadedPage).addClass('show active');
// //     selectPage(loadedPage);
// // }
//
// // function selectPage(pageId) {
// //     $('.fade').removeClass('show active')
// //     $("#" + pageId).addClass('show active')
// //
// // }