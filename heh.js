$(function () {

    $('nav > a').click(function () {
        var portrait = window.innerHeight > window.innerWidth;
        var eyesArrayLeft = ['left-eyes1', 'left-eyes2', 'left-eyes3', 'left-eyes4',
            'left-eyes5', 'center-eyes'];
        var eyesArrayRight = ['right-eyes1', 'right-eyes2', 'right-eyes3', 'right-eyes4',
            'right-eyes5', 'right-eyes6', 'right-eyes7', 'right-eyes8', 'right-eyes9'];
        var randomBlinkLeft = eyesArrayLeft[Math.floor(Math.random() * eyesArrayLeft.length)];
        var randomBlinkRight = eyesArrayRight[Math.floor(Math.random() * eyesArrayRight.length)];
        if (portrait) {
            showEyes();
            $('#' + randomBlinkLeft).delay(500).fadeOut(100);
            $('#' + randomBlinkLeft).fadeIn(100);
            $('#' + randomBlinkRight).delay(1000).fadeOut(100);
            $('#' + randomBlinkRight).fadeIn(100);
            setTimeout(function () {
                for (var i = 0; i <= eyesArrayRight; i++) {
                    $('#' + randomBlinkRight).delay(1000).fadeOut(100);
                    $('#' + randomBlinkRight).fadeIn(100);
                }
                $('#' + randomBlinkLeft).delay(500).fadeOut(100);
                $('#' + randomBlinkLeft).fadeIn(100);

            }, 4000);
        } else {
            hideEyes();
        }
        function showEyes() {
            $('.eyes').css("display", "block");
        }
        function hideEyes() {
            $('.eyes').fadeOut(300);
        }
    });
    $('.exit').click(hideEyes);
    function hideEyes() {
        $('.eyes').fadeOut(300);
    }

    $(".exit").click(function () {
        $(this).parent().fadeOut(250);
    });

    function animationEnd() {
        var me = $(this);
        me.removeClass("animated");
        //makes the animation continually work/end
        me.one("animationend", animationEnd);
    }
    //just like .on() but will unbound after the first time
    $('.noose').one("animationend", animationEnd);

    $('.noose').hover(function () {
        $(this).addClass("animated");
    });
    $('nav > a').click(function () {
        var href = $(this).attr("href");//#tour
        var popup = $(href + "-popup");//#tour-popup
        $('.popup').fadeOut(250);
        //associative array: turning a string into a function
        if (popup.hasClass("dynamic")) {
            var jsname = "get" + capitalize(href.substring(1)) + "Info";
            //becasuse the value of the key of the object is a function
            dynamicAjax[jsname](); //function call
        } else { //not dynamic, can show it right away
            $(href + "-popup").fadeIn(250);
        }
        return false;
    });
    var dynamicAjax = {
        //key         name of function--- use same text to convert a key into the same name
        getTourInfo: getTourInfo,
        getBioInfo: getBioInfo,
        getNewsInfo: getNewsInfo
    };
    /*var dynamicDetailedAjax = {
     
     };*/

    function capitalize(name) {
        //gives us 't'              turn 't' to 'T'   'T' + 'our'
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }


    $('#tour-popup').on("click", "tr", showTourUpdate);
    $('#bio-names').on("click", "div > div", bioDesc);
    $('#news-popup').on("click", "#news-info > div", updateNewsInfo);

    $('#add-news').click(showAddNews);
    $('button[name="save-add-news-button"]').click(addNews);
    $('button[name="cancel-add-news-button"]').click(function () {
        $('#news-popup').fadeIn(250);
        $('#add-news-popup').fadeOut(250);
    });
    $("#news-save-button").click(updateNews);
    $('button[name=news-delete]').click(deleteNews);
    $('button[name=news-cancel]').click(function () {
        $("#news-popup").fadeIn(250);
        $("#news-modify-popup").fadeOut(250);
    });

    $('#bio-name').on("click", bioUpdate);
    $('button[name="add-bio"]').click(showAddBioPopup);
    $('button[name="save-bio-add-button"]').click(addBio);
    $('button[name="cancel-modify-bio-button"]').click(function () {
        $('#detail-popup').fadeIn(250);
        $('#bio-modify-popup').fadeOut(250);
    });
    $('button[name="cancel-bio-add-button"]').click(function () {
        $('#bio-popup').fadeIn(250);
        $('#bio-add-popup').fadeOut(250);
    });
    $('#addbutton').click(showAddTourDatePopup);
    $('button[name="save-add-button"]').click(addTourDate);
    $('button[name="cancel-add-button"]').click(function () {
        $('#tour-popup').fadeIn(250);
        $('#tour-add-popup').fadeOut(250);
    });

    $("button[name='modify-bio-save-button']").click(updateBio);
    $("button[name='delete-bio-button']").click(deleteBio);

    $('button[name="save"]').click(updateTourDate);
    $("button[name='delete']").click(deleteTourDate);
    $('button[name="cancel"]').click(function () {
        $('#tour-popup').fadeIn(250);
        $('#tour-modify-popup').fadeOut(250);
    });

    function updateNewsInfo() {
        $("#news-popup").fadeOut(250);
        var news = $(this).data("news");
        var newsKey = news.newsKey;
        var newsInfo = news.newsInfo;
        $("input[name='news-key']").val(newsKey);
        $("textarea[name='news-desc']").val(newsInfo);
        $("#news-modify-popup").fadeIn(250);
        return false;
    }

    function bioUpdate() {
        $('#detail-popup').fadeOut(250);
        var bio = $(this).data("bio");
        var bioNamePerson = bio.bioNamePerson;
        var bioNameInstrument = bio.bioNameInstrument;
        var bioNameDescription = bio.bioNameDesc;
        var bioNameStatus = bio.bioNameStatus;
        var bioNameKey = bio.bioNameKey;
        $("input[name='modify-bio-name']").val(bioNamePerson);
        $("input[name='modify-bio-instrument']").val(bioNameInstrument);
        $("textarea[name='modify-bio-description']").val(bioNameDescription);
        $("input[name='modify-bio-status']").val(bioNameStatus);
        $("input[name='modify-bio-key']").val(bioNameKey);
        $('#bio-modify-popup').fadeIn(250);
        return false;
    }

    function showTourUpdate() {
        $('#tour-modify-popup').fadeIn(250);
        var tourDate = $(this).data("tour");
        var tourDateDate = tourDate.tourDateDate;
        var tourDateLocation = tourDate.tourDateLocation;
        var tourDateCity = tourDate.tourDateCity;
        var tourDateState = tourDate.tourDateState;
        var tourDatePurchase = tourDate.tourDatePurchase;
        var tourDateKey = tourDate.tourDateKey;
        $("input[name='date']").val(tourDateDate);
        $("input[name='location']").val(tourDateLocation);
        $("input[name='city']").val(tourDateCity);
        $("input[name='state']").val(tourDateState);
        $("input[name='purchase']").val(tourDatePurchase);
        $("input[name='key']").val(tourDateKey);
        $('#tour-popup').fadeOut(250);
        return false;
    }

    function updateNews() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "updateNewsInfo",
                news_key: $('input[name="news-key"]').val(),
                news_info: $("textarea[name='news-desc']").val()
            },
            error: ajaxError,
            success: updateNewsComplete
        });
        $('#news-modify-popup').fadeOut(250);
    }

    function updateBio() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "updateBioName",
                hemh_biokey: $("input[name='modify-bio-key']").val(),
                hemh_person: $("input[name='modify-bio-name']").val(),
                hemh_desc: $("textarea[name='modify-bio-description']").val(),
                hemh_status: $("input[name='modify-bio-status']").val(),
                hemh_instrument: $("input[name='modify-bio-instrument']").val()
            },
            error: ajaxError,
            success: updateBioComplete
        });
        $('#bio-modify-popup').fadeOut(250);
    }

    function addNews() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "insertNewsInfo",
                news_info: $('textarea[name="add-news-desc"]').val()
            },
            error: ajaxError,
            success: addNewsInfoComplete
        });
        $('#add-news-popup').fadeOut(250);
    }

    function addBio() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "insertBioName",
                hemh_person: $("input[name='add-bio-name']").val(),
                hemh_desc: $("textarea[name='add-bio-description']").val(),
                hemh_status: $("input[name='add-bio-status']").val(),
                hemh_instrument: $("input[name='add-bio-instrument']").val()
            },
            error: ajaxError,
            success: addBioComplete
        });
        $('#bio-add-popup').fadeOut(250);
    }

    function deleteTourDate() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "deleteTourDate",
                hemh_key: $("input[name='key']").val()
            },
            error: ajaxError,
            success: deleteComplete
        });
        $('#tour-modify-popup').fadeOut(250);
    }

    function deleteNews() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "deleteNewsInfo",
                news_key: $('input[name="news-key"]').val()
            },
            error: ajaxError,
            success: deleteNewsComplete
        });
        $('#news-modify-popup').fadeOut(250);
    }

    function deleteBio() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "deleteBioName",
                hemh_biokey: $("input[name='modify-bio-key").val()
            },
            error: ajaxError,
            success: deleteBioComplete
        });
        $('#bio-modify-popup').fadeOut(250);
    }

    function addTourDate() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "insertTourDate",
                hemh_date: $("input[name='add-date']").val(),
                hemh_location: $("input[name='add-location']").val(),
                hemh_city: $("input[name='add-city']").val(),
                hemh_state: $("input[name='add-state']").val(),
                hemh_purchase: $("input[name='add-purchase']").val()
            },
            error: ajaxError,
            success: addTourDateComplete
        });
        $('#tour-add-popup').fadeOut(250);
    }

    function updateTourDate() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "updateTourDate",
                hemh_key: $("input[name='key']").val(),
                hemh_date: $("input[name='date']").val(),
                hemh_location: $("input[name='location']").val(),
                hemh_city: $("input[name='city']").val(),
                hemh_state: $("input[name='state']").val(),
                hemh_purchase: $("input[name='purchase']").val()
            },
            error: ajaxError,
            success: updateTourDateComplete
        });
        $('#tour-modify-popup').fadeOut(250);
    }

    function updateNewsComplete(updateNewsStatus) {
        if (updateNewsStatus.status) {
            getNewsInfo(loadNewsComplete);
        } else {
            console.log("Could not modify news information");
        }
    }

    function updateBioComplete(bioUpdateStatus) {
        if (bioUpdateStatus.status) {
            getBioInfo(loadBioComplete);
        } else {
            console.log("Could not modify member information");
        }
    }

    function addNewsInfoComplete(addNewsStatus) {
        if (addNewsStatus.status) {
            getNewsInfo(loadNewsComplete);
        } else {
            console.log("Could not add new post");
        }
    }



    function addBioComplete(bioAddStatus) {
        if (bioAddStatus.status) {
            getBioInfo(loadBioComplete);
        } else {
            console.log("Could not add Bio");
        }
    }

    function deleteComplete(deleteAddStatus) {
        if (deleteAddStatus.status) {
            getTourInfo(loadBioComplete);
        } else {
            console.log("Could not delete tour date");
        }
    }

    function deleteNewsComplete(deleteNewsStatus) {
        if (deleteNewsStatus.status) {
            getNewsInfo(loadComplete);
        } else {
            console.log("Could not delete news information");
        }
    }

    function deleteBioComplete(deleteBioStatus) {
        if (deleteBioStatus.status) {
            getBioInfo(loadComplete);
        } else {
            console.log("Could not delete member information");
        }
    }

    function addTourDateComplete(tourDateStatus) {
        if (tourDateStatus.status) {
            getTourInfo(loadComplete);
        } else {
            console.log("Could not add tour date");
        }
    }

    function updateTourDateComplete(tourDateStatus) {
        if (tourDateStatus.status) {
            getTourInfo(loadComplete);
        } else {
            console.log("Could not update tour date");
        }
    }

    function loadNewsComplete() {
        $("#news-popup").fadeIn(250);
    }

    function loadBioComplete() {
        $('#bio-popup').fadeIn(250);
    }

    function loadComplete() {
        $('#tour-popup').fadeIn(250);
    }

    function showAddTourDatePopup() {
        $('#tour-popup').fadeOut(250);
        $('#tour-add-popup').fadeIn(250);
        return false;
    }

    function showAddNews() {
        $('#news-popup').fadeOut(250);
        $('#add-news-popup').fadeIn(250);
        return false;
    }

    function showAddBioPopup() {
        $('#bio-popup').fadeOut(250);
        $('#bio-add-popup').fadeIn(250);
        return false;
    }

    function getTourInfo() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "getTourDates",
                hemh_key: "tourDateKey",
                hemh_date: "tourDateDate",
                hemh_location: "tourDateLocation",
                hemh_city: "tourDateCity",
                hemh_state: "tourDateState",
                hemh_purchase: "tourDatePurchase"
            },
            error: ajaxError,
            success: function (data) {
                tourInfo(data);
            }
        });
    }

    function tourInfo(date) {
        if (date.status) {
            var tourInfo = $('#tour-info');
            tourInfo.empty();
            for (var i = 0; i < date.tourInfo.length; i++) {
                var tr = $('<tr></tr>');
                var tourDate = date.tourInfo[i];
                tr.data("tour", tourDate);
                var td = $('<td></td>');
                td.append(tourDate.tourDateDate);
                tr.append(td);
                td = $('<td></td>');
                td.append(tourDate.tourDateLocation);
                tr.append(td);
                td = $('<td></td>');
                td.append(tourDate.tourDateCity);
                tr.append(td);
                td = $('<td></td>');
                td.append(tourDate.tourDateState);
                tr.append(td);
                td = $('<td></td>');
                td.append(tourDate.tourDatePurchase);
                tr.append(td);
                tourInfo.append(tr);
            }
            $('#tour-popup').fadeIn(250);
        } else {
            console.log('Got an error status back');
            console.log(tourInfo);
        }
    }

    function getBioInfo() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "getBioNames",
                hemh_biokey: "bioNameKey",
                hemh_person: "bioNamePerson",
                hemh_desc: "bioNameDesc",
                hemh_status: "bioNameStatus",
                hemh_instrument: "bioNameInstrument"
            },
            error: ajaxError,
            success: bioInfo
        });
    }

    function ajaxError() {
        console.log("error");
    }

    function bioInfo(names) {
        if (names.status) {
            var bioNames = $('#bio-names');
            bioNames.empty();
            for (var i = 0; i < names.bioInfo.length; i++) {
                var bioName = names.bioInfo[i];
                var flexItem = $('<div></div>');
                bioNames.append(flexItem);
                var div = $('<div>');
                div.append(bioName.bioNamePerson);
                flexItem.append(div);
                div.data("bio", bioName);
                var span = $('<span></span>');
                span.append(bioName.bioNameInstrument);
                flexItem.append(span);
            }
            $('#bio-popup').fadeIn(250);
        } else {
            console.log("Got an error status back");
            console.log(bioNames);
        }
    }

    function getNewsInfo() {
        $.ajax({
            url: "tourdates",
            method: "post",
            dataType: "json",
            data: {
                action: "getNewsInfo",
                news_key: "newsKey",
                news_info: "newsInfo"
            },
            error: ajaxError,
            success: news
        });
    }

    function news(info) {
        if (info.status) {
            var newsInfos = $('#news-info');
            newsInfos.empty();
            for (var i = 0; i < info.newsInfo.length; i++) {
                var newsInfo = info.newsInfo[i];
                var flexItem = $('<div></div>');
                var div = $('<div>');
                div.append(newsInfo.newsInfo);
                flexItem.append(div);
                flexItem.data("news", newsInfo);
                newsInfos.append(flexItem);
            }
            $('#news-popup').fadeIn(250);
        } else {
            console.log("Got an error status back");
            console.log(newsInfos);
        }
    }

    function bioDesc() {
        $('#bio-popup').fadeOut(250);
        var bio = $(this).data("bio");
        $('#bio-name').empty().append(bio.bioNamePerson);
        $('#bio-name').data("bio", bio);
        $('#bio-instrument').empty().append(bio.bioNameInstrument);
        $('#bio-desc').empty().append(bio.bioNameDesc);
        $('#detail-popup').fadeIn(250);
        $('#exit').click(function () {
            $('#detail-popup').fadeOut(250);
            $('#bio-popup').fadeIn(250);
        });
    }



    $("#upload").addClass("dropzone");
    var uploader = new Dropzone("#upload", {
        acceptedFiles: ".mp3,.mp4,.jpg",
        //change test to context root i'm using
        url: "uploads/noprefix/scale/1080/thumbscale/200"
    });
    uploader.on("success", function (file, resp) {
        if (resp.status) {
            //get the url for this file
            var link = resp.url;
            $('#audio').attr("src", link);
            $('#player')[0].load();
            //get the stored number of files in the hidden field and add one to it
            var filenum = parseInt($("#data-form input[name='numFiles']").val()) + 1;
            //update the stored number of files in the hidden field
            $("#data-form input[name='numFiles']").val(filenum);
            //create a new input for this uploaded filef
            var hidden = $("<input type='hidden'/>");
            //set a unique name for this input - file1, file2, etc
            hidden.attr("name", "file" + filenum);
            //set the value for this input to the file url
            hidden.val(link);
            //add the file input to the container
            $("#data-form").append(hidden);
        } else {
            alert("Upload failed");
        }
    });
    uploader.on("error", function (file, msg, xhr) {
        console.log(msg);
        console.log(xhr);
    });


});



