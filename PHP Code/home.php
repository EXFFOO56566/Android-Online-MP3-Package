<?php include("includes/header.php");

$qry_cat="SELECT COUNT(*) as num FROM tbl_category";
$total_category= mysqli_fetch_array(mysqli_query($mysqli,$qry_cat));
$total_category = $total_category['num'];

$qry_art="SELECT COUNT(*) as num FROM tbl_artist";
$total_artist= mysqli_fetch_array(mysqli_query($mysqli,$qry_art));
$total_artist = $total_artist['num'];

$qry_mp3="SELECT COUNT(*) as num FROM tbl_mp3";
$total_mp3 = mysqli_fetch_array(mysqli_query($mysqli,$qry_mp3));
$total_mp3 = $total_mp3['num'];

 

?>       


        <div class="btn-floating" id="help-actions">
      <div class="btn-bg"></div>
      <button type="button" class="btn btn-default btn-toggle" data-toggle="toggle" data-target="#help-actions"> <i class="icon fa fa-plus"></i> <span class="help-text">Shortcut</span> </button>
      <div class="toggle-content">
        <ul class="actions">
          <li><a href="http://www.viaviweb.com" target="_blank">Website</a></li>
           <li><a href="https://codecanyon.net/user/viaviwebtech?ref=viaviwebtech" target="_blank">About</a></li>
        </ul>
      </div>
    </div>
    <div class="row">
      <div class="col-lg-4 col-md-6 col-sm-6 col-xs-12"> <a href="manage_category.php" class="card card-banner card-green-light">
        <div class="card-body"> <i class="icon fa fa-sitemap fa-4x"></i>
          <div class="content">
            <div class="title">Categories</div>
            <div class="value"><span class="sign"></span><?php echo $total_category;?></div>
          </div>
        </div>
        </a> 
        </div>
        <div class="col-lg-4 col-md-6 col-sm-6 col-xs-12"> <a href="manage_artist.php" class="card card-banner card-green-light">
        <div class="card-body"> <i class="icon fa fa-buysellads fa-4x"></i>
          <div class="content">
            <div class="title">Artist</div>
            <div class="value"><span class="sign"></span><?php echo $total_artist;?></div>
          </div>
        </div>
        </a> 
        </div>
        <div class="col-lg-4 col-md-6 col-sm-6 col-xs-12"> <a href="manage_mp3.php" class="card card-banner card-yellow-light">
        <div class="card-body"> <i class="icon fa fa-music fa-4x"></i>
          <div class="content">
            <div class="title">Mp3 Songs</div>
            <div class="value"><span class="sign"></span><?php echo $total_mp3;?></div>
          </div>
        </div>
        </a> 
        </div>
         
     
    </div>

        
<?php include("includes/footer.php");?>       
