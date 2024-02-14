function setLastDateSynchronised(){
    if ($("#lastDateSynchronisezSudoc")) {
        $.ajax({
            type: "GET",
            url: "LastPPNSynchronized",
            success:function(data) {
                $("#lastDateSynchronisezSudoc").text(data);
            },
            error: function() {
                $("#lastDateSynchronisezSudoc").text("Erreur...");
            }
        });
    }
}

$( document ).ready(function () {
    setLastDateSynchronised();
});