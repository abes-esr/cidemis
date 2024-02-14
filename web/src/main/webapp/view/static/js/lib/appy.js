
    var handleMainMenu = function () {
        $('.page-sidebar .has-sub > a').click(function () {

            var handleContentHeight = function () {
                var content = $('.page-content');
                var sidebar = $('.page-sidebar');

                if (!content.attr("data-height")) {
                    content.attr("data-height", content.height());
                }


                if (sidebar.height() > content.height()) {
                    content.css("min-height", sidebar.height() + 20);
                } else {
                    content.css("min-height", content.attr("data-height"));
                }
            }

            var last = $('.has-sub.open', $('.page-sidebar'));
            if (last.size() == 0) {
            }
            last.removeClass("open");
            $('.arrow', last).removeClass("open");
            $('.sub', last).slideUp(200);

            var sub = $(this).next();
            if (sub.is(":visible")) {
                $('.arrow', $(this)).removeClass("open");
                $(this).parent().removeClass("open");
                sub.slideUp(100, function () {
                    handleContentHeight();
                });
            } else {
                $('.arrow', $(this)).addClass("open");
                $(this).parent().addClass("open");
                sub.slideDown(100, function () {
                    handleContentHeight();
                });
            }
        });
    }

    var handleSidebarToggler = function () {

        var container = $(".page-container");
        
        container.addClass("sidebar-closed");
        
        
        $('.page-sidebar .sidebar-toggler').click(function () {
            $(".sidebar-search").removeClass("open");
            var container = $(".page-container");
            if (container.hasClass("sidebar-closed") === true) {
                container.removeClass("sidebar-closed");
            } else {
                container.addClass("sidebar-closed");
            }
        });

        
    }

    var handleAccordions = function () {
        
        var lastClicked;

        $('.accordion.scrollable .accordion-toggle').click(function () {
            lastClicked = $(this);
        });

        $('.accordion.scrollable').on('shown', function () {
            $('html,body').animate({
                scrollTop: lastClicked.offset().top - 150
            }, 'slow');
        });
    }

	$( document ).ready(function() {
		handleMainMenu(); 
		//handleSidebarToggler();
		handleAccordions(); 
	});
