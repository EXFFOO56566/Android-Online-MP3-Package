<?php include("includes/header.php");

	require("includes/function.php");
	require("language/language.php");
    
    if(isset($_POST['search_data']))
   {
     
    
    $data_qry="SELECT * FROM tbl_album
               WHERE tbl_album.album_name like '%".addslashes($_POST['search_value'])."%' ORDER BY tbl_album.aid DESC";  
               
    $result=mysqli_query($mysqli,$data_qry);
    
     
   }
   else
   {
      
      $tableName="tbl_album";   
      $targetpage = "manage_album.php"; 
      $limit = 10; 
      
      $query = "SELECT COUNT(*) as num FROM $tableName";
      $total_pages = mysqli_fetch_array(mysqli_query($mysqli,$query));
      $total_pages = $total_pages['num'];
      
      $stages = 3;
      $page=0;
      if(isset($_GET['page'])){
      $page = mysqli_real_escape_string($mysqli,$_GET['page']);
      }
      if($page){
        $start = ($page - 1) * $limit; 
      }else{
        $start = 0; 
        } 
      
     $data_qry="SELECT * FROM tbl_album
                   ORDER BY tbl_album.aid DESC LIMIT $start, $limit";
 
     $result=mysqli_query($mysqli,$data_qry); 
   }

	
	if(isset($_GET['album_id']))
	{

	 
		$cat_res=mysqli_query($mysqli,'SELECT * FROM tbl_album WHERE aid=\''.$_GET['album_id'].'\'');
		$cat_res_row=mysqli_fetch_assoc($cat_res);


		if($cat_res_row['album_image']!="")
	    {
	    	unlink('images/'.$cat_res_row['album_image']);
			  unlink('images/thumbs/'.$cat_res_row['album_image']);

		}
 
		Delete('tbl_album','aid='.$_GET['album_id'].'');

      
		$_SESSION['msg']="12";
		header( "Location:manage_album.php");
		exit;
		
	}	
	 
  function get_total_songs($album_id)
  { 
    global $mysqli;   

    $qry_songs="SELECT COUNT(*) as num FROM tbl_mp3 WHERE album_id='".$album_id."'";
     
    $total_songs = mysqli_fetch_array(mysqli_query($mysqli,$qry_songs));
    $total_songs = $total_songs['num'];
     
    return $total_songs;

  }

    //Active and Deactive status
  if(isset($_GET['status_deactive_id']))
  {
     $data = array('status'  =>  '0');
    
     $edit_status=Update('tbl_album', $data, "WHERE aid = '".$_GET['status_deactive_id']."'");
    
     $_SESSION['msg']="14";
     header( "Location:manage_album.php");
     exit;
  }
  if(isset($_GET['status_active_id']))
  {
      $data = array('status'  =>  '1');
      
      $edit_status=Update('tbl_album', $data, "WHERE aid = '".$_GET['status_active_id']."'");
      
      $_SESSION['msg']="13";   
      header( "Location:manage_album.php");
      exit;
  }  

?>
                
    <div class="row">
      <div class="col-xs-12">
        <div class="card mrg_bottom">
          <div class="page_title_block">
            <div class="col-md-5 col-xs-12">
              <div class="page_title">Manage Album</div>
            </div>
            <div class="col-md-7 col-xs-12">
              <div class="search_list">
                <div class="search_block">
                      <form  method="post" action="">
                        <input class="form-control input-sm" placeholder="Search..." aria-controls="DataTables_Table_0" type="search" name="search_value" required>
                        <button type="submit" name="search_data" class="btn-search"><i class="fa fa-search"></i></button>
                      </form>  
                    </div>
                <div class="add_btn_primary"> <a href="add_album.php?add=yes">Add Album</a> </div>
              </div>
            </div>
          </div>
          <div class="clearfix"></div>
          <div class="row mrg-top">
            <div class="col-md-12">
               
              <div class="col-md-12 col-sm-12">
                <?php if(isset($_SESSION['msg'])){?> 
               	 <div class="alert alert-success alert-dismissible" role="alert"> <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
                	<?php echo $client_lang[$_SESSION['msg']] ; ?></a> </div>
                <?php unset($_SESSION['msg']);}?>	
              </div>
            </div>
          </div>
           <div class="col-md-12 mrg-top">
            <div class="row">
              <?php 
              $i=0;
              while($row=mysqli_fetch_array($result))
              {         
          ?>
              <div class="col-lg-3 col-sm-6 col-xs-12">
                <div class="block_wallpaper">           
                  <div class="wall_image_title">
                    <h2><a href="#"><?php echo $row['album_name'];?> <span>(<?php echo get_total_songs($row['aid']);?>)</span></a></h2>
                    <ul>                
                      <li><a href="add_album.php?album_id=<?php echo $row['aid'];?>" data-toggle="tooltip" data-tooltip="Edit"><i class="fa fa-edit"></i></a></li>               
                      <li><a href="?album_id=<?php echo $row['aid'];?>" data-toggle="tooltip" data-tooltip="Delete" onclick="return confirm('Are you sure you want to delete this album?');"><i class="fa fa-trash"></i></a></li>
                      
                      <?php if($row['status']!="0"){?>
                      <li><div class="row toggle_btn"><a href="manage_album.php?status_deactive_id=<?php echo $row['aid'];?>" data-toggle="tooltip" data-tooltip="ENABLE"><img src="assets/images/btn_enabled.png" alt="wallpaper_1" /></a></div></li>

                      <?php }else{?>
                      
                      <li><div class="row toggle_btn"><a href="manage_album.php?status_active_id=<?php echo $row['aid'];?>" data-toggle="tooltip" data-tooltip="DISABLE"><img src="assets/images/btn_disabled.png" alt="wallpaper_1" /></a></div></li>
                  
                      <?php }?>


                    </ul>
                  </div>
                  <span><img src="images/<?php echo $row['album_image'];?>" /></span>
                </div>
              </div>
          <?php
            
            $i++;
              }
        ?>     
               
      </div>
          </div>
           <div class="col-md-12 col-xs-12">
            <div class="pagination_item_block">
              <nav>
                <?php if(!isset($_POST["search_data"])){ include("pagination.php");}?>            
              </nav>
            </div>
          </div>
          <div class="clearfix"></div>
        </div>
      </div>
    </div>
        
<?php include("includes/footer.php");?>       
