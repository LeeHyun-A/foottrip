<!doctype html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta charset="utf-8" />
	<title>Insert title here</title>


	<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">

<style type="text/css">
.iconButton {
	border: 0;
}
</style>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript">

	$(document).ready(function() {
		$.ajax({
			url: "http://192.168.0.5:8089/download_example/UploadDownloadFileServlet",
			type: "POST",
			dataType : 'text',
			data: { "image":"img-data", "file-name":"ex.png"},
			
			success: function (result) {
				if( result.length > 0 ) {
					alert
					$('#div1').html('<img src="data:image/png;base64,' + result + '" onClick="showAndroidToast()" />');
				}
							
			},
		
		})
	
	});

	</script>
</head>
<body class="container">
	<script type="text/javascript">
	function showAndroidToast(){
		Android.detailInfo();
	}
	function doWrite(){
		Android.doWrite();
	}
	</script>

	<script type='text/javascript'>
	$(function(){
      //Keep track of last scroll
      var lastScroll = 0;
      $(window).scroll(function(event){
          //Sets the current scroll position
          var st = $(this).scrollTop();
          //Determines up-or-down scrolling
          if (st > lastScroll){
             //Replace this with your function call for downward-scrolling
             $('#navbar').hide();
         }
         else {
             //Replace this with your function call for upward-scrolling
             $('#navbar').show();
         }
          //Updates scroll position
          lastScroll = st;
      });
  });
	</script>
<!-- take picture from server -->
<!-- 	<div class="row">
		<div class="col-md-6 thumbnail">
			<div id="div1" class="center"></div>
			<div class="btn-group btn-group-justified">
				<a href="#" class="btn btn-default">Left</a>
				<a href="#" class="btn btn-default">Middle</a>
			</div>
		</div>
	</div> -->

<!-- test form -->
	<div class="row">
		<div class="col-md-3 thumbnail">
			<img src="17.png" class="container" onClick="showAndroidToast()" />
			<div class="btn-group btn-group-justified ">
				<a href="#" class="btn btn-default">Like</a> 
				<a href="#"	class="btn btn-default">Comment</a>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 thumbnail">
			<img src="25.png" class="container" onClick="showAndroidToast()" />
			<div class="btn-group btn-group-justified ">
				<a href="#" class="btn btn-default">Like</a> 
				<a href="#"	class="btn btn-default">Comment</a>
			</div>
		</div>
	</div>
		<div class="row">
		<div class="col-md-3 thumbnail">
			<img src="31.png" class="container" onClick="showAndroidToast()" />
			<div class="btn-group btn-group-justified ">
				<a href="#" class="btn btn-default">Like</a> 
				<a href="#"	class="btn btn-default">Comment</a>
			</div>
		</div>
	</div>


	<nav id ="navbar"  class="navbar navbar-default navbar-fixed-bottom"
		role="navigation">
		<button onClick="doWrite()" style="height: 100%; background-color: transparent;"
			class="iconButton btn btn-default btn-lg">
			<span class="glyphicon glyphicon-pencil"></span>
		</button>
		<button style="height: 100%; background-color: transparent;"
			class="iconButton btn btn-default btn-lg">
			<span class="glyphicon glyphicon-picture"></span>
		</button>
		<button style="height: 100%; background-color: transparent;"
			class="iconButton btn btn-default btn-lg">
			<span class="glyphicon glyphicon-map-marker"></span>
		</button>
	</nav>
</body>
</html>