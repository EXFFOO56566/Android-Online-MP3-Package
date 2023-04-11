<?php include("includes/header.php");

	require("includes/function.php");
	require("language/language.php");
    
    if(isset($_POST['search_data']))
   {
     
    
    $data_qry="SELECT * FROM tbl_playlist
               WHERE tbl_playlist.playlist_name like '%".addslashes($_POST['search_value'])."%' ORDER BY tbl_playlist.pid DESC";  
               
    $result=mysqli_query($mysqli,$data_qry);
    
     
   }
   else
   {
      
      $tableName="tbl_playlist";   
      $targetpage = "manage_playlist.php"; 
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
      
     $data_qry="SELECT * FROM tbl_playlist
                   ORDER BY tbl_playlist.pid DESC LIMIT $start, $limit";
 
     $result=mysqli_query($mysqli,$data_qry); 
   }

	
	if(isset($_GET['playlist_id']))
	{

	 
		$cat_res=mysqli_query($mysqli,'SELECT * FROM tbl_playlist WHERE pid=\''.$_GET['playlist_id'].'\'');
		$cat_res_row=mysqli_fetch_assoc($cat_res);


		if($cat_res_row['playlist_image']!="")
	    {
	    	unlink('images/'.$cat_res_row['playlist_image']);
			  unlink('images/thumbs/'.$cat_res_row['playlist_image']);

		}
 
		Delete('tbl_playlist','pid='.$_GET['playlist_id'].'');

      
		$_SESSION['msg']="12";
		header( "Location:manage_playlist.php");
		exit;
		
	}	
	 
   
    //Active and Deactive status
  if(isset($_GET['status_deactive_id']))
  {
     $data = array('status'  =>  '0');
    
     $edit_status=Update('tbl_playlist', $data, "WHERE pid = '".$_GET['status_deactive_id']."'");
    
     $_SESSION['msg']="14";
     header( "Location:manage_playlist.php");
     exit;
  }
  if(isset($_GET['status_active_id']))
  {
      $data = array('status'  =>  '1');
      
      $edit_status=Update('tbl_playlist', $data, "WHERE pid = '".$_GET['status_active_id']."'");
      
      $_SESSION['msg']="13";   
      header( "Location:manage_playlist.php");
      exit;
  }  

?>
                
    <div class="row">
      <div class="col-xs-12">
        <div class="card mrg_bottom">
          <div class="page_title_block">
            <div class="col-md-5 col-xs-12">
              <div class="page_title">Manage Playlist</div>
            </div>
            <div class="col-md-7 col-xs-12">
              <div class="search_list">
                <div class="search_block">
                      <form  method="post" action="">
                        <input class="form-control input-sm" placeholder="Search..." aria-controls="DataTables_Table_0" type="search" name="search_value" required>
                        <button type="submit" name="search_data" class="btn-search"><i class="fa fa-search"></i></button>
                      </form>  
                    </div>
                <div class="add_btn_primary"> <a href="add_playlist.php?add=yes">Add Playlist</a> </div>
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
                    <h2><a href="#"><?php echo $row['playlist_name'];?></a></h2>
                    <ul>                
                      <li><a href="add_playlist.php?playlist_id=<?php echo $row['pid'];?>" data-toggle="tooltip" data-tooltip="Edit"><i class="fa fa-edit"></i></a></li>               
                      <li><a href="?playlist_id=<?php echo $row['pid'];?>" data-toggle="tooltip" data-tooltip="Delete" onclick="return confirm('Are you sure you want to delete this playlist?');"><i class="fa fa-trash"></i></a></li>
                      
                      <?php if($row['status']!="0"){?>
                      <li><div class="row toggle_btn"><a href="manage_playlist.php?status_deactive_id=<?php echo $row['pid'];?>" data-toggle="tooltip" data-tooltip="ENABLE"><img src="assets/images/btn_enabled.png" alt="wallpaper_1" /></a></div></li>

                      <?php }else{?>
                      
                      <li><div class="row toggle_btn"><a href="manage_playlist.php?status_active_id=<?php echo $row['pid'];?>" data-toggle="tooltip" data-tooltip="DISABLE"><img src="assets/images/btn_disabled.png" alt="wallpaper_1" /></a></div></li>
                  
                      <?php }?>


                    </ul>
                  </div>
                  <span><img src="images/<?php echo $row['playlist_image'];?>" /></span>
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
