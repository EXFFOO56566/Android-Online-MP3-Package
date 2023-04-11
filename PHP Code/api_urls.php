<?php include("includes/header.php");

$file_path = 'http://'.$_SERVER['SERVER_NAME'] . dirname($_SERVER['REQUEST_URI']).'/';
?>
<div class="row">
      <div class="col-sm-12 col-xs-12">
     	 	<div class="card">
		        <div class="card-header">
		          Example API urls
		        </div>
       			    <div class="card-body no-padding">
         		
         			 <pre><code class="html"><b>Latest Songs</b><br><?php echo $file_path."api.php?latest"?><br><br><b>Category List</b><br><?php echo $file_path."api.php?cat_list"?><br><br><b>Recent Artist List</b><br><?php echo $file_path."api.php?recent_artist_list"?><br><br><b>Artist List</b><br><?php echo $file_path."api.php?artist_list"?><br><br><b>Album List</b><br><?php echo $file_path."api.php?album_list"?><br><br><b>Songs List By Album ID</b><br><?php echo $file_path."api.php?album_id=1"?><br><br><b>Playlist List</b><br><?php echo $file_path."api.php?playlist"?><br><br><b>Songs List By Playlist ID</b><br><?php echo $file_path."api.php?playlist_id=1"?><br><br><b>Songs List By Cat ID</b><br><?php echo $file_path."api.php?cat_id=1"?><br><br><b>Songs List By Artist Name</b><br><?php echo $file_path."api.php?artist_name=Pritam"?><br><br><b>Single Song</b><br><?php echo $file_path."api.php?mp3_id=14&device_id=123"?><br><br><b>Songs Search</b><br><?php echo $file_path."api.php?search_text=ma"?><br><br><b>Rating</b><br><?php echo $file_path."api_rating.php?post_id=14&device_id=123&rate=4"?><br><br><b>Song Download</b><br><?php echo $file_path."api.php?song_download_id=14"?><br><br><b>App Details</b><br><?php echo $file_path."api.php"?></code></pre>
       		
       				</div>
          	</div>
        </div>
</div>
    <br/>
    <div class="clearfix"></div>
        
<?php include("includes/footer.php");?>       
